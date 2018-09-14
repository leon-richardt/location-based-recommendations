package de.nuttercode.androidprojectss2018.csi.pojo;

import java.io.Serializable;

/**
 * base class for all POJOs
 * 
 * @author Johannes B. Latzel
 *
 */
public abstract class LBRPOJO implements Serializable {

	private static final long serialVersionUID = 2162139559240206678L;

	private final int id;

	protected LBRPOJO(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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
		LBRPOJO other = (LBRPOJO) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LBRPOJO [id=" + id + "]";
	}

}
