package net.glowstone.entity.vehicles;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.BlockID;
import net.glowstone.item.ItemID;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Boat;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowBoat extends GlowVehicle implements Boat {
    private boolean workOnLand;
    private double unoccupiedDeceleration, occupiedDeceleration;
    
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world The world.
     */
    public GlowBoat(GlowServer server, GlowWorld world) {
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
