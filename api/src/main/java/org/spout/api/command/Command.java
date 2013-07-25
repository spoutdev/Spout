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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.spout.api.command.filter.CommandFilter;
import org.spout.api.exception.CommandException;
import org.spout.api.util.Named;
import org.spout.api.util.SpoutToStringStyle;

/**
 * Represents a command sent by a {@link CommandSource}.
 */
public final class Command implements Named {
	private final String name;
	private final List<String> aliases = new ArrayList<String>();
	private final Set<Command> children = new HashSet<Command>();
	private String help, usage, permission;
	private Executor executor;
	private List<CommandFlags.Flag> flags = new ArrayList<CommandFlags.Flag>();
	private Set<CommandFilter> filters = new HashSet<CommandFilter>();
	private boolean parseFlags = true;

	protected Command(String name, String... names) {
		this.name = name;
		aliases.addAll(Arrays.asList(names));
		aliases.add(name);
	}

	/**
	 * Executes the command in the executor it is currently set to.
	 *
	 * @param source that sent the command
	 * @param args command arguments
	 * @throws CommandException if the command executor is null or if {@link Executor#execute(CommandSource, Command, CommandArguments)} throws a CommandException.
	 */
	public void process(CommandSource source, String... args) throws CommandException {
		process(source, new CommandArguments(name, args));
	}

	/**
	 * Executes the command in the executor it is currently set to.
	 *
	 * @param source that sent the command
	 * @param args command arguments
	 * @throws CommandException if the command executor is null or if {@link Executor#execute(CommandSource, Command, CommandArguments)} throws a CommandException.
	 */
	public void process(CommandSource source, CommandArguments args) throws CommandException {

		// check permissions
		if (permission != null && !source.hasPermission(permission)) {
			throw new CommandException("You do not have permission to execute this command.");
		}

		args.flags().registerFlags(this.flags);

		// no child found, try to execute
		for (CommandFilter filter : filters) {
			filter.validate(this, source, args);
		}

		// execute a child if applicable
		if (children.size() > 0) {
			Command child = getChild(args.popString("child"), false);
			if (child != null) {
				if (executor != null) {
					executor.execute(source, this, args);
				}
				child.process(source, args);
			} else {
				throw args.failure("child", "Unknown child!", false);
			}
		} else {
			if (executor == null) {
				throw new CommandException("CommandDescription exists but has no set executor.");
			}
			args.flags().parse();
			executor.execute(source, this, args);
		}
	}

	/**
	 * Returns the {@link Executor} associated with this command.
	 *
	 * @return command's executor
	 */
	public Executor getExecutor() {
		return executor;
	}

	/**
	 * Sets the {@link Executor} associated with this command.
	 *
	 * @param executor to set
	 * @return this command
	 */
	public Command setExecutor(Executor executor) {
		this.executor = executor;
		return this;
	}

	/**
	 * Returns the filter to be run before execution.
	 *
	 * @return filter to run
	 */
	public Set<CommandFilter> getFilters() {
		return filters;
	}

	/**
	 * Sets the filter to be run before execution.
	 *
	 * @param filter to run
	 * @return this command
	 */
	public Command addFilter(CommandFilter... filter) {
		filters.addAll(Arrays.asList(filter));
		return this;
	}

	/**
	 * Returns a set of all the command's children.
	 *
	 * @return children commands
	 */
	public Set<Command> getChildren() {
		return Collections.unmodifiableSet(children);
	}

	/**
	 * Returns a child of this command with the specified. Will create a new unless otherwise specified.
	 *
	 * @param name name of command
	 * @param createIfAbsent true if should create command if non-existent
	 * @return new child or existing child
	 */
	public Command getChild(String name, boolean createIfAbsent) {
		for (Command child : children) {
			for (String alias : child.getAliases()) {
				if (alias.equalsIgnoreCase(name)) {
					return child;
				}
			}
		}

		Command command = null;
		if (createIfAbsent) {
			children.add(command = new Command(name));
		}

		return command;
	}

	/**
	 * Returns a child of this command with the specified. Will create a new unless otherwise specified.
	 *
	 * @param name name of command
	 * @return new child or existing child
	 */
	public Command getChild(String name) {
		return getChild(name, true);
	}

	/**
	 * Returns all the names that the command is recognized under.
	 *
	 * @return list of names the command is called
	 */
	public List<String> getAliases() {
		return Collections.unmodifiableList(aliases);
	}

	/**
	 * Adds a name that the command is recognized under.
	 *
	 * @param alias to add
	 * @return this command
	 */
	public Command addAlias(String... alias) {
		aliases.addAll(Arrays.asList(alias));
		return this;
	}

	/**
	 * Removes the names that a command is recognized under.
	 *
	 * @param alias to remove
	 * @return this command
	 */
	public Command removeAlias(String... alias) {
		aliases.removeAll(Arrays.asList(alias));
		return this;
	}

	public boolean shouldParseFlags() {
		return parseFlags;
	}

	public void setShouldParseFlags(boolean parseFlags) {
		this.parseFlags = parseFlags;
	}

	public List<CommandFlags.Flag> getFlags() {
		return ImmutableList.copyOf(flags);
	}

	public void addFlag(CommandFlags.Flag flag) {
		this.flags.add(flag);
	}

	public void addFlag(CommandFlags.Flag... flags) {
		this.flags.addAll(Arrays.asList(flags));
	}

	/**
	 * Returns the command's help information.
	 *
	 * @return help info
	 */
	public String getHelp() {
		return help;
	}

	/**
	 * Sets the command's help specification.
	 *
	 * @param help to display
	 * @return this command
	 */
	public Command setHelp(String help) {
		this.help = help;
		return this;
	}

	/**
	 * Returns the correct usage for this plugin.
	 *
	 * @return command usage
	 */
	public String getUsage() {
		return usage;
	}

	/**
	 * Sets the Command's correct usage
	 *
	 * @param usage of command
	 * @return this command
	 */
	public Command setUsage(String usage) {
		this.usage = usage;
		return this;
	}

	/**
	 * Returns the permission node required to execute this command.
	 *
	 * @return permission node
	 */
	public String getPermission() {
		return permission;
	}

	/**
	 * Sets the permission node required to execute this command.
	 *
	 * @param permission node required
	 * @return this command
	 */
	public Command setPermission(String permission) {
		this.permission = permission;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Command)) {
			return false;
		}

		Command other = (Command) obj;
		return new EqualsBuilder()
				.append(name, other.name)
				.build();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(SpoutToStringStyle.INSTANCE)
				.append("name", name)
				.build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(name)
				.build();
	}
}
