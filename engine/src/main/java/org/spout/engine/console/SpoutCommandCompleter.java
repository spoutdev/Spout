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
package org.spout.engine.console;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jline.console.completer.Completer;

import org.spout.api.Engine;
import org.spout.api.command.Command;

/**
 * A copy of {@link jline.console.completer.StringsCompleter} that uses a set of strings sourced from the list of commands registered with the commands manager
 */
public class SpoutCommandCompleter implements Completer {
	private final Engine engine;

	public SpoutCommandCompleter(Engine engine) {
		this.engine = engine;
	}

	@Override
	@SuppressWarnings ("unchecked")
	public int complete(String buffer, int cursor,
						@SuppressWarnings ("rawtypes")
						List candidates) {
		String start = (buffer == null) ? "" : buffer;
		TreeSet<String> all = new TreeSet<>();
		for (Command cmd : engine.getCommandManager().getCommands()) {
			all.add(cmd.getName());
		}
		SortedSet<String> matches = all.tailSet(start);

		for (String can : matches) {

			if (!(can.startsWith(start))) {
				break;
			}

			/*if (delimiter != null) {
				int index = can.indexOf(delimiter, cursor);

				if (index != -1) {
					can = can.substring(0, index + 1);
				}
			}*/

			candidates.add(can);
		}

		if (candidates.size() == 1) {
			candidates.set(0, candidates.get(0) + " ");
		}

		// The index of the completion is always from the beginning of
		// the buffer.
		return (candidates.isEmpty()) ? (-1) : 0;
	}
}
