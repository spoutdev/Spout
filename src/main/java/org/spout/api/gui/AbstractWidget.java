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
import javax.xml.bind.TypeConstraintException;
import org.spout.api.Spout;
import org.spout.api.plugin.Plugin;
import org.spout.api.util.Color;

/**
 * A generic Widget will display a border and background, but it is up to any
 * children to extend that with more options.
 * Extend AbstractAttr to provide utility methods for attribute changing.
 */
public abstract class AbstractWidget extends AbstractAttr implements Widget {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 5L;
	/** Used for generating unique ids, numbers below 16 are reserved for static widgets. */
	private static int lastId = 16;
	/** Identify ourselves. */
	private int id = -1;
	/** The parent container, may be a screen. */
	private Container parent = null;
	/** Attributes, all values are Object, even for primitives. */
	private final Map<WidgetAttr, Object> attributes;
	/** All dirty attributes. */
	private final Set<WidgetAttr> dirtyAttr = EnumSet.noneOf(WidgetAttr.class);

	/**
	 * Create a new widget initialized from the specified AbstractWidget.
	 * @param from the widget from which to initialize this AbstractWidget
	 */
	public AbstractWidget(final AbstractWidget from) {
		attributes = new EnumMap(from.attributes);
	}

	/**
	 * Create a new widget with no default values.
	 */
	public AbstractWidget() {
		attributes = new EnumMap<WidgetAttr, Object>(WidgetAttr.class);
	}

	/**
	 * Create a new widget with default values.
	 * @param width the default width
	 * @param height the default height
	 */
	public AbstractWidget(final int width, final int height) {
		this();
		this.setAttr(WidgetAttr.WIDTH, width);
		this.setAttr(WidgetAttr.HEIGHT, height);
	}

	/**
	 * Create a new widget with default values.
	 * @param left the default left
	 * @param top the default top
	 * @param width the default width
	 * @param height the default height
	 */
	public AbstractWidget(final int left, final int top, final int width, final int height) {
		this(width, height);
		this.setAttr(WidgetAttr.LEFT, left);
		this.setAttr(WidgetAttr.TOP, top);
	}

	@Override
	public int getVersion() {
		return (int) serialVersionUID + WidgetAttr.values().length;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.WIDGET;
	}

	@Override
	public int getNumBytes() {
		int size = WidgetHandler.getNumBytes(dirtyAttr.size());
		for (WidgetAttr attr : dirtyAttr) {
			final Class type = attr.getType();
			if (type == null) {
				continue;
			}
			if (type.equals(Integer.class)) {
				size += WidgetHandler.getNumBytes((Integer) getAttr(attr));
			} else if (type.equals(String.class)) {
				size += WidgetHandler.getNumBytes((String) getAttr(attr));
			} else if (type.equals(Color.class)) {
				size += WidgetHandler.getNumBytes((Color) getAttr(attr));
			} else if (type.equals(Plugin.class)) {
				size += WidgetHandler.getNumBytes(((Plugin) getAttr(attr)).getDescription().getName(), true);
			} else {
				// throw unknown type
			}
		}
//		return 48 + PacketUtil.getNumBytes(tooltip) + PacketUtil.getNumBytes(plugin == null ? PLUGIN : plugin);
		return size;
	}

	@Override
	public void readData(final DataInputStream input) throws IOException {
		readAttr(WidgetAttr.class, attributes, input);
	}

	@Override
	public void writeData(final DataOutputStream output) throws IOException {
		writeAttr(attributes, dirtyAttr, output);
	}

	@Override
	public Widget setAttr(final Attr key, final Object... values) {
		if (key instanceof WidgetAttr) {
			setAttr(attributes, dirtyAttr, (WidgetAttr) key, values[0]);
		} else {
			throw new TypeConstraintException("Unknown attribute: " + key.toString());
		}
		return this;
	}

	@Override
	public Object getAttr(final Attr key, final Object def) {
		if (key instanceof WidgetAttr) {
			return getAttr(attributes, (WidgetAttr) key, def);
		} else {
			throw new TypeConstraintException("Unknown attribute: " + key.toString());
		}
	}

	@Override
	public Object getAttr(final Attr key) {
		return getAttr(key, null);
	}

	@Override
	public boolean hasAttr(final Attr key) {
		if (key instanceof WidgetAttr) {
			return hasAttr(attributes, (WidgetAttr) key);
		} else {
			throw new TypeConstraintException("Unknown attribute: " + key.toString());
		}
	}

	/**
	 * Set the default widget id, this must be a number lower than 255 for
	 * static widget ids.
	 * @param id to use
	 */
	protected void setUID(final int id) {
		if (id >= 0xf) {
			throw new UnsupportedOperationException("Static widget ids need to be under 16.");
		}
		this.id = id;
	}

	@Override
	public final int getUID() {
		if (id == -1) {
			id = lastId++;
		}
		return id;
	}

	@Override
	public int hashCode() {
		return getUID();
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof Widget && other.hashCode() == hashCode();
	}

	@Override
	public void onTick() {
	}

	@Override
	public Container getParent() {
		return parent;
	}

	@Override
	public boolean hasParent() {
		return getParent() != null;
	}

	@Override
	public void setParent(final Container parent) {
		if (hasParent() && parent != null && !getParent().equals(parent)) {
			getParent().removeChild(this);
		}
		this.parent = parent;
	}

	@Override
	public Widget updateSize() {
		if (hasParent()) {
			getParent().deferSize();
		}
		return this;
	}

	@Override
	public Container getScreen() {
		Container found = null;
		if (hasParent()) {
			if (getParent() instanceof Screen) {
				found = getParent();
			} else {
				found = getParent().getScreen();
			}
		}
		return found;
	}

	@Override
	public boolean hasScreen() {
		return getScreen() != null;
	}

	@Override
	public boolean hasPosition() {
		return hasAttr(WidgetAttr.TOP)
				|| hasAttr(WidgetAttr.RIGHT)
				|| hasAttr(WidgetAttr.BOTTOM)
				|| hasAttr(WidgetAttr.LEFT);
	}

	@Override
	public boolean hasSize() {
		return hasAttr(WidgetAttr.WIDTH)
				|| hasAttr(WidgetAttr.HEIGHT)
				|| hasAttr(WidgetAttr.MIN_WIDTH)
				|| hasAttr(WidgetAttr.MAX_WIDTH)
				|| hasAttr(WidgetAttr.MIN_HEIGHT)
				|| hasAttr(WidgetAttr.MAX_HEIGHT);
	}
}
