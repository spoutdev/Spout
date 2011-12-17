package org.getspout.server.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import org.getspout.server.SpoutServer;
import org.getspout.server.entity.SpoutPlayer;

/**
 * Represents the portion of a player's inventory which handles crafting.
 */
public class CraftingInventory extends SpoutInventory {	
	public CraftingInventory() {
		super((byte) 0, 5);
	}
    
    public CraftingInventory(byte id, int slots)
    {
        super(id, slots);
    }

	/**
	 * Return the name of the inventory
	 *
	 * @return The inventory name
	 */
	@Override
	public String getName() {
		return "Crafting";
	}
    
    public int getResultSlot() {
        return 4;
    }

	/**
	 * Stores the ItemStack at the given index.
	 * Notifies all attached InventoryViewers of the change.
	 *
	 * @param index The index where to put the ItemStack
	 * @param item The ItemStack to set
	 */
	@Override
	public void setItem(int index, SpoutItemStack item) {
		super.setItem(index, item);

		if (index != getResultSlot()) {
			ItemStack[] items = new ItemStack[4];
			for (int i = 0; i < 4; ++i) {
				items[i] = getItem(i);
			}

			Recipe recipe = ((SpoutServer) Bukkit.getServer()).getCraftingManager().getCraftingRecipe(items);
			if (recipe == null) {
				setItem(getResultSlot(), null);
			} else {
				setItem(getResultSlot(), recipe.getResult());
			}
		}
	}

	// Helper for crafting insertion
	private final static int craftinsertconversion[] = {
		8, 7, 6, 5, 4, 3, 2, 1, 0, // quickbar
		35, 34, 33, 32, 31, 30, 29, 28, 27,
		26, 25, 24, 23, 22, 21, 20, 19, 18,
		17, 16, 15, 14, 13, 12, 11, 10, 9,
	};
	/**
	 * Remove a layer of items from the inventory according to the current recipe.
	 * Or remove as many items as possible if it was shift clicked
	 */
	public void craft(SpoutPlayer player, boolean isShift) {
		ItemStack[] items = new ItemStack[4];
		for (int i = 0; i < 4; ++i) {
			items[i] = getItem(i);
		}

		Recipe recipe = ((SpoutServer) Bukkit.getServer()).getCraftingManager().getCraftingRecipe(items);
		if (recipe != null) {
			ItemStack currentItem = recipe.getResult();

			if (isShift) {
				// Rules of storage for shift clicking are different
				int maxStackSize = currentItem.getType() == null ? 64 : currentItem.getType().getMaxStackSize();
				int mat = currentItem.getTypeId();

				short damage = currentItem.getDurability();
				SpoutItemStack[] slots = player.getInventory().getContents();

				int iteration = 0;
				do {
					Recipe checkrecipe = ((SpoutServer) Bukkit.getServer()).getCraftingManager().getCraftingRecipe(items);
					if (checkrecipe != null) {
						int toAdd = currentItem.getAmount();
						if (checkrecipe.equals(recipe)) {
							boolean couldfit = true;
							// Put the items in the inventory

							for (int j = 0; toAdd > 0 && j < 36; ++j) {
								// Look for existing stacks to add to
								if (slots[craftinsertconversion[j]] != null && slots[craftinsertconversion[j]].getTypeId() == mat && slots[craftinsertconversion[j]].getDurability() == damage) {
									int space = maxStackSize - slots[craftinsertconversion[j]].getAmount();
									if (space < 0) {
										continue;
									}
									if (space > toAdd) {
										space = toAdd;
									}

									slots[craftinsertconversion[j]].setAmount(slots[craftinsertconversion[j]].getAmount() + space);
									player.getInventory().setItem(craftinsertconversion[j], slots[craftinsertconversion[j]]);

									toAdd -= space;
								}
							}

							if (toAdd > 0) {
								// Look for empty slots to add to
								for (int j = 0; toAdd > 0 && j < 36; ++j) {
									if (slots[craftinsertconversion[j]] == null) {
										int num = toAdd > maxStackSize ? maxStackSize : toAdd;
										SpoutItemStack newitem = new SpoutItemStack(mat, num, damage);
										slots[craftinsertconversion[j]] = newitem;
										player.getInventory().setItem(craftinsertconversion[j], newitem);
										toAdd -= num;
									}
								}
							}

							if (toAdd > 0) {
								couldfit = false;
							}

							if (couldfit) {
								((SpoutServer) Bukkit.getServer()).getCraftingManager().removeItems(items, recipe);
								iteration++;
							} else {
								isShift = false;
							}
						}
					} else {
						isShift = false;
					}
				} while (isShift && iteration < 64);
			} else {
				if (recipe != null) {
					ItemStack inhand = player.getItemOnCursor();
					ItemStack result = recipe.getResult();
					if (inhand != null) {
						if (inhand.getTypeId() == result.getTypeId()
								&& inhand.getDurability() == result.getDurability()) {
							int ramount = result.getAmount();
							int iamount = inhand.getAmount();
							int msize = result.getType().getMaxStackSize();
							if ((ramount + iamount) <= msize) {
								inhand.setAmount(inhand.getAmount() + result.getAmount());
								player.setItemOnCursor(new SpoutItemStack(inhand));
							}
						}
					} else {
						player.setItemOnCursor(new SpoutItemStack(result));
					}

					((SpoutServer) Bukkit.getServer()).getCraftingManager().removeItems(items, recipe);
				}
			}
			for (int i = 0; i < 4; ++i) {
				setItem(i, items[i]);
			}
		}
	}

	private final static int slotConversion[] = {
		1, 2, 3, 4, 0
	};

	/**
	 * Get the network index from a slot index.
	 * @param itemSlot The index for use with getItem/setItem.
	 * @return The index modified for transfer over the network, or -1 if there is no equivalent.
	 */
	@Override
	public int getNetworkSlot(int itemSlot) {
		if (itemSlot > slotConversion.length) return -1;
		return slotConversion[itemSlot];
	}

	/**
	 * Get the slot index from a network index.
	 * @param networkSlot The index received over the network.
	 * @return The index modified for use with getItem/setItem, or -1 if there is no equivalent.
	 */
	@Override
	public int getItemSlot(int networkSlot) {
		for (int i = 0; i < slotConversion.length; ++i) {
			if (slotConversion[i] == networkSlot) return i;
		}
		return -1;
	}
}
