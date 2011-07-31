package org.getspout.inventory;

import java.util.Arrays;

import org.bukkit.inventory.ItemStack;

import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCrafting;

public class ContribCraftingInventory extends ContribCraftInventory implements CraftingInventory, ContribInventory{
	protected IInventory result;
	public ContribCraftingInventory(InventoryCrafting inventory, IInventory result) {
		super(inventory);
		this.result = result;
	}
	
	public InventoryCrafting getMatrixHandle() {
		return (InventoryCrafting)super.getHandle();
	}
	
	public IInventory getResultHandle() {
		return this.result;
	}

	@Override
	public int getSize() {
		return this.result.getSize() + this.inventory.getSize();
	}

	@Override
	public void setContents(ItemStack[] items) {
		int resultLen = this.result.getContents().length;
		int len = this.inventory.getContents().length + resultLen;
		if (len != items.length) {
			throw new IllegalArgumentException("Invalid inventory size; expected " + len);
		}
		setContents(items[0], Arrays.copyOfRange(items, 1, items.length));
	}
	
	@Override
	public ContribCraftItemStack[] getContents() {
		ContribCraftItemStack[] items = new ContribCraftItemStack[getSize()];
		net.minecraft.server.ItemStack[] mcResultItems = this.result.getContents();

		int i = 0;
		for (i = 0; i < mcResultItems.length; i++ ) {
			items[i] = ContribCraftItemStack.fromItemStack(mcResultItems[i]);
		}
		
		net.minecraft.server.ItemStack[] mcItems = this.inventory.getContents();
		
		for (int j = 0; j < mcItems.length; j++) {
			items[i + j] = ContribCraftItemStack.fromItemStack(mcItems[j]);
		}

		return items;
	}
	
	public void setContents(ItemStack result, ItemStack[] contents) {
		setResult(result);
		setMatrix(contents);
	}
	
	@Override
	public ContribCraftItemStack getItem(int index) {
		if (index == 0) {
			if (this.result.getItem(index) != null) {
				return ContribCraftItemStack.fromItemStack(this.result.getItem(index));
			}
			return new ContribCraftItemStack(0, 1, (short)0);
		}
		else if (this.inventory.getItem(index - this.result.getSize()) != null) {
			return ContribCraftItemStack.fromItemStack(this.inventory.getItem(index - this.result.getSize()));
		}
		return new ContribCraftItemStack(0, 1, (short)0);
	}
	
	@Override
	public void setItem(int index, ItemStack item) {
		if (item != null && item.getTypeId() == 0) {
			item = null;
		}
		if (index == 0) {
			this.result.setItem(index, (item == null ? null : new net.minecraft.server.ItemStack( item.getTypeId(), item.getAmount(), item.getDurability())));
		}
		else {
			this.inventory.setItem((index - this.result.getSize()), (item == null ? null : new net.minecraft.server.ItemStack( item.getTypeId(), item.getAmount(), item.getDurability())));
		}
	}

	@Override
	public ContribCraftItemStack[] getMatrix() {
		ContribCraftItemStack[] items = new ContribCraftItemStack[getSize()];
	   // net.minecraft.server.ItemStack[] matrix = this.inventory.getContents();

		for (int i = 0; i < getSize(); i++ ) {
			items[i] = ContribCraftItemStack.fromItemStack(this.inventory.getItem(i));
		}

		return items;
	}

	@Override
	public ContribCraftItemStack getResult() {
		net.minecraft.server.ItemStack item = this.result.getItem(0);
		return ContribCraftItemStack.fromItemStack(item);
	}

	@Override
	public void setMatrix(ItemStack[] contents) {
		if (this.inventory.getContents().length != contents.length) {
			throw new IllegalArgumentException("Invalid inventory size; expected " + this.inventory.getContents().length);
		}

		net.minecraft.server.ItemStack[] mcItems = this.inventory.getContents();

		for (int i = 0; i < contents.length; i++ ) {
			ItemStack item = contents[i];
			if (item == null || item.getTypeId() <= 0) {
				mcItems[i] = null;
			} else {
				mcItems[i] = new net.minecraft.server.ItemStack( item.getTypeId(), item.getAmount(), item.getDurability());
			}
		}
	}

	@Override
	public void setResult(ItemStack item) {
		net.minecraft.server.ItemStack[] contents = this.result.getContents();
		if (item == null || item.getTypeId() <= 0) {
			contents[0] = null;
		}
		else {
			contents[0] = new net.minecraft.server.ItemStack( item.getTypeId(), item.getAmount(), item.getDurability());
		}
	}

}
