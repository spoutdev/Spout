/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui.widget;

import java.awt.Point;

import org.spout.api.gui.MouseButton;
import org.spout.api.gui.WidgetType;
import org.spout.api.keyboard.Keyboard;
import org.spout.api.signal.Signal;

public class TextField extends AbstractControl {
	
	//Represents the caret or the selection if start and end diverge
	private int selectionStart = 0, selectionEnd = 0;
	private String text;
	
	/**
	 * <dl>
	 * 	<dt>Name:</dt>
	 * 	<dd>returnPressed</dd>
	 * </dl>
	 * <dl>
	 * 	<dt>Called:</dt>
	 * 	<dd>When the user presses ENTER or RETURN</dd>
	 * </dl>
	 * <dl>
	 * 	<dt>Arguments:</dt>
	 * 	<dd>
	 * 		<i>No arguments</i>
	 * 	</dd>
	 * </dl>
	 */
	public static final Signal SIGNAL_RETURN_PRESSED = new Signal("returnPressed");
	
	/**
	 * <dl>
	 * 	<dt>Name:</dt>
	 * 	<dd>textChanged</dd>
	 * </dl>
	 * <dl>
	 * 	<dt>Called:</dt>
	 * 	<dd>Whenever the text changes</dd>
	 * </dl>
	 * <dl>
	 * 	<dt>Arguments:</dt>
	 * 	<dd>
	 * 		<ol>
	 * 			<li>The new string</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 */
	public static final Signal SIGNAL_TEXT_CHANGED = new Signal("textChanged", String.class);
	
	/**
	 * <dl>
	 * 	<dt>Name:</dt>
	 * 	<dd>editingFinished</dd>
	 * </dl>
	 * <dl>
	 * 	<dt>Called:</dt>
	 * 	<dd>When the user has not typed for 200ms or focussed another control or pressed Enter</dd>
	 * </dl>
	 * <dl>
	 * 	<dt>Arguments:</dt>
	 * 	<dd>
	 * 		<ol>
	 * 			<li>The new string</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 */
	public static final Signal SIGNAL_EDITING_FINISHED = new Signal("editingFinished", String.class);
	
	/**
	 * <dl>
	 * 	<dt>Name:</dt>
	 * 	<dd>selectionChanged</dd>
	 * </dl>
	 * <dl>
	 * 	<dt>Called:</dt>
	 * 	<dd>When the selection changed</dd>
	 * </dl>
	 * <dl>
	 * 	<dt>Arguments:</dt>
	 * 	<dd>
	 * 		<ol>
	 * 			<li>Selection start</li>
	 * 			<li>Selection end (including)</li>
	 * 			<li>Selected text</li>
	 * 		</ol>
	 * 	</dd>
	 * </dl>
	 */
	public static final Signal SIGNAL_SELECTION_CHANGED = new Signal("selectionChanged", Integer.class, Integer.class, String.class);
	
	
	{
		registerSignal(SIGNAL_RETURN_PRESSED);
		registerSignal(SIGNAL_TEXT_CHANGED);
		registerSignal(SIGNAL_EDITING_FINISHED);
		registerSignal(SIGNAL_SELECTION_CHANGED);
	}
	
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
