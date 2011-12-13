package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.item.ItemID;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowZombie extends GlowMonster implements Zombie {
    
    public GlowZombie(GlowServer server, GlowWorld world) {
        super(server, world, 54);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0) loot.add(new ItemStack(ItemID.ROTTEN_FLESH, amount));
        return loot;
    }

    protected GlowZombie(GlowServer server, GlowWorld world, int id) {
        super(server, world, id);
    }
}
