package net.thumbtack.updateNotifierBackend.database.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class Filter implements Serializable {

	private static final long serialVersionUID = -4459289876319834920L;
	
	private Long id;
	private Long resourceId;
	private String path;
	private Set<String> attrs;
	
	public Filter() {
		super();
		attrs = Collections.emptySet();
	}

//	public Filter(Long id, Long resourceId, String path, String attrs) {
//		super();
//		this.id = id;
//		this.resourceId = resourceId;
//		this.path = path;
//		this.attrs = attrs;
//	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Set<String> getAttrs() {
		return attrs;
	}

	public void setAttrs(Set<String> attributes) {
		this.attrs = attributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attrs == null) ? 0 : attrs.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
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
		Filter other = (Filter) obj;
		if (attrs == null) {
			if (other.attrs != null)
				return false;
		} else if (!attrs.equals(other.attrs))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		return true;
	}
	
	

}
