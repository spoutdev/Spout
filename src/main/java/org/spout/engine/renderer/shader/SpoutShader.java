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
package org.spout.engine.renderer.shader;

import java.util.List;

import org.spout.api.math.Matrix;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Shader;

public interface SpoutShader extends Shader{

	public abstract void enableAttribute(String name, int size, int type, int stride, long offset, int layout);

	public abstract void assign();

	public abstract RenderMaterial getMaterialAssigned();

	public abstract void setMaterialAssigned(RenderMaterial material);

	@Override
	public abstract void setUniform(String name, Matrix[] matricies);

	public abstract void checkUniform();

	public abstract void checkAttributes(List<Integer> attributesUsed);

}
