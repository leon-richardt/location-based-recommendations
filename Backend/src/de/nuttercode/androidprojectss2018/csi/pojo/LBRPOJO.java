package de.nuttercode.androidprojectss2018.csi.pojo;

import java.io.Serializable;

public abstract class LBRPOJO implements Serializable {
	
	private static final long serialVersionUID = 2162139559240206678L;
	
	private final int id;
	
	protected LBRPOJO(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
