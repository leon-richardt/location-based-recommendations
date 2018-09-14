package de.nuttercode.androidprojectss2018.example;

import de.nuttercode.androidprojectss2018.csi.config.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.pojo.ScoredEvent;
import de.nuttercode.androidprojectss2018.csi.store.EventStore;

/**
 * example for LBRQuery connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRQueryExample {

	public static void main(String[] args) {

		ClientConfiguration clientConfiguration = new ClientConfiguration();
		EventStore eventStore = new EventStore(clientConfiguration);

		// register StoreListener
		eventStore.addStoreListener(new StoreListenerExample<>());

		// blocking until tags received or timeout
		if (eventStore.refresh().isOK())
			for (ScoredEvent scoredEvent : eventStore.getAll())
				System.out.println(scoredEvent);

	}

}
