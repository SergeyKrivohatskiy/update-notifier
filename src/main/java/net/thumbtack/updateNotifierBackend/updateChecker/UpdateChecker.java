package net.thumbtack.updateNotifierBackend.updateChecker;

import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sergey Krivohatskiy
 * 
 * This class checks all resources for update
 */
public class UpdateChecker {

	private static final int STARTING_DELAY_FACTOR = 5;
	private static final Logger log = LoggerFactory.getLogger(UpdateChecker.class);
	private static final long VERY_OFTEN = 60000;
	private static final long OFTEN = 300000;
	private static final long REGULAR = 600000;
	private static final long RARELY = 3600000;
	private static final long VERY_RARELY = 86400000;
	private final static long INTERVALS[] = 
		{VERY_OFTEN, OFTEN, REGULAR, RARELY, VERY_RARELY};
	public final static int SHEDULE_CODE_MAX_VALUE = INTERVALS.length-1;

	private static final long TIME_TO_STOP = 10000;
	
	private Timer timer = new Timer(true);
	private static final int THREADS_COUNT = 32;
	private Executor executor = Executors.newFixedThreadPool(THREADS_COUNT);

	public void start() {
		log.debug("Starting");
		for(byte scheduleCode = 0; scheduleCode < INTERVALS.length; scheduleCode += 1) {
			timer.schedule(new StartUpdatesChecking(scheduleCode, executor), 
					INTERVALS[scheduleCode] / STARTING_DELAY_FACTOR, INTERVALS[scheduleCode]);
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
