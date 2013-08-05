package net.thumbtack.updateNotifierBackend.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;

public class ResourcesUpdateListener {
	
	private static final Logger log = LoggerFactory.getLogger(ResourcesUpdateListener.class);

	public void onResourceUpdate(Resource resource) {
		log.debug("Resource updated URL =  " + resource.getUrl());
	}
	
}
