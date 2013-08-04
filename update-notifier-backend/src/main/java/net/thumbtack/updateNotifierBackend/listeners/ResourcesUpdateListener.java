package net.thumbtack.updateNotifierBackend.listeners;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.updateChecker.CheckForUpdate;

public class ResourcesUpdateListener {
	
	private static final Logger log = LoggerFactory.getLogger(ResourcesUpdateListener.class);

	public void onResourceUpdate(Resource resource) {
		log.debug("Resource updated URL =  " + resource.getUrl());
		// To set hash code
		try {
			resource.setHash(CheckForUpdate.getNewHashCode(resource));
		} catch (IOException e) {} // Ignore
	}
	
}
