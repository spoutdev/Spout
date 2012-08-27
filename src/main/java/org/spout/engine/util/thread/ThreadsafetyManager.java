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

/**
 * This class contains various methods to verify thread safe operation of the
 * API method calls
 */
public class ThreadsafetyManager {
	private static Thread mainThread = null;

	public static void setMainThread(Thread thread) {
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