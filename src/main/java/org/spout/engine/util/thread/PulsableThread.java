/*
 * This file is part of Spout (http://www.spout.org/).
 *
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

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Thread object that can be pulsed
 */
public abstract class PulsableThread extends Thread {
	private static final Logger logger = Logger.getLogger(PulsableThread.class.getCanonicalName());
	private final AtomicBoolean pulsing = new AtomicBoolean(false);

	public PulsableThread(String name) {
		super(name);
	}

	/**
	 * Causes the thread to execute one pulse by calling pulseRun();
	 * @return false if the thread was already pulsing
	 */
	public boolean pulse() {
		boolean success = pulsing.compareAndSet(false, true);
		if (!success) {
			return false;
		}
		synchronized (pulsing) {
			pulsing.notifyAll();
		}
		return true;
	}

	/**
	 * Puts the current thread to sleep until the current pulse operation has
	 * completed
	 */
	public void pulseJoin() throws InterruptedException {
		synchronized (pulsing) {
			while (pulsing.get()) {
				pulsing.wait();
			}
		}
	}

	/**
	 * Puts the current thread to sleep until the current pulse operation has
	 * completed
	 * @param millis the time in milliseconds to wait before throwing a
	 * TimeoutException
	 */
	public void pulseJoin(long millis) throws InterruptedException, TimeoutException {
		if (millis == 0) {
			pulseJoin();
			return;
		}
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + millis;
		synchronized (pulsing) {
			while (currentTime < endTime && pulsing.get()) {
				pulsing.wait(endTime - currentTime);
				currentTime = System.currentTimeMillis();
			}
		}
		if (currentTime >= endTime) {
			throw new TimeoutException();
		}
	}

	/**
	 * This method indicates if the thread is currently pulsing
	 * @return true if the thread is pulsing
	 */
	public boolean isPulsing() {
		return pulsing.get();
	}

	/**
	 * This method is called when the thread is woken up.
	 * <p/>
	 * Interrupted exceptions MUST be thrown when interrupts happen.
	 * <p/>
	 * Where InterruptedExceptions are caught for handling,
	 * InterruptedExceptions should be chained.
	 * <p/>
	 * This is required in order to ensure that the thread can be automatically
	 * shut down.
	 */
	protected abstract void pulsedRun() throws InterruptedException;

	/**
	 * The thread will continue until it is interrupted
	 */
	@Override
	public final void run() {
		try {
			while (!isInterrupted()) {
				synchronized (pulsing) {
					while (!pulsing.get()) {
						pulsing.wait();
					}
				}

				try {
					pulsedRun();
				} catch (InterruptedException ie) {
					throw ie;
				} catch (Throwable t) {
					logger.log(Level.SEVERE, "Error while pulsing thread " + getName() + ":  " + t.getMessage(), t);
					t.printStackTrace();
				} finally {
					synchronized (pulsing) {
						pulsing.set(false);
						pulsing.notifyAll();
					}
				}
			}
		} catch (InterruptedException ie) {
		}
	}
}
