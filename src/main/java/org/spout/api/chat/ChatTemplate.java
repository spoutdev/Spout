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

/**
 * A storage class for a source ChatArguments to prevent users from modifying the original ChatArguments and its placeholders
 */
public class ChatTemplate {
	private final ChatArguments source;

	public ChatTemplate(ChatArguments source) {
		this.source = source;
	}

	public ChatArguments getArguments() {
		return new ChatArguments(source.getArguments());
	}

	/**
	 * Passes through to {@link ChatArguments#fromFormatString(String)} to create a new ChatTemplate from a format string
	 *
	 * @see ChatArguments#fromFormatString(String)
	 * @param formatString The format string
	 * @return The new ChatTemplate object
	 */
	public static ChatTemplate fromFormatString(String formatString) {
		return new ChatTemplate(ChatArguments.fromFormatString(formatString));
	}

	/**
	 * Converts this ChatTemplate to a format string by using {@link ChatArguments#toFormatString()}
	 * The toFormatString() method is called on the original ChatArguments passed in.
	 *
	 * @see ChatArguments#toFormatString()
	 * @return The format string
	 */
	public String toFormatString() {
		return source.toFormatString();
	}
}
