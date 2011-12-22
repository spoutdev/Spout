package org.getspout.unchecked.api.protocol.notch.msg;

import org.getspout.unchecked.api.inventory.ItemStack;
import org.getspout.unchecked.api.protocol.Message;

public final class SpawnItemMessage extends Message {
	private final int id, x, y, z, rotation, pitch, roll;
	private final ItemStack item;

	public SpawnItemMessage(int id, ItemStack item, int x, int y, int z, int rotation, int pitch, int roll) {
		this.id = id;
		this.item = item;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotation = rotation;
		this.pitch = pitch;
		this.roll = roll;
	}

	public int getId() {
		return id;
	}

	public ItemStack getItem() {
		return item;
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

	@Override
	public String toString() {
		return "SpawnItemMessage{id=" + id + ",item=" + item + ",x=" + x + ",y=" + y + ",z=" + z + ",rotation=" + rotation + ",pitch=" + pitch + ",roll=" + roll + "}";
	}
}
