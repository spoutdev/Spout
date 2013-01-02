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
package org.spout.api.ai.goap;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.spout.api.ai.AStarNode;
import org.spout.api.ai.Agent;
import org.spout.api.ai.Plan;

import com.google.common.collect.Lists;

public class PlannerNode extends AStarNode {
	private final PlannerAgent agent;
	private final Action applied;
	private float cachedModifier = -1f;
	private final WorldState state;

	private PlannerNode(PlannerAgent agent, WorldState initialState) {
		this(agent, initialState, null);
	}

	private PlannerNode(PlannerAgent agent, WorldState initialState, Action appliedAction) {
		this.agent = agent;
		this.state = initialState;
		this.applied = appliedAction;
	}

	@Override
	public Plan<Agent> buildPlan() {
		Deque<Action> actions = new ArrayDeque<Action>();
		Iterable<PlannerNode> parents = getParents();
		for (PlannerNode start : parents) {
			if (start.applied != null)
				actions.add(start.applied);
		}
		Action[] plan = actions.toArray(new Action[actions.size()]);
		return new ActionPlan(state, plan, getPathCost());
	}

	public int difference(WorldState otherState) {
		return otherState.difference(state);
	}

	private float getHeuristicModifier() {
		if (applied == null)
			return 0;
		return cachedModifier == -1 ? cachedModifier = Math.max(1, agent.getCostModifierFor(applied)) * Math.max(1, applied.getCost()) : cachedModifier;
	}

	@Override
	public Iterable<AStarNode> getNeighbours() {
		List<AStarNode> neighbours = Collections.emptyList();
		for (Action action : agent.getAvailableActions()) {
			WorldState preconditions = action.getPreconditions();
			if (preconditions != null && state.difference(preconditions) != 0)
				continue;
			boolean canExecute = action.evaluateContextPreconditions();
			if (!canExecute)
				continue;
			WorldState effects = action.getEffects();
			WorldState newState = state.apply(effects);
			PlannerNode newNode = PlannerNode.create(agent, newState, action);
			if (neighbours == Collections.EMPTY_LIST)
				neighbours = Lists.newArrayList();
			neighbours.add(newNode);
		}
		return neighbours;
	}

	public float heuristic(PlannerNode to) {
		return heuristic(to.state);
	}

	public float heuristic(WorldState goal) {
		return state.difference(goal) + getHeuristicModifier();
	}

	public boolean stateEquals(WorldState goal) {
		return goal.difference(state) == 0;
	}

	public static PlannerNode create(PlannerAgent agent, WorldState initialState) {
		return new PlannerNode(agent, initialState);
	}

	public static PlannerNode create(PlannerAgent agent, WorldState initialState, Action appliedAction) {
		return new PlannerNode(agent, initialState, appliedAction);
	}
}
