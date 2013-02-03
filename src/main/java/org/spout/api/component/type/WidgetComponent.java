/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.component.type;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.spout.api.component.Component;
import org.spout.api.component.ComponentOwner;
import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.event.player.input.PlayerMouseMoveEvent;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Focusable;
import org.spout.api.gui.RenderPartContainer;
import org.spout.api.gui.Widget;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.IntVector2;
import org.spout.api.signal.Signal;
import org.spout.api.signal.SignalInterface;
import org.spout.api.signal.SignalObjectDelegate;
import org.spout.api.signal.SubscriberInterface;

public class WidgetComponent extends Component implements SignalInterface, SubscriberInterface, Focusable, RenderPartContainer {
	private SignalInterface sender = null;
	private SignalObjectDelegate signalDelegate = new SignalObjectDelegate();

	@Override
	public Widget getOwner() {
		return (Widget) super.getOwner();
	}

	@Override
	public List<RenderPart> getRenderParts() {
		return Collections.emptyList(); // Components which decide how this widget is rendered need to reimplement this method
	}

	@Override
	public void onClicked(PlayerClickEvent event) {
	}

	@Override
	public void onKey(PlayerKeyEvent event) {
	}

	@Override
	public boolean canFocus() {
		return true;
	}

	@Override
	public boolean isFocused() {
		return getOwner().isFocused();
	}

	@Override
	public void onFocus(FocusReason reason) {
	}

	@Override
	public void onFocusLost() {
	}

	@Override
	public void onMouseMove(PlayerMouseMoveEvent event) {
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

	protected void emit(Signal signal, Object... args) {
		signalDelegate.emitD(signal, args);
	}

	protected void emit(String signal, Object... args) {
		signalDelegate.emitD(signal, args);
	}
}
