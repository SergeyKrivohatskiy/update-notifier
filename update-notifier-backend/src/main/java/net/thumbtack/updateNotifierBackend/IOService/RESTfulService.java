package main.java.net.thumbtack.updateNotifierBackend.IOService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.json.JSONObject;

import main.java.net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;
import main.java.net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;
import main.java.net.thumbtack.updateNotifierBackend.accountManagment.AccountManager;


public class RESTfulService implements HttpRequestHandler {
	
	private static final String REQUEST_RESOURCES_LIST = "/resourceslist?";
	private static final String REQUEST_SIGN_IN = "/signin?";
	private AccountManager accountManager;
	private DatabaseService databaseService;
	
	public RESTfulService(AccountManager accountManager, DatabaseService databaseService) {
		this.accountManager = accountManager;
		this.databaseService = databaseService;
	}
	
	private void singnIn(HttpResponse response, String params) {
		try {
			Long id = databaseService.getAccountIdByEmail(params);
			response.setEntity(new StringEntity(id.toString()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void getResourcesList(HttpResponse response, String request) {
		long userId = 0;
		try{
			userId = Long.parseLong(request);
		} catch(NumberFormatException ex) {
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			return;
		}
		List<ResourceInfo> userResources = databaseService.getResourcesInfoByAccountId(userId);
		if(userResources == null) {
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			return;
		}
		JSONObject resourcesList = new JSONObject(userResources);
	}
	
	private void editResource(String request) {
		
	}
	
	private void checkForUpdates(String request) {
		
	}

	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		String uri = request.getRequestLine().getUri();
		if (method.equals("GET")) {
            if(uri.startsWith(REQUEST_SIGN_IN)) {
            	singnIn(response, uri.substring(REQUEST_SIGN_IN.length()));
                return;
            }
            if(uri.startsWith(REQUEST_RESOURCES_LIST)) {
            	getResourcesList(response, uri.substring(REQUEST_RESOURCES_LIST.length()));
                return;
            }
            throw new MethodNotSupportedException(method + uri + " method not supported");
        }
		if (method.equals("DELETE")) {
			
            throw new MethodNotSupportedException(method + uri + " method not supported");
        }
		if (method.equals("POST")) {

            throw new MethodNotSupportedException(method + uri + " method not supported");
        }

		throw new MethodNotSupportedException(method + " method not supported");
	}
}
