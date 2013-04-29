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

import org.junit.Test;

import org.spout.api.exception.CommandException;

import static org.junit.Assert.*;

public class CommandArgumentsTest {
	@Test
	public void testArguments() {
		CommandArguments args = new CommandArguments("foo", "1", "2.5", "true", "false", "here", "is", "a", "joined", "string");

		try {
			assertEquals("foo", args.getString(0));
			assertEquals(1, args.getInteger(1));
			assertEquals(2.5, args.getDouble(2), 0);
			assertTrue(args.getBoolean(3));
			assertFalse(args.getBoolean(4));
			assertEquals("here is a joined string", args.getJoinedString(5));
		} catch (CommandException e) {
			CommandTest.unexpectedException(e);
		}

		try {
			args.getString(10);
			CommandTest.expectedException();
			args.getInteger(0);
			CommandTest.expectedException();
			args.getDouble(0);
			CommandTest.expectedException();
			args.getBoolean(0);
			CommandTest.expectedException();
		} catch (CommandException ignored) {
		}

	}
}
