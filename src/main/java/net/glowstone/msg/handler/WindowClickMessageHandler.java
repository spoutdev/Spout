package net.glowstone.msg.handler;

import java.util.HashMap;
import java.util.logging.Level;

import net.glowstone.inventory.GlowItemStack;
import net.glowstone.msg.CloseWindowMessage;
import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.CraftingInventory;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.msg.TransactionMessage;
import net.glowstone.msg.WindowClickMessage;
import net.glowstone.net.Session;

public final class WindowClickMessageHandler extends MessageHandler<WindowClickMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, WindowClickMessage message) {
        if (player == null)
            return;
        
        GlowInventory inv = player.getInventory();
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
        
        GlowItemStack currentItem = inv.getItem(slot);

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
                }
                else
                {
                    // If someone shift clicked in the crafting inventory, move all of the item down to regular inventory
                    // Main inventory takes precedence, then quickbar
                    GlowItemStack result = null;
                    GlowItemStack[] slots = player.getInventory().getContents();

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
                                player.getInventory().setItem(j, new GlowItemStack(mat, num, damage));
                                toAdd -= num;
                            }
                        }
                    }

                    if (toAdd > 0) {
                        // Still couldn't stash them all.
                        // Try stashing in the quickbar
                        
                        result = new GlowItemStack(mat, toAdd, damage);
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
                                    player.getInventory().setItem(j, new GlowItemStack(mat, num, damage));
                                    toAdd -= num;
                                }
                            }
                        }

                        if (toAdd > 0) {
                            // Still couldn't stash them all.
                            result = new GlowItemStack(mat, toAdd, damage);
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
                    GlowItemStack result = null;
                    GlowItemStack[] slots = player.getInventory().getContents();

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
                                player.getInventory().setItem(j, new GlowItemStack(mat, num, damage));
                                toAdd -= num;
                            }
                        }
                    }

                    if (toAdd > 0) {
                        // Still couldn't stash them all.
                        result = new GlowItemStack(mat, toAdd, damage);                                        
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
                    GlowItemStack result = null;
                    GlowItemStack[] slots = player.getInventory().getContents();

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
                                player.getInventory().setItem(j, new GlowItemStack(mat, num, damage));
                                toAdd -= num;
                            }
                        }
                    }

                    if (toAdd > 0) {
                        // Still couldn't stash them all.
                        result = new GlowItemStack(mat, toAdd, damage);
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
        
        if(message.isRightClick())
        {
            // TODO: Handle right-clicks
        }
        
        if (inv == player.getInventory().getCraftingInventory() && slot == CraftingInventory.RESULT_SLOT && currentItem != null)
        {
            player.getInventory().getCraftingInventory().craft(player, false);
                 
            response(session, message, true);
            return;
        }
        
        response(session, message, true);
        
        inv.setItem(slot, player.getItemOnCursor());
        player.setItemOnCursor(currentItem);
    }
    
    private void response(Session session, WindowClickMessage message, boolean success) {
        session.send(new TransactionMessage(message.getId(), message.getTransaction(), success));
    }
    
}
