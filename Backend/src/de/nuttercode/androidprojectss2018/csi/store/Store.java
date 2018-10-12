package de.nuttercode.androidprojectss2018.csi.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.config.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.pojo.LBRPojo;
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
 *            any {@link LBRPojo}
 */
public class Store<T extends LBRPojo, Q extends Query<T>> {

	/**
	 * default ttl is 2h
	 */
	private final static int DEFAULT_TTL_MILLISECONDS = 2 * 60 * 60 * 1000;

	/**
	 * maps T Ids to their instances
	 */
	private final Map<Integer, T> tMap;

	/**
	 * maps Ids to ttl. the Ids will be ignored in the lbrqueries until the ttl is
	 * reached.
	 */
	private final Map<Integer, Long> ignoreIdMap;

	protected final Q query;
	private final List<StoreListener<T>> storeListenerList;

	/**
	 * time to live in seconds for ignored Ids
	 */
	private int ttl;

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
		ignoreIdMap = new HashMap<>();
		setTTL(DEFAULT_TTL_MILLISECONDS);
	}

	/**
	 * removes all Ids whose ttl was reached and returns all remaining Ids.
	 * 
	 * @return all remaining Ids in {@link #ignoreIdMap}
	 */
	private Set<Integer> getIgnoreIds() {
		long now = System.currentTimeMillis();
		synchronized (ignoreIdMap) {
			for (Iterator<Integer> iterator = ignoreIdMap.keySet().iterator(); iterator.hasNext(); ) {
				Integer curId = iterator.next();
				if (now >= ignoreIdMap.get(curId))
					iterator.remove();
			}
		}
		return Collections.unmodifiableSet(ignoreIdMap.keySet());
	}

	/**
	 * sets {@link #ttl}
	 * 
	 * @param ttl
	 * @throws IllegalArgumentException
	 *             if ttl is not positive
	 */
	public void setTTL(int ttl) {
		Assurance.assurePositive(ttl);
		this.ttl = ttl;
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
		query.setIgnoreIds(getIgnoreIds());
		QueryResultSummary<T> resultSummary = query.run();
		QueryResultInformation resultInformation = resultSummary.getQueryResultInformation();
		Collection<T> receivedElements;
		int id;
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
				if (!receivedElements.contains(element) && !ignoreIdMap.containsKey(element.getId()))
					for (StoreListener<T> listener : storeListenerList)
						try {
							listener.onElementRemoved(element);
							tMap.remove(element.getId());
						} catch (RuntimeException e) {
							e.printStackTrace();
						}
			for (T t : receivedElements) {
				id = t.getId();
				tMap.put(id, t);
				ignoreIdMap.put(id, System.currentTimeMillis() + ttl);
			}
		}
		for (StoreListener<T> listener : storeListenerList)
			listener.onRefreshFinished(resultInformation);
		return resultInformation;
	}

	/**
	 * calls {@link Query#setClientConfiguration(ClientConfiguration)} and clears
	 * {@link #ignoreIdMap}
	 * 
	 * @param clientConfiguration
	 */
	public void setClientConfiguration(ClientConfiguration clientConfiguration) {
		query.setClientConfiguration(clientConfiguration);
		ignoreIdMap.clear();
	}

	@Override
	public String toString() {
		return "Store [tSet=" + Arrays.toString(tMap.values().toArray()) + "]";
	}

}
