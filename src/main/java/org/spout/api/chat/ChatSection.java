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

import java.util.List;
import java.util.Map;

import org.spout.api.chat.style.ChatStyle;

/**
 * Represents a portion of ChatArguments
 */
public interface ChatSection {
	/**
	 * The styles affecting this word.
	 * @return The styles affecting this word
	 */
	Map<Integer, List<ChatStyle>> getActiveStyles();

	/**
	 * The string that this word is.
	 *
	 * @return The value of this word
	 */
	String getPlainString();

	/**
	 * Returns the split type that was used to create this
	 *
	 * @return The {@link org.spout.api.chat.ChatSection.SplitType} that was originally
	 * 			used to create this object (along with any others)
	 */
	SplitType getSplitType();

	/**
	 * Creates a new ChatArguments instance containing this ChatSection
	 *
	 * @return a ChatArguments instance with the data in this section
	 */
	ChatArguments toChatArguments();

	/**
	 * Returns a section of this ChatSection instance.
	 *
	 * @param startIndex The index to start from, inclusive
	 * @param endIndex The index to end at, inclusive
	 * @return The section of this {@link ChatSection}
	 * @throws IndexOutOfBoundsException if the provided indexes are not part of th
	 */
	ChatSection subSection(int startIndex, int endIndex);

	/**
	 * Returns the length of the raw text content of this section.
	 * Should equal {@link #getPlainString()}.{@link String#length() length()}
	 *
	 * @return The length of the text.
	 */
	int length();

	public static enum SplitType {
		/**
		 * Create a new {@link ChatSection} after each word
		 */
		WORD,
		/**
		 * Creates a new {@link ChatSection} each time a new ChatStyle is added or removed
		 */
		STYLE_CHANGE,
		/**
		 * Returns a {@link ChatSection} containing the entire contents of the ChatArguments
		 */
		ALL
	}
}
