package de.nuttercode.androidprojectss2018.csi.pojo;

/**
 * POJO for a Tag entry in the database
 * 
 * @author Johannes B. Latzel
 *
 */
public class Tag extends LBRPOJO {

	private static final long serialVersionUID = 1193555989071795461L;

	private final String name;
	private final String description;

	public Tag(int id, String name, String description) {
		super(id);
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
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
		Tag other = (Tag) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		return "Tag [id=" + getId() + ", name=" + name + ", description=" + description + "]";
	}

}
