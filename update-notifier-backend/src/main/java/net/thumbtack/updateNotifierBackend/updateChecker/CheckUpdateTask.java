package main.java.net.thumbtack.updateNotifierBackend.updateChecker;

import main.java.net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;


public class CheckUpdateTask implements Runnable {
	
	ResourceInfo resourceInfo;
	
	public CheckUpdateTask(ResourceInfo resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public void run() {
	}

}
