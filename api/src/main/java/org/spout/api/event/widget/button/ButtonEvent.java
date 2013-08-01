package org.spout.api.event.widget.button;

import org.spout.api.event.widget.WidgetEvent;
import org.spout.api.guix.widget.Button;

/**
 * Represents an event that occurs on a {@link Button}.
 */
public abstract class ButtonEvent extends WidgetEvent {
	public ButtonEvent(Button button) {
		super(button);
	}

	@Override
	public Button getWidget() {
		return (Button) super.getWidget();
	}
}
