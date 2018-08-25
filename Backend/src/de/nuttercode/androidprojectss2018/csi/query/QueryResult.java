package de.nuttercode.androidprojectss2018.csi.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * the answer of the {@link LBRServer} to a {@link Query}. can be iterated over.
 * 
 * @author Johannes B. Latzel
 *
 * @param <T>
 *            any Serializable
 */
public class QueryResult<T extends Serializable> implements Serializable, Iterable<T> {

	private static final long serialVersionUID = -5520897909649177838L;

	private final ArrayList<T> tList;

	public QueryResult(Collection<T> tCollection) {
		tList = new ArrayList<>(tCollection);
	}

	@Override
	public Iterator<T> iterator() {
		return tList.iterator();
	}

	@Override
	public String toString() {
		return "QueryResult [tList=" + Arrays.toString(tList.toArray()) + "]";
	}

}
