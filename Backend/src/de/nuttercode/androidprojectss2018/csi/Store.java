package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import de.nuttercode.androidprojectss2018.csi.pojo.LBRPOJO;
import de.nuttercode.androidprojectss2018.csi.query.Query;
import de.nuttercode.androidprojectss2018.csi.query.QueryResultInformation;
import de.nuttercode.androidprojectss2018.csi.query.QueryResultState;
import de.nuttercode.androidprojectss2018.csi.query.QueryResultSummary;

/**
 * stores elements locally. use {@link #addStoreListener(StoreListener)} to
 * listener to new or removed elements.
 * 
 * @author Johannes B. Latzel
 *
 * @param <T>
 *            some {@link Serializable}
 */
public class Store<T extends LBRPOJO, Q extends Query<T>> {

	private final Map<Integer, T> tSet;
	protected final Q query;
	private final List<StoreListener<T>> storeListenerList;

	/**
	 * @param query
	 * @throws IllegalArgumentException
	 *             if query is null
	 */
	protected Store(Q query) {
		Assurance.assureNotNull(query);
		tSet = new HashMap<>();
		this.query = query;
		storeListenerList = new ArrayList<>();
	}

	public void addStoreListener(StoreListener<T> storeNewElementListener) {
		storeListenerList.add(storeNewElementListener);
	}

	public Set<T> getAll() {
		return new HashSet<>(tSet.values());
	}

	/**
	 * @param id
	 * @return true if the element specified by the id exists in this {@link Store}
	 */
	public boolean contains(int id) {
		return tSet.containsKey(id);
	}

	/**
	 * @param id
	 *            id of the element
	 * @return the element specified by the id
	 * @throws NoSuchElementException
	 *             if the element does not exist
	 */
	public T getById(int id) {
		if (!contains(id))
			throw new NoSuchElementException();
		return tSet.get(id);
	}

	/**
	 * retrieves all applicable elements from the {@link LBRServer}. if the
	 * underlying queries does not have an {@link QueryResultState#OK} then no data
	 * will be saved in this {@link Store}. calls all added {@link StoreListener}s
	 * as added by {@link #addStoreListener(StoreListener)}.
	 * 
	 * @return informations about the state of the underlying query
	 */
	public QueryResultInformation refresh() {
		QueryResultSummary<T> resultSummary = query.run();
		QueryResultInformation resultInformation = resultSummary.getQueryResultInformation();
		Collection<T> receivedElements;
		if (resultInformation.isOK()) {
			receivedElements = resultSummary.getQueryResult().getAll();
			for (T newElement : receivedElements)
				if (!tSet.containsKey(newElement.getId()))
					for (StoreListener<T> listener : storeListenerList)
						try {
							listener.onElementAdded(newElement);
						} catch (RuntimeException e) {
							e.printStackTrace();
						}

			for (T element : tSet.values())
				if (!receivedElements.contains(element))
					for (StoreListener<T> listener : storeListenerList)
						try {
							listener.onElementRemoved(element);
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
			tSet.clear();
			for (T t : receivedElements)
				tSet.put(t.getId(), t);
		}
		return resultInformation;
	}

	@Override
	public String toString() {
		return "Store [tSet=" + Arrays.toString(tSet.values().toArray()) + "]";
	}

}
