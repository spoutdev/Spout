package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.item.ItemID;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowSkeleton extends GlowMonster implements Skeleton {

    public GlowSkeleton(GlowServer server, GlowWorld world) {
        super(server, world, 51);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();

        int count = this.random.nextInt(3);
        if (count > 0) loot.add(new ItemStack(ItemID.ARROW, count));
        count = this.random.nextInt(3);
        if (count > 0) loot.add(new ItemStack(ItemID.BONE, count));
        return loot;
    }
}
