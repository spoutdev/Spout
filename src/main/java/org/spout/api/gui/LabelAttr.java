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

import org.spout.api.util.Color;

/**
 * All attributes for a Gradient widget.
 */
public enum LabelAttr implements Attr {

	/** The text to display (String). */
	TEXT(String.class),
	/** The label color ({@Link Color}). */
	COLOR(Color.class),
	/** Direction to draw the gradient. */
	ALIGN(Align.class, Align.LEFT);

	/** The allowed type of data - Integer.class as default. */
	private final Class type;
	/** The default value - null by default. */
	private final Object def;

	/**
	 * Default type is Integer.
	 */
	private LabelAttr() {
		this(Integer.class, null);
	}

	/**
	 * Set the type explicitely.
	 * @param allow this class and subclasses only
	 */
	private LabelAttr(final Class allow) {
		this(allow, null);
	}

	/**
	 * Set the default value for an int attribute.
	 * @param defValue default value
	 */
	private LabelAttr(final int defValue) {
		this(Integer.class, defValue);
	}

	/**
	 * Set the type and default.
	 * @param allow class
	 * @param defValue default value
	 */
	private LabelAttr(final Class allow, final Object defValue) {
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
