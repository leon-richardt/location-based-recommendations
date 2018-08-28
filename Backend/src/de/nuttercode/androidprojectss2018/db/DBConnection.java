package de.nuttercode.androidprojectss2018.db;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.Tag;

import java.sql.SQLException;
import java.util.Collection;

/**
 * provides a Connection to MySQL LBR-DB via JDBC
 * 
 * @author Johannes B. Latzel, Henrik Gerdes
 *
 */
public class DBConnection {

	/**
	 * 
	 * @param dbDNSHostname
	 * @param dbPort
	 * @param dbName
	 * @return db connection url in jdbc mysql format
	 * @throws IllegalArgumentException
	 *             if dbDNSHostname or dbName is empty or if dbPort is not in [0,
	 *             65535]
	 */
	public static String createURL(String dbDNSHostname, int dbPort, String dbName) {
		Assurance.assureNotEmpty(dbDNSHostname);
		Assurance.assureBoundaries(dbPort, 0, 65_535);
		Assurance.assureNotEmpty(dbName);
		StringBuilder stringBuilder = new StringBuilder(150);
		stringBuilder.append("jdbc:mysql://");
		stringBuilder.append(dbDNSHostname);
		stringBuilder.append(':');
		stringBuilder.append(dbPort);
		stringBuilder.append('/');
		stringBuilder.append(dbName);
		return stringBuilder.toString();
	}

	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @throws SQLException
	 *             if {@link DoQuery#GetConnection(String, String, String)} does
	 */
	public DBConnection(String url, String user, String password) throws SQLException {
		DoQuery.GetConnection(url, user, password);
	}

	/**
	 * 
	 * @param radius
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws SQLException
	 *             if {@link DoQuery#GetEventsWithTag(java.util.ArrayList)} does
	 */
	public Collection<Event> getAllEventsByRadiusAndLocation(double radius, double latitude, double longitude)
			throws SQLException {
		return DoQuery
				.GetEventsWithTag(DoQuery.GetEvents(GenerateQuery.GenerateQueryEvents(radius, latitude, longitude)));
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 *             if {@link DoQuery#GetAllTags()} does
	 */
	public Collection<Tag> getAllTags() throws SQLException {
		return DoQuery.GetAllTags();
	}

}
