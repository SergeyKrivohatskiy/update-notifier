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
	private byte sheduleCode;
	private Executor executor;
	private Set<Resource> resources;
	private Semaphore canBeRunned;
	private boolean isTerminated;
	
	public UpdatesChecking(byte sheduleCode, Executor executor) {
		this.sheduleCode = sheduleCode;
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
				getResourcesBySheduleCode(sheduleCode);
	}

}
