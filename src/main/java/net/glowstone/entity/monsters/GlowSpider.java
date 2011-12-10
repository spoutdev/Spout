package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.ItemID;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Spider;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowSpider extends GlowMonster implements Spider {

    public GlowSpider(GlowServer server, GlowWorld world) {
        super(server, world, 52);
    }

    protected GlowSpider(GlowServer server, GlowWorld world, int id) {
        super(server, world, id);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int count = random.nextInt(3);
        if (count > 0) loot.add(new ItemStack(ItemID.STRING, count));
        return loot;
    }
}
