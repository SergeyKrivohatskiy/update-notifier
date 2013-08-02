package net.thumbtack.updateNotifierBackend.listeners;

import net.thumbtack.updateNotifierBackend.database.entities.Resource;


public class ResourcesChangesListener {
	
	public void onAddResource(Resource resource) {
		// recompute hash for new resource
	}
	
	public void onEditResourceUrl(Resource resource) {
		// if resource.url was changed => recompute hash
	}
	
}
