package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;
import java.util.Arrays;

/**
 * POJO for an Event entry in the database
 * 
 * @author Johannes B. Latzel
 *
 */
public class Event implements Serializable {

	private static final long serialVersionUID = -6794608442641090075L;

	private final Genre[] genres;
	private final String description;
	private final String name;
	private final int id;
	private final Venue venue;

	public Event(Venue venue, Genre[] genres, String name, String description, int id) {
		this.genres = genres;
		this.name = name;
		this.description = description;
		this.id = id;
		this.venue = venue;
	}

	public Genre[] getGenres() {
		return genres;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public Venue getVenue() {
		return venue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + Arrays.hashCode(genres);
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((venue == null) ? 0 : venue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (!Arrays.equals(genres, other.genres))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (venue == null) {
			if (other.venue != null)
				return false;
		} else if (!venue.equals(other.venue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Event [genres=" + Arrays.toString(genres) + ", description=" + description + ", name=" + name + ", id="
				+ id + ", venue=" + venue + "]";
	}

}
