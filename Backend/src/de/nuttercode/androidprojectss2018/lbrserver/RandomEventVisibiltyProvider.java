package de.nuttercode.androidprojectss2018.lbrserver;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.Event;

/**
 * randomly assigns visibility states to known event ids
 * 
 * @author Johannes B. Latzel
 *
 */
public class RandomEventVisibiltyProvider implements Closeable {

	private final static int MAX_SLEEP_TIME = 30 * 60 * 1000;

	private final Thread eventProviderThread;

	/**
	 * contains the visibility-state of known event ids
	 */
	private final Map<Integer, Boolean> visibilityMap;

	private boolean isRunning;
	private final Random random;

	public RandomEventVisibiltyProvider() {
		isRunning = true;
		visibilityMap = new HashMap<>();
		eventProviderThread = new Thread(this::run);
		eventProviderThread.start();
		random = new Random(System.currentTimeMillis());
	}

	/**
	 * sets visibility of known ids periodically
	 */
	private void run() {
		while (isRunning) {
			synchronized (visibilityMap) {
				for (int key : visibilityMap.keySet())
					visibilityMap.put(key, random.nextBoolean());
			}
			try {
				Thread.sleep(MAX_SLEEP_TIME);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	/**
	 * @param event
	 * @return true if the event is visible
	 * @throws IllegalArgumentException
	 *             if event is null
	 */
	public boolean isVisible(Event event) {
		Assurance.assureNotNull(event);
		int id = event.getId();
		synchronized (visibilityMap) {
			if (!visibilityMap.containsKey(id))
				visibilityMap.put(id, random.nextBoolean());
			return visibilityMap.get(event.getId());
		}
	}

	@Override
	public void close() throws IOException {
		isRunning = false;
		eventProviderThread.interrupt();
	}

}
