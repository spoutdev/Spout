package org.getspout.unchecked.server.inventory;

import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.getspout.api.util.nbt.Tag;

public class SpoutItemStack extends ItemStack {
	private static final long serialVersionUID = 7920525754029137821L;
	private Map<String, Tag> nbtData;

	public SpoutItemStack(final int type) {
		this(type, 1, (short) 0, null);
	}

	public SpoutItemStack(final int type, final int amount) {
		this(type, amount, (short) 0, null);
	}

	public SpoutItemStack(final int type, final int amount, final short damage) {
		this(type, amount, damage, null);
	}

	public SpoutItemStack(final int type, final int amount, final short damage, Map<String, Tag> nbtData) {
		super(type, amount, damage);
		this.nbtData = nbtData;
	}

	public SpoutItemStack(ItemStack stack) {
		super(stack.getTypeId(), stack.getAmount(), stack.getDurability());
	}

	public void setNbtData(Map<String, Tag> nbtData) {
		this.nbtData = nbtData;
	}

	public Map<String, Tag> getNbtData() {
		return nbtData;
	}
}
