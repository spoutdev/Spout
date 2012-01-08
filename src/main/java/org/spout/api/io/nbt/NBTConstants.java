/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.io.nbt;

import java.nio.charset.Charset;

/**
 * A class which holds constant values.
 * @author Graham Edgecombe
 */
public final class NBTConstants {
	/**
	 * The character set used by NBT (UTF-8).
	 */
	public static final Charset CHARSET = Charset.forName("UTF-8");

	/**
	 * Tag type constants.
	 */
	public static final int TYPE_END = 0,
		TYPE_BYTE = 1,
		TYPE_SHORT = 2,
		TYPE_INT = 3,
		TYPE_LONG = 4,
		TYPE_FLOAT = 5,
		TYPE_DOUBLE = 6,
		TYPE_BYTE_ARRAY = 7,
		TYPE_STRING = 8,
		TYPE_LIST = 9,
		TYPE_COMPOUND = 10;

	/**
	 * Default private constructor.
	 */
	private NBTConstants() {

	}
}
