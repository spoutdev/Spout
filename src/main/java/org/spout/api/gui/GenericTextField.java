/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.spout.api.ClientOnly;
import org.spout.api.packet.PacketUtil;
import org.spout.api.util.Color;

public class GenericTextField extends AbstractControl implements TextField {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 3L;
	private static final char MASK_MAXLINES = 0x7F; // bits 1–7
	private static final char MASK_TABINDEX = 0x3F80; // bits 8–14
	private static final char FLAG_PASSWORD = 0x4000; // bit 15
	//private static final char FLAG_FOCUS = 0x8000; // bit 16 // Focus is already set in Control.
	private String text = "";
	private String placeholder = "";
	private int cursor = 0;
	private int maxChars = 16;
	private int maxLines = 1;
	private int tabIndex = 0;
	private boolean password = false;
	private Color fieldColor = new Color(0, 0, 0);
	private Color borderColor = new Color(0.625F, 0.625F, 0.625F);

	public GenericTextField() {
	}

	public GenericTextField(int width, int height) {
		super(width, height);
	}

	public GenericTextField(int X, int Y, int width, int height) {
		super(X, Y, width, height);
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 16 + PacketUtil.getNumBytes(text) + PacketUtil.getNumBytes(placeholder);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setFieldColor(PacketUtil.readColor(input));
		setBorderColor(PacketUtil.readColor(input));
		char c = input.readChar();
		setPasswordField((c & FLAG_PASSWORD) > 0);
		setMaximumLines(c & MASK_MAXLINES);
		setTabIndex((c & MASK_TABINDEX) >>> 7);
		setCursorPosition(input.readChar());
		setMaximumCharacters(input.readChar());
		setText(PacketUtil.readString(input));
		setPlaceholder(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		PacketUtil.writeColor(output, getFieldColor());
		PacketUtil.writeColor(output, getBorderColor());
		output.writeChar((char) (getMaximumLines() & MASK_MAXLINES | (getTabIndex() << 7) & MASK_TABINDEX | (isPasswordField() ? FLAG_PASSWORD : 0)));
		output.writeChar(getCursorPosition());
		output.writeChar(getMaximumCharacters());
		PacketUtil.writeString(output, getText());
		PacketUtil.writeString(output, getPlaceholder());
	}

	@Override
	public int getCursorPosition() {
		return cursor;
	}

	@Override
	public TextField setCursorPosition(int position) {
		if (getCursorPosition() != position) {
			this.cursor = position;
			autoDirty();
		}
		return this;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public TextField setText(String text) {
		if (text != null && !getText().equals(text)) {
			this.text = text;
			autoDirty();
		}
		return this;
	}

	@Override
	public int getMaximumCharacters() {
		return maxChars;
	}

	@Override
	public TextField setMaximumCharacters(int max) {
		if (getMaximumCharacters() != max) {
			this.maxChars = max;
			autoDirty();
		}
		return this;
	}

	@Override
	public int getMaximumLines() {
		return maxLines;
	}

	@Override
	public TextField setMaximumLines(int max) {
		if (getMaximumLines() != max) {
			this.maxLines = max;
			autoDirty();
		}
		return this;
	}

	@Override
	public Color getFieldColor() {
		return fieldColor;
	}

	@Override
	public TextField setFieldColor(Color color) {
		if (color != null && !getFieldColor().equals(color)) {
			this.fieldColor = color;
			autoDirty();
		}
		return this;
	}

	@Override
	public Color getBorderColor() {
		return borderColor;
	}

	@Override
	public TextField setBorderColor(Color color) {
		if (color != null && !getBorderColor().equals(color)) {
			this.borderColor = color;
			autoDirty();
		}
		return this;
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

	@Override
	public TextField setTabIndex(int index) {
		if (getTabIndex() != index) {
			tabIndex = index;
			autoDirty();
		}
		return this;
	}

	@Override
	public boolean isPasswordField() {
		return password;
	}

	@Override
	public TextField setPasswordField(boolean password) {
		if (isPasswordField() != password) {
			this.password = password;
			autoDirty();
		}
		return null;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.TextField;
	}

	@Override
	public TextField copy() {
		// ignore focus parameter which would lead to strange behaviour!
		return ((TextField) super.copy()).setText(getText()).setCursorPosition(getCursorPosition()).setMaximumCharacters(getMaximumCharacters()).setFieldColor(getFieldColor()).setBorderColor(getBorderColor()).setMaximumLines(getMaximumLines()).setTabIndex(getTabIndex()).setPasswordField(isPasswordField()).setPlaceholder(getPlaceholder());
	}

//	@Override
//	public void onTextFieldChange(TextFieldChangeEvent event) {
//		this.callEvent(event);
//	}
	@Override
	public void onTypingFinished() {
	}

	@Override
	public TextField setPlaceholder(String text) {
		if (text != null && !getPlaceholder().equals(text)) {
			placeholder = text;
			autoDirty();
		}
		return this;
	}

	@Override
	public String getPlaceholder() {
		return placeholder;
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
