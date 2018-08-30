package de.nuttercode.androidprojectss2018.csi;

import de.nuttercode.androidprojectss2018.csi.query.LBRQuery;

/**
 * stores all {@link ScoredEvent}s received from the {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 */
public class EventStore extends Store<ScoredEvent> {

	/**
	 * @param clientConfiguration
	 * @throws IllegalArgumentException
	 *             if {@link Store#Store(Query)} does
	 */
	public EventStore(ClientConfiguration clientConfiguration) {
		super(new LBRQuery(clientConfiguration));
	}

	/**
	 * sets the new user location
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setUserLocation(double latitude, double longitude) {
		LBRQuery lbrQuery = (LBRQuery) query;
		lbrQuery.setLatitude(latitude);
		lbrQuery.setLongitude(longitude);
	}

}
