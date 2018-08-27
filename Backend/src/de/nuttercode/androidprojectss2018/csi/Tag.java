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
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Tag other = (Tag) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}
