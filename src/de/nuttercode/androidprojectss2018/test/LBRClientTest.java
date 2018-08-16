package de.nuttercode.androidprojectss2018.test;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.LBRQuery;

/**
 * Test LBRClient connection
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRClientTest {

	public static void main(String[] args) {
		System.out.println(new LBRQuery(new ClientConfiguration()).run());
	}

}
