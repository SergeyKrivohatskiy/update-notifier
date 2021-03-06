package net.thumbtack.updateNotifierBackend;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.database.DatabaseWrapper;
import net.thumbtack.updateNotifierBackend.updateChecker.UpdateCheckLauncher;
import net.thumbtack.updateNotifierBackend.updateListener.ResourcesUpdateListener;

@ApplicationPath("/")
public class UpdateNotifierBackend extends ResourceConfig {

	private static final Logger log = LoggerFactory.getLogger(UpdateNotifierBackend.class);
	private static DatabaseWrapper databaseService = DatabaseWrapper.getInstance();
	private static UpdateCheckLauncher updateChecker = new UpdateCheckLauncher();
	private static ResourcesUpdateListener resUpdateListener = ResourcesUpdateListener.getInstance();
	
	public UpdateNotifierBackend() {
		log.debug("Starting");
		packages("net.thumbtack.updateNotifierBackend.resourceHandlers");
		updateChecker.start();
	}

	/**
	 * Use it when you need an access to the database
	 * 
	 * @return application database service
	 */
	public static DatabaseWrapper getDatabaseService() {
		return databaseService;
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
}
