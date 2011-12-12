/*
 * This file is part of SpoutCommons (http://wiki.getspout.org/).
 *
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons;

/**
 * Use this class within SpoutCommons to make use of the JIT optimisations by
 * accessing the static final vars. There are mirrored methods and vars to allow
 * either programming style, however only the vars are currently known to
 * reduce code that can never be used.
 */
final public class Commons {

	/**
	 * Is this running on Spoutcraft (client)?
	 */
	final public static boolean isSpoutcraft;
	/**
	 * Is this running on Spout (server)?
	 */
	final public static boolean isSpout;

	static {
		boolean spoutcraft = false;
		try {
			Class.forName("org.getspout.commons.Spoutcraft");
			spoutcraft = true;
		} catch (ClassNotFoundException e) {
		}
		isSpoutcraft = spoutcraft;
		isSpout = !spoutcraft;
	}

	private Commons() {
	}

	/**
	 * Is this running on Spoutcraft (client)?
	 * NOTE: You should use the variable of the same name instead!!!
	 * This is currently marked as deprecated, but will never be removed.
	 * @return if it is client side
	 */
	@Deprecated
	public static boolean isSpoutcraft() {
		return isSpoutcraft;
	}

	/**
	 * Is this running on Spout (server)?
	 * NOTE: You should use the variable of the same name instead!!!
	 * This is currently marked as deprecated, but will never be removed.
	 * @return if it is server side
	 */
	@Deprecated
	public static boolean isSpout() {
		return isSpout;
	}
}
