package de.nuttercode.androidprojectss2018.test;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.csi.query.LBRQuery;

/**
 * Test LBRClient connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRQueryTest {

	public static void main(String[] args) {

		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setServerDNSName("localhost");
		LBRQuery lbrQuery = new LBRQuery(clientConfiguration);
		System.out.println(lbrQuery.run());
		clientConfiguration.getTagPreferenceConfiguration().addTag(new Tag(3, "testGenre3", "testGenre3"));
		System.out.println(lbrQuery.run());
		clientConfiguration.getTagPreferenceConfiguration().addTag(new Tag(1, "testGenre1", "testGenre1"));
		System.out.println(lbrQuery.run());

	}

}
