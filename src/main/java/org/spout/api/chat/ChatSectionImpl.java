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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.util.SpoutToStringStyle;

/**
 * Represents a snapshot of a section of a {@link ChatArguments} split according to the
 * {@link SplitType} returned by {@link #getSplitType()}
 */
public class ChatSectionImpl implements ChatSection {
	private final SplitType type;
	private final Map<Integer, List<ChatStyle>> activeStyles;
	private final String wordValue;

	public ChatSectionImpl(SplitType type, Map<Integer, List<ChatStyle>> activeStyles, String wordValue) {
		this.type = type;
		this.activeStyles = Collections.unmodifiableMap(activeStyles);
		this.wordValue = wordValue;
	}

	public Map<Integer, List<ChatStyle>> getActiveStyles() {
		return activeStyles;
	}

	public String getPlainString() {
		return wordValue;
	}

	public SplitType getSplitType() {
		return type;
	}

	public ChatArguments toChatArguments() {
		return new ChatArguments(this);
	}

	public ChatSection subSection(final int startIndex, final int endIndex) throws IndexOutOfBoundsException {
		if (startIndex < 0) {
			throw new IndexOutOfBoundsException("-1 < 0");
		} else if (endIndex >= length()) {
			throw new IndexOutOfBoundsException("Index: " + endIndex + ", Length: " + length());
		}
		final Map<Integer, List<ChatStyle>> styles = new LinkedHashMap<Integer, List<ChatStyle>>();
		String modifiedString = getPlainString().substring(startIndex, endIndex + 1);
		int i;
		for (Map.Entry<Integer, List<ChatStyle>> entry : this.activeStyles.entrySet()) {
			i = entry.getKey();
			if (i == -1) {
				styles.put(i, new ArrayList<ChatStyle>());
			} else if (i < startIndex) {
				List<ChatStyle> styleList = ChatSectionUtils.getOrCreateList(styles, -1);
				for (ChatStyle style : entry.getValue()) {
					ChatSectionUtils.removeConflicting(styleList, style);
					styleList.add(style);
				}
			} else if (i <= endIndex) {
				styles.put(i - startIndex, new ArrayList<ChatStyle>(entry.getValue()));
			}
		}

		return new ChatSectionImpl(type, styles, modifiedString);
	}

	public int length() {
		return getPlainString().length();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("type", type)
				.append("activeStyles", activeStyles, true)
				.append("wordValue", wordValue)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(859, 943)
				.append(this.type)
				.append(this.activeStyles)
				.append(this.wordValue)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {return false;}
		if (getClass() != obj.getClass()) {return false;}
		final ChatSectionImpl other = (ChatSectionImpl) obj;
		return new EqualsBuilder()
				.append(this.activeStyles, other.activeStyles)
				.append(this.wordValue, other.wordValue)
				.isEquals();
	}
}
