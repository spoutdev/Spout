/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * The SpoutAPI is licensed under the SpoutDev license version 1.
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

package org.spout.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * Tests for ChatColor
 */
public class ChatColorTest {
	@Test
	public void testFromCodeString() {
		for (ChatColor color : ChatColor.values()) {
			assertEquals("Color failed: " + color.name(), color,
					ChatColor.byName(color.toString()));
		}
	}

	@Test
	public void testFromName() {
		for (ChatColor color : ChatColor.values()) {
			assertEquals("Color failed: " + color.name(), color,
					ChatColor.byName(color.name()));
		}
	}

	@Test
	public void testFromId() {
		for (ChatColor color : ChatColor.values()) {
			assertEquals("Color failed: " + color.name(), color,
					ChatColor.byCode(color.getCode()));
		}
	}

	@Test
	public void testStripColor() {
		StringBuilder builder = new StringBuilder();
		for (ChatColor color : ChatColor.values()) {
			builder.append(color);
		}
		assertEquals(ChatColor.strip(builder.toString()), "");
	}

	@Test
	public void testStripColor2() {
		assertEquals(ChatColor.strip(ChatColor.GOLD + "This " + ChatColor.BLUE + "is a "
				+ ChatColor.BRIGHT_GREEN + "colored " + ChatColor.WHITE + "string."),
				"This is a colored string.");
	}
}
