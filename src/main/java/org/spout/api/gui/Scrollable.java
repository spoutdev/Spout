/*
 * This file is part of Spout API (http://wiki.getspout.org/).
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
package org.spout.api.gui;

/**
 * This defines a control for a scrollbar.
 */
public interface Scrollable extends Control {

	/**
	 * Gets the inner size for given axis, in pixels.
	 * @param axis of the size vector
	 * @return the size of the viewport on given axis
	 */
	public int getInnerSize(Orientation axis);

	/**
	 * Gets the scroll position, in pixels.
	 * @param axis of the scroll vector
	 * @return the position of the viewport on given axis
	 */
	public int getScrollPosition(Orientation axis);

	/**
	 * Sets the scroll position on the axis, in pixels.
	 * The allowed scroll amount ranges from 0 to getMaximumScrollPosition for given axis.
	 * @param axis the axis to scroll
	 * @param position the position of the viewport on given axis
	 */
	public void setScrollPosition(Orientation axis, int position);

	/**
	 * Adds x and y to the Horizontal and Vertical scroll position.
	 * @param x
	 * @param y
	 */
	public void scroll(int x, int y);

	/**
	 * Adjusts the scroll position so that the given rectangle will fit into the viewport.
	 * If the given rect is too big, it will scroll to the x or y position of the rectangle.
	 * @param rect
	 */
	public void ensureVisible(Rectangle rect);

	/**
	 * Gets the maximum allowed scroll position. 
	 * @param axis
	 * @return the maximum scroll position. Use the return value to scroll to the very bottom.
	 * @returns 0 if no scrolling is possible, that means the whole content would fit into the viewport.
	 */
	public int getMaximumScrollPosition(Orientation axis);

	/**
	 * Depending on the set ScrollBarPolicy, returns wether to show or not to show a scrollbar on the given axis
	 * @param axis to check if it should view a scrollbar
	 * @return if the scrollbar should be shown for that axis
	 */
	public boolean needsScrollBar(Orientation axis);

	/**
	 * Sets the scrollbar policy for the given axis
	 * @param axis
	 * @param policy
	 */
	public void setScrollBarPolicy(Orientation axis, ScrollBarPolicy policy);

	/**
	 * Gets the scrollbar policy for the given axis
	 * @param axis
	 * @return the scrollbar policy for the given axis
	 */
	public ScrollBarPolicy getScrollBarPolicy(Orientation axis);

	/**
	 * Gets the size of the rectangle inside the scrollable. This is usually getWidth/Height() - 16 when the corresponding scrollbar is visible.
	 * @param axis
	 * @return
	 */
	public int getViewportSize(Orientation axis);
	
	/**
	 * Gets the background color of this list
	 * @return color
	 */
	public Color getBackgroundColor();
	
	/**
	 * Sets the background color of this list
	 * @param color to set
	 * @return this
	 */
	public Scrollable setBackgroundColor(Color color);
}
