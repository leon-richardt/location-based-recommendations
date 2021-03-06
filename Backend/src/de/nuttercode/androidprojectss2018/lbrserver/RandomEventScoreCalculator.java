package de.nuttercode.androidprojectss2018.lbrserver;

import java.util.Random;

import de.nuttercode.androidprojectss2018.csi.config.TagPreferenceConfiguration;
import de.nuttercode.androidprojectss2018.csi.pojo.Event;
import de.nuttercode.androidprojectss2018.csi.pojo.ScoredEvent;

/**
 * random implementation of {@link EventScoreCalculator}
 * 
 * @author Johannes B. Latzel
 *
 */
public class RandomEventScoreCalculator implements EventScoreCalculator {

	private final Random random;

	public RandomEventScoreCalculator(long seed) {
		random = new Random(seed);
	}

	@Override
	public ScoredEvent scoreEvent(Event event, TagPreferenceConfiguration tpc) {
		return new ScoredEvent(event, random.nextDouble());
	}

}