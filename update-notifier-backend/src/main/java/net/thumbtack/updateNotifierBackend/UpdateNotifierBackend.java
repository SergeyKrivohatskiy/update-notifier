package net.thumbtack.updateNotifierBackend;

import java.util.Properties;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import net.thumbtack.updateNotifierBackend.database.DatabaseService;
import net.thumbtack.updateNotifierBackend.listeners.ResourcesUpdateListener;
import net.thumbtack.updateNotifierBackend.listeners.ResourcesChangesListener;
import net.thumbtack.updateNotifierBackend.updateChecker.UpdateChecker;

@ApplicationPath("/")
public class UpdateNotifierBackend extends ResourceConfig {

	private static DatabaseService databaseService = new DatabaseService();
	private static UpdateChecker updateChecker = new UpdateChecker();
	private static ResourcesUpdateListener resUpdateListener = new ResourcesUpdateListener();
	private static ResourcesChangesListener resChangesListener = new ResourcesChangesListener();
	
	public UpdateNotifierBackend() {
		packages("net.thumbtack.updateNotifierBackend.resourceHandlers");
		updateChecker.start();
	}

	/**
	 * Use it when you need an access to the database
	 * 
	 * @return application database service
	 */
	public static DatabaseService getDatabaseService() {
		return databaseService;
	}

	/**
	 * You need to notify application resource changer 
	 * listener about all resource changes(adding new 
	 * resource or editing resource URL)
	 * 
	 * @return application resource changer listener
	 */
	public static ResourcesChangesListener getResourcesChangesListener() {
		return resChangesListener;
	}

	/**
	 * You need to notify application update listener
	 * about all resource updates
	 * 
	 * @return application update listener
	 */
	public static ResourcesUpdateListener getResourcesUpdateListener() {
		return resUpdateListener;
	}
	
	@Override
	protected void finalize() throws Throwable {
		updateChecker.stop();
		super.finalize();
	}
}
