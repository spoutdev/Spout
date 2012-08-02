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
package org.spout.api.tickable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class LogicRunnableTest {

	@Test
	public void testPriorities() {

		// Create a list for sorting
		List<TestRunnable> runnables = new ArrayList<TestRunnable>(2);
		BasicTickable parent = new BasicTickable();

		// Put everything in backwards
		TestRunnable lowest = new TestRunnable(parent, LogicPriority.LOWEST);
		runnables.add(lowest);

		TestRunnable low = new TestRunnable(parent, LogicPriority.LOW);
		runnables.add(low);

		TestRunnable normal = new TestRunnable(parent, LogicPriority.NORMAL);
		runnables.add(normal);

		TestRunnable high = new TestRunnable(parent, LogicPriority.HIGH);
		runnables.add(high);

		TestRunnable highest = new TestRunnable(parent, LogicPriority.HIGHEST);
		runnables.add(highest);

		// Sort and everything should be in the right position now
		Collections.sort(runnables);
		assertEquals(highest, runnables.get(0));
		assertEquals(high, runnables.get(1));
		assertEquals(normal, runnables.get(2));
		assertEquals(low, runnables.get(3));
		assertEquals(lowest, runnables.get(4));
	}

	private class TestRunnable extends LogicRunnable<BasicTickable> {
		public TestRunnable(BasicTickable parent, LogicPriority priority) {
			super(parent, priority);
		}

		@Override
		public boolean shouldRun(float dt) {
			return false;
		}

		@Override
		public void run() {
		}
	}
}
