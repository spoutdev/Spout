/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.command.annotated;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;

public final class AnnotatedCommandExecutorFactory {
	private AnnotatedCommandExecutorFactory() {
	}

	private static boolean isValidMethod(Method method) {
		return hasValidModifiers(method) && hasValidParameters(method) && hasCommandAnnotation(method);
	}

	private static boolean hasValidModifiers(Method method) {
		int mod = method.getModifiers();
		return !Modifier.isAbstract(mod) && !Modifier.isPrivate(mod) && !Modifier.isProtected(mod)
				&& !Modifier.isStatic(mod) && Modifier.isPublic(mod);
	}

	private static boolean hasValidParameters(Method method) {
		Class<?>[] params = method.getParameterTypes();
		return params.length == 2 && CommandSource.class.equals(params[0]) && CommandArguments.class.equals(params[1]);
	}

	private static boolean hasCommandAnnotation(Method method) {
		return method.isAnnotationPresent(org.spout.api.command.annotated.Command.class);
	}

	/**
	 * Registers all the defined commands by method in this class.
	 *
	 * @param parent to register commands under
	 */
	public static AnnotatedCommandExecutor create(Object instance, org.spout.api.command.Command parent) {
		Map<org.spout.api.command.Command, Method> cmdMap = new HashMap<org.spout.api.command.Command, Method>();
		for (Method method : instance.getClass().getMethods()) {
			method.setAccessible(true);
			// check the validity of the current method
			if (!isValidMethod(method)) {
				continue;
			}

			// create the command
			Engine engine = Spout.getEngine();
			org.spout.api.command.annotated.Command a = method.getAnnotation(org.spout.api.command.annotated.Command.class);
			org.spout.api.command.Command command;
			if (parent != null) { // parent specified? create child
				command = parent.getChild(a.aliases()[0]);
			} else { // no parent specified? create normal command
				command = engine.getCommandManager().getCommand(a.aliases()[0]);
			}

			// set annotation data
			command.addAlias(a.aliases());
			command.setHelp(a.desc());
			command.setUsage(a.usage());
			command.setArgumentBounds(a.min(), a.max());

			// check the platform
			if (method.isAnnotationPresent(Platform.class)) {
				Platform pa = method.getAnnotation(Platform.class);
				org.spout.api.Platform actual = Spout.getPlatform();
				boolean success = false;
				for (org.spout.api.Platform platform : pa.value()) {
					if (platform == actual) {
						success = true;
						break;
					}
				}

				if (!success) {
					// current platform not supported for this command, skip it.
					continue;
				}
			}

			// add the permissions
			if (method.isAnnotationPresent(Permissible.class)) {
				command.setPermission(method.getAnnotation(Permissible.class).value());
			}

			// add binding
			// you can still have a binding annotation on a server command method but this block will be skipped
			if (method.isAnnotationPresent(Binding.class) && engine instanceof Client) {
				int max = a.max();
				if (max < 1 && max != -1) {
					throw new IllegalArgumentException("Command binding must allow at least 1 argument.");
				}
				Binding binding = method.getAnnotation(Binding.class);
				org.spout.api.input.Binding b = new org.spout.api.input.Binding(command.getName(), binding.value(), binding.mouse()).setAsync(binding.async());
				((Client) engine).getInputManager().bind(b);
			}

			// add filter
			if (method.isAnnotationPresent(Filter.class)) {
				Filter cfa = method.getAnnotation(Filter.class);
				Class<? extends org.spout.api.command.filter.CommandFilter>[] filterTypes = cfa.value();
				org.spout.api.command.filter.CommandFilter[] filters = new org.spout.api.command.filter.CommandFilter[filterTypes.length];
				for (int i = 0; i < filters.length; i++) {
					try {
						filters[i] = filterTypes[i].newInstance();
					} catch (InstantiationException e) {
						throw new IllegalArgumentException("All CommandFilters must have an empty constructor.");
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				command.addFilter(filters);
			}

			// put the command in our map
			cmdMap.put(command, method);
		}

		// set the executor of the commands
		AnnotatedCommandExecutor exe = new AnnotatedCommandExecutor(instance, cmdMap);
		for (org.spout.api.command.Command cmd : cmdMap.keySet()) {
			cmd.setExecutor(exe);
		}

		return exe;
	}

	/**
	 * Registers all the defined commands by method in this class.
	 */
	public static void create(Object instance) {
		create(instance, null);
	}
}
