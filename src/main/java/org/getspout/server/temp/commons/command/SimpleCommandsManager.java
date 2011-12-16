package org.getspout.server.temp.commons.command;

import org.getspout.server.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Java15Compat;

import java.util.*;

public class SimpleCommandsManager implements CommandsManager {
	private Map<String, Command> commands = new HashMap<String, Command>();
	private Set<String> aliases;

	@Override
	public boolean execute(String input, CommandSender sender, boolean fuzzyLookup) {
		String[] split = input.split(" ");
		return execute(null, split, sender, fuzzyLookup);
	}

	private boolean execute(Command parent, String[] input, CommandSender sender, boolean fuzzyLookup) {
		if (input.length < 1) return false;
		Command command = getCommand(input[0], fuzzyLookup);
		if (command == null) {
			return false;
		}

		if (command.getSubCommands().size() > 0 && input.length > 1) {
			if (!execute(command, Java15Compat.Arrays_copyOfRange(input, 1, input.length - 1), sender, fuzzyLookup)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean register(String collisionPrefix, Command command) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Set<Command> getRegisteredCommands() {
		return new HashSet<Command>(commands.values());
	}

	@Override
	public Set<String> getRegisteredCommandNames() {
		return new HashSet<String>(commands.keySet());
	}

	@Override
	public Command getCommand(String name) {
		return getCommand(name, false);
	}

	public Command getCommand(String name, boolean fuzzyLookup) {
		Command command = commands.get(name);
		if (command == null && fuzzyLookup) {
			for (Map.Entry<String, Command> entry : commands.entrySet()) {
				if (StringUtil.getLevenshteinDistance(name, entry.getKey()) <= 1) {
					command = entry.getValue();
					break;
				}
			}
		}
		return command;
	}

	@Override
	public boolean unregisterCommandsOfType(Class<? extends Command> clazz) {
		List<String> toRemove = new ArrayList<String>();
		for (Iterator<Command> i = commands.values().iterator(); i.hasNext(); ) {
			Command cmd = i.next();
			if (clazz.isAssignableFrom(cmd.getClass())) {
				i.remove();
				for (String alias : cmd.getAliases()) {
					Command aliasCmd = commands.get(alias);
					if (cmd.equals(aliasCmd)) {
						aliases.remove(alias);
						toRemove.add(alias);
					}
				}
			}
		}
		for (String string : toRemove) {
			commands.remove(string);
		}
		return toRemove.size() > 0;
	}

	@Override
	public boolean unregisterCommand(Command cmd) {
		List<String> toRemove = new ArrayList<String>();
		for (Iterator<Command> i = commands.values().iterator(); i.hasNext(); ) {
			Command registered = i.next();
			if (registered.equals(cmd)) {
				i.remove();
				for (String alias : cmd.getAliases()) {
					Command aliasCmd = commands.get(alias);
					if (cmd.equals(aliasCmd)) {
						aliases.remove(alias);
						toRemove.add(alias);
					}
				}
			}
		}
		for (String string : toRemove) {
			commands.remove(string);
		}
		return toRemove.size() > 0;
	}
}
