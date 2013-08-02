package net.thumbtack.updateNotifierBackend.updateChecker;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;

public class CheckForUpdate implements Runnable {

	private Resource resource;
	
	public CheckForUpdate(Resource resource) {
		this.resource = resource;
	}

	public void run() {
		System.out.println("updateChecking URL = \"" + resource.getUrl() + "\"");
	}

}
