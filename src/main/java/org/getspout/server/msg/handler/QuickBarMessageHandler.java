package org.getspout.server.msg.handler;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.block.BlockProperties;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.inventory.SpoutInventory;
import org.getspout.server.inventory.SpoutItemStack;
import org.getspout.server.item.ItemProperties;
import org.getspout.server.msg.QuickBarMessage;
import org.getspout.server.net.Session;

public class QuickBarMessageHandler extends MessageHandler<QuickBarMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, QuickBarMessage message) {
		if (player.getGameMode() != GameMode.CREATIVE) {
			player.kickPlayer("Now now, don't try that here. Won't work.");
			return;
		}
		SpoutInventory inv = player.getInventory();
		int slot = inv.getItemSlot(message.getSlot());

		if (slot < 0 || slot > 8
				|| !checkValidId(message.getSlot())) {
			player.onSlotSet(inv, slot, inv.getItem(slot));
		}
		SpoutItemStack newItem = new SpoutItemStack(message.getId(), message.getAmount(), message.getDamage(), message.getNbtData());
		SpoutItemStack currentItem = inv.getItem(slot);

		inv.setItem(slot, newItem);
		if (currentItem != null) {
			player.setItemOnCursor(currentItem);
		} else {
			player.setItemOnCursor(null);
		}
	}

	public boolean checkValidId(int id) {
		return BlockProperties.get(id) == null && ItemProperties.get(id) == null;
	}
}
