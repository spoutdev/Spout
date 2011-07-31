package org.getspout.gui;

public interface TextField extends Control{
	
	/**
	 * Gets the position of the cursor in the text field. Position zero is the start of the text.
	 * @return position
	 */
	public int getCursorPosition();
	
	/**
	 * Sets the position of the cursor in the text field.
	 * @param position to set to
	 * @return textfield
	 */
	public TextField setCursorPosition(int position);
	
	/**
	 * Gets the text typed in this text field
	 * @return text
	 */
	public String getText();
	
	/**
	 * Sets the text visible in this text field
	 * @param text inside of the text field
	 * @return textfield
	 */
	public TextField setText(String text);
	
	/**
	 * Gets the maximum characters that can be typed into this text field
	 * @return maximum characters
	 */
	public int getMaximumCharacters();
	
	/**
	 * Sets the maximum characters that can be typed into this text field
	 * @param max characters that can be typed
	 * @return max chars
	 */
	public TextField setMaximumCharacters(int max);
	
	/**
	 * Gets the color of the inner field area of the text box. The color is rendered as a hex color code.
	 * @return field color
	 */
	public int getFieldColor();
	
	/**
	 * Sets the field color of the inner field area of the text box. The color is rendered as a hex color code.
	 * @param hex color to render as
	 * @return textfield
	 */
	public TextField setFieldColor(int hex);
	
	/**
	 * Gets the outside color of the field area of the text box. The color is rendered as a hex color code.
	 * @return border color
	 */
	public int getBorderColor();
	
	/**
	 * Sets the outside color of the field area of the text box. The color is rendered as a hex color code.
	 * @param hex color to render as
	 * @return textfield
	 */
	public TextField setBorderColor(int hex);

}
