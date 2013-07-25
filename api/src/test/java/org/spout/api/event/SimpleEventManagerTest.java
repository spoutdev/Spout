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
package org.spout.api.event;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.spout.api.exception.EventException;

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

		HandlerList.unregisterAll();
	}

	@Test
	public void testSubEventCalling() {
		final EventManager eventManager = new SimpleEventManager();
		final TestSubListener testListener = new TestSubListener();
		eventManager.registerEvents(testListener, this);
		eventManager.callEvent(new TestEvent());
		eventManager.callEvent(new TestSubEvent());

		assertEquals(2, testListener.getParentCallCount());
		assertEquals(1, testListener.getChildCallCount());
	}

	@Test
	public void testEventPriorities() {
		final EventManager eventManager = new SimpleEventManager();
		final List<Order> calledOrders = new ArrayList<Order>();
		for (final Order order : Order.values()) {
			eventManager.registerEvent(TestEvent.class, order, new EventExecutor() {
				@Override
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

class TestEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}

class TestSubEvent extends TestEvent {
	private static final HandlerList HANDLERS = new HandlerList(TestEvent.getHandlerList());

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}

class TestSubListener implements Listener {
	private int parentCallCount = 0;
	private int childCallCount = 0;

	@EventHandler
	public void onTestEvent(TestEvent event) {
		parentCallCount++;
	}

	@EventHandler
	public void onTestSubEvent(TestSubEvent event) {
		childCallCount++;
	}

	public int getParentCallCount() {
		return parentCallCount;
	}

	public int getChildCallCount() {
		return childCallCount;
	}
}

class TestListener implements Listener {
	private boolean hasBeenCalled = false;

	@EventHandler(order = Order.DEFAULT)
	public void onTestEvent(TestEvent event) {
		hasBeenCalled = true;
	}

	public boolean hasBeenCalled() {
		return hasBeenCalled;
	}
}

