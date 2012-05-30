/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.renderer.vertexformat;

import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;

public class PositionNormalTexture extends VertexFormat {
	Vector3 position;
	Vector3 normal;
	Vector2 texture;

	public PositionNormalTexture(Vector3 position, Vector3 normal, Vector2 uv) {
		this.position = position;
		this.normal = normal;
		this.texture = uv;
	}

	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getNormal() {
		return normal;
	}

	public Vector2 getTexture() {
		return texture;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public void setNormal(Vector3 normal) {
		this.normal = normal;
	}

	public void setTexture(Vector2 texture) {
		this.texture = texture;
	}
}
