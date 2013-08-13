package net.thumbtack.updateNotifierBackend.database.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Resource implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private transient Long userId;

	private String url;
	private byte scheduleCode;
	private transient int hash;
	private String domPath;
	private String filter;
	private List<Long> tags = Collections.emptyList();
	
	public Resource() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte getSheduleCode() {
		return scheduleCode;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<Long> getTags() {
		return tags;
	}


	public void setTags(List<Long> list) {
		this.tags = list;
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

	public String getDomPath() {
		return domPath;
	}

	public void setDomPath(String domPath) {
		this.domPath = domPath;
	}
	
	public String getFilter() {
		return filter;
	}

	@Override
	public String toString() {
		return "Resource [id=" + id + ", userId=" + userId + ", url=" + url
				+ ", scheduleCode=" + scheduleCode + ", domPath=" + domPath
				+ ", filter=" + filter + ", tags=" + tags + "]";
	}

}
