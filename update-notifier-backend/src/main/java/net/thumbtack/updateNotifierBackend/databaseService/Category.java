package main.java.net.thumbtack.updateNotifierBackend.databaseService;

import java.util.HashSet;
import java.util.Set;

public class Category {

	private String name;
	private Set<ResourceInfo> resources = new HashSet<ResourceInfo>();
	private AccountInfo account;
	
	public Category() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<ResourceInfo> getResources() {
		return resources;
	}
	
	public void setResources(Set<ResourceInfo> resources) {
		this.resources = resources;
	}

	public AccountInfo getAccount() {
		return account;
	}

	public void setAccount(AccountInfo account) {
		this.account = account;
	}
	
	
}
