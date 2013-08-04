package net.thumbtack.updateNotifierBackend.resourceHandlers;

import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;

@Path("/users")
@Singleton
public class UsersHandler {

	private static final Logger log = LoggerFactory.getLogger(UsersHandler.class);
	
	@Path("signin")
	@GET
	public long signIn(@QueryParam("email") String userEmail) {
		log.trace("Sign in: " + userEmail);
		Long userId = UpdateNotifierBackend.getDatabaseService()
				.getUserIdByEmail(userEmail);
		if (userId == null) {
			log.error("Database request failed.Sign in failed");
			throw (new WebApplicationException("Database get account error"));
		}
		return userId;
	}

	@Path("/{id}/resources")
	@GET
	@Produces({ "application/json" })
	public String getUserResources(@PathParam("id") long userId,
			@DefaultValue("") @QueryParam("tags") String tagsString) {
		// TODO process errors
		log.trace(userId + "/resourses?" + tagsString);
		Long[] tags = parseTags(tagsString);
		List<Resource> resources = UpdateNotifierBackend.getDatabaseService()
				.getResourcesByIdAndTags(userId, tags);
		if (resources == null) {
			log.debug("Database request failed. Get resources bad request");
			throw (new BadRequestException("Incorrect userId"));
		}
		return new Gson().toJson(resources);
	}

	@Path("/{id}/resources")
	@DELETE
	public void deleteUserResources(@PathParam("id") long userId,
			@DefaultValue("") @QueryParam("tags") String tagsString) {
		// TODO process errors
//		Long[] tags = parseTags(tagsString);
//		UpdateNotifierBackend.getDatabaseService().deleteResourcesByIdAndTags(
//				userId, tags);
	}

	@Path("/{id}/resources")
	@POST
	@Consumes({ "application/json" })
	public void addUserResource(@PathParam("id") long userId,
			String resourceJson) {
		// TODO process errors
//		Resource res = parseResource(resourceJson);
//		UpdateNotifierBackend.getResourcesChangesListener().onAddResource(res);
//		UpdateNotifierBackend.getDatabaseService().addResource(userId, res);
	}

	@Path("/{id}/resources")
	@PUT
	@Consumes({ "application/json" })
	public void editUserResource(@PathParam("id") long userId,
			String resourceJson) {
		// TODO process errors
		// Resource res = parseResource(resourceJson);
		// TODO Do it one more time
//		Resource savedResource = UpdateNotifierBackend.getDatabaseService()
//				.getResource(userId, resourceId);
//		if (savedResource == null) {
//			throw (new BadRequestException("Resource not exist"));
//		}
//
//		if (savedResource.getUrl() != res.getUrl()) {
//			UpdateNotifierBackend.getResourcesChangesListener()
//					.onEditResourceUrl(res);
//		}
//
//		UpdateNotifierBackend.getDatabaseService().editResource(userId,
//				resourceId, res);

	}

	@Path("/{id}/resources/{resourceId}")
	@GET
	@Produces({ "application/json" })
	public String getUserResource(@PathParam("id") long userId,
			@PathParam("resourceId") long resourceId) {
		// TODO process errors
		return new Gson().toJson(UpdateNotifierBackend.getDatabaseService()
				.getResource(userId, resourceId));
	}

	@Path("/{id}/tags")
	@GET
	@Produces({ "application/json" })
	public String getUserTags(@PathParam("id") long userId) {
		// TODO process errors
		return new Gson().toJson(UpdateNotifierBackend.getDatabaseService()
				.getTags(userId));
	}

	@Path("/{id}/tags")
	@POST
	public void addTag(@PathParam("id") long userId,
			String tagName) {
		// TODO process errors
		UpdateNotifierBackend.getDatabaseService().addTag(userId, tagName);
	}

	private Resource parseResource(String resourceJson) {
		try {
			return new Gson().fromJson(resourceJson, Resource.class);
		} catch (JsonSyntaxException ex) {
			throw (new BadRequestException("Json parsing error"));
		}
	}

	private static Long[] parseTags(String tagsString) {
		Long[] tags;
		if ("".equals(tagsString) || tagsString == null) {
			tags = null;
		} else {
			String[] tagsStrings = tagsString.split(",");
			tags = new Long[tagsStrings.length];
			try {
				for (int i = 0; i < tagsStrings.length; i += 1) {
					tags[i] = Long.parseLong(tagsStrings[i]);
				}
			} catch (NumberFormatException ex) {
				throw (new BadRequestException("Tags id parsing error"));
			}
		}
		return tags;
	}
}
