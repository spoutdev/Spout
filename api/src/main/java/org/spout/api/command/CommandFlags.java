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

import java.text.NumberFormat;
import java.text.ParseException;
import org.spout.api.exception.ArgumentParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle parsing and storing values of flags for a {@link CommandArguments}
 * Flags are stored in the same map as other command arguments in the attached CommandArguments instance.
 */
public class CommandFlags {
	public static final Pattern FLAG_REGEX = Pattern.compile("^-(?<key>-?[\\w]+)(?:=(?<value>.*))?$");

	public static class Flag {
		private final String[] names;
		private final boolean value;

		public Flag(boolean value, String... names) {
			this.value = value;
			this.names = names;
		}

		public String[] getNames() {
			return names;
		}

		public boolean isValue() {
			return value;
		}

		/**
		 * Create a new value flag with the specified aliases
		 *
		 * @param names The aliases for the flag
		 * @return The flag object
		 */
		public static Flag v(String... names) {
			return new Flag(true, names);
		}

		/**
		 * Create a new boolean flag with the specified aliases
		 *
		 * @param names The aliases for the flag
		 * @return The flag object
		 */
		public static Flag b(String... names) {
			return new Flag(false, names);
		}
	}

	private final CommandArguments args;
	private final Map<String, Flag> flags = new HashMap<String, Flag>();

	public CommandFlags(CommandArguments args) {
		this.args = args;
	}

	public void registerFlags(Flag... flags) {
		registerFlags(Arrays.asList(flags));
	}

	public void registerFlags(List<Flag> flags) {
		for (Flag f : flags) {
			for (String name : f.getNames()) {
				this.flags.put(name, f);
			}
		}
	}

	/**
	 * Parse flags from the attached {@link CommandArguments} instance
	 *
	 * @return Whether any flags were parsed
	 * @throws ArgumentParseException if an invalid flag is provided
	 */
	public boolean parse() throws ArgumentParseException {
		boolean anyFlags = false;
		int oldIndex = args.index; // Make argument index invalid when parsing flags
		args.index = args.length() + 1;
		for (Iterator<String> it = args.getLive().iterator(); it.hasNext();) {
			if (tryExtractFlags(it)) {
				anyFlags = true;
			}
		}
		args.index = oldIndex;
		return anyFlags;
	}

	/**
	 * Handle a flag 'word' -- an element in the arguments list
	 * May result in multiple flags
	 *
	 * @param it The iterator to draw the arguments from
	 * @return Whether any flags were successfully parsed
	 * @throws ArgumentParseException if an invalid or incomplete flag is provided
	 */
	protected boolean tryExtractFlags(Iterator<String> it) throws ArgumentParseException {
		String arg = it.next();
		Matcher match = FLAG_REGEX.matcher(arg);
		if (!match.matches()) {
			return false;
		}

		String rawFlag = match.group("key");
		try {
			NumberFormat.getInstance().parse(rawFlag);
			// If it's a number, it's not a flag
			return false;
		} catch (ParseException ex) {}

		it.remove();
		
		if (rawFlag.startsWith("-")) { // Long flag in form --flag
			rawFlag = rawFlag.substring(1);
			handleFlag(it, rawFlag, match.group("value"));
		} else {
			for (char c : rawFlag.toCharArray()) {
				handleFlag(it, String.valueOf(c), null);
			}
		}
		return true;
	}

	/**
	 * Handles a flag.
	 * 3 flag types:
	 * <ul>
	 *     <li>{@code --name=value} - These flags do not have to be defined in advance, and can replace any named positional arguments</li>
	 *     <li>{@code -abc [value]} - These must be defined in advance, can optionally have a value. Multiple flags can be combined in one 'word'</li>
	 *     <li>{@code --name [value]} - These must be defined in advance, can optionally have a value. Each 'word' contains one multicharacter flag name</li>
	 * </ul>
	 *
	 * @param it The iterator to source values from
	 * @param name The name of the argument
	 * @param value A predefined argument, for the first type of flag (shown above)
	 * @throws ArgumentParseException when an invalid flag is presented.
	 */
	protected void handleFlag(Iterator<String> it, String name, String value) throws ArgumentParseException {
		Flag f = this.flags.get(name);
		if (f == null && value == null) {
			throw args.failure(name, "Undefined flag presented", false);
		} else if (f != null) {
			name = f.getNames()[0];
		}

		if (args.has(name)) {
			throw args.failure(name, "This argument has already been provided!", false);
		}

		if (value != null) {
			args.setArgOverride(name, value);
			args.popString(name);
		} else if (f.isValue()) {
			if (!it.hasNext()) {
				throw args.failure(name, "No value for flag requiring value!", false);
			}
			args.setArgOverride(name, it.next());
			args.popString(name);
			it.remove();
		} else {
			args.setArgOverride(name, "true");
			args.popBoolean(name);
		}
	}

	public boolean hasFlag(String flag) {
		Flag f = flags.get(flag);
		if (f == null) {
			return false;
		}
		flag = f.getNames()[0];
		return args.has(flag);
	}
}
