package org.getspout.api.command;

public class SimpleCommand implements Command {
	public Command addSubCommand(Enum<?> commandEnum) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command sub(Enum<?> commandEnum) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command closeSubCommand() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command closeSub() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Command setCommandName(String name) {
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
