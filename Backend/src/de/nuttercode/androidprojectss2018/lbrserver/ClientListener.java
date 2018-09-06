package de.nuttercode.androidprojectss2018.lbrserver;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;

import de.nuttercode.androidprojectss2018.csi.Assurance;

/**
 * 
 * listens for incoming LBRClient requests and stores them temporary. poll
 * requests by using {@link #getRequest()}
 * 
 * @author Johannes B. Latzel
 *
 */
public class ClientListener implements Closeable {

	/**
	 * determines the milliseconds after which a socket will be closed if the
	 * LBRClient sends no data
	 */
	private final static int SERVER_SOCKET_TIMEOUT = 30_000;

	/**
	 * max time in milliseconds a {@link #getRequest()} will wait for the
	 * {@link #pendingClientQueue} to be filled with at least 1 pending connection
	 */
	private final static int PENDING_QUEUE_WAIT = 5_000;

	/**
	 * used to accept LBRClient connections
	 */
	private final ServerSocket serverSocket;

	/**
	 * collects LBRClient connections
	 */
	private final Thread listenerThread;

	/**
	 * true if this instance has been closed
	 */
	private boolean isClosed;

	/**
	 * a queue of all non-processed LBRClient connections
	 */
	private final Queue<Socket> pendingClientQueue;

	/**
	 * 
	 * @param port
	 * @throws IOException
	 *             if {@link ServerSocket} does
	 * @throws IllegalArgumentException
	 *             if port <= 0
	 */
	public ClientListener(int port) throws IOException {
		Assurance.assurePositive(port);
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);
		listenerThread = new Thread(this::run);
		listenerThread.setName("LBRClientListenerThread");
		listenerThread.start();
		isClosed = false;
		pendingClientQueue = new LinkedList<>();
	}

	/**
	 * accepts sockets and queues them in {@link #pendingClientQueue}
	 */
	private void run() {
		Socket socket;
		while (!isClosed) {
			try {
				socket = serverSocket.accept();
				synchronized (pendingClientQueue) {
					pendingClientQueue.add(socket);
					pendingClientQueue.notifyAll();
				}
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * blocks until a request by a LBRClient is available
	 * 
	 * @return socket of LBRClient connection
	 * @throws InterruptedException
	 *             on {@link #wait()} of {@link #pendingClientQueue}
	 */
	public Socket getRequest() throws InterruptedException {
		synchronized (pendingClientQueue) {
			while (pendingClientQueue.isEmpty())
				pendingClientQueue.wait(PENDING_QUEUE_WAIT);
			return pendingClientQueue.poll();
		}
	}

	@Override
	public void close() throws IOException {
		if (isClosed)
			return;
		isClosed = true;
		listenerThread.interrupt();
		serverSocket.close();
	}

}
