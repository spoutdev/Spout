package org.spout.api.gui;

import java.awt.Point;

import org.spout.api.gui.layout.FreeLayout;
import org.spout.api.gui.widget.AbstractWidget;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.plugin.Plugin;

public class GenericScreen extends AbstractWidget implements Screen {
	
	private Control focussedControl;
	private Layout layout;

	public GenericScreen(Plugin plugin) {
		super(plugin);
		setLayout(new FreeLayout());
	}

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		getLayout().onMouseDown(position, button);
	}

	@Override
	public void onMouseMove(Point position) {
		getLayout().onMouseMove(position);
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		getLayout().onMouseUp(position, button);
	}

	@Override
	public void onKeyPress(Keyboard key) {
		if(focussedControl != null) {
			focussedControl.onKeyPress(key);
		}
	}

	@Override
	public void onKeyRelease(Keyboard key) {
		if(focussedControl != null) {
			focussedControl.onKeyRelease(key);
		}
	}

	@Override
	public Layout getLayout() {
		return layout;
	}

	@Override
	public Container setLayout(Layout layout) {
		this.layout = layout;
		return this;
	}

	@Override
	public void render() {
		getLayout().render();
	}

	@Override
	public Screen setFocussedControl(Control control) {
		this.focussedControl = control;
		return this;
	}

	@Override
	public Control getFocussedControl() {
		return focussedControl;
	}

	@Override
	public Screen getScreen() {
		return this;
	}

	@Override
	public Widget setScreen(Screen screen) {
		return this;
	}
}
