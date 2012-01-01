package org.getspout.api.command.annotated;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.getspout.api.command.Command;
import org.getspout.api.command.CommandContext;
import org.getspout.api.command.CommandException;
import org.getspout.api.command.CommandExecutor;
import org.getspout.api.command.CommandSource;
import org.getspout.api.command.WrappedCommandException;

public abstract class AnnotatedCommandExecutor implements CommandExecutor {
	private final Object instance;
	private final Method method;

	public AnnotatedCommandExecutor(Object instance, Method method) {
		this.instance = instance;
		this.method = method;
	}

	public boolean processCommand(CommandSource source, Command command, CommandContext args) throws CommandException {
		try {
			List<Object> commandArgs = new ArrayList<Object>(4);
			commandArgs.add(args);
			commandArgs.add(source);
			commandArgs.addAll(getAdditionalArgs(source, command));
			method.invoke(instance, (Object[])commandArgs.toArray());
		} catch (IllegalAccessException e) {
			throw new WrappedCommandException(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() == null) {
				throw new WrappedCommandException(e);
			} else {
				Throwable cause = e.getCause();
				if (cause instanceof CommandException) {
					throw (CommandException) cause;
				} else {
					throw new WrappedCommandException(cause);
				}
			}
		}
		return true;
	}

	public abstract List<Object> getAdditionalArgs(CommandSource source, Command command);
}
