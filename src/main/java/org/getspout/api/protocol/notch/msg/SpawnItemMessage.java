package org.getspout.api.protocol.notch.msg;

import org.getspout.api.protocol.Message;

public final class SpawnItemMessage extends Message {
	private final int id, x, y, z, rotation, pitch, roll;
	private final int itemId, count;
	private final short damage;

	public SpawnItemMessage(int id, int itemId, int count, short damage, int x, int y, int z, int rotation, int pitch, int roll) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.pitch = pitch;
		this.roll = roll;
		this.itemId = itemId;
		this.count = count;
		this.damage = damage;
	}

	public int getId() {
		return id;
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

	public int getRotation() {
		return rotation;
	}

	public int getPitch() {
		return pitch;
	}

	public int getRoll() {
		return roll;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public int getCount() {
		return count;
	}
	
	public short getDamage() {
		return damage;
	}

	@Override
	public String toString() {
		return "SpawnItemMessage{id=" + id + ",item=[" + itemId + "," + count + "," + damage + "],x=" + x + ",y=" + y + ",z=" + z + ",rotation=" + rotation + ",pitch=" + pitch + ",roll=" + roll + "}";
	}
}
