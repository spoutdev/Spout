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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Bundles multiple Flags together as an OR-operation or AND-operation<br><br>
 * <b>The flags inside this bundle are not evaluated when using it as a parameter<br>
 * Only use a bundle as input if you handle the specific bundle yourself</b>
 */
public class FlagBundle implements Flag {
	private final List<Flag> flags;
	private final boolean useAnd;

	/**
	 * Constructs a Bundle using the OR-operation
	 * 
	 * @param flags to use
	 */
	public FlagBundle(Flag... flags) {
		this(false, flags);
	}

	/**
	 * Constructs a Bundle using the specified operation
	 * 
	 * @param useAnd True to use the AND-operation, False to use the OR-operation
	 * @param flags to use
	 */
	public FlagBundle(boolean useAnd, Flag... flags) {
		this.flags = Arrays.asList(flags);
		this.useAnd = useAnd;
	}

	@Override
	public boolean evaluate(Set<Flag> flags) {
		for (Flag flag : this.flags) {
			if (useAnd != flag.evaluate(flags)) {
				return !useAnd;
			}
		}
		return useAnd;
	}
}
