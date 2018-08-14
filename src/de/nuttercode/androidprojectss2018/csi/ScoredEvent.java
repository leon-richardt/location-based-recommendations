package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;

/**
 * {@link Event} + score
 * 
 * @author Johannes B. Latzel
 *
 */
public class ScoredEvent implements Serializable {

	private static final long serialVersionUID = -1442060615555625349L;

	private final Event event;
	private final double score;

	public ScoredEvent(Event event, double score) {
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
	public String toString() {
		return "ScoredEvent [event=" + event + ", score=" + score + "]";
	}

}
