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
package org.spout.api.material;

import org.spout.api.geo.World;
import org.spout.api.math.AABoundingBox;
import org.spout.api.math.AABoundingBoxm;

public class GenericBlockMaterial extends GenericItemMaterial implements BlockMaterial {
	private final AABoundingBoxm area = new AABoundingBoxm(0F, 0F, 0F, 1F, 1F, 1F);
	private float hardness = 0F;
	private float friction = 0F;
	private boolean opaque = true;
	private int lightLevel = 0;

	private GenericBlockMaterial(String name, int id, int data, boolean subtypes) {
		super(name, id, data, subtypes);
	}

	protected GenericBlockMaterial(String name, int id, int data) {
		this(name, id, data, true);
	}

	protected GenericBlockMaterial(String name, int id) {
		this(name, id, 0, false);
	}

	public float getFriction() {
		return friction;
	}

	public BlockMaterial setFriction(float friction) {
		this.friction = friction;
		return this;
	}

	public float getHardness() {
		return hardness;
	}

	public BlockMaterial setHardness(float hardness) {
		this.hardness = hardness;
		return this;
	}

	public boolean isOpaque() {
		return opaque;
	}

	public BlockMaterial setOpaque(boolean opaque) {
		this.opaque = opaque;
		return this;
	}

	public int getLightLevel() {
		return lightLevel;
	}

	public BlockMaterial setLightLevel(int level) {
		lightLevel = level;
		return this;
	}

	public void onWorldRender() {
		// TODO Auto-generated method stub
	}

	public boolean isLiquid() {
		return false;
	}

	public boolean hasPhysics() {
		return false;
	}
	
	@Override
	public AABoundingBox getBoundingArea() {
		return area;
	}
	
	public BlockMaterial setBoundingArea(AABoundingBox box) {
		area.set(box);
		return this;
	}

	public void onUpdate(World world, int x, int y, int z) {

	}

	public void onDestroy(World world, int x, int y, int z) {

	}
}
