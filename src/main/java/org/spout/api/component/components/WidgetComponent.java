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
package org.spout.api.component.components;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Widget;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.keyboard.KeyEvent;
import org.spout.api.math.IntVector2;
import org.spout.api.signal.Signal;
import org.spout.api.signal.SignalInterface;
import org.spout.api.signal.SignalObjectDelegate;
import org.spout.api.signal.SubscriberInterface;

public class WidgetComponent extends Component implements SignalInterface, SubscriberInterface {
	private SignalInterface sender = null;
	private SignalObjectDelegate signalDelegate = new SignalObjectDelegate();
	
	@Override
	public Widget getOwner() {
		return (Widget) super.getOwner();
	}
	
	/**
	 * Returns a list of RenderParts that are to be rendered for this widget <br/>
	 * Only called when the widget's internal cache isn't clean, that means, 
	 * you must call getOwner().update() to invoke a render update
	 * @return a list of RenderParts
	 */
	public List<RenderPart> getRenderParts() {
		return Collections.emptyList(); // Components which decide how this widget is rendered need to reimplement this method
	}
	
	/**
	 * Called when this widget was clicked on
	 * @param position the position on the widget (in pixels)
	 * @param mouseDown if the mouse is pressed
	 */
	public void onClicked(IntVector2 position, boolean mouseDown) {
		
	}
	
	/**
	 * Called when this widget is focussed and a key was typed
	 * @param event the key event
	 */
	public void onKey(KeyEvent event) {
		
	}
	
	/**
	 * Called when this widget gains focus
	 * @param reason the reason why this focus was set
	 */
	public void onFocus(FocusReason reason) {
		
	}
	
	/**
	 * Called when this widget loses focus
	 */
	public void onFocusLost() {
		
	}
	
	/**
	 * Called when the mouse moved
	 * @param position the cursor position translated to the widgets geometry (0,0 is top,left of the widget)
	 */
	public void onMouseMove(IntVector2 position) {
		
	}
	
	@Override
	public final boolean attachTo(ComponentOwner owner) {
		if (!(owner instanceof Widget)) {
			throw new IllegalStateException("owner must be a widget");
		}
		return super.attachTo(owner);
	}

	// Implementations for the signal stuff
	
	@Override
	public SignalInterface sender() {
		return sender;
	}

	@Override
	public void setSender(SignalInterface sender) {
		this.sender = sender;
	}

	@Override
	public boolean subscribe(String signal, Object receiver, Method method) {
		return signalDelegate.subscribe(signal, receiver, method);
	}

	@Override
	public boolean subscribe(String signal, Object receiver, String method)
			throws SecurityException, NoSuchMethodException {
		return signalDelegate.subscribe(signal, receiver, method);
	}

	@Override
	public boolean subscribe(Signal signal, Object receiver, Method method) {
		return signalDelegate.subscribe(signal, receiver, method);
	}

	@Override
	public boolean subscribe(Signal signal, Object receiver, String method)
			throws SecurityException, NoSuchMethodException {
		return signalDelegate.subscribe(signal, receiver, method);
	}

	@Override
	public void unsubscribe(String signal, Object receiver) {
		signalDelegate.unsubscribe(signal, receiver);
	}

	@Override
	public void unsubscribe(Object receiver) {
		signalDelegate.unsubscribe(receiver);
	}
	
	protected void registerSignal(Signal signal) {
		signalDelegate.registerSignalD(signal);
	}
	
	protected void emit(Signal signal, Object ...args) {
		signalDelegate.emitD(signal, args);
	}
	
	protected void emit(String signal, Object ...args) {
		signalDelegate.emitD(signal, args);
	}
}
