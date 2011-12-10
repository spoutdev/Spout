package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Giant;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GlowGiant extends GlowZombie implements Giant {

    public GlowGiant(GlowServer server, GlowWorld world) {
        super(server, world, 53);
    }

    public boolean isStupid() {
        return true;
    }
}
