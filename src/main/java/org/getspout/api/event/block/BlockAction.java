/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.event.block;

public enum BlockAction {
	/**
	 * Left-clicking a block
	 */
	LEFT_CLICK_BLOCK,
	/**
	 * Right-clicking a block
	 */
	RIGHT_CLICK_BLOCK,
	/**
	 * Left-clicking the air
	 */
	LEFT_CLICK_AIR,
	/**
	 * Right-clicking the air
	 */
	RIGHT_CLICK_AIR,
	/**
	 * Triggered with stuff like pressure plates
	 */
	PHYSICAL;

}