/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.chat.console;

import jline.Completor;
import org.spout.api.Engine;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A copy of {@link jline.SimpleCompletor} that uses a set of strings sourced
 * from the list of commands registered with the commands manager
 */
public class SpoutCommandCompletor implements Completor {
	private final Engine engine;

	public SpoutCommandCompletor(Engine engine) {
		this.engine = engine;
	}
	@Override
	@SuppressWarnings("unchecked")
	public int complete(String buffer, int cursor, @SuppressWarnings("rawtypes") List candidates) {
		String start = (buffer == null) ? "" : buffer;
		TreeSet<String> all = new TreeSet<String>();
		all.addAll(engine.getRootCommand().getChildNames());

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

		// the index of the completion is always from the beginning of
		// the buffer.
		return (candidates.size() == 0) ? (-1) : 0;
	}
}
