/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine.filesystem;

import org.spout.engine.filesystem.resource.loader.AnimationLoader;
import org.spout.engine.filesystem.resource.loader.BlockMeshLoader;
import org.spout.engine.filesystem.resource.loader.CubeMeshLoader;
import org.spout.engine.filesystem.resource.loader.EntityPrefabLoader;
import org.spout.engine.filesystem.resource.loader.FontLoader;
import org.spout.engine.filesystem.resource.loader.MeshLoader;
import org.spout.engine.filesystem.resource.loader.ModelLoader;
import org.spout.engine.filesystem.resource.loader.RenderMaterialLoader;
import org.spout.engine.filesystem.resource.loader.ShaderLoader;
import org.spout.engine.filesystem.resource.loader.SkeletonLoader;
import org.spout.engine.filesystem.resource.loader.SoundLoader;
import org.spout.engine.filesystem.resource.loader.TextureLoader;

public class ClientFileSystem extends CommonFileSystem {
	@Override
	public void init() {
		System.out.println("Registering loaders...");
		registerLoader(new AnimationLoader());
		registerLoader(new BlockMeshLoader());
		registerLoader(new CubeMeshLoader());
		registerLoader(new EntityPrefabLoader());
		registerLoader(new FontLoader());
		registerLoader(new MeshLoader());
		registerLoader(new ModelLoader());
		registerLoader(new RenderMaterialLoader());
		registerLoader(new ShaderLoader());
		registerLoader(new SkeletonLoader());
		registerLoader(new SoundLoader());
		registerLoader(new TextureLoader());
		super.init();
	}
}
