package net.thumbtack.updateNotifierBackend.updateChecker;

import static net.thumbtack.updateNotifierBackend.util.IDN.ChangeToPunycode;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseSeriousException;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
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
public class ResourceInvestigator implements Runnable {

	private static final String PATTERN_RFC2822 = "EEE, dd MMM yyyy HH:mm:ss z";
	private static final String IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final Logger log = LoggerFactory
			.getLogger(ResourceInvestigator.class);
	private static final int TIMEOUT = 5000;
	private static final String LAST_MODIFIED = "Last-Modified";
	private Resource resource;

	public ResourceInvestigator(Resource resource) {
		this.resource = resource;
	}

	public void run() {
		if (wasResourceUpdated()) {
			UpdateNotifierBackend.getResourcesUpdateListener()
					.onResourceUpdate(resource);
		}
	}

	public static boolean setResourceMask(Resource resource) {
		log.debug("Try to get resource info for update checking: {}", resource);
		boolean result = false;
		try {
			Response response = Jsoup
					.connect(ChangeToPunycode(resource.getUrl()))
					.timeout(TIMEOUT).execute();
			log.debug("Response received: {}", resource);
			setLastModified(resource, response);
			// Now set hash trace
			Document document = response.parse();
			String filter = resource.getFilter();
			resource.setHash(getFilteredPage(document.body(), filter)
					.hashCode());
			log.debug("Info received successfully");
			result = true;
		} catch (IOException e) {
			log.debug("Exception on executing reqest: {}", e);
		}
		return result;
	}

	private static void setLastModified(Resource resource, Response response) {
		String header = response.header(LAST_MODIFIED);
		if (header != null) {
			log.debug("Last-Modified header was found");
			try {
				Date date = DateUtils.parseDate(header);
				log.debug("Header date parsed");
				resource.setLastModified(date);
			} catch (DateParseException e) {
				log.debug("Last-Modified date parsing failed because {}", e);
			}
		} else {
			log.debug("Last-Modified header wasn't found");
		}
	}

	private boolean wasResourceUpdated() {
		log.debug("Check if resource {} is updated", resource);
		int newHashCode = 0;
		boolean isUpdated = false;
		try {
			Connection connection = Jsoup.connect(
					ChangeToPunycode(resource.getUrl())).timeout(TIMEOUT);
			if (resource.getLastModified() != null) {
				connection.header(IF_MODIFIED_SINCE, DateUtils.formatDate(
						resource.getLastModified(), PATTERN_RFC2822));
			}
			Response response = connection.ignoreHttpErrors(true).execute();
			if (response.statusCode() != 304) {
				if (response.statusCode() != 200) {
					log.debug("Bad response HTTP status code: {}", response);
				} else {
					setLastModified(resource, response);
					// TODO Try another methods
					Document document = response.parse();
					String filter = resource.getFilter();
					newHashCode = getFilteredPage(document.body(), filter)
							.hashCode();
					if (newHashCode == 0) {
						log.debug("getNewHashCode failed");
					} else if (newHashCode != resource.getHash()) {
						log.debug("New HashCode = " + newHashCode);
						try {
							resource.setHash(newHashCode);
							UpdateNotifierBackend.getDatabaseService()
									.updateResourceHash(resource);
						} catch (DatabaseSeriousException e) {
						} // Ignore if resource is not exist
						isUpdated = true;
					} else {
						log.debug("Resource hash is the same");

					}
				}
			} else {
				log.debug("HTTP status code 304: resource wasn't modified");
			}
		} catch (IOException e) {
			log.debug("Can't check update: exception on executing reqest: {}",
					e);
		}
		return isUpdated;
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
			return getFilteredPage(document.body(), filter).hashCode();
		} catch (Throwable e) {
			// May be NullPtrEx, NumberFormatException,
			// IndexOutOfBoundsException,
			// IOex or other Jsoup exceptions
			log.debug(e.toString());
			return 0;
		}
	}

	private static String getFilteredPage(Element element, String filter) {
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
