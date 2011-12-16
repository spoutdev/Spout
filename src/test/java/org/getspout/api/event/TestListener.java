package org.getspout.api.event;

public class TestListener {

	@EventHandler(event = TestEvent.class, priority = Order.DEFAULT)
	public void onTestEvent(TestEvent event) {

	}
}
