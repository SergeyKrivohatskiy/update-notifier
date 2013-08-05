package net.thumbtack.updateNotifierBackend.resourceHandlers;

import java.util.List;
import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
import net.thumbtack.updateNotifierBackend.database.entities.Tag;

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
		log.trace("Get resources");
		Long[] tags = parseTags(tagsString);
		List<Resource> resources = UpdateNotifierBackend.getDatabaseService()
				.getResourcesByIdAndTags(userId, tags);
		if (resources == null) {
			log.debug("Database get request failed. Get resources bad request");
			throw (new BadRequestException("Incorrect userId"));
		}
		return new Gson().toJson(resources);
	}

	@Path("/{id}/resources")
	@DELETE
	public void deleteUserResources(@PathParam("id") long userId,
			@DefaultValue("") @QueryParam("tags") String tagsString) {
		log.trace("Delete resources");
		Long[] tags = parseTags(tagsString);
		if(!UpdateNotifierBackend.getDatabaseService().deleteResourcesByIdAndTags(
				userId, tags)) {
			log.debug("Database delete request failed. Delete resources bnot found");
			throw (new NotFoundException());
		}
	}

	@Path("/{id}/resources")
	@POST
	@Consumes({ "application/json" })
	public void addUserResource(@PathParam("id") long userId,
			String resourceJson) {
		log.trace("Add resource");
		Resource res = parseResource(resourceJson);
		UpdateNotifierBackend.getResourcesChangesListener().onAddResource(res);
		if(!UpdateNotifierBackend.getDatabaseService().addResource(userId, res)) {
			log.debug("Database add request failed. Add resources bad request");
			throw (new BadRequestException("Incorrect params"));
		}
	}

	@Path("/{id}/resources")
	@PUT
	@Consumes({ "application/json" })
	public void editUserResource(@PathParam("id") long userId,
			String resourceJson) {
		log.trace("Edit resource");
		Resource res = parseResource(resourceJson);
		Resource savedResource = UpdateNotifierBackend.getDatabaseService()
				.getResource(userId, res.getId());
		if (savedResource == null) {
			log.debug("Database get request failed. Edit resources not found");
			throw (new NotFoundException("Resource not exist"));
		}

		if (savedResource.getUrl() != res.getUrl()) {
			UpdateNotifierBackend.getResourcesChangesListener()
					.onEditResourceUrl(res);
		}

		if(!UpdateNotifierBackend.getDatabaseService().editResource(userId,
				res.getId(), res)) {
			log.debug("Database edit request failed. Edit resources bad request");
			throw (new BadRequestException());
		}

	}

	@Path("/{id}/resources/{resourceId}")
	@GET
	@Produces({ "application/json" })
	public String getUserResource(@PathParam("id") long userId,
			@PathParam("resourceId") long resourceId) {
		log.trace("Get resource");
		Resource res = UpdateNotifierBackend.getDatabaseService().getResource(userId, resourceId);
		if(res == null) {
			log.debug("Database get request failed. Get resource not found");
			throw (new NotFoundException("Resource not exist"));
		}
		return new Gson().toJson(res);
	}

	@Path("/{id}/tags")
	@GET
	@Produces({ "application/json" })
	public String getUserTags(@PathParam("id") long userId) {
		log.trace("Get tags");
		Set<Tag> tags = UpdateNotifierBackend.getDatabaseService().getTags(userId);
		if(tags == null) {
			log.debug("Database get request failed. Get tags not found");
			throw (new NotFoundException());
		}
		return new Gson().toJson(tags);
	}

	@Path("/{id}/tags")
	@POST
	public void addTag(@PathParam("id") long userId,
			String tagName) {
		log.trace("Add tag");
		if(!UpdateNotifierBackend.getDatabaseService().addTag(userId, tagName)) {
			log.debug("Database add request failed. Edit resources bad request");
			throw (new BadRequestException());
		}
	}
	
	@Path("/{id}/tags/{tagId}")
	@PUT
	public void editTag(@PathParam("id") long userId, @PathParam("tagId") long tagId,
			String tagName) {
		log.trace("Edit tag");
		if(!UpdateNotifierBackend.getDatabaseService().editTag(userId, tagId, tagName)) {
			log.debug("Database add request failed. Edit resources bad request");
			throw (new BadRequestException());
		}
	}

	private static Resource parseResource(String resourceJson) {
		try {
			return new Gson().fromJson(resourceJson, Resource.class);
		} catch (JsonSyntaxException ex) {
			log.debug("Resource parsing error");
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
				log.debug("Tags id parsing error");
				throw (new BadRequestException("Tags id parsing error"));
			}
		}
		return tags;
	}
}
