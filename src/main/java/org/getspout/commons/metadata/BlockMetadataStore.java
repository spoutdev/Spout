/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.metadata;

import java.util.List;
import org.getspout.commons.World;
import org.getspout.commons.block.Block;
import org.getspout.commons.plugin.Plugin;

/**
 * A BlockMetadataStore stores metadata values for {@link Block} objects.
 */
public class BlockMetadataStore extends MetadataStoreBase<Block> implements MetadataStore<Block> {

	private World owningWorld;

	/**
	 * Initializes a BlockMetadataStore.
	 * @param owningWorld The world to which this BlockMetadataStore belongs.
	 */
	public BlockMetadataStore(World owningWorld) {
		this.owningWorld = owningWorld;
	}

	/**
	 * Generates a unique metadata key for a {@link Block} object based on its coordinates in the world.
	 * @see MetadataStoreBase#Disambiguate(Object, String)
	 * @param block
	 * @param metadataKey
	 * @return
	 */
	@Override
	protected String disambiguate(Block block, String metadataKey) {
		return Integer.toString(block.getX()) + ":" + Integer.toString(block.getY()) + ":" + Integer.toString(block.getZ()) + ":" + metadataKey;
	}

	/**
	 * Retrieves the metadata for a {@link Block}, ensuring the block being asked for actually belongs to this BlockMetadataStore's
	 * owning world.
	 * @see MetadataStoreBase#getMetadata(Object, String)
	 * @param block
	 * @param metadataKey
	 * @return
	 */
	@Override
	public List<MetadataValue> getMetadata(Block block, String metadataKey) {
		if (block.getWorld() == owningWorld) {
			return super.getMetadata(block, metadataKey);
		} else {
			throw new IllegalArgumentException("Block does not belong to world " + owningWorld.getName());
		}
	}

	/**
	 * Tests to see if a metadata value has been added to a {@link Block}, ensuring the block being interrogated belongs
	 * to this BlockMetadataStore's owning world.
	 * @see MetadataStoreBase#hasMetadata(Object, String)
	 * @param block
	 * @param metadataKey
	 * @return
	 */
	@Override
	public boolean hasMetadata(Block block, String metadataKey) {
		if (block.getWorld() == owningWorld) {
			return super.hasMetadata(block, metadataKey);
		} else {
			throw new IllegalArgumentException("Block does not belong to world " + owningWorld.getName());
		}
	}

	/**
	 * Removes metadata from from a {@link Block} belonging to a given {@link Plugin}, ensuring the block being deleted from belongs
	 * to this BlockMetadataStore's owning world.
	 * @see MetadataStoreBase#removeMetadata(Object, String, org.bukkit.plugin.Plugin)
	 * @param block
	 * @param metadataKey
	 * @param owningPlugin
	 */
	@Override
	public void removeMetadata(Block block, String metadataKey, Plugin owningPlugin) {
		if (block.getWorld() == owningWorld) {
			super.removeMetadata(block, metadataKey, owningPlugin);
		} else {
			throw new IllegalArgumentException("Block does not belong to world " + owningWorld.getName());
		}
	}

	/**
	 * Sets or overwrites a metadata value on a {@link Block} from a given {@link Plugin}, ensuring the target block belongs
	 * to this BlockMetadataStore's owning world.
	 * @param block
	 * @param metadataKey A unique key to identify this metadata.
	 * @param newMetadataValue
	 */
	@Override
	public void setMetadata(Block block, String metadataKey, MetadataValue newMetadataValue) {
		if (block.getWorld() == owningWorld) {
			super.setMetadata(block, metadataKey, newMetadataValue);
		} else {
			throw new IllegalArgumentException("Block does not belong to world " + owningWorld.getName());
		}
	}

}
