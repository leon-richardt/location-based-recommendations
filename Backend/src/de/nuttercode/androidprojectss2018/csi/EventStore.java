package de.nuttercode.androidprojectss2018.csi;

import de.nuttercode.androidprojectss2018.csi.query.LBRQuery;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

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

}
