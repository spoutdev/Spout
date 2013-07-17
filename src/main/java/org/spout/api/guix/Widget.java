package org.spout.api.guix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spout.api.event.player.input.PlayerClickEvent;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.geo.discrete.Transform2D;
import org.spout.api.math.IntVector2;
import org.spout.api.math.Rectangle;
import org.spout.api.tickable.Tickable;

/**
 * Represents an element on a {@link Screen}.
 */
public abstract class Widget implements Comparable<Widget>, Tickable {
	protected Screen screen;
	private Rectangle bounds = Rectangle.ZERO;
	private Transform2D transform = new Transform2D();
	private int zIndex = 0;

	// internal helper fields
	private int clicks = 0;
	private float clickTimer = 0.5f;


	private boolean grabbed = false;

	public List<Sprite> getSprites() {
		return Collections.emptyList();
	}

	/**
	 * Returns the hit box of this widget.
	 *
	 * @return hit box
	 */
	public final Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Sets the hit box of this widget.
	 *
	 * @param bounds of widget
	 */
	public final void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	/**
	 * Returns the transform of the widget.
	 *
	 * @return transform
	 */
	public final Transform2D getTransform() {
		return transform;
	}

	/**
	 * Returns the z index of the widget on the screen.
	 *
	 * @return z index
	 */
	public final int getZIndex() {
		return zIndex;
	}

	/**
	 * Sets the z index of this widget
	 *
	 * @param zIndex of widget
	 */
	public final void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	/**
	 * Notifies widget of focus.
	 */
	public final void focus() {
		onFocus();
	}

	/**
	 * Called when the Widget focuses.
	 */
	public void onFocus() {
	}

	/**
	 * Notifies widget of being blurred.
	 */
	public final void blur() {
		onBlur();
	}

	/**
	 * Called when the Widget is blurred.
	 */
	public void onBlur() {
	}

	private void resetClickCounter() {
		clicks = 0;
		clickTimer = 0.5f;
	}

	/**
	 * Notifies the widget of being clicked.
	 *
	 * @param event of click
	 */
	public final void click(PlayerClickEvent event) {
		// clicks are the amount of clicks within a 0.5 sec period
		clicks++;
		if (clicks == 2) onDoubleClick(event);
		if (clicks == 3) onTripleClick(event);

		// TODO: handle drag and drops

		onClick(event);
	}

	/**
	 * Called when the widget is clicked.
	 *
	 * @param event of click
	 */
	public void onClick(PlayerClickEvent event) {
	}

	/**
	 * Called when there are two clicks within a 0.5 second period.
	 *
	 * @param event of click
	 */
	public void onDoubleClick(PlayerClickEvent event) {
	}

	/**
	 * Called when there are three clicks within a 0.5 second period.
	 *
	 * @param event of click
	 */
	public void onTripleClick(PlayerClickEvent event) {
	}

	/**
	 * Notifies the widget of being keyed.
	 *
	 * @param event of key press/release
	 */
	public final void key(PlayerKeyEvent event) {
		onKey(event);
	}

	/**
	 * Called when the widget is keyed.
	 *
	 * @param event of key
	 */
	public void onKey(PlayerKeyEvent event) {
	}

	/**
	 * Called when the mouse is moved.
	 *
	 * @param from the location of mouse before this event
	 * @param to the location of mouse after this event
	 * @param hovered if the mouse is hovered over this widget
	 */
	public final void mouseMove(IntVector2 from, IntVector2 to, boolean hovered) {
		// handle drags
		// TODO: Need something like isMouseDown independent of events
		onMouseMove(from, to, hovered);
	}

	/**
	 * Called when the mouse is moved.
	 *
	 * @param from the location of mouse before this event
	 * @param to the location of mouse after this event
	 * @param hovered if the mouse is hovered over this widget
	 */
	public void onMouseMove(IntVector2 from, IntVector2 to, boolean hovered) {
	}

	/**
	 * Called when this widget is "dragged" from it's position to another.
	 *
	 * @param from original location
	 * @param to new location
	 */
	public void onDrag(IntVector2 from, IntVector2 to) {
	}

	/**
	 * Called when the widget is "dropped" after being "dragged".
	 *
	 * @param at where it was dropped.
	 */
	public void onDrop(IntVector2 at) {
	}

	@Override
	public void onTick(float dt) {
	}

	@Override
	public boolean canTick() {
		return screen != null;
	}

	@Override
	public final void tick(float dt) {
		if (!canTick()) {
			return;
		}

		if (clicks > 0) {
			clickTimer -= dt;
			if (clickTimer <= 0) {
				resetClickCounter();
			}
		}

		onTick(dt);
	}

	@Override
	public int compareTo(Widget o) {
		return zIndex - o.zIndex;
	}
}
