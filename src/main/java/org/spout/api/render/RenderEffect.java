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
package org.spout.api.render;

/**
 * Renderer to attach to a Mesh to change the way the mesh renders
 *
 */
public interface RenderEffect {
	/**
	 * Called before the mesh has been batched.
	 *
	 * Used for setting the shader or texture.
	 */
	public void preBatch(Renderer batcher);

	/**
	 * Called after the mesh has been batched but before the batch has been
	 * flushed to the GPU
	 *
	 * Used to add additional verticies to the model
	 */
	public void postBatch(Renderer batcher);

	/**
	 * Called before the mesh is drawn to the scene. Used to set GPU modes
	 * and/or effects
	 */
	public void preDraw(Renderer batcher);

	/**
	 * Called after the mesh is drawn to the scene
	 *
	 * Used to clean up things done in preDraw()
	 */
	public void postDraw(Renderer batcher);
}
