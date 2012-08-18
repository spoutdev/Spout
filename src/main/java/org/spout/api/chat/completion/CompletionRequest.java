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
package org.spout.api.chat.completion;

import java.util.List;

import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatSection;
import org.spout.api.chat.ChatSection.SplitType;

/**
 * A request for completion.
 * Several helper methods are provided to get specific parts
 */
public class CompletionRequest {
	private final int cursorIndex;
	private final ChatArguments originalArguments;
	private final List<ChatSection> sections;

	public CompletionRequest(ChatArguments args, int cursorIndex) {
		this.originalArguments = args;
		this.cursorIndex = cursorIndex;
		this.sections = args.toSections(SplitType.WORD);
	}

	/**
	 * @return the cursorIndex
	 */
	public int getCursorIndex() {
		return cursorIndex;
	}

	/**
	 * @return the original {@link ChatArguments} object passed to this request
	 */
	public ChatArguments getOriginalArguments() {
		return originalArguments;
	}

	/**
	 * @return the sections
	 */
	public List<ChatSection> getSections() {
		return sections;
	}

	public int getWordIndex() {
		// TODO: Return word index
		throw new UnsupportedOperationException("Not supported yet.");
	}

	// TODO: Cache these responses
	/**
	 * Return the word that the cursor is currently in. This may not be a complete word if the word hasn't been fully typed yet.
	 * @return The word the cursor is in
	 */
	public ChatSection getWordOnCursor() {
		String plainString = originalArguments.getPlainString();
		int wordStartIndex = clampWordStart(plainString, cursorIndex);
		int wordEndIndex = clampWordEnd(plainString, cursorIndex);
		return originalArguments.subSection(wordStartIndex, wordEndIndex);
	}

	public ChatArguments getTextAfterCursor() {
		return originalArguments.subSection(cursorIndex, originalArguments.length()).toChatArguments();
	}

	public ChatArguments getTextFromCursorWord() {
		int wordStartIndex = clampWordStart(originalArguments.getPlainString(), cursorIndex);
		return originalArguments.subSection(wordStartIndex, originalArguments.length()).toChatArguments();
	}

	private static int clampWordStart(String source, int cursor) {
		int index = source.lastIndexOf(" ", cursor);
		if (index == cursor && source.charAt(index) != ' ') {
			return 0;
		} else {
			return index;
		}
	}

	private static int clampWordEnd(String source, int cursor) {
		int index = source.indexOf(" ", cursor + 1);
		if (index == -1) {
			index = source.length();
		}
		return index;
	}
}
