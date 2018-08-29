package de.nuttercode.androidprojectss2018.db;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.csi.Venue;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * provides static access to MySQL DB for LBR-Instances
 * 
 * @author Henrik Gerdes, Johannes B. Latzel
 *
 */
public class DoQuery {

	private static Connection conn = null;
	private static Statement stm = null;

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
	public static void getConnection(String url, String user, String password) throws SQLException {
		conn = DriverManager.getConnection(url, user, password);
		stm = conn.createStatement();

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
	public static ArrayList<Event> getEvents(String query) throws SQLException {
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
	 * provides default values for nullable date and time
	 * 
	 * @param date
	 * @param time
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
	public static ArrayList<Event> getEventsWithTag(ArrayList<Event> events) throws SQLException {
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
	 * Gets all Tags
	 * 
	 * @return ArrayList of all Tags
	 * @throws SQLException
	 *             SQL error
	 */
	public static ArrayList<Tag> getAllTags() throws SQLException {
		checkConnection();
		ResultSet dbResult = stm.executeQuery(GenerateQuery.generateQueryForAllTags());
		ArrayList<Tag> tags = new ArrayList<>();
		while (dbResult.next())
			tags.add(new Tag(dbResult.getInt("tag_id"), dbResult.getString("tag_name"),
					dbResult.getString("tag_description")));
		return tags;
	}

	/**
	 * Checks if the Connection was initialized before working on query
	 */
	private static void checkConnection() {
		if (conn == null || stm == null) {
			throw new IllegalStateException("Please initialize Connection first ");
		}
	}
}
