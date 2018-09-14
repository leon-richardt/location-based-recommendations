package de.nuttercode.androidprojectss2018.lbrserver;

import java.util.List;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.ScoredEvent;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.csi.TagPreferenceConfiguration;
import de.nuttercode.androidprojectss2018.csi.TagUserChoice;

/**
 * uses the following metric to calculate a score. For every tag in the event a
 * {@link TagUserChoice#DENY} counts as -1, a {@link TagUserChoice#ACCEPT}
 * counts as 0, and a {@link TagUserChoice#LIKE} counts as +1. Let m be the
 * number of +1, n number of -1, and p the number of {@link Tag}s in the
 * {@link Event}. The score s will be s = (m + n + p) / 2p which satisfies
 * {@link ScoredEvent}.
 * 
 * @author Johannes B. Latzel
 *
 */
public class CountEventScoreCalculator implements EventScoreCalculator {

	@Override
	public ScoredEvent scoreEvent(Event event, TagPreferenceConfiguration tpc) {
		Assurance.assureNotNull(event);
		Assurance.assureNotNull(tpc);
		List<Tag> tagList = event.getTags();
		int countScore = 0;
		double score = 0.0;
		for (Tag tag : tagList) {
			switch (tpc.getUserChoice(tag)) {
			case ACCEPT:
				break;
			case DENY:
				countScore--;
				break;
			case LIKE:
				countScore++;
				break;
			default:
				break;
			}
		}
		if (!tagList.isEmpty()) {
			int size = tagList.size();
			// double constant to mitigate int-division
			score = countScore + size / (2.0 * size);
		}
		// mitigate rounding errors
		if (score < 0)
			score = 0;
		else if (score > 1)
			score = 1;
		return new ScoredEvent(event, score);
	}

}
