package org.getspout.server.entity.monsters;

import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.Damager;
import org.getspout.server.item.ItemID;

import java.util.ArrayList;
import java.util.List;

public class SpoutSkeleton extends SpoutMonster implements Skeleton {

    public SpoutSkeleton(SpoutServer server, SpoutWorld world) {
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
