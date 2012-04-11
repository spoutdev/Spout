package org.spout.api.gui;

/**
 * A container contains a layout, which is capable to hold as many widgets as you want.
 * Containers can be simple widgets that just contain other widgets, or more advanced ones that offer scrolling and stuff like that.
 * Also, a screen is by definition a container.
 * To implement your own container, you should not only implement the get/set methods defined in this interface, but also redirect method calls to 
 *  - onMouseDown
 *  - onMouseUp
 *  - onMouseMove
 *  - render
 */
public interface Container extends Widget, MouseEventHandler {
	/**
	 * Gets the layout of this container
	 * @return the layout
	 */
	public Layout getLayout();
	
	/**
	 * Sets the layout of this container
	 * @warning when setting the layout to null, this can lead to unexpected behavior. Also, you will loose all widgets of the old layout, and have to re-add them if needed.
	 * @param layout
	 * @return
	 */
	public Container setLayout(Layout layout);
}
