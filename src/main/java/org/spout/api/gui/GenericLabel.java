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
import org.spout.api.ChatColor;
import org.spout.api.ClientOnly;
import org.spout.api.packet.PacketUtil;
import org.spout.api.util.Color;

public class GenericLabel extends AbstractInline implements Label {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 5L;
	private String text = "Your Text Here";
	private WidgetAnchor align = WidgetAnchor.TOP_LEFT;
	private Color color = new Color(1F, 1F, 1F);
	private boolean auto = true;
	private boolean resize = false;
	private int textWidth = -1, textHeight = -1;
	private float scale = 1.0F;

	public GenericLabel() {
	}

	public GenericLabel(String text) {
		setText(text);
	}

	public GenericLabel(int width, int height) {
		super(width, height);
	}

	public GenericLabel(int width, int height, String text) {
		super(width, height);
		setText(text);
	}

	public GenericLabel(int X, int Y, int width, int height) {
		super(X, Y, width, height);
	}

	public GenericLabel(int X, int Y, int width, int height, String text) {
		super(X, Y, width, height);
		setText(text);
	}

	@Override
	public WidgetType getType() {
		return WidgetType.LABEL;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + PacketUtil.getNumBytes(getText()) + 11;
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setText(PacketUtil.readString(input));
		setAlign(WidgetAnchor.getAnchorFromId(input.readByte()));
		setAuto(input.readBoolean());
		setTextColor(PacketUtil.readColor(input));
		setScale(input.readFloat());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		PacketUtil.writeString(output, getText());
		output.writeByte(getAlign().getId());
		output.writeBoolean(isAuto());
		PacketUtil.writeColor(output, getTextColor());
		output.writeFloat(getScale());
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Label setText(String text) {
		if (text != null && !getText().equals(text)) {
			this.text = text;
			textHeight = textWidth = -1;
			doResize();
			autoDirty();
		}
		return this;
	}

	@Override
	public boolean isAuto() {
		return auto;
	}

	@Override
	public Label setAuto(boolean auto) {
		if (isAuto() != auto) {
			this.auto = auto;
			autoDirty();
		}
		return this;
	}

	@Override
	public WidgetAnchor getAlign() {
		return align;
	}

	@Override
	public Label setAlign(WidgetAnchor pos) {
		if (pos != null && !getAlign().equals(pos)) {
			this.align = pos;
			autoDirty();
		}
		return this;
	}

	@Override
	public Color getTextColor() {
		return color;
	}

	@Override
	public Label setTextColor(Color color) {
		if (color != null && !getTextColor().equals(color)) {
			this.color = color;
			autoDirty();
		}
		return this;
	}

	@Override
	public Label setScale(float scale) {
		if (this.scale != scale) {
			this.scale = scale;
			autoDirty();
		}
		return this;
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public Label copy() {
		return ((Label) super.copy()) //
				.setText(getText()) //
				.setScale(getScale()) //
				.setAuto(isAuto()) //
				.setTextColor(getTextColor()) //
				.setResize(isResize());
	}

	@Override
	public boolean isResize() {
		return resize;
	}

	@Override
	public Label setResize(boolean resize) {
		this.resize = resize;
		doResize();
		return this;
	}

	@Override
	public Label doResize() {
		if (resize) {
			if (textHeight < 0 || textWidth < 0) {
				textHeight = getStringHeight(text, getScale());
				textWidth = getStringWidth(text, getScale());
			}
			setMinHeight(textHeight);
			setMinWidth(textWidth);
			if (isFixed()) {
				setHeight(textHeight);
				setWidth(textWidth);
			}
		} else {
			textHeight = textWidth = -1;
		}
		return this;
	}

	@Override
	public Widget setFixed(boolean fixed) {
		super.setFixed(fixed);
		doResize();
		return this;
	}

	/**
	 * Gets the height of the text
	 * @param text
	 * @return height in pixels
	 */
	public static int getStringHeight(String text) {
		return getStringHeight(text, 1.0F);
	}

	/**
	 * Gets the height of the text, at the given scale
	 * @param text
	 * @param scale of the text, 1.0 is default
	 * @return height in pixels
	 */
	public static int getStringHeight(String text, float scale) {
		return (int) (text.split("\n").length * 10 * scale);
	}

	/**
	 * Gets the width of the text
	 * @param text
	 * @return width of the text
	 */
	public static int getStringWidth(String text) {
		return getStringWidth(text, 1.0F);
	}

	/**
	 * Gets the width of the text, at the given scale
	 * @param text
	 * @param scale of the text, 1.0 is default
	 * @return width of the text
	 */
	public static int getStringWidth(String text, float scale) {
		final int[] characterWidths = new int[]{
			1, 9, 9, 8, 8, 8, 8, 7, 9, 8, 9, 9, 8, 9, 9, 9,
			8, 8, 8, 8, 9, 9, 8, 9, 8, 8, 8, 8, 8, 9, 9, 9,
			4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6,
			7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6,
			3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6,
			6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6,
			6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6,
			8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6,
			9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
			9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9,
			8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7,
			7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1
		};
		final String allowedCharacters = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~?Ã³ÚÔõÓÕþÛÙÞ´¯ý─┼╔µã¶÷‗¹¨ Í▄°úÏÎâßÝ¾·±Ð¬║┐«¼¢╝í½╗";
		int length = 0;
		for (String line : ChatColor.strip(text).split("\n")) {
			int lineLength = 0;
			boolean skip = false;
			for (char ch : line.toCharArray()) {
				if (skip) {
					skip = false;
				} else if (ch == '\u00A7') {
					skip = true;
				} else if (allowedCharacters.indexOf(ch) != -1) {
					lineLength += characterWidths[ch];
				}
			}
			length = Math.max(length, lineLength);
		}
		return (int) (length * scale);
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
