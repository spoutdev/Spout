package org.getspout.api.command.annotated;

import org.getspout.api.command.*;
import org.getspout.api.command.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
			commandArgs.add(source);
			commandArgs.add(args);
			commandArgs.addAll(getAdditionalArgs(source, command));
			method.invoke(instance, commandArgs.toArray(new Object[commandArgs.size()]));
		} catch (IllegalAccessException e) {
			throw new WrappedCommandException(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() == null) {
				throw new WrappedCommandException(e);
			} else {
				Throwable cause = e.getCause();
				if (cause instanceof CommandException) {
					throw (CommandException)cause;
				} else {
					throw new WrappedCommandException(cause);
				}
			}
		}
		return true;
	}
	
	public abstract List<Object> getAdditionalArgs(CommandSource source, Command command);
}
