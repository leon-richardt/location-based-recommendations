package de.nuttercode.androidprojectss2018.csi.query;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;

/**
 * used to query elements of type T from the {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 * @param <T>
 *            any Serializable
 */
public class Query<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 2130540249378192775L;

	private final String serverDNSName;
	private final int serverPort;

	/**
	 * 
	 * @param clientConfiguration
	 * @throws IllegalArgumentException
	 *             if clientConfiguration is null
	 */
	protected Query(ClientConfiguration clientConfiguration) {
		Assurance.assureNotNull(clientConfiguration);
		serverDNSName = clientConfiguration.getServerDNSName();
		serverPort = clientConfiguration.getServerPort();
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
		QueryResult<T> queryResult = null;
		QueryResultState queryResultState = QueryResultState.OK;
		String message = "OK";
		try (ServerConnection clientConnection = new ServerConnection(serverPort, serverDNSName)) {
			clientConnection.writeQuery(this);
			queryResult = (QueryResult<T>) clientConnection.readResult();
		} catch (ClassCastException e) {
			queryResultState = QueryResultState.ClassCastException;
			message = e.getMessage();
		} catch (UnknownHostException e) {
			queryResultState = QueryResultState.UnknownHostException;
			message = e.getMessage();
		} catch (IOException e) {
			queryResultState = QueryResultState.IOException;
			message = e.getMessage();
		} catch (ClassNotFoundException e) {
			queryResultState = QueryResultState.ClassNotFoundException;
			message = e.getMessage();
		} catch (InterruptedException e) {
			queryResultState = QueryResultState.InterruptedException;
			message = e.getMessage();
		} catch (RuntimeException e) {
			queryResultState = QueryResultState.RuntimeException;
			message = e.getMessage();
		}
		return new QueryResultSummary<>(queryResult, new QueryResultInformation(queryResultState, message));
	}

}
