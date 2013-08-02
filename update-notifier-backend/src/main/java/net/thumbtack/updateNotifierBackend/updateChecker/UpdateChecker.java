package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class UpdateChecker {

	private static final long VERY_OFTEN = 60000;
	private static final long OFTEN = 300000;
	private static final long REGULAR = 600000;
	private static final long RARELY = 3600000;
	private static final long VERY_RARELY = 86400000;
	private final static long INTERVALS[] = 
		{VERY_OFTEN, OFTEN, REGULAR, RARELY, VERY_RARELY};

	private static final long TIME_TO_STOP = 10000;
	
	private Timer timer = new Timer(true);
	private static final int THREADS_COUNT = 2;
	private Executor executor = Executors.newFixedThreadPool(THREADS_COUNT);
	
	public UpdateChecker() {
	}

	public void start() {
		for(int i = 0; i < INTERVALS.length; i += 1) {
			timer.schedule(new StartUpdatesChecking(i, executor), INTERVALS[i], INTERVALS[i]);
		}
	}

	public void stop() {
		timer.cancel();
		timer.purge();
		System.out.println("Stopping");
		try {
			Thread.sleep(TIME_TO_STOP);
		} catch (InterruptedException e) {} // Ignore
	}
}
