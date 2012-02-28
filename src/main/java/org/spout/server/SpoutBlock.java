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
package org.spout.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.entity.MaterialController;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.MaterialData;

public class SpoutBlock extends Block {
	BlockMaterial material = null;
	Entity mEntity;

	public SpoutBlock(World world, int x, int y, int z) {
		super(world, x, y, z);
	}

	public SpoutBlock(World world, int x, int y, int z, short id) {
		this(world, x, y, z);
		setBlockId(id);
	}

	public SpoutBlock(World world, int x, int y, int z, BlockMaterial material) {
		this(world, x, y, z);
		setBlockMaterial(material);
	}

	//TODO: set id at world location too!
	@Override
	public BlockMaterial setBlockMaterial(BlockMaterial material) {
		setBlockId(material.getId());
		return material;
	}

	@Override
	public short setBlockId(short id) {
		BlockMaterial newMat = MaterialData.getBlock(id);
		if (newMat != null) {
			material = newMat;
		}
		if(material.hasMaterialEntity()) {
			try {
				setMaterialEntity(material.getMaterialEntity().newInstance());
			} catch (Exception ex) {
				Spout.getGame().getLogger().log(Level.SEVERE, "Tried to set invalid Material entity for block at "+this.getX()+" "+this.getY()+" "+this.getZ()+"!");
				Logger.getLogger(SpoutBlock.class.getName()).log(Level.SEVERE, null, ex);
			} 
		}
		return material != null ? material.getId() : 0;
	}

	@Override
	public BlockMaterial getBlockMaterial() {
		return material;
	}

	@Override
	public short getBlockId() {
		return material.getId();
	}

	@Override
	public BlockMaterial getLiveBlockMaterial() {
		return getBlockMaterial();
	}

	@Override
	public short getLiveBlockId() {
		return getBlockId();
	}

	@Override
	public void setMaterialEntity(MaterialController materialEntity) {
		if(mEntity == null) {
			mEntity = this.getWorld().createEntity(base, materialEntity);
		}
		else {
			if(mEntity.getController() instanceof MaterialController) {
				materialEntity.inherit((MaterialController) mEntity.getController());
			}
			mEntity.setController(materialEntity);
		}
	}

	@Override
	public MaterialController getMaterialEntity() {
		if(mEntity==null)
			return null;
		if(!(mEntity.getController() instanceof MaterialController))
			return null;
		return (MaterialController) mEntity.getController();
	}
}
