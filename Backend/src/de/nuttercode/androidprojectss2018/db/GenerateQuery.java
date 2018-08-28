package de.nuttercode.androidprojectss2018.db;

import de.nuttercode.androidprojectss2018.csi.Event;

public class GenerateQuery {
	/**
	 * Generates a SQL Query to get Events near the given latitude and longitude
	 * 
	 * @param radius
	 *            The radius that matches the Event location to the current location
	 * @param latitude
	 *            Latitude of the current position
	 * @param longitude
	 *            Longitude of the current position
	 * @return String ready to be executed by a SQL Database
	 */
	public static String GenerateQueryEvents(double radius, double latitude, double longitude) {
		StringBuilder str = new StringBuilder();

		str.append("SELECT  Distinct e.event_id, e.event_name, e.date, e.time, e.event_description,"
				+ "v.venue_id, v.venue_name, v.venue_description, v.latitude, v.longitude,"
				+ "(6371 * acos( cos( radians(" + latitude
				+ "))* cos( radians( latitude)) * cos(radians( longitude) - radians(" + longitude + "))+ sin(radians( "
				+ latitude + ")) * sin(radians( latitude)))) AS distance "
				+ "FROM Event e, Event_Tag et, Venue v, Tag t \n"
				+ "where e.venue_id=v.venue_id and et.event_id = e.event_id and t.tag_id = et.tag_id having distance < "
				+ radius + " ORDER BY distance ASC;");

		return str.toString();
	}

	/**
	 * Generates a SQL Query to get ALL Tags for one Event
	 * 
	 * @param event
	 *            The Event to get the Tags for
	 * @return String ready to be executed by a SQL Database
	 */
	public static String GenerateQueryTagsForEvent(Event event) {
		StringBuilder str = new StringBuilder();
		str.append("SELECT t.tag_id, t.tag_name, t.tag_description from Event_Tag ev, Tag t\n" + "where ev.event_id ="
				+ event.getId() + " and ev.tag_id = t.tag_id");

		return str.toString();

	}

	/**
	 * Generates Query to get all Tags form DB
	 * 
	 * @return SQL Query to get all Tags
	 */
	public static String GenerateQueryForAllTags() {
		return "Select * from Tag";
	}
}
