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
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import static net.thumbtack.updateNotifierBackend.UpdateNotifierBackend.getDatabaseService;
import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import net.thumbtack.updateNotifierBackend.database.entities.Tag;

@Path("/users")
@Singleton
public class UsersHandler {
	
	// TODO Why do not I use this and create new gSon object every time?
	private static final Gson GSON = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd hh:mm:ss.S").create();
	private static final Logger log = LoggerFactory
			.getLogger(UsersHandler.class);

	@Path("signin")
	@GET
	public long signIn(@QueryParam("email") String userEmail) {
		log.trace("Sign in: " + userEmail);
		if (userEmail == null) {
			throw new BadRequestException(
					"Missing 'email' parameter in the url");
		}
		Long userId = getDatabaseService().getUserIdByEmailOrAdd(userEmail);
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
		List<Resource> resources = getDatabaseService()
				.getResourcesByIdAndTags(userId, tags);
		if (resources == null) {
			log.debug("Database get request failed. Get resources bad request");
			throw (new BadRequestException("Incorrect userId"));
		}
		return GSON.toJson(resources);
	}

	@Path("/{id}/resources")
	@DELETE
	public void deleteUserResources(@PathParam("id") long userId,
			@DefaultValue("") @QueryParam("tags") String tagsString) {
		log.trace("Delete resources");
		Long[] tags = parseTags(tagsString);
		if (!getDatabaseService().deleteResourcesByIdAndTags(userId, tags)) {
			log.debug("Database delete request failed. Delete resources bnot found");
			throw (new NotFoundException());
		}
	}

	@Path("/{id}/resources")
	@POST
	@Consumes({ "application/json" })
	public Response addUserResource(@PathParam("id") long userId,
			String resourceJson) {
		log.trace("Add resource");
		Resource resource = parseResource(resourceJson);
		if (!getDatabaseService().addResource(userId, resource)) {
			log.debug("Database add request failed. Add resources bad request");
			throw (new BadRequestException("Incorrect params"));
		}
		
		return Response.status(HttpStatus.CREATED_201).entity(resource.getId().toString())
				.build();
	}

	@Path("/{id}/resources")
	@PUT
	@Consumes({ "application/json" })
	public void editUserResource(@PathParam("id") long userId,
			String resourceJson) {
		log.trace("Edit resource");
		Resource resource = parseResource(resourceJson);

		if (!getDatabaseService().editResource(userId, resource)) {
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
		Resource res = getDatabaseService().getResource(userId, resourceId);
		if (res == null) {
			log.debug("Database get request failed. Get resource not found");
			throw (new NotFoundException("Resource not exist"));
		}
		return GSON.toJson(res);
	}


	@Path("/{id}/resources/{resourceId}")
	@DELETE
	@Produces({ "application/json" })
	public void deleteUserResource(@PathParam("id") long userId,
			@PathParam("resourceId") long resourceId) {
		log.trace("Get resource");
		
		if (!getDatabaseService().deleteResource(resourceId)) {
			log.debug("Database delete request failed. Delete resource not found");
			throw (new NotFoundException("Resource not exist"));
		}
	}

	@Path("/{id}/tags")
	@GET
	@Produces({ "application/json" })
	public String getUserTags(@PathParam("id") long userId) {
		log.trace("Get tags");
		Set<Tag> tags = getDatabaseService().getTags(userId);
		if (tags == null) {
			log.debug("Database get request failed. Get tags not found");
			throw (new NotFoundException());
		}
		return GSON.toJson(tags);
	}

	@Path("/{id}/tags")
	@POST
	public Response addTag(@PathParam("id") long userId, String tagName) {
		log.trace("Add tag");
		Long id = getDatabaseService().addTag(userId, tagName);
		if (id == null) {
			log.debug("Database add request failed. Edit resources bad request");
			throw (new BadRequestException());
		}
		return Response.status(HttpStatus.CREATED_201).entity(id.toString())
				.build();
	}

	@Path("/{id}/tags/{tagId}")
	@PUT
	public void editTag(@PathParam("id") long userId,
			@PathParam("tagId") long tagId, String tagName) {
		log.trace("Edit tag");
		if (!getDatabaseService().editTag(userId, tagId, tagName)) {
			log.debug("Database add request failed. Edit resources bad request");
			throw (new BadRequestException());
		}
	}

	private static Resource parseResource(String resourceJson) {
		try {
			Resource res = GSON.fromJson(resourceJson, Resource.class);
			if(res.getDomPath() == null || res.getSheduleCode() < 0 || res.getSheduleCode() > 4 ||
					res.getUrl() == null) {
				log.debug("Resource parsing error");
				throw (new BadRequestException("Json parsing error"));
			}
			return res;
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
