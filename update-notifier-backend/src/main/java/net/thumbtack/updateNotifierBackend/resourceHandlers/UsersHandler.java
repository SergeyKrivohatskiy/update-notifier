package main.java.net.thumbtack.updateNotifierBackend.resourceHandlers;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import main.java.net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;

@Path("/users")
@Singleton
public class UsersHandler {
	
	@Path("/signin")
	@GET
	public long signIn(@QueryParam("email") String userEmail) {
		Long userId = UpdateNotifierBackend.getDatabaseService()
				.getAccountIdByEmail(userEmail);
		if(userId == null) {
			//TODO process errors
		}
		return userId;
	}
}
