package org.getspout.server.entity.objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Item;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.msg.Message;
import org.getspout.server.msg.SpawnItemMessage;
import org.getspout.server.util.Position;


import java.util.List;

/**
 * Represents an item that is also an {@link org.getspout.server.entity.SpoutEntity} within the world.
 * @author Graham Edgecombe
 */
public final class SpoutItem extends SpoutEntity implements Item {

    /**
     * The item.
     */
    private ItemStack item;
    
    /**
     * The remaining delay until this item may be picked up.
     */
    private int pickupDelay;

    /**
     * Creates a new item entity.
     * @param world The world.
     * @param item The item.
     */
    public SpoutItem(SpoutServer server, SpoutWorld world, ItemStack item) {
        super(server, world);
        this.item = item;
        pickupDelay = 20;
    }

    /**
     * Gets the item that this {@link SpoutItem} represents.
     * @return The item.
     */
    public ItemStack getItemStack() {
        return item;
    }

    /**
     * Sets the item that this item represents.
     * @param stack The new ItemStack to use.
     */
    public void setItemStack(ItemStack stack) {
        item = stack.clone();
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return new SpawnItemMessage(id, item, x, y, z, yaw, pitch, 0);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        return null;
    }

    public int getPickupDelay() {
        return pickupDelay;
    }

    public void setPickupDelay(int delay) {
        pickupDelay = delay;
    }

}
