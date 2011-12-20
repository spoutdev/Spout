package org.getspout.server.io.entity;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.objects.SpoutItem;
import org.getspout.server.util.nbt.ByteTag;
import org.getspout.server.util.nbt.CompoundTag;
import org.getspout.server.util.nbt.ShortTag;
import org.getspout.server.util.nbt.Tag;

public class ItemEntityStore extends EntityStore<SpoutItem> {
	public ItemEntityStore() {
		super(SpoutItem.class, "Item");
	}

	@Override
	public SpoutItem load(SpoutServer server, SpoutWorld world, CompoundTag compound) {
		ItemStack stack = null;
		if (compound.getValue().containsKey("Item")) {
			stack = readItemStack((CompoundTag) compound.getValue().get("Item"));
		}
		SpoutItem item = new SpoutItem(server, world, stack);
		if (compound.getValue().containsKey("Health")) {
			// item.setHealth(((IntTag)compound.getValue().get("Health")).getValue());
		}
		if (compound.getValue().containsKey("Age")) {
			// item.setAge(((IntTag)compound.getValue().get("Age")).getValue());
		}
		return item;
	}

	@Override
	public void load(SpoutItem entity, CompoundTag compound) {
		if (compound.getValue().containsKey("Item")) {
			ItemStack stack = readItemStack((CompoundTag) compound.getValue().get("Item"));
			entity.setItemStack(stack);
		}
		if (compound.getValue().containsKey("Health")) {
			// item.setHealth(((IntTag)compound.getValue().get("Health")).getValue());
		}
		if (compound.getValue().containsKey("Age")) {
			// item.setAge(((IntTag)compound.getValue().get("Age")).getValue());
		}
	}

	@Override
	public Map<String, Tag> save(SpoutItem entity) {
		Map<String, Tag> ret = super.save(entity);
		Map<String, Tag> itemTag = new HashMap<String, Tag>();
		itemTag.put("id", new ShortTag("id", (short) entity.getItemStack().getTypeId()));
		itemTag.put("Damage", new ShortTag("Damage", entity.getItemStack().getDurability()));
		itemTag.put("Count", new ByteTag("Count", (byte) entity.getItemStack().getAmount()));
		ret.put("Item", new CompoundTag("Item", itemTag));
		// ret.put("Health", new IntTag("Health", entity.getHealth()));
		// ret.put("Age", new IntTag("Age", entity.getAge()));
		return ret;
	}

	public ItemStack readItemStack(CompoundTag compound) {
		ItemStack stack = null;
		Tag idTag = compound.getValue().get("id");
		Tag damageTag = compound.getValue().get("Damage");
		Tag countTag = compound.getValue().get("Count");
		short id = idTag == null ? 0 : ((ShortTag) idTag).getValue();
		short damage = damageTag == null ? 0 : ((ShortTag) damageTag).getValue();
		byte count = countTag == null ? 0 : ((ByteTag) countTag).getValue();
		if (id != 0 && count != 0) {
			stack = new ItemStack(id, count, damage);
		}
		return stack;
	}
}
