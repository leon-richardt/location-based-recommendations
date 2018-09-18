package de.nuttercode.androidprojectss2018.csi.config;

import java.io.Serializable;

import de.nuttercode.androidprojectss2018.csi.Assurance;

/**
 * represents the configuration of a LBRClient - may be saved locally by
 * serialization
 * 
 * @author Johannes B. Latzel
 *
 */
public class ClientConfiguration implements Serializable {

	/**
	 * this upper bound constraints the radius to a maximum of this value - the unit
	 * is the same as of {@link #radius}
	 */
	private final static double RADIUS_UPPER_BOUND = 200.0;		// TODO: Change back to 100.0

	private static final long serialVersionUID = 135520319529800968L;

	public final static int DEFAULT_LBR_PORT = 5445;
	private final static String DEFAULT_LBR_DNS_NAME = "lbr.nuttercode.de";

	/**
	 * default value for {@link #radius} - same unit as {@link #radius}
	 */
	private final static double DEFAULT_RADIUS = 1.0;

	private final TagPreferenceConfiguration tagPreferenceConfiguration;

	/**
	 * distance of farthest considerable event in kilometers (km)
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
		tagPreferenceConfiguration = new TagPreferenceConfiguration();
		radius = DEFAULT_RADIUS;
		serverDNSName = DEFAULT_LBR_DNS_NAME;
		serverPort = DEFAULT_LBR_PORT;
	}

	public TagPreferenceConfiguration getTagPreferenceConfiguration() {
		return tagPreferenceConfiguration;
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
	 * @throws IllegalArgumentException
	 *             when 0 <= radius <= RADIUS_UPPER_BOUND
	 */
	public void setRadius(double radius) {
		Assurance.assurePositive(radius);
		Assurance.assureBoundaries(radius, 0, RADIUS_UPPER_BOUND);
		this.radius = radius;
	}

	/**
	 * setter for serverDNSName
	 * 
	 * @param serverDNSName
	 * @throws IllegalArgumentException
	 *             when serverDNSName is empty or null
	 */
	public void setServerDNSName(String serverDNSName) {
		Assurance.assureNotEmpty(serverDNSName);
		this.serverDNSName = serverDNSName;
	}

	/**
	 * setter for serverPort
	 * 
	 * @param serverPort
	 * @throws IllegalArgumentException
	 *             when serverPort <= 0
	 */
	public void setServerPort(int serverPort) {
		Assurance.assurePositive(serverPort);
		this.serverPort = serverPort;
	}

	@Override
	public String toString() {
		return "ClientConfiguration [tagPreferenceConfiguration=" + tagPreferenceConfiguration + ", radius="
				+ radius + ", serverDNSName=" + serverDNSName + ", serverPort=" + serverPort + "]";
	}

}
