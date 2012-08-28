/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.spout.api.Engine;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatSection;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.entity.Player;
import org.spout.api.plugin.Platform;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharObjectHashMap;
import gnu.trove.set.TCharSet;
import gnu.trove.set.hash.TCharHashSet;

/**
 * This class serves as a wrapper for command arguments. It also provides
 * boolean and value flag parsing and several command helper methods.
 */
public class CommandContext {
	protected final String command;
	protected final List<ChatSection> parsedArgs;
	protected final TIntList originalArgIndices;
	protected final List<ChatSection> originalArgs;
	protected final TCharSet booleanFlags = new TCharHashSet();
	protected final TCharObjectMap<ChatSection> valueFlags = new TCharObjectHashMap<ChatSection>();

	public CommandContext(String command, List<ChatSection> args) throws CommandException {
		this(command, args, null);
	}

	/**
	 * @param command The command name used
	 * @param args An array with arguments. Empty strings outside quotes will be
	 *            removed.
	 * @param valueFlags A set containing all value flags. Pass null to disable
	 *            value flag parsing.
	 * @throws CommandException This is thrown if flag fails for some reason.
	 */
	public CommandContext(String command, List<ChatSection> args, TCharSet valueFlags) throws CommandException {
		if (valueFlags == null) {
			valueFlags = new TCharHashSet();
		}
		originalArgs = args;
		this.command = command;

		// Eliminate empty args and combine multiword args first
		TIntList argIndexList = new TIntArrayList(args.size());
		List<ChatSection> argList = new ArrayList<ChatSection>(args.size());
		for (int i = 0; i < args.size(); ++i) {
			ChatSection arg = args.get(i);
			argIndexList.add(i);

			switch (arg.getPlainString().charAt(0)) {
				case '\'':
				case '"':
					final ChatArguments build = new ChatArguments();
					final char quotedChar = arg.getPlainString().charAt(0);

					int endIndex;
					for (endIndex = i; endIndex < args.size(); ++endIndex) {
						final ChatSection arg2 = args.get(endIndex);
						if (arg2.getPlainString().charAt(arg2.length() - 1) == quotedChar) {
							if (endIndex != i) {
								build.append(' ');
							}
							build.append(arg2.subSection(endIndex == i ? 1 : 0, arg2.length() - 2));
							break;
						} else if (endIndex == i) {
							build.append(arg2.subSection(1, arg2.length() - 1));
						} else {
							build.append(' ').append(arg2);
						}
					}

					if (endIndex < args.size()) {
						arg = build.toSections(ChatSection.SplitType.ALL).get(0);
						i = endIndex;
					}
					// else raise exception about hanging quotes?
			}
			argList.add(arg);
		}

		// Then flags

		originalArgIndices = new TIntArrayList(argIndexList.size());
		parsedArgs = new ArrayList<ChatSection>(argList.size());

		for (int nextArg = 0; nextArg < argList.size();) {
			// Fetch argument
			ChatSection arg = argList.get(nextArg++);
			String plainArg = arg.getPlainString();

			// Not a flag?
			if (plainArg.charAt(0) != '-' || plainArg.length() == 1 || !plainArg.matches("^-[a-zA-Z]+$")) {
				originalArgIndices.add(argIndexList.get(nextArg - 1));
				parsedArgs.add(arg);
				continue;
			}

			// Handle flag parsing terminator --
			if (arg.getPlainString().equals("--")) {
				while (nextArg < argList.size()) {
					originalArgIndices.add(argIndexList.get(nextArg));
					parsedArgs.add(argList.get(nextArg++));
				}
				break;
			}

			// Go through the flag characters
			for (int i = 1; i < arg.getPlainString().length(); ++i) {
				char flagName = arg.getPlainString().charAt(i);

				if (valueFlags.contains(flagName)) {
					if (this.valueFlags.containsKey(flagName)) {
						throw new CommandException("Value flag '" + flagName + "' already given");
					}

					if (nextArg >= argList.size()) {
						throw new CommandException("No value specified for the '-" + flagName + "' flag.");
					}

					// If it is a value flag, read another argument and add it
					this.valueFlags.put(flagName, argList.get(nextArg++));
				} else {
					booleanFlags.add(flagName);
				}
			}
		}
	}

	public int length() {
		return parsedArgs.size();
	}

	public String getCommand() {
		return command;
	}

	public int getInteger(int index) throws NumberFormatException {
		return Integer.parseInt(getString(index));
	}

	public int getInteger(int index, int def) throws NumberFormatException {
		return index < parsedArgs.size() ? Integer.parseInt(parsedArgs.get(index).getPlainString()) : def;
	}

	public boolean isInteger(int index) {
		if (index >= parsedArgs.size()) {
			return false;
		}
		try {
			Integer.parseInt(parsedArgs.get(index).getPlainString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public World getWorld(int index) {
		return Spout.getEngine().getWorld(getString(index));
	}

	public World getWorld(int index, boolean exact) {
		return Spout.getEngine().getWorld(getString(index), exact);
	}

	public Player getPlayer(int index, boolean exact) {
		Engine engine = Spout.getEngine();
		if (!(engine instanceof Server)) {
			throw new IllegalStateException("You can only get players in server mode.");
		}
		return ((Server) engine).getPlayer(getString(index), exact);
	}

	public Collection<World> matchWorld(int index) {
		return Spout.getEngine().matchWorld(getString(index));
	}

	public Collection<Player> matchPlayer(int index) {
		Platform p = Spout.getPlatform();
		if (p != Platform.SERVER || p != Platform.PROXY) {
			throw new IllegalStateException("You can only match players in server mode.");
		}
		return ((Server) Spout.getEngine()).matchPlayer(getString(index));
	}

	public double getDouble(int index) throws NumberFormatException {
		return Double.parseDouble(getString(index));
	}

	public double getDouble(int index, double def) throws NumberFormatException {
		return index < parsedArgs.size() ? Double.parseDouble(parsedArgs.get(index).getPlainString()) : def;
	}

	public boolean isDouble(int index) {
		if (index >= parsedArgs.size()) {
			return false;
		}
		try {
			Double.parseDouble(parsedArgs.get(index).getPlainString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public ChatSection get(int index) {
		return parsedArgs.get(index);
	}

	public ChatSection get(int index, ChatSection def) {
		return index < parsedArgs.size() ? parsedArgs.get(index) : def;
	}

	public String getString(int index) {
		return parsedArgs.get(index).getPlainString();
	}

	public String getString(int index, String def) {
		return index < parsedArgs.size() ? parsedArgs.get(index).getPlainString() : def;
	}

	public boolean hasFlag(char ch) {
		return booleanFlags.contains(ch) || valueFlags.containsKey(ch);
	}

	public TCharSet getFlags() {
		return booleanFlags;
	}

	public TCharObjectMap<ChatSection> getValueFlags() {
		return valueFlags;
	}

	public ChatSection getFlag(char ch) {
		return valueFlags.get(ch);
	}

	public ChatSection getFlag(char ch, ChatSection def) {
		final ChatSection value = valueFlags.get(ch);
		if (value == null) {
			return def;
		}

		return value;
	}

	public String getFlagString(char ch) {
		ChatSection sect = valueFlags.get(ch);
		return sect == null ? null : sect.getPlainString();
	}

	public String getFlagString(char ch, String def) {
		final ChatSection value = valueFlags.get(ch);
		if (value == null) {
			return def;
		}

		return value.getPlainString();
	}

	public int getFlagInteger(char ch) throws NumberFormatException {
		return Integer.parseInt(getFlagString(ch));
	}

	public int getFlagInteger(char ch, int def) throws NumberFormatException {
		final ChatSection value = valueFlags.get(ch);
		if (value == null) {
			return def;
		}

		return Integer.parseInt(value.getPlainString());
	}

	public boolean isFlagInteger(char ch) {
		try {
			Integer.parseInt(getFlagString(ch));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public double getFlagDouble(char ch) throws NumberFormatException {
		return Double.parseDouble(getFlagString(ch));
	}

	public double getFlagDouble(char ch, double def) throws NumberFormatException {
		final ChatSection value = valueFlags.get(ch);
		if (value == null) {
			return def;
		}

		return Double.parseDouble(value.getPlainString());
	}

	public boolean isFlagDouble(char ch) {
		try {
			Double.parseDouble(getFlagString(ch));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public ChatArguments getJoinedString(int initialIndex) {
		initialIndex = originalArgIndices.get(initialIndex);
		ChatArguments args = new ChatArguments(originalArgs.get(initialIndex));
		for (int i = initialIndex + 1; i < originalArgs.size(); ++i) {
			args.append(" ").append(originalArgs.get(i));
		}
		return args;
	}

	public List<ChatSection> getRawArgs() {
		return Collections.unmodifiableList(originalArgs);
	}
}
