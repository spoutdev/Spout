package org.getspout.unchecked.api.protocol.notch.msg;

import org.getspout.unchecked.api.inventory.ItemStack;
import org.getspout.unchecked.api.protocol.Message;

import java.util.Arrays;

public final class SetWindowSlotsMessage extends Message {
	private final int id;
	private final ItemStack[] items;

	public SetWindowSlotsMessage(int id, ItemStack[] items) {
		this.id = id;
		this.items = items;
	}

	public int getId() {
		return id;
	}

	public ItemStack[] getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "SetWindowSlotsMessage{id=" + id + ",slots=" + Arrays.toString(items) + "}";
	}
}
