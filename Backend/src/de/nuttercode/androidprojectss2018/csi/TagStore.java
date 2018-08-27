package de.nuttercode.androidprojectss2018.csi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.nuttercode.androidprojectss2018.csi.query.QueryResultState;
import de.nuttercode.androidprojectss2018.csi.query.QueryResultSummary;
import de.nuttercode.androidprojectss2018.csi.query.TagQuery;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * stores all locally available {@link Tag}s and retreives new {@link Tag}s from
 * the {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 */
public class TagStore {

	private final Set<Tag> tagList;
	private final TagQuery tagQuery;

	/**
	 * 
	 * @param clientConfiguration
	 * @throws IllegalArgumentException
	 *             if {@link TagQuery#TagQuery(ClientConfiguration)} does
	 */
	public TagStore(ClientConfiguration clientConfiguration) {
		tagList = new HashSet<>();
		tagQuery = new TagQuery(clientConfiguration);
	}

	public List<Tag> getTags() {
		return new ArrayList<>(tagList);
	}

	/**
	 * retrieves all locally unknown {@link Tag}s from the {@link LBRServer}
	 * 
	 * @return
	 */
	public QueryResultState refreshTags() {
		QueryResultSummary<Tag> resultSummary = tagQuery.run();
		if (resultSummary.getQueryResultState() == QueryResultState.OK) {
			tagList.clear();
			tagList.addAll(resultSummary.getQueryResult().getAll());
		}
		return resultSummary.getQueryResultState();
	}

	@Override
	public String toString() {
		return "TagStore [tagList=" + Arrays.toString(tagList.toArray()) + "]";
	}

}
