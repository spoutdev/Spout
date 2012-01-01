package org.getspout.api.command;

import org.getspout.api.ChatColor;
import org.getspout.api.Game;
import org.getspout.api.SpoutRuntimeException;

public class RootCommand extends SimpleCommand {

	public RootCommand(Game owner) {
		super(owner, "root");
	}

	@Override
	public String getUsage(String[] input, int baseIndex) {
		return ChatColor.RED + "Command '" + (input.length > baseIndex ? input[baseIndex] : getPreferredName()) + "' could not be found";
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
