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

import java.util.Set;

/**
 * Can be used to send in additional data<br><br>
 * During evaluation, it will return True if one of the flags contains the same key as set for this Flag
 */
public class FlagData<T> implements Flag {
	protected final Flag key;
	private final T data;

	public FlagData() {
		this(new FlagSingle(), null);
	}

	protected FlagData(Flag key, T data) {
		this.key = key;
		this.data = data;
	}

	/**
	 * Gets the Data contained by this Data flag
	 * 
	 * @return Flag data
	 */
	public T getData() {
		return this.data;
	}

	/**
	 * Constructs this Data flag for the data specified
	 * 
	 * @param data to use
	 * @return Data flag containing the data
	 */
	public FlagData<T> forData(T data) {
		return new FlagData<T>(this.key, data);
	}

	/**
	 * Attempts to obtain the data under this Flag data key
	 * 
	 * @param flags to look in
	 * @return The data value, or the default if the flag was not found
	 */
	@SuppressWarnings("unchecked")
	public T getData(Set<Flag> flags) {
		for (Flag flag : flags) {
			if (flag instanceof FlagData && ((FlagData<?>) flag).key == this.key) {
				return ((FlagData<T>) flag).data;
			}
		}
		return null;
	}

	@Override
	public boolean evaluate(Set<Flag> flags) {
		for (Flag flag : flags) {
			if (flag instanceof FlagData && ((FlagData<?>) flag).key == this.key) {
				return true;
			}
		}
		return false;
	}
}
