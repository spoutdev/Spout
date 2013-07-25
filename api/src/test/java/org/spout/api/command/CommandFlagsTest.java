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
package org.spout.api.command;

import org.junit.Test;

import org.spout.api.exception.ArgumentParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.spout.api.command.CommandFlags.Flag.b;
import static org.spout.api.command.CommandFlags.Flag.v;

/**
 * Test command flags
 */
public class CommandFlagsTest {
	@Test
	public void testBooleanFlags() throws ArgumentParseException {
		CommandArguments args = new CommandArguments("flag", "-ba", "--long");
		CommandFlags flags = args.flags();
		flags.registerFlags(b("b", "boo"), b("a"), b("long"));
		flags.parse();
		assertTrue(flags.hasFlag("b"));
		assertTrue(args.get("b", Boolean.class));
		assertTrue(flags.hasFlag("boo")); // Aliases work

		assertTrue(flags.hasFlag("a"));
		assertTrue(args.get("a", Boolean.class));
		assertTrue(flags.hasFlag("long"));
		assertTrue(args.get("long", Boolean.class));
	}

	@Test
	public void testValueFlags() throws ArgumentParseException {
		CommandArguments args = new CommandArguments("flag", "-u", "waylon531", "--game", "dwarffortress");
		CommandFlags flags = args.flags();
		flags.registerFlags(v("user", "u"), v("game"));
		flags.parse();

		assertEquals("waylon531", args.getString("user"));
		assertEquals("dwarffortress", args.getString("game"));
	}

	@Test
	public void testUndefinedFlags() throws ArgumentParseException {
		CommandArguments args = new CommandArguments("flag", "--source=risaccess1", "--target=waylon531");
		CommandFlags flags = args.flags();
		flags.parse();

		assertEquals("risaccess1", args.popString("source"));
		assertEquals("waylon531", args.popString("target"));
	}

	@Test
	public void testMixedFlags() throws ArgumentParseException {
		CommandArguments args = new CommandArguments("flag", "word", "-av", "value", "another", "--long");
		CommandFlags flags = args.flags();
		flags.registerFlags(b("a"), v("v"), b("long"));
		flags.parse();

		assertTrue(args.has("a"));
		assertTrue(args.has("long"));
		assertEquals("value", args.getString("v"));
		assertEquals("word", args.popString("first"));
		assertEquals("another", args.popString("second"));
	}

	@Test
	public void testTypeFlags() throws ArgumentParseException {
		CommandArguments args = new CommandArguments("flag", "-ab", "42", "3.14");
		CommandFlags flags = args.flags();
		flags.registerFlags(v("a"), v("b"));
		flags.parse();

		assertEquals(42, args.popInteger("a"));
		assertEquals(3.14, args.popFloat("b"), 0.001);
	}
}
