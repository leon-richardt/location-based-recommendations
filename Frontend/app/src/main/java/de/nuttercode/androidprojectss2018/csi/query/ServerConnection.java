package de.nuttercode.androidprojectss2018.csi.query;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.nuttercode.androidprojectss2018.csi.Assurance;

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
	private final static int SOCKET_TIMEOUT = 5_000;

	/**
	 * socket of the connection to the {@link LBRServer}
	 */
	private final Socket socket;

	/**
	 * used to send objects to the {@link LBRServer}
	 */
	private final ObjectOutputStream objectOutputStream;

	/**
	 * used to receive objects from the {@link LBRServer}
	 */
	private ObjectInputStream objectInputStream;

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
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		// can't initialize objectInputStream as the LBRServer might not have send any
		// objects yet. the constructor would block until a timeout was reached.
		objectInputStream = null;
	}

	/**
	 * @return the next object read from the {@link InputStream} of the
	 *         {@link #socket}
	 * @throws ClassNotFoundException
	 *             when {@link ObjectInputStream#readObject()} does
	 * @throws IOException
	 *             when {@link ObjectInputStream#readObject()} does
	 */
	private Object readObject() throws ClassNotFoundException, IOException {
		if (objectInputStream == null)
			objectInputStream = new ObjectInputStream(socket.getInputStream());
		return objectInputStream.readObject();
	}

	/**
	 * writes the {@link Query} to the {@link OutputStream} of the {@link #socket}
	 * 
	 * @param query
	 * @throws IOException
	 *             when {@link ObjectOutputStream#writeObject(Object)} does
	 */
	public void writeQuery(Query<?> query) throws IOException {
		objectOutputStream.writeObject(query);
		objectOutputStream.flush();
	}

	/**
	 * reads the {@link QueryResultState} of the answer of the {@link LBRServer}
	 * from the {@link InputStream} of the {@link #socket}
	 * 
	 * @throws IOException
	 *             when {@link #readObject()} does
	 * @throws ClassNotFoundException
	 *             when {@link #readObject()} does
	 */
	public QueryResponse<?> readResponse() throws ClassNotFoundException, IOException {
		return (QueryResponse<?>) readObject();
	}

	@Override
	public void close() throws IOException {
		socket.close();
		objectOutputStream.close();
		if (objectInputStream != null)
			objectInputStream.close();
	}

}
