package de.nuttercode.androidprojectss2018.db;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.csi.Venue;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Collection;

/**
 * provides a Connection to MySQL LBR-DB via JDBC
 * 
 * @author Johannes B. Latzel, Henrik Gerdes
 *
 */
public class DBConnection implements Closeable {

	private Properties info;
	private Connection conn;
	private Statement stm;

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
	 *             If login is not successful
	 */
	public DBConnection(String url, String user, String password) throws SQLException {
		this.info = new Properties();
		info.put("url", url);
		info.put("user", user);
		info.put("password", password);
		info.put("autoReconnect", "true");
		info.put("useUnicode", "true");
		info.put("characterEncoding", "utf8");

		conn = DriverManager.getConnection(url, info);
		stm = conn.createStatement();
	}

	/**
	 * Gets all Events (with Tags) within the radius
	 * 
	 * @param radius
	 *            The radius to look for the events
	 * @param latitude
	 *            current position
	 * @param longitude
	 *            current position
	 * @return Collection of Events with Tags
	 * @throws SQLException
	 *             if SQL Error
	 */
	public Collection<Event> getAllEventsByRadiusAndLocation(double radius, double latitude, double longitude)
			throws SQLException {
		return getEventsWithTag(getEvents(GenerateQuery.generateQueryEvents(radius, latitude, longitude)));
	}

	/**
	 * Gets all Tags on the DB
	 * 
	 * @return Collection of tags
	 * @throws SQLException
	 *             if SQL Error
	 */
	public Collection<Tag> getAllTags() throws SQLException {
		checkConnection();
		ResultSet dbResult = stm.executeQuery(GenerateQuery.generateQueryForAllTags());
		ArrayList<Tag> tags = new ArrayList<>();
		while (dbResult.next())
			tags.add(new Tag(dbResult.getInt("tag_id"), dbResult.getString("tag_name"),
					dbResult.getString("tag_description")));
		return tags;
	}

	/**
	 * Gets all Events for the given query
	 *
	 * @param query
	 *            The query that specifics the Events
	 * @return ArrayList of Events
	 * @throws SQLException
	 *             SQL Error
	 * @throws IllegalStateException
	 *             if {@link #checkConnection()} does
	 */
	private ArrayList<Event> getEvents(String query) throws SQLException {
		checkConnection();
		ArrayList<Event> events = new ArrayList<>();
		ResultSet dbResult = stm.executeQuery(query);
		while (dbResult.next())
			events.add(new Event(new Venue(dbResult.getInt("venue_id"), dbResult.getString("venue_name"),
					dbResult.getString("venue_description"), dbResult.getBigDecimal("longitude").doubleValue(),
					dbResult.getBigDecimal("latitude").doubleValue(), dbResult.getBigDecimal("distance").doubleValue()),
					dbResult.getString("event_name"), dbResult.getString("event_description"),
					dbResult.getInt("event_id"), getLocalDateTime(dbResult.getDate("date"), dbResult.getTime("time"))));
		return events;
	}

	/**
	 * Gets the Tags for every Event
	 *
	 * @param events
	 *            List of Events
	 * @return ArrayList of Events with Tags
	 * @throws SQLException
	 *             SQL error
	 * @throws IllegalArgumentException
	 *             if events is null or it contains null
	 * @throws IllegalStateException
	 *             if {@link #checkConnection()} does
	 */
	private ArrayList<Event> getEventsWithTag(ArrayList<Event> events) throws SQLException {
		checkConnection();
		Assurance.assureNotNull(events);
		for (Event e : events) {
			Assurance.assureNotNull(e);
			ResultSet dbResult = stm.executeQuery(GenerateQuery.generateQueryTagsForEvent(e));
			while (dbResult.next())
				e.addTag(new Tag(dbResult.getInt("tag_id"), dbResult.getString("tag_name"),
						dbResult.getString("tag_description")));
		}
		return events;
	}

	/**
	 * Reinitialize the DB Connection
	 * 
	 * @return true if successfully else false
	 */
	public boolean reconnect() {
		try {
			conn = DriverManager.getConnection(info.getProperty("url"), info);
			stm = conn.createStatement();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/**
	 * Closes this stream and releases any system resources associated with it. If
	 * the stream is already closed then invoking this method has no effect.
	 *
	 * <p>
	 * As noted in {@link AutoCloseable#close()}, cases where the close may fail
	 * require careful attention. It is strongly advised to relinquish the
	 * underlying resources and to internally <em>mark</em> the {@code Closeable} as
	 * closed, prior to throwing the {@code IOException}.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	public void close() throws IOException {
		try {
			stm.close();
			conn.close();
		} catch (SQLException e) {
			throw new IOException("SQL Close Fehler", e);
		}

	}

	/**
	 * Creates a String containing a url matching the JDBC URL schema
	 * 
	 * @param dbDNSHostname
	 *            Host or IP the the DB
	 * @param dbPort
	 *            The Port where the DB is listening
	 * @param dbName
	 *            Name of the DB witch contains the data
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
	 * provides default values for nullable date and time
	 *
	 * @param date
	 *            The SQL Result Date may be null
	 * @param time
	 *            The SQL Result Time may be null
	 * @return LocalDateTime with default values if parameters are null
	 */
	private static LocalDateTime getLocalDateTime(Date date, Time time) {
		Date actualDate = date;
		if (actualDate == null)
			actualDate = new Date(0);
		Time actualTime = time;
		if (actualTime == null)
			actualTime = new Time(0);
		return LocalDateTime.of(actualDate.toLocalDate(), actualTime.toLocalTime());
	}

	/**
	 * Checks if the Connection was initialized before working on query
	 */
	private void checkConnection() {
		if (conn == null || stm == null) {
			throw new IllegalStateException("Please initialize Connection first ");
		}
	}

}
