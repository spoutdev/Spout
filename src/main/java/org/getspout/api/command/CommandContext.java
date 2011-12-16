package org.getspout.api.command;

import gnu.trove.set.TCharSet;
import gnu.trove.set.hash.TCharHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class CommandContext {

	protected final String command;
	protected final List<String> parsedArgs;
	protected final List<Integer> originalArgIndices;
	protected final String[] originalArgs;
	protected final Set<Character> booleanFlags = new HashSet<Character>();
	protected final Map<Character, String> valueFlags = new HashMap<Character, String>();

	public CommandContext(String[] args) throws CommandException {
		this(args, null);
	}

	/**
	 * @param args An array with arguments. Empty strings outside quotes will be removed.
	 * @param valueFlags A set containing all value flags. Pass null to disable value flag parsing.
	 * @throws CommandException This is thrown if flag fails for some reason.
	 */
	public CommandContext(String[] args, TCharSet valueFlags) throws CommandException {
		if (valueFlags == null) {
			valueFlags = new TCharHashSet();
		}

		originalArgs = args;
		command = args[0];

		// Eliminate empty args and combine multiword args first
		List<Integer> argIndexList = new ArrayList<Integer>(args.length);
		List<String> argList = new ArrayList<String>(args.length);
		for (int i = 1; i < args.length; ++i) {
			String arg = args[i];
			if (StringUtils.isEmpty(arg)) {
				continue;
			}

			argIndexList.add(i);

			switch (arg.charAt(0)) {
				case '\'':
				case '"':
					final StringBuilder build = new StringBuilder();
					final char quotedChar = arg.charAt(0);

					int endIndex;
					for (endIndex = i; endIndex < args.length; ++endIndex) {
						final String arg2 = args[endIndex];
						if (arg2.charAt(arg2.length() - 1) == quotedChar) {
							if (endIndex != i) build.append(' ');
							build.append(arg2.substring(endIndex == i ? 1 : 0, arg2.length() - 1));
							break;
						} else if (endIndex == i) {
							build.append(arg2.substring(1));
						} else {
							build.append(' ').append(arg2);
						}
					}

					if (endIndex < args.length) {
						arg = build.toString();
						i = endIndex;
					}
					// else raise exception about hanging quotes?
			}
			argList.add(arg);
		}

		// Then flags

		this.originalArgIndices = new ArrayList<Integer>(argIndexList.size());
		this.parsedArgs = new ArrayList<String>(argList.size());

		for (int nextArg = 0; nextArg < argList.size(); ) {
			// Fetch argument
			String arg = argList.get(nextArg++);

			// Not a flag?
			if (arg.charAt(0) != '-' || arg.length() == 1 || !arg.matches("^-[a-zA-Z]+$")) {
				originalArgIndices.add(argIndexList.get(nextArg - 1));
				parsedArgs.add(arg);
				continue;
			}

			// Handle flag parsing terminator --
			if (arg.equals("--")) {
				while (nextArg < argList.size()) {
					originalArgIndices.add(argIndexList.get(nextArg));
					parsedArgs.add(argList.get(nextArg++));
				}
				break;
			}

			// Go through the flag characters
			for (int i = 1; i < arg.length(); ++i) {
				char flagName = arg.charAt(i);

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
		return Integer.parseInt(parsedArgs.get(index));
	}

	public int getInteger(int index, int def) throws NumberFormatException {
		return index < parsedArgs.size() ? Integer.parseInt(parsedArgs.get(index)) : def;
	}

	public double getDouble(int index) throws NumberFormatException {
		return Double.parseDouble(parsedArgs.get(index));
	}

	public double getDouble(int index, double def) throws NumberFormatException {
		return index < parsedArgs.size() ? Double.parseDouble(parsedArgs.get(index)) : def;
	}

	public String getString(int index) throws NumberFormatException {
		return parsedArgs.get(index);
	}

	public String getString(int index, String def) throws NumberFormatException {
		return index < parsedArgs.size() ? parsedArgs.get(index) : def;
	}

	public boolean hasFlag(char ch) {
		return booleanFlags.contains(ch) || valueFlags.containsKey(ch);
	}

	public Set<Character> getFlags() {
		return booleanFlags;
	}

	public Map<Character, String> getValueFlags() {
		return valueFlags;
	}

	public String getFlag(char ch) {
		return valueFlags.get(ch);
	}

	public String getFlag(char ch, String def) {
		final String value = valueFlags.get(ch);
		if (value == null) {
			return def;
		}

		return value;
	}

	public int getFlagInteger(char ch) throws NumberFormatException {
		return Integer.parseInt(valueFlags.get(ch));
	}

	public int getFlagInteger(char ch, int def) throws NumberFormatException {
		final String value = valueFlags.get(ch);
		if (value == null) {
			return def;
		}

		return Integer.parseInt(value);
	}

	public double getFlagDouble(char ch) throws NumberFormatException {
		return Double.parseDouble(valueFlags.get(ch));
	}

	public double getFlagDouble(char ch, double def) throws NumberFormatException {
		final String value = valueFlags.get(ch);
		if (value == null) {
			return def;
		}

		return Double.parseDouble(value);
	}
}
