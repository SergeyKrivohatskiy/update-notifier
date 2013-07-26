package main.java.net.thumbtack.updateNotifierBackend.IOService;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import main.java.net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;
import main.java.net.thumbtack.updateNotifierBackend.accountManagment.AccountManager;


public class RESTfulService implements HttpRequestHandler {
	
	private AccountManager accountManager;
	private DatabaseService databaseService;
	
	public RESTfulService(AccountManager accountManager, DatabaseService databaseService) {
		this.accountManager = accountManager;
		this.databaseService = databaseService;
	}
	
	private void singnIn() {
		
	}
	
	private String getResourcesList(String request) {
		return "getResourcesList";
	}
	
	private String editResource(String request) {
		return "editResource";
	}
	
	private String checkForUpdates(String request) {
		return "checkForUpdates";
	}

	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		response.addHeader("MY_HEADER", "UPDATE_NOTIFIER");
	}
}
