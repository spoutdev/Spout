package org.spout.api.gui.widget;

import java.awt.Point;

import org.spout.api.gui.MouseButton;
import org.spout.api.gui.WidgetType;
import org.spout.api.keyboard.Keyboard;

public class TextField extends AbstractControl {
	
	//Represents the caret or the selection if start and end diverge
	private int selectionStart = 0, selectionEnd = 0;
	private String text;
	
	public TextField(String text) {
		super();
		setText(text);
	}
	
	public TextField() {
		this("");
	}

	@Override
	public WidgetType getWidgetType() {
		return WidgetType.TEXTFIELD;
	}

	@Override
	public void onMouseDown(Point position, MouseButton button) {
		// TODO Auto-generated method stub
		super.onMouseDown(position, button);
	}

	@Override
	public void onMouseMove(Point from, Point to) {
		// TODO Auto-generated method stub
		super.onMouseMove(from, to);
	}

	@Override
	public void onMouseUp(Point position, MouseButton button) {
		// TODO Auto-generated method stub
		super.onMouseUp(position, button);
	}

	@Override
	public void onKeyPress(Keyboard key) {
		super.onKeyPress(key);
		boolean handled = false;
		if(isShiftDown()) {
			if(key == Keyboard.KEY_LEFT) {
				selectionStart --;
				handled = true;
			}
			if(key == Keyboard.KEY_RIGHT) {
				selectionEnd ++;
				handled = true;
			}
			if(key == Keyboard.KEY_UP) {
				selectionStart = 0;
				handled = true;
			}
			if(key == Keyboard.KEY_DOWN) {
				selectionStart = selectionEnd;
				selectionEnd = getText().length();
				handled = true;
			}
			if(selectionStart < 0) {
				selectionStart = 0;
			}
			if(selectionEnd > getText().length()) {
				selectionEnd = getText().length();
			}
		} else {
			if(key == Keyboard.KEY_LEFT) {
				selectionStart --;
				selectionEnd = selectionStart;
				handled = true;
			}
			if(key == Keyboard.KEY_RIGHT) {
				selectionEnd ++;
				selectionStart = selectionEnd;
				handled = true;
			}
			if(key == Keyboard.KEY_UP) {
				selectionStart = selectionEnd = 0;
				handled = true;
			}
			if(key == Keyboard.KEY_DOWN) {
				selectionStart = selectionEnd = getText().length();
				handled = true;
			}
		}
		if(key == Keyboard.KEY_BACK) {
			String text = getText();
			if(isWholeWordModDown()) {
				//TODO delete last word
			} else {
				String newtext = text.substring(0, selectionStart - (selectionStart == selectionEnd? 1 : 0)) + text.substring(selectionEnd, text.length());
				setText(newtext);
			}
			handled = true;
		}
		
		if(!handled) {
			char c = org.lwjgl.input.Keyboard.getEventCharacter();
			String newtext = text.substring(0, selectionStart) + c + text.substring(selectionEnd, text.length());
			setText(newtext);
			selectionStart ++;
			selectionEnd = selectionStart;
		}
	}
	
	@Override
	public void onKeyRelease(Keyboard key) {
		// TODO Auto-generated method stub
		super.onKeyRelease(key);
	}
	
	private boolean isShiftDown() {
		return org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_LSHIFT.getKeyCode()) || org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_RSHIFT.getKeyCode());
	}
	
	private boolean isWholeWordModDown() {
		//TODO decide by operating system
		/**
		 * Windows & Linux has CTRL
		 * Mac OS X has ALT
		 */
		return org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_LMENU.getKeyCode()) || org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_RMENU.getKeyCode());
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}


	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

}
