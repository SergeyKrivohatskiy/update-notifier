package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.TimerTask;
import java.util.concurrent.Executor;

public class StartUpdatesChecking extends TimerTask {
	private Thread updateCheckingThread;
	private UpdatesChecking updateChecking;
	
	public StartUpdatesChecking(int periodicity, Executor executor) {
		updateChecking = new UpdatesChecking(periodicity, executor);
		updateCheckingThread = new Thread(updateChecking);
		updateCheckingThread.setDaemon(true);
		updateCheckingThread.start();
	}
	
	@Override
	public void run() {
		updateChecking.startIfTerminated();
	}

}
