package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;

import org.bukkit.entity.CaveSpider;

public class GlowCaveSpider extends GlowSpider implements CaveSpider {
    
    public GlowCaveSpider(GlowServer server, GlowWorld world) {
        super(server, world, 59);
    }
}
