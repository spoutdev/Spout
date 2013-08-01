package org.spout.api.event.widget.button;

import org.spout.api.event.HandlerList;
import org.spout.api.guix.widget.Button;

/**
 * Called when a pressed button has been released.
 */
public class ButtonReleaseEvent extends ButtonEvent {
	private static final HandlerList handlers = new HandlerList();

	public ButtonReleaseEvent(Button button) {
		super(button);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
