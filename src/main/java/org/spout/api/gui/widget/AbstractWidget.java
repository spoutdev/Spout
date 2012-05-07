package org.spout.api.gui.widget;

import java.awt.Rectangle;

import org.spout.api.gui.Layout;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.plugin.Plugin;

public abstract class AbstractWidget implements Widget {
	
	private Rectangle geometry = null, minimumSize = null, maximumSize = null;
	private Layout layout = null;
	private Screen screen = null;

	@Override
	public Rectangle getGeometry() {
		return new Rectangle(geometry);
	}

	@Override
	public Widget setGeometry(Rectangle geometry) {
		this.geometry = new Rectangle(geometry);
		return this;
	}

	@Override
	public Rectangle getMinimumSize() {
		return new Rectangle(minimumSize);
	}

	@Override
	public Rectangle getMaximumSize() {
		return new Rectangle(maximumSize);
	}

	@Override
	public Widget setMinimumSize(Rectangle minimum) {
		this.minimumSize = new Rectangle(minimum);
		return this;
	}

	@Override
	public Widget setMaximumSize(Rectangle maximum) {
		this.maximumSize = new Rectangle(maximum);
		return this;
	}

	@Override
	public Widget setParent(Layout layout) {
		this.layout = layout;
		if(layout != null) {
			setScreen(getParent().getParent().getScreen());	//works even when the container IS a screen, because GenericScreen returns itself in that case.
		}
		return this;
	}

	@Override
	public Layout getParent() {
		return layout;
	}

	@Override
	public Screen getScreen() {
		return screen;
	}

	@Override
	public Widget setScreen(Screen screen) {
		this.screen = screen;
		return this;
	}

	@Override
	public void onTick(float dt) {
		
	}
}
