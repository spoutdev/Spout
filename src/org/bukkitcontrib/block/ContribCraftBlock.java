package org.bukkitcontrib.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;

public class ContribCraftBlock extends CraftBlock implements ContribBlock {
	protected final int x, y, z;
	protected final ContribCraftChunk chunk;
	public ContribCraftBlock(ContribCraftChunk chunk, int x, int y, int z) {
		super(chunk, x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
		this.chunk = chunk;
	}

	@Override
	public ContribChunk getChunk() {
		return chunk;
	}
	
	@Override
	public void setTypeAsync(Material type) {
		setTypeIdAsync(type.getId());
	}
	
	@Override
	public void setTypeIdAsync(int type) {
		chunk.queuedId.put(getIndex(), type);
		final byte data = getData();
		for (Player player : chunk.getWorld().getPlayers()) {
			player.sendBlockChange(getLocation(), type, data);
		}
	}
	
	@Override
	public void setDataAsync(byte data) {
		chunk.queuedData.put(getIndex(), data);
		final int type = getTypeId();
		for (Player player : chunk.getWorld().getPlayers()) {
			player.sendBlockChange(getLocation(), type, data);
		}
	}
	
	@Override
	public void setTypeIdAndDataAsync(int type, byte data) {
		chunk.queuedId.put(getIndex(), type);
		chunk.queuedData.put(getIndex(), data);
		for (Player player : chunk.getWorld().getPlayers()) {
			player.sendBlockChange(getLocation(), type, data);
		}
	}
	
	@Override
	public BlockState getState() {
		Material material = getType();

		switch (material) {
			case CHEST:
				return new ContribCraftChest(this);
			default:
				return super.getState();
		}
	}
	
	@Override
	public ContribBlock getFace(BlockFace face) {
		return getFace(face, 1);
	}
	
	@Override
	public ContribBlock getFace(BlockFace face, int distance) {
		return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
	}
	
	@Override
	public ContribBlock getRelative(int modX, int modY, int modZ) {
		Block result = super.getRelative(modX, modY, modZ);
		if (result instanceof ContribBlock) {
			return (ContribBlock)result;
		}
		//XXX should not happen!
		ContribCraftChunk.replaceBukkitChunk(result.getChunk());
		//Try again!
		result = super.getRelative(modX, modY, modZ);
		if (result instanceof ContribBlock) {
			return (ContribBlock)result;
		}
		System.out.println("[BukkitContrib] Unexpected Behavior in ContribCraftBlock.getRelative(...)!");
		return new ContribCraftBlock((ContribCraftChunk)result.getWorld().getChunkAt(result.getChunk().getX(), result.getChunk().getZ()), result.getX(), result.getY(), result.getZ());

	}
	
	@Override
	public ContribBlock getRelative(BlockFace face) {
		return getRelative(face.getModX(), face.getModY(), face.getModZ());
	}
	
	private int getIndex() {
		return (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
	}
	
}
