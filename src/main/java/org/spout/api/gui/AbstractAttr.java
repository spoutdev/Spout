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
import java.util.Map;
import java.util.Set;
import org.spout.api.Spout;
import org.spout.api.plugin.Plugin;
import org.spout.api.util.Color;

/**
 * Utility methods for attributes in widgets.
 */
public abstract class AbstractAttr {

	/** If we have unsynchronised data. */
	private transient boolean dirty = true;

	/**
	 * Provided for Widget use.
	 * @param dirty if changes are pending
	 */
	public void setDirty(final boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Provided for Widget use.
	 * @return if changes are pending
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Set a single attribute and the dirty flag for it.
	 * @param <T> an Enum that also extends Attr
	 * @param attrStore an EnumMap for the attributes
	 * @param attrDirty an EnumSet for the dirty flag
	 * @param key the attribute to set
	 * @param value the value to set it to or null to delete it
	 */
	protected final <T extends Enum & Attr> void setAttr(final Map<T, Object> attrStore, final Set<T> attrDirty,
			final T key, final Object value) {
		// Need to check type constraints
		final Object old = ((Map<T, Object>) attrStore).get(key);
		if ((old != null && !old.equals(value)) || (old == null && value != null)) {
			if (value == null) {
				((Map<T, Object>) attrStore).remove(key);
			} else {
				((Map<T, Object>) attrStore).put(key, value);
			}
			if (attrDirty != null) {
				((Set<T>) attrDirty).add(key);
				setDirty(true);
			}
		}
	}

	/**
	 * Get a single attribute, or the default if not found, or the attribute default if no method default.
	 * @param <T> an Enum that also extends Attr
	 * @param attrStore an EnumMap for the attributes
	 * @param key the attribute to get
	 * @param def the value to return if not found, or null to get the attribute default
	 * @return the value
	 */
	protected final <T extends Enum & Attr> Object getAttr(final Map<T, Object> attrStore,
			final T key, final Object def) {
		// Need to check type constraints
		Object value = ((Map<T, Object>) attrStore).get(key);
		if (value == null) {
			value = def;
			if (value == null) {
				value = key.getDefault();
			}
		}
		return value;
	}

	/**
	 * Check if a single attribute has been set.
	 * @param <T> an Enum that also extends Attr
	 * @param attrStore an EnumMap for the attributes
	 * @param key the attribute to check
	 * @return if set
	 */
	protected final <T extends Enum & Attr> boolean hasAttr(final Map<T, Object> attrStore,
			final T key) {
		return ((Map<T, Object>) attrStore).containsKey(key);
	}

	protected final <T extends Enum & Attr> int sizeData(final Map<T, Object> attrStore, final Set<T> attrDirty) {
		int size = WidgetHandler.getNumBytes(attrDirty.size());
		for (T attr : attrDirty) {
			final Class type = attr.getType();
			if (type == null) {
				continue;
			}
			final Object value = getAttr(attrStore, attr, null);
			if (value == null || !hasAttr(attrStore, attr)) {
				size += WidgetHandler.getNumBytes(-1 - attr.ordinal()); // Fake one's complement
			} else {
				size += WidgetHandler.getNumBytes(attr.ordinal());
				if (type.equals(Integer.class)) {
					size += WidgetHandler.getNumBytes((Integer) value);
				} else if (type.equals(String.class)) {
					size += WidgetHandler.getNumBytes((String) value);
				} else if (type.equals(Color.class)) {
					size += WidgetHandler.getNumBytes((Color) value);
				} else if (type.equals(Plugin.class)) {
					size += WidgetHandler.getNumBytes(((Plugin) value).getDescription().getName(), true);
				}

			}
		}
		return size;
	}

	public final <T extends Enum & Attr> int getNumBytes(final Map<T, Object> attrStore, final Set<T> attrDirty) {
		int size = WidgetHandler.getNumBytes(attrDirty.size());
		for (Attr attr : attrDirty) {
			if (!hasAttr(attrStore, (T) attr)) {
				continue;
			}
			final Class type = attr.getType();
			if (type == null) {
				continue;
			}
			if (type.equals(Integer.class)) {
				size += WidgetHandler.getNumBytes((Integer) getAttr(attrStore, (T) attr, null));
			} else if (type.equals(String.class)) {
				size += WidgetHandler.getNumBytes((String) getAttr(attrStore, (T) attr, null));
			} else if (type.equals(Color.class)) {
				size += WidgetHandler.getNumBytes((Color) getAttr(attrStore, (T) attr, null));
			} else if (type.equals(Plugin.class)) {
				size += WidgetHandler.getNumBytes(((Plugin) getAttr(attrStore, (T) attr, null)).getDescription().getName(), true);
			} else {
				// throw unknown type
			}
		}
		return size;
	}

	protected final <T extends Enum & Attr> void readAttr(final Class<T> clazz, final Map<T, Object> attrStore,
			final DataInputStream input) throws IOException {
		for (int i = WidgetHandler.readInt(input); i > 0; i--) {
			final int ordinal = WidgetHandler.readInt(input);
			if (ordinal < 0) {
				final T attr = clazz.getEnumConstants()[Math.abs(ordinal) - 1]; // Fake one's complement
				setAttr(attrStore, null, attr, null);
			} else {
				final T attr = clazz.getEnumConstants()[ordinal];
				final Class type = attr.getType();
				if (type.equals(Integer.class)) {
					setAttr(attrStore, null, attr, WidgetHandler.readInt(input));
				} else if (type.equals(String.class)) {
					setAttr(attrStore, null, attr, WidgetHandler.readString(input));
				} else if (type.equals(Color.class)) {
					setAttr(attrStore, null, attr, WidgetHandler.readColor(input));
				} else if (type.equals(Plugin.class)) {
					setAttr(attrStore, null, attr, Spout.getGame().getPluginManager().getPlugin(WidgetHandler.readString(input, true)));
				}
			}
		}
	}

	protected final <T extends Enum & Attr> void writeAttr(final Map<T, Object> attrStore, final Set<T> attrDirty,
			final DataOutputStream output) throws IOException {
		WidgetHandler.writeInt(output, attrDirty.size());
		for (T attr : attrDirty) {
			final Object value = getAttr(attrStore, attr, null);
			if (value == null || !hasAttr(attrStore, attr)) {
				WidgetHandler.writeInt(output, -1 - attr.ordinal()); // Fake one's complement
			} else {
				WidgetHandler.writeInt(output, attr.ordinal());
				final Class type = attr.getType();
				if (type.equals(Integer.class)) {
					WidgetHandler.writeInt(output, (Integer) value);
				} else if (type.equals(String.class)) {
					WidgetHandler.writeString(output, (String) value);
				} else if (type.equals(Color.class)) {
					WidgetHandler.writeColor(output, (Color) value);
				} else if (type.equals(Plugin.class)) {
					WidgetHandler.writeString(output, ((Plugin) value).getDescription().getName(), true);
				}
			}
		}
	}
}
