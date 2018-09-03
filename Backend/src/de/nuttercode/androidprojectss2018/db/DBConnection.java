package de.nuttercode.androidprojectss2018.db;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.Tag;
import java.util.Properties;
import java.sql.SQLException;
import java.util.Collection;

/**
 * provides a Connection to MySQL LBR-DB via JDBC
 * 
 * @author Johannes B. Latzel, Henrik Gerdes
 *
 */
public class DBConnection {

	private Properties info;

	/**
	 *  Creates a String containing a url matching the JDBC URL schema
	 * @param dbDNSHostname Host or IP the the DB
	 * @param dbPort The Port where the DB is listening
	 * @param dbName Name of the DB witch contains the data
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
	 * Initializes the DB Connection
	 *
	 * @param url
	 *            The DB URL, Port and DB-Name
	 * @param user
	 *            Username
	 * @param password
	 *            Password for Username
	 * @throws SQLException
	 *             if {@link DoQuery#getConnection(String, String, String)} does
	 */
	public DBConnection(String url, String user, String password) throws SQLException {
		this.info = new Properties();
		info.put("url", url);
		info.put("user",user);
		info.put("password",password);
		DoQuery.getConnection(url, user, password);
	}

	/**
	 *  Gets all Events (with Tags) within the radius
	 * @param radius The radius to look for the events
	 * @param latitude current position
	 * @param longitude current position
	 * @return Collection of Events with Tags
	 * @throws SQLException
	 *             if {@link DoQuery#getEventsWithTag(java.util.ArrayList)} does
	 */
	public Collection<Event> getAllEventsByRadiusAndLocation(double radius, double latitude, double longitude)
			throws SQLException {
		return DoQuery
				.getEventsWithTag(DoQuery.getEvents(GenerateQuery.generateQueryEvents(radius, latitude, longitude)));
	}

	/**
	 * Gets all Tags on the DB
	 * @return Collection of tags
	 * @throws SQLException
	 *             if {@link DoQuery#getAllTags()} does
	 */
	public Collection<Tag> getAllTags() throws SQLException {
		return DoQuery.getAllTags();
	}

	/**
	 * Reinitialize the DB Connection
	 * @return true if successfully else false
	 */
	public boolean reconnect(){
		try {
			DoQuery.getConnection(info.getProperty("url"), info.getProperty("user"),info.getProperty("password"));
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

}
