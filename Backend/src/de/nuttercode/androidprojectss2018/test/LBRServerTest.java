package de.nuttercode.androidprojectss2018.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		FileHandler fileHandler = null;
		Logger logger = Logger.getLogger(LBRServerTest.class.getCanonicalName() + "::Test");
		logger.setLevel(Level.ALL);
		try (LBRServer lbrServer = new LBRServer(ClientConfiguration.DEFAULT_LBR_PORT,
				new RandomEventScoreCalculator(42), "localhost", 3306, "lbr", "lbr", args[0], logger)) {
			fileHandler = new FileHandler(
					Paths.get(System.getProperty("user.dir"), "LBRServer.xml").toFile().getAbsolutePath(), false);
			logger.addHandler(fileHandler);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
				while (!reader.readLine().equals("exit")) {
					Thread.sleep(500);
				}
			}
		} catch (InterruptedException | IOException | IllegalArgumentException | IllegalStateException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		if (fileHandler != null) {
			fileHandler.flush();
			fileHandler.close();
		}
	}

}
