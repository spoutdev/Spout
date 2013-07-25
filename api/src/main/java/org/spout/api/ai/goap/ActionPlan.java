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
package org.spout.api.ai.goap;

import java.util.Arrays;

import org.spout.api.ai.Agent;
import org.spout.api.ai.Plan;

public class ActionPlan implements Plan<Agent>, Comparable<Plan<Agent>> {
	private final float cost;
	private final WorldState end;
	private Action executing;
	private int index = -1;
	private final Action[] plan;

	ActionPlan(WorldState end, Action[] plan, float cost) {
		this.plan = plan;
		this.end = end;
		this.cost = cost;
		advancePlan();
	}

	private void advancePlan() {
		if (++index >= plan.length) {
			executing = null;
			return;
		}
		executing = plan[index];
		executing.activate();
	}

	@Override
	public int compareTo(Plan<Agent> o) {
		return (int) (this.cost - ((ActionPlan) o).cost);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		ActionPlan other = (ActionPlan) obj;
		if (Float.floatToIntBits(cost) != Float.floatToIntBits(other.cost)) {
			return false;
		}
		if (plan.length != other.plan.length) {
			return false;
		}
		for (int i = 0; i < plan.length; i++) {
			if (plan[i].getClass() != other.plan[i].getClass()) {
				return false;
			}
		}
		return true;
	}

	public WorldState getWorldStateChanges() {
		return end;
	}

	int hashcode = -1;

	@Override
	public int hashCode() {
		if (hashcode != -1) {
			return hashcode;
		}
		final int prime = 31;
		return (hashcode = prime * (prime + Float.floatToIntBits(cost)) + Arrays.hashCode(plan));
	}

	@Override
	public boolean isComplete() {
		return index >= plan.length;
	}

	@Override
	public void update(Agent agent) {
		if (executing == null) {
			return;
		}
		executing.update();
		if (executing.isComplete()) {
			advancePlan();
		}
	}
}