package org.getspout.server.util.thread;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class AsyncExecutorUtils {

	/**
	 * Waits for a list of ManagedThreads to complete a pulse
	 *
	 * @param threads the threads to join for
	 * @param timeout how long to wait
	 *
	 */
	public static void pulseJoinAll(List<AsyncExecutor> executors, long timeout) throws TimeoutException, InterruptedException {
		ThreadsafetyManager.checkMainThread();

		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + timeout;
		boolean waitForever = timeout == 0;

		if (timeout < 0) {
			throw new IllegalArgumentException("Negative timeouts are not allowed (" + timeout + ")");
		}

		boolean done = false;
		while (!done && (endTime > currentTime || waitForever)) {
			done = false;
			while (!done && (endTime > currentTime || waitForever)) {
				done = true;
				for (AsyncExecutor e : executors) {
					currentTime = System.currentTimeMillis();
					if (endTime <= currentTime && !waitForever) {
						break;
					}
					if (!e.isPulseFinished()) {
						done = false;
						e.pulseJoin(endTime - currentTime);
					}
				}
			}
			try {
				for (AsyncExecutor e : executors) {
					e.disableWake();
				}
				done = true;
				for (AsyncExecutor e : executors) {
					if (!e.isPulseFinished()) {
						done = false;
						break;
					}
				}
			} finally {
				for (AsyncExecutor e : executors) {
					e.enableWake();
				}
			}
		}

		if (endTime <= currentTime && !waitForever) {
			throw new TimeoutException("pulseJoinAll timed out");
		}

	}

}
