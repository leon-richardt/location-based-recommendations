package de.nuttercode.androidprojectss2018.lbrserver;

import java.io.Closeable;
import java.io.IOException;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.pojo.Event;

/**
 * assigns visibility states to events with id 1 and 2. event with id 1 will be
 * visible if and only if event with id 2 is not visible.
 * 
 * @author Johannes B. Latzel
 *
 */
public class EventVisibiltyProvider implements Closeable {

	/**
	 * time in milliseconds until {@link #idVisibility} will be switched
	 */
	private final static int SLEEP_TIME = 30 * 60 * 1000;

	private final Thread eventProviderThread;
	private boolean isRunning;

	/**
	 * true if and only if event with id 1 is visible
	 */
	private boolean idVisibility;

	public EventVisibiltyProvider() {
		isRunning = true;
		idVisibility = false;
		eventProviderThread = new Thread(this::run);
		eventProviderThread.start();
	}

	/**
	 * sets visibility of known ids periodically
	 */
	private void run() {
		while (isRunning) {
			idVisibility = !idVisibility;
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	/**
	 * all events with id != 1 or 2 will be visible. events with 1 or 2 will be
	 * visible as described in the class description {@link EventVisibiltyProvider}
	 * 
	 * @param event
	 * @return true if the event is visible
	 * @throws IllegalArgumentException
	 *             if event is null
	 */
	public boolean isVisible(Event event) {
		Assurance.assureNotNull(event);
		int id = event.getId();
		if (id == 1) {
			return idVisibility;
		} else if (id == 2) {
			return !idVisibility;
		}
		return true;
	}

	@Override
	public void close() throws IOException {
		isRunning = false;
		eventProviderThread.interrupt();
	}

}
