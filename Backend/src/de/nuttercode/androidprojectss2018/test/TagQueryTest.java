package de.nuttercode.androidprojectss2018.test;

import de.nuttercode.androidprojectss2018.csi.config.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.store.TagStore;
import de.nuttercode.androidprojectss2018.example.StoreListenerExample;

/**
 * Test LBRClient connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class TagQueryTest {

	public static void main(String[] args) {

		ClientConfiguration clientConfiguration = new ClientConfiguration();
		TagStore tagStore = new TagStore(clientConfiguration);
		tagStore.addStoreListener(new StoreListenerExample<>());
		tagStore.refresh();
		tagStore.refresh();

	}

}
