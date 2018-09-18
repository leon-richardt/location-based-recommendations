package de.nuttercode.androidprojectss2018.csi.query;

import java.io.Serializable;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * encapsulates {@link QueryResult} and {@link QueryResultState} as send by
 * {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 * @param <T>
 */
public class QueryResponse<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = -3074134072459244746L;

	private final QueryResult<T> queryResult;
	private final QueryResultState serverQueryResultState;

	public QueryResponse(QueryResult<T> queryResult, QueryResultState serverQueryResultState) {
		Assurance.assureNotNull(queryResult);
		Assurance.assureNotNull(serverQueryResultState);
		this.queryResult = queryResult;
		this.serverQueryResultState = serverQueryResultState;
	}

	@Override
	public String toString() {
		return "LBRQueryResponse [queryResult=" + queryResult + ", serverQueryResultState=" + serverQueryResultState
				+ "]";
	}

	public QueryResult<T> getQueryResult() {
		return queryResult;
	}

	public QueryResultState getServerQueryResultState() {
		return serverQueryResultState;
	}

}
