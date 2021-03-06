package de.nuttercode.androidprojectss2018.example;

import de.nuttercode.androidprojectss2018.csi.config.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.pojo.Tag;
import de.nuttercode.androidprojectss2018.csi.store.TagStore;

/**
 * Test LBRClient connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class TagQueryExample {

	public static void main(String[] args) {

		ClientConfiguration clientConfiguration = new ClientConfiguration();
		TagStore tagStore = new TagStore(clientConfiguration);

		// register StoreListener
		tagStore.addStoreListener(new StoreListenerExample<>());

		// blocking until tags received or timeout
		if (tagStore.refresh().isOK())
			for (Tag tag : tagStore.getAll())
				System.out.println(tag);

	}

}
