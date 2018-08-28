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
	private final QueryResultInformation queryResultInformation;

	public QueryResultSummary(QueryResult<T> queryResult, QueryResultInformation queryResultInformation) {
		Assurance.assureNotNull(queryResultInformation);
		this.queryResult = queryResult;
		this.queryResultInformation = queryResultInformation;
	}

	public QueryResult<T> getQueryResult() {
		return queryResult;
	}

	public QueryResultInformation getQueryResultInformation() {
		return queryResultInformation;
	}

	@Override
	public String toString() {
		return "QueryResultSummary [queryResult=" + queryResult + ", queryResultInformation=" + queryResultInformation
				+ "]";
	}

}
