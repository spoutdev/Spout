package org.spout.api.gui;

public interface Layout extends MouseEventHandler {
	/**
	 * Called whenever the size of the underlying container changes.
	 * The implementation has to set new geometry of the contained widgets when that happens
	 * In some cases, it might be useful to update the containers minimum and maximum size
	 */
	public void relayout();
	
	/**
	 * Renders the contained widgets
	 */
	public void render();
	
	/**
	 * Gets all attached widgets
	 * @return all attached widgets
	 */
	public Widget[] getWidgets();
	
	/**
	 * Adds widgets to the layout. Should call relayout() after that.
	 * This method has to call widget.setLayout(this) for each widget it adds.
	 * @param widgets the widgets to add
	 */
	public void addWidgets(Widget ...widgets);
	
	/**
	 * Removes all widgets from the layout
	 */
	public void clear();
	
	/**
	 * Removes the given widgets from the layout. Should call relayout() after that.
	 * This method has to call widget.setLayout(null) for each widget it removes.
	 * @param widgets the widgets to remove
	 */
	public void removeWidgets(Widget ...widgets);
	
	/**
	 * Sets the parent property to the given container
	 * @param container the new parent
	 */
	public void setParent(Container container);
	
	/**
	 * Gets the parent property
	 * @return the parent container
	 */
	public Container getParent();
}
