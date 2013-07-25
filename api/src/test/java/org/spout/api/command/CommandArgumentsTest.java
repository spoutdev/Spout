/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.spout.api.exception.ArgumentParseException;

import static org.junit.Assert.*;

public class CommandArgumentsTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testGeneral() throws ArgumentParseException {
		CommandArguments test = new CommandArguments("tested", "arg1", "arg2");

		assertEquals("arg1", test.currentArgument("arg1"));
		assertEquals(null, test.getString("arg1"));
		assertEquals("arg1", test.popString("arg1"));
		assertEquals("arg1", test.getString("arg1"));

		assertEquals("arg2", test.currentArgument("arg2"));
		assertEquals(null, test.getString("arg2")); // currentArgument shouldn't store
		assertEquals("arg2", test.popString("arg2"));
		assertEquals("arg2", test.getString("arg2"));

		test.assertCompletelyParsed();
	}

	@Test
	public void testQuotedString() throws Exception {
		CommandArguments validDouble = new CommandArguments("qtest", "\"Hi", "there\"");
		assertEquals("Hi there", validDouble.popString("joined"));

		CommandArguments validSingle = new CommandArguments("qtest", "'Hi", "there'");
		assertEquals("Hi there", validSingle.popString("joined"));

		CommandArguments validSame = new CommandArguments("qtest", "'word'");
		assertEquals("word", validSame.popString("single"));

		CommandArguments validEmpty = new CommandArguments("qtest", "\"\"");
		assertEquals("", validEmpty.popString("empty"));

		CommandArguments valid = new CommandArguments("qtest", "'Hi", "there'", "another");
		assertEquals("Hi there", valid.popString("quoted"));
		assertEquals("another", valid.popString("unquoted"));

	}

	@Test
	public void testUnmatchedQuoteString() throws ArgumentParseException {
		CommandArguments invalid = new CommandArguments("qtest", "'Hehehe");
		thrown.expect(ArgumentParseException.class);
		invalid.popString("gonnathrowanerror");
	}

	@Test
	public void testNotEnoughArgs() throws ArgumentParseException {
		CommandArguments args = new CommandArguments("cmd", "arg1");
		assertEquals("arg1", args.popString("1"));
		thrown.expect(ArgumentParseException.class);
		args.popString("2"); // Should throw exception
	}
}
