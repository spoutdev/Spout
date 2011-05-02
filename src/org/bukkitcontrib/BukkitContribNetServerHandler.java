package org.bukkitcontrib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukitcontrib.inventory.ContribCraftInventory;
import org.bukkit.entity.Player;
import org.bukkitcontrib.event.inventory.InventoryCloseEvent;


import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCraftResult;
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

public class BukkitContribNetServerHandler extends NetServerHandler{
    
    private Map<Integer, Short> n = new HashMap<Integer, Short>();

    public BukkitContribNetServerHandler(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }
    
    @Override
    public void a(Packet101CloseWindow packet) {
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
            super.a(packet);
            e.printStackTrace();
            return;
        }
        InventoryCloseEvent event = new InventoryCloseEvent((Player)this.player.getBukkitEntity(), 
            new ContribCraftInventory(inventory));
        BukkitContrib.getMinecraftServer().getPluginManager().callEvent(event);
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
            ItemStack itemstack = this.player.activeContainer.a(packet.b, packet.c, packet.f, this.player);

            if (ItemStack.equals(packet.e, itemstack)) {
                this.player.netServerHandler.sendPacket(new Packet106Transaction(packet.a, packet.d, true));
                this.player.h = true;
                this.player.activeContainer.a();
                this.player.y();
                this.player.h = false;
            } else {
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
