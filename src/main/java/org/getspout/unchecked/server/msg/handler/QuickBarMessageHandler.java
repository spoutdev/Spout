package org.getspout.unchecked.server.msg.handler;

import org.bukkit.GameMode;

import org.getspout.unchecked.server.block.BlockProperties;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.inventory.SpoutInventory;
import org.getspout.unchecked.server.inventory.SpoutItemStack;
import org.getspout.unchecked.server.item.ItemProperties;
import org.getspout.unchecked.server.msg.QuickBarMessage;
import org.getspout.unchecked.server.net.Session;

public class QuickBarMessageHandler extends MessageHandler<QuickBarMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, QuickBarMessage message) {
		if (player.getGameMode() != GameMode.CREATIVE) {
			player.kickPlayer("Now now, don't try that here. Won't work.");
			return;
		}
		SpoutInventory inv = player.getInventory();
		int slot = inv.getItemSlot(message.getSlot());

		if (slot < 0 || slot > 8 || !checkValidId(message.getSlot())) {
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
