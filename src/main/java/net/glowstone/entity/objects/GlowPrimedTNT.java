package net.glowstone.entity.objects;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.Damager;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowPrimedTNT extends GlowExplosive implements TNTPrimed {

    private int fuseTicks;
    
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public GlowPrimedTNT(GlowServer server, GlowWorld world) {
        super(server, world, 50);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        return null;
    }

    public void setFuseTicks(int fuseTicks) {
        this.fuseTicks = fuseTicks;
    }

    public int getFuseTicks() {
        return fuseTicks;
    }
}
