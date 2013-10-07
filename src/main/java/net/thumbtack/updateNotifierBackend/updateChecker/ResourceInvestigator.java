package net.thumbtack.updateNotifierBackend.updateChecker;

import static net.thumbtack.updateNotifierBackend.util.IDN.ChangeToPunycode;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.thumbtack.updateNotifierBackend.database.entities.Filter;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.exceptions.DatabaseSeriousException;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
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

	/**
	 * Get some resource info for update checking
	 * 
	 * @param resource
	 * @return
	 */
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
			cleanPage(body1);
			cleanPage(body2);
			String filter = resource.getFilter();
			List<Filter> filters = new LinkedList<Filter>();
			log.debug("Try to filter differences");
			filterDifferences(body1, body2, filters);
			log.debug("Differences filtered");
			applyFilters(body1, filters);
			resource.setPage(body1.outerHtml());
			String pageText = getPageText(body1, filter);
			// savePage(resource.getId(), body1);

			resource.setHash(pageText.hashCode());
			resource.setFilters(filters);
			log.debug("Info received successfully");
			result = true;
		} catch (IOException e) {
			log.debug("Exception on executing request: {}", e);
		}
		return result;
	}

	// /**
	// * Save page to the file storage. Page name will be 'page_'+ resource id,
	// * which links to this page
	// *
	// * @param id
	// * resource
	// * @param body
	// * @return
	// */
	// private static boolean savePage(long id, Element body) {
	// log.debug("Try to save page to the file storage; resource id: {}", id);
	// FileWriter writer = null;
	// try {
	// writer = new FileWriter(new File(System.getProperty("user.dir")
	// + "\\page_" + id + ".txt"));
	// writer.write(body.outerHtml());
	// writer.close();
	// log.debug("Page saved");
	// return true;
	// } catch (IOException e) {
	// log.debug("Can't save page, stack trace: {}", e);
	// } finally {
	// if (writer != null) {
	// try {
	// writer.close();
	// } catch (IOException e) {
	// }
	// }
	// }
	// return false;
	// }

	/**
	 * Find Last-Modified header in the response and set it to resource, if
	 * header exists.
	 * 
	 * @param resource
	 *            resource to set header
	 * @param response
	 *            response for searching header
	 */
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

	/**
	 * Filter 'body' tag from <code>filters<code> and select necessary 
	 * element(-s) by <code>filter<code>
	 * 
	 * @param body
	 *            body element, containing page html code
	 * @param filters
	 *            list of filters (note, that order is important, so list is
	 *            used)
	 * @param filter
	 *            some filter for selecting necessary element(-s)
	 * @return
	 */
	private static String getPageText(Element body, String filter) {
		log.debug("Try to get filtered page");
		if (filter == null) {
			log.debug("Filter is null, nothing to do more");
			return body.text();
		}
		try {
			// log.debug("Successfully, return page");
			// http://jsoup.org/apidocs/org/jsoup/select/Selector.html
			return body.select(filter).text();
		} catch (Throwable e) {
			log.debug("Selection error, filter ignored");
			return body.text();
		}
	}

	private static void applyFilters(Element body, List<Filter> filters) {
		Elements forRemoving = collectElements(body, filters);
		for (Element elem : forRemoving) {
			for (TextNode node : elem.textNodes()) {
				node.remove();
			}
			// elem.remove();
		}
	}

	private static List<String> collectDifferences(Element body,
			List<Filter> filters) {
		Elements elements = collectElements(body, filters);
		LinkedList<String> list = new LinkedList<String>();
		for (Element element : elements) {
			list.add(element.ownText());
		}
		return list;
	}

	private static Elements collectElements(Element body, List<Filter> filters) {
		Elements elements = new Elements();
		for (Filter f : filters) {
			Element elem = body;
			String[] indexes = f.getPath().split("/");
			try {
				for (String index : indexes) {
					int i = Integer.parseInt(index);
					elem = elem.child(i);
				}
				// if (f.getAttrs().isEmpty()) {
				elements.add(elem);
				// } else {
				// for (String a : f.getAttrs()) {
				// elem.removeAttr(a);
				// }
				// }
			} catch (IndexOutOfBoundsException e) {
				log.debug("Bad filter: can't to find elem for filtering");
			}
		}
		return elements;
	}

	/**
	 * Clean element from different a priori useless tags
	 * 
	 * @param element
	 */
	private static void cleanPage(Element element) {

		if (element == null) {
			return;
		}
		try {
			Elements scripts = element.select("script");
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
		filter.setPath(buildPath(elem1));
		if (elem1.ownText().hashCode() != elem2.ownText().hashCode()) {
			filters.add(filter);
		}
		if (elem1.text().hashCode() != elem2.text().hashCode()) {
//			 if (elem1.children().text().hashCode() == elem2.children().text()
//			 .hashCode()) {
//			if (elem1.attr("id").equals("watch7-user-header")) {
//				System.out.println(elem1.text());
//				System.out.println(elem2.text());
//				System.out.println(elem1.children().text());
//				System.out.println(elem2.children().text());
//			}
			filterChildren(elem1.children(), elem2.children(), filters);
		}
	}

	private static boolean filterChildren(Elements children1,
			Elements children2, List<Filter> filters) {
		if (children1.text().hashCode() != children2.text().hashCode()
				&& children1.size() == children2.size()) {
			Element child1 = null;
			Element child2 = null;
			for (int i = 0; i < children1.size(); i++) {
				try {
					child1 = children1.get(i);
					child2 = children2.get(i);
				} catch (IndexOutOfBoundsException e) {
					System.out.println("It's impossible!");
				}
				filterDifferences(child1, child2, filters);
			}
			return true;
		}
		return false;
	}

	// /**
	// * Compares <code>attrs1</code> and <code>attrs2</code>, find different
	// * attributes and add them to result set
	// *
	// * @param attrs1
	// * @param attrs2
	// * @return set of the different attributes
	// */
	// private static Set<String> filterAttributes(Attributes attrs1,
	// Attributes attrs2) {
	// Set<String> differentAttrs = new HashSet<String>(
	// (int) (attrs1.size() / 0.75));
	// if (attrs1.hashCode() != attrs2.hashCode()) {
	// // Set<String> commonAttrs = new HashSet<String>(
	// // (int) (attrs1.size() / 0.75));
	// for (Attribute attr1 : attrs1) {
	// String attr1Key = attr1.getKey();
	// String attr2Value = attrs2.get(attr1Key);
	// if ("".equals(attr2Value)) {
	// // attrs1.remove(attr1Key);
	// differentAttrs.add(attr1Key);
	// } else {
	// // if (attr1.getValue().equals(attr2Value)) {
	// // commonAttrs.add(attr1Key);
	// // }
	// if (!attr1.getValue().equals(attr2Value)) {
	// differentAttrs.add(attr1Key);
	// }
	// }
	// }
	// // for (Attribute attr2 : attrs2) {
	// // if (!commonAttrs.contains(attr2.getKey())) {
	// // attrs2.remove(attr2.getKey());
	// // }
	// // }
	// }
	// return differentAttrs;
	// }

	private static String buildPath(Element elem1) {
		Element parent = elem1;
		StringBuilder path = new StringBuilder();

		while (!"body".equals(parent.tagName())) {
			path.insert(0, "/").insert(0, parent.elementSiblingIndex());
			parent = parent.parent();
		}

		return path.toString();

	}

	// private void findDifferences() {
	// long bodyId = resource.getBodyId();
	// UpdateNotifierBackend.getDatabaseService().getDivsByParent
	// }

	private List<String> wasResourceUpdated() {
		log.debug("Check if resource {} is updated", resource);
		int newHashCode = 0;
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
					String oldPageCode = UpdateNotifierBackend
							.getDatabaseService().getPage(resource.getPageId());
					Element oldPage = Jsoup.parse(oldPageCode).body();

					cleanPage(document.body());
					applyFilters(document.body(), filters);
					String newPageText = getPageText(document.body(), filter);
					newHashCode = newPageText.hashCode();
					if (newHashCode == 0) {
						log.debug("getNewHashCode failed");
					} else if (newHashCode != resource.getHash()) {
						log.debug("New HashCode = " + newHashCode);
						List<Filter> diffs = new LinkedList<Filter>();
						filterDifferences(document.body(), oldPage, diffs);
						if (diffs.isEmpty()) {
							log.debug("Resource hash is NOT the same, but differences are not found");
							return Collections.emptyList();
							// FileWriter fw11 = new FileWriter(new
							// File("1-code.txt"));
							// fw11.append(document.body().outerHtml());
							// fw11.close();
							// FileWriter fw12 = new FileWriter(new
							// File("1-text.txt"));
							// fw12.append(document.body().text());
							// fw12.close();
							// FileWriter fw21 = new FileWriter(new
							// File("2-code.txt"));
							// fw21.append(document.body().outerHtml());
							// fw21.close();
							// FileWriter fw22 = new FileWriter(new
							// File("2-text.txt"));
							// fw22.append(document.body().text());
							// fw22.close();
						}
						List<String> differences = collectDifferences(
								document.body(), diffs);
						try {
							resource.setHash(newHashCode);
							resource.setPage(document.body().outerHtml());
							UpdateNotifierBackend.getDatabaseService()
									.updateResourceHashAndTrace(resource);
						} catch (DatabaseSeriousException e) {
						} // Ignore if resource is not exist
						return differences;
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
		return Collections.emptyList();
	}

	public void run() {
		List<String> differences = wasResourceUpdated();
		if (!differences.isEmpty()) {
			UpdateNotifierBackend.getResourcesUpdateListener()
					.onResourceUpdate(resource, differences);
		}
	}

}
