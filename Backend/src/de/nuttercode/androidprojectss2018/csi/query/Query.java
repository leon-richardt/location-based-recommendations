package de.nuttercode.androidprojectss2018.csi.query;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.csi.pojo.LBRPOJO;

/**
 * used to query elements of type T from the {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 * @param <T>
 *            any Serializable
 */
public class Query<T extends LBRPOJO> implements Serializable {

	private static final long serialVersionUID = 2130540249378192775L;

	private String serverDNSName;
	private int serverPort;

	/**
	 * 
	 * @param clientConfiguration
	 * @throws IllegalArgumentException
	 *             if clientConfiguration is null
	 */
	protected Query(ClientConfiguration clientConfiguration) {
		setClientConfiguration(clientConfiguration);
	}

	/**
	 * runs the query - sends itself to the {@link LBRServer} and waits for an
	 * answer
	 * 
	 * @return an {@link QueryResultSummary} representing the answer of the
	 *         {@link LBRServer} or an error state
	 */
	@SuppressWarnings("unchecked")
	public QueryResultSummary<T> run() {
		QueryResponse<T> queryResponse = null;
		QueryResultState clientQueryResultState = QueryResultState.OK;
		String message = "OK";
		try (ServerConnection clientConnection = new ServerConnection(serverPort, serverDNSName)) {
			clientConnection.writeQuery(this);
			queryResponse = (QueryResponse<T>) clientConnection.readResponse();
			if (queryResponse.getServerQueryResultState() != QueryResultState.OK)
				message = "Server Problem - please try again later";
		} catch (ClassCastException e) {
			clientQueryResultState = QueryResultState.ClassCastException;
			message = e.getMessage();
		} catch (UnknownHostException e) {
			clientQueryResultState = QueryResultState.UnknownHostException;
			message = e.getMessage();
		} catch (IOException e) {
			clientQueryResultState = QueryResultState.IOException;
			message = e.getMessage();
		} catch (ClassNotFoundException e) {
			clientQueryResultState = QueryResultState.ClassNotFoundException;
			message = e.getMessage();
		} catch (RuntimeException e) {
			clientQueryResultState = QueryResultState.RuntimeException;
			message = e.getMessage();
		}
		if (queryResponse != null)
			return new QueryResultSummary<>(queryResponse.getQueryResult(), new QueryResultInformation(
					clientQueryResultState, queryResponse.getServerQueryResultState(), message));
		else {
			// kotlin type inference problem - use of T directly
			return new QueryResultSummary<>(new QueryResult<>(new ArrayList<T>()),
					new QueryResultInformation(clientQueryResultState, QueryResultState.Null, message));
		}
	}

	/**
	 * copies needed values for this query from the {@link ClientConfiguration}. Use
	 * this method if the {@link ClientConfiguration} has changed.
	 * 
	 * @param clientConfiguration
	 * @throws IllegalArgumentException
	 *             if clientConfiguration is null
	 */
	public void setClientConfiguration(ClientConfiguration clientConfiguration) {
		Assurance.assureNotNull(clientConfiguration);
		serverDNSName = clientConfiguration.getServerDNSName();
		serverPort = clientConfiguration.getServerPort();
	}

}
