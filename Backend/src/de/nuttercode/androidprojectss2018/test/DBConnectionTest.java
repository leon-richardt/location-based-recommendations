package de.nuttercode.androidprojectss2018.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.db.DBConnection;

/**
 * Test-class of {@link DBConnection}
 * 
 * @author Johannes B. Latzel
 *
 */
public class DBConnectionTest {

	public static void main(String[] args) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.print("enter password: ");
			DBConnection dbConnection = new DBConnection("jdbc:mysql://lbr.nuttercode.de:3306/lbr", "lbr",
					reader.readLine());
			for (Tag tag : dbConnection.getAllTags())
				System.out.println(tag);
			for (Event event : dbConnection.getAllEventsByRadiusAndLocation(10, 52.3, 8.05))
				System.out.println(event);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
