package main.java.net.thumbtack.updateNotifierBackend;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import main.java.net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;

@ApplicationPath("/")
public class UpdateNotifierBackend extends ResourceConfig {

	private static DatabaseService databaseService = new DatabaseService();
	
	public UpdateNotifierBackend() {
		packages("main.java.net.thumbtack.updateNotifierBackend.resourceHandlers");
	}

	public static DatabaseService getDatabaseService() {
		return databaseService;
	}
}
