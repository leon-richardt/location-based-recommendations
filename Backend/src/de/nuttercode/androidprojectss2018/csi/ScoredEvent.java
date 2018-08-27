package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;

/**
 * an {@link Event} and its score. the score must be an element of [0, 1].
 * 
 * @author Johannes B. Latzel
 *
 */
public class ScoredEvent implements Serializable {

	private static final long serialVersionUID = -1442060615555625349L;

	private final Event event;
	private final double score;

	/**
	 * @param event
	 * @param score
	 * @throws IllegalArgumentException
	 *             if event == null or score not an element of [0, 1]
	 */
	public ScoredEvent(Event event, double score) {
		Assurance.assureNotNull(event);
		Assurance.assureBoundaries(score, 0, 1);
		this.event = event;
		this.score = score;
	}

	public Event getEvent() {
		return event;
	}

	public double getScore() {
		return score;
	}

	@Override
	public int hashCode() {
		return event.getId() ^ Double.hashCode(score);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScoredEvent other = (ScoredEvent) obj;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ScoredEvent [event=" + event + ", score=" + score + "]";
	}

}
