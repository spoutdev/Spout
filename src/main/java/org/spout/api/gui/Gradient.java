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
import org.spout.api.ClientOnly;
import org.spout.api.util.Color;

/**
 * A Gradient is a solid block of one or two colors, the direction of the
 * gradient can be changed. See {@Link GradientAttr} for attributes.
 */
public class Gradient extends AbstractInline implements Widget {

	/**
	 * Current version for serialisation and packet handling.
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * Attributes, all values are Object, even for primitives.
	 */
	private final Map<GradientAttr, Object> attributes = new EnumMap<GradientAttr, Object>(GradientAttr.class);
	/**
	 * All dirty attributes.
	 */
	private final Set<GradientAttr> dirtyAttr = EnumSet.noneOf(GradientAttr.class);

	/**
	 * Create a new widget initialized from the specified GenericGradient.
	 *
	 * @param from the widget from which to initialize this GenericGradient
	 */
	public Gradient(final Gradient from) {
		super(from);
		attributes.putAll(from.attributes);
	}

	/**
	 * Create a new widget with no default values.
	 */
	public Gradient() {
		super();
	}

	/**
	 * Create a new widget with default values.
	 *
	 * @param both the color to set both values
	 */
	public Gradient(final Color both) {
		super();
		setAttr(GradientAttr.COLOR, both);
	}

	/**
	 * Create a new widget with default values.
	 *
	 * @param color1 the first color
	 * @param color2 the second color
	 */
	public Gradient(final Color color1, final Color color2) {
		super();
		setAttr(GradientAttr.COLOR, color1, color2);
	}

	/**
	 * Create a new widget with default values.
	 *
	 * @param width the default width
	 * @param height the default height
	 */
	public Gradient(final int width, final int height) {
		super(width, height);
	}

	/**
	 * Create a new widget with default values.
	 *
	 * @param width the default width
	 * @param height the default height
	 * @param both the color to set both values
	 */
	public Gradient(final int width, final int height, final Color both) {
		super(width, height);
		setAttr(GradientAttr.COLOR, both);
	}

	/**
	 * Create a new widget with default values.
	 *
	 * @param width the default width
	 * @param height the default height
	 * @param color1 the first color
	 * @param color2 the second color
	 */
	public Gradient(final int width, final int height, final Color color1, final Color color2) {
		super(width, height);
		setAttr(GradientAttr.COLOR, color1, color2);
	}

	/**
	 * Create a new widget with default values.
	 *
	 * @param left the default left
	 * @param top the default top
	 * @param width the default width
	 * @param height the default height
	 */
	public Gradient(final int left, final int top, final int width, final int height) {
		super(left, top, width, height);
	}

	/**
	 * Create a new widget with default values.
	 *
	 * @param left the default left
	 * @param top the default top
	 * @param width the default width
	 * @param height the default height
	 * @param both the color to set both values
	 */
	public Gradient(final int left, final int top, final int width, final int height, final Color both) {
		super(left, top, width, height);
		setAttr(GradientAttr.COLOR, both);
	}

	/**
	 * Create a new widget with default values.
	 *
	 * @param left the default left
	 * @param top the default top
	 * @param width the default width
	 * @param height the default height
	 * @param color1 the first color
	 * @param color2 the second color
	 */
	public Gradient(final int left, final int top, final int width, final int height, final Color color1, final Color color2) {
		super(left, top, width, height);
		setAttr(GradientAttr.COLOR, color1, color2);
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID + GradientAttr.values().length;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.GRADIENT;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + getNumBytes(attributes, dirtyAttr);
	}

	@Override
	public void readData(final DataInputStream input) throws IOException {
		super.readData(input);
		readAttr(GradientAttr.class, attributes, input);
	}

	@Override
	public void writeData(final DataOutputStream output) throws IOException {
		super.writeData(output);
		writeAttr(attributes, dirtyAttr, output);
	}

	@Override
	public Gradient setAttr(final Attr key, final Object... values) {
		if (key instanceof GradientAttr) {
			switch ((GradientAttr) key) {
				case COLOR:
					setAttr(attributes, dirtyAttr, GradientAttr.COLOR1, values[0]);
					setAttr(attributes, dirtyAttr, GradientAttr.COLOR2, values[values.length == 2 ? 1 : 0]);
					break;
				default:
					setAttr(attributes, dirtyAttr, (GradientAttr) key, values[0]);
			}
		} else {
			super.setAttr(key, values);
		}
		return this;
	}

	@Override
	public Object getAttr(final Attr key, final Object def) {
		if (key instanceof GradientAttr) {
			return getAttr(attributes, (GradientAttr) key, def);
		}
		return super.getAttr(key, def);
	}

	@Override
	public boolean hasAttr(final Attr key) {
		if (key instanceof GradientAttr) {
			return hasAttr(attributes, (GradientAttr) key);
		}
		return super.hasAttr(key);
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
