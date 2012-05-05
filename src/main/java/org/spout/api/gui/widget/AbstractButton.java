package org.spout.api.gui.widget;

import java.awt.Point;

import org.spout.api.gui.MouseButton;
import org.spout.api.gui.TextProperties;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.plugin.Plugin;

public abstract class AbstractButton extends AbstractControl implements Button {
	private TextProperties textProperties = new TextProperties();
	private String text;
	private boolean down = false;
	private boolean checked = false;
	private boolean checkable = false;
	private int timeout = -1;

	public AbstractButton(String text, Plugin plugin) {
		super(plugin);
		setText("");
	}
	
	public AbstractButton(Plugin plugin) {
		this("", plugin);
	}

	@Override
	public TextProperties getTextProperties() {
		return textProperties;
	}

	@Override
	public Label setTextProperties(TextProperties p) {
		this.textProperties = p;
		return this;
	}

	@Override
	public Label setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isDown() {
		return down;
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public Button setChecked(boolean check) {
		if(isCheckable()) {
			this.checked = check;
		}
		return this;
	}

	@Override
	public Button setCheckable(boolean checkable) {
		if(!checkable && checked) {
			setChecked(false);
		}
		this.checkable = checkable;
		return this;
	}

	@Override
	public boolean isCheckable() {
		return checkable;
	}

	@Override
	public Button click() {
		setChecked(!isChecked());
		return this;
	}

	@Override
	public Button clickLong(int ticks) {
		down = true;
		timeout = ticks;
		return this;
	}

	@Override
	public void onTick() {
		if(timeout >= 0) {
			if(timeout == 0) {
				down = false;
				click();
			}
			timeout --; //if timeout == 0, this will be -1, so it returns into valid state after that...
		}
	}

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		if(button == MouseButton.LEFT_BUTTON) {
			down = true;
		}
		super.onMouseDown(position, button);
	}

	@Override
	public void onMouseMove(Point from, Point to) {
		if(!getGeometry().contains(to)) {
			down = false;
		}
		super.onMouseMove(from, to);
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		if(button == MouseButton.LEFT_BUTTON && isDown()) {
			down = false;
			click();
		}
		super.onMouseUp(position, button);
	}

	@Override
	public void onKeyPress(Keyboard key) {
		if(key == Keyboard.KEY_SPACE) {
			down = true;
		}
		if(key == Keyboard.KEY_RETURN) {
			click();
		}
		super.onKeyPress(key);
	}

	@Override
	public void onKeyRelease(Keyboard key) {
		if(key == Keyboard.KEY_ESCAPE) {
			down = false;
		}
		if(key == Keyboard.KEY_SPACE && isDown()) {
			down = false;
			click();
		}
		super.onKeyRelease(key);
	}
}
