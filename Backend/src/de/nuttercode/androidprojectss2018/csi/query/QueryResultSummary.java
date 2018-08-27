package de.nuttercode.androidprojectss2018.csi.query;

import java.io.Serializable;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * contains the {@link QueryResult} and the {@link QueryResultState} of a
 * {@link Query} as received from the {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 * @param <T> any {@link Serializable}
 */
public class QueryResultSummary<T extends Serializable> {

	private final QueryResult<T> queryResult;
	private final QueryResultState queryResultState;

	public QueryResultSummary(QueryResult<T> queryResult, QueryResultState queryResultState) {
		Assurance.assureNotNull(queryResultState);
		this.queryResult = queryResult;
		this.queryResultState = queryResultState;
		if (queryResultState == QueryResultState.OK && queryResult == null)
			queryResultState = QueryResultState.Null;
	}

	public QueryResult<T> getQueryResult() {
		return queryResult;
	}

	public QueryResultState getQueryResultState() {
		return queryResultState;
	}

	@Override
	public String toString() {
		return "QueryResultSummary [queryResult=" + queryResult + ", queryResultState=" + queryResultState + "]";
	}

}
