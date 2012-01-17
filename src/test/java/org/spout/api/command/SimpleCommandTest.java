/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.command;

import org.spout.api.event.server.data.RetrieveIntDataEvent;
import org.spout.api.geo.World;
import org.spout.api.util.Named;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author zml2008
 */
public class SimpleCommandTest implements Named, CommandSource {
	private SimpleCommand testCommand;

	@Before
	public void setUp() {
		testCommand = new SimpleCommand(this, "test1", "test2");
	}

	public String getName() {
		return getClass().getName();
	}

	@Test(expected = MissingCommandException.class)
	public void testUnknownSubCommand() throws CommandException {
		testCommand.execute(this, new String[] {"test1", "hellothere"}, -1, false);
	}

	public boolean sendMessage(String message) {
		System.out.println(message);
		return true;
	}

	public boolean sendRawMessage(String message) {
		System.out.println(message);
		return true;
	}

	public boolean hasPermission(String node) {
		return true;
	}

	public boolean isInGroup(String group) {
		return false;
	}

	public String[] getGroups() {
		return new String[0];
	}

	public boolean isGroup() {
		return false;
	}

	public boolean hasPermission(World world, String node) {
		return true;
	}

	public Object getData(String node) {
		return null;
	}

	public Object getData(String node, Object defaultValue) {
		return defaultValue;
	}

	public Object getData(World world, String node) {
		return null;
	}

	public Object getData(World world, String node, Object defaultValue) {
		return defaultValue;
	}

	public int getInt(String node) {
		return RetrieveIntDataEvent.DEFAULT_VALUE;
	}

	public int getInt(String node, int defaultValue) {
		return defaultValue;
	}

	public int getInt(World world, String node) {
		return RetrieveIntDataEvent.DEFAULT_VALUE;
	}

	public int getInt(World world, String node, int defaultValue) {
		return defaultValue;
	}

	public String getString(String node) {
		return null;
	}

	public String getString(String node, String defaultValue) {
		return defaultValue;
	}

	public String getString(World world, String node) {
		return null;
	}

	public String getString(World world, String node, String defaultValue) {
		return defaultValue;
	}
}
