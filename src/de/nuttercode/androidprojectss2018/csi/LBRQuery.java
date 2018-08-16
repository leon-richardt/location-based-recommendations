package de.nuttercode.androidprojectss2018.csi;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;

import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * instances of LBRQueries can be used as often as needed to query events from
 * the {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 */
public class LBRQuery implements Serializable {

	private static final long serialVersionUID = -4918607867958232990L;

	private final ClientConfiguration clientConfiguration;

	public LBRQuery(ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
	}

	public ClientConfiguration getClientConfiguration() {
		return clientConfiguration;
	}

	/**
	 * runs the query - sends the {@link ClientConfiguration} to the
	 * {@link LBRServer} and waits for an answer
	 * 
	 * @return an {@link LBRResultSummary} representing the answer of the
	 *         {@link LBRServer} or an error state
	 */
	public LBRResultSummary run() {
		LBRResult lbrResult = null;
		LBRResultState lbrResultState = LBRResultState.OK;
		try (ServerConnection clientConnection = new ServerConnection(clientConfiguration.getServerPort(),
				clientConfiguration.getServerDNSName())) {
			clientConnection.writeQuery(this);
			lbrResult = clientConnection.readResult();
		} catch (UnknownHostException e) {
			lbrResultState = LBRResultState.UnknownHostException;
		} catch (IOException e) {
			lbrResultState = LBRResultState.IOException;
		} catch (ClassNotFoundException e) {
			lbrResultState = LBRResultState.ClassNotFoundException;
		} catch (InterruptedException e) {
			lbrResultState = LBRResultState.InterruptedException;
		} catch (RuntimeException e) {
			lbrResultState = LBRResultState.RuntimeException;
		}
		return new LBRResultSummary(lbrResult, lbrResultState);
	}

	@Override
	public String toString() {
		return "LBRQuery [clientConfiguration=" + clientConfiguration + "]";
	}

}
