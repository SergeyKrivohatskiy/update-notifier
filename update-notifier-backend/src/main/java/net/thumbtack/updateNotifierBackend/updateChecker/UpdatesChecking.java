package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;


import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;
/**
 * @author Sergey Krivohatskiy
 * 
 * This class checks all resources with specified 
 * schedule code for update using specified executor.
 */
public class UpdatesChecking implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(UpdatesChecking.class);
	private byte scheduleCode;
	private Executor executor;
	private Set<Resource> resources;
	private Semaphore canBeRunned;
	private boolean isTerminated;
	
	/**
	 * 
	 * @param scheduleCode Schedule code of resources to check
	 * @param executor Executor that will execute checks
	 */
	public UpdatesChecking(byte scheduleCode, Executor executor) {
		this.scheduleCode = scheduleCode;
		this.executor = executor;
		canBeRunned = new Semaphore(0);
		isTerminated = true;
	}

	public void run() {
		try {
			while(true) {
				canBeRunned.acquire();
				doUpdateChecking();
				isTerminated = true;
			}
		} catch (InterruptedException e) {
			log.debug("UpdatesChecking was interrupted");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Start update checking if last one has terminated
	 */
	public void startIfTerminated() {
		if(isTerminated()) {
			start();
		}
	}

	private void start() {
		isTerminated = false;
		canBeRunned.release();
	}

	private boolean isTerminated() {
		return canBeRunned.availablePermits() == 0 && isTerminated;
	}

	private void doUpdateChecking() {
		resources = loadResources();
		if(resources == null) {
			log.error("Load resources failed. UpdatesChecking failed.");
			return;
		}
		for(Resource resource: resources) {
			executor.execute(new CheckForUpdate(resource));
		}
	}

	private Set<Resource> loadResources() {
		return UpdateNotifierBackend.getDatabaseService().
				getResourcesByScheduleCode(scheduleCode);
	}

}
