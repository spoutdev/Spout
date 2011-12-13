package net.glowstone.entity.water;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.item.ItemID;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GlowSquid extends GlowWaterMob implements Squid {
    
    /**
     * Creates a new monster.
     *
     * @param world The world this monster is in.
     * @param server The server this entity is part of
     */
    public GlowSquid(GlowServer server, GlowWorld world) {
        super(server, world, 94);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int count = random.nextInt(3) + 1;
        if (count > 0) {
            loot.add(new ItemStack(ItemID.INK_SACK, count));
        }
        return loot;
    }
}
