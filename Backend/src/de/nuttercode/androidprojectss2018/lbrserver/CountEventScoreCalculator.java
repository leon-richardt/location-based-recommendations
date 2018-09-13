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
 * {@link TagUserChoice#Deny} counts as -1, a {@link TagUserChoice#Accept}
 * counts as 0, and a {@link TagUserChoice#Like} counts as +1. All counts are
 * summed up and divided by the total number of {@link Tag}s in the
 * {@link Event}.
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
			case Accept:
				break;
			case Deny:
				countScore--;
				break;
			case Like:
				countScore++;
				break;
			default:
				break;
			}
		}
		if (!tagList.isEmpty())
			score = countScore / tagList.size();
		return new ScoredEvent(event, score);
	}

}
