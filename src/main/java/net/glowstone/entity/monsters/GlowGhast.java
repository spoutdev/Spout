package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.ItemID;
import net.glowstone.entity.Angerable;
import net.glowstone.entity.Damager;
import net.glowstone.util.Parameter;
import org.bukkit.entity.Ghast;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowGhast extends GlowFlying implements Ghast, Angerable {

    /**
     * Is this ghast angry?
     */
    private boolean angry;

    public GlowGhast(GlowServer server, GlowWorld world) {
        super(server, world, 56);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0) loot.add(new ItemStack(ItemID.SULPHUR, amount));
        return loot;
    }

    public boolean isAngry() {
        return angry;
    }

    public void setAngry(boolean angry) {
        this.angry = angry;
        setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)(angry ? 1 : 0)));
    }
}
