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
package org.spout.api.inventory.recipe;

import org.powermock.api.mockito.PowerMockito;
import org.spout.api.Engine;
import org.spout.api.FileSystem;
import org.spout.api.Spout;
import org.spout.api.plugin.Platform;

@SuppressWarnings("deprecation")
public class EngineFaker {
	
	private final static Engine engineInstance;
	
	static {
		Engine engine = PowerMockito.mock(Engine.class);
		FileSystem filesystem = PowerMockito.mock(FileSystem.class);
		try {
			PowerMockito.when(engine, Engine.class.getMethod("getPlatform", (Class[])null)).withNoArguments().thenReturn(Platform.SERVER);
			PowerMockito.stub(Engine.class.getMethod("getFilesystem", (Class[])null)).andReturn(filesystem);
			PowerMockito.stub(FileSystem.class.getMethod("getResource", new Class[] {String.class})).andReturn(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (engine == null) throw new NullPointerException("Engine is null");
		if (engine.getPlatform() == null) throw new NullPointerException("Platform is null");
		Spout.setEngine(engine);
		engineInstance = engine;
	}
	
	public static Engine setupEngine() {
		return engineInstance;
	}
}
