package de.nuttercode.androidprojectss2018.example;

import de.nuttercode.androidprojectss2018.csi.StoreListener;

/**
 * example for {@link StoreListener} implementation
 * 
 * @author Johannes B. Latzel
 *
 */
public class StoreListenerExample<T> implements StoreListener<T> {

	@Override
	public void onElementAdded(T newElement) {
		System.out.println("new element: " + newElement);
	}

	@Override
	public void onElementRemoved(T removedElement) {
		System.out.println("remove element: " + removedElement);
	}

}
