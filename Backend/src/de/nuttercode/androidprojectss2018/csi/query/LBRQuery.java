package de.nuttercode.androidprojectss2018.csi.query;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.ScoredEvent;
import de.nuttercode.androidprojectss2018.csi.TagPreferenceConfiguration;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * used to query {@link ScoredEvent}s from the {@link LBRServer}
 * 
 * @author johannes
 *
 */
public class LBRQuery extends Query<ScoredEvent> {

	private static final long serialVersionUID = 2485145114980361102L;

	public LBRQuery(ClientConfiguration clientConfiguration) {
		super(clientConfiguration);
	}

	public TagPreferenceConfiguration getTagPreferenceConfiguration() {
		return clientConfiguration.getTagPreferenceConfiguration();
	}

}
