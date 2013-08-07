package net.thumbtack.updateNotifierBackend.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.updateChecker.CheckForUpdate;


public class ResourcesChangesListener {
	
	private static final Logger log = LoggerFactory.getLogger(ResourcesChangesListener.class);
	
	/**
	 * recompute hash for new resource
	 * @param resource
	 */
	public void onAddResource(Resource resource) {
		log.trace("On add: URL = " + resource.getUrl());
		renewResource(resource);
	}
	
	/**
	 * Recompute hash if resource.url was changed
	 * @param resource
	 */
	public void onEditResourceUrl(Resource resource) {
		log.trace("On add: URL = " + resource.getUrl());
		renewResource(resource);
	}

	private void renewResource(Resource resource) {
		Integer newHashCode = CheckForUpdate.getNewHashCode(resource);
		if(newHashCode == null) {
			newHashCode = 0;
		}
		resource.setHash(newHashCode);
	}
}
