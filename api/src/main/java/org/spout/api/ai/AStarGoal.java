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

public interface AStarGoal<T extends AStarNode> {
	/**
	 * Returns the cost of moving between the two supplied {@link AStarNode}s.
	 * 
	 * @param from
	 *            The node to start from
	 * @param to
	 *            The end node
	 * @return The cost
	 */
	float g(T from, T to);

	/**
	 * Returns the initial cost value when starting from the supplied
	 * {@link AStarNode}. This represents an initial estimate for reaching the
	 * goal state from the start node.
	 * 
	 * @param node
	 *            The start node
	 * @return The initial cost
	 */
	float getInitialCost(T node);

	/**
	 * Returns the estimated heuristic cost of traversing from the supplied
	 * {@link AStarNode} to the goal.
	 * 
	 * @param from
	 *            The start node
	 * @return The heuristic cost
	 */
	float h(T from);

	/**
	 * Returns whether the supplied {@link AStarNode} represents the goal state
	 * for this <code>AStarGoal</code>. This will halt execution of the calling
	 * {@link AStarMachine}.
	 * 
	 * @param node
	 *            The node to check
	 * @return Whether the node is the goal state
	 */
	boolean isFinished(T node);
}
