package net.glowstone.entity.water;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowCreature;
import org.bukkit.entity.WaterMob;

public abstract class GlowWaterMob extends GlowCreature implements WaterMob {

    /**
     * Creates a new monster.
     *
     * @param world The world this monster is in.
     * @param type  The type of monster.
     */
    public GlowWaterMob(GlowServer server, GlowWorld world, int type) {
        super(server, world, type);
    }
}
