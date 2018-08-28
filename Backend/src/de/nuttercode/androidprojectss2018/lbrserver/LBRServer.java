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
	 * 
	 * @param port
	 * @param eventScoreCalculator
	 * @param dbDNSHostname
	 * @param dbPort
	 * @param dbName
	 * @param dbUsername
	 * @param dbPassword
	 * @throws IOException
	 *             can occur in the {@link ClientListener#ClientListener(int)}
	 * @throws SQLException
	 *             if {@link DBConnection#DBConnection(String, String, String)} does
	 * @throws IllegalArgumentException
	 *             if eventScoreCalculator, dbPassword, or dbUsername is null, if
	 *             {@link DBConnection#createURL(String, String, String)} does, or
	 *             if dbUsername is empty
	 */
	public LBRServer(int port, EventScoreCalculator eventScoreCalculator, String dbDNSHostname, int dbPort,
			String dbName, String dbUsername, String dbPassword) throws IOException, SQLException {
		Assurance.assureNotNull(eventScoreCalculator);
		Assurance.assureNotNull(dbPassword);
		Assurance.assureNotEmpty(dbUsername);
		this.eventScoreCalculator = eventScoreCalculator;
		clientListener = new ClientListener(port);
		serverThread = new Thread(this::run);
		serverThread.setName("LBRServerThread");
		serverThread.start();
		isClosed = false;
		executorServer = Executors.newFixedThreadPool(4);
		dbConnection = new DBConnection(DBConnection.createURL(dbDNSHostname, dbPort, dbName), dbUsername, dbPassword);
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
		ArrayList<ScoredEvent> scoredEventList = new ArrayList<>(eventList.size());
		for (Event event : eventList)
			scoredEventList.add(eventScoreCalculator.scoreEvent(event));
		return scoredEventList;
	}

	private QueryResult<Tag> createTagResult(TagQuery tagQuery) {
		try {
			return new QueryResult<>(dbConnection.getAllTags());
		} catch (SQLException e) {
			e.printStackTrace();
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
		try {
			return new QueryResult<ScoredEvent>(
					scoreEvents(
							filterEvents(
									dbConnection.getAllEventsByRadiusAndLocation(lbrQuery.getRadius(),
											lbrQuery.getLatitude(), lbrQuery.getLongitude()),
									lbrQuery.getTagPreferenceConfiguration())));
		} catch (SQLException e) {
			e.printStackTrace();
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

		Query<?> query = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		// no try-with-resource, because ObjectInputStream/ObjectOutputStream might
		// close the socket prematurely
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			query = (Query<?>) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		if (query != null) {
			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				if (query.getClass().equals(LBRQuery.class)) {
					oos.writeObject(createLBRResult((LBRQuery) query));
				} else if (query.getClass().equals(TagQuery.class)) {
					oos.writeObject(createTagResult((TagQuery) query));
				}
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		try {
			socket.close();
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * used by the {@link #serverThread} - polls requests and processes them
	 */
	private void run() {
		while (!isClosed) {
			try {
				final Socket socket = clientListener.getRequest();
				executorServer.execute(() -> {
					answerRequest(socket);
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws IOException {
		if (isClosed)
			return;
		isClosed = true;
		serverThread.interrupt();
		clientListener.close();
	}

}
