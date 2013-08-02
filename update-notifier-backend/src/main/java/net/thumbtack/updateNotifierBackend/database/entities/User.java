package net.thumbtack.updateNotifierBackend.database.entities;

import java.util.HashSet;
import java.util.Set;

public class User {

	private Long id;
	private String email;
	private Set<Resource> resources = new HashSet<Resource>();
	private Set<Tag> tags = new HashSet<Tag>();
	
	public User() {
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Set<Resource> getResources() {
		return resources;
	}
	
	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> categories) {
		this.tags = categories;
	}
	
}
