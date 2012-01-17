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

import gnu.trove.set.TCharSet;
import gnu.trove.set.hash.TCharHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spout.api.util.MiscCompatibilityUtils;
import org.spout.api.util.Named;
import org.spout.api.util.StringUtil;

public class SimpleCommand implements Command {
	protected final Map<String, Command> children = new HashMap<String, Command>();
	protected Command parent;
	private final Named owner;
	private boolean locked;
	protected List<String> aliases = new ArrayList<String>();
	protected CommandExecutor executor;
	protected RawCommandExecutor rawExecutor;
	protected String help;
	protected String usage;
	protected final TCharSet valueFlags = new TCharHashSet();
	protected final TCharSet flags = new TCharHashSet();
	protected String[] permissions = new String[0];
	protected boolean requireAllPermissions;
	protected int minArgLength = 0, maxArgLength = -1;

	public SimpleCommand(Named owner, String... names) {
		aliases.addAll(Arrays.asList(names));
		this.owner = owner;
	}

	public Command addSubCommand(Named owner, String primaryName) {
		if (isLocked()) {
			return this;
		}
		SimpleCommand sub = new SimpleCommand(owner, primaryName);
		while (children.containsKey(primaryName)) {
			primaryName = owner.getName() + ":" + primaryName;
		}
		children.put(primaryName, sub);
		sub.parent = this;
		return sub;
	}

	public <T> Command addSubCommands(Named owner, T object, CommandRegistrationsFactory<T> factory) {
		factory.create(owner, object, this);
		return this;
	}

	public Command closeSubCommand() {
		if (parent == null) {
			throw new UnsupportedOperationException("This command has no parent");
		}
		lock(owner);
		return parent;
	}

	public Command addAlias(String... names) {
		if (!isLocked()) {
			if (parent != null) {
				boolean changed = false;
				for (String name : names) {
					if (!parent.hasChild(name)) {
						aliases.add(name);
						changed = true;
					}
				}
				if (changed) {
					parent.updateAliases(this);
				}
			} else {
				aliases.addAll(Arrays.asList(names));
			}
		}
		return this;
	}

	public Command setHelp(String help) {
		if (!isLocked()) {
			this.help = help;
		}
		return this;
	}

	public Command setUsage(String usage) {
		if (!isLocked()) {
			this.usage = usage;
		}
		return this;
	}

	public Command setExecutor(CommandExecutor executor) {
		if (!isLocked()) {
			this.executor = executor;
		}
		return this;
	}

	public Command addFlags(String flagString) {
		if (!isLocked()) {
			char[] raw = flagString.toCharArray();
			for (int i = 0; i < raw.length; ++i) {
				if (raw.length > i + 1 && raw[i + 1] == ':') {
					valueFlags.add(raw[i]);
					++i;
				}
				flags.add(raw[i]);
			}
		}
		return this;
	}

	public void execute(CommandSource source, String[] args, int baseIndex, boolean fuzzyLookup) throws CommandException {
		if (rawExecutor != null && rawExecutor != this) {
			rawExecutor.execute(source,  args, baseIndex, fuzzyLookup);
			return;
		}

		if (args.length > baseIndex && children.size() > 0) {
			Command sub = getChild(args[baseIndex + 1], fuzzyLookup);
			if (sub == null) {
				throw new MissingCommandException("Child command needed!", getUsage(args, baseIndex));
			}
			sub.execute(source, args, ++baseIndex, fuzzyLookup);
			return;
		}

		if (executor == null || baseIndex >= args.length) {
			throw new MissingCommandException("No command found!", getUsage(args, baseIndex));
		}

		if (!hasPermission(source)) {
			throw new CommandException("You no not have the required permissions!");
		}
		args = MiscCompatibilityUtils.arrayCopyOfRange(args, baseIndex, args.length);

		CommandContext context = new CommandContext(args, valueFlags);
		for (char flag : context.getFlags().toArray()) {
			if (!flags.contains(flag)) {
				throw new CommandUsageException("Unknown flag:" + flag, getUsage(args, baseIndex));
			}
		}

		if (context.length() < minArgLength) {
			throw new CommandUsageException("Not enough arguments", getUsage(args, baseIndex));
		}
		if (maxArgLength >= 0 && context.length() > maxArgLength) {
			throw new CommandUsageException("Too many arguments", getUsage(args, baseIndex));
		}

		try {
			executor.processCommand(source, this, context);
		} catch (CommandException e) {
			throw e;
		} catch (Throwable t) {
			throw new WrappedCommandException(t);
		}
	}

	public String getPreferredName() {
		return aliases.get(0);
	}

	public Set<Command> getChildCommands() {
		return new HashSet<Command>(children.values());
	}

	public Set<String> getChildNames() {
		return new HashSet<String>(children.keySet());
	}

	public List<String> getNames() {
		return new ArrayList<String>(aliases);
	}

	public String getUsage(String[] input, int baseIndex) {
		return help + " - " + usage;
	}

	public Command getChild(String name, boolean fuzzyLookup) {
		Command command = children.get(name);
		if (command == null && fuzzyLookup) {
			int minDistance = -1;
			for (Map.Entry<String, Command> entry : children.entrySet()) {
				int distance = StringUtil.getLevenshteinDistance(name, entry.getKey());
				if (minDistance < 0 || distance < minDistance) {
					command = entry.getValue();
					minDistance = distance;
				}
			}
			if (minDistance > 0 && minDistance < 2) {
				return command;
			} else {
				return null;
			}
		}
		return command;
	}

	public Command removeChild(Command cmd) {
		if (isLocked()) {
			return this;
		}
		Map<String, Command> removeAliases = new HashMap<String, Command>();
		for (Iterator<Command> i = children.values().iterator(); i.hasNext();) {
			Command registered = i.next();
			if (registered.equals(cmd)) {
				i.remove();
				for (String alias : cmd.getNames()) {
					Command aliasCmd = children.get(alias);
					if (cmd.equals(aliasCmd)) {
						removeAliases.put(alias, aliasCmd);
					}
				}
			}
		}
		for (Map.Entry<String, Command> entry : removeAliases.entrySet()) {
			entry.getValue().removeAlias(entry.getKey());
		}
		return this;
	}

	public Command removeChild(String name) {
		if (isLocked()) {
			return this;
		}
		Command command = getChild(name, false);
		if (command == null) {
			return this;
		}
		Map<String, Command> removeAliases = new HashMap<String, Command>();
		for (Iterator<Command> i = children.values().iterator(); i.hasNext();) {
			Command cmd = i.next();
			if (command.equals(cmd)) {
				i.remove();
				for (String alias : cmd.getNames()) {
					Command aliasCmd = children.get(alias);
					if (cmd.equals(aliasCmd)) {
						removeAliases.put(alias, aliasCmd);
					}
				}
			}
		}
		for (Map.Entry<String, Command> entry : removeAliases.entrySet()) {
			entry.getValue().removeAlias(entry.getKey());
		}
		return this;
	}

	public Command removeAlias(String name) {
		if (isLocked()) {
			return this;
		}
		aliases.remove(name);
		if (parent != null) {
			parent.updateAliases(this);
		}
		return this;
	}

	public boolean lock(Named owner) {
		if (owner == this.owner) {
			locked = true;
			return true;
		}
		return false;
	}

	public boolean unlock(Named owner) {
		if (owner == this.owner) {
			locked = false;
			return true;
		}
		return false;
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean updateAliases(Command child) {
		boolean changed = false;
		List<String> names = child.getNames();
		synchronized (children) {
			for (Iterator<Map.Entry<String, Command>> i = children.entrySet().iterator(); i.hasNext();) {
				Map.Entry<String, Command> entry = i.next();
				if (entry.getValue() != child) {
					continue;
				}
				if (!names.contains(entry.getKey())) {
					i.remove();
					changed = true;
				}
			}
			for (String alias : names) {
				if (!children.containsKey(alias)) {
					children.put(alias, child);
				}
			}
			return changed;
		}
	}

	public boolean hasChild(String name) {
		return children.containsKey(name);
	}

	public Command setParent(Command parent) {
		if (this.parent == null) {
			this.parent = parent;
			parent.updateAliases(this);
		}
		return this;
	}

	public Command setRawExecutor(RawCommandExecutor rawExecutor) {
		if (!isLocked()) {
			this.rawExecutor = rawExecutor;
		}
		return this;
	}

	public Command setPermissions(boolean requireAll, String... permissions) {
		this.requireAllPermissions = requireAll;
		this.permissions = permissions;
		return this;
	}

	public boolean hasPermission(CommandSource sender) {
		if (permissions == null || permissions.length < 1) return true;
		boolean success = requireAllPermissions;
		for (String perm : permissions) {
			if (requireAllPermissions) {
				success &= sender.hasPermission(perm);
			} else {
				success |= sender.hasPermission(perm);
			}
		}
		return success;
	}

	public Command setArgBounds(int min, int max) {
		if (min >= 0) {
			minArgLength = min;
		}
		maxArgLength = max;
		return this;
	}
}
