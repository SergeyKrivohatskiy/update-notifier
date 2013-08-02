package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.TimerTask;
import java.util.concurrent.Executor;

public class StartUpdatesChecking extends TimerTask {

	private int periodicity;
	private Executor executor;
	
	public StartUpdatesChecking(int periodicity, Executor executor) {
		this.periodicity = periodicity;
		this.executor = executor;
	}
	
	@Override
	public void run() {
		Thread thread = new Thread(new UpdatesChecking(periodicity, executor));
		thread.setDaemon(true);
		thread.start();
	}

}
