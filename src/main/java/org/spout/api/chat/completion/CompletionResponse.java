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

import java.util.Collections;
import java.util.List;

import org.spout.api.chat.ChatArguments;

/**
 * Represents a completion from a {@link Completor} object
 */
public class CompletionResponse {
	private final boolean completeAutomatically;
	private final CompletionRequest request;
	private final List<ChatArguments> results;

	public CompletionResponse(boolean completeAutomatically, CompletionRequest request, List<ChatArguments> results) {
		this.completeAutomatically = completeAutomatically;
		this.request = request;
		this.results = Collections.unmodifiableList(results);
	}

	/**
	 * Returns whether the response should complete automatically
	 *
	 * @return whether the response should automatically complete
	 */
	public boolean shouldCompleteAutomatically() {
		return completeAutomatically;
	}

	/**
	 * Gets the {@link CompletionRequest} for this response
	 *
	 * @return the CompletionRequest for this reponse
	 */
	public CompletionRequest getRequest() {
		return request;
	}

	/**
	 * Gets the results as a List of ChatArguments
	 *
	 * @return the results of the response
	 */
	public List<ChatArguments> getResults() {
		return results;
	}
}
