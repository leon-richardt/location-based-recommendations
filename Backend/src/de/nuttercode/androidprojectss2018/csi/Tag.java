package de.nuttercode.androidprojectss2018.csi;

import java.io.Serializable;

/**
 * POJO for a Tag entry in the database
 * 
 * @author Johannes B. Latzel
 *
 */
public class Tag implements Serializable {

	private static final long serialVersionUID = 1193555989071795461L;

	private final int id;
	private final String name;
	private final String description;

	public Tag(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
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
		Tag other = (Tag) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}
