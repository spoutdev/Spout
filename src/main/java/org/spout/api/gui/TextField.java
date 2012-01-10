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

import org.spout.api.event.screen.TextFieldChangeEvent;

/**
 * This is a box where the user can input a string.
 */
public interface TextField extends Control {

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
	 * Gets the maximum line this text field can hold
	 * @return max lines
	 */
	public int getMaximumLines();

	/**
	 * Sets the maximum lines this text field can hold. If zero is passed, the text field will hold as many lines as it can depending on its size.
	 * 
	 * @param max lines (0 – 127)
	 * @return textfield
	 */
	public TextField setMaximumLines(int max);

	/**
	 * Gets the color of the inner field area of the text box.
	 * @return field color
	 */
	public Color getFieldColor();

	/**
	 * Sets the field color of the inner field area of the text box.
	 * @param color to render as
	 * @return textfield
	 */
	public TextField setFieldColor(Color color);

	/**
	 * Gets the outside color of the field area of the text box.
	 * @return border color
	 */
	public Color getBorderColor();

	/**
	 * Sets the outside color of the field area of the text box.
	 * @param color to render as
	 * @return textfield
	 */
	public TextField setBorderColor(Color color);

	/**
	 * Gets the tab index for this text field
	 * @return tab index
	 */
	public int getTabIndex();

	/**
	 * Sets the tab index for this text field. When the player presses 
	 * the tabulator key the text field with index+1 will obtain the focus.
	 * Text fields using the same index may not obtain focus when pressing the tabulator key.
	 * The behaviour discontinuous index sequences is undefined.
	 * @param index Tab index (0 – 127)
	 * @return textfield
	 */
	public TextField setTabIndex(int index);

	/**
	 * Determines if this text field is a password field
	 * @return password field
	 */
	public boolean isPasswordField();

	/**
	 * Sets whether the text will be obfuscated by asterisk (*) characters.
	 * Setting to true forces the maximum lines to be 1.
	 * @param password
	 * @return textfield
	 */
	public TextField setPasswordField(boolean password);

	/**
	 * Determines if this text field is focused
	 * @return focused
	 */
	public boolean isFocused();

	/**
	 * Sets whether this text field shall obtain focus.
	 * Make sure only one text field gets the focus at a time.
	 * @param focus
	 * @return textfield
	 */
	@Override
	public TextField setFocus(boolean focus);

	/**
	 * Fires when this text field is typed into on the screen.
	 * This event will also be sent to the screen listener.
	 * @param event
	 */
	public void onTextFieldChange(TextFieldChangeEvent event);

	/**
	 * Fires when the user presses Enter.
	 */
	public void onTypingFinished();

	/**
	 * Sets the placeholder to text.
	 * The placeholder will be displayed when no text is in the TextField
	 * @param text to set as placeholder
	 * @return textfield
	 */
	public TextField setPlaceholder(String text);

	/**
	 * Gets the placeholder
	 * @return the placeholder
	 */
	public String getPlaceholder();
}
