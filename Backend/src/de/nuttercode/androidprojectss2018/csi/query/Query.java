package de.nuttercode.androidprojectss2018.csi.query;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;

import de.nuttercode.androidprojectss2018.csi.Assurance;
import de.nuttercode.androidprojectss2018.csi.ClientConfiguration;
import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

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

	protected final ClientConfiguration clientConfiguration;

	/**
	 * 
	 * @param clientConfiguration
	 * @throws IllegalArgumentException
	 *             if clientConfiguration is null
	 */
	protected Query(ClientConfiguration clientConfiguration) {
		Assurance.assureNotNull(clientConfiguration);
		this.clientConfiguration = clientConfiguration;
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
		try (ServerConnection clientConnection = new ServerConnection(clientConfiguration.getServerPort(),
				clientConfiguration.getServerDNSName())) {
			clientConnection.writeQuery(this);
			queryResult = (QueryResult<T>) clientConnection.readResult();
		} catch (ClassCastException e) {
			queryResultState = QueryResultState.ClassCastException;
		} catch (UnknownHostException e) {
			queryResultState = QueryResultState.UnknownHostException;
		} catch (IOException e) {
			queryResultState = QueryResultState.IOException;
		} catch (ClassNotFoundException e) {
			queryResultState = QueryResultState.ClassNotFoundException;
		} catch (InterruptedException e) {
			queryResultState = QueryResultState.InterruptedException;
		} catch (RuntimeException e) {
			queryResultState = QueryResultState.RuntimeException;
		}
		return new QueryResultSummary<>(queryResult, queryResultState);
	}

}
