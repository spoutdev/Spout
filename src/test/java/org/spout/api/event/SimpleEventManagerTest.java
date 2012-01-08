/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * The SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.spout.api.event;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zml2008
 */
public class SimpleEventManagerTest {
	protected EventManager eventManager;

	@Before
	public void setUp() {
		eventManager = new SimpleEventManager();
	}

	@Test
	public void testEventCalling() {
		final EventManager eventManager = new SimpleEventManager();
		final TestListener testListener = new TestListener();
		eventManager.registerEvents(testListener, this);
		eventManager.callEvent(new TestEvent());
		assertTrue(testListener.hasBeenCalled());
	}
	
	@Test
	public void testEventPriorities() {
		final EventManager eventManager = new SimpleEventManager();
		final List<Order> calledOrders = new ArrayList<Order>();
		for (final Order order : Order.values()) {
			eventManager.registerEvent(TestEvent.class, order, new EventExecutor() {
				public void execute(Event event) throws EventException {
					calledOrders.add(order);
				}
			}, this);
		}
		eventManager.callEvent(new TestEvent());
		assertEquals(calledOrders.size(), Order.values().length);
		for (Order order : Order.values()) {
			assertTrue("Order not contained in results list! ", calledOrders.indexOf(order) >= 0);
			assertEquals(calledOrders.get(order.getIndex()), order);
		}
	}
}
