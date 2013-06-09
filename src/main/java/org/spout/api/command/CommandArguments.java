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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.Server;

import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;

/**
 * This class is used as a wrapper for command arguments and making them easily
 * parse-able. Notice that this class does not actually have a reference to the
 * root command of the execution.
 */
public class CommandArguments {
	private final List<String> args;

	public CommandArguments(List<String> args) {
		this.args = args;
	}

	public CommandArguments(String... args) {
		this(Arrays.asList(args));
	}

	/**
	 * Returns all the arguments.
	 *
	 * @return all arguments
	 */
	public List<String> get() {
		return Collections.unmodifiableList(args);
	}

	/**
	 * Returns the length of the arguments.
	 *
	 * @return length of arguments
	 */
	public int length() {
		return args.size();
	}

	private String getString0(int index) {
		if (index >= args.size()) {
			return null;
		}
		return args.get(index);
	}

	/**
	 * Returns the {@link String} at the specified index.
	 *
	 * @param index to get string from
	 * @return string at specified index
	 * @throws CommandException if the specified index is out of bounds
	 */
	public String getString(int index) throws CommandException {
		String str = getString0(index);
		if (str == null) {
			throw new CommandException("Specified index is out of bounds. (index " + index + "; size " + args.size() + ")");
		}
		return str;
	}

	private Integer getInteger0(int index) {
		try {
			return Integer.parseInt(getString0(index));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Parses and returns an integer at the specified index.
	 *
	 * @param index to get int from
	 * @return int at index
	 * @throws CommandException if string at specified index is not an int
	 */
	public int getInteger(int index) throws CommandException {
		Integer i = getInteger0(index);
		if (i == null) {
			throw new CommandException("Expected integer at index " + index);
		}
		return i;
	}

	/**
	 * Returns true if the {@link String} at the specified index is an integer.
	 *
	 * @param index to check
	 * @return true if string at specified index is an int
	 */
	public boolean isInteger(int index) {
		return getInteger0(index) != null;
	}

	private Double getDouble0(int index) {
		try {
			return Double.parseDouble(getString0(index));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Parses and returns a double at the specified index.
	 *
	 * @param index to get double from
	 * @return double at index
	 * @throws CommandException if string at specified index is not a double
	 */
	public double getDouble(int index) throws CommandException {
		Double d = getDouble0(index);
		if (d == null) {
			throw new CommandException("Expected floating point at index " + index);
		}
		return d;
	}

	/**
	 * Returns true if the {@link String} at the specified index is a double.
	 *
	 * @param index to check
	 * @return true if string at specified index is a double
	 */
	public boolean isDouble(int index) {
		return getDouble0(index) != null;
	}

	private Boolean getBoolean0(int index) {
		String str = getString0(index);
		if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
			return null;
		}
		return Boolean.parseBoolean(str);
	}

	/**
	 * Parses and returns a boolean at the specified index.
	 *
	 * @param index to get boolean from
	 * @return boolean at specified index
	 * @throws CommandException if the string at the specified index is not a boolean
	 */
	public boolean getBoolean(int index) throws CommandException {
		Boolean b = getBoolean0(index);
		if (b == null) {
			throw new CommandException("Expected boolean value at index " + index);
		}
		return b;
	}

	/**
	 * Returns true if the string at the specified index is a boolean.
	 *
	 * @param index to check
	 * @return true if string at specified index is a boolean
	 */
	public boolean isBoolean(int index) {
		return getBoolean0(index) != null;
	}

	/**
	 * Returns a string including every argument from the specified index on.
	 *
	 * @param index of arg to start string at
	 * @return string of specified arg on
	 */
	public String getJoinedString(int index) {
		StringBuilder builder = new StringBuilder();
		for (int i = index; i < args.size(); i++) {
			builder.append(args.get(i));
			if (i + 1 != args.size()) {
				builder.append(' ');
			}
		}
		return builder.toString();
	}

	private Player getPlayer0(int index, boolean exact) {
		if (Spout.getPlatform() == Platform.SERVER) {
			return ((Server) Spout.getEngine()).getPlayer(getString0(index), exact);
		} else {
			if (exact && ((Client) Spout.getEngine()).getPlayer().getName().equals(getString0(index))) {
				return ((Client) Spout.getEngine()).getPlayer();
			}
			// TODO fix this please
			throw new UnsupportedOperationException("Can't match player on client");
		}
	}

	/**
	 * Returns a player at the specified index.
	 *
	 * @param index to get player from
	 * @param exact if the player's name must be exact
	 * @return the player at the specified index
	 * @throws CommandException if the specified player is not online
	 */
	public Player getPlayer(int index, boolean exact) throws CommandException {
		Player player = getPlayer0(index, exact);
		if (player == null) {
			throw new CommandException("Player not found.");
		}
		return player;
	}

	/**
	 * Returns a player at the specified index.
	 *
	 * @param index to get player from
	 * @return the player at the specified index
	 * @throws CommandException if the specified player is not online
	 */
	public Player getPlayer(int index) throws CommandException {
		return getPlayer(index, false);
	}

	/**
	 * Returns true if there is an online player's name at the specified index.
	 *
	 * @param index to check
	 * @param exact if the player's name needs to be exact
	 * @return true if player is online
	 */
	public boolean isPlayer(int index, boolean exact) {
		return getPlayer0(index, exact) != null;
	}

	/**
	 * Returns true if there is an online player's name at the specified index.
	 *
	 * @param index to check
	 * @return true if player is online
	 */
	public boolean isPlayer(int index) {
		return isPlayer(index, false);
	}

	private World getWorld0(int index, boolean exact) {
		return Spout.getEngine().getWorld(getString0(index), exact);
	}

	/**
	 * Returns the world at the specified index.
	 *
	 * @param index to get world from
	 * @param exact if the world name must be exact
	 * @return world at index
	 * @throws CommandException if world does not exist
	 */
	public World getWorld(int index, boolean exact) throws CommandException {
		World world = getWorld0(index, exact);
		if (world == null) {
			throw new CommandException("World not found.");
		}
		return world;
	}

	/**
	 * Returns the world at the specified index.
	 *
	 * @param index to get world from
	 * @return world at index
	 * @throws CommandException if world does not exist
	 */
	public World getWorld(int index) throws CommandException {
		return getWorld(index, true);
	}

	/**
	 * Returns true if the world at the specified index exists.
	 *
	 * @param index to check
	 * @param exact if the name of the world needs to be exact
	 * @return true if world is at specified index
	 */
	public boolean isWorld(int index, boolean exact) {
		return getWorld0(index, exact) != null;
	}

	/**
	 * Returns true if the world at the specified index exists.
	 *
	 * @param index to check
	 * @return true if world is at specified index
	 */
	public boolean isWorld(int index) {
		return isWorld(index, true);
	}

	/**
	 * Returns the arguments in an array.
	 *
	 * @return arguments
	 */
	public String[] toArray() {
		return args.toArray(new String[args.size()]);
	}

	@Override
	public String toString() {
		return getJoinedString(0);
	}
}
