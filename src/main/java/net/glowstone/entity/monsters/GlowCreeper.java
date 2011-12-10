package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.ItemID;
import net.glowstone.entity.Damager;
import net.glowstone.util.Parameter;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class GlowCreeper extends GlowMonster implements Creeper {
    
    /**
     * Whether this creeper is powered or not.
     */
    private boolean powered;
    
    public GlowCreeper(GlowServer server, GlowWorld world) {
        super(server, world, 50);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0) loot.add(new ItemStack(ItemID.SULPHUR, amount));
        if (damager != null && damager instanceof Arrow) {
            if (((Arrow)damager).getShooter() instanceof Skeleton) {
                loot.add(new ItemStack(ItemID.DISC_13 + random.nextInt(2), 1));
            }
        }
        return loot;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean value) {
        this.powered = value;
        setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)(powered ? 1 : 0)));
    }
}
