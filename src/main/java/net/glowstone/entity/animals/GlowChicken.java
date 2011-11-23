package net.glowstone.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.ItemID;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Chicken;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowChicken extends GlowAminals implements Chicken {
    /**
     * Creates a new monster.
     *
     * @param world The world this monster is in.
     * @param type  The type of monster.
     */
    public GlowChicken(GlowServer server, GlowWorld world) {
        super(server, world, 93);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0) {
            loot.add(new ItemStack(ItemID.FEATHER, amount));
            loot.add(new ItemStack(getFireTicks() > 0 ? ItemID.COOKED_CHICKEN : ItemID.RAW_CHICKEN, 1));
        }
        return loot;
    }
}
