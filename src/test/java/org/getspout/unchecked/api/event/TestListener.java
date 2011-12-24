package org.getspout.unchecked.api.event;

import org.getspout.api.event.EventHandler;
import org.getspout.api.event.Order;

public class TestListener {

	@EventHandler(event = TestEvent.class, priority = Order.DEFAULT)
	public void onTestEvent(TestEvent event) {

	}
}
