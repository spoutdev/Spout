package org.spout.engine.protocol.builtin.message;

import org.spout.api.Spout;
import org.spout.api.datatable.delta.DeltaMap;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.engine.world.SpoutChunk;

public class ChunkDatatableMessage extends DatatableMessage {
	final String world;
	final int x, y, z;

	public ChunkDatatableMessage(String world, int x, int y, int z, byte[] compressedData, DeltaMap.DeltaType type) {
		super(compressedData, type);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public ChunkDatatableMessage(SpoutChunk c) {
		this(c.getWorld().getName(), c.getX(), c.getY(), c.getZ(), c.getDataMap().getDeltaMap().serialize(), c.getDataMap().getDeltaMap().getType());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public String getWorld() {
		return world;
	}

	public Chunk getChunk() {
		return Spout.getEngine().getWorld(world, true).getChunk(x, y, z);
	}
}
