package net.thumbtack.updateNotifierBackend.resourceHandlers;

import java.util.Set;

import javax.inject.Singleton;
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

import com.google.gson.Gson;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
import net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;

@Path("/users")
@Singleton
public class UsersHandler {

	@Path("signin")
	@GET
	public long signIn(@QueryParam("email") String userEmail) {
		Long userId = UpdateNotifierBackend.getDatabaseService()
				.getUserIdByEmail(userEmail);
		if (userId == null) {
			// TODO process errors
		}
		return userId;
	}

	@Path("/{id}/resourses")
	@GET
	@Produces({"application/json"})
	public String getUserResources(@PathParam("id") long userId,
			@DefaultValue("") @QueryParam("tags") String tagsString) {
		// TODO process errors
		long[] tags;
		if("".equals(tagsString)) {
			tags = null;
		} else {
			String[] tagsStrings = tagsString.split(",");
			tags = new long[tagsStrings.length];
			for(int i = 0; i < tagsStrings.length; i += 1) {
				tags[i] = Long.parseLong(tagsStrings[i]);
			}
		}
		Set<ResourceInfo> resources = UpdateNotifierBackend
				.getDatabaseService().getResourcesByIdAndTags(userId, tags);
		return new Gson().toJson(resources);
	}

	@Path("/{id}/resourses")
	@DELETE
	public void deleteUserResources(@PathParam("id") long userId,
			@DefaultValue("") @QueryParam("tags") String tagsString) {
		// TODO process errors
		long[] tags;
		if("".equals(tagsString)) {
			tags = null;
		} else {
			String[] tagsStrings = tagsString.split(",");
			tags = new long[tagsStrings.length];
			for(int i = 0; i < tagsStrings.length; i += 1) {
				tags[i] = Long.parseLong(tagsStrings[i]);
			}
		}
		UpdateNotifierBackend.getDatabaseService()
		.deleteResourcesByIdAndTags(userId, tags);
	}

	@Path("/{id}/resourses")
	@POST
	@Consumes({"application/json"})
	public void addUserResource(@PathParam("id") long userId, String resourceJson) {
		// TODO process errors
		UpdateNotifierBackend.getDatabaseService()
		.addResource(userId, new Gson().fromJson(resourceJson, ResourceInfo.class));
	}

	@Path("/{id}/resourses/{resourceId}")
	@PUT
	@Consumes({"application/json"})
	public void editUserResource(@PathParam("id") long userId, 
			@PathParam("resourceId") long resourceId, String resourceJson) {
		// TODO process errors
		UpdateNotifierBackend.getDatabaseService()
		.editResource(userId, resourceId, new Gson().fromJson(resourceJson, ResourceInfo.class));
	}

	@Path("/{id}/resourses/{resourceId}")
	@GET
	@Produces({"application/json"})
	public void getUserResource(@PathParam("id") long userId, 
			@PathParam("resourceId") long resourceId, String resourceJson) {
		// TODO process errors
		UpdateNotifierBackend.getDatabaseService()
		.getResource(userId, resourceId);
	}

	@Path("/{id}/tags")
	@GET
	@Produces({"application/json"})
	public void getUserTags(@PathParam("id") long userId, 
			@PathParam("resourceId") long resourceId, String resourceJson) {
		// TODO process errors
		UpdateNotifierBackend.getDatabaseService()
		.getTags(userId);
	}
}
