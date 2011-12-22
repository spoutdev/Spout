package org.getspout.api.protocol.notch.msg;

import org.getspout.api.protocol.Message;

public final class LoadChunkMessage extends Message {
	private final int x, z;
	private final boolean loaded;

	public LoadChunkMessage(int x, int z, boolean loaded) {
		this.x = x;
		this.z = z;
		this.loaded = loaded;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public String toString() {
		return "LoadChunkMessage{x=" + x + ",z=" + z + ",loaded=" + loaded + "}";
	}
}
