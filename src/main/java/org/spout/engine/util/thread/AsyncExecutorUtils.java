/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.util.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.spout.api.Spout;

public class AsyncExecutorUtils {
	private static final String LINE = "------------------------------";
	private static final AtomicReference<AsyncExecutor> waitingExecutor = new AtomicReference<AsyncExecutor>();

	/**
	 * Logs all threads, the thread details, and active stack traces
	 */
	public static void dumpAllStacks() {
		Logger log = Spout.getLogger();
		Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
		Iterator<Entry<Thread, StackTraceElement[]>> i = traces.entrySet().iterator();
		while (i.hasNext()) {
			Entry<Thread, StackTraceElement[]> entry = i.next();
			Thread thread = entry.getKey();
			log.info(LINE);

			log.info("Current Thread: " + thread.getName());
			log.info("    PID: " + thread.getId() + " | Alive: " + thread.isAlive() + " | State: " + thread.getState());
			log.info("    Stack:");
			StackTraceElement[] stack = entry.getValue();
			for (int line = 0; line < stack.length; line++) {
				log.info("        " + stack[line].toString());
			}
		}
		log.info(LINE);
	}

	/**
	 * Scans for deadlocked threads
	 */
	public static void checkForDeadlocks() {
		Logger log = Spout.getLogger();
		ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
		long[] ids = tmx.findDeadlockedThreads();
		if (ids != null) {
			log.info("Checking for deadlocks");
			ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
			log.info("The following threads are deadlocked:");
			for (ThreadInfo ti : infos) {
				log.info(ti.toString());
			}
		}
	}
	
	/**
	 * Dumps the stack for the given Thread
	 * 
	 * @param t the thread
	 */
	public static void dumpStackTrace(Thread t) {
		StackTraceElement[] stackTrace = t.getStackTrace();
		Spout.getEngine().getLogger().info("Stack trace for Thread " + t.getName());
		for (StackTraceElement e : stackTrace) {
			Spout.getEngine().getLogger().info("\tat " + e);
		}
	}
	
	/**
	 * Gets the current executor that pulseJoinAll is waiting on, or null of none
	 * 
	 * @return the executor, or null if none
	 */
	public static AsyncExecutor getWaitingExecutor() {
		return waitingExecutor.get();
	}

	/**
	 * Waits for a list of ManagedThreads to complete a pulse
	 * @param executors the threads to join for
	 * @param timeout   how long to wait, or 0 to wait forever
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
						waitingExecutor.set(e);
						e.pulseJoin(endTime - currentTime);
						waitingExecutor.set(null);
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
