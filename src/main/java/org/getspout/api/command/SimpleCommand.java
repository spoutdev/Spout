package org.getspout.api.command;

import org.getspout.api.util.Named;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleCommand implements Command {
	private final Map<String, Command> children = new HashMap<String, Command>();
	private SimpleCommand parent;
	private final Named owner;
	private List<String> aliases = new ArrayList<String>();
	private CommandExecutor executor;
	private String help;
	private String usage;
	private String description;
	
	public SimpleCommand(Named owner, String... names) {
		aliases.addAll(Arrays.asList(names));
		this.owner = owner;
	}

	public Command addSubCommand(Named owner, String primaryName) {
		SimpleCommand sub = new SimpleCommand(owner, primaryName);
		while (children.containsKey(primaryName)) {
			primaryName = owner.getName() + ":" + primaryName;
		}
		children.put(primaryName, sub);
		sub.parent = this;
		return sub;
	}

	public Command sub(Named owner, String primaryName) {
		return addSubCommand(owner, primaryName);
	}

	@Override
	public <T> Command addSubCommands(Named owner, T object, CommandRegistrationsFactory<T> factory) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command closeSubCommand() {
		if (parent == null) {
			throw new UnsupportedOperationException("This command has no parent");
		}
		return parent;
	}

	public Command closeSub() {
		return closeSubCommand();
	}

	public Command addCommandName(String name) {
		if (parent != null) {
			if (!parent.children.containsKey(name)) {
				parent.children.put(name, this);
			}
		}
		return this;
	}

	public Command name(String name) {
		return addCommandName(name);
	}

	public Command setHelpString(String help) {
		this.help = help;
		return this;
	}

	public Command help(String help) {
		return setHelpString(help);
	}

	@Override
	public Command setUsageString(String usage) {
		this.usage = usage;
		return this;
	}

	@Override
	public Command usage(String usage) {
		return setUsageString(usage);
	}

	public Command setExecutor(CommandExecutor executor) {
		this.executor = executor;
		return this;
	}

	public Command executor(CommandExecutor executor) {
		return setExecutor(executor);
	}

	public boolean execute(CommandSource source, String[] args, int baseIndex) {
		if (args.length > 1 && children.size() > 0) {
			Command sub = children.get(args[0]);
		}
		return true;
	}

	public String getPreferredName() {
		return aliases.get(0);
	}

	public String getUsageMessage(String[] input) {
		return help + " - " + usage;
	}
}
