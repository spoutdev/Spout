/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.material.item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.getspout.api.material.BlockMaterial;
import org.getspout.api.material.CustomBlockMaterial;
import org.getspout.api.material.MaterialData;
import org.getspout.api.material.Tool;
import org.getspout.api.material.item.GenericCustomItemMaterial;
import org.getspout.api.plugin.Plugin;

import gnu.trove.map.hash.TObjectFloatHashMap;

public class GenericCustomTool extends GenericCustomItemMaterial implements Tool{
	private short durability = 1;
	private TObjectFloatHashMap<BlockMaterial> strengthMods = new TObjectFloatHashMap<BlockMaterial>();
	
	public GenericCustomTool(){
		super();
	}
	
	public GenericCustomTool(Plugin addon, String name, String texture) {
		super(addon, name, texture);
	}

	public short getDurability() {
		return durability;
	}

	public Tool setDurability(short durability) {
		this.durability = durability;
		return this;
	}

	public float getStrengthModifier(BlockMaterial block) {
		if (strengthMods.contains(block)) {
			return strengthMods.get(block);
		}
		return 1.0F;
	}

	public Tool setStrengthModifier(BlockMaterial block, float modifier) {
		strengthMods.put(block, modifier);
		return this;
	}

	public BlockMaterial[] getStrengthModifiedBlocks() {
		return (BlockMaterial[]) strengthMods.keys();
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 2 + 2 + strengthMods.size() * 10;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setDurability(input.readShort());
		short size = input.readShort();
		for (int i = 0; i < size; i++) {
			int id = input.readInt();
			int data = input.readShort();
			float mod = input.readFloat();
			BlockMaterial block = MaterialData.getBlock(id, (short) data);
			if (data == -1) {
				block = MaterialData.getCustomBlock(id);
			}
			setStrengthModifier(block, mod);
		}
	}
	
	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeShort(getDurability());
		BlockMaterial[] mod = getStrengthModifiedBlocks();
		output.writeShort(mod.length);
		for (int i = 0; i < mod.length; i++) {
			BlockMaterial block =  mod[i];
			if (block instanceof CustomBlockMaterial) {
				output.writeInt(((CustomBlockMaterial)block).getCustomId());
				output.writeShort(-1);
			}
			else {
				output.writeInt(block.getRawId());
				output.writeShort(block.getRawData());
			}
			output.writeFloat(getStrengthModifier(block));
		}
	}

	@Override
	public int getVersion() {
		return super.getVersion() + 0;
	}
	

}
