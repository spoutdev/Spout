/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.material;

public class GenericBlockMaterial extends GenericItemMaterial implements BlockMaterial {

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
		this.lightLevel = level;
		return this;
	}

	public void onWorldRender() {
		// TODO Auto-generated method stub
	}

	public boolean isLiquid() {
		return false;
	}
}
