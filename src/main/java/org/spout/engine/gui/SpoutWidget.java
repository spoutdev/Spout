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
import org.spout.api.geo.discrete.Transform2D;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Focusable;
import org.spout.api.gui.RenderPartContainer;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.ControlComponent;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.math.IntVector2;
import org.spout.api.math.Rectangle;

import org.spout.engine.batcher.SpriteBatch;

public class SpoutWidget extends BaseComponentHolder implements Widget {
	private List<RenderPartPack> renderPartCache = new LinkedList<RenderPartPack>();
	private boolean renderCacheClean = false;
	private boolean dirty = true;
	private SpriteBatch batcher = new SpriteBatch();
	private Screen screen = null;
	private Rectangle hitBox = Rectangle.ZERO;
	private Transform2D transform = new Transform2D();

	@Override
	public List<RenderPartPack> getRenderPartPacks() {
		synchronized (renderPartCache) {
			if (!renderCacheClean) {
				renderPartCache = new LinkedList<RenderPartPack>();

				for (Component component : values()) {
					if (component instanceof RenderPartContainer) {
						RenderPartContainer c = (RenderPartContainer) component;
						renderPartCache.addAll(c.getRenderPartPacks());
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
			batcher.flush(getRenderPartPacks());
			dirty = false;
		}
		
		batcher.render(transform.toMatrix());
	}

	@Override
	public void update() {
		dirty = true;
		synchronized (renderPartCache) {
			renderCacheClean = false;
		}
	}

	@Override
	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	@Override
	public Screen getScreen() {
		return screen;
	}


	@Override
	public Transform2D getTransform() {
		return transform;
	}

	@Override
	public boolean canFocus() {
		return get(ControlComponent.class) != null;
	}

	@Override
	public boolean isFocused() {
		return screen.getFocusedWidget().equals(this);
	}

	@Override
	public void onFocusLost() {
		for (Component c : values()) {
			if (c instanceof Focusable) {
				((Focusable) c).onFocusLost();
			}
		}
	}

	@Override
	public void setBounds(Rectangle hitBox) {
		this.hitBox = hitBox;
	}

	@Override
	public Rectangle getBounds() {
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
	public void onMouseMoved(IntVector2 prev, IntVector2 pos, boolean hovered) {
		for (Component c : values()) {
			if (c instanceof Focusable) {
				((Focusable) c).onMouseMoved(prev, pos, hovered);
			}
		}
	}

	@Override
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
