/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.util.Named;

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

	public boolean create(Named owner, Class<?> commands, org.spout.api.command.Command parent) {
		Object instance = null;
		if (injector != null) {
			instance = injector.newInstance(commands);
		}
		boolean success = true;
		for (Method method : commands.getMethods()) {
			// Basic checks
			if (!Modifier.isStatic(method.getModifiers()) && instance == null) {
				continue;
			}
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}

			Command command = method.getAnnotation(Command.class);
			if (command.aliases().length < 1) {
				success = false;
				continue;
			}
			org.spout.api.command.Command child = parent.addSubCommand(owner, command.aliases()[0]).addAlias(command.aliases()).addFlags(command.flags()).setUsage(command.usage()).setHelp(command.desc()).setArgBounds(command.min(), command.max());

			if (method.isAnnotationPresent(CommandPermissions.class)) {
				CommandPermissions perms = method.getAnnotation(CommandPermissions.class);
				child.setPermissions(perms.requireAll(), perms.value());
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
}
