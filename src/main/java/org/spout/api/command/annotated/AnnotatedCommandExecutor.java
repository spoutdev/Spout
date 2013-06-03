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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.spout.api.command.Command;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.Executor;
import org.spout.api.exception.CommandException;
import org.spout.api.exception.WrappedCommandException;

/**
 * Allows for method-registration of commands.
 */
public final class AnnotatedCommandExecutor implements Executor {
	private final Object instance;
	private final Map<Command, Method> cmdMap;

	protected AnnotatedCommandExecutor(Object instance, Map<Command, Method> cmdMap) {
		this.instance = instance;
		this.cmdMap = cmdMap;
	}

	@Override
	public void execute(CommandSource source, Command command, CommandArguments args) throws CommandException {
		Method method = cmdMap.get(command);
		if (method != null) {
			method.setAccessible(true);
			try {
				method.invoke(instance, source, args);
			} catch (IllegalAccessException e) {
				throw new WrappedCommandException(e);
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				if (cause == null) {
					throw new WrappedCommandException(e);
				}

				if (cause instanceof CommandException) {
					throw (CommandException) cause;
				}

				throw new WrappedCommandException(e);
			}
		}
	}
}
