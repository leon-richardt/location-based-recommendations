package de.nuttercode.androidprojectss2018.test;

import java.io.IOException;
import java.lang.Thread;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;
import de.nuttercode.androidprojectss2018.lbrserver.RandomEventScoreCalculator;

/**
 * test {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRServerTest {

	public static void main(String[] args) {
		try (LBRServer lbrServer = new LBRServer(ClientConfiguration.DEFAULT_LBR_PORT,
				new RandomEventScoreCalculator(42))) {
			while (true) {
				// nothing to do
				Thread.sleep(10_000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
