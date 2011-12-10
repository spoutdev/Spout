package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Silverfish;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowSilverfish extends GlowMonster implements Silverfish {

    public GlowSilverfish(GlowServer server, GlowWorld world) {
        super(server, world, 60);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        return null;
    }
}
