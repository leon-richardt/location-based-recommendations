package de.nuttercode.androidprojectss2018.lbrserver;

import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.ScoredEvent;
import de.nuttercode.androidprojectss2018.csi.TagPreferenceConfiguration;

/**
 * interface for different Event scoring mechanisms
 * 
 * @author Johannes B. Latzel
 *
 */
public interface EventScoreCalculator {

	/**
	 * processes the event and calculates its score - the score must satisfy the
	 * boundaries specified in {@link ScoredEvent}
	 * 
	 * @param event
	 * @param tpc
	 * @return new {@link ScoredEvent}
	 * @throws IllegalArgumentException
	 *             if event or tpc is null
	 */
	ScoredEvent scoreEvent(Event event, TagPreferenceConfiguration tpc);

}
