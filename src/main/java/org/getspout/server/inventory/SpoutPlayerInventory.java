package org.getspout.server.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * An Inventory representing the items a player is holding.
 */
public class SpoutPlayerInventory extends SpoutInventory implements PlayerInventory {
	public static final int HELMET_SLOT = 36;
	public static final int CHESTPLATE_SLOT = 37;
	public static final int LEGGINGS_SLOT = 38;
	public static final int BOOTS_SLOT = 39;

	private final CraftingInventory crafting = new CraftingInventory();

	private int heldSlot = 0;

	public SpoutPlayerInventory() {
		// all player inventories are ID 0
		// 36 = 4 rows of 9
		// + 4 = armor, completed inventory
		super((byte) 0, 40);
	}

	/**
	 * Return the name of the inventory
	 *
	 * @return The inventory name
	 */
	@Override
	public String getName() {
		return "Player Inventory";
	}

	/**
	 * Get the crafting inventory.
	 *
	 * @return The CraftingInventory attached to this player
	 */
	public CraftingInventory getCraftingInventory() {
		return crafting;
	}

	/**
	 * Set the slot number of the currently held item
	 *
	 * @return Held item slot number
	 */
	public void setHeldItemSlot(int slot) {
		if (slot < 0) {
			heldSlot = 0;
		} else if (slot > 8) {
			heldSlot = 8;
		} else {
			heldSlot = slot;
		}
		setItemInHand(getItemInHand());
	}

	@Override
	public SpoutItemStack[] getArmorContents() {
		SpoutItemStack[] armor = new SpoutItemStack[4];
		for (int i = 0; i < 4; ++i) {
			armor[i] = getItem(HELMET_SLOT + i);
		}
		return armor;
	}

	@Override
	public void setArmorContents(ItemStack[] items) {
		if (items.length != 4) {
			throw new IllegalArgumentException("Length of armor must be 4");
		}
		for (int i = 0; i < 4; ++i) {
			setItem(HELMET_SLOT + i, items[i]);
		}
	}

	@Override
	public SpoutItemStack getHelmet() {
		return getItem(HELMET_SLOT);
	}

	@Override
	public SpoutItemStack getChestplate() {
		return getItem(CHESTPLATE_SLOT);
	}

	@Override
	public SpoutItemStack getLeggings() {
		return getItem(LEGGINGS_SLOT);
	}

	@Override
	public SpoutItemStack getBoots() {
		return getItem(BOOTS_SLOT);
	}

	@Override
	public void setHelmet(ItemStack helmet) {
		setItem(HELMET_SLOT, helmet);
	}

	@Override
	public void setChestplate(ItemStack chestplate) {
		setItem(CHESTPLATE_SLOT, chestplate);
	}

	@Override
	public void setLeggings(ItemStack leggings) {
		setItem(LEGGINGS_SLOT, leggings);
	}

	@Override
	public void setBoots(ItemStack boots) {
		setItem(BOOTS_SLOT, boots);
	}

	@Override
	public SpoutItemStack getItemInHand() {
		return getItem(heldSlot);
	}

	@Override
	public void setItemInHand(ItemStack stack) {
		setItem(heldSlot, stack);
	}

	@Override
	public int getHeldItemSlot() {
		return heldSlot;
	}

	// Helper stuff

	private final static int slotConversion[] = {36, 37, 38, 39, 40, 41, 42, 43, 44, // quickbar
	9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 5, 6, 7, 8 // armor
	};

	/**
	 * Get the network index from a slot index.
	 *
	 * @param itemSlot The index for use with getItem/setItem.
	 * @return The index modified for transfer over the network, or -1 if there
	 *         is no equivalent.
	 */
	@Override
	public int getNetworkSlot(int itemSlot) {
		if (itemSlot > slotConversion.length) {
			return -1;
		}
		return slotConversion[itemSlot];
	}

	/**
	 * Get the slot index from a network index.
	 *
	 * @param networkSlot The index received over the network.
	 * @return The index modified for use with getItem/setItem, or -1 if there
	 *         is no equivalent.
	 */
	@Override
	public int getItemSlot(int networkSlot) {
		for (int i = 0; i < slotConversion.length; ++i) {
			if (slotConversion[i] == networkSlot) {
				return i;
			}
		}
		return -1;
	}
}
