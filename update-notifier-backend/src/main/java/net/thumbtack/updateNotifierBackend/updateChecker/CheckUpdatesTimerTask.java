package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;
import net.thumbtack.updateNotifierBackend.databaseService.ResourceInfo;


public class CheckUpdatesTimerTask extends TimerTask {

	/**
	 * In milliseconds
	 */
	private static long AWAIT_TERMINATION_TIME = 5000;
	private long taskTimeout;
	DatabaseService database;
	ExecutorService threadPool;
	
	public CheckUpdatesTimerTask(DatabaseService database, long taskTimeout, ExecutorService threadPool) {
		this.database = database;
		this.taskTimeout = taskTimeout;
		this.threadPool = threadPool;
	}
	
	@Override
	public void run() {
		if (System.currentTimeMillis() - scheduledExecutionTime() >= taskTimeout) {
			// Task skipped
			return;
		}
       
		List<ResourceInfo> resources = database.getAllResources();
		for(ResourceInfo resourceInfo: resources) {
			threadPool.execute(new CheckUpdateTask(resourceInfo));
		}
		
		try {
       		if(!threadPool.awaitTermination(AWAIT_TERMINATION_TIME, TimeUnit.MILLISECONDS)) {
       			// Task not scheduled in time
       		}
		} catch (InterruptedException e) {
			// Just exit if Interrupted
		}
	}

}
