package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;

/**
 * represents the configuration of a LBRClient - may be saved locally by
 * serialization
 * 
 * @author Johannes B. Latzel
 *
 */
public class ClientConfiguration implements Serializable {

	private static final long serialVersionUID = 135520319529800968L;

	private final GenrePreferenceConfiguration genrePreferenceConfiguration;

	/**
	 * distance of farthest considerable event
	 */
	private double radius;
	
	/**
	 * DNS hostname of the LBRServer
	 */
	private String serverDNSName;
	
	/**
	 * TCP-Port of the LBRServer
	 */
	private int serverPort;

	public ClientConfiguration() {
		genrePreferenceConfiguration = new GenrePreferenceConfiguration();
		radius = 1.0;
		serverDNSName = "localhost";
		serverPort = 5555;
	}

	public GenrePreferenceConfiguration getGenrePreferenceConfiguration() {
		return genrePreferenceConfiguration;
	}

	public double getRadius() {
		return radius;
	}

	public String getServerDNSName() {
		return serverDNSName;
	}

	public int getServerPort() {
		return serverPort;
	}

	/**
	 * setter for radius
	 * 
	 * @param radius
	 * @throws IllegalArgumentException when radius <= 0
	 */
	public void setRadius(double radius) {
		Assurance.assurePositive(radius);
		this.radius = radius;
	}

	/**
	 * setter for serverDNSName
	 * 
	 * @param serverDNSName
	 * @throws IllegalArgumentException when serverDNSName is empty or null
	 */
	public void setServerDNSName(String serverDNSName) {
		Assurance.assureNotEmpty(serverDNSName);
		this.serverDNSName = serverDNSName;
	}

	/**
	 * setter for serverPort
	 * 
	 * @param serverPort
	 * @throws IllegalArgumentException when serverPort <= 0
	 */
	public void setServerPort(int serverPort) {
		Assurance.assurePositive(serverPort);
		this.serverPort = serverPort;
	}

	@Override
	public String toString() {
		return "ClientConfiguration [genrePreferenceConfiguration=" + genrePreferenceConfiguration + ", radius="
				+ radius + ", serverDNSName=" + serverDNSName + ", serverPort=" + serverPort + "]";
	}

}
