package org.getspout.api.event;

public class TestListener implements Listener {
	private boolean hasBeenCalled = false;

	@EventHandler(event = TestEvent.class, order = Order.DEFAULT)
	public void onTestEvent(TestEvent event) {
		hasBeenCalled = true;
	}

	public boolean hasBeenCalled() {
		return hasBeenCalled;
	}
}
