package de.nuttercode.androidprojectss2018.test;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.csi.LBRQuery;

/**
 * Test LBRClient connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRClientTest {

	public static void main(String[] args) {
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		System.out.println(new LBRQuery(clientConfiguration).run());
		clientConfiguration.getGenrePreferenceConfiguration().addGenre(new Tag(3, "testGenre3", "testGenre3"));
		System.out.println(new LBRQuery(clientConfiguration).run());
		clientConfiguration.getGenrePreferenceConfiguration().addGenre(new Tag(1, "testGenre1", "testGenre1"));
		System.out.println(new LBRQuery(clientConfiguration).run());
	}

}
