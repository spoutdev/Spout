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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.spout.api.ai.Agent;
import org.spout.api.ai.Plan;

import com.google.common.collect.Lists;

public class SimpleActionPlanner implements ActionPlanner {
	private final PlannerAgent agent;
	private final List<Action> availableActions = Lists.newArrayList();
	private final List<Goal> availableGoals = Lists.newArrayList();
	private Goal currentGoal;
	private Plan<Agent> currentPlan;

	public SimpleActionPlanner(PlannerAgent agent) {
		this.agent = agent;
	}

	@Override
	public Iterable<Action> getAvailableActions() {
		return availableActions;
	}

	@Override
	public float getCostModifierFor(Action action) {
		return 1F;
	}

	private void replan() {
		Goal best = selectBestGoal(agent);
		if (best != null) {
			Plan<Agent> plan = agent.generatePlan(best.getGoalState());
			boolean replace = shouldReplaceCurrentPlan(plan);
			if (replace)
				switchPlanTo(best, plan);
		}
	}

	private void resetPlan() {
		currentPlan = null;
		currentGoal = null;
	}

	private Goal selectBestGoal(final PlannerAgent agent) {
		Collections.sort(availableGoals, new Comparator<Goal>() {
			@Override
			public int compare(Goal o1, Goal o2) {
				// descending order
				return o2.getPriority() - o1.getPriority();
			}
		});
		for (Goal goal : availableGoals) {
			if (goal == currentGoal) {
				break;
			}
			if (agent.contains(goal.getGoalState())) {
				continue;
			}
			return goal;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean shouldReplaceCurrentPlan(Plan<Agent> plan) {
		if (plan == null)
			return false;
		if (currentPlan == null)
			return true;
		return ((Comparable<Plan<Agent>>) currentPlan).compareTo(plan) < 0 && !currentPlan.equals(plan);
	}

	private void switchPlanTo(Goal goal, Plan<Agent> plan) {
		currentGoal = goal;
		currentPlan = plan;
	}

	@Override
	public void update() {
		updatePlan();
	}

	private void updatePlan() {
		replan();
		if (currentPlan == null) {
			return;
		}
		if (!currentGoal.shouldContinue()) {
			resetPlan();
			return;
		}
		currentPlan.update(agent);
		if (currentPlan.isComplete()) {
			if (currentPlan instanceof ActionPlan) {
				agent.apply(((ActionPlan) currentPlan).getWorldStateChanges());
			}
			resetPlan();
		}
	}

	@Override
	public void registerAction(Action action) {
		availableActions.add(action);
	}

	@Override
	public void registerGoal(Goal goal) {
		availableGoals.add(goal);
	}
}
