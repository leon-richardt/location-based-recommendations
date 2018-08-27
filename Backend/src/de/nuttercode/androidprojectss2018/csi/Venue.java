package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;

import de.nuttercode.androidprojectss2018.csi.query.LBRQuery;

/**
 * POJO for a Venue entry in the database
 * 
 * @author Johannes B. Latzel
 *
 */
public class Venue implements Serializable {

	private static final long serialVersionUID = -2902776286525562764L;

	private final String description;
	private final int id;
	private final String name;
	private final double longitude;
	private final double latitude;

	/**
	 * the distance of this venue to the user in kilometers (km) at the time when
	 * {@link LBRQuery#run()} was run
	 */
	private final double initialDistance;

	/**
	 * 
	 * @param description
	 * @param id
	 * @param name
	 * @param longitude
	 * @param latitude
	 * @param initialDistance
	 * @throws IllegalArgumentException
	 *             if description or name is name, name is empty, or initialDistance
	 *             < 0
	 */
	public Venue(String description, int id, String name, double longitude, double latitude, double initialDistance) {
		Assurance.assureNotNull(description);
		Assurance.assureNotEmpty(name);
		Assurance.assureNotNegative(initialDistance);
		this.description = description;
		this.id = id;
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
		this.initialDistance = initialDistance;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getInitialDistance() {
		return initialDistance;
	}

	@Override
	public String toString() {
		return "Venue [description=" + description + ", id=" + id + ", name=" + name + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", initialDistance=" + initialDistance + "]";
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Venue other = (Venue) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(initialDistance) != Double.doubleToLongBits(other.initialDistance))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}