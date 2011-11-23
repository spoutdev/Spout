package net.glowstone.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.ItemID;
import net.glowstone.entity.Damager;
import org.bukkit.entity.Cow;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zml2008
 */
public class GlowCow extends GlowAminals implements Cow {
    
    /**
     * Creates a new monster.
     *
     * @param world The world this monster is in.
     * @param type  The type of monster.
     */
    public GlowCow(GlowServer server, GlowWorld world) {
        super(server, world, 5);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0) loot.add(new ItemStack(ItemID.LEATHER, amount));
        amount = random.nextInt(3) + 1;
        if (amount > 0) loot.add(new ItemStack(getFireTicks() > 0 ? ItemID.COOKED_BEEF : ItemID.RAW_BEEF, amount));
        return loot;
    }
}
