package de.nuttercode.androidprojectss2018.csi.query;

import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.Tag;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * used to query {@link Tag}s from the {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 */
public class TagQuery extends Query<Tag> {

	private static final long serialVersionUID = 1047614553393401738L;

	public TagQuery(ClientConfiguration clientConfiguration) {
		super(clientConfiguration);
	}

}
