package org.getspout.server.util.thread;

/**
 * This class contains various methods to verify thread safe operation of the
 * API method calls
 */

public class ThreadsafetyManager {

	private static Thread mainThread = null;

	public void setMainThread(Thread thread) {
		if (mainThread == null) {
			mainThread = thread;
		} else {
			throw new IllegalArgumentException("The main thread may not be set more than once");
		}
	}

	public static void checkMainThread() {
		if (Thread.currentThread() != mainThread) {
			throw new ThreadTimingException("An attempt was made to run a main thread only method from outside the main thread");
		}
	}

	public static void checkCurrentThread(Thread thread) {
		if (Thread.currentThread() != thread) {
			throw new ThreadTimingException("An attempt was made to run a manager only thread only method from outside the main thread");
		}
	}

}