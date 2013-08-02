package net.thumbtack.updateNotifierBackend.updateChecker;


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
		if(isResourceWasUpdated(resource)) {
			UpdateNotifierBackend.getResourcesUpdateListener().
				onResourceUpdate(resource);
		}
	}

	public static boolean isResourceWasUpdated(Resource resource) {
		log.debug("CheckForUpdate URL = \"" + resource.getUrl() + "\"");
		Document document;
		try {
			document = Jsoup.connect(resource.getUrl()).get();
		} catch (Exception e) {
			log.error("Jsoup connect to resource failed. Resource marked as not updated", e);
			return false;
		}
		Integer newHashCode = document.html().hashCode();
		if(!newHashCode.equals(resource.getHash())) {
			resource.setHash(newHashCode);
			UpdateNotifierBackend.getDatabaseService().updateResourceHash(resource.getId(), newHashCode);
			return true;
		}
		return false;
	}

}
