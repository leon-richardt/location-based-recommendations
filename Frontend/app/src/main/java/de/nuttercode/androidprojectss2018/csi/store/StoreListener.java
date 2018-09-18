package de.nuttercode.androidprojectss2018.csi.store;

import de.nuttercode.androidprojectss2018.csi.query.QueryResultInformation;

/**
 * Use {@link Store#addStoreListener(StoreListener)} to add this listener to a
 * specific {@link Store}.
 *
 * @param <T> some type
 * @author Johannes B. Latzel
 */
public interface StoreListener<T> {

    /**
     * will be called every time a new element is added to a {@link Store}.
     *
     * @param newElement the new element added to the {@link Store}
     */
    void onElementAdded(T newElement);

    /**
     * will be called every time an element is removed from a {@link Store}.
     *
     * @param newElement the element that is removed from the {@link Store}
     */
    void onElementRemoved(T newElement);

    /**
     * will be called every time {@link Store#refresh()} is finished
     *
     * @param queryResultInformation
     */
    void onRefreshFinished(QueryResultInformation queryResultInformation);
}
