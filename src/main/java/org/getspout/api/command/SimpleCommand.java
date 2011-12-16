package org.getspout.api.command;

public class SimpleCommand implements Command {

	public Command addSubCommand(String primaryName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command sub(String primaryName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command closeSubCommand() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command closeSub() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command addCommandName(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command name(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command setHelpString(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command help(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command setExecutor(CommandExecutor executor) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command executor(CommandExecutor executor) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean execute(String[] args, int baseIndex) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getPreferredName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getUsageMessage() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
