package net.glowstone.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowCreature;
import org.bukkit.entity.Animals;

public abstract class GlowAnimals extends GlowCreature implements Animals {
    /**
     * Creates a new monster.
     *
     * @param world The world this monster is in.
     * @param type  The type of monster.
     */
    public GlowAnimals(GlowServer server, GlowWorld world, int type) {
        super(server, world, type);
    }
}
