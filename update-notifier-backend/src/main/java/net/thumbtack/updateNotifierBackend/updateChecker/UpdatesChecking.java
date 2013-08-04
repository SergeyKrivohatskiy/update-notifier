package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;


import net.thumbtack.updateNotifierBackend.database.entities.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thumbtack.updateNotifierBackend.UpdateNotifierBackend;

public class UpdatesChecking implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(UpdatesChecking.class);
	private byte periodicity;
	private Executor executor;
	private Set<Resource> resources;
	private Semaphore canBeRunned;
	
	public UpdatesChecking(byte periodicity, Executor executor) {
		this.periodicity = periodicity;
		this.executor = executor;
		canBeRunned = new Semaphore(0);
	}

	public void run() {
		try {
			while(true) {
				canBeRunned.acquire();
				doUpdateChecking();
			}
		} catch (InterruptedException e) {
			log.debug("UpdatesChecking was interrupted");
			Thread.currentThread().interrupt();
		}
	}

	public void startIfTerminated() {
		if(isTerminated()) {
			canBeRunned.release();
		}
	}

	private boolean isTerminated() {
		return canBeRunned.availablePermits() == 0;
	}

	public void doUpdateChecking() {
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
				getResourcesBySheduleCode(periodicity);
	}

}
