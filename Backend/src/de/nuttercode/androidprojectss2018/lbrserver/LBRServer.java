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

import de.nuttercode.androidprojectss2018.csi.*;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.csi.query.LBRQuery;
import de.nuttercode.androidprojectss2018.csi.query.Query;
import de.nuttercode.androidprojectss2018.csi.query.QueryResult;
import de.nuttercode.androidprojectss2018.csi.query.TagQuery;

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
		ArrayList<Tag> dummyTags = new ArrayList<>(getDummyTags());
		// eventList.add(new Event(new Venue(1, "testVenue1", "testVenue1", 100, 100,
		// 1), "testEvent1", "testEvent1", 1));
		// eventList.add(new Event(new Venue(2, "testVenue2", "testVenue2", 101, 99, 1),
		// "testEvent2", "testEvent2", 1));
		// eventList.add(new Event(new Venue("testVenue3", 3, "testVenue3", 100, 99, 1),
		// "testEvent3", "testEvent3", 1));
		// eventList.add(new Event(new Venue("testVenue1", 1, "testVenue1", 101, 100,
		// 1), "testEvent4", "testEvent4", 1));
		// eventList.get(0).addAll(dummyTags);
		// eventList.get(1).addAll(dummyTags);
		// eventList.get(2).addAll(dummyTags);
		// eventList.get(3).addAll(dummyTags);
		return eventList;
	}

	private Collection<Tag> getDummyTags() {
		ArrayList<Tag> tagList = new ArrayList<>();
		tagList.add(new Tag(1, "testGenre1", "testGenre1"));
		tagList.add(new Tag(2, "testGenre2", "testGenre2"));
		return tagList;
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
		return new QueryResult<>(getDummyTags());
	}

	/**
	 * analyzes the LBRQuery and creates an appropriate response
	 * 
	 * @param lbrQuery
	 * @return appropriate response as {@link LBRResult}
	 */
	private QueryResult<ScoredEvent> createLBRResult(LBRQuery lbrQuery) {
		return new QueryResult<ScoredEvent>(
				scoreEvents(filterEvents(getDummyEvents(), lbrQuery.getTagPreferenceConfiguration())));
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
