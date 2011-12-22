package org.getspout.unchecked.api.command.annotated;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.getspout.unchecked.api.command.CommandRegistrationsFactory;
import org.getspout.unchecked.api.util.Named;

public class AnnotatedCommandRegistrationFactory implements CommandRegistrationsFactory<Class<?>> {

	/**
	 * An {@link Injector} used to create instances of command classes when
	 * non-static methods are used.
	 */
	private final Injector injector;

	private final AnnotatedCommandExecutorFactory executorFactory;

	public AnnotatedCommandRegistrationFactory(Injector injector, AnnotatedCommandExecutorFactory executorFactory) {
		this.injector = injector;
		this.executorFactory = executorFactory;
	}

	public boolean create(Named owner, Class<?> commands, org.getspout.unchecked.api.command.Command parent) {
		Object instance = null;
		if (injector != null) {
			instance = injector.newInstance(commands);
		}
		boolean success = true;
		for (Method method : commands.getClass().getMethods()) {
			// Simple checks
			if (!Modifier.isStatic(method.getModifiers()) && injector == null) {
				continue;
			}
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}

			Command command = method.getAnnotation(Command.class);
			if (command.aliases().length < 1) {
				return false;
			}
			org.getspout.unchecked.api.command.Command child = parent.addSubCommand(owner, command.aliases()[0]);
			for (String alias : command.aliases()) {
				child.addAlias(alias);
			}
			child.addFlags(command.flags());
			child.setUsage(command.usage());
			child.setHelp(command.desc());

			if (method.isAnnotationPresent(NestedCommand.class)) {
				for (Class<?> clazz : method.getAnnotation(NestedCommand.class).value()) {
					success &= create(owner, clazz, child);
				}
			} else {
				child.setExecutor(executorFactory.getAnnotatedCommandExecutor(instance, method));
			}
		}
		return success;
	}
}
