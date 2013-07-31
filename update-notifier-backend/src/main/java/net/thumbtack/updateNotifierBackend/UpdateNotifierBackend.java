package net.thumbtack.updateNotifierBackend;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;

@ApplicationPath("/")
public class UpdateNotifierBackend extends ResourceConfig {

	private static DatabaseService databaseService = new DatabaseService();
	
	public UpdateNotifierBackend() {
		packages("net.thumbtack.updateNotifierBackend.resourceHandlers");
	}

	public static DatabaseService getDatabaseService() {
		return databaseService;
	}
}
