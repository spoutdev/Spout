/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, VanillaDev <http://www.spout.org/>
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
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
package org.spout.api.util.flag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A container used for storing multiple flags and performing AND-matching operations on them
 */
public class FlagContainer {
	private final List<Flag> flags = new ArrayList<Flag>();

	/**
	 * Checks if all the flags contained are set
	 * 
	 * @param flags to evaluate against
	 * @param True if all flags are evaluated, False if not
	 */
	public boolean matchFlags(Set<Flag> flags) {
		for (Flag flag : this.flags) {
			if (!flag.evaluate(flags)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes all the flags from this Flag container
	 * 
	 * @return this Flag container
	 */
	public FlagContainer clearFlags() {
		this.flags.clear();
		return this;
	}

	/**
	 * Adds all the flags specified
	 * 
	 * @param flags to add
	 * @return this Flag container
	 */
	public FlagContainer addFlags(Flag... flags) {
		this.flags.addAll(Arrays.asList(flags));
		return this;
	}

	/**
	 * Removes all the flags specified
	 * 
	 * @param flags to remove
	 * @return this Flag container
	 */
	public FlagContainer removeFlags(Flag... flags) {
		this.flags.removeAll(Arrays.asList(flags));
		return this;
	}

	/**
	 * Gets all the flags contained
	 * 
	 * @return Unmodifiable List of Flags
	 */
	public List<Flag> getFlags() {
		return Collections.unmodifiableList(this.flags);
	}
}
