package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.thumbtack.updateNotifierBackend.databaseService.DatabaseService;


public class UpdateChecker {
	
	private static final int THREDS_COUNT = 1;
	/**
	 * Timeout to skip timer task
	 */
	private static final long TASK_TIMEOUT = 5000;
	private static final long TASK_PERIOD = 60000;
	private ExecutorService threadPool;
	private Timer timer = null;
	private CheckUpdatesTimerTask timerTask;
	
	public UpdateChecker(DatabaseService database) {
		threadPool = Executors.newFixedThreadPool(THREDS_COUNT);
		timerTask = new CheckUpdatesTimerTask(database, TASK_TIMEOUT, threadPool);
		timer = new Timer();
	}

	public void start() {
		timer.schedule(timerTask, 0, TASK_PERIOD);
	}
	
	public void stop() {
		timer.cancel();
		timer.purge();
	}
	
}
