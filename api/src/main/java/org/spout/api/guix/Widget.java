package org.spout.api.guix;

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
	private int zIndex = 0, tabIndex = 0;

	// internal helper fields
	private int clicks = 0;
	private float gracePeriod = 0.5f, clickTimer = gracePeriod;


	private boolean grabbed = false;

	/**
	 * Returns the {@link Sprite}s that belong to this Widget. This collection
	 * of Sprites are used for rendering within the {@link GuiRenderer}
	 * implementation.
	 *
	 * @return list of sprites belonging to this widget
	 */
	public List<Sprite> getSprites() {
		return Collections.emptyList();
	}

	/**
	 * Returns the hit box of this widget. The bounds of this widget determines
	 * when the {@link org.spout.api.input.InputManager} should notify this
	 * widget of being clicked.
	 *
	 * @return hit box of widget
	 */
	public final Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Sets the hit box of this widget. The bounds of this widget determines
	 * when the {@link org.spout.api.input.InputManager} should notify this
	 * widget of being clicked.
	 *
	 * @param bounds of widget
	 */
	public final void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	/**
	 * Returns the transform of the widget. This determines how the widget's
	 * geometry should be configured on the screen. All {@link Sprite}s
	 * belonging to this widget will be shifted accordingly. This should be
	 * modified to position, rotate, and scale this widget proportionally.
	 *
	 * @return transform of widget
	 */
	public final Transform2D getTransform() {
		return transform;
	}

	/**
	 * Returns the z index of the widget on the screen. Widgets with smaller
	 * z-indices will be rendered behind widgets with larger z-indices.
	 *
	 * @return z index
	 */
	public final int getZIndex() {
		return zIndex;
	}

	/**
	 * Sets the z index of this widget. Widgets with smaller
	 * z-indices will be rendered behind widgets with larger z-indices.
	 *
	 * @param zIndex of widget
	 */
	public final void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	/**
	 * Notifies widget of focus. Focus is gained when the client either tabs to
	 * make the widget active or if the client clicks the widget. This method
	 * is marked final for internal handling of focusing and always makes a
	 * call to {@link org.spout.api.guix.Widget#onFocus()}.
	 */
	public final void focus() {
		onFocus();
	}

	/**
	 * Called when the Widget focuses. Focus is gained when the client either
	 * tabs to make the widget active or if the client clicks the widget.
	 */
	public void onFocus() {
	}

	/**
	 * Notifies widget of being blurred. A widget is blurred once a widget has
	 * focus and loses it. This method is marked final for internal handling of
	 * blurring and always makes a call to
	 * {@link org.spout.api.guix.Widget#onBlur()}.
	 *
	 * @see org.spout.api.guix.Widget#onFocus()
	 */
	public final void blur() {
		onBlur();
	}

	/**
	 * Called when the Widget is blurred. A widget is blurred once a widget has
	 * focus and loses it.
	 */
	public void onBlur() {
	}

	private void resetClickCounter() {
		clicks = 0;
		clickTimer = 0.5f;
	}

	/**
	 * Returns the period in which to report how many clicks have occurred, in
	 * seconds. The default is 0.5 and is widely accepted as the 'grace period'
	 * for double-clicks.
	 *
	 * @return period in which to report clicks
	 */
	public final float getGracePeriod() {
		return gracePeriod;
	}

	/**
	 * Sets the period in which to report how many clicks have occurred, in
	 * seconds. The default is 0.5 and is widely accepted as the 'grace period'
	 * for double-clicks.
	 *
	 * @param gracePeriod to set
	 */
	public final void setGracePeriod(float gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	/**
	 * Notifies the widget of being clicked. A widget is 'clicked' when the
	 * client presses or releases a mouse button. This method is marked final
	 * for internal handling of client clicks. This method also reports the
	 * amount of clicks clicked within the designated period for easier double,
	 * triple, etc click handling. This method reports the event that occurred
	 * and the amount of clicks within the 'grace period' designated in this
	 * widget. This method always calls
	 * {@link Widget#onClick(org.spout.api.event.player.input.PlayerClickEvent, int)}
	 *
	 * @param event of click
	 */
	public final void click(PlayerClickEvent event) {
		onClick(event, clicks++);
	}

	/**
	 * Called when the widget is clicked. A widget is 'clicked' when the
	 * client presses or releases a mouse button.
	 *
	 * @param event of click
	 */
	public void onClick(PlayerClickEvent event, int clicksWithinTimer) {
	}

	/**
	 * Notifies the widget of being keyed. A widget is 'keyed' when the widget
	 * is focused and a key is pressed or released by the client. This method
	 * marked final for internal key handling and always calls
	 * {@link Widget#onKey(org.spout.api.event.player.input.PlayerKeyEvent)}
	 *
	 * @param event of key press/release
	 */
	public final void key(PlayerKeyEvent event) {
		onKey(event);
	}

	/**
	 * Called when the widget is keyed. A widget is 'keyed' when the widget
	 * is focused and a key is pressed or released by the client.
	 *
	 * @param event of key
	 */
	public void onKey(PlayerKeyEvent event) {
	}

	/**
	 * Called when the mouse is moved. This method is called when the mouse is
	 * moved. This also specifies if the mouse if hovering over this widget's
	 * hit box. This method is marked final for internal handling and always
	 * calls
	 * {@link Widget#onMouseMove(org.spout.api.math.IntVector2, org.spout.api.math.IntVector2, boolean)}
	 *
	 * @see org.spout.api.guix.Widget#getBounds()
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
	 * Called when the mouse is moved. This method is called when the mouse is
	 * moved. This also specifies if the mouse if hovering over this widget's
	 * hit box.
	 *
	 * @see org.spout.api.guix.Widget#getBounds()
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
