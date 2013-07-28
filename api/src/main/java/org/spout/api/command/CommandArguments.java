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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.exception.ArgumentParseException;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.Plugin;

/**
 * This class is used as a wrapper for command arguments to make them easily parse-able.
 *
 * Please note that the javadocs for the pop* methods describe how input is currently handled. Handling may change over time, and while efforts are made to retain backwards compatibility it is not
 * always possible.
 */
public class CommandArguments {
	private final StringBuilder commandString = new StringBuilder();
	private final Map<String, Object> parsedArgs = new HashMap<>();
	private final Map<String, String> argOverrides = new HashMap<>();
	private final List<String> args;
	private final CommandFlags flags;
	int index = 0;

	public CommandArguments(String commandName, List<String> args) {
		this.commandString.append(commandName);
		this.args = new ArrayList<>(args);
		this.flags = new CommandFlags(this);
	}

	public CommandArguments(String commandName, String... args) {
		this(commandName, Arrays.asList(args));
	}

	/**
	 * Returns all the remaining arguments.
	 *
	 * @return all arguments
	 */
	public List<String> get() {
		return args.subList(index, args.size());
	}

	/**
	 * Gives the mutable list of argument strings currently in use by this.
	 *
	 * @return the arguments
	 */
	List<String> getLive() {
		return args;
	}

	/**
	 * Returns the length of the arguments.
	 *
	 * @return length of arguments
	 */
	public int length() {
		return args.size();
	}

	/**
	 * Returns whether any more unparsed arguments are present
	 *
	 * @return whether the current index is less than the total number of arguments
	 */
	public boolean hasMore() {
		return index < args.size();
	}

	public CommandFlags flags() {
		return flags;
	}

	// State control

	/**
	 * Called when an error has occurred while parsing the specified argument Example:
	 * <pre>
	 * 	 if (success) {
	 * 		 return success(argName, myValue);
	 *     } else {
	 * 		 throw failure(argName, "I dun goofed", "some", "other", "options");
	 *     }
	 * </pre>
	 *
	 * @param argName The name of the argument
	 * @param error The error that occurred
	 * @param completions Possible completions for the argument
	 * @param silenceable Whether the error is caused by syntax of single argument/permanntly invalid provided value (or not)
	 * @return The exception -- must be thrown
	 * @see ArgumentParseException for more detail about meanings of args
	 */
	public ArgumentParseException failure(String argName, String error, boolean silenceable, String... completions) {
		return new ArgumentParseException(commandString.toString(), argName, error, silenceable, completions);
	}

	/**
	 * Must be called when an argument has been successfully parsed This stores the parsed value into the map, appends the string value to the map, and advances the index.
	 *
	 * @param argName The name of the arg
	 * @param parsedValue The parsed value of the argument
	 * @param <T> The type of the parsed value
	 * @return {@code parsedValue}
	 */
	public <T> T success(String argName, T parsedValue) {
		return success(argName, parsedValue, false);
	}

	public <T> T success(String argName, T parsedValue, boolean fallbackValue) {
		if (argName != null) { // Store arg
			parsedArgs.put(argName, parsedValue);
		}

		String valueOverride = argOverrides.get(argName); // Add to parsed command string
		commandString.append(' ');

		if (valueOverride != null) {
			commandString.append(valueOverride);
		} else if (index >= args.size()) {
			commandString.append(" [").append(argName).append("]");
		} else {
			commandString.append(args.get(index));
			if (!fallbackValue) {
				index++; // And increment index
			}
		}

		return parsedValue;
	}

	/**
	 * This method should be called in methods that can potentially return a default value.
	 *
	 * @param e The thrown exception
	 * @param def The default value that could be returned
	 * @param <T> The type of the argument
	 * @return The default value, if error is safe to silence
	 * @throws ArgumentParseException if the error is not appropriate to be silenced
	 */
	public <T> T potentialDefault(ArgumentParseException e, T def) throws ArgumentParseException {
		if (e.isSilenceable()) {
			return success(e.getInvalidArgName(), def, true);
		} else {
			throw e;
		}
	}

	private static final Pattern QUOTE_START_REGEX = Pattern.compile("^('|\")"),
			QUOTE_END_REGEX = Pattern.compile("[^\\\\]?('|\")$"),
			QUOTE_ESCAPE_REGEX = Pattern.compile("\\\\([\"'])");

	/**
	 * Return the current argument, without advancing the argument index. Combines quoted strings provided as arguments as necessary. If there are no arguments remaining, the default value is returned.
	 *
	 * @param argName The name of the argument
	 * @return The argument with the current index.
	 * @throws ArgumentParseException if an invalid quoted string was attempted to be used
	 * @see #success(String, Object)
	 * @see #failure(String, String, boolean, String...)
	 * @see #popString(String) for getting a string-typed argument
	 */
	public String currentArgument(String argName) throws ArgumentParseException {
		if (argName != null && argOverrides.containsKey(argName)) {
			return argOverrides.get(argName);
		}

		if (index >= args.size()) {
			throw failure(argName, "Argument not present", true);
		}

		// Quoted argument parsing -- comparts and removes unnecessary arguments
		String current = args.get(index);
		Matcher start = QUOTE_START_REGEX.matcher(current);
		if (start.find()) { // We've found a quoted string
			boolean foundEnd = false;
			String quoteChar = start.group(1);
			StringBuffer quotedBuilder = new StringBuffer(2 * current.length());

			current = current.substring(1);
			for (boolean first = true; ((index + 1) < args.size() || first) && !foundEnd; first = false) {
				if (!first) {
					current = args.remove(index + 1);
				}

				Matcher end = QUOTE_END_REGEX.matcher(current);
				if (end.find() && end.group(1).equals(quoteChar)) { // End character found here
					foundEnd = true;
					current = current.substring(0, current.length() - 1);
				}

				if (!first) {
					quotedBuilder.append(" ");
				}
				Matcher escape = QUOTE_ESCAPE_REGEX.matcher(current); // Replace escaped strings
				while (escape.find()) {
					escape.appendReplacement(quotedBuilder, escape.group(1));
				}
				escape.appendTail(quotedBuilder);
			}

			if (!foundEnd) { // Unmatched: "quoted string
				throw failure(argName, "Unmatched quoted string!", false, quoteChar);
			}
			args.set(index, (current = quotedBuilder.toString()));
		} else {
			Matcher escape = QUOTE_ESCAPE_REGEX.matcher(current);
			if (escape.find()) {
				StringBuffer replace = new StringBuffer(current.length() - 1);
				escape.appendReplacement(replace, escape.group(1));
				while (escape.find()) {
					escape.appendReplacement(replace, escape.group(1));
				}
				escape.appendTail(replace);
			}
		}

		return current;
	}

	boolean setArgOverride(String name, String value) {
		if (!this.argOverrides.containsKey(name)) {
			this.argOverrides.put(name, value);
			return true;
		}
		return false;
	}

	/**
	 * Increase the argument 'pointer' by one without storing any arguments
	 *
	 * @return Whether there is an argument present at the incremented index
	 */
	public boolean advance() {
		return ++index < args.size();
	}

	/**
	 * @throws ArgumentParseException when unparsed arguments are present.
	 */
	public void assertCompletelyParsed() throws ArgumentParseException {
		if (index < args.size()) {
			throw failure("...", "Too many arguments are present!", false);
		}
	}

	// Argument storage methods

	public String popString(String argName) throws ArgumentParseException {
		String arg = currentArgument(argName);
		return success(argName, arg);
	}

	public String popString(String argName, String def) throws ArgumentParseException {
		try {
			return popString(argName);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	public int popInteger(String argName) throws ArgumentParseException {
		String arg = currentArgument(argName);
		try {
			return success(argName, Integer.parseInt(arg));
		} catch (NumberFormatException e) {
			throw failure(argName, "Input '" + arg + "' is not an integer you silly!", false);
		}
	}

	public int popInteger(String argName, int def) throws ArgumentParseException {
		try {
			return popInteger(argName);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	public float popFloat(String argName) throws ArgumentParseException {
		String arg = currentArgument(argName);
		try {
			return success(argName, Float.parseFloat(arg));
		} catch (NumberFormatException e) {
			throw failure(argName, "Input '" + arg + "' is not a float you silly!", false);
		}
	}

	public float popFloat(String argName, float def) throws ArgumentParseException {
		try {
			return popFloat(argName);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	public double popDouble(String argName) throws ArgumentParseException {
		String arg = currentArgument(argName);
		try {
			return success(argName, Double.parseDouble(arg));
		} catch (NumberFormatException e) {
			throw failure(argName, "Input '" + arg + "' is not a double you silly!", false);
		}
	}

	public double popDouble(String argName, double def) throws ArgumentParseException {
		try {
			return popDouble(argName);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	public boolean popBoolean(String argName) throws ArgumentParseException {
		String str = currentArgument(argName);
		if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
			throw failure(argName, "Value '" + str + "' is not a boolean you silly!", false);
		}
		return success(argName, Boolean.parseBoolean(str));
	}

	public boolean popBoolean(String argName, boolean def) throws ArgumentParseException {
		try {
			return popBoolean(argName);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	public Player popPlayer(String argName) throws ArgumentParseException {
		String name = currentArgument(argName);
		Player player;
		Engine e = Spout.getEngine();
		if (e instanceof Server) {
			Server server = (Server) e;
			player = server.getPlayer(name, false);
			if (player == null) {
				Collection<Player> matched = server.matchPlayer(name);
				String[] names = new String[matched.size()];
				int index = 0;
				for (Player p : matched) {
					names[index] = p.getName();
					index++;
				}
				throw failure(argName, "Player not found.", true, names);
			}
		} else if (e instanceof Client) {
			if (((Client) e).getPlayer().getName().equals(name)) {
				return ((Client) e).getPlayer();
			} else {
				throw failure(argName, "Not the client player!", true);
			}
		} else {
			throw failure(argName, "Unknown Engine type: " + e.getPlatform(), false);
		}
		return success(argName, player);
	}

	public Player popPlayerOrMe(String argName, CommandSource me) throws ArgumentParseException {
		if (!hasMore()) {
			if (me instanceof Player) {
				return success(argName, (Player) me);
			}
			throw failure(argName, "You must either be a player or specify a player!", true);
		}
		return popPlayer(argName);
	}

	/**
	 * Gets a worl
	 */
	public World popWorld(String argName) throws ArgumentParseException {
		String arg = currentArgument(argName);
		World world = Spout.getEngine().getWorld(arg, false);
		if (world == null) {
			throw failure(argName, "World not found!", true);
		}
		return success(argName, world);
	}

	public World popWorld(String argName, CommandSource source) throws ArgumentParseException {
		String arg;
		try {
			arg = currentArgument(argName);
		} catch (ArgumentParseException ex) {
			if (source instanceof Player) {
				return ((Player) source).getWorld();
			} else {
				throw ex;
			}
		}
		World world = Spout.getEngine().getWorld(arg, false);
		if (world == null) {
			throw failure(argName, "World not found!", true);
		}
		return success(argName, world);
	}

	public World popWorld(String argName, World def) throws ArgumentParseException {
		try {
			return popWorld(argName);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	/**
	 * Pop a {@link Vector3}. Accepts either x y z or x,y,z syntax TODO support relative syntax
	 *
	 * @param argName The name of the argument
	 * @return A parsed vector
	 * @throws ArgumentParseException if not enough coordinates are provided or the coordinates are not floats
	 */
	public Vector3 popVector3(String argName) throws ArgumentParseException {
		try {
			float x, y, z;
			if (currentArgument(argName).contains(",")) {
				String[] els = currentArgument(argName).split(",");
				if (els.length < 3) {
					throw failure(argName, "Must provide 3 coordinates", false);
				}
				x = Float.parseFloat(els[0]);
				y = Float.parseFloat(els[1]);
				z = Float.parseFloat(els[2]);
			} else {
				x = popFloat(null);
				y = popFloat(null);
				z = popFloat(null);
			}
			return success(argName, new Vector3(x, y, z));
		} catch (ArgumentParseException e) {
			throw failure(argName, e.getReason(), e.isSilenceable(), e.getCompletions());
		}
	}

	public Vector3 popVector3(String argName, Vector3 def) throws ArgumentParseException {
		try {
			return popVector3(argName);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	/**
	 * Format for point: {@code world x,y,z} or {@code world x y z}
	 *
	 * @param argName Name of key to store this argument value as
	 * @return A point
	 * @see #popWorld(String, CommandSource) for world syntax
	 * @see #popVector3(String) for coordinates syntax
	 */
	public Point popPoint(String argName, CommandSource source) throws ArgumentParseException {
		try {
			World world = popWorld(null, source);
			Vector3 vec = popVector3(null);
			return success(argName, new Point(vec, world));
		} catch (ArgumentParseException e) {
			throw failure(argName, e.getReason(), e.isSilenceable(), e.getCompletions());
		}
	}

	/**
	 * @see #popPoint(String, CommandSource) non-defaulted version
	 */
	public Point popPoint(String argName, CommandSource source, Point def) throws ArgumentParseException {
		try {
			return popPoint(argName, source);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	private static final int MAX_ARG_FULLPRINT = 5;

	private static String buildEnumError(Class<? extends Enum<?>> enumClass) {
		Enum<?>[] constants = enumClass.getEnumConstants();
		String itemList;
		if (constants.length > MAX_ARG_FULLPRINT) {
			itemList = "an element of " + enumClass.getSimpleName();
		} else {
			boolean first = true;
			StringBuilder build = new StringBuilder();
			for (Enum<?> e : constants) {
				if (!first) {
					build.append(", ");
				}
				build.append("'").append(e.name()).append("'");
				first = false;
			}
			itemList = build.toString();
		}
		return "Invalid " + enumClass.getSimpleName() + "; Must be 0-" + constants.length + " or " + itemList + ".";
	}

	/**
	 * Pop an enum value from the arguments list. Values are checked by index and by uppercased name.
	 *
	 * @param argName The name of the argument
	 * @param enumClass The enum class to
	 * @param <T> The type of enum
	 * @return The enum value
	 * @throws ArgumentParseException if no argument is present or an unknown element is chosen.
	 */
	public <T extends Enum<T>> T popEnumValue(String argName, Class<T> enumClass) throws ArgumentParseException {
		String key = currentArgument(argName);
		T[] constants = enumClass.getEnumConstants();
		T value;
		try {
			int index = Integer.parseInt(key);
			if (index < 0 || index >= constants.length) {
				throw failure(argName, buildEnumError(enumClass), false);
			}
			value = constants[index];
		} catch (NumberFormatException e) {
			try {
				value = Enum.valueOf(enumClass, key.toUpperCase());
			} catch (IllegalArgumentException e2) {
				throw failure(argName, buildEnumError(enumClass), false);
			}
		}
		return success(argName, value);
	}

	/**
	 * @see #popEnumValue(String, Class) non-defaulted version
	 */
	public <T extends Enum<T>> T popEnumValue(String argName, Class<T> enumClass, T def) throws ArgumentParseException {
		try {
			return popEnumValue(argName, enumClass);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	/**
	 * Returns a string including every remaining argument
	 *
	 * @return string from specified arg on
	 */
	public String popRemainingStrings(String argName) throws ArgumentParseException {
		if (!hasMore()) {
			failure(argName, "No arguments present", true);
		}
		StringBuilder builder = new StringBuilder();
		while (hasMore()) {
			builder.append(currentArgument(argName));
			advance();
			if (hasMore()) {
				builder.append(' ');
			}
		}
		String ret = builder.toString();
		assertCompletelyParsed(); // If not, there's a bug
		return success(argName, ret);
	}

	public String popRemainingStrings(String argName, String def) throws ArgumentParseException {
		try {
			return popRemainingStrings(argName);
		} catch (ArgumentParseException e) {
			return potentialDefault(e, def);
		}
	}

	// Command utility methods

	public Player checkPlayer(CommandSource source) throws CommandException {
		if (source instanceof Player) {
			return (Player) source;
		} else {
			throw new CommandException("You must be a player to use this commmand!");
		}
	}

	public void logAndNotify(Engine loggerSource, CommandSource source, String message) {
		logAndNotify(loggerSource.getLogger(), source, message);
	}

	public void logAndNotify(Plugin loggerSource, CommandSource source, String message) {
		logAndNotify(loggerSource.getLogger(), source, message);
	}

	public void logAndNotify(Logger logger, CommandSource source, String message) {
		if (source instanceof Player) { // TODO: Better detection if we're console
			source.sendMessage(message);
		}
		if (logger != null) {
			logger.info(message);
		}
	}

	// Parsed argument access methods
	public <T> T get(String key, Class<T> type) {
		return get(key, type, null);
	}

	public <T> T get(String key, Class<T> type, T def) {
		Object o = parsedArgs.get(key);

		if (o == null) {
			return def;
		} else if (type.isInstance(o)) {
			return type.cast(o);
		}
		throw new RuntimeException("Incorrect argument type " + type.getName() + " for argument " + key);
	}

	public boolean has(String key) {
		return parsedArgs.containsKey(key);
	}

	public String getString(String key) {
		return get(key, String.class);
	}

	public String getString(String key, String def) {
		return get(key, String.class, def);
	}

	public int getInteger(String key, int def) {
		Integer i = get(key, Integer.class);
		if (i == null) {
			return def;
		}
		return i;
	}

	public float getFloat(String key, float def) {
		Float f = get(key, Float.class);
		if (f == null) {
			return def;
		}
		return f;
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
		try {
			return popRemainingStrings(null);
		} catch (ArgumentParseException e) {
			return "";
		}
	}
}
