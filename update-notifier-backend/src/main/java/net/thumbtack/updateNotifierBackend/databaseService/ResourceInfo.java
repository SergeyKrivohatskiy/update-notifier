package main.java.net.thumbtack.updateNotifierBackend.databaseService;

import java.util.HashSet;
import java.util.Set;

public class ResourceInfo {
	
	private Long id;
	private Long resourceHash;
	private String url;
	private Set<AccountInfo> accounts = new HashSet<AccountInfo>();

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

	public Set<AccountInfo> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<AccountInfo> accounts) {
		this.accounts = accounts;
	}

	public Long getResourceHash() {
		return resourceHash;
	}

	public void setResourceHash(Long resourceHash) {
		this.resourceHash = resourceHash;
	}
}
