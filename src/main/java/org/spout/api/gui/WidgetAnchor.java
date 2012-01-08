/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

import java.util.HashMap;

/**
 * Widget anchors allow you to place widgets that stick or "anchor" to a point
 * on the screen.
 *
 * A widget's coordinates refer to it's <b>top left</b> corner and anchors
 * change the point they are relative to on the screen.
 *
 * You can choose any of nine points to anchor to, noting that if anchoring to
 * the bottom or right the widget will be offscreen until you set a negative y
 * or x value to "correct" it.
 *
 * When a widget is anchored to any of those points the display will be scaled
 * to the same GUI Scale setting as the client options.
 *
 * The only exception is SCALE (default) which assumes the screen to always be
 * 427x240 and stretches everything widgets to conform.
 *
 * In order to move a set of widgets to a specific anchor it is advised you use
 * a Container, then anchor and move that instead.
 *
 * Widgets are anchored by their top-left corner because the positioning of one
 * widget may rely on another widget, and there is no way to know which widgets
 * are related to each other without using a Container.
 */
public enum WidgetAnchor {

	/**
	 * Anchor the top-left of the widget to the top-left of the display.
	 */
	TOP_LEFT(0),
	/**
	 * Anchor the top-left of the widget to the top-center of the display.
	 *
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth() / 2);
	 *
	 * For multiple widgets being anchored it is advised to use a Container and
	 * to anchor that instead.
	 */
	TOP_CENTER(1),
	/**
	 * Anchor the top-left of the widget to the top-right of the display.
	 *
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth());
	 *
	 * For multiple widgets being anchored it is advised to use a Container and
	 * to anchor that instead.
	 */
	TOP_RIGHT(2),
	/**
	 * Anchor the top-left of the widget to the center-left of the display.
	 *
	 * Vertical correction: widget.shiftYpos(- widget.getHeight() / 2);
	 *
	 * For multiple widgets being anchored it is advised to use a Container and
	 * to anchor that instead.
	 */
	CENTER_LEFT(3),
	/**
	 * Anchor the top-left of the widget to the center of the display.
	 *
	 * This is the anchor used by in-game popups and menus.
	 *
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth() / 2);
	 * Vertical correction: widget.shiftYpos(- widget.getHeight() / 2);
	 *
	 * For multiple widgets being anchored it is advised to use a Container and
	 * to anchor that instead.
	 */
	CENTER_CENTER(4),
	/**
	 * Anchor the top-left of the widget to the center-right of the display.
	 *
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth()); Vertical
	 * correction: widget.shiftYpos(- widget.getHeight() / 2);
	 *
	 * For multiple widgets being anchored it is advised to use a Container and
	 * to anchor that instead.
	 */
	CENTER_RIGHT(5),
	/**
	 * Anchor the top-left of the widget to the bottom-left of the display.
	 *
	 * Vertical correction: widget.shiftYpos(- widget.getHeight());
	 *
	 * For multiple widgets being anchored it is advised to use a Container and
	 * to anchor that instead.
	 */
	BOTTOM_LEFT(6),
	/**
	 * Anchor the top-left of the widget to the bottom-center of the display.
	 *
	 * This is the anchor used by the in-game HUD.
	 *
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth() / 2);
	 * Vertical correction: widget.shiftYpos(- widget.getHeight());
	 *
	 * For multiple widgets being anchored it is advised to use a Container and
	 * to anchor that instead.
	 */
	BOTTOM_CENTER(7),
	/**
	 * Anchor the top-left of the widget to the bottom-right of the display.
	 *
	 * Horizontal correction: widget.shiftXpos(- widget.getWidth()); Vertical
	 * correction: widget.shiftYpos(- widget.getHeight());
	 *
	 * For multiple widgets being anchored it is advised to use a Container and
	 * to anchor that instead.
	 */
	BOTTOM_RIGHT(8),
	/**
	 * Scale the widget to a percentage of the display (default).
	 *
	 * This will stretch the widget as if the client screen has a 427x240 pixel
	 * display. This can result in significant distortion if the player is
	 * full-screen or has changed their window shape from the default.
	 *
	 * NOTE: Do not assume that because it looks ok on your display when testing
	 * that it will look ok for anyone else!!!
	 */
	SCALE(9);

	private final int id;

	WidgetAnchor(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	private static final HashMap<Integer, WidgetAnchor> lookupId = new HashMap<Integer, WidgetAnchor>();

	static {
		for (WidgetAnchor t : values()) {
			lookupId.put(t.getId(), t);
		}
	}

	public static WidgetAnchor getAnchorFromId(int id) {
		return lookupId.get(id);
	}
}