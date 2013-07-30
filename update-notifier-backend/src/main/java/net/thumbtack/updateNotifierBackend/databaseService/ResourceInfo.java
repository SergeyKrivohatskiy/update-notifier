package main.java.net.thumbtack.updateNotifierBackend.databaseService;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ResourceInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Long id;

	private transient Long resourceHash;
	private String url;
	private Set<Category> categories = new HashSet<Category>();
	private transient AccountInfo account;

	public ResourceInfo() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Long getResourceHash() {
		return resourceHash;
	}

	public void setResourceHash(Long resourceHash) {
		this.resourceHash = resourceHash;
	}

	public AccountInfo getAccount() {
		return account;
	}

	public void setAccount(AccountInfo account) {
		this.account = account;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

}
