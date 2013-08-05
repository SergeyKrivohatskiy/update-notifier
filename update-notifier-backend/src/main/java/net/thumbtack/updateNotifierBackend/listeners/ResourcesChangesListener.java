package net.thumbtack.updateNotifierBackend.listeners;

import java.io.IOException;

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
		// recompute hash for new resource
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
		try {
			Integer newHashCode = CheckForUpdate.getNewHashCode(resource);
			log.trace("New hash = " + newHashCode);
			resource.setHash(newHashCode);
		} catch (IOException e) {} // Ignore
	}
	
}
