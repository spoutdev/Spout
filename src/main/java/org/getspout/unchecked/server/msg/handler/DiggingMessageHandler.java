package org.getspout.unchecked.server.msg.handler;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.getspout.unchecked.server.EventFactory;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.block.BlockID;
import org.getspout.unchecked.server.block.BlockProperties;
import org.getspout.unchecked.server.block.SpoutBlock;
import org.getspout.unchecked.server.entity.SpoutPlayer;
import org.getspout.unchecked.server.inventory.SpoutItemStack;
import org.getspout.unchecked.server.item.ItemProperties;
import org.getspout.unchecked.server.msg.DiggingMessage;
import org.getspout.unchecked.server.net.Session;

/**
 * A {@link MessageHandler} which processes digging messages.
 */
public final class DiggingMessageHandler extends MessageHandler<DiggingMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, DiggingMessage message) {
		if (player == null) {
			return;
		}

		boolean blockBroken = false;

		SpoutWorld world = player.getWorld();

		int x = message.getX();
		int y = message.getY();
		int z = message.getZ();

		SpoutBlock block = world.getBlockAt(x, y, z);

		// Need to have some sort of verification to deal with malicious clients.
		if (message.getState() == DiggingMessage.STATE_START_DIGGING) {
			Action act = Action.LEFT_CLICK_BLOCK;
			if (player.getLocation().distanceSquared(block.getLocation()) > 36 || block.getTypeId() == BlockID.AIR) {
				act = Action.LEFT_CLICK_AIR;
			}
			BlockFace face = MessageHandlerUtils.messageToBlockFace(message.getFace());
			PlayerInteractEvent interactEvent = EventFactory.onPlayerInteract(player, act, block, face);
			if (interactEvent.isCancelled()) {
				return;
			}
			if (interactEvent.useItemInHand() != Event.Result.DENY) {
				SpoutItemStack heldItem = player.getItemInHand();
				if (heldItem != null && heldItem.getTypeId() > 255) {
					ItemProperties props = ItemProperties.get(heldItem.getTypeId());
					if (props != null) {
						if (!props.getPhysics().interact(player, block, heldItem, Action.LEFT_CLICK_BLOCK, face)) {
							return;
						}
					}
				}
			}
			if (interactEvent.useInteractedBlock() != Event.Result.DENY) {
				if (!BlockProperties.get(block.getTypeId()).getPhysics().interact(player, block, false, face)) {
					return;
				}
			}
			BlockDamageEvent event = EventFactory.onBlockDamage(player, block);
			if (!event.isCancelled()) {
				blockBroken = event.getInstaBreak() || player.getGameMode() == GameMode.CREATIVE;
			}
		} else if (message.getState() == DiggingMessage.STATE_DONE_DIGGING) {
			BlockBreakEvent event = EventFactory.onBlockBreak(block, player);
			if (!event.isCancelled()) {
				blockBroken = true;
			}
		}

		if (blockBroken) {
			if (!block.isEmpty() && !block.isLiquid()) {
				if (!player.getInventory().contains(block.getTypeId()) || player.getGameMode() != GameMode.CREATIVE) {
					player.getInventory().addItem(BlockProperties.get(block.getTypeId()).getDrops(block.getData()));
				}
			}
			world.playEffectExceptTo(block.getLocation(), Effect.STEP_SOUND, block.getTypeId(), 64, player);
			block.setTypeId(BlockID.AIR);
		}
	}
}
