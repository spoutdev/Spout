package org.getspout.spout.block;

import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChunk;

public class SpoutCraftBlock extends CraftBlock implements SpoutBlock {
	protected final int x, y, z;
	protected final SpoutCraftChunk chunk;
	public SpoutCraftBlock(SpoutCraftChunk chunk, int x, int y, int z) {
		super(chunk, x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
		this.chunk = chunk;
	}

	@Override
	public SpoutChunk getChunk() {
		return chunk;
	}
	
	@Override
	public void setTypeAsync(Material type) {
		setTypeIdAsync(type.getId());
	}
	
	@Override
	public void setTypeIdAsync(int type) {
		chunk.queuedId.put(getIndex(), type);
		SpoutCraftChunk.queuedChunks.add(chunk);
		final byte data = getData();
		for (Player player : chunk.getWorld().getPlayers()) {
			player.sendBlockChange(getLocation(), type, data);
		}
	}
	
	@Override
	public void setDataAsync(byte data) {
		chunk.queuedData.put(getIndex(), data);
		SpoutCraftChunk.queuedChunks.add(chunk);
		final int type = getTypeId();
		for (Player player : chunk.getWorld().getPlayers()) {
			player.sendBlockChange(getLocation(), type, data);
		}
	}
	
	@Override
	public void setTypeIdAndDataAsync(int type, byte data) {
		chunk.queuedId.put(getIndex(), type);
		chunk.queuedData.put(getIndex(), data);
		SpoutCraftChunk.queuedChunks.add(chunk);
		for (Player player : chunk.getWorld().getPlayers()) {
			player.sendBlockChange(getLocation(), type, data);
		}
	}
	
	@Override
	public Serializable setData(String id, Serializable data) {
		return SpoutManager.getChunkDataManager().setBlockData(id, getWorld(), getX(), getY(), getZ(), data);
	}
	
	@Override
	public Serializable getData(String id) {
		return SpoutManager.getChunkDataManager().getBlockData(id, getWorld(), getX(), getY(), getZ());
	}
	
	@Override
	public Serializable removeData(String id) {
		return SpoutManager.getChunkDataManager().removeBlockData(id, getWorld(), getX(), getY(), getZ());
	}
	
	@Override
	public BlockState getState() {
		Material material = getType();

		switch (material) {
			case CHEST:
				return new SpoutCraftChest(this);
			default:
				return super.getState();
		}
	}
	
	@Override
	public SpoutBlock getFace(BlockFace face) {
		return getFace(face, 1);
	}
	
	@Override
	public SpoutBlock getFace(BlockFace face, int distance) {
		return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
	}
	
	@Override
	public SpoutBlock getRelative(int modX, int modY, int modZ) {
		Block result = super.getRelative(modX, modY, modZ);
		if (result instanceof SpoutBlock) {
			return (SpoutBlock)result;
		}
		//XXX should not happen!
		net.minecraft.server.Chunk chunk = ((CraftChunk)result.getChunk()).getHandle();
		chunk.bukkitChunk = new SpoutCraftChunk(chunk);
		return (SpoutBlock)chunk.bukkitChunk.getBlock(result.getX() & 0xF, result.getY() & 0x7F, result.getZ() & 0xF);
	}
	
	@Override
	public SpoutBlock getRelative(BlockFace face) {
		return getRelative(face.getModX(), face.getModY(), face.getModZ());
	}
	
	private int getIndex() {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
	}
	
}
