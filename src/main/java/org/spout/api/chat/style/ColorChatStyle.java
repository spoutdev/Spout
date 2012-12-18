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
package org.spout.api.chat.style;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A ChatStyle that represents a color, and conflicts with other colors
 */
public class ColorChatStyle extends ChatStyle {
	private static final long serialVersionUID = 1L;
	private final Color color;

	public ColorChatStyle(String name, Color color) {
		super(name);
		this.color = color;
	}

	@Override
	public boolean conflictsWith(ChatStyle other) {
		return other instanceof ColorChatStyle;
	}

	public Color getColor() {
		return color;
	}

	@Override
	protected void writeObject(ObjectOutputStream oos) throws IOException {
		super.writeObject(oos);
	}

	@Override
	protected void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		super.readObject(ois);
		setField(ColorChatStyle.class, "color", color);
	}
}
