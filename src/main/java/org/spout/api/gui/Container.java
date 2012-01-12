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

/**
 * Containers are a specific type of widget that are designed for easy layout
 * and control of other widgets.
 *
 * Containers make use of the various Margin methods to provide space between
 * elements. They also require both a Width and Height in order to actual
 * perform an automatic layout of child elements. This can be used to provide
 * a "group" type container that doesn't change the layout of any children.
 *
 * Most Widget methods that affect position or layout will be passed down to
 * all children when used on the container itself, that includes setDirty().
 *
 * Automatic layout is handled by updateLayout (though you should normally use
 * deferLayout() calls in subclasses). Any widgets that change dimension or
 * position at this stage will also be set as dirty.
 */
public interface Container extends Widget {

	/**
	 * Adds a single widget to a container.
	 * @param child the widget to add
	 * @return this
	 */
	public Container addChild(Widget child);

	/**
	 * Adds a single widget to a container.
	 * @param index the position to insert it, use -1 for append
	 * @param child the widget to add
	 * @return this
	 */
	public Container insertChild(int index, Widget child);

	/**
	 * Adds a list of children to a container.
	 * @param children The widgets to add
	 * @return this
	 */
	public Container addChildren(Widget... children);

	/**
	 * Removes a single widget from this container.
	 * @param child the widget to add
	 * @return this
	 */
	public Container removeChild(Widget child);

	/**
	 * Removes a list of widget from this container.
	 * @param children the widgets to remove
	 * @return this
	 */
	public Container removeChildren(Widget... children);

	/**
	 * Remove all widgets owned by a plugin.
	 * @param plugin owning widgets to remove
	 * @return this
	 */
	public Container removeChildren(Plugin plugin);

	/**
	 * Get a list of widgets inside this container.
	 * @return all direct children
	 */
	public Widget[] getChildren();

	/**
	 * Get a list of all widgets inside this container.
	 * @param deep direct descendents or not
	 * @return list of widgets
	 */
	public Widget[] getChildren(boolean deep);

	/**
	 * Check if a container contains a widget.
	 * @param widget to check
	 * @return if it is a direct child
	 */
	public boolean containsChild(Widget widget);

	/**
	 * Check if a container contains a widget by widget id.
	 * @param id to check
	 * @return if it is a direct child
	 */
	public boolean containsChild(int id);

	/**
	 * Get a child by widget id.
	 * @param id to find
	 * @return widget or null
	 */
	public Widget getChild(int id);

	/**
	 * Get a direct child by widget id.
	 * Unlike the other methods, this allows you to limit the search to only
	 * direct children, and not further down the tree.
	 * @param id to find
	 * @param deep perform a deep search
	 * @return widget or null
	 */
	public Widget getChild(int id, boolean deep);

	/**
	 * Set the automatic layout type for children, triggered by setWidth() or setHeight()
	 * @param type ContainerType.VERTICAL, .HORIZONTAL or .OVERLAY
	 * @return this
	 */
	public Container setLayout(ContainerType type);

	/**
	 * Get the automatic layout type for children
	 * @return the type of container
	 */
	public ContainerType getLayout();

	/**
	 * Force the container to re-layout all non-fixed children.
	 * Unless you specifically need to update the layout at this instant,
	 * you should use use deferLayout() instead.
	 * This will re-position and resize all child elements.
	 * @return this
	 */
	public Container updateLayout();

	/**
	 * Automatically call updateLayout during the next onTick.
	 * This is automatically called when anything changes that would affect the container layout.
	 * NOTE: Subclasses should ensure they don't prevent Container.onTick() from running.
	 * @return this
	 */
	public Container deferLayout();

	/**
	 * Automatically call updateSize during the next onTick.
	 * This is automatically called when anything changes that would affect the container resize.
	 * NOTE: Subclasses should ensure they don't prevent Container.onTick() from running.
	 * @return this
	 */
	public Container deferSize();

	/**
	 * Set the contents alignment.
	 * @return this
	 */
	public Container setAlign(WidgetAnchor anchor);

	/**
	 * Get the contents alignment.
	 * @return
	 */
	public WidgetAnchor getAlign();

	/**
	 * Reverse the drawing order (right to left or bottom to top).
	 * @param reverse Set to reverse direction
	 * @return
	 */
	public Container setReverse(boolean reverse);

	/**
	 * If this is drawing in reverse order.
	 * @return
	 */
	public boolean getReverse();

	/**
	 * Determines if children expand to fill width and height
	 * @param auto
	 * @return this
	 */
	public Container setAuto(boolean auto);

	/**
	 * True if the children will expand to fill width and height
	 * @return
	 */
	public boolean isAuto();
}
