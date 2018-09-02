package de.nuttercode.androidprojectss2018.lbrserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.Thread;

import de.nuttercode.androidprojectss2018.csi.*;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.csi.query.LBRQuery;
import de.nuttercode.androidprojectss2018.csi.query.Query;
import de.nuttercode.androidprojectss2018.csi.query.QueryResult;
import de.nuttercode.androidprojectss2018.csi.query.TagQuery;
import de.nuttercode.androidprojectss2018.db.DBConnection;

/**
 * LBRServer accepts incoming LBRClients and answers their requests
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRServer implements Closeable {

	/**
	 * listens for LBRClients
	 */
	private final ClientListener clientListener;

	/**
	 * polls LBRClients and initiates the answering-process
	 */
	private final Thread serverThread;

	/**
	 * true, if the server has been closed
	 */
	private boolean isClosed;

	/**
	 * executes answering-process-instances
	 */
	private final ExecutorService executorServer;

	/**
	 * calculates the event scores in the answering process
	 */
	private final EventScoreCalculator eventScoreCalculator;

	/**
	 * connection to the LBR-DB
	 */
	private final DBConnection dbConnection;

	/**
	 * used to log messages
	 */
	private final Logger logger;

	/**
	 * 
	 * @param port
	 * @param eventScoreCalculator
	 * @param dbDNSHostname
	 * @param dbPort
	 * @param dbName
	 * @param dbUsername
	 * @param dbPassword
	 * @throws IllegalStateException
	 *             if the server can not be initialized
	 * @throws IllegalArgumentException
	 *             if eventScoreCalculator, dbPassword, logger, or dbUsername is
	 *             null, if {@link DBConnection#createURL(String, String, String)}
	 *             does, or if dbUsername is empty
	 */
	public LBRServer(int port, EventScoreCalculator eventScoreCalculator, String dbDNSHostname, int dbPort,
			String dbName, String dbUsername, String dbPassword, Logger logger) {
		Assurance.assureNotNull(eventScoreCalculator);
		Assurance.assureNotNull(dbPassword);
		Assurance.assureNotEmpty(dbUsername);
		Assurance.assureNotNull(logger);
		this.logger = logger;
		logger.log(Level.INFO, "starting LBRServer: " + toString());
		this.eventScoreCalculator = eventScoreCalculator;
		try {
			clientListener = new ClientListener(port);
		} catch (IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
			throw new IllegalStateException("clientListener unintializable", e);
		}
		serverThread = new Thread(this::run);
		serverThread.setName("LBRServerThread");
		serverThread.start();
		isClosed = false;
		executorServer = Executors.newFixedThreadPool(4);
		try {
			dbConnection = new DBConnection(DBConnection.createURL(dbDNSHostname, dbPort, dbName), dbUsername,
					dbPassword);
		} catch (SQLException e) {
			logger.log(Level.WARNING, e.toString(), e);
			throw new IllegalStateException("dbConnection unintializable", e);
		}
	}

	/**
	 * removes all events from eventList which do not contain a {@link Tag} in the
	 * {@link TagPreferenceConfiguration}
	 * 
	 * @param eventList
	 * @param genrePreferenceConfiguration
	 * @return eventList
	 */
	private Collection<Event> filterEvents(Collection<Event> eventList,
			TagPreferenceConfiguration tagPreferenceConfiguration) {
		logger.log(Level.FINER, "filtering events");
		eventList.removeIf((Event event) -> {
			return !tagPreferenceConfiguration.containsAny(event.getTags());
		});
		return eventList;
	}

	/**
	 * scores all events in eventList
	 * 
	 * @param eventList
	 * @return scored events
	 */
	private Collection<ScoredEvent> scoreEvents(Collection<Event> eventList) {
		logger.log(Level.FINER, "scoring events");
		ArrayList<ScoredEvent> scoredEventList = new ArrayList<>(eventList.size());
		for (Event event : eventList)
			scoredEventList.add(eventScoreCalculator.scoreEvent(event));
		return scoredEventList;
	}

	private QueryResult<Tag> createTagResult(TagQuery tagQuery) {
		logger.log(Level.FINER, "creating tag result");
		try {
			return new QueryResult<>(dbConnection.getAllTags());
		} catch (SQLException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		return new QueryResult<>(new ArrayList<>());
	}

	/**
	 * analyzes the LBRQuery and creates an appropriate response
	 * 
	 * @param lbrQuery
	 * @return appropriate response as {@link LBRResult}
	 */
	private QueryResult<ScoredEvent> createLBRResult(LBRQuery lbrQuery) {
		logger.log(Level.FINER, "creating LBR result");
		try {
			return new QueryResult<ScoredEvent>(
					scoreEvents(
							filterEvents(
									dbConnection.getAllEventsByRadiusAndLocation(lbrQuery.getRadius(),
											lbrQuery.getLatitude(), lbrQuery.getLongitude()),
									lbrQuery.getTagPreferenceConfiguration())));
		} catch (SQLException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		return new QueryResult<>(new ArrayList<>());
	}

	/**
	 * reads a LBRClient request and answers appropriately
	 * 
	 * @param socket
	 *            socket of the LBRClient connection
	 */
	private void answerRequest(Socket socket) {

		logger.log(Level.INFO, "answering request: " + socket.toString());

		Query<?> query = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		// no try-with-resource, because ObjectInputStream/ObjectOutputStream might
		// close the socket prematurely
		try {
			logger.log(Level.FINE, "reading query: " + socket.toString());
			ois = new ObjectInputStream(socket.getInputStream());
			query = (Query<?>) ois.readObject();
		} catch (ClassNotFoundException | IOException | RuntimeException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		if (query != null) {
			try {
				logger.log(Level.FINE, "creating answer: " + socket.toString());
				oos = new ObjectOutputStream(socket.getOutputStream());
				if (query.getClass().equals(LBRQuery.class)) {
					logger.log(Level.FINER, "request is LBRQuery: " + socket.toString());
					oos.writeObject(createLBRResult((LBRQuery) query));
				} else if (query.getClass().equals(TagQuery.class)) {
					logger.log(Level.FINER, "request is TagQuery: " + socket.toString());
					oos.writeObject(createTagResult((TagQuery) query));
				}
				oos.flush();
			} catch (IOException | RuntimeException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
		try {
			logger.log(Level.FINE, "closing request: " + socket.toString());
			socket.close();
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
	}

	/**
	 * used by the {@link #serverThread} - polls requests and processes them
	 */
	private void run() {
		while (!isClosed) {
			try {
				final Socket socket = clientListener.getRequest();
				logger.log(Level.INFO, "new request: " + socket.toString());
				executorServer.execute(() -> {
					answerRequest(socket);
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param level
	 *            new logging {@link Level} of the logger
	 */
	public void setLogLevel(Level level) {
		logger.log(Level.INFO, "setting log level to " + level);
		logger.setLevel(level);
	}

	@Override
	public void close() throws IOException {
		logger.log(Level.INFO, "closing LBRServer: " + toString());
		if (isClosed)
			return;
		isClosed = true;
		clientListener.close();
		serverThread.interrupt();
	}

}
