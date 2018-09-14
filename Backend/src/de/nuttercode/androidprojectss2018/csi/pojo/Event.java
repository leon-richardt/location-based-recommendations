package de.nuttercode.androidprojectss2018.csi.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.nuttercode.androidprojectss2018.csi.Assurance;

/**
 * POJO for an Event entry in the database
 * 
 * @author Johannes B. Latzel
 *
 */
public class Event extends LBRPOJO {

	private static final long serialVersionUID = -6794608442641090075L;

	private final List<Tag> tagList;
	private String description;
	private String name;
	private final Venue venue;

	/**
	 * 
	 * @param venue
	 * @param name
	 * @param description
	 * @param id
	 * @throws IllegalArgumentException
	 *             if name is null or empty, if description is null, if or if venue
	 *             is null
	 */
	public Event(Venue venue, String name, String description, int id) {
		super(id);
		Assurance.assureNotNull(venue);
		this.tagList = new ArrayList<>();
		this.venue = venue;
		setName(name);
		setDescription(description);
	}

	public List<Tag> getTags() {
		return new ArrayList<>(tagList);
	}

	/**
	 * @param tag
	 * @throws IllegalArgumentException
	 *             if tag is null
	 */
	public void addTag(Tag tag) {
		Assurance.assureNotNull(tag);
		tagList.add(tag);
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
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
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
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
		if (tagList == null) {
			if (other.tagList != null)
				return false;
		} else if (!tagList.equals(other.tagList))
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
		return "Event [tagList=" + Arrays.toString(tagList.toArray()) + ", description=" + description + ", name="
				+ name + ", id=" + getId() + ", venue=" + venue + "]";
	}

}
