package org.getspout.unchecked.server.msg;

public final class RespawnMessage extends Message {
	private final byte dimension, difficulty, mode;
	private final int worldHeight;
	private final long seed;

	public RespawnMessage(byte dimension, byte difficulty, byte mode, int worldHeight, long seed) {
		this.dimension = dimension;
		this.difficulty = difficulty;
		this.mode = mode;
		this.worldHeight = worldHeight;
		this.seed = seed;
	}

	public byte getDimension() {
		return dimension;
	}

	public byte getDifficulty() {
		return difficulty;
	}

	public byte getGameMode() {
		return mode;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	public long getSeed() {
		return seed;
	}

	@Override
	public String toString() {
		return "RespawnMessage{dimension=" + dimension + ",difficulty=" + difficulty + ",gameMode=" + mode + ",worldHeight=" + worldHeight + ",seed=" + seed + "}";
	}
}
