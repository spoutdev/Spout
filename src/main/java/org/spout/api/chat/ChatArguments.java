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
package org.spout.api.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.spout.api.chat.style.ChatStyle;
import org.spout.api.chat.style.StyleHandler;
import org.spout.api.chat.style.fallback.DefaultStyleHandler;

/**
 * A class to hold the arguments in a chat message
 */
public class ChatArguments {
	private final List<Object> elements = new ArrayList<Object>();

	public ChatArguments(Collection<?> elements) {
		append(elements);
	}

	public ChatArguments(Object... elements) {
		append(elements);
	}

	public List<Object> getArguments() {
		return Collections.unmodifiableList(elements);
	}

	public ChatArguments append(Collection<?> elements) {
		for (Object o : elements) {
			append(o);
		}
		return this;
	}

	public ChatArguments append(Object[] objects) {
		return append(Arrays.asList(objects));
	}

	public ChatArguments append(int[] elements) {
		for (int i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(short[] elements) {
		for (short i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(byte[] elements) {
		for (byte i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(long[] elements) {
		for (long i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(boolean[] elements) {
		for (boolean i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(float[] elements) {
		for (float i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(double[] elements) {
		for (double i : elements) {
			append(i);
		}
		return this;
	}

	public ChatArguments append(Object o) {
		if (o instanceof Collection<?>) {
			append((Collection<?>) o);
		} else if (o.getClass().isArray() && Object.class.isAssignableFrom(o.getClass().getComponentType())) {
			append((Object[]) o);
		} else {
			elements.add(o);
		}
		return this;
	}

	public String asString() {
		return asString(DefaultStyleHandler.ID);
	}

	/**
	 * Starting from end of elements, append strings as they appear. If a ChatStyle appears, apply it to existing text.
	 * If this existing text has already been formatted, check for conflicts
	 * If no conflicts, append existing text to the area to be formatted
	 *
	 * @param handlerId The handlerId to use to get the {@link org.spout.api.chat.style.StyleFormatter StyleFormatters} for ChatStyles
	 * @return The stringified version of this object
	 */
	public String asString(int handlerId) {
		StringBuilder finalBuilder = new StringBuilder();
		StringBuilder singleBuilder = new StringBuilder();
		StyleHandler handler = StyleHandler.get(handlerId);
		ChatStyle previousStyle = null;

		for (int i = elements.size() - 1; i >= 0; --i) {
			Object element = elements.get(i);
			if (element instanceof ChatStyle) {
				ChatStyle style = (ChatStyle) element;
				if (previousStyle != null) {
					if (previousStyle.conflictsWith(style)) {
						finalBuilder.insert(0, handler.getFormatter(style).format(singleBuilder.toString()));
					} else {
						// oh god teh ugliness
						String formatted = handler.getFormatter(style).format(finalBuilder.toString());
						finalBuilder.delete(0, finalBuilder.length());
						finalBuilder.append(formatted);
						finalBuilder.insert(0, handler.getFormatter(style).format(singleBuilder.toString()));
					}
				} else {
					finalBuilder.insert(0, handler.getFormatter(style).format(singleBuilder.toString()));
				}
				previousStyle = style;
				singleBuilder.delete(0, singleBuilder.length());
			} else {
				singleBuilder.insert(0, String.valueOf(element));
			}
		}

		if (singleBuilder.length() > 0) {
			finalBuilder.insert(0, singleBuilder);
		}
		return finalBuilder.toString();
	}

	public static ChatArguments fromString(String str) {
		return fromString(str, DefaultStyleHandler.ID);
	}

	public static ChatArguments fromString(String str, int handlerId) {
		return StyleHandler.get(handlerId).extractArguments(str);
	}
}
