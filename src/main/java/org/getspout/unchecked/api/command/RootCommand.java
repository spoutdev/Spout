package org.getspout.unchecked.api.command;

import org.bukkit.ChatColor;
import org.getspout.unchecked.api.Game;
import org.getspout.unchecked.api.SpoutRuntimeException;

public class RootCommand extends SimpleCommand {

	public RootCommand(Game owner) {
		super(owner, "root");
	}

	@Override
	public String getUsage(String[] input) {
		return ChatColor.GRAY + "Command '" + (input.length > 0 ? input[0] : getPreferredName()) + "' could not be found";
	}

	@Override
	public Command closeSubCommand() {
		throw new SpoutRuntimeException("The root command has no parent.");
	}

	@Override
	public boolean isLocked() {
		return false;
	}
}
