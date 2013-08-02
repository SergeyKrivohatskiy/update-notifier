package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.Set;
import java.util.concurrent.Executor;

import net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;

public class UpdatesChecking implements Runnable {

	private int periodicity;
	private Executor executor;
	private Set<ResourceInfo> resources;
	
	public UpdatesChecking(int periodicity, Executor executor) {
		this.periodicity = periodicity;
		this.executor = executor;
	}

	public void run() {
		resources = loadResources(periodicity);
		if(resources == null) {
			return;
		}
		for(ResourceInfo resource: resources) {
			executor.execute(new CheckForUpdate(resource));
		}
	}

	private Set<ResourceInfo> loadResources(int periodicity2) {
		// TODO Auto-generated method stub
		return null;
	}

}
