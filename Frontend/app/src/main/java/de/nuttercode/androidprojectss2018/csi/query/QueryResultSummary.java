package de.nuttercode.androidprojectss2018.csi.query;

import java.io.Serializable;

import de.nuttercode.androidprojectss2018.csi.Assurance;

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
	private final String message;

	public QueryResultSummary(QueryResult<T> queryResult, QueryResultState queryResultState) {
		this(queryResult, queryResultState, "");
	}

	public QueryResultSummary(QueryResult<T> queryResult, QueryResultState queryResultState, String message) {
		Assurance.assureNotNull(queryResultState);
		Assurance.assureNotNull(message);
		this.queryResult = queryResult;
		if (queryResultState == QueryResultState.OK && queryResult == null)
			this.queryResultState = QueryResultState.Null;
		else
			this.queryResultState = queryResultState;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public QueryResult<T> getQueryResult() {
		return queryResult;
	}

	public QueryResultState getQueryResultState() {
		return queryResultState;
	}

	@Override
	public String toString() {
		return "QueryResultSummary [queryResult=" + queryResult + ", queryResultState=" + queryResultState + ", message=" + message + "]";
	}

}
