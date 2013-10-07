package net.thumbtack.updateNotifierBackend.database.entities;

import java.io.Serializable;

public class Page implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1296549796309152282L;

	private long id;
	private String page;
	
	public Page(String page) {
		super();
		id = 0;
		this.page = page;
	}

	public Page(long id, String page) {
		this(page);
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	
	

}
