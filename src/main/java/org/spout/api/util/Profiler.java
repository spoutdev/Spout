/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.util;

import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.hash.TObjectLongHashMap;
import gnu.trove.stack.TLongStack;
import gnu.trove.stack.array.TLongArrayStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

import org.spout.api.Spout;

import com.google.common.collect.MapMaker;

/**
 * Profiling class for debugging time spent in Spout engine code.
 * 
 * For internal use only.
 */
public enum Profiler {
	INSTANCE;

	private Map<Thread, Profile> threadMap = new MapMaker().weakKeys().softValues().makeMap();
	private AtomicLong time = new AtomicLong(System.currentTimeMillis());
	
	/**
	 * Starts a new profile context with the given name. 
	 * <br/><br/>
	 * <b>Note:</b> Will have no effect if debug mode is disabled.
	 * 
	 * @param profile
	 */
	public static void start(String profile) {
		if (Spout.debugMode()) {
			getProfile().start(profile);
		}
	}

	/**
	 * Stops the previous profile context and starts a new profile context
	 * with the given name.
	 * <br/><br/>
	 * <b>Note:</b> Will have no effect if debug mode is disabled.
	 * 
	 * @param profile context name
	 */
	public static void startAndStop(String profile) {
		stop();
		start(profile);
	}

	/**
	 * Stops the current profile context
	 * <br/><br/>
	 * <b>Note:</b> Will have no effect if debug mode is disabled.
	 */
	public static void stop() {
		if (Spout.debugMode()) {
			getProfile().stop();
		}
	}

	private static Profile getProfile() {
		Profile profile = INSTANCE.threadMap.get(Thread.currentThread());
		if (profile == null) {
			profile = INSTANCE.new Profile();
			INSTANCE.threadMap.put(Thread.currentThread(), profile);
		}
		return profile;
	}
	
	/**
	 * Logs the total times spent in the profiler. 
	 * <br/><br/>
	 * <b>Warning: This should only be called from
	 * the main thread, or unspecified behavior will occur.</b>
	 * <br/><br/>
	 * <b>Note:</b> Will have no effect if debug mode is disabled.
	 */
	public static void log() {
		if (Spout.debugMode()) {
			TObjectLongHashMap<String> totals = new TObjectLongHashMap<String>();
			for (Entry<Thread, Profile> entry : INSTANCE.threadMap.entrySet()) {
				if (entry.getValue().totalTime.size() > 0) {
					TObjectLongIterator<String> i = entry.getValue().totalTime.iterator();
					i.advance();
					while(i.hasNext()) {
						totals.adjustOrPutValue(i.key(), i.value(), i.value());
						i.advance();
					}
					entry.getValue().totalTime.clear();
				}
			}
			Spout.getLogger().info("-------------- Time Passed: " + (System.currentTimeMillis() - INSTANCE.time.getAndSet(System.currentTimeMillis())) + " ms --------------");
			ArrayList<String> keys = new ArrayList<String>(totals.keySet());
			Collections.sort(keys);
			for (String key : keys) {
				Spout.getLogger().info(key + " : " + totals.get(key) / 1E6D + " ms");
			}
		}
	}

	private class Profile {
		Stack<String> level = new Stack<String>();
		TLongStack time = new TLongArrayStack();
		TObjectLongHashMap<String> totalTime = new TObjectLongHashMap<String>();

		public void start(String name) {
			level.push(name);
			time.push(System.nanoTime());
		}

		public void stop() {
			String profile = this.level.pop();
			StringBuilder builder = new StringBuilder();
			for (String level : this.level) {
				builder.append(level);
				builder.append(".");
			}
			builder.append(profile);
			long time = System.nanoTime() - this.time.pop();
			totalTime.adjustOrPutValue(builder.toString(), time, time);
		}
	}
}
