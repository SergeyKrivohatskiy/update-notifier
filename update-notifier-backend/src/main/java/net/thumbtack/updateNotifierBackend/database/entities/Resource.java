package net.thumbtack.updateNotifierBackend.database.entities;

import java.io.Serializable;

public class Resource implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private transient Long user_id;

	private transient Long resourceHash;
	private String url;
	private int hash;
	private Long[] tagsIdArray;
	
	public Resource() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public Long getResourceHash() {
		return resourceHash;
	}

	public Long[] getTagsIdArray() {
		return tagsIdArray;
	}


	public int getHash() {
		return hash;
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Resource other = (Resource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}
