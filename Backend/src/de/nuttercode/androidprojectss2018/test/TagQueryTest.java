package de.nuttercode.androidprojectss2018.test;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.TagStore;

/**
 * Test LBRClient connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class TagQueryTest {

	public static void main(String[] args) {

		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setServerDNSName("localhost");
		TagStore tagStore = new TagStore(clientConfiguration);
		System.out.println(tagStore);
		System.out.println(tagStore.refreshTags());
		System.out.println(tagStore);

	}

}
