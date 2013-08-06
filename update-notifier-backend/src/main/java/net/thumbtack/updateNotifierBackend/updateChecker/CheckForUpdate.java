package net.thumbtack.updateNotifierBackend.updateChecker;



import net.thumbtack.updateNotifierBackend.database.entities.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
		newHashCode = getNewHashCode(resource);
		if(newHashCode == null) {
			log.debug("getNewHashCode failed");
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

	/**
	 * @param resource
	 * @return Hash code of specified HTML element. Or null if 
	 * Jsoup.connect failed or checkingParam is incorrect.
	 */
	public static Integer getNewHashCode(Resource resource) {
		try {
			Document document;
			document = Jsoup.connect(resource.getUrl()).get();
			String domPathString = resource.getDomPath();
			String filter = resource.getDomPath();
			
			String[] domPath = domPathString.split("/");
			Element targetElement = document.body();
			
			for(int i = 1; i < domPath.length; i += 1) {
				targetElement = targetElement.child(Integer.parseInt(domPath[i]));
			}
			log.debug(applyFilter(targetElement, filter));
			return targetElement.html().hashCode();
		} catch (Throwable e) {
			// May be NullPtrEx, NumberFormatException, 
			// IOex or other Jsoup.connect exceptions
			return null;
		}
	}

	private static String applyFilter(Element targetElement, String filter) {
		// TODO Write this method
		return targetElement.html();
	}

}
