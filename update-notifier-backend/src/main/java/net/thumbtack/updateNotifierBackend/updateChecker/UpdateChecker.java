package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateChecker {

	private static final Logger log = LoggerFactory.getLogger(UpdateChecker.class);
	private static final long VERY_OFTEN = 60000;
	private static final long OFTEN = 300000;
	private static final long REGULAR = 600000;
	private static final long RARELY = 3600000;
	private static final long VERY_RARELY = 86400000;
	private final static long INTERVALS[] = 
		{VERY_OFTEN, OFTEN, REGULAR, RARELY, VERY_RARELY};

	private static final long TIME_TO_STOP = 10000;
	
	private Timer timer = new Timer(true);
	private static final int THREADS_COUNT = 32;
	private Executor executor = Executors.newFixedThreadPool(THREADS_COUNT);

	public void start() {
		log.debug("Starting");
		for(byte sheduleCode = 0; sheduleCode < INTERVALS.length; sheduleCode += 1) {
			timer.schedule(new StartUpdatesChecking(sheduleCode, executor), 
					INTERVALS[sheduleCode] / 10, INTERVALS[sheduleCode]);
		}
	}

	public void stop() {
		timer.cancel();
		timer.purge();
		log.debug("Stopping");
		try {
			Thread.sleep(TIME_TO_STOP);
		} catch (InterruptedException e) {} // Ignore
	}
}
