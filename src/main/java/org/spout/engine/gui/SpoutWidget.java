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

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.component.BaseComponentHolder;
import org.spout.api.component.Component;
import org.spout.api.component.type.WidgetComponent;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.ControlComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.map.DefaultedKey;
import org.spout.api.math.Rectangle;

public class SpoutWidget extends BaseComponentHolder implements Widget {
	private List<RenderPart> renderPartCache = new LinkedList<RenderPart>();
	private boolean renderCacheClean = false;
	private Screen screen = null;
	private static DefaultedKey<Rectangle> KEY_GEOMETRY = new DefaultedKey<Rectangle>() {
		@Override
		public Rectangle getDefaultValue() {
			return new Rectangle(0, 0, 1, 1);
		}

		@Override
		public String getKeyString() {
			return "geometry";
		}
	};

	/**
	 * Returns a sorted list of render parts that consists of all render parts of the components
	 * @return a list of render parts
	 */
	public List<RenderPart> getRenderParts() {
		synchronized (renderPartCache) {
			if (!renderCacheClean) {
				renderPartCache = new LinkedList<RenderPart>();

				for (Component component : values()) {
					if (component instanceof WidgetComponent) {
						WidgetComponent wc = (WidgetComponent) component;
						renderPartCache.addAll(wc.getRenderParts());
					}
				}

				Collections.sort(renderPartCache);

				renderCacheClean = true;
			}
			return renderPartCache;
		}
	}

	/**
	 * Invokes a render update in the next frame
	 */
	public void update() {
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

	public boolean canFocus() {
		return get(ControlComponent.class) != null;
	}

	public boolean isFocused() {
		return screen.getFocusedWidget().equals(this);
	}

	public void onFocusLost() {
		for (Component c : values()) {
			if (c instanceof WidgetComponent) {
				((WidgetComponent) c).onFocusLost();
			}
		}
	}

	public void onFocus(FocusReason reason) {
		for (Component c : values()) {
			if (c instanceof WidgetComponent) {
				((WidgetComponent) c).onFocus(reason);
			}
		}
	}

	public Rectangle getTranslatedGeometry() {
		return getGeometry().divide(((Client) Spout.getEngine()).getResolution());
	}

	public Rectangle getGeometry() {
		return getData().get(KEY_GEOMETRY);
	}

	public void setGeometry(Rectangle geometry) {
		getData().put(KEY_GEOMETRY, geometry);
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
