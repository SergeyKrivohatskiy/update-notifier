package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.TimerTask;
import java.util.concurrent.Executor;

/**
 * @author Sergey Krivohatskiy
 * 
 *         This class is the instance of timer task. It launch/relaunch update
 *         checking thread
 */
public class StartUpdatesChecking extends TimerTask {
	private Thread updateCheckingThread;
	private UpdatesChecking updateChecking;

	public StartUpdatesChecking(byte scheduleCode, Executor executor) {
		updateChecking = new UpdatesChecking(scheduleCode, executor);
		updateCheckingThread = new Thread(updateChecking);
		updateCheckingThread.setDaemon(true);
		updateCheckingThread.start();
	}

	@Override
	public void run() {
		updateChecking.startIfTerminated();
	}

}
