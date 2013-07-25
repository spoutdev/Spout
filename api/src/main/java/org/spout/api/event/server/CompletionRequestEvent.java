/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.event.server;

import org.spout.api.event.Event;
import org.spout.api.event.HandlerList;

/**
 * This event is fired when a player requests for a string to be completed in chat commonly denoted by a press of the TAB button.
 */
public class CompletionRequestEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final String text;
	private String completion = "";

	public CompletionRequestEvent(String text) {
		this.text = text;
	}

	/**
	 * Returns the text before the cursor when the completion is requested.
	 *
	 * @return text behind cursor
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the text to be appended to the text before the cursor.
	 *
	 * @return text to append
	 */
	public String getCompletion() {
		return completion;
	}

	/**
	 * Sets the text to be appended to the text before the cursor.
	 *
	 * @param completion text to append
	 */
	public void setCompletion(String completion) {
		this.completion = completion;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
