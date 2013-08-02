package net.thumbtack.updateNotifierBackend.updateChecker;

import net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;

public class CheckForUpdate implements Runnable {

	private ResourceInfo resource;
	
	public CheckForUpdate(ResourceInfo resource) {
		this.resource = resource;
	}

	public void run() {
		System.out.println("updateChecking URL = \"" + resource.getUrl() + "\"");
	}

}
