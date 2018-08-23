package de.nuttercode.androidprojectss2018.lbrserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.lang.Thread;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.Genre;
import de.nuttercode.androidprojectss2018.csi.GenrePreferenceConfiguration;
import de.nuttercode.androidprojectss2018.csi.LBRQuery;
import de.nuttercode.androidprojectss2018.csi.LBRResult;
import de.nuttercode.androidprojectss2018.csi.ScoredEvent;
import de.nuttercode.androidprojectss2018.csi.Venue;

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
	 * 
	 * @param port
	 * @param eventScoreCalculator
	 * @throws IOException
	 *             can occur in the {@link ClientListener#ClientListener(int)}
	 * @throws IllegalArgumentException
	 *             when eventScoreCalculator is null
	 */
	public LBRServer(int port, EventScoreCalculator eventScoreCalculator) throws IOException {
		Assurance.assureNotNull(eventScoreCalculator);
		this.eventScoreCalculator = eventScoreCalculator;
		clientListener = new ClientListener(port);
		serverThread = new Thread(this::run);
		serverThread.setName("LBRServerThread");
		serverThread.start();
		isClosed = false;
		executorServer = Executors.newFixedThreadPool(4);
	}

	/**
	 * creates some dummy events for test purposes
	 * 
	 * @return Collection<Event> dummy events
	 */
	private Collection<Event> getDummyEvents() {
		ArrayList<Event> eventList = new ArrayList<>();
		ArrayList<Genre> genreList = new ArrayList<>();
		genreList.add(new Genre(1, "testGenre1", "testGenre1"));
		genreList.add(new Genre(2, "testGenre2", "testGenre2"));
		eventList.add(new Event(new Venue("testVenue1", 1, "testVenue1", 100, 100, 1), genreList, "testEvent1",
				"testEvent1", 1));
		return eventList;
	}

	/**
	 * removes all events from eventList which do not contain a {@link Genre} in the
	 * {@link GenrePreferenceConfiguration}
	 * 
	 * @param eventList
	 * @param genrePreferenceConfiguration
	 * @return eventList
	 */
	private Collection<Event> filterEvents(Collection<Event> eventList,
			GenrePreferenceConfiguration genrePreferenceConfiguration) {
		eventList.removeIf((Event event) -> {
			return !genrePreferenceConfiguration.containsAny(event.getGenres());
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

	/**
	 * analyzes the LBRQuery and creates an appropriate response
	 * 
	 * @param lbrQuery
	 * @return appropriate response as {@link LBRResult}
	 */
	private LBRResult createLBRResult(LBRQuery lbrQuery) {
		return new LBRResult(scoreEvents(
				filterEvents(getDummyEvents(), lbrQuery.getClientConfiguration().getGenrePreferenceConfiguration())));
	}

	/**
	 * reads a LBRClient request and answers appropriately
	 * 
	 * @param socket
	 *            socket of the LBRClient connection
	 */
	private void answerRequest(Socket socket) {

		LBRQuery lbrQuery = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		// no try-with-resource, because ObjectInputStream/ObjectOutputStream might
		// close the socket prematurely
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			lbrQuery = (LBRQuery) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		if (lbrQuery != null) {
			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(createLBRResult(lbrQuery));
				oos.flush();
			} catch (IOException e) {
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
