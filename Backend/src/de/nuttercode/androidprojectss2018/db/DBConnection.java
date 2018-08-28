package de.nuttercode.androidprojectss2018.db;

import de.nuttercode.androidprojectss2018.csi.Event;
import de.nuttercode.androidprojectss2018.csi.Tag;

import java.sql.SQLException;
import java.util.Collection;

public class DBConnection {

	public DBConnection(String url, String user, String password) throws SQLException {
		DoQuery.GetConnection(url, user, password);
	}

	public Collection<Event> getAllEventsByRadiusAndLocation(double radius, double latitude, double longitude)
			throws SQLException {
		return DoQuery
				.GetEventsWithTag(DoQuery.GetEvents(GenerateQuery.GenerateQueryEvents(radius, latitude, longitude)));
	}

	public Collection<Tag> getAllTags() throws SQLException {
		return DoQuery.GetAllTags();
	}

}
