package org.spout.api.gui.widget;

import java.awt.Point;

import org.spout.api.gui.Control;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.MouseButton;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.plugin.Plugin;

public abstract class AbstractControl extends AbstractWidget implements Control {
	private boolean enabled = true;

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		setFocus(FocusReason.MOUSE_CLICKED);
	}

	@Override
	public void onMouseMove(Point from, Point to) {

	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {

	}

	@Override
	public void onKeyPress(Keyboard key) {

	}

	@Override
	public void onKeyRelease(Keyboard key) {

	}

	@Override
	public boolean setFocus() {
		return setFocus(FocusReason.GENERIC_REASON);
	}

	@Override
	public boolean setFocus(FocusReason reason) {
		getScreen().setFocussedControl(this);
		return true;
	}

	@Override
	public boolean hasFocus() {
		return getScreen().getFocussedControl() == this;
	}

	@Override
	public Control setEnabled(boolean enable) {
		this.enabled = enable;
		return this;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
