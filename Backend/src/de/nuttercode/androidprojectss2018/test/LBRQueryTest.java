package de.nuttercode.androidprojectss2018.test;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.EventStore;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.example.StoreListenerExample;

/**
 * Test LBRClient connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRQueryTest {

	public static void main(String[] args) {

		ClientConfiguration clientConfiguration = new ClientConfiguration();
		EventStore eventStore = new EventStore(clientConfiguration);
		eventStore.addStoreListener(new StoreListenerExample<>());
		System.out.println(eventStore.refresh());
		clientConfiguration.getTagPreferenceConfiguration().addTag(new Tag(3, "testGenre3", "testGenre3"));
		System.out.println(eventStore.refresh());
		clientConfiguration.getTagPreferenceConfiguration().addTag(new Tag(1, "testGenre1", "testGenre1"));
		System.out.println(eventStore.refresh());
		System.out.println(eventStore.refresh());

	}

}
