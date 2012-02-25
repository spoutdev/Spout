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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.spout.api.ChatColor;
import org.spout.api.ClientOnly;

public class Label extends AbstractInline implements Widget {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 5L;
	/** Attributes, all values are Object, even for primitives. */
	private final Map<LabelAttr, Object> attributes = new EnumMap<LabelAttr, Object>(LabelAttr.class);
	/** All dirty attributes. */
	private final Set<LabelAttr> dirtyAttr = EnumSet.noneOf(LabelAttr.class);

	/**
	 * Create a new widget initialized from the specified GenericLabel.
	 * @param from the widget from which to initialize this GenericLabel
	 */
	public Label(final Label from) {
		super(from);
		attributes.putAll(from.attributes);
	}

	/**
	 * Create a new widget with no default values.
	 */
	public Label() {
		super();
	}

	/**
	 * Create a new widget with default values.
	 * @param width the default width
	 * @param height the default height
	 */
	public Label(final int width, final int height) {
		super(width, height);
	}

	/**
	 * Create a new widget with default values.
	 * @param left the default left
	 * @param top the default top
	 * @param width the default width
	 * @param height the default height
	 */
	public Label(final int left, final int top, final int width, final int height) {
		super(left, top, width, height);
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID + LabelAttr.values().length;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.LABEL;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + getNumBytes(attributes, dirtyAttr);
	}

	@Override
	public void readData(final DataInputStream input) throws IOException {
		super.readData(input);
		readAttr(LabelAttr.class, attributes, input);
	}

	@Override
	public void writeData(final DataOutputStream output) throws IOException {
		super.writeData(output);
		writeAttr(attributes, dirtyAttr, output);
	}

	@Override
	public Label setAttr(final Attr key, final Object... values) {
		if (key instanceof GradientAttr) {
			switch ((LabelAttr) key) {
				case TEXT:
					setAttr(attributes, dirtyAttr, LabelAttr.TEXT, values[0]);
					// Need to set the label size too
					break;
				default:
					setAttr(attributes, dirtyAttr, (LabelAttr) key, values[0]);
			}
		} else {
			super.setAttr(key, values);
		}
		return this;
	}

	@Override
	public Object getAttr(final Attr key, final Object def) {
		if (key instanceof LabelAttr) {
			return getAttr(attributes, (LabelAttr) key, def);
		}
		return super.getAttr(key, def);
	}

	@Override
	public boolean hasAttr(final Attr key) {
		if (key instanceof LabelAttr) {
			return hasAttr(attributes, (LabelAttr) key);
		}
		return super.hasAttr(key);
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}

	/**
	 * Gets the height of the text.
	 * @param text the text to check
	 * @return height in pixels
	 */
	public static int getStringHeight(final String text) {
		return getStringHeight(text, 1.0F);
	}

	/**
	 * Gets the height of the text, at the given scale.
	 * @param text the text to check
	 * @param scale of the text, 1.0 is default
	 * @return height in pixels
	 */
	public static int getStringHeight(final String text, final float scale) {
		return (int) (text.split("\n").length * 10 * scale);
	}

	/**
	 * Gets the width of the text.
	 * @param text the text to check
	 * @return width of the text
	 */
	public static int getStringWidth(final String text) {
		return getStringWidth(text, 1.0F);
	}

	/**
	 * Gets the width of the text, at the given scale.
	 * @param text the text to check
	 * @param scale of the text, 1.0 is default
	 * @return width of the text
	 */
	public static int getStringWidth(final String text, final float scale) {
		final int[] characterWidths = new int[]{
			1, 9, 9, 8, 8, 8, 8, 7, 9, 8, 9, 9, 8, 9, 9, 9,
			8, 8, 8, 8, 9, 9, 8, 9, 8, 8, 8, 8, 8, 9, 9, 9,
			4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6,
			7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6,
			3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6,
			6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6,
			6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6,
			8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6,
			9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
			9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9,
			8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7,
			7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1
		};
		final String allowedCharacters = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~?Ã³ÚÔõÓÕþÛÙÞ´¯ý─┼╔µã¶÷‗¹¨ Í▄°úÏÎâßÝ¾·±Ð¬║┐«¼¢╝í½╗";
		int length = 0;
		for (String line : ChatColor.strip(text).split("\n")) {
			int lineLength = 0;
			boolean skip = false;
			for (char ch : line.toCharArray()) {
				if (skip) {
					skip = false;
				} else if (ch == '\u00A7') {
					skip = true;
				} else if (allowedCharacters.indexOf(ch) != -1) {
					lineLength += characterWidths[ch];
				}
			}
			length = Math.max(length, lineLength);
		}
		return (int) (length * scale);
	}
}
