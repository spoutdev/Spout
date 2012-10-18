package org.spout.api.gui.component;

import java.util.List;

import org.spout.api.gui.render.RenderPart;
import org.spout.api.keyboard.KeyEvent;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.math.IntVector2;
import org.spout.api.signal.Signal;

public class ButtonComponent extends LabelComponent {
	public static final Signal SIGNAL_CLICKED = new Signal("clicked");
	private boolean down = false;
	
	public ButtonComponent () {
		registerSignal(SIGNAL_CLICKED);
	}
	
	@Override
	public void onAttached() {
		getOwner().add(ControlComponent.class);
	}
	
	@Override
	public List<RenderPart> getRenderParts() {
		return super.getRenderParts(); // TODO add text, rectangle for clicked stuff
	}
	
	@Override
	public void onClicked(IntVector2 position, boolean mouseDown) {
		if (mouseDown) {
			down = true;
		} else {
			// TODO detect if mouse button was released outside of the button
			emit(SIGNAL_CLICKED);
			down = false;
		}
	}
	
	@Override
	public void onKey(KeyEvent event) {
		if (event.getKey() == Keyboard.KEY_SPACE) {
			if (event.isPressed()) {
				down = true;
				getOwner().update();
			} else {
				if (isDown()) {
					emit(SIGNAL_CLICKED);
					down = true;
					getOwner().update();
				}
			}
		}
		if (event.getKey() == Keyboard.KEY_ESCAPE) {
			down = false;
			getOwner().update();
		}
	}
	
	public boolean isDown() {
		return down;
	}
}
