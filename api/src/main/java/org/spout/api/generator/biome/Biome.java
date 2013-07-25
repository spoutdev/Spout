/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.generator.biome;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.spout.api.generator.biome.selector.LayeredBiomeSelectorElement;
import org.spout.api.geo.cuboid.Chunk;

/**
 * Defines an abstract biome.
 */
public abstract class Biome implements LayeredBiomeSelectorElement {
	private int id;
	private boolean registered = false;
	private final List<Decorator> decorators = new ArrayList<Decorator>();

	public Biome() {
		BiomeRegistry.register(this);
	}

	/**
	 * Remove all the decorators.
	 */
	public final void clearDecorators() {
		decorators.clear();
	}

	/**
	 * Remove all the decorators and add the passed ones.
	 *
	 * @param decorators The decorators to set.
	 */
	public final void setDecorators(Decorator... decorators) {
		clearDecorators();
		addDecorators(decorators);
	}

	/**
	 * Adds the decorators.
	 *
	 * @param decorators The decorators to add.
	 */
	public final void addDecorators(Decorator... decorators) {
		this.decorators.addAll(Arrays.asList(decorators));
	}

	/**
	 * Removes the decorators.
	 *
	 * @param decorators The decorators to remove.
	 */
	public final void removeDecorators(Decorator... decorators) {
		this.decorators.removeAll(Arrays.asList(decorators));
	}

	/**
	 * Gets the unprotected list of decorators. Use this to get more control over editing of the list.
	 *
	 * @return The list of decorator.
	 */
	public final List<Decorator> getDecorators() {
		return decorators;
	}

	private final static ConcurrentHashMap<Class<? extends Decorator>, AtomicLong> populatorProfilerMap = new ConcurrentHashMap<Class<? extends Decorator>, AtomicLong>();
	private final static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

	private final void populatorAdd(Decorator populator, long delta) {
		AtomicLong i = populatorProfilerMap.get(populator.getClass());
		if (i == null) {
			i = new AtomicLong();
			AtomicLong oldCounter = populatorProfilerMap.putIfAbsent(populator.getClass(), i);
			if (oldCounter != null) {
				i = oldCounter;
			}
		}
		i.addAndGet(delta);
	}

	private final static Comparator<Entry<Class<? extends Decorator>, Long>> comp = new Comparator<Entry<Class<? extends Decorator>, Long>>() {
		@Override
		public int compare(Entry<Class<? extends Decorator>, Long> o1, Entry<Class<? extends Decorator>, Long> o2) {
			if (o1.getValue() > o2.getValue()) {
				return 1;
			} else if (o1.getValue() < o2.getValue()) {
				return -1;
			} else {
				return 0;
			}
		}
	};

	public static List<Entry<Class<? extends Decorator>, Long>> getProfileResults() {

		List<Entry<Class<? extends Decorator>, Long>> list = new ArrayList<Entry<Class<? extends Decorator>, Long>>(populatorProfilerMap.size());

		for (Entry<Class<? extends Decorator>, AtomicLong> e : populatorProfilerMap.entrySet()) {
			list.add(new AbstractMap.SimpleEntry<Class<? extends Decorator>, Long>(e.getKey(), e.getValue().get()));
		}

		Collections.sort(list, comp);

		return list;
	}

	public final void decorate(Chunk chunk, Random random) {
		for (Decorator b : decorators) {
			long time = -bean.getCurrentThreadCpuTime();
			b.populate(chunk, random);
			time += bean.getCurrentThreadCpuTime();
			populatorAdd(b, time);
		}
	}

	protected final void setId(int id) {
		if (!registered) {
			this.id = id;
			registered = true;
		}
	}

	public final int getId() {
		return id;
	}

	public abstract String getName();
}
