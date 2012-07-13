/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.command.annotated;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.plugin.Platform;
import org.spout.api.util.Named;

public class AnnotatedCommandRegistrationFactory implements CommandRegistrationsFactory<Class<?>> {
	/**
	 * An {@link Injector} used to create instances of command classes when
	 * non-static methods are used.
	 */
	private final Injector injector;

	private final AnnotatedCommandExecutorFactory executorFactory;

	public AnnotatedCommandRegistrationFactory() {
		this(null, new SimpleAnnotatedCommandExecutorFactory());
	}

	public AnnotatedCommandRegistrationFactory(Injector injector) {
		this(injector, new SimpleAnnotatedCommandExecutorFactory());
	}

	public AnnotatedCommandRegistrationFactory(AnnotatedCommandExecutorFactory executorFactory) {
		this(null, executorFactory);
	}

	public AnnotatedCommandRegistrationFactory(Injector injector, AnnotatedCommandExecutorFactory executorFactory) {
		this.injector = injector;
		this.executorFactory = executorFactory;
	}

	public boolean create(Named owner, Class<?> commands, org.spout.api.command.Command parent) {
		Object instance = null;
		if (injector != null) {
			instance = injector.newInstance(commands);
		}

		boolean success = methodRegistration(owner, commands, instance, parent);
		success &= nestedClassRegistration(owner, commands, instance, parent);
		return success;
	}

	private org.spout.api.command.Command createCommand(Named owner, org.spout.api.command.Command parent, AnnotatedElement obj) {
		if (!obj.isAnnotationPresent(Command.class)) {
			return null;
		}

		Command command = obj.getAnnotation(Command.class);
		if (command.aliases().length < 1) {
			throw new IllegalArgumentException("Command must have at least one alias");
		}
		org.spout.api.command.Command child = parent.addSubCommand(owner, command.aliases()[0]).addAlias(command.aliases()).addFlags(command.flags()).setUsage(command.usage()).setHelp(command.desc()).setArgBounds(command.min(), command.max());

		if (obj.isAnnotationPresent(CommandPermissions.class)) {
			CommandPermissions perms = obj.getAnnotation(CommandPermissions.class);
			child.setPermissions(perms.requireAll(), perms.value());
		}
		return child;
	}

	private boolean methodRegistration(Named owner, Class<?> commands, Object instance, org.spout.api.command.Command parent) {
		boolean success = true;
		for (Method method : commands.getDeclaredMethods()) {
			// Basic checks
			method.setAccessible(true);
			if (!Modifier.isStatic(method.getModifiers()) && instance == null) {
				continue;
			}
			org.spout.api.command.Command child = createCommand(owner, parent, method);
			if (child == null) {
				continue;
			}

			if (method.isAnnotationPresent(NestedCommand.class)) {
				for (Class<?> clazz : method.getAnnotation(NestedCommand.class).value()) {
					success &= create(owner, clazz, child);
				}
			} else {
				child.setExecutor(executorFactory.getAnnotatedCommandExecutor(instance, method));
			}
			child.closeSubCommand();
		}
		return success;
	}

	@SuppressWarnings("unchecked")
	public <T> Constructor<T> getClosestConstructor(Class<T> clazz, Class<?>... args) {
		constructors: for (Constructor constructor : clazz.getDeclaredConstructors()) {
			Class<?>[] classes = constructor.getParameterTypes();
			if (classes.length != args.length) {
				continue;
			}

			for (int i = 0; i < args.length; ++i) {
				if (!args[i].isAssignableFrom(classes[i])) {
					continue constructors;
				}
			}
			return constructor;
		}
		return null;
	}

	private boolean nestedClassRegistration(Named owner, Class<?> commands, Object instance, org.spout.api.command.Command parent) {
		boolean success = true, anyRegistered = false;
		for (Class<?> clazz : commands.getDeclaredClasses()) {
			Object subInstance = null;
			if (!Modifier.isStatic(clazz.getModifiers())) {
				try {
					Constructor<?> constr = getClosestConstructor(clazz, commands);
					if (constr == null) {
						continue;
					}

					constr.setAccessible(true);
					subInstance = constr.newInstance(instance);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					continue;
				} catch (InstantiationException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException ignore) {
				}
			}

			org.spout.api.command.Command child = createCommand(owner, parent, clazz);
			if (child == null) {
				continue;
			}
			anyRegistered = true;

			if (!nestedClassRegistration(owner, clazz, subInstance, child)) {
				for (Method method : clazz.getDeclaredMethods()) {
					if (!method.isAnnotationPresent(Executor.class)) {
						continue;
					}

					Platform platform = method.getAnnotation(Executor.class).value();
					child.setExecutor(platform, executorFactory.getAnnotatedCommandExecutor(subInstance, method));
				}
			}
		}
		return success && anyRegistered;
	}
}
