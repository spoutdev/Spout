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

import org.spout.api.plugin.Plugin;
import org.spout.api.util.Color;

/**
 * All attributes in a base widget.
 * As every widget inherits from widget, this includes layout and generic drawing
 * information.
 * Think of a widget as a form of HTML/CSS entity, it's not identical, but it's
 * very similar.
 */
public enum WidgetAttr implements Attr {

	/** Widget top offset. */
	TOP,
	/** Widget right offset. */
	RIGHT,
	/** Widget bottom offset. */
	BOTTOM,
	/** Widget left offset. */
	LEFT,
	/** Widget width. */
	WIDTH,
	/** Widget minimum width. */
	MIN_WIDTH,
	/** Widget maximum width. */
	MAX_WIDTH,
	/** Widget height. */
	HEIGHT,
	/** Widget minimum height. */
	MIN_HEIGHT,
	/** Widget maximum height. */
	MAX_HEIGHT,
	/** Tooltip on mouse hover. */
	TOOLTIP(String.class),
	/** Plugin which owns the widget. */
	PLUGIN(Plugin.class),
	/** How to display the widget "box". */
	DISPLAY(Display.class),
	/** What to do when the content overflows the box. */
	OVERFLOW(Overflow.class, Overflow.VISIBLE),
	/** Where to layout. */
	POSITION(Position.class, Position.STATIC),
	/** Whether the widget is drawn, it still takes up space. */
	VISIBLE(Boolean.class, true),
	/** Priority for drawing order. */
	PRIORITY,
	/** Padding top size. */
	PADDING_TOP,
	/** Padding right size. */
	PADDING_RIGHT,
	/** Padding bottom size. */
	PADDING_BOTTOM,
	/** Padding left size. */
	PADDING_LEFT,
	/** Border top size. */
	BORDER_TOP,
	/** Border top color. */
	BORDER_TOP_COLOR(Color.class),
	/** Border top style. */
	BORDER_TOP_STYLE(Border.class, Border.SOLID),
	/** Border right size. */
	BORDER_RIGHT,
	/** Border right color. */
	BORDER_RIGHT_COLOR(Color.class),
	/** Border right style. */
	BORDER_RIGHT_STYLE(Border.class, Border.SOLID),
	/** Border bottom size. */
	BORDER_BOTTOM,
	/** Border bottom color. */
	BORDER_BOTTOM_COLOR(Color.class),
	/** Border bottom style. */
	BORDER_BOTTOM_STYLE(Border.class, Border.SOLID),
	/** Border left size. */
	BORDER_LEFT,
	/** Border left color. */
	BORDER_LEFT_COLOR(Color.class),
	/** Border left style. */
	BORDER_LEFT_STYLE(Border.class, Border.SOLID),
	/** Margin top size. */
	MARGIN_TOP,
	/** Margin right size. */
	MARGIN_RIGHT,
	/** Margin bottom size. */
	MARGIN_BOTTOM,
	/** Margin left size. */
	MARGIN_LEFT,;

	/**
	 * Used for setting the display mode of widgets.
	 */
	public enum Display {

		/** Not shown at all. */
		NONE,
		/** Full width, on it's own line. */
		BLOCK,
		/** Minimum width, inline with other widgets. */
		INLINE;
	}

	/**
	 * Used for setting the border style.
	 */
	public enum Border {

		/** Solid line. */
		SOLID;
	}

	/**
	 * Used for clipping the widget content.
	 */
	public enum Overflow {

		/** Default - render it outside the box. */
		VISIBLE,
		/** Clip the content. */
		HIDDEN
	}

	/**
	 * Used for setting the position of widgets.
	 */
	public enum Position {

		/** Default - in the normal flow. */
		STATIC,
		/** Same position as STATIC, but used as a base location for RELATIVE children. */
		RELATIVE,
		/** Position is set by the X and Y coordinates relative to the first non-STATIC parent. */
		ABSOLUTE,
		/** Position is set by the X and Y coordinates relative to the Screen. */
		FIXED;
	}
	/** The allowed type of data - Integer.class as default. */
	private final Class type;
	/** The default value - null by default. */
	private final Object def;

	/**
	 * Default type is Integer.
	 */
	private WidgetAttr() {
		this(Integer.class, null);
	}

	/**
	 * Set the type explicitely.
	 * @param allow this class and subclasses only
	 */
	private WidgetAttr(final Class allow) {
		this(allow, null);
	}

	/**
	 * Set the default value for an int attribute.
	 * @param defValue default value
	 */
	private WidgetAttr(final int defValue) {
		this(Integer.class, defValue);
	}

	/**
	 * Set the type and default.
	 * @param allow class
	 * @param defValue default value
	 */
	private WidgetAttr(final Class allow, final Object defValue) {
		type = allow;
		def = defValue;
	}

	@Override
	public final Class getType() {
		return type;
	}

	@Override
	public final Object getDefault() {
		return def;
	}
}