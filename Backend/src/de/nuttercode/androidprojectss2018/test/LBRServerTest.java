package de.nuttercode.androidprojectss2018.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread;
import java.sql.SQLException;

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
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.print("enter password: ");
			try (LBRServer lbrServer = new LBRServer(ClientConfiguration.DEFAULT_LBR_PORT,
					new RandomEventScoreCalculator(42), "lbr.nuttercode.de", 3306, "lbr", "lbr", reader.readLine())) {
				while (true) {
					// nothing to do
					Thread.sleep(10_000);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
