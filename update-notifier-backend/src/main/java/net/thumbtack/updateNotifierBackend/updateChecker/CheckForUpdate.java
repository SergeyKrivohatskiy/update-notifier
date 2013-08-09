package net.thumbtack.updateNotifierBackend.updateChecker;

import java.net.URL;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
/**
 * @author Sergey Krivohatskiy
 * 
 * This class checks specified resource for update.
 */
public class CheckForUpdate implements Runnable {

	private static final Logger log = LoggerFactory
			.getLogger(CheckForUpdate.class);
	private static final int TIMEOUT = 1000;
	private Resource resource;

	public CheckForUpdate(Resource resource) {
		this.resource = resource;
	}

	public void run() {
		if (isResourceWasUpdated()) {
			UpdateNotifierBackend.getResourcesUpdateListener()
					.onResourceUpdate(resource);
		}
	}

	private boolean isResourceWasUpdated() {
		log.debug("CheckForUpdate URL = \"" + resource.getUrl() + "\"");
		Integer newHashCode;
		newHashCode = getNewHashCode(resource);
		boolean result = false;
		if (newHashCode == null) {
			log.debug("getNewHashCode failed");
		} else if (!newHashCode.equals(resource.getHash())) {
			log.debug("New HashCode = " + newHashCode);
			resource.setHash(newHashCode);
			UpdateNotifierBackend.getDatabaseService().updateResourceHash(
					resource.getId(), newHashCode);
			result = true;
		}
		return result;
	}

	/**
	 * @param resource Resource to check
	 * @return Hash code of specified HTML element with specified filter.
	 *  Or null if Jsoup.connect failed or checking parameters is incorrect.
	 */
	public static Integer getNewHashCode(Resource resource) {
		try {
			Document document;
			
			document = Jsoup.parse(new URL(resource.getUrl()), TIMEOUT);
			String domPathString = resource.getDomPath();
			String filter = resource.getFilter();
			if(domPathString.startsWith("/")) {
				domPathString = domPathString.substring(1);
			} else {
				log.debug("incorrect dom path " + domPathString);
				return null;
			}
			String[] domPath = domPathString.split("/");
			Element targetElement = document.body();
			if(!domPathString.equals("")) {
				for(int i = 0; i < domPath.length; i += 1) {
					targetElement = targetElement.child(Integer.parseInt(domPath[i]));
				}
			}
			return applyFilter(targetElement, filter).hashCode();
		} catch (Throwable e) {
			// May be NullPtrEx, NumberFormatException, IndexOutOfBoundsException,
			// IOex or other Jsoup exceptions
			log.debug(e.toString());
			return null;
		}
	}

	private static String applyFilter(Element element, String filter) {
		if (filter == null) {
			return element.text();
		}
		try{
			// http://jsoup.org/apidocs/org/jsoup/select/Selector.html
			return element.select(filter).text();
		} catch(Throwable e) {
			log.debug("Selection error, filter ignored");
			return element.text();
		}
	}

}
