/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.component.widget;

import java.awt.Color;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.event.player.input.PlayerKeyEvent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.math.Rectangle;
import org.spout.api.render.Font;
import org.spout.api.render.SpoutRenderMaterials;
import org.spout.api.signal.Signal;

public class TextFieldComponent extends LabelComponent {
	private static final DefaultedKey<Character> KEY_PASSWORD_CHAR = new DefaultedKeyImpl<Character>("password_char", '*');
	private static final DefaultedKey<Boolean> KEY_PASSWORD_FIELD = new DefaultedKeyImpl<Boolean>("password_field", false);
	private static final DefaultedKey<Boolean> KEY_SCROLLABLE = new DefaultedKeyImpl<Boolean>("scrollable", false);
	private static final DefaultedKey<String> KEY_CACHED_TEXT = new DefaultedKeyImpl<String>("cached_text", "");
	private static final DefaultedKey<Integer> KEY_MAX_CHARS = new DefaultedKeyImpl<Integer>("max_chars", 100);
	private static final DefaultedKey<Integer> KEY_MAX_ROWS = new DefaultedKeyImpl<Integer>("max_rows", 20);
	private static final DefaultedKey<Integer> KEY_ROWS = new DefaultedKeyImpl<Integer>("rows", 1);
	private static final DefaultedKey<Integer> KEY_CURSOR_INDEX = new DefaultedKeyImpl<Integer>("cursor_index", 0);
	private static final DefaultedKey<Integer> KEY_CURSOR_ROW = new DefaultedKeyImpl<Integer>("cursor_row", 0);
	private final RenderPart cursor = new RenderPart();
	private final RenderPart field = new RenderPart();
	private final RenderPart border = new RenderPart();
	private Client client;
	private int typingTimer = 80;
	private int blinkingTimer = 20;
	/**
	 * Emitted whenever the text changes
	 */
	public static final Signal SIGNAL_TEXT_CHANGED = new Signal("textChanged", String.class);
	/**
	 * Emitted whenever the user presses enter or return.
	 */
	public static final Signal SIGNAL_RETURN_PRESSED = new Signal("returnPressed");

	public TextFieldComponent() {
		registerSignal(SIGNAL_TEXT_CHANGED);
		registerSignal(SIGNAL_RETURN_PRESSED);
	}

	private void init() {

		clear();

		float fieldX = -toScreenX(4);
		float fieldY = -toScreenY(4);
		float fieldWidth = 0.25f;

		field.setZIndex(2);
		field.setSprite(new Rectangle(fieldX, fieldY, fieldWidth, getRowHeight()));
		field.setColor(Color.WHITE);

		border.setZIndex(3);
		border.setSprite(getBorderBounds());
		border.setColor(Color.GRAY);

		cursor.setZIndex(1);
		cursor.setSprite(getInitialCursorBounds());
		cursor.setColor(Color.BLACK);

		getOwner().add(ControlComponent.class);
	}

	private boolean canFitOnRow(int row, char c) {
		String text = getText(row);
		Font font = getFont();
		float textWidth = 0;
		float space = field.getSprite().getWidth() - toScreenX(8); // Leave some room in the field
		float charWidth = toScreenX(font.getPixelBounds(c).width);

		for (char ch : text.toCharArray()) {
			textWidth += toScreenX(font.getPixelBounds(ch).width);
		}

		return textWidth + charWidth <= space;
	}

	private int indexOf(String str, int from) {
		// first new line from the specified int or last index
		return str.indexOf('\n', from) == -1 ? str.length() - 1 : str.indexOf('\n');
	}

	private float toScreenX(int pixels) {
		return pixels / client.getResolution().getX();
	}

	private float toScreenY(int pixels) {
		return pixels / client.getResolution().getY();
	}

	private Rectangle getBorderBounds() {
		Rectangle rect = field.getSprite();
		float height = rect.getHeight() + toScreenY(8);
		float width = rect.getWidth() + toScreenX(8);
		float x = rect.getX() - toScreenX(4);
		float y = rect.getY() - toScreenY(4);
		return new Rectangle(x, y, width, height);
	}

	private Rectangle getInitialCursorBounds() {
		Rectangle rect = field.getSprite();
		float height = rect.getHeight() - toScreenY(4);
		float width = toScreenX(getFont().getSpaceWidth() / 2);
		float x = rect.getX() + toScreenX(2);
		float y = rect.getY() + toScreenY(2);
		return new Rectangle(x, y, width, height);
	}

	/**
	 * Returns the text on the specified row. Note that this method uses the
	 * cached text field which means not all returned text is
	 * necessarily visible.
	 * @param row to get text from
	 * @return text on specified row
	 */
	public String getText(int row) {
		int maxRows = getMaxRows();
		if (row < 0 || row > maxRows) {
			throw new IllegalArgumentException("Specified row must be between 0 and " + maxRows);
		}
		String str = getCachedText();
		int start = 0;
		int end = indexOf(str, 0);
		for (int i = 0; i < row; i++) {
			str = str.substring(start, end);
			start = str.indexOf('\n') + 1;
			end = indexOf(str, start + 1);
		}
		return str;
	}

	/**
	 * Returns the bounding {@link Rectangle} for this TextField.
	 * @return bounding rectangle of field
	 */
	public Rectangle getBounds() {
		return field.getSprite();
	}

	/**
	 * Returns the {@link Color} of the field being typed into.
	 * @return color of field
	 */
	public Color getFieldColor() {
		return field.getColor();
	}

	/**
	 * Sets the {@link Color} of the field being typed into.
	 * @param color of the field
	 */
	public void setFieldColor(Color color) {
		field.setColor(color);
	}

	/**
	 * Sets the amount of rows in the TextField.
	 * @param rows in the text field
	 */
	public void setRows(int rows) {
		if (rows < 1 || rows > getMaxRows()) {
			throw new IllegalArgumentException("Specified rows exceeds the limit for this text field or is less than one.");
		}
		getDatatable().put(KEY_ROWS, rows);
		Rectangle rect = field.getSprite();
		float height = getRowHeight() * rows;
		// shift y down, multiply row height by specified rows
		field.setSprite(new Rectangle(rect.getX(), rect.getY() - height / rows, rect.getWidth(), height));
	}

	/**
	 * Returns the amount of rows in the TextField.
	 * @return rows of TextField
	 */
	public int getRows() {
		return getDatatable().get(KEY_ROWS);
	}

	/**
	 * Returns the height of a row in the text field.
	 * @return the height of a single row
	 */
	public float getRowHeight() {
		return toScreenY(getFont().getCharHeight() + 8);
	}

	/**
	 * Returns the maximum amount of rows permitted in this TextField. When the
	 * user exceeds this limit the text field will begin to scroll if the
	 * {@link #isScrollable()} call returns <b>true</b>.
	 * @return maximum amount of rows
	 */
	public int getMaxRows() {
		return getDatatable().get(KEY_MAX_ROWS);
	}

	/**
	 * Sets the maximum amount of rows permitted in this TextField. When the
	 * user exceeds this limit the text field will begin to scroll if the
	 * {@link #isScrollable()} call returns <b>true</b>.
	 * @param maxRows maximum amount of rows
	 */
	public void setMaxRows(int maxRows) {
		getDatatable().put(KEY_MAX_ROWS, maxRows);
	}

	/**
	 * Returns the width of this field.
	 * @return width of the field
	 */
	public float getWidth() {
		return field.getSprite().getWidth();
	}

	/**
	 * Sets the width of this field.
	 * @param width of the field
	 */
	public void setWidth(float width) {
		Rectangle rect = field.getSprite();
		field.setSprite(new Rectangle(rect.getX(), rect.getY(), width, rect.getHeight()));
	}

	/**
	 * Returns the maximum amount of chars permitted on a row. A new line will
	 * be formed once exceeding this limit if permitted by
	 * {@link #getMaxRows()}.
	 * @return maximum amount of characters permitted on a row
	 */
	public int getMaxChars() {
		return getDatatable().get(KEY_MAX_CHARS);
	}

	/**
	 * Sets the maximum amount of characters permitted on a row. A new line
	 * will be formed once exceeding this limit if permitted by
	 * {@link #getMaxRows()}.
	 * @param maxChars maximum amount of characters permitted on a row
	 */
	public void setMaxChars(int maxChars) {
		getDatatable().put(KEY_MAX_CHARS, maxChars);
	}

	/**
	 * Whether this text field will attach a scroll bar once the maximum number
	 * of rows is exceeded that is specified by {@link #getMaxRows()}.
	 * @return whether this text field is scrollable
	 */
	public boolean isScrollable() {
		return getDatatable().get(KEY_SCROLLABLE);
	}

	/**
	 * Sets whether this text field will attach a scroll bar once the maximum
	 * number of rows is exceeded that is specified by {@link #getMaxRows()}.
	 * @param scrollable whether this text field should attach a scroll bar.
	 */
	public void setScrollable(boolean scrollable) {
		getDatatable().put(KEY_SCROLLABLE, scrollable);
	}

	/**
	 * Whether this text field should output all inputted text as the character specified in
	 * {@link #getPasswordChar()} for viewing protection for fields that could be used for
	 * passwords.
	 * @return true if this is a password field
	 */
	public boolean isPasswordField() {
		return getDatatable().get(KEY_PASSWORD_FIELD);
	}

	/**
	 * Sets whether this text field should output all inputted text as the character specified in
	 * {@link #getPasswordChar()} for viewing protection for fields that could be used for
	 * passwords.
	 * @param passwordField
	 */
	public void setPasswordField(boolean passwordField) {
		getDatatable().get(KEY_PASSWORD_FIELD, passwordField);
	}

	/**
	 * Gets the character used when {@link #isPasswordField()} returns true.
	 */
	public char getPasswordChar() {
		return getDatatable().get(KEY_PASSWORD_CHAR);
	}

	/**
	 * Sets the character used when {@link #isPasswordField()} returns true.
	 * @param passwordChar char for password field
	 */
	public void setPasswordChar(char passwordChar) {
		if (!isValidChar(passwordChar)) {
			throw new IllegalArgumentException("Specified character must be a valid character as designated by LabelComponent#isValidChar(char)");
		}
		getDatatable().put(KEY_PASSWORD_CHAR, passwordChar);
	}

	/**
	 * Whether a user has typed within the last <b>2 seconds</b>.
	 * @return true if user has typed within the last two seconds
	 */
	public boolean isTyping() {
		return typingTimer > 0;
	}

	/**
	 * Returns the index where the text cursor is located. <b>0</b> being the
	 * first index located before the first character of the field's text.
	 * The cursor must always be behind a character so this index may not
	 * exceed the last index of the text's characters.
	 * @return index of the cursor among the text
	 */
	public int getCursorIndex() {
		return getDatatable().get(KEY_CURSOR_INDEX);
	}

	/**
	 * Sets the index where the text cursor is located. <b>0</b> being the
	 * first index located before the first character of the field's text.
	 * The cursor must always be behind a character so this index may not
	 * exceed the last index of the text's characters.
	 * @param cursorIndex index of cursor
	 */
	public void setCursorIndex(int cursorIndex) {
		String row = getText(getCursorRow());
		if (cursorIndex < 0 || cursorIndex > row.length()) {
			throw new IllegalArgumentException("Specified index must be between 0 and " + row.length());
		}

		getDatatable().put(KEY_CURSOR_INDEX, cursorIndex);
		Rectangle rect = cursor.getSprite();
		float x = getInitialCursorBounds().getX();
		int textIndex = cursorIndex - 1;
		if (textIndex < 0) {
			cursor.setSprite(new Rectangle(x, rect.getY(), rect.getWidth(), rect.getHeight()));
			return;
		}

		Font font = getFont();
		for (int i = 0; i < cursorIndex; i++) {
			x += toScreenX(font.getPixelBounds(row.charAt(i)).width);
		}

		cursor.setSprite(new Rectangle(x, rect.getY(), rect.getWidth(), rect.getHeight()));
	}

	/**
	 * Returns the current row on which the cursor is on.
	 * @return row that cursor is on
	 */
	public int getCursorRow() {
		return getDatatable().get(KEY_CURSOR_ROW);
	}

	/**
	 * Sets the row on which the cursor is on.
	 * @param cursorRow row that cursor is on
	 */
	public void setCursorRow(int cursorRow) {
		int rows = getRows();
		if (cursorRow < 0 || cursorRow > rows - 1) {
			throw new IllegalArgumentException("Specified row must be between 0 and " + (rows - 1));
		}
		float rowHeight = getRowHeight();
		float y = getInitialCursorBounds().getY() + rows * rowHeight - cursorRow * rowHeight;
		Rectangle rect = cursor.getSprite();
		cursor.setSprite(new Rectangle(rect.getX(), y, rect.getWidth(), rect.getHeight()));
		getDatatable().put(KEY_CURSOR_ROW, cursorRow);
	}

	/**
	 * Returns the color of the text cursor.
	 * @return color of text cursor
	 */
	public Color getCursorColor() {
		return cursor.getColor();
	}

	/**
	 * Sets the color of the text cursor.
	 * @param color of cursor
	 */
	public void setCursorColor(Color color) {
		cursor.setColor(color);
	}

	/**
	 * Whether the cursor is visible or not.
	 * @return true if cursor is visible
	 */
	public boolean isCursorVisible() {
		return cursor.getColor().getAlpha() > 0;
	}

	/**
	 * Sets whether the cursor is visible or not. This method's primary use is
	 * to implement the blinking cursor while a user is not typing.
	 * @param visible true to make the cursor visible
	 */
	public void setCursorVisible(boolean visible) {
		Color c = cursor.getColor();
		cursor.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), visible ? 255 : 0));
	}

	/**
	 * Returns the color of the field's border.
	 * @return color of field border
	 */
	public Color getBorderColor() {
		return border.getColor();
	}

	/**
	 * Sets the color of the field's border.
	 * @param color of the border
	 */
	public void setBorderColor(Color color) {
		border.setColor(color);
	}

	/**
	 * Returns <b>all</b> of the text in the text field. The inputted text of
	 * this text field must be cached in the text field because
	 * {@link org.spout.api.component.widget.LabelComponent#getText()} only
	 * returns any <b>visible</b> text; not all text in a text field is
	 * necessarily visible.
	 * @return all of the text
	 */
	public String getCachedText() {
		return getDatatable().get(KEY_CACHED_TEXT);
	}

	private void setCachedText(String cachedText) {
		getDatatable().put(KEY_CACHED_TEXT, cachedText);
	}

	@Override
	public void onAttached() {
		super.onAttached();
		if (!(Spout.getEngine() instanceof Client)) {
			throw new IllegalStateException("Cannot attach TextField in server mode.");
		}
		client = (Client) Spout.getEngine();
		init();
	}

	@Override
	public void onTick(float dt) {
		if (typingTimer > 0) {
			typingTimer--;
		}

		if (!isTyping()) {
			if (blinkingTimer > 0) {
				blinkingTimer--;
			} else {
				// toggle cursor
				setCursorVisible(!isCursorVisible());
				blinkingTimer = 20;
			}
		} else {
			setCursorVisible(true);
		}
	}

	@Override
	public List<RenderPartPack> getRenderPartPacks() {
		List<RenderPartPack> parts = super.getRenderPartPacks();
		RenderPartPack pack = new RenderPartPack(SpoutRenderMaterials.GUI_COLOR);
		pack.add(cursor);
		pack.add(field);
		pack.add(border);
		parts.add(pack);
		return parts;
	}

	@Override
	public void onKey(PlayerKeyEvent event) {
		if (!event.isPressed()) {
			return;
		}

		typingTimer = 80;
		boolean whitespace = false;
		switch (event.getKey()) {
			case KEY_BACK:
				backspace();
				whitespace = true;
				break;
			case KEY_RETURN:
				if (getRows() < getMaxRows()) {
					newLine();
				}
				whitespace = true;
				emit(SIGNAL_RETURN_PRESSED);
				break;
			default:
				break;
		}

		/*
		 * If the char can't fit on the current row and we have room for
		 * another line, make a new line, set the cursor index to zero, and
		 * increment the cursor row. If we don't have room for another line,
		 * start to scroll to the right.
		 */

		if (!whitespace) {
			char c = event.getChar();
			String cachedText = getCachedText();
			if (cachedText.length() >= getMaxChars() || !isValidChar(c)) {
				return;
			}
			if (!canFitOnRow(getCursorRow(), c)) {
				if (getRows() < getMaxRows()) {
					newLine();
				} else {
					// TODO: scroll to right
				}
			}
			append(isPasswordField() ? getPasswordChar() : c);
			setCachedText(cachedText + c);
			setCursorIndex(getCursorIndex() + 1);
		}

		emit(SIGNAL_TEXT_CHANGED, getText());
		getOwner().update();
	}

	@Override
	public void backspace() {
		super.backspace();
		String cachedText = getCachedText();
		if (!cachedText.isEmpty()) {
			setCachedText(cachedText.substring(0, cachedText.length() - 1));
			setCursorIndex(getCursorIndex() - 1);
		}
	}

	@Override
	public void newLine() {
		super.newLine();
		setRows(getRows() + 1);
		setCursorIndex(0);
		setCursorRow(getCursorRow() + 1);
	}
}
