package de.nuttercode.androidprojectss2018.example;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.EventStore;
import de.nuttercode.androidprojectss2018.csi.ScoredEvent;
import de.nuttercode.androidprojectss2018.csi.query.QueryResultState;

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
		if (eventStore.refresh() == QueryResultState.OK)
			for (ScoredEvent scoredEvent : eventStore.getAll())
				System.out.println(scoredEvent);

	}

}
