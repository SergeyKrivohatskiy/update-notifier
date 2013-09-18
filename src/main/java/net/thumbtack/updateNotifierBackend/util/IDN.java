package net.thumbtack.updateNotifierBackend.util;

import gnu.inet.encoding.IDNAException;

import java.net.MalformedURLException;
import java.net.URL;

public class IDN {
	/**
	 * Jsoup throws UnknownHostException if domain name(?) contains UTF-8, this
	 * method convert url to acceptable for Jsoup
	 * 
	 * @param urlString
	 * @return
	 */
	public static String ChangeToPunycode(String urlString) {
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
}
