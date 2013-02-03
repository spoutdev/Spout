/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine.gui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.spout.api.component.BaseComponentHolder;
import org.spout.api.component.Component;
import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.event.player.input.PlayerMouseMoveEvent;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Focusable;
import org.spout.api.gui.RenderPartContainer;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.ControlComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.IntVector2;
import org.spout.api.math.Rectangle;

import org.spout.engine.batcher.SpriteBatch;

public class SpoutWidget extends BaseComponentHolder implements Widget {
	private List<RenderPart> renderPartCache = new LinkedList<RenderPart>();
	private boolean renderCacheClean = false;
	private boolean dirty = true;
	private SpriteBatch batcher = new SpriteBatch();
	private Screen screen = null;
	private Rectangle hitBox = Rectangle.ZERO;
	private Transform transform = new Transform();

	/**
	 * Returns a sorted list of render parts that consists of all render parts of the components
	 * @return a list of render parts
	 */
	public List<RenderPart> getRenderParts() {
		synchronized (renderPartCache) {
			if (!renderCacheClean) {
				renderPartCache = new LinkedList<RenderPart>();

				for (Component component : values()) {
					if (component instanceof RenderPartContainer) {
						RenderPartContainer c = (RenderPartContainer) component;
						renderPartCache.addAll(c.getRenderParts());
					}
				}

				Collections.sort(renderPartCache);

				renderCacheClean = true;
			}
			return renderPartCache;
		}
	}
	
	public void render() {
		if (dirty) {
			batcher.flush(getRenderParts());
			dirty = false;
		}
		
		batcher.render(transform.toMatrix());
	}

	/**
	 * Invokes a render update in the next frame
	 */
	public void update() {
		dirty = true;
		synchronized (renderPartCache) {
			renderCacheClean = false;
		}
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public Screen getScreen() {
		return screen;
	}


	@Override
	public Transform getTransform() {
		return transform;
	}
	
	public boolean canFocus() {
		return get(ControlComponent.class) != null;
	}

	public boolean isFocused() {
		return screen.getFocusedWidget().equals(this);
	}

	public void onFocusLost() {
		for (Component c : values()) {
			if (c instanceof Focusable) {
				((Focusable) c).onFocusLost();
			}
		}
	}

	public void setHitBox(Rectangle hitBox) {
		this.hitBox = hitBox;
	}

	public Rectangle getHitBox() {
		return hitBox;
	}

	@Override
	public void onClicked(PlayerClickEvent event) {
		for (Component c : values()) {
			if (c instanceof Focusable) {
				((Focusable) c).onClicked(event);
			}
		}
	}

	@Override
	public void onKey(PlayerKeyEvent event) {
		for (Component c : values()) {
			if (c instanceof Focusable) {
				((Focusable) c).onKey(event);
			}
		}
	}

	@Override
	public void onMouseMove(PlayerMouseMoveEvent event) {
		for (Component c : values()) {
			if (c instanceof Focusable) {
				((Focusable) c).onMouseMove(event);
			}
		}
	}

	public void onFocus(FocusReason reason) {
		for (Component c : values()) {
			if (c instanceof Focusable && ((Focusable) c).canFocus()) {
				((Focusable) c).onFocus(reason);
			}
		}
	}

	@Override
	public void onTick(float dt) {
		for (Component c : values()) {
			c.onTick(dt);
		}
	}

	@Override
	public boolean canTick() {
		return true;
	}

	@Override
	public void tick(float dt) {
		if (canTick()) {
			onTick(dt);
		}
	}
}
