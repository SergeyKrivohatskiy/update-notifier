package net.thumbtack.updateNotifierBackend.resourceHandlers;

import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import com.google.gson.Gson;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
import net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;

@Path("/users")
@Singleton
public class UsersHandler {

	@Path("/signin")
	@GET
	public long signIn(@QueryParam("email") String userEmail) {
		Long userId = UpdateNotifierBackend.getDatabaseService()
				.getAccountIdByEmail(userEmail);
		if (userId == null) {
			// TODO process errors
		}
		return userId;
	}

	@Path("/{id}/resourses")
	@GET
	@Produces({ "application/json" })
	public String useresources(@PathParam("id") long userId,
			@QueryParam("tags") String tags) {
		// TODO process errors
		Set<ResourceInfo> resources = UpdateNotifierBackend
				.getDatabaseService().getResourcesByIdAndTags(userId,tags.split(","));
		return new Gson().toJson(resources);
	}
}
