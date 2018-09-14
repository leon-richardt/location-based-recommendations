package de.nuttercode.androidprojectss2018.csi.pojo;

import de.nuttercode.androidprojectss2018.csi.Assurance;

/**
 * an {@link Event} and its score. the score must be an element of [0, 1].
 * 
 * @author Johannes B. Latzel
 *
 */
public class ScoredEvent extends LBRPOJO {

	private static final long serialVersionUID = -1442060615555625349L;

	private final Event event;
	private final double score;

	/**
	 * @param event
	 * @param score
	 * @throws IllegalArgumentException
	 *             if score is not an element of [0, 1]
	 * @throws NullPointerException
	 *             if event is null
	 */
	public ScoredEvent(Event event, double score) {
		super(event.getId());
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
		return event.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
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
