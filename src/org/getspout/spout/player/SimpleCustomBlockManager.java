package org.getspout.spout.player;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;

import org.getspout.spout.block.SpoutCustomBlock;
import org.getspout.spoutapi.packet.PacketBlockTextures;
import org.getspout.spoutapi.player.CustomBlockManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleCustomBlockManager implements CustomBlockManager {

	public List<Integer> customBlocks = new ArrayList<Integer>();
	public List<PacketBlockTextures> packetQueue = new ArrayList<PacketBlockTextures>();
	public List<SpoutCustomBlock> customBlockClasses = new ArrayList<SpoutCustomBlock>();
	
	@Override
	public void clearBlocks() {
		for (int x : customBlocks) {
			Item.byId[x] = null;
		}
	}
	
	public void createBlockClass(int id, net.minecraft.server.Material mat) {
		customBlockClasses.add(new SpoutCustomBlock(id, mat));
	}
	
	@Override
	public int createCustomBlock(byte arg0, int[] arg1, int arg2) {
		int id = 97;
		if (customBlocks.size() != 0) {
			id = customBlocks.get(customBlocks.size()-1)+1;
		}
		createBlockClass(id, net.minecraft.server.Material.WOOD);
		Item.byId[id] = new ItemBlock(id-256);
		addToQueue(new PacketBlockTextures(id,arg1,arg0));
		return id;
	}

	@Override
	public void sendPacket(SpoutPlayer arg0) {
		for (PacketBlockTextures current : packetQueue) {
			arg0.sendPacket(current);
		}
	}
	
	private void addToQueue(PacketBlockTextures pbt) {
		packetQueue.add(pbt);
	}
}