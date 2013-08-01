package org.spout.api.event.widget.button;

import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.guix.widget.Button;

/**
 * Called when a button is pressed but the mouse has not yet released the
 * button.
 *
 * @see ButtonReleaseEvent
 */
public class ButtonPressEvent extends ButtonEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	public ButtonPressEvent(Button button) {
		super(button);
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
