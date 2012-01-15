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
import org.spout.api.plugin.Plugin;
import org.spout.api.util.Color;

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
	 *
	 * @return
	 */
	public int getNumBytes();

	/**
	 * The version this widget is. Mismatched versions will fail to be created.
	 *
	 * @return version
	 */
	public int getVersion();

	/**
	 * The type of widget this is. Required for proper synchronization between
	 * the server and client.
	 *
	 * @return widget type
	 */
	public WidgetType getType();

	/**
	 * Returns a unique id for this widget
	 *
	 * @return id
	 */
	public int getUID();

	/**
	 * Called after this widget this created for serialization.
	 *
	 * @param input
	 * @throws IOException
	 */
	public void readData(DataInputStream input) throws IOException;

	/**
	 * Called when this widget is serialized to the client.
	 *
	 * Note: ensure that any changes here are reflected in {@link getNumBytes()}
	 * and are also present on the client.
	 *
	 * @param output
	 * @throws IOException
	 */
	public void writeData(DataOutputStream output) throws IOException;

	/**
	 * Get's the plugin that attached this widget to the screen, or null if this
	 * screen is unattached.
	 *
	 * @return plugin that attached this widget to the screen
	 */
	public Plugin getPlugin();

	/**
	 * Get's the plugin that attached this widget to the screen, or null if this
	 * screen is unattached.
	 *
	 * @return plugin that attached this widget to the screen
	 */
	public String getPluginName();

	/**
	 * Internal use only.
	 *
	 * @param plugin
	 * @return this
	 */
	public Widget setPlugin(Plugin plugin);

	/**
	 * Internal use only.
	 *
	 * @param plugin
	 * @return this
	 */
	public Widget setPlugin(String name);

	/**
	 * Marks this widget as needing an update on the client. It will be updated
	 * after the next onTick call, and marked as setDirty(false) Every widget is
	 * dirty immediately after creation
	 *
	 * @param dirty
	 */
	public void setDirty(boolean dirty);

	/**
	 * Is true if this widget has been marked dirty
	 *
	 * @return dirty
	 */
	public boolean isDirty();

	/**
	 * Gets the render priority for this widget. Lowest priorities render first
	 * (in the background), the highest priorities render on top (in the
	 * foreground).
	 *
	 * @return priority
	 */
	public byte getPriority();

	/**
	 * Sets the render priority for this widget. Lowest priorities render first
	 * (in the background), the highest priorities render on top (in the
	 * foreground).
	 *
	 * @param priority to render at
	 * @return widget
	 */
	public Widget setPriority(byte priority);

	/**
	 * Gets the width of this widget, in pixels
	 *
	 * @return width
	 */
	public int getWidth();

	/**
	 * Sets the width of this widget, in pixels
	 *
	 * @param width to set
	 * @return widget
	 */
	public Widget setWidth(int width);

	/**
	 * Gets the height of this widget, in pixels
	 *
	 * @return height
	 */
	public int getHeight();

	/**
	 * Sets the height of this widget, in pixels
	 *
	 * @param height to set
	 * @return widget
	 */
	public Widget setHeight(int height);

	/**
	 * Gets the x coordinate of this widget. Widgets (and screens) render from
	 * the top left cornor the screen. 0,0 represents the top left corner.
	 *
	 * @return x-coordinate
	 */
	public int getX();

	/**
	 * Gets the y coordinate of this widget. Widgets (and screens) render from
	 * the top left cornor the screen. 0,0 represents the top left corner.
	 *
	 * @return y-coordinate
	 */
	public int getY();

	/**
	 * Sets the x coordinate of this widget. Widgets (and screens) render from
	 * the top left cornor the screen. 0,0 represents the top left corner.
	 *
	 * @param pos to set
	 * @return widget
	 */
	public Widget setX(int pos);

	/**
	 * Sets the y coordinate of this widget. Widgets (and screens) render from
	 * the top left cornor the screen. 0,0 represents the top left corner.
	 *
	 * @param pos to set
	 * @return widget
	 */
	public Widget setY(int pos);

	/**
	 * Shifts this widget the given number of pixels in the x direction.
	 *
	 * @param x pixels to shift
	 * @return widget
	 */
	public Widget shiftXPos(int x);

	/**
	 * Shifts this widget the given number of pixels in the y direction
	 *
	 * @param y pixels to shift
	 * @return widget
	 */
	public Widget shiftYPos(int y);

	/**
	 * Is true if this widget is visible and rendering on the screen
	 *
	 * @return visible
	 */
	public boolean isVisible();

	/**
	 * Sets the visibility of this widget. If true, it will render normally. If
	 * false, it will not appear on the screen.
	 *
	 * @param enable the visibility
	 * @return widget
	 */
	public Widget setVisible(boolean enable);

	/**
	 * Called each tick this widget is updated. This widget is processed for
	 * isDirty() immediately afterwords.
	 */
	public void onTick();

	/**
	 * Set the widget's tooltip. Returns the current instance of the widget to
	 * make chainable calls.
	 */
	public Widget setTooltip(String tooltip);

	/**
	 * Gets the widget's tooltip
	 */
	public String getTooltip();

	/**
	 * Gets the parent of this widget, or null if unattached.
	 * @return parent or null
	 */
	public Container getParent();

	/**
	 * Check if this widget has a parent or is unattached.
	 * @return if it has a parent
	 */
	public boolean hasParent();

	/**
	 * Sets the parent for this widget.
	 * @param parent the container parent
	 */
	public void setParent(Container parent);

	/**
	 * Get the screen this widget is attached to.
	 * @return screen or null
	 */
	public Container getScreen();

	/**
	 * Check if this widget is connected to a screen.
	 * @return if connected
	 */
	public boolean hasScreen();

	/**
	 * Container Layout - Set whether the widget will be resized with it's
	 * container
	 *
	 * @param fixed if it is a static size
	 * @return the container
	 */
	public Widget setFixed(boolean fixed);

	/**
	 * Container Layout - Whether the widget is fixed size inside it's container
	 *
	 * @return
	 */
	public boolean isFixed();

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
	 * Set the display type of this widget.
	 * @param display how to display it
	 * @return this
	 */
	public Widget setDisplay(Display display);

	/**
	 * Get the display type of this widget.
	 * @return display type
	 */
	public Display getDisplay();

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
	 * Set the overflow clipping of the widget.
	 * @param overflow how to clip the content
	 * @return this
	 */
	public Widget setOverflow(Overflow overflow);

	/**
	 * Get the overflow clipping of this widget.
	 * @return the overflow clipping
	 */
	public Overflow getOverflow();

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

	/**
	 * Set the position of this widget relative to it's parents.
	 * @param position where to display it
	 * @return this
	 */
	public Widget setPosition(Position position);

	/**
	 * Get the position of this widget.
	 * @return the position
	 */
	public Position getPosition();

	/**
	 * Get the margin used by this widget. Margin is the outermost part of the
	 * layout box, and will collapse with other margins.
	 * @return margin
	 */
	public Box getMargin();

	/**
	 * Get the padding used by this widget. Padding is the innermost part of the
	 * layout box, inside the border and margin.
	 * @return padding
	 */
	public Box getPadding();

	/**
	 * Get the border used for this widget. Border is between the Padding and
	 * the Margin, and has a colour associated with it.
	 * @return 
	 */
	public BoxColor getBorder();

	/**
	 * A box outside the widget dimensions, used by Margins and Padding.
	 */
	public interface Box {

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param all edges
		 * @return widget
		 */
		public Widget set(int all);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param topBottom edges
		 * @param leftRight edges
		 * @return widget
		 */
		public Widget set(int topBottom, int leftRight);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param top edge
		 * @param leftRight edges
		 * @param bottom edge
		 * @return widget
		 */
		public Widget set(int top, int leftRight, int bottom);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param top edge
		 * @param right edge
		 * @param bottom edge
		 * @param left edge
		 * @return widget
		 */
		public Widget set(int top, int right, int bottom, int left);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param top edge
		 * @return widget
		 */
		public Widget setTop(int top);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param right edge
		 * @return widget
		 */
		public Widget setRight(int right);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param bottom edge
		 * @return widget
		 */
		public Widget setBottom(int bottom);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param left edge
		 * @return widget
		 */
		public Widget setLeft(int left);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge
		 */
		public int getTop();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge
		 */
		public int getRight();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge
		 */
		public int getBottom();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge
		 */
		public int getLeft();
	}

	/**
	 * A colored box outside the widget dimensions, used for Border.
	 */
	public interface BoxColor {

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param all edges
		 * @return widget
		 */
		public Widget set(int all, Color color);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param topBottom edges
		 * @param leftRight edges
		 * @return widget
		 */
		public Widget set(int topBottom, int leftRight, Color color);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param top edge
		 * @param leftRight edges
		 * @param bottom edge
		 * @return widget
		 */
		public Widget set(int top, int leftRight, int bottom, Color color);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param top edge
		 * @param right edge
		 * @param bottom edge
		 * @param left edge
		 * @return widget
		 */
		public Widget set(int top, int right, int bottom, int left, Color color);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param top edge
		 * @return widget
		 */
		public Widget setTop(int top, Color color);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param right edge
		 * @return widget
		 */
		public Widget setRight(int right, Color color);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param bottom edge
		 * @return widget
		 */
		public Widget setBottom(int bottom, Color color);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @param left edge
		 * @return widget
		 */
		public Widget setLeft(int left, Color color);

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge
		 */
		public int getTop();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge color
		 */
		public Color getTopColor();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge
		 */
		public int getRight();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge color
		 */
		public Color getRightColor();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge
		 */
		public int getBottom();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge color
		 */
		public Color getBottomColor();

		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge
		 */
		public int getLeft();
		/**
		 * Spacing to use for automatic container layout, not included in
		 * widget dimensions.
		 * @return edge color
		 */
		public Color getLeftColor();
	}

	/**
	 * Container Layout - Set the minimum width for this widget
	 *
	 * @param min
	 * @return
	 */
	public Widget setMinWidth(int min);

	/**
	 * Container Layout - Get the minimum width for this widget
	 *
	 * @return
	 */
	public int getMinWidth();

	/**
	 * Container Layout - Set the maximum width for this widget
	 *
	 * @param min
	 * @return
	 */
	public Widget setMaxWidth(int max);

	/**
	 * Container Layout - Get the maximum width for this widget
	 *
	 * @return
	 */
	public int getMaxWidth();

	/**
	 * Container Layout - Set the minimum height for this widget
	 *
	 * @param min
	 * @return
	 */
	public Widget setMinHeight(int min);

	/**
	 * Container Layout - Get the minimum height for this widget
	 *
	 * @return
	 */
	public int getMinHeight();

	/**
	 * Container Layout - Set the maximum height for this widget
	 *
	 * @param min
	 * @return
	 */
	public Widget setMaxHeight(int max);

	/**
	 * Container Layout - Get the maximum height for this widget
	 *
	 * @return
	 */
	public int getMaxHeight();

	/**
	 * Set the anchor point for this widget, default is CENTER
	 *
	 * @param anchor
	 * @return
	 */
	public Widget setAnchor(WidgetAnchor anchor);

	/**
	 * Get the current anchor position
	 *
	 * @return
	 */
	public WidgetAnchor getAnchor();

	/**
	 * Returns a copy of this widget with a new UUID.
	 *
	 * Copies will not be equal to each other, but will have the same internal
	 * data.
	 *
	 * Note: the copy will not be attached to a screen, nor be part of a
	 * container even if the original was.
	 *
	 * Warning: copy will not work on screens.
	 *
	 * @return a copy of this widget
	 */
	public Widget copy();

	/**
	 * Called when any dimension or limit changes
	 *
	 * @return widget
	 */
	public Widget updateSize();

	/**
	 * Sets whether this widget should automatically be marked as dirty when it
	 * is changed.
	 *
	 * @param dirty if it should be automatic (default: true)
	 * @return widget
	 */
	public Widget setAutoDirty(boolean dirty);

	/**
	 * Check whether this widget is automatically being marked as dirty.
	 *
	 * @return if autodirty is on
	 */
	public boolean isAutoDirty();

	/**
	 * Sets the dirty flag automatically is isAutoDirty() returns true.
	 */
	public void autoDirty();

	/**
	 * Setup a simple automatic animation that automatically repeats and resets
	 * when finished. Please note that some animation types are limited to
	 * certain types of widget. All animation is carried out on the client, so
	 * it isn't possible to update the server side values affected by the
	 * animation...
	 *
	 * @param type the type of animation to use
	 * @param value a custom value used by some types (default: 1)
	 * @param count how many frames
	 * @param ticks how many ticks per "frame"
	 * @return widget
	 */
	public Widget animate(WidgetAnim type, float value, short count, short ticks);

	/**
	 * Setup a simple automatic animation that resets when finished. Please note
	 * that some animation types are limited to certain types of widget. All
	 * animation is carried out on the client, so it isn't possible to update
	 * the server side values affected by the animation...
	 *
	 * @param type the type of animation to use
	 * @param value a custom value used by some types (default: 1)
	 * @param count how many frames
	 * @param ticks how many ticks per "frame"
	 * @param repeat should the animation be repeated
	 * @return widget
	 */
	public Widget animate(WidgetAnim type, float value, short count, short ticks, boolean repeat);

	/**
	 * Setup a simple automatic animation. Please note that some animation types
	 * are limited to certain types of widget. All animation is carried out on
	 * the client, so it isn't possible to update the server side values
	 * affected by the animation...
	 *
	 * @param type the type of animation to use
	 * @param value a custom value used by some types (default: 1)
	 * @param count how many frames
	 * @param ticks how many ticks per "frame"
	 * @param repeat should the animation be repeated
	 * @param reset should it reset back to the first frame after finishing
	 * @return widget
	 */
	public Widget animate(WidgetAnim type, float value, short count, short ticks, boolean repeat, boolean reset);

	/**
	 * Start the animation.
	 *
	 * @return widget
	 */
	public Widget animateStart();

	/**
	 * Stop the animation, optionally letting it finish a loop. If the "reset"
	 * option was set when creating the animation it will go back to the first
	 * frame, otherwise it will stop where it is.
	 *
	 * @param finish should it finish the current loop (if repeating)
	 * @return widget
	 */
	public Widget animateStop(boolean finish);

	/**
	 * This handles animation every frame. NOTE: On the server the default
	 * animation handler doesn't do anything as all animation is handled on the
	 * client. If you are writing an animation handler then please keep
	 * bandwidth use in mind...
	 */
	public void onAnimate();

	/**
	 * This is called when the animation stops, and can be used for chaining
	 * together animations. This is called whether the stop was automatic or
	 * manual, and occurs at the start of the final frame (so the frame hasn't
	 * had any ticks of visibility yet). NOTE: On the server the values changed
	 * in the animation <b>will not<b> have changed, this is due to the
	 * animation being client side. If you didn't tell the animation to reset
	 * after finishing then please remember to change them!
	 */
	public void onAnimateStop();

	/**
	 * Returns true if the widget has had it's position set.
	 *
	 * @return true if it has a position
	 */
	public boolean hasPosition();

	/**
	 * Returns true if a widget has had it's size set.
	 *
	 * @return if it has a size
	 */
	public boolean hasSize();

	/**
	 * Render the widget on the screen.
	 */
	@ClientOnly
	public void render();
}
