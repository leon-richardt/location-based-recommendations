package de.nuttercode.androidprojectss2018.csi;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.nuttercode.androidprojectss2018.lbrserver.LBRServer;

/**
 * represents a LBRClients' connection to a {@link LBRServer}
 * 
 * @author Johannes B. Latzel
 *
 */
public class ServerConnection implements Closeable {

	/**
	 * time in milliseconds after which a socket will be closed if the
	 * {@link LBRServer} does not answer
	 */
	private final static int SOCKET_TIMEOUT = 3_000;

	/**
	 * socket of the connection to the {@link LBRServer}
	 */
	private final Socket socket;

	/**
	 * @param port
	 *            the port the {@link LBRServer} listens on - usually 5555
	 * @param serverDNSName
	 *            the DNS hostname of the {@link LBRServer}
	 * @throws UnknownHostException
	 *             when {@link #socket} does
	 * @throws IOException
	 *             when {@link #socket} does
	 * @throws IllegalArgumentException
	 *             if serverDNSName is empty or port <= 0
	 */
	public ServerConnection(int port, String serverDNSName) throws UnknownHostException, IOException {
		Assurance.assurePositive(port);
		Assurance.assureNotEmpty(serverDNSName);
		socket = new Socket(serverDNSName, port);
		socket.setSoTimeout(SOCKET_TIMEOUT);
	}

	/**
	 * writes the {@link LBRQuery} to the {@link OutputStream} of the
	 * {@link #socket}
	 * 
	 * @param lbrQuery
	 * @throws IOException
	 *             when {@link ObjectOutputStream#writeObject(Object)} does
	 */
	public void writeQuery(LBRQuery lbrQuery) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(lbrQuery);
		oos.flush();
	}

	/**
	 * reads the {@link LBRResult} of the answer of the {@link LBRServer} from the
	 * {@link InputStream} of the {@link #socket}
	 * 
	 * @param lbrQuery
	 * @throws IOException
	 *             when {@link ObjectInputStream#readObject()} does
	 */
	public LBRResult readResult() throws ClassNotFoundException, IOException, InterruptedException {
		if (socket.getInputStream().available() == 0) {
			// Thread.sleep(1000);
		}
		return (LBRResult) new ObjectInputStream(socket.getInputStream()).readObject();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

}
