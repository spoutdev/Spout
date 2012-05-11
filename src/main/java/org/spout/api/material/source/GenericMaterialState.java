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
package org.spout.api.material.source;

import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;

public class GenericMaterialState implements MaterialState {
	private Material material;
	private short data;

	public GenericMaterialState() {
		this.material = BlockMaterial.AIR;
		this.data = 0;
	}

	public GenericMaterialState(MaterialSource material) {
		this(material.getMaterial(), material.getData());
	}

	public GenericMaterialState(MaterialSource material, DataSource datasource) {
		this(material, datasource.getData());
	}

	public GenericMaterialState(MaterialSource material, int data) {
		this.setMaterial(material);
		this.setData(data);
	}

	@Override
	public GenericMaterialState setMaterial(MaterialSource material, DataSource datasource) {
		return this.setMaterial(material, datasource.getData());
	}

	@Override
	public GenericMaterialState setMaterial(MaterialSource material, int data) {
		this.setMaterial(material);
		this.setData(data);
		return this;
	}

	@Override
	public GenericMaterialState setData(DataSource datasource) {
		return this.setData(datasource.getData());
	}

	@Override
	public GenericMaterialState setMaterial(MaterialSource material) {
		this.material = material.getMaterial();
		return this;
	}
	
	@Override
	public GenericMaterialState setData(int data) {
		this.data = (short) data;
		return this;
	}

	@Override
	public Material getMaterial() {
		return this.material;
	}

	@Override
	public short getData() {
		return this.data;
	}

	@Override
	public Material getSubMaterial() {
		return this.getMaterial().getSubMaterial(this.getData());
	}
}

