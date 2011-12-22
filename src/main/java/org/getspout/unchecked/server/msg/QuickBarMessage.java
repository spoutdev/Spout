package org.getspout.unchecked.server.msg;

import java.util.Map;

import org.getspout.unchecked.server.util.nbt.Tag;

public class QuickBarMessage extends Message {
	private final short slot, id, amount, damage;
	private final Map<String, Tag> nbtData;

	public QuickBarMessage(short slot, short id, short amount, short damage, Map<String, Tag> nbtData) {
		this.slot = slot;
		this.id = id;
		this.amount = amount;
		this.damage = damage;
		this.nbtData = nbtData;
	}

	public short getSlot() {
		return slot;
	}

	public short getId() {
		return id;
	}

	public short getAmount() {
		return amount;
	}

	public short getDamage() {
		return damage;
	}

	public Map<String, Tag> getNbtData() {
		return nbtData;
	}

	@Override
	public String toString() {
		return "QuickBarMessage{slot=" + slot + ",id=" + id + ",amount=" + amount + ",damage=" + damage + ",nbtData=" + nbtData + "}";
	}
}
