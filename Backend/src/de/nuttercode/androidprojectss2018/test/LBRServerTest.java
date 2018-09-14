package de.nuttercode.androidprojectss2018.test;

import java.io.IOException;
import java.lang.Thread;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.nuttercode.androidprojectss2018.csi.config.ClientConfiguration;
import de.nuttercode.androidprojectss2018.lbrserver.CountEventScoreCalculator;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * test {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRServerTest {

	public static void main(String[] args) {
		final FileHandler fileHandler;
		final Logger logger = Logger.getLogger(LBRServerTest.class.getCanonicalName() + "::Test");
		logger.info("starting server");
		logger.setLevel(Level.ALL);
		LBRServer lbrServer = new LBRServer(ClientConfiguration.DEFAULT_LBR_PORT, new CountEventScoreCalculator(),
				"localhost", 3306, "lbr", "lbr", args[0], logger);
		try {
			fileHandler = new FileHandler(
					Paths.get(System.getProperty("user.dir"), "LBRServer.xml").toFile().getAbsolutePath(), false);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				logger.info("shutting down");
				try {
					lbrServer.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "LBRServer not closeable", e);
				}
				fileHandler.flush();
				fileHandler.close();
			}));
			logger.addHandler(fileHandler);
		} catch (IOException | IllegalArgumentException | IllegalStateException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}

	}

}
