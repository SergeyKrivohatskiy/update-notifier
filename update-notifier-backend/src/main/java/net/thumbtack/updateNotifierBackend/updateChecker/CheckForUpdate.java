package net.thumbtack.updateNotifierBackend.updateChecker;


import java.io.IOException;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;

public class CheckForUpdate implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(CheckForUpdate.class);
	private Resource resource;
	
	public CheckForUpdate(Resource resource) {
		this.resource = resource;
	}

	public void run() {
		if(isResourceWasUpdated()) {
			UpdateNotifierBackend.getResourcesUpdateListener().
				onResourceUpdate(resource);
		}
	}
	
	private boolean isResourceWasUpdated() {
		log.debug("CheckForUpdate URL = \"" + resource.getUrl() + "\"");
		Integer newHashCode;
		try {
			newHashCode = getNewHashCode(resource);
		} catch (Exception e) {
			log.error("Jsoup connect to resource failed. Resource marked as not updated", e);
			return false;
		}
		if(!newHashCode.equals(resource.getHash())) {
			log.debug("New HashCode = " + newHashCode);
			resource.setHash(newHashCode);
			UpdateNotifierBackend.getDatabaseService().updateResourceHash(resource.getId(), newHashCode);
			return true;
		}
		return false;
	}

	public static Integer getNewHashCode(Resource resource) throws IOException {
		Document document;
		document = Jsoup.connect(resource.getUrl()).get();
		Integer newHashCode = document.html().hashCode();
		return newHashCode;
	}

}
