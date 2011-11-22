/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.inventory;

import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TIntByteHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spout.block.mcblock.CustomMCBlock;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.MaterialManager;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.packet.PacketBlockData;
import org.getspout.spoutapi.packet.PacketItemName;
import org.getspout.spoutapi.packet.SpoutPacket;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.util.map.TIntPairFloatHashMap;
import org.getspout.spoutapi.util.map.TIntPairHashSet;
import org.getspout.spoutapi.util.map.TIntPairObjectHashMap;

public abstract class AbstractBlockManager implements MaterialManager{
	protected final TIntPairObjectHashMap<String> customNames = new TIntPairObjectHashMap<String>(100);
	
	protected final TIntPairFloatHashMap originalHardness = new TIntPairFloatHashMap();
	protected final TIntPairFloatHashMap originalFriction = new TIntPairFloatHashMap();
	protected final TIntByteHashMap originalOpacity = new TIntByteHashMap();
	protected final TIntIntHashMap originalLight = new TIntIntHashMap();
	protected Set<org.getspout.spoutapi.material.Block> cachedBlockData = null;
	
	@Override
	public void reset() {
		customNames.clear();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutCraftPlayer) {
				if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
					((SpoutPlayer) player).sendPacket(new PacketItemName(0, (short) 0, "[resetall]"));
				}
			}
		}
	}
	

	public void onPlayerJoin(SpoutPlayer player) {
		if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
			for (TLongObjectIterator<String> it = customNames.iterator(); it.hasNext();) {
				it.advance();
				((SpoutPlayer) player).sendPacket(new PacketItemName(TIntPairHashSet.longToKey1(it.key()), (short) TIntPairHashSet.longToKey2(it.key()), it.value()));
			}
		}
	}
	
	@Override
	public void setItemName(Material item, String name) {
		customNames.put(item.getRawId(), item.getRawData(), name);
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player instanceof SpoutCraftPlayer) {
				if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
					((SpoutPlayer) player).sendPacket(new PacketItemName(item.getRawId(), (short) item.getRawData(), name));
				}
			}
		}
	}

	@Override
	public void resetName(Material item) {
		int id = item.getRawId();
		int data = item.getRawData();
		if (customNames.containsKey(id, data)) {
			customNames.remove(id, data);
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (player instanceof SpoutCraftPlayer) {
					if (((SpoutPlayer) player).isSpoutCraftEnabled()) {
						((SpoutPlayer) player).sendPacket(new PacketItemName(id, (short) data, "[reset]"));
					}
				}
			}
		}
	}

	@Override
	public String getStepSound(org.getspout.spoutapi.material.Block block) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStepSound(org.getspout.spoutapi.material.Block block, String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetStepSound(org.getspout.spoutapi.material.Block block) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getFriction(org.getspout.spoutapi.material.Block block) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		return net.minecraft.server.Block.byId[id].frictionFactor;
	}

	@Override
	public void setFriction(org.getspout.spoutapi.material.Block block, float friction) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		int data = block.getRawData();
		if (!originalFriction.containsKey(id, data)) {
			originalFriction.put(id, data, getFriction(block));
		}
		net.minecraft.server.Block.byId[id].frictionFactor = friction;
		updateBlockAttributes(id, (short) data); // invalidate cache
	}

	@Override
	public void resetFriction(org.getspout.spoutapi.material.Block block) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		int data = block.getRawData();
		if (originalFriction.containsKey(id, data)) {
			setFriction(block, originalFriction.get(id, data));
			originalFriction.remove(id, data);
		}
		updateBlockAttributes(id, (short) data); // invalidate cache
	}

	@Override
	public float getHardness(org.getspout.spoutapi.material.Block block) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		return net.minecraft.server.Block.byId[id].l();
	}

	@Override
	public void setHardness(org.getspout.spoutapi.material.Block block, float hardness) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		int data = block.getRawData();
		if (!originalHardness.containsKey(id, data)) {
			originalHardness.put(id, data, getHardness(block));
		}
		net.minecraft.server.Block b = net.minecraft.server.Block.byId[id];
		if (b instanceof CustomMCBlock) {
			((CustomMCBlock) b).setHardness(hardness);
		}
		updateBlockAttributes(id, (short) data); // invalidate cache
	}

	@Override
	public void resetHardness(org.getspout.spoutapi.material.Block block) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		int data = block.getRawData();
		if (originalHardness.containsKey(id, data)) {
			setHardness(block, originalHardness.get(id, data));
			originalHardness.remove(id, data);
		}
		updateBlockAttributes(id, (short) data); // invalidate cache
	}

	@Override
	public boolean isOpaque(org.getspout.spoutapi.material.Block block) {
		int id = block.getRawId();
		return net.minecraft.server.Block.o[id];
	}

	@Override
	public void setOpaque(org.getspout.spoutapi.material.Block block, boolean opacity) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		int data = block.getRawData();
		if (!originalOpacity.containsKey(id)) {
			originalOpacity.put(id, (byte) (isOpaque(block) ? 1 : 0));
		}
		net.minecraft.server.Block.o[id] = opacity;
		updateBlockAttributes(id, (short) data); // invalidate cache
	}

	@Override
	public void resetOpacity(org.getspout.spoutapi.material.Block block) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		int data = block.getRawData();
		if (originalOpacity.containsKey(id)) {
			setOpaque(block, originalOpacity.get(id) != 0);
			originalOpacity.remove(id);
		}
		updateBlockAttributes(id, (short) data); // invalidate cache
	}

	@Override
	public int getLightLevel(org.getspout.spoutapi.material.Block block) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		return net.minecraft.server.Block.s[id];
	}

	@Override
	public void setLightLevel(org.getspout.spoutapi.material.Block block, int level) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		int data = block.getRawData();
		if (!originalLight.containsKey(id)) {
			originalLight.put(id, getLightLevel(block));
		}
		net.minecraft.server.Block.s[id] = level;
		updateBlockAttributes(id, (short) data); // invalidate cache
	}

	@Override
	public void resetLightLevel(org.getspout.spoutapi.material.Block block) {
		int id = block.getRawId();
		if(block instanceof CustomBlock) {
			id = ((CustomBlock) block).getBlockId();
		}
		int data = block.getRawData();
		if (originalLight.containsKey(id)) {
			setLightLevel(block, originalLight.get(id));
			originalLight.remove(id);
		}
		updateBlockAttributes(id, (short) data); // invalidate cache
	}

	@Override
	public Set<org.getspout.spoutapi.material.Block> getModifiedBlocks() {
		// hit cache first
		if (cachedBlockData != null) {
			return cachedBlockData;
		}
		Set<org.getspout.spoutapi.material.Block> modified = new HashSet<org.getspout.spoutapi.material.Block>();
		TLongFloatIterator i = originalFriction.iterator();
		while (i.hasNext()) {
			i.advance();
			int id = TIntPairHashSet.longToKey1(i.key());
			int data = TIntPairHashSet.longToKey2(i.key());

			org.getspout.spoutapi.material.Block block = MaterialData.getBlock(id, (short) data);
			if (block != null) {
				modified.add(block);
			}
		}

		i = originalHardness.iterator();
		while (i.hasNext()) {
			i.advance();
			int id = TIntPairHashSet.longToKey1(i.key());
			int data = TIntPairHashSet.longToKey2(i.key());
			org.getspout.spoutapi.material.Block block = MaterialData.getBlock(id, (short) data);
			if (block != null) {
				modified.add(block);
			}
		}

		TIntIntIterator j = originalLight.iterator();
		while (j.hasNext()) {
			j.advance();
			org.getspout.spoutapi.material.Block block = MaterialData.getBlock(j.key());
			if (block != null) {
				modified.add(block);
			}
		}

		TIntByteIterator k = originalOpacity.iterator();
		while (k.hasNext()) {
			k.advance();
			org.getspout.spoutapi.material.Block block = MaterialData.getBlock(k.key());
			if (block != null) {
				modified.add(block);
			}
		}
		cachedBlockData = modified; // save to cache
		return modified;
	}

	private void updateBlockAttributes(int id, short data) {
		org.getspout.spoutapi.material.Block block = MaterialData.getBlock(id, data);
		if (block != null) {
			cachedBlockData = null;
			HashSet<org.getspout.spoutapi.material.Block> toUpdate = new HashSet<org.getspout.spoutapi.material.Block>(1);
			toUpdate.add(block);
			SpoutPacket updatePacket = new PacketBlockData(toUpdate);
			for (SpoutPlayer player : SpoutManager.getOnlinePlayers()) {
				if (player.isSpoutCraftEnabled())
					player.sendPacket(updatePacket);
			}
		}
	}
	
}
