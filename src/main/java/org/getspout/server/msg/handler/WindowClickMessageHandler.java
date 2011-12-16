package org.getspout.server.msg.handler;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.inventory.CraftingInventory;
import org.getspout.server.inventory.SpoutInventory;
import org.getspout.server.inventory.SpoutItemStack;
import org.getspout.server.inventory.SpoutPlayerInventory;
import org.getspout.server.msg.CloseWindowMessage;
import org.getspout.server.msg.TransactionMessage;
import org.getspout.server.msg.WindowClickMessage;
import org.getspout.server.net.Session;

public final class WindowClickMessageHandler extends MessageHandler<WindowClickMessage> {
	@Override
	public void handle(Session session, SpoutPlayer player, WindowClickMessage message) {
		if (player == null)
			return;

		SpoutInventory inv = player.getInventory();
		int slot = inv.getItemSlot(message.getSlot());

		// Modify slot if needed
		if (slot < 0) {
			inv = player.getInventory().getCraftingInventory();
			slot = ((CraftingInventory)inv).getItemSlot(message.getSlot());
		}
		if (slot == -1) {
			player.setItemOnCursor(null);
			response(session, message, true);
			return;
		}
		if (slot < 0) {
			response(session, message, false);
			player.getServer().getLogger().log(Level.WARNING, "Got invalid inventory slot {0} from {1}", new Object[]{message.getSlot(), player.getName()});
			return;
		}

		SpoutItemStack currentItem = inv.getItem(slot);

		if (player.getGameMode() == GameMode.CREATIVE && message.getId() == inv.getId()) {
			response(session, message, false);
			player.onSlotSet(inv, slot, currentItem);
			player.getServer().getLogger().log(Level.WARNING, "{0} tried to do an invalid inventory action in Creative mode!", new Object[]{player.getName()});
			return;
		}

		if (currentItem == null) {
			if (message.getItem() != -1) {
				player.onSlotSet(inv, slot, currentItem);
				response(session, message, false);
				return;
			}
		} else if (message.getItem() != currentItem.getTypeId() ||
				message.getCount() != currentItem.getAmount() ||
				message.getDamage() != currentItem.getDurability()) {
			player.onSlotSet(inv, slot, currentItem);
			response(session, message, false);
			return;
		}

		if (message.isShift()) {
			if (false/*inv == player.getInventory().getOpenWindow()*/) {
				// Chest takes precedence over all all moves
				// Quickbar is filled first

				// TODO: Waiting on getOpenWindow implementation
			} else if (inv == player.getInventory().getCraftingInventory()) {
				if (slot == CraftingInventory.RESULT_SLOT && currentItem != null) {
					player.getInventory().getCraftingInventory().craft(player, true);
					response(session, message, true);
					return;
				}
				else
				{
					// If someone shift clicked in the crafting inventory, move all of the item down to regular inventory
					// Main inventory takes precedence, then quickbar
					SpoutItemStack result = null;
					SpoutItemStack[] slots = player.getInventory().getContents();

					int maxStackSize = currentItem.getType() == null ? 64 : currentItem.getType().getMaxStackSize();
					int mat = currentItem.getTypeId();
					int toAdd = currentItem.getAmount();
					short damage = currentItem.getDurability();

					for (int j = 9; toAdd > 0 && j < 36; ++j) {
						// Look for existing stacks to add to
						if (slots[j] != null && slots[j].getTypeId() == mat && slots[j].getDurability() == damage) {
							int space = maxStackSize - slots[j].getAmount();
							if (space < 0) continue;
							if (space > toAdd) space = toAdd;

							slots[j].setAmount(slots[j].getAmount() + space);
							player.getInventory().setItem(j, slots[j]);

							toAdd -= space;
						}
					}

					if (toAdd > 0) {
						// Look for empty slots to add to
						for (int j = 9; toAdd > 0 && j < 36; ++j) {
							if (slots[j] == null) {
								int num = toAdd > maxStackSize ? maxStackSize : toAdd;
								player.getInventory().setItem(j, new SpoutItemStack(mat, num, damage));
								toAdd -= num;
							}
						}
					}

					if (toAdd > 0) {
						// Still couldn't stash them all.
						// Try stashing in the quickbar

						result = new SpoutItemStack(mat, toAdd, damage);
						for (int j = 0; toAdd > 0 && j < 9; ++j) {
							// Look for existing stacks to add to
							if (slots[j] != null && slots[j].getTypeId() == mat && slots[j].getDurability() == damage) {
								int space = maxStackSize - slots[j].getAmount();
								if (space < 0) continue;
								if (space > toAdd) space = toAdd;

								slots[j].setAmount(slots[j].getAmount() + space);
								player.getInventory().setItem(j, slots[j]);

								toAdd -= space;
							}
						}

						if (toAdd > 0) {
							// Look for empty slots to add to
							for (int j = 0; toAdd > 0 && j < 9; ++j) {
								if (slots[j] == null) {
									int num = toAdd > maxStackSize ? maxStackSize : toAdd;
									player.getInventory().setItem(j, new SpoutItemStack(mat, num, damage));
									toAdd -= num;
								}
							}
						}

						if (toAdd > 0) {
							// Still couldn't stash them all.
							result = new SpoutItemStack(mat, toAdd, damage);
						}
					}

					if(result == null)
					{
						player.getInventory().getCraftingInventory().setItem(slot, null);
						response(session, message, true);
						return;
					}
					else if(!result.equals(currentItem))
					{

						player.getInventory().getCraftingInventory().setItem(slot, result);
						response(session, message, true);
						return;
					}
				}
			} else {
				// If the quickbar if shift clicked, move it to main inventory, and vice versa
				if (slot < 9) {
					// Quickbar
					SpoutItemStack result = null;
					SpoutItemStack[] slots = player.getInventory().getContents();

					int maxStackSize = currentItem.getType() == null ? 64 : currentItem.getType().getMaxStackSize();
					int mat = currentItem.getTypeId();
					int toAdd = currentItem.getAmount();
					short damage = currentItem.getDurability();

					for (int j = 9; toAdd > 0 && j < 36; ++j) {
						// Look for existing stacks to add to
						if (slots[j] != null && slots[j].getTypeId() == mat && slots[j].getDurability() == damage) {
							int space = maxStackSize - slots[j].getAmount();
							if (space < 0) continue;
							if (space > toAdd) space = toAdd;

							slots[j].setAmount(slots[j].getAmount() + space);
							player.getInventory().setItem(j, slots[j]);

							toAdd -= space;
						}
					}

					if (toAdd > 0) {
						// Look for empty slots to add to
						for (int j = 9; toAdd > 0 && j < 36; ++j) {
							if (slots[j] == null) {
								int num = toAdd > maxStackSize ? maxStackSize : toAdd;
								player.getInventory().setItem(j, new SpoutItemStack(mat, num, damage));
								toAdd -= num;
							}
						}
					}

					if (toAdd > 0) {
						// Still couldn't stash them all.
						result = new SpoutItemStack(mat, toAdd, damage);
					}

					if(result == null)
					{
						player.getInventory().setItem(slot, null);
						response(session, message, true);
						return;
					}
					else if(!result.equals(currentItem))
					{

						player.getInventory().setItem(slot, result);
						response(session, message, true);
						return;
					}
				} else {
					// Main inventory
					SpoutItemStack result = null;
					SpoutItemStack[] slots = player.getInventory().getContents();

					int maxStackSize = currentItem.getType() == null ? 64 : currentItem.getType().getMaxStackSize();
					int mat = currentItem.getTypeId();
					int toAdd = currentItem.getAmount();
					short damage = currentItem.getDurability();

					for (int j = 0; toAdd > 0 && j < 9; ++j) {
						// Look for existing stacks to add to
						if (slots[j] != null && slots[j].getTypeId() == mat && slots[j].getDurability() == damage) {
							int space = maxStackSize - slots[j].getAmount();
							if (space < 0) continue;
							if (space > toAdd) space = toAdd;

							slots[j].setAmount(slots[j].getAmount() + space);
							player.getInventory().setItem(j, slots[j]);

							toAdd -= space;
						}
					}

					if (toAdd > 0) {
						// Look for empty slots to add to
						for (int j = 0; toAdd > 0 && j < 9; ++j) {
							if (slots[j] == null) {
								int num = toAdd > maxStackSize ? maxStackSize : toAdd;
								player.getInventory().setItem(j, new SpoutItemStack(mat, num, damage));
								toAdd -= num;
							}
						}
					}

					if (toAdd > 0) {
						// Still couldn't stash them all.
						result = new SpoutItemStack(mat, toAdd, damage);
					}

					if(result == null)
					{
						player.getInventory().setItem(slot, null);
						response(session, message, true);
						return;
					}
					else if(!result.equals(currentItem))
					{

						player.getInventory().setItem(slot, result);
						response(session, message, true);
						return;
					}
				}
			}
		}

		if (inv == player.getInventory().getCraftingInventory() && slot == CraftingInventory.RESULT_SLOT)
		{
			player.getInventory().getCraftingInventory().craft(player, false);

			response(session, message, true);
			return;
		}

		ItemStack oncursor = player.getItemOnCursor();
		SpoutItemStack newoncursor = null;
		SpoutItemStack newinvitem = null;
		if (message.isRightClick()) {
			if (oncursor == null) {
				if (currentItem != null) {
					// Pick up half of the items
					int newinvitemcount = (int) Math.floor(currentItem.getAmount() / 2);
					int newitemoncursorcount = currentItem.getAmount() - newinvitemcount;
					if (newitemoncursorcount > 0) newoncursor = new SpoutItemStack(currentItem.getTypeId(), newitemoncursorcount, currentItem.getDurability(), currentItem.getNbtData());
					if (newinvitemcount > 0)	  newinvitem = new SpoutItemStack(currentItem.getTypeId(), newinvitemcount, currentItem.getDurability(), currentItem.getNbtData());

					response(session, message, true);
					inv.setItem(slot, newinvitem);
					player.setItemOnCursor(newoncursor);
				}
			} else {
				// Right clicking to place a single item
				int currentitemcount = 0;

				if (currentItem != null) {
					currentitemcount = currentItem.getAmount();
					if (oncursor.getTypeId() != currentItem.getTypeId()) {
						// Item types differ, swap them
						response(session, message, true);

						inv.setItem(slot, player.getItemOnCursor());
						player.setItemOnCursor(currentItem);
					} else {
						// Try to add one to the item stack
						if (currentItem.getType().getMaxStackSize() <= currentitemcount) {
							// The stack is full
							response(session, message, false);
							return;
						}
					}
				}

				if (oncursor.getType().getMaxStackSize() > 1) {
					int newitemoncursorcount = player.getItemOnCursor().getAmount() - 1;
					int newinvitemcount = currentitemcount + 1;

					if (newinvitemcount > oncursor.getType().getMaxStackSize()) {
						// No space, fail.
						response(session, message, false);
						return;
					}

					if (newitemoncursorcount > 0)   newoncursor = new SpoutItemStack(oncursor.getTypeId(), newitemoncursorcount, oncursor.getDurability());
					if (newinvitemcount > 0)		newinvitem = new SpoutItemStack(oncursor.getTypeId(), newinvitemcount, oncursor.getDurability());

					response(session, message, true);

					inv.setItem(slot, newinvitem);
					player.setItemOnCursor(newoncursor);
				} else {
					// Item not stackable
					response(session, message, false);
					return;
				}
			}
		} else {
			// Handle left clicks
			if (oncursor == null) {
				newinvitem = null;
				newoncursor = currentItem;
			} else {
				if (currentItem == null) {
					// Putting stack on an empty slot
					newinvitem = (SpoutItemStack) oncursor;
					newoncursor = currentItem;
				} else {
					// Putting a stack on another stack
					// Check if the stack is of the same type,
					//  Put as much as possible on the stack, rest in hand
					if (oncursor.getTypeId() == currentItem.getTypeId()) {
						int total = currentItem.getAmount() + oncursor.getAmount();
						int newinvitemcount = Math.min(currentItem.getType().getMaxStackSize(), total);
						int newitemoncursoramount = total - newinvitemcount;

						if (currentItem.getType().getMaxStackSize() >= newinvitemcount) {
							newinvitem = currentItem;
							newinvitem.setAmount(newinvitemcount);
							if (newitemoncursoramount == 0) {
								newoncursor = null;
							} else {
								newoncursor = (SpoutItemStack) oncursor;
								newoncursor.setAmount(newitemoncursoramount);
							}
						} else {
							response(session, message, false);
							return;
						}
					} else {
						// Items are not the same type, swap them
						newinvitem = (SpoutItemStack) oncursor;
						newoncursor = currentItem;
					}
				}
			}

			response(session, message, true);

			inv.setItem(slot, newinvitem);
			player.setItemOnCursor(newoncursor);
			return;
		}
	}

	private void response(Session session, WindowClickMessage message, boolean success) {
		session.send(new TransactionMessage(message.getId(), message.getTransaction(), success));
	}
}
