package de.nuttercode.androidprojectss2018.lbrserver;

import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.ScoredEvent;

/**
 * interface for different Event scoring mechanisms
 * 
 * @author Johannes B. Latzel
 *
 */
public interface EventScoreCalculator {

	/**
	 * processes the event and calculates a score
	 * 
	 * @param event
	 * @return event + score
	 */
	ScoredEvent scoreEvent(Event event);

}
