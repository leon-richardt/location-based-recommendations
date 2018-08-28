package de.nuttercode.androidprojectss2018.csi.query;

import de.nuttercode.androidprojectss2018.csi.Assurance;

/**
 * encapsulates a state and a message of some {@link Query}
 * 
 * @author Johannes B. Latzel
 *
 */
public class QueryResultInformation {

	private final QueryResultState queryResultState;
	private final String message;

	public QueryResultInformation(QueryResultState queryResultState, String message) {
		Assurance.assureNotNull(queryResultState);
		Assurance.assureNotEmpty(message);
		this.queryResultState = queryResultState;
		this.message = message;
	}

	public QueryResultState getQueryResultState() {
		return queryResultState;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "QueryResultInformation [queryResultState=" + queryResultState + ", message=" + message + "]";
	}

}
