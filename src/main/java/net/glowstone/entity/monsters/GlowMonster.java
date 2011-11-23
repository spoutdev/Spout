package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowCreature;
import org.bukkit.entity.Monster;

public abstract class GlowMonster extends GlowCreature implements Monster {

    protected GlowMonster(GlowServer server, GlowWorld world, int id) {
        super(server, world, id);
    }
}
