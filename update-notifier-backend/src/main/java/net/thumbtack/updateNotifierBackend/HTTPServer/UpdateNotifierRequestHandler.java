package main.java.net.thumbtack.updateNotifierBackend.HTTPServer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.google.gson.Gson;

import main.java.net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;
import main.java.net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;

public class UpdateNotifierRequestHandler implements HttpRequestHandler {
	
	private static final String REQUEST_SIGN_IN = "/signin?";
	private static final String REQUEST_CHECK_UPDATES = "/checkupdates?";
	private static final String REQUEST_RESOURCE = "/resources?";
	private DatabaseService databaseService;
	private static final MessageFormat userIdParam = 
			new MessageFormat("userid={0,number}");
	private static final MessageFormat tagParam = 
			new MessageFormat("tag={0}");
	private static final MessageFormat emailParam = 
			new MessageFormat("email={0}");
	
	public UpdateNotifierRequestHandler(DatabaseService databaseService) {
		this.databaseService = databaseService;
	}
	
	private void singnIn(HttpResponse response, String request) {
		try {
			String email = (String) emailParam.parse(request)[0];
			Long id = databaseService.getAccountIdByEmail(email);
			response.setEntity(new StringEntity(id.toString()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			return;
		}
	}
	
	private void getResourcesList(HttpResponse response, String request) {
		Long userId = null;
		String[] tags = null;
		String[] stringParams = request.split("&");
		try {
			userId = (Long) userIdParam.parse(stringParams[0])[0];
			if(stringParams.length > 1) {
				tags = new String[stringParams.length - 1];
				for(int i = 1; i < stringParams.length; i +=1) {
					tags[i - 1] = (String) tagParam.parse(stringParams[i])[0];
				}
			}
		} catch (ParseException e1) {
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			return;
		}
		
		Set<ResourceInfo> userResources = databaseService.getResourcesByIdAndTags(userId, tags);
		if(userResources == null) {
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			return;
		}
		try {
			StringEntity entity = new StringEntity(new Gson().toJson(userResources));
			entity.setContentType(ContentType.APPLICATION_JSON.toString());
			response.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			//ignore
		}
		response.setStatusCode(HttpStatus.SC_OK);
	}
	
	private void editResource(HttpResponse response, String paramsString, HttpRequest request) {

		Long userId = null;
		StringBuilder resourceJson = new StringBuilder();
		try{
			userId = (Long) userIdParam.parse(paramsString)[0];
			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			if(!ContentType.get(entity).equals(ContentType.APPLICATION_JSON)) {
				response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
				return;
			}
			Scanner sc = new Scanner(entity.getContent());
			while(sc.hasNext()) {
				resourceJson.append(sc.next());
			}
			
			ResourceInfo resourceInfo = new Gson().fromJson(resourceJson.toString(), ResourceInfo.class);
			
			if(resourceInfo == null || !databaseService.appendResource(userId, resourceInfo)) {
				response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
				return;
			}
			
		} catch(Exception ex) {
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			return;
		}
		response.setStatusCode(HttpStatus.SC_OK);
	}
	
	private void checkForUpdates(HttpResponse response, String request) {
		
	}

	private void deleteResource(HttpResponse response, String request) {
		long resourceId = 0;
		try{
			resourceId = Long.parseLong(request);
		} catch(NumberFormatException ex) {
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
			return;
		}
		if(databaseService.deleteResource(resourceId)) {
			response.setStatusCode(HttpStatus.SC_OK);
		} else {
			response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		}
		return;
	}

	/**
	 * Process request.
	 * Supported methods:
	 * <a>GET /signin?email=example@gmail.com => userid
	 * GET /resources?userid=1(&tag=tag1&tag=tag2) => Resource.json[]
	 * POST /resources?userid=1 + Resource.json => adding/editing resource
	 * DELETE /resources?userid=1&resourceurl=example.com => delete resource
	 * DELETE /resources?userid=1(&tag=tag1&tag=tag2) => delete resources
	 * GET /tags?userid=1 => tag.json[]
	 */
	public void handle(HttpRequest request, HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		String uri = request.getRequestLine().getUri();
		if (method.equals("GET")) {
            processGetRequest(response, uri);
            return;
        }
		if (method.equals("DELETE")) {
			processDeleteRequest(response, uri);
			return;
        }
		if (method.equals("POST")) {
			processPostRequest(response, uri, request);
			return;
        }

		throw new MethodNotSupportedException(method + " method not supported");
	}

	private void processGetRequest(HttpResponse response, String uri) {
		if(uri.startsWith(REQUEST_SIGN_IN)) {
			singnIn(response, uri.substring(REQUEST_SIGN_IN.length()));
		    return;
		}
		if(uri.startsWith(REQUEST_RESOURCE)) {
			getResourcesList(response, uri.substring(REQUEST_RESOURCE.length()));
		    return;
		}
		if(uri.startsWith(REQUEST_CHECK_UPDATES)) {
			checkForUpdates(response, uri.substring(REQUEST_CHECK_UPDATES.length()));
		    return;
		}
		response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
	}

	private void processPostRequest(HttpResponse response, String uri, HttpRequest request) {
		if(uri.startsWith(REQUEST_RESOURCE)) {
			editResource(response, uri.substring(REQUEST_RESOURCE.length()), request);
		    return;
		}
		response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
	}

	private void processDeleteRequest(HttpResponse response, String uri) {
		if(uri.startsWith(REQUEST_RESOURCE)) {
			deleteResource(response, uri.substring(REQUEST_RESOURCE.length()));
		    return;
		}
		response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
	}
}
