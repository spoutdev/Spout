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
package org.spout.api;

import org.mockito.Mockito;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.command.CommandManager;
import org.spout.api.resource.FileSystem;

public class EngineFaker {
	private final static Engine engineInstance;

	static {
		Engine engine = Mockito.mock(Engine.class);
		FileSystem filesystem = Mockito.mock(FileSystem.class);
		
		Mockito.when(filesystem.getResource(Mockito.anyString())).thenReturn(null);
		
		Mockito.when(engine.getPlatform()).thenReturn(Platform.SERVER);
		Mockito.when(engine.getFileSystem()).thenReturn(filesystem);
		Mockito.when(engine.getCommandManager()).thenReturn(new CommandManager());

		Spout.setEngine(engine);
		engineInstance = engine;
	}

	public static Engine setupEngine() {
		return engineInstance;
	}
}
