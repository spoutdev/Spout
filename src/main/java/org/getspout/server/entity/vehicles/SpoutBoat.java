package org.getspout.server.entity.vehicles;

import org.bukkit.entity.Boat;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.block.BlockID;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

import java.util.ArrayList;
import java.util.List;

public class SpoutBoat extends SpoutVehicle implements Boat {
    private boolean workOnLand;
    private double unoccupiedDeceleration, occupiedDeceleration;
    
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world The world.
     */
    public SpoutBoat(SpoutServer server, SpoutWorld world) {
        super(server, world, 41);
        maxSpeed = 0.4;
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        loot.add(new ItemStack(BlockID.WOOD, 3));
        loot.add(new ItemStack(ItemID.STICK, 2));
        return loot;
    }

    public double getOccupiedDeceleration() {
        return occupiedDeceleration;
    }

    public void setOccupiedDeceleration(double rate) {
        this.occupiedDeceleration = rate;
    }

    public double getUnoccupiedDeceleration() {
        return unoccupiedDeceleration;
    }

    public void setUnoccupiedDeceleration(double rate) {
        this.unoccupiedDeceleration = rate;
    }

    public boolean getWorkOnLand() {
        return workOnLand;
    }

    public void setWorkOnLand(boolean workOnLand) {
        this.workOnLand = workOnLand;
    }
}
