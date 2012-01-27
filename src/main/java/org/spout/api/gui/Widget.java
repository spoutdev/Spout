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
import org.spout.api.ClientOnly;

/**
 * This is the base class of all other widgets, and should never be used
 * directly.
 *
 * If you subclass this for a custom type of widget then you must make sure that
 * isDirty() always returns false otherwise the widget will try to be sent to
 * the client and will cause an exception to be thrown.
 */
public interface Widget {

	/**
	 * The number of bytes of data serialized when sending or receiving data.
	 * @return number of bytes
	 */
	int getNumBytes();

	/**
	 * The version this widget is. Mismatched versions will fail to be created.
	 * @return version
	 */
	int getVersion();

	/**
	 * The type of widget this is. Required for proper synchronization between
	 * the server and client.
	 * @return this type
	 */
	WidgetType getType();

	/**
	 * Returns a unique id for this widget.
	 * @return id
	 */
	int getUID();

	/**
	 * Called after this widget this created for serialization.
	 * @param input network stream
	 * @throws IOException on network error
	 */
	void readData(DataInputStream input) throws IOException;

	/**
	 * Called when this widget is serialized to the client.
	 * Note: ensure that any changes here are reflected in {@link getNumBytes()}
	 * and are also present on the client.
	 * @param output network stream
	 * @throws IOException on network error
	 */
	void writeData(DataOutputStream output) throws IOException;

	/**
	 * Marks this widget as needing an update on the client. It will be updated
	 * after the next onTick call, and marked as setDirty(false) Every widget is
	 * dirty immediately after creation
	 * @param dirty if it should be sent again
	 */
	void setDirty(boolean dirty);

	/**
	 * Is true if this widget has been marked dirty.
	 * @return dirty
	 */
	boolean isDirty();

	/**
	 * Called each tick this widget is updated. This widget is processed for
	 * isDirty() immediately afterwords.
	 */
	void onTick();

	/**
	 * Gets the parent of this widget, or null if unattached.
	 * @return parent or null
	 */
	Container getParent();

	/**
	 * Check if this widget has a parent or is unattached.
	 * @return if it has a parent
	 */
	boolean hasParent();

	/**
	 * Sets the parent for this widget.
	 * @param parent the container parent
	 */
	void setParent(Container parent);

	/**
	 * Get the screen this widget is attached to.
	 * @return screen or null
	 */
	Container getScreen();

	/**
	 * Check if this widget is connected to a screen.
	 * @return if connected
	 */
	boolean hasScreen();

	/**
	 * Called when any dimension or limit changes.
	 * @return this
	 */
	Widget updateSize();

	/**
	 * Returns true if the widget has had it's position set.
	 * @return true if it has a position
	 */
	boolean hasPosition();

	/**
	 * Returns true if a widget has had it's size set.
	 * @return if it has a size
	 */
	boolean hasSize();

	/**
	 * Set a single attribute.
	 * @param key the attribute to set
	 * @param values the values to use, for normal attributes only the first is
	 * used, but some "shortcut" attributes can take multiple values
	 * @return this
	 */
	Widget setAttr(Attr key, Object... values);

	/**
	 * Get an attribute but return our own default if it doesn't exist.
	 * @param key the attribute to get
	 * @param def the default if it doesn't exist
	 * @return the value
	 */
	Object getAttr(Attr key, Object def);

	/**
	 * Get an attribute, return null if not found.
	 * @param key the attribute to get
	 * @return the value
	 */
	Object getAttr(Attr key);

	/**
	 * Check if an attribute has been set.
	 * @param key the attribute to check
	 * @return if it exists
	 */
	boolean hasAttr(Attr key);

	/**
	 * Render the widget on the screen.
	 */
	@ClientOnly
	void render();
}
