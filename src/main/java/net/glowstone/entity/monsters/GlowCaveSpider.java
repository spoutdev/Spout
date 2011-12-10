package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.Damager;
import org.bukkit.entity.CaveSpider;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowCaveSpider extends GlowSpider implements CaveSpider {
    
    public GlowCaveSpider(GlowServer server, GlowWorld world) {
        super(server, world, 59);
    }
}
