package org.bukkitcontrib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkitcontrib.event.inventory.InventoryClickEvent;
import org.bukkitcontrib.event.inventory.InventoryCloseEvent;
import org.bukkitcontrib.event.inventory.InventoryCraftEvent;
import org.bukkitcontrib.event.inventory.InventoryOpenEvent;
import org.bukkitcontrib.event.inventory.InventoryPlayerClickEvent;
import org.bukkitcontrib.event.inventory.InventorySlotType;
import org.bukkitcontrib.inventory.ContribCraftInventory;
import org.bukkitcontrib.inventory.ContribCraftInventoryPlayer;
import org.bukkitcontrib.inventory.ContribCraftItemStack;
import org.bukkitcontrib.inventory.ContribCraftingInventory;
import org.bukkitcontrib.inventory.ContribInventory;
import org.bukkitcontrib.inventory.CraftingInventory;


import net.minecraft.server.Container;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCraftResult;
import net.minecraft.server.InventoryCrafting;
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
    protected ItemStack lastOverrideDisplayStack = null;

    public ContribNetServerHandler(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }

    public void setActiveInventoryLocation(Location location) {
        activeLocation = location;
    }
    
    public Location getActiveInventoryLocation() {
        return activeLocation;
    }

    public void setActiveInventory(boolean active) {
        activeInventory = active;
    }

    public ContribInventory getActiveInventory() {
        return getInventoryFromContainer(this.player.activeContainer);
    }

    public ContribInventory getDefaultInventory() {
        if (this.player.defaultContainer.equals(this.player.activeContainer)){
            return null;
        }
        return getInventoryFromContainer(this.player.defaultContainer);
    }

    public InventorySlotType getInventorySlotType(int clicked) {
        if (clicked < 9) return InventorySlotType.QUICKBAR;
        return InventorySlotType.CONTAINER;
    }

    public InventorySlotType getActiveInventorySlotType(int clicked) {
        if (clicked == -999) {
            return InventorySlotType.OUTSIDE;
        }
        ContribInventory active = getActiveInventory();
        int size = active.getSize();
        if (this.player.activeContainer instanceof ContainerChest) {
            return InventorySlotType.CONTAINER;
        }
        else if (this.player.activeContainer instanceof ContainerPlayer) {
            if (clicked == 0) return InventorySlotType.RESULT;
            if (clicked < 5) return InventorySlotType.CRAFTING;
            if (clicked == 5) return InventorySlotType.HELMET;
            if (clicked == 6) return InventorySlotType.ARMOR;
            if (clicked == 7) return InventorySlotType.LEGGINGS;
            if (clicked == 8) return InventorySlotType.BOOTS;
            if (clicked < size) return InventorySlotType.CONTAINER;
            return InventorySlotType.QUICKBAR;
        }
        else if (this.player.activeContainer instanceof ContainerFurnace) {
            if (clicked == 0) return InventorySlotType.SMELTING;
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

    @Override
    public void a(Packet101CloseWindow packet) {
        ContribInventory inventory = getActiveInventory();

        InventoryCloseEvent event = new InventoryCloseEvent((Player)this.player.getBukkitEntity(), inventory, getDefaultInventory(), activeLocation);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            IInventory inv = ((ContribInventory)event.getInventory()).getHandle();
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
                this.player.a(inv);
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
        if (this.player.dead){
            return;
        }
        Short oshort = this.n.get(Integer.valueOf(this.player.activeContainer.windowId));

        if (oshort != null && packet.b == oshort.shortValue() && this.player.activeContainer.windowId == packet.a && !this.player.activeContainer.c(this.player)) {
            this.player.activeContainer.a(this.player, true);
        }
    }

    @Override
    public void a(Packet102WindowClick packet) {
        if (this.player.activeContainer.windowId == packet.a && this.player.activeContainer.c(this.player)) {
            ContribInventory inventory = getActiveInventory();
            CraftPlayer player = (CraftPlayer) this.player.getBukkitEntity();
            ItemStack before = ItemStack.b(packet.e);
            ItemStack cursorBefore = this.player.inventory.j();
            ContribCraftItemStack slot = ContribCraftItemStack.fromItemStack(before);
            ContribCraftItemStack cursor = ContribCraftItemStack.fromItemStack(cursorBefore);
            InventorySlotType type = getActiveInventorySlotType(packet.b);
            boolean clickSuccessful = true;
            final int windowId = packet.a;

            //alert of a newly opened inventory
            if (!activeInventory) {
                activeInventory = true;
                InventoryOpenEvent event = new InventoryOpenEvent(player, inventory, getDefaultInventory(), activeLocation);
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    this.player.y();
                    activeInventory = false;
                    activeLocation = null;
                    return;
                }
            }

            // Fire InventoryChange or InventoryCraft event
            if (packet.b != -999) {
                if (inventory instanceof CraftingInventory) {
                    CraftingInventory crafting = (CraftingInventory) inventory;
                    InventoryCrafting recipe = (InventoryCrafting) crafting.getMatrixHandle();

                    ContribCraftItemStack craftResult = ContribCraftItemStack.fromItemStack(CraftingManager.getInstance().craft(recipe));
                    ContribCraftItemStack[] recipeContents = new ContribCraftItemStack[recipe.getSize()];
                    for (int i = 0; i < recipe.getSize(); i++) {
                        org.bukkit.inventory.ItemStack temp = crafting.getMatrix()[i];
                        recipeContents[i] = temp == null ? null : new ContribCraftItemStack(temp.getTypeId(), temp.getAmount(), temp.getDurability());
                    }

                    ContribCraftItemStack[][] matrix = null;
                    if (recipe.getSize() == 4) {
                        matrix = new ContribCraftItemStack[][] {
                            Arrays.copyOfRange(recipeContents, 0, 2),
                            Arrays.copyOfRange(recipeContents, 2, 4)
                        };
                    }
                    else if (recipe.getSize() == 9) {
                        matrix = new ContribCraftItemStack[][] {
                            Arrays.copyOfRange(recipeContents, 0, 3),
                            Arrays.copyOfRange(recipeContents, 3, 6),
                            Arrays.copyOfRange(recipeContents, 6, 9)
                        };
                    }
                    //Clicking to grab the crafting result
                    if (type == InventorySlotType.RESULT) {
                        InventoryCraftEvent craftEvent = new InventoryCraftEvent(this.getPlayer(), crafting, this.activeLocation, type, packet.b,  matrix, craftResult, cursor, packet.c == 0, packet.f);
                        Bukkit.getServer().getPluginManager().callEvent(craftEvent);
                        craftEvent.getInventory().setResult(craftEvent.getResult());
                        cursor = craftEvent.getCursor() == null ? null : new ContribCraftItemStack(craftEvent.getCursor().getTypeId(), craftEvent.getCursor().getAmount(), craftEvent.getCursor().getDurability());
                        if (craftEvent.isCancelled()) {
                            craftEvent.getInventory().setMatrix(recipeContents);
                            setCursorSlot(cursor != null ? cursor.getHandle() : null);
                            clickSuccessful = false;
                        }
                    }
                }
            }

            if (clickSuccessful) {
                clickSuccessful = handleInventoryClick(packet, type, slot, cursor, inventory);
            }

            if (clickSuccessful) {
                this.player.netServerHandler.sendPacket(new Packet106Transaction(windowId, packet.d, true));
                this.player.h = true;
                this.player.activeContainer.a();
                this.player.z();
                this.player.h = false;
            }
            else {
                this.n.put(Integer.valueOf(this.player.activeContainer.windowId), Short.valueOf(packet.d));
                this.player.netServerHandler.sendPacket(new Packet106Transaction(windowId, packet.d, false));
                this.player.activeContainer.a(this.player, false);
                ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();

                for (int i = 0; i < this.player.activeContainer.e.size(); ++i) {
                    arraylist.add(((Slot) this.player.activeContainer.e.get(i)).getItem());
                }

                this.player.a(this.player.activeContainer, arraylist);
            }
        }
    }
    
    public boolean handleInventoryClick(Packet102WindowClick packet, InventorySlotType type, ContribCraftItemStack slot, ContribCraftItemStack cursor, ContribInventory inventory) {
        InventoryClickEvent event = null;
        Result result = Result.DEFAULT;
        boolean success = false;
        final int LEFT_CLICK = 0;
        final int RIGHT_CLICK = 1;
        int click = packet.c;

        //clicked on bottom player inventory
        if (!(this.player.activeContainer instanceof ContainerPlayer) && this.player.defaultContainer instanceof ContainerPlayer && packet.b >= inventory.getSize()) {
            int activeSlot = packet.b - inventory.getSize() + 9;
            if (activeSlot > this.getPlayer().getInventory().getSize()) {
                activeSlot -= this.getPlayer().getInventory().getSize();
            }
            type = getInventorySlotType(activeSlot);
            event = new InventoryPlayerClickEvent(this.getPlayer(), this.getPlayer().getInventory(), type, slot, cursor, activeSlot, click == LEFT_CLICK, packet.f, activeLocation);
        }
        else {
            event = new InventoryClickEvent(this.getPlayer(), inventory, type, slot, cursor, packet.b, click == LEFT_CLICK, packet.f, activeLocation);
        }

        if (event != null) {
             Bukkit.getServer().getPluginManager().callEvent(event);
             result = event.getResult();
             cursor = ContribCraftItemStack.getContribCraftItemStack(event.getCursor());
             slot = ContribCraftItemStack.getContribCraftItemStack(event.getItem());
        }

        //initialize setup
        ItemStack itemstack = slot != null ? slot.getHandle() : null;
        ItemStack cursorstack = cursor != null ? cursor.getHandle() : null;

        // NOTE: Successful means that its successful as-is; thus, only becomes true for default behaviour

        switch(result) {
        case DEFAULT:
            itemstack = this.player.activeContainer.a(packet.b, packet.c, packet.f, this.player);
            success = ItemStack.equals(packet.e, itemstack);
            break;
        case DENY:
            if(packet.b != -999) { // Only swap if target is not OUTSIDE
                if (itemstack != null) {
                    setActiveSlot(packet.b, itemstack);
                    setCursorSlot((ItemStack) null);
                }
                if (event.getCursor() != null) {
                      setActiveSlot(packet.b, itemstack);
                      //cursorstack = new ItemStack(event.getCursor().getTypeId(), event.getCursor().getAmount(), event.getCursor().getDurability());
                      setCursorSlot(cursorstack);
                }
            }
            
            break;
        case ALLOW: // Allow the placement unconditionally
            if (packet.b == -999) { // Clicked outside, just defer to default
                itemstack = this.player.activeContainer.a(packet.b, packet.c, packet.f, this.player);
            }
            else {
                if(click == LEFT_CLICK && (itemstack != null && cursorstack != null && itemstack.doMaterialsMatch(cursorstack))) {
                    // Left-click full slot with full cursor of same item; merge stacks
                    itemstack.count += cursorstack.count;
                    cursorstack = null;
                }
                else if (click == LEFT_CLICK || (itemstack != null && cursorstack != null && !itemstack.doMaterialsMatch(cursorstack))) {
                    // Either left-click, or right-click full slot with full cursor of different item; just swap contents
                    ItemStack temp = itemstack;
                    itemstack = cursorstack;
                    cursorstack = temp;
                }
                else if (click == RIGHT_CLICK) { // Right-click with either slot or cursor empty
                    if (itemstack == null) { // Slot empty; drop one
                        if (cursorstack != null) {
                            itemstack = cursorstack.a(1);
                            if (cursorstack.count == 0) {
                               cursorstack = null;
                            }
                        }
                    }
                    else if (cursorstack == null) { // Cursor empty; take half
                        cursorstack = itemstack.a((itemstack.count + 1) / 2);
                    }
                    else { // Neither empty, but same item; drop one
                        ItemStack drop = cursorstack.a(1);
                        itemstack.count += drop.count;
                        if (cursorstack.count == 0) {
                             cursorstack = null;
                        }
                    }
                }
                //update the stacks
                setActiveSlot(packet.b, itemstack);
                setCursorSlot(cursorstack);
            }
            break;
        }
        return success;
    }

    public void setActiveSlot(int slot, ItemStack item) {
        this.player.activeContainer.b(slot).c(item);
    }

    public void setCursorSlot(ItemStack item) {
        this.player.inventory.b(item);
    }
    
    private ContribInventory getInventoryFromContainer(Container container) {
        try {
            if (container instanceof ContainerChest) {
                Field a = ContainerChest.class.getDeclaredField("a");
                a.setAccessible(true);
                return new ContribCraftInventory((IInventory) a.get((ContainerChest)container));
            }
            if (container instanceof ContainerPlayer) {
               return new ContribCraftInventoryPlayer(this.player.inventory, new ContribCraftingInventory(((ContainerPlayer)container).craftInventory, ((ContainerPlayer)container).resultInventory));
            }
            if (container instanceof ContainerFurnace) {
                Field a = ContainerFurnace.class.getDeclaredField("a");
                a.setAccessible(true);
                return new ContribCraftInventory((TileEntityFurnace)a.get((ContainerFurnace)container));
            }
            if (container instanceof ContainerDispenser) {
                Field a = ContainerDispenser.class.getDeclaredField("a");
                a.setAccessible(true);
               return new ContribCraftInventory((TileEntityDispenser)a.get((ContainerDispenser)container));
            }
            if (container instanceof ContainerWorkbench) {
                return new ContribCraftingInventory(((ContainerWorkbench)container).craftInventory, ((ContainerWorkbench)container).resultInventory);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ContribCraftInventory(this.player.inventory);
        }
        return null;
   }

}
