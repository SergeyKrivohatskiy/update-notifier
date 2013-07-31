package main.java.net.thumbtack.updateNotifierBackend.databaseService;

import java.util.HashSet;
import java.util.Set;

public class AccountInfo {

	private Long id;
	private String email;
	private Set<ResourceInfo> resources = new HashSet<ResourceInfo>();
	private Set<Category> categories = new HashSet<Category>();
	
	public AccountInfo() {
		
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
		AccountInfo other = (AccountInfo) obj;
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
	
	public Set<ResourceInfo> getResources() {
		return resources;
	}
	
	public void setResources(Set<ResourceInfo> resources) {
		this.resources = resources;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}
	
}
