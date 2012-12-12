/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui.component;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.event.player.PlayerKeyEvent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.input.Keyboard;
import org.spout.api.math.Rectangle;
import org.spout.api.math.Vector2;

public class TextFieldComponent extends LabelComponent {
	private final RenderPart cursor = new RenderPart();
	private final RenderPart field = new RenderPart();
	private final RenderPart border = new RenderPart();
	private Client client;
	private int cursorIndex = 0;
	private int maxRows = 1;
	private int maxChars = 20;
	private String lastText;
	private float typingTimer = 2;
	private float blinkingTimer = 0.5f;
	private boolean scrollable = false;
	private boolean passwordField = false;
	private char passwordChar = '*';

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
		super.onTick(dt);
		String text = getText().getPlainString();
		if (lastText != null && lastText.equals(text)) {
			// text hasn't changed, decrement timer
			if (typingTimer > 0) {
				typingTimer -= dt;
			}
		} else {
			// text changed, reset timer
			typingTimer = 2;
		}
		lastText = text;

		if (typingTimer <= 0) {
			// user not typing
			if (blinkingTimer > 0) {
				blinkingTimer -= dt;
			} else {
				setCursorVisible(!isCursorVisible());
			}
		} else {
			blinkingTimer = 0.5f;
		}
	}

	@Override
	public List<RenderPart> getRenderParts() {
		List<RenderPart> parts = super.getRenderParts();
		parts.addAll(Arrays.asList(cursor, field, border));
		return parts;
	}

	@Override
	public void onKey(PlayerKeyEvent event) {
		if (!event.isPressed()) {
			return;
		}
		String str = getText().getPlainString();
		Keyboard key = event.getKey();
		if (key == Keyboard.KEY_BACK) {
			if (str.isEmpty()) {
				return;
			}
			str = str.substring(0, str.length() - 1);
		} else {
			str += event.getChar();
		}
		setText(new ChatArguments(str));
	}

	private void init() {

		setText(new ChatArguments(""));

		Rectangle geo = getOwner().getGeometry();
		float fieldX = geo.getX() - toScreenX(4);
		float fieldY = geo.getY() - toScreenY(4);
		float fieldWidth = 0.25f;

		System.out.println("Row height: " + getRowHeight());

		field.setZIndex(2);
		field.setSprite(new Rectangle(fieldX, fieldY, fieldWidth, getRowHeight()));
		field.setColor(Color.WHITE);

		Rectangle borderRect = getBorderBounds();
		border.setZIndex(3);
		border.setSprite(new Rectangle(borderRect.getX(), borderRect.getY(), borderRect.getWidth(), borderRect.getHeight()));
		border.setColor(Color.GRAY);

		Rectangle cursorRect = getInitialCursorBounds();
		cursor.setZIndex(1);
		cursor.setSprite(new Rectangle(cursorRect.getX(), cursorRect.getY(), cursorRect.getWidth(), cursorRect.getHeight()));
		cursor.setColor(Color.BLACK);

		getOwner().add(ControlComponent.class);
	}

	private void update() {
		// TODO: Call whenever field is changed and update the other render parts
	}

	/**
	 * Returns the bounding {@link Rectangle} for the TextField.
	 *
	 * @return bounding rectangle of field
	 */
	public Rectangle getBounds() {
		return field.getSprite();
	}

	/**
	 * Returns the {@link Color} of the field being typed into.
	 *
	 * @return color of field
	 */
	public Color getFieldColor() {
		return field.getColor();
	}

	/**
	 * Sets the {@link Color} of the field being typed into.
	 *
	 * @param color of the field
	 */
	public void setFieldColor(Color color) {
		field.setColor(color);
	}

	/**
	 * Sets the amount of rows in the TextField.
	 *
	 * @param rows in the text field
	 */
	public void setRows(int rows) {
		if (rows < 1 || rows > getMaxRows()) {
			throw new IllegalArgumentException("Specified rows exceeds the limit for this text field or is less than one.");
		}
		Rectangle rect = field.getSprite();
		float height = getRowHeight();
		// shift y down, multiply row height by specified rows
		field.setSprite(new Rectangle(rect.getX(), rect.getY() - height, rect.getWidth(), height * rows));
	}

	/**
	 * Returns the amount of rows in the TextField.
	 *
	 * @return rows of TextField
	 */
	public int getRows() {
		return (int) getRowHeight() / (int) field.getSprite().getHeight();
	}

	/**
	 * Returns the height of a row in the text field. The row of this text
	 * field is determined by <code>(charHeight + 4) / screenHeight</code>
	 *
	 * @return the height of a single row
	 */
	public float getRowHeight() {
		return (getFont().getCharHeight() + 8) / client.getResolution().getY();
	}

	public Rectangle getBorderBounds() {
		Rectangle rect = field.getSprite();
		float height = rect.getHeight() + toScreenY(8);
		float width = rect.getWidth() + toScreenX(8);
		float x = rect.getX() - toScreenX(4);
		float y = rect.getY() - toScreenY(4);
		return new Rectangle(x, y, width, height);
	}

	public Rectangle getInitialCursorBounds() {
		Rectangle rect = field.getSprite();
		float height = rect.getHeight() - toScreenY(4);
		float width = rect.getWidth() - toScreenX(getFont().getSpaceWidth() / 2);
		float x = rect.getX() + toScreenX(2);
		float y = rect.getY() + toScreenY(2);
		return new Rectangle(x, y, width, height);
	}

	private float toScreenX(int pixels) {
		return pixels / client.getResolution().getX();
	}

	private float toScreenY(int pixels) {
		return pixels / client.getResolution().getY();
	}

	/**
	 * Returns the maximum amount of rows permitted in this TextField. When the
	 * user exceeds this limit the text field will begin to scroll if the
	 * {@link #isScrollable()} call returns <b>true</b>.
	 *
	 * @return maximum amount of rows
	 */
	public int getMaxRows() {
		return maxRows;
	}

	/**
	 * Sets the maximum amount of rows permitted in this TextField. When the
	 * user exceeds this limit the text field will begin to scroll if the
	 * {@link #isScrollable()} call returns <b>true</b>.
	 *
	 * @param maxRows maximum amount of rows
	 */
	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

	/**
	 * Returns the width of this field.
	 *
	 * @return width of the field
	 */
	public float getWidth() {
		return field.getSprite().getWidth();
	}

	/**
	 * Sets the width of this field.
	 *
	 * @param width of the field
	 */
	public void setWidth(float width) {
		Rectangle rect = field.getSprite();
		field.setSprite(new Rectangle(rect.getX(), rect.getY(), width, rect.getHeight()));
	}

	/**
	 * Returns the position of the field on the screen.
	 *
	 * @return position of field
	 */
	public Vector2 getPosition() {
		return field.getSprite().getPosition();
	}

	/**
	 * Sets the position of the field on the screen.
	 *
	 * @param x coordinate of field
	 * @param y coordinate of field
	 */
	public void setPosition(float x, float y) {
		Rectangle rect = field.getSprite();
		field.setSprite(new Rectangle(x, y, rect.getWidth(), rect.getHeight()));
	}

	/**
	 * Sets the position of the field on the screen.
	 *
	 * @param pos position on screen
	 */
	public void setPosition(Vector2 pos) {
		setPosition(pos.getX(), pos.getY());
	}

	/**
	 * Returns the maximum amount of chars permitted on a row. A new line will
	 * be formed once exceeding this limit if permitted by
	 * {@link #getMaxRows()}.
	 *
	 * @return maximum amount of characters permitted on a row
	 */
	public int getMaxChars() {
		return maxChars;
	}

	/**
	 * Sets the maximum amount of characters permitted on a row. A new line
	 * will be formed once exceeding this limit if permitted by
	 * {@link #getMaxRows()}.
	 *
	 * @param maxChars maximum amount of characters permitted on a row
	 */
	public void setMaxChars(int maxChars) {
		this.maxChars = maxChars;
	}

	/**
	 * Whether this text field will attach a scroll bar once the maximum number
	 * of rows is exceeded that is specified by {@link #getMaxRows()}.
	 *
	 * @return whether this text field is scrollable
	 */
	public boolean isScrollable() {
		return scrollable;
	}

	/**
	 * Sets whether this text field will attach a scroll bar once the maximum
	 * number of rows is exceeded that is specified by {@link #getMaxRows()}.
	 *
	 * @param scrollable whether this text field should attach a scroll bar.
	 */
	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	/**
	 * Whether this text field should output all inputted text as the character specified in
	 * {@link #getPasswordCharacter()} for viewing protection for fields that could be used for
	 * passwords.
	 *
	 * @return true if this is a password field
	 */
	public boolean isPasswordField() {
		return passwordField;
	}

	/**
	 * Sets whether this text field should output all inputted text as the character specified in
	 * {@link #getPasswordCharacter()} for viewing protection for fields that could be used for
	 * passwords.
	 *
	 * @param passwordField
	 */
	public void setPasswordField(boolean passwordField) {
		this.passwordField = passwordField;
	}

	/**
	 * Gets the character used when {@link #isPasswordField()} returns true.
	 */
	public char getPasswordCharacter() {
		return passwordChar;
	}

	/**
	 * Sets the character used when {@link #isPasswordField()} returns true.
	 *
	 * @param character
	 */
	public void setPasswordCharacter(char character) {
		passwordChar = character;
	}

	/**
	 * Whether a user has typed within the last <b>2 seconds</b>.
	 *
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
	 *
	 * @return index of the cursor among the text
	 */
	public int getCursorIndex() {
		return cursorIndex;
	}

	/**
	 * Sets the index where the text cursor is located. <b>0</b> being the
	 * first index located before the first character of the field's text.
	 * The cursor must always be behind a character so this index may not
	 * exceed the last index of the text's characters.
	 *
	 * @return index of the cursor among the text
	 */
	public void setCursorIndex(int cursorIndex) {
		this.cursorIndex = cursorIndex;
	}

	/**
	 * Returns the color of the text cursor.
	 *
	 * @return color of text cursor
	 */
	public Color getCursorColor() {
		return cursor.getColor();
	}

	/**
	 * Sets the color of the text cursor.
	 *
	 * @param color of cursor
	 */
	public void setCursorColor(Color color) {
		cursor.setColor(color);
	}

	public boolean isCursorVisible() {
		return cursor.getColor().getAlpha() > 0;
	}

	public void setCursorVisible(boolean visible) {
		Color c = cursor.getColor();
		cursor.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), visible ? 1 : 0));
	}

	/**
	 * Returns the color of the field's border.
	 *
	 * @return color of field border
	 */
	public Color getBorderColor() {
		return border.getColor();
	}

	/**
	 * Sets the color of the field's border.
	 *
	 * @param color of the border
	 */
	public void setBorderColor(Color color) {
		border.setColor(color);
	}
}
