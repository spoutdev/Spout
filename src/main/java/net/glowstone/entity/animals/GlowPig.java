package net.glowstone.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.ItemID;
import net.glowstone.entity.Damager;
import net.glowstone.util.Parameter;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class GlowPig extends GlowAminals implements Pig {
    
    /**
     * Whether this pig has a saddle
     */
    private boolean saddled;
    
    /**
     * Creates a new monster.
     *
     * @param world The world this monster is in.
     * @param type  The type of monster.
     */
    public GlowPig(GlowServer server, GlowWorld world) {
        super(server, world, 90);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0) {
            loot.add(new ItemStack(getFireTicks() > 0 ? ItemID.GRILLED_PORK : ItemID.PORK, amount));
        }
        return loot;
    }

    public boolean hasSaddle() {
        return saddled;
    }

    public void setSaddle(boolean saddled) {
        this.saddled = saddled;
        setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)(this.saddled ? 1 : 0)));
    }
}
