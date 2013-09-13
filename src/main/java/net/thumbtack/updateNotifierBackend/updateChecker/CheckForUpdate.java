package net.thumbtack.updateNotifierBackend.updateChecker;

import gnu.inet.encoding.IDNAException;

import java.net.MalformedURLException;
import java.net.URL;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseSeriousException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;

/**
 * @author Sergey Krivohatskiy
 * 
 *         This class checks specified resource for update.
 */
public class CheckForUpdate implements Runnable {

	private static final Logger log = LoggerFactory
			.getLogger(CheckForUpdate.class);
	private static final int TIMEOUT = 3000;
	private Resource resource;

	/**
	 * Jsoup throws UnknownHostException if domain name(?) contains UTF-8, this
	 * method convert url to acceptable for Jsoup
	 * 
	 * @param urlString
	 * @return
	 */
	private static String ChangeToPunycode(String urlString) {
		URL url;
		try {
			url = new URL(urlString);
			String oldHost = url.getHost();
			String newHost = gnu.inet.encoding.IDNA.toASCII(oldHost);
			urlString = urlString.replaceFirst(oldHost, newHost);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IDNAException e) {
			e.printStackTrace();
		}
		return urlString;
	}

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
		int newHashCode;
		newHashCode = getNewHashCode(resource);
		boolean result = false;
		if (newHashCode == 0) {
			log.debug("getNewHashCode failed");
		} else if (newHashCode != resource.getHash()) {
			log.debug("New HashCode = " + newHashCode);
//			boolean hashWasNull = resource.getHash() == 0;
//			resource.setHash(newHashCode);
			try {
				UpdateNotifierBackend.getDatabaseService().updateResourceHash(
						resource.getId(), newHashCode);
			} catch (DatabaseSeriousException e) {
			} // Ignore if resource is not exist
			result = true;
		} else {
			log.debug("Resource hash is the same");
		}
		return result;
	}

	/**
	 * @param resource
	 *            Resource to check
	 * @return Hash code of specified HTML element with specified filter. Or
	 *         null if Jsoup.connect failed or checking parameters is incorrect.
	 */
	public static int getNewHashCode(Resource resource) {
		try {
			Document document;

			document = Jsoup.parse(
					new URL(ChangeToPunycode(resource.getUrl())), TIMEOUT);
			String filter = resource.getFilter();
			return applyFilter(document.body(), filter).hashCode();
		} catch (Throwable e) {
			// May be NullPtrEx, NumberFormatException,
			// IndexOutOfBoundsException,
			// IOex or other Jsoup exceptions
			log.debug(e.toString());
			return 0;
		}
	}

	private static String applyFilter(Element element, String filter) {
		if (filter == null) {
			return element.text();
		}
		try {
			// http://jsoup.org/apidocs/org/jsoup/select/Selector.html
			return element.select(filter).text();
		} catch (Throwable e) {
			log.debug("Selection error, filter ignored");
			return element.text();
		}
	}

}
