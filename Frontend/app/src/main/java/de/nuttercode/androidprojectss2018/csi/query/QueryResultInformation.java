package de.nuttercode.androidprojectss2018.csi.query;

import de.nuttercode.androidprojectss2018.csi.Assurance;

/**
 * encapsulates a state and a message of some {@link QueryResponse}
 * 
 * @author Johannes B. Latzel
 *
 */
public class QueryResultInformation {

	private final QueryResultState clientQueryResultState;
	private final QueryResultState serverQueryResultState;
	private final String message;

	public QueryResultInformation(QueryResultState clientQueryResultState, QueryResultState serverQueryResultState,
			String message) {
		Assurance.assureNotNull(clientQueryResultState);
		Assurance.assureNotNull(serverQueryResultState);
		Assurance.assureNotEmpty(message);
		this.clientQueryResultState = clientQueryResultState;
		this.serverQueryResultState = serverQueryResultState;
		this.message = message;
	}

	public QueryResultState getClientQueryResultState() {
		return clientQueryResultState;
	}

	public String getMessage() {
		return message;
	}

	public QueryResultState getServerQueryResultState() {
		return serverQueryResultState;
	}

	/**
	 * @return true if {@link #getServerQueryResultState()} and
	 *         {@link #clientQueryResultState} are {@link QueryResultState#OK}
	 */
	public boolean isOK() {
		return serverQueryResultState == QueryResultState.OK && clientQueryResultState == QueryResultState.OK;
	}

	@Override
	public String toString() {
		return "QueryResultInformation [clientQueryResultState=" + clientQueryResultState + ", serverQueryResultState="
				+ serverQueryResultState + ", message=" + message + "]";
	}

}
