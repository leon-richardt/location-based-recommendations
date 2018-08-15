package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * POJO for an Event entry in the database
 * 
 * @author Johannes B. Latzel
 *
 */
public class Event implements Serializable {

	private static final long serialVersionUID = -6794608442641090075L;

	private final List<Genre> genreList;
	private String description;
	private String name;
	private final int id;
	private final Venue venue;

	/**
	 * 
	 * @param venue
	 * @param genreCollection
	 * @param name
	 * @param description
	 * @param id
	 * @throws IllegalArgumentException
	 *             if name is null or empty, if description is null, if
	 *             genreCollection is null, or if venue is null
	 */
	public Event(Venue venue, Collection<Genre> genreCollection, String name, String description, int id) {
		Assurance.assureNotNull(venue);
		Assurance.assureNotNull(genreCollection);
		this.genreList = new ArrayList<>(genreCollection);
		this.id = id;
		this.venue = venue;
		setName(name);
		setDescription(description);
	}

	public List<Genre> getGenres() {
		return new ArrayList<>(genreList);
	}

	/**
	 * @param genre
	 * @throws IllegalArgumentException
	 *             if genre is null
	 */
	public void addGenre(Genre genre) {
		Assurance.assureNotNull(genre);
		genreList.add(genre);
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

	/**
	 * @param name
	 * @throws IllegalArgumentException
	 *             if name is null or empty
	 */
	public void setName(String name) {
		Assurance.assureNotEmpty(name);
		this.name = name;
	}

	/**
	 * @param description
	 * @throws IllegalArgumentException
	 *             if description is null
	 */
	public void setDescription(String description) {
		Assurance.assureNotNull(description);
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((genreList == null) ? 0 : genreList.hashCode());
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
		if (genreList == null) {
			if (other.genreList != null)
				return false;
		} else if (!genreList.equals(other.genreList))
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
		return "Event [genreList=" + genreList + ", description=" + description + ", name=" + name + ", id=" + id
				+ ", venue=" + venue + "]";
	}

}
