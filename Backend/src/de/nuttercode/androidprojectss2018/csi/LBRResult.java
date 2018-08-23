package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * the result as created by a {@link LBRServer} and send to a LBRClient as an
 * answer to a {@link LBRQuery}. iterate through this result to yield all
 * contained events.
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRResult implements Serializable, Iterable<ScoredEvent> {

	private static final long serialVersionUID = 3054248771914189534L;

	private final ArrayList<ScoredEvent> scoredEventList;

	public LBRResult(Collection<ScoredEvent> scoredEventCollection) {
		scoredEventList = new ArrayList<>(scoredEventCollection);
	}

	@Override
	public Iterator<ScoredEvent> iterator() {
		return scoredEventList.iterator();
	}

	@Override
	public String toString() {
		return "LBRResult [scoredEventList=" + Arrays.toString(scoredEventList.toArray()) + "]";
	}

}
