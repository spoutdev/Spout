/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.unchecked.api.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

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
	 * Is this running on Spoutcraft (ie, not on the server) - declared final in
	 * GenericWidget!
	 *
	 * @return if it's running on a client
	 */
	public boolean isSpoutcraft();

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
	public UUID getId();

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
	//	public Plugin getPlugin();

	/**
	 * Internal use only.
	 *
	 * @param plugin
	 * @return this
	 */
	//	public Widget setPlugin(Plugin plugin);

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
	 * Gets the render priority for this widget. Highest priorities render first
	 * (in the background), the lowest priorities render on top (in the
	 * foreground).
	 *
	 * @return priority.
	 */
	public RenderPriority getPriority();

	/**
	 * Sets the render priority for this widget. Highest priorities render first
	 * (in the background), the lowest priorities render on top (in the
	 * foreground).
	 *
	 * @param priority to render at
	 * @return widget
	 */
	public Widget setPriority(RenderPriority priority);

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
	 * Gets the screen this widget is attached to, or null if unattached
	 *
	 * @return screen
	 */
	//	public Screen getScreen();

	/**
	 * Sets the screen and plugin this widget is attached to. Should not be used
	 * normally, is handled with screen.attachWidget() is called.
	 *
	 * @param screen this is attached to
	 * @param plugin this is attached to
	 * @return widget
	 */
	//	public Widget setScreen(Plugin plugin, Screen screen);

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
	 * Gets the widget's container
	 */
	//	public Container getContainer();

	/**
	 * Does the widget have a container
	 */
	public boolean hasContainer();

	/**
	 * Sets the parant container for this widget
	 */
	//	public void setContainer(Container container);

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

	// NOTE: Margins follow the same order as CSS
	/**
	 * Container Layout - Padding to use for automatic container layout - not
	 * included in dimensions
	 *
	 * @param marginAll
	 * @return
	 */
	public Widget setMargin(int marginAll);

	/**
	 * Container Layout - Padding to use for automatic container layout - not
	 * included in dimensions
	 *
	 * @param marginTopBottom
	 * @param marginLeftRight
	 * @return
	 */
	public Widget setMargin(int marginTopBottom, int marginLeftRight);

	/**
	 * Container Layout - Padding to use for automatic container layout - not
	 * included in dimensions
	 *
	 * @param marginTop
	 * @param marginLeftRight
	 * @param marginBottom
	 * @return
	 */
	public Widget setMargin(int marginTop, int marginLeftRight, int marginBottom);

	/**
	 * Container Layout - Padding to use for automatic container layout - not
	 * included in dimensions
	 *
	 * @param marginTop
	 * @param marginRight
	 * @param marginBottom
	 * @param marginLeft
	 * @return
	 */
	public Widget setMargin(int marginTop, int marginRight, int marginBottom, int marginLeft);

	/**
	 * Container Layout - Padding to use for automatic container layout - not
	 * included in dimensions
	 *
	 * @param marginLeft
	 * @return
	 */
	public Widget setMarginTop(int marginTop);

	/**
	 * Container Layout - Padding to use for automatic container layout - not
	 * included in dimensions
	 *
	 * @param marginLeft
	 * @return
	 */
	public Widget setMarginRight(int marginRight);

	/**
	 * Container Layout - Padding to use for automatic container layout - not
	 * included in dimensions
	 *
	 * @param marginLeft
	 * @return
	 */
	public Widget setMarginBottom(int marginBottom);

	/**
	 * Container Layout - Padding to use for automatic container layout - not
	 * included in dimensions
	 *
	 * @param marginLeft
	 * @return
	 */
	public Widget setMarginLeft(int marginLeft);

	/**
	 * Container Layout - Get the margin used for container layout
	 *
	 * @return
	 */
	public int getMarginTop();

	/**
	 * Container Layout - Get the margin used for container layout
	 *
	 * @return
	 */
	public int getMarginRight();

	/**
	 * Container Layout - Get the margin used for container layout
	 *
	 * @return
	 */
	public int getMarginBottom();

	/**
	 * Container Layout - Get the margin used for container layout
	 *
	 * @return
	 */
	public int getMarginLeft();

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
	 * Container Layout - Save the position for later restoration
	 *
	 * @return
	 */
	public Widget savePos();

	/**
	 * Container Layout - Restore the earlier saved position
	 *
	 * @return
	 */
	public Widget restorePos();

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
}
