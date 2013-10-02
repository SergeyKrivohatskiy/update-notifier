package net.thumbtack.updateNotifierBackend.updateChecker;

import static net.thumbtack.updateNotifierBackend.util.IDN.ChangeToPunycode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.thumbtack.updateNotifierBackend.database.entities.Filter;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseSeriousException;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;

/**
 * @author Sergey Krivohatskiy, Dmitry Kamorin
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

	public static boolean setResourceMask(Resource resource) {
		log.debug("Try to get resource info for update checking: {}", resource);
		boolean result = false;
		try {
			Response response = Jsoup
					.connect(ChangeToPunycode(resource.getUrl()))
					.timeout(TIMEOUT).execute();
			log.debug("Response received: {}", resource);
			setLastModified(resource, response);
			Element body2 = Jsoup.connect(ChangeToPunycode(resource.getUrl()))
					.timeout(TIMEOUT).execute().parse().body();

			Element body1 = response.parse().body();
			clearPage(body1);
			clearPage(body2);
			String filter = resource.getFilter();
			// try {
			// body1.select(filter);
			// body2.select(filter);
			// } catch (Throwable e) {
			// log.debug("Selection error, filter ignored");
			// body1.outerHtml().hashCode();
			// }
			List<Filter> filters = new LinkedList<Filter>();
			filterDifferences(body1, body2, filters);
			resource.setHash(getHashCode(body1, filters, filter));
			resource.setFilters(filters);
			log.debug("Info received successfully");
			result = true;
		} catch (IOException e) {
			log.debug("Exception on executing request: {}", e);
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

	private static int getHashCode(Element body, List<Filter> filters,
			String filter) {
		if (filter == null) {
			return body.outerHtml().hashCode();
		}
		try {
			Elements forRemoving = new Elements();
			for (Filter f : filters) {
				Element elem = body;
				String[] indexes = f.getPath().split("/");
				try {
					for (String index : indexes) {
						int i = Integer.parseInt(index);
						elem = elem.child(i);
					}
					if (f.getAttrs().isEmpty()) {
						forRemoving.add(elem);
					} else {
						for (String a : f.getAttrs()) {
							elem.removeAttr(a);
						}
					}
				} catch (IndexOutOfBoundsException e) {
					System.err.println("Oops");
				}
			}
			for(Element elem : forRemoving){
				elem.remove();
			}

			try {
				FileWriter fw = new FileWriter(new File(
						"D:\\update-notifier-web\\page_"
								+ body.outerHtml().hashCode() + ".txt"));
				fw.write(body.outerHtml());
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// http://jsoup.org/apidocs/org/jsoup/select/Selector.html
			return body.select(filter).outerHtml().hashCode();
		} catch (Throwable e) {
			log.debug("Selection error, filter ignored");
			return body.outerHtml().hashCode();
		}
	}

	private static void clearPage(Element body) {
		if (body == null) {
			return;
		}
		try {
			Elements scripts = body.select("script");
			for (Element script : scripts) {
				script.remove();
			}
		} catch (Throwable e) {
			return;
		}
	}

	/**
	 * Find and mark differences. If elements have different tags, it will be
	 * filtered. Otherwise, if elements have different attributes, it will be
	 * filtered. Otherwise, if its children have some differences, it will be
	 * filtered.
	 * 
	 * @param elem1
	 * @param elem2
	 */
	private static void filterDifferences(Element elem1, Element elem2,
			List<Filter> filters) {
		Filter filter = new Filter();
		if (elem1.outerHtml().hashCode() != elem2.outerHtml().hashCode()) {
			// // Will be true if something changed in element attrs or its
			// // children
			// boolean changes = false;
			filter.setPath(buildPath(elem1));
			if (!elem1.tag().getName().equals(elem2.tag().getName())) {
				// elem1.remove();
				// elem2.remove();
				return;
			}
			if((elem1.tagName().equals("a"))&&(elem1.parent().tagName().equals("li"))) {
				System.out.println();
			}
			Set<String> attributes = filterAttributes(elem1.attributes(),
					elem2.attributes());
			if ((attributes != null) && (!attributes.isEmpty())) {
				// changes = true;
				filter.setAttrs(attributes);
				filters.add(filter);
			}
			// changes = changes
			// || filterChildren(elem1.children(), elem2.children(), filters);
			int size = filters.size();
			filterChildren(elem1.children(), elem2.children(), filters);
			if ((size == filters.size()) && (!filters.contains(filter))) {
				filters.add(filter);
			}
		}
	}

	private static boolean filterChildren(Elements children1,
			Elements children2, List<Filter> filters) {
		if (children1.outerHtml().hashCode() != children2.outerHtml()
				.hashCode()) {
			Element child1;
			Element child2;
			// TODO what to do if children lists have different size (can't
			// invent example)
			for (int i = 0; i < children1.size(); i++) {
				child1 = children1.get(i);
				child2 = children2.get(i);
				filterDifferences(child1, child2, filters);
			}
			return true;
		}
		return false;
	}

	/**
	 * Compares <code>attrs1</code> and <code>attrs2</code>, find different
	 * attributes and add them to result set
	 * 
	 * @param attrs1
	 * @param attrs2
	 * @return set of the different attributes
	 */
	private static Set<String> filterAttributes(Attributes attrs1,
			Attributes attrs2) {
		Set<String> differentAttrs = new HashSet<String>(
				(int) (attrs1.size() / 0.75));
		if (attrs1.hashCode() != attrs2.hashCode()) {
			// Set<String> commonAttrs = new HashSet<String>(
			// (int) (attrs1.size() / 0.75));
			for (Attribute attr1 : attrs1) {
				String attr1Key = attr1.getKey();
				String attr2Value = attrs2.get(attr1Key);
				if ("".equals(attr2Value)) {
					// attrs1.remove(attr1Key);
					differentAttrs.add(attr1Key);
				} else {
					// if (attr1.getValue().equals(attr2Value)) {
					// commonAttrs.add(attr1Key);
					// }
					if (!attr1.getValue().equals(attr2Value)) {
						differentAttrs.add(attr1Key);
					}
				}
			}
			// for (Attribute attr2 : attrs2) {
			// if (!commonAttrs.contains(attr2.getKey())) {
			// attrs2.remove(attr2.getKey());
			// }
			// }
		}
		return differentAttrs;
	}

	private static String buildPath(Element elem1) {
		// TODO If there is some elements with the same tag in the parent?
		Element parent = elem1;
		// StringBuilder path = new StringBuilder(elem1.tagName());
		StringBuilder path = new StringBuilder();
//		StringBuilder path = new StringBuilder(String.valueOf(parent
//				.elementSiblingIndex()));

		while (!"body".equals(parent.tagName())) {
			// path.append("/").append(parent.tagName());
			path.insert(0, "/").insert(0, parent.elementSiblingIndex());
			parent = parent.parent();
		}
//		path.reverse();

		return path.toString();

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
					Document document = response.parse();
					String filter = resource.getFilter();
					List<Filter> filters = resource.getFilters();
					clearPage(document.body());
					newHashCode = getHashCode(document.body(), filters, filter);
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

	public void run() {
		if (wasResourceUpdated()) {
			UpdateNotifierBackend.getResourcesUpdateListener()
					.onResourceUpdate(resource);
		}
	}

}
