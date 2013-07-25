/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.ai;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

public class SimpleAStarStorage implements AStarStorage {
	private final Map<AStarNode, Float> closed = Maps.newHashMap();
	private final Map<AStarNode, Float> open = Maps.newHashMap();
	private final Queue<AStarNode> queue = new PriorityQueue<AStarNode>();

	@Override
	public void close(AStarNode node) {
		open.remove(node);
		closed.put(node, node.f);
	}

	@Override
	public AStarNode getBestNode() {
		return queue.peek();
	}

	@Override
	public void open(AStarNode node) {
		queue.offer(node);
		open.put(node, node.f);
		closed.remove(node);
	}

	@Override
	public AStarNode removeBestNode() {
		return queue.poll();
	}

	@Override
	public boolean shouldExamine(AStarNode neighbour) {
		Float openF = open.get(neighbour);
		if (openF != null && openF > neighbour.f) {
			open.remove(neighbour);
			openF = null;
		}
		Float closedF = closed.get(neighbour);
		if (closedF != null && closedF > neighbour.f) {
			closed.remove(neighbour);
			closedF = null;
		}
		return closedF == null && openF == null;
	}

	@Override
	public String toString() {
		return "SimpleAStarStorage [closed=" + closed + ", open=" + open + "]";
	}

	public static final Supplier<AStarStorage> FACTORY = new Supplier<AStarStorage>() {
		@Override
		public AStarStorage get() {
			return new SimpleAStarStorage();
		}
	};
}
