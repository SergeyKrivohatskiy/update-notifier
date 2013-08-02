package net.thumbtack.updateNotifierBackend.listeners;

import net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;


public class ResourcesChangesListener {
	
	public void onAddResource(ResourceInfo resource) {
		// recompute hash for new resource
	}
	
	public void onEditResourceUrl(ResourceInfo resource) {
		// if resource.url was changed => recompute hash
	}
	
}
