package org.getspout.unchecked.server.msg.handler;

import org.getspout.api.player.Player;
import org.getspout.api.protocol.MessageHandler;
import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.notch.msg.QuickBarMessage;
import org.getspout.unchecked.server.block.BlockProperties;
import org.getspout.unchecked.server.item.ItemProperties;

public class QuickBarMessageHandler extends MessageHandler<QuickBarMessage> {
	@Override
	public void handle(Session session, Player player, QuickBarMessage message) {
		/*if (player.getGameMode() != GameMode.CREATIVE) {
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
		*/
	}

	public boolean checkValidId(int id) {
		return BlockProperties.get(id) == null && ItemProperties.get(id) == null;
	}
}
