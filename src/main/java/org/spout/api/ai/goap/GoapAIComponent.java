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

import java.util.Map;

import org.spout.api.ai.AStarGoal;
import org.spout.api.ai.AStarMachine;
import org.spout.api.ai.Agent;
import org.spout.api.ai.Plan;
import org.spout.api.ai.Sensor;
import org.spout.api.component.entity.AIComponent;
import org.spout.api.entity.Entity;

import com.google.common.collect.Maps;

public class GoapAIComponent extends AIComponent implements PlannerAgent {
	private final AStarMachine<PlannerNode, ActionPlan> machine = AStarMachine.createWithDefaultStorage();
	private final ActionPlanner planner = new SimpleActionPlanner(this);
	private final Map<Class<? extends Sensor>, Sensor> sensors = Maps.newHashMap();
	private WorldState worldState = WorldState.createEmptyState();

	@Override
	public void apply(WorldState changes) {
		worldState.apply(changes);
	}

	@Override
	public boolean contains(WorldState state) {
		return worldState.contains(state);
	}

	@Override
	public Plan<Agent> generatePlan(WorldState to) {
		PlannerNode root = PlannerNode.create(this, worldState);
		AStarGoal<PlannerNode> goal = PlannerGoal.createWithGoalState(to);
		return machine.runFully(goal, root);
	}

	@Override
	public Iterable<Action> getAvailableActions() {
		return planner.getAvailableActions();
	}

	@Override
	public float getCostModifierFor(Action action) {
		return planner.getCostModifierFor(action);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Sensor> T getSensor(Class<T> clazz) {
		return (T) sensors.get(clazz);
	}

	@Override
	public Entity getEntity() {
		return getOwner();
	}

	@Override
	public void onTick(float dt) {
		updateSensors();
		planner.update();
	}

	public void registerAction(Action action) {
		planner.registerAction(action);
	}

	public void registerSensor(Sensor sensor) {
		sensors.put(sensor.getClass(), sensor);
	}

	public void registerGoal(Goal goal) {
		planner.registerGoal(goal);
	}

	private void updateSensors() {
		for (Sensor sensor : sensors.values())
			worldState = worldState.apply(sensor.generateState());
	}
}
