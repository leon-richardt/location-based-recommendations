package de.nuttercode.androidprojectss2018.csi.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
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
 *            some {@link LBRPOJO}
 */
public class Store<T extends LBRPOJO, Q extends Query<T>> {

	private final Map<Integer, T> tMap;
	protected final Q query;
	private final List<StoreListener<T>> storeListenerList;

	/**
	 * @param query
	 * @throws IllegalArgumentException
	 *             if query is null
	 */
	protected Store(Q query) {
		Assurance.assureNotNull(query);
		tMap = new HashMap<>();
		this.query = query;
		storeListenerList = new ArrayList<>();
	}

	public void addStoreListener(StoreListener<T> storeNewElementListener) {
		storeListenerList.add(storeNewElementListener);
	}

	public Set<T> getAll() {
		return new HashSet<>(tMap.values());
	}

	/**
	 * @param id
	 * @return true if the element specified by the id exists in this {@link Store}
	 */
	public boolean contains(int id) {
		return tMap.containsKey(id);
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
		return tMap.get(id);
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
				if (!tMap.containsKey(newElement.getId()))
					for (StoreListener<T> listener : storeListenerList)
						try {
							listener.onElementAdded(newElement);
						} catch (RuntimeException e) {
							e.printStackTrace();
						}

			for (T element : tMap.values())
				if (!receivedElements.contains(element))
					for (StoreListener<T> listener : storeListenerList)
						try {
							listener.onElementRemoved(element);
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
			tMap.clear();
			for (T t : receivedElements)
				tMap.put(t.getId(), t);
		}
		for (StoreListener<T> listener : storeListenerList)
			listener.onRefreshFinished(resultInformation);
		return resultInformation;
	}

	/**
	 * just calls {@link Query#setClientConfiguration(ClientConfiguration)}
	 * 
	 * @param clientConfiguration
	 */
	public void setClientConfiguration(ClientConfiguration clientConfiguration) {
		query.setClientConfiguration(clientConfiguration);
	}

	@Override
	public String toString() {
		return "Store [tSet=" + Arrays.toString(tMap.values().toArray()) + "]";
	}

}
