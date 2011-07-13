package org.bukkitcontrib.inventory;

import java.util.Collection;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;

public class CustomMCInventory implements IInventory{
    protected ItemStack[] items;
    protected String name;
    
    public CustomMCInventory(org.bukkit.inventory.ItemStack items[], String name) {
        this.items = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null|| items[i].getTypeId() == 0) {
                this.items[i] = null;
            }
            else {
                ContribCraftItemStack item = ContribCraftItemStack.getContribCraftItemStack(items[i]);
                this.items[i] = item == null ? null : item.getHandle();
            }
        }
        this.name = name;
    }
    
    public CustomMCInventory(Collection<org.bukkit.inventory.ItemStack> items, String name) {
        this.items = new ItemStack[items.size()];
        int pos = 0;
        for (org.bukkit.inventory.ItemStack item : items) {
            if (item == null|| item.getTypeId() == 0) {
                this.items[pos] = null;
            }
            else {
                ContribCraftItemStack temp = ContribCraftItemStack.getContribCraftItemStack(item);
                this.items[pos] = temp == null ? null : temp.getHandle();
            }
            pos++;
        }
        this.name = name;
    }
    
    public CustomMCInventory(int size, String name) {
        this.items = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            this.items[i] = null;
        }
        this.name = name;
    }

    @Override
    public boolean a_(EntityHuman arg0) {
        return true;
    }

    @Override
    public ItemStack[] getContents() {
        return this.items;
    }

    @Override
    public ItemStack getItem(int i) {
        return this.items[i];
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getSize() {
        return items.length;
    }

    @Override
    public void setItem(int i, ItemStack item) {
        this.items[i] = item;
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        if (this.items[i] != null) {
            ItemStack itemstack;

            if (this.items[i].count <= j) {
                itemstack = this.items[i];
                this.items[i] = null;
                return itemstack;
            }
            else {
                itemstack = this.items[i].a(j);
                if (this.items[i].count == 0) {
                    this.items[i] = null;
                }

                return itemstack;
            }
        }
        return null;
    }

    @Override
    public void update() {
        
    }

}
