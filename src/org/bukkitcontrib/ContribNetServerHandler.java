package org.bukkitcontrib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkitcontrib.event.inventory.InventoryClickEvent;
import org.bukkitcontrib.event.inventory.InventoryCloseEvent;
import org.bukkitcontrib.event.inventory.InventoryOpenEvent;
import org.bukkitcontrib.event.inventory.InventorySlotType;
import org.bukkitcontrib.inventory.ContribCraftInventory;
import org.bukkitcontrib.inventory.ContribCraftInventoryPlayer;
import org.bukkitcontrib.inventory.ContribCraftItemStack;


import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCraftResult;
import net.minecraft.server.InventoryPlayer;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet101CloseWindow;
import net.minecraft.server.Packet102WindowClick;
import net.minecraft.server.ContainerPlayer;
import net.minecraft.server.ContainerFurnace;
import net.minecraft.server.ContainerChest;
import net.minecraft.server.ContainerDispenser;
import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.Packet106Transaction;
import net.minecraft.server.Slot;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;

public class ContribNetServerHandler extends NetServerHandler{
    
    protected Map<Integer, Short> n = new HashMap<Integer, Short>();
    protected boolean activeInventory = false;
    protected Location activeLocation = null;

    public ContribNetServerHandler(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }
    
    public IInventory getActiveInventory() {
        IInventory inventory = null;
        try {
            if (this.player.activeContainer instanceof ContainerChest) {
                Field a = ContainerChest.class.getDeclaredField("a");
                a.setAccessible(true);
                inventory = (IInventory) a.get((ContainerChest)this.player.activeContainer);
            }
            else if (this.player.activeContainer instanceof ContainerPlayer) {
                inventory = ((ContainerPlayer)this.player.activeContainer).b;
            }
            else if (this.player.activeContainer instanceof ContainerFurnace) {
                Field a = ContainerFurnace.class.getDeclaredField("a");
                a.setAccessible(true);
                inventory = (TileEntityFurnace)a.get((ContainerFurnace)this.player.activeContainer);
            }
            else if (this.player.activeContainer instanceof ContainerDispenser) {
                Field a = ContainerDispenser.class.getDeclaredField("a");
                a.setAccessible(true);
                inventory = (TileEntityDispenser)a.get((ContainerDispenser)this.player.activeContainer);
            }
            else if (this.player.activeContainer instanceof ContainerWorkbench) {
                inventory = ((ContainerWorkbench)this.player.activeContainer).b;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            inventory = this.player.inventory;
        }
        return inventory;
    }
    
    public InventorySlotType getActiveInventorySlotType(int clicked) {
        if (clicked == -999) {
            return InventorySlotType.OUTSIDE;
        }
        IInventory active = getActiveInventory();
        int size = active.getSize();
        if (this.player.activeContainer instanceof ContainerChest) {
            return InventorySlotType.CONTAINER;
        }
        else if (this.player.activeContainer instanceof ContainerPlayer) {
            if (clicked == 0) return InventorySlotType.RESULT;
            if (clicked < size) return InventorySlotType.CRAFTING;
            size += 4;
            if (clicked < size) return InventorySlotType.ARMOR;
            return InventorySlotType.CONTAINER;
        }
        else if (this.player.activeContainer instanceof ContainerFurnace) {
            if (clicked == 1) return InventorySlotType.FUEL;
            return InventorySlotType.RESULT;
        }
        else if (this.player.activeContainer instanceof ContainerDispenser) {
            return InventorySlotType.CONTAINER;
        }
        else if (this.player.activeContainer instanceof ContainerWorkbench) {
            if (clicked == 0) return InventorySlotType.RESULT;
            else if (clicked < size) return InventorySlotType.CRAFTING;
            return InventorySlotType.CONTAINER;
        }
        if (clicked >= size + 27) return InventorySlotType.QUICKBAR;
        if (clicked >= size) return InventorySlotType.PACK;
        return InventorySlotType.CONTAINER;
    }
    
    public CraftItemStack fromItemStack(ItemStack item) {
        if (item == null) return null;
        return new CraftItemStack(item.id, item.count, (short) item.damage);
    }
    
    public CraftInventory getCraftInventory(IInventory inventory) {
        if (inventory instanceof InventoryPlayer) {
            return new ContribCraftInventoryPlayer((InventoryPlayer)inventory);
        }
        return new ContribCraftInventory(inventory);
    }
    
    public ContribCraftItemStack getContribCraftItemStack(org.bukkit.inventory.ItemStack item) {
        if (item == null) return null;
        if (item instanceof ContribCraftItemStack) {
            return (ContribCraftItemStack)item;
        }
        return new ContribCraftItemStack(item.getTypeId(), item.getAmount(), item.getDurability());
    }
    
    @Override
    public void a(Packet101CloseWindow packet) {
        IInventory inventory = getActiveInventory();
        
        
        InventoryCloseEvent event = new InventoryCloseEvent((Player)this.player.getBukkitEntity(), getCraftInventory(inventory), activeLocation);
        Bukkit.getServer().getPluginManager().callEvent(event);
        
        if (event.isCancelled()) {
            inventory = ((ContribCraftInventory)event.getInventory()).getHandle();
            if (inventory instanceof TileEntityFurnace) {
                this.player.a((TileEntityFurnace)inventory);
            }
            else if (inventory instanceof TileEntityDispenser) {
                this.player.a((TileEntityDispenser)inventory);
            }
            else if (inventory instanceof InventoryCraftResult && this.player.activeContainer instanceof ContainerWorkbench) {
                sendPacket(new Packet100OpenWindow(packet.a, 1, "Crafting", 9));
                this.player.syncInventory();
            }
            else if (inventory instanceof InventoryCraftResult) {
                //There is no way to force a player's own inventory back open.
            }
            else {
                this.player.a(inventory);
            }
        }
        else {
            activeInventory = false;
            activeLocation = null;
            super.a(packet);
        }
    }
    
    @Override
    public void a(Packet106Transaction packet) {
        Short oshort = this.n.get(Integer.valueOf(this.player.activeContainer.f));

        if (oshort != null && packet.b == oshort.shortValue() && this.player.activeContainer.f == packet.a && !this.player.activeContainer.c(this.player)) {
            this.player.activeContainer.a(this.player, true);
        }
    }
    
    @Override
    public void a(Packet102WindowClick packet) {
        if (this.player.activeContainer.f == packet.a && this.player.activeContainer.c(this.player)) {
            IInventory inventory = getActiveInventory();
            CraftPlayer player = (CraftPlayer) this.player.getBukkitEntity();
            ItemStack before = ItemStack.b(packet.e);
            ItemStack cursorBefore = this.player.inventory.j();
            CraftItemStack slot = fromItemStack(before);
            CraftItemStack cursor = fromItemStack(cursorBefore);
            InventorySlotType type = getActiveInventorySlotType(packet.b);
            
            //alert of a newly opened inventory
            if (!activeInventory) {
                activeInventory = true;
                InventoryOpenEvent event = new InventoryOpenEvent(player, getCraftInventory(inventory), activeLocation);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    this.player.x();
                    activeInventory = false;
                    activeLocation = null;
                    return;
                }
            }
            
            InventoryClickEvent event = new InventoryClickEvent(player, getCraftInventory(inventory), type, slot, cursor, packet.b, activeLocation);
            Bukkit.getServer().getPluginManager().callEvent(event);
            
            ItemStack itemstack = null;
            // NOTE: Successful means that its successful as-is; thus, only becomes true for default behaviour
            boolean clickSuccessful = false;
            switch(event.getResult()) {
            case DEFAULT: // Default behaviour
                // CraftBukkit end
                itemstack = this.player.activeContainer.a(packet.b, packet.c, packet.f, this.player);
                // CraftBukkit start
                clickSuccessful = ItemStack.equals(packet.e, itemstack);
                break;
            case DENY: // Deny default behaviour, but allow the handler to override
                itemstack = getContribCraftItemStack(event.getItem()) != null ? getContribCraftItemStack(event.getItem()).getHandle() : null;
                if(packet.b != -999) { // Only swap if target is not OUTSIDE
                    if (itemstack != null) {
                        this.player.activeContainer.b(packet.b).c(itemstack);
                    }
                    else if (event.getCursor() != null) {
                          itemstack = new ItemStack(event.getCursor().getTypeId(), event.getCursor().getAmount(), event.getCursor().getDurability());
                          this.player.activeContainer.b(packet.b).c(itemstack);
                    }
                }
                this.player.inventory.b((ItemStack) null);
                break;
            case ALLOW: // Allow the placement unconditionally
                if (packet.b == -999) { // Clicked outside, just defer to default
                    itemstack = this.player.activeContainer.a(packet.b, packet.c, packet.f, this.player);
                }
                else {
                    itemstack = this.player.activeContainer.a(packet.b).j();
                    ItemStack cursorstack = this.player.inventory.j();
                    int click = packet.c, rclick = 1, lclick = 0;
                    if(click == lclick && (itemstack != null && cursorstack != null && itemstack.a(cursorstack))) {
                        // Left-click full slot with full cursor of same item; merge stacks
                        itemstack.count += cursorstack.count;
                        this.player.inventory.b((ItemStack) null);
                    }
                    else if (click == lclick || (itemstack != null && cursorstack != null && !itemstack.a(cursorstack))) {
                        // Either left-click, or right-click full slot with full cursor of different item; just swap contents
                        this.player.activeContainer.b(packet.b).c(cursorstack);
                        this.player.inventory.b(itemstack);
                    }
                    else if (click == rclick) { // Right-click with either slot or cursor empty
                        if (itemstack == null) { // Slot empty; drop one
                            this.player.activeContainer.b(packet.b).c(cursorstack.a(1));
                            if(cursorstack.count == 0)
                                this.player.inventory.b((ItemStack) null);
                        }
                        else if (cursorstack == null) { // Cursor empty; take half
                            this.player.inventory.b(itemstack.a((itemstack.count + 1) / 2));
                        }
                        else { // Neither empty, but same item; drop one
                            ItemStack drop = cursorstack.a(1);
                            itemstack.count += drop.count;
                            this.player.activeContainer.b(packet.b).c(itemstack);
                            if (cursorstack.count == 0) {
                                this.player.inventory.b((ItemStack) null);
                            }
                        }
                    }
                    itemstack = this.player.activeContainer.a(packet.b);
                }
                break;
            }

            if (clickSuccessful) {
                this.player.netServerHandler.sendPacket(new Packet106Transaction(packet.a, packet.d, true));
                this.player.h = true;
                this.player.activeContainer.a();
                this.player.y();
                this.player.h = false;
            }
            else {
                this.n.put(Integer.valueOf(this.player.activeContainer.f), Short.valueOf(packet.d));
                this.player.netServerHandler.sendPacket(new Packet106Transaction(packet.a, packet.d, false));
                this.player.activeContainer.a(this.player, false);
                ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();

                for (int i = 0; i < this.player.activeContainer.e.size(); ++i) {
                    arraylist.add(((Slot) this.player.activeContainer.e.get(i)).getItem());
                }

                this.player.a(this.player.activeContainer, arraylist);
            }
        }
    }
}
