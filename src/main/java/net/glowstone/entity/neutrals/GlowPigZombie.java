package net.glowstone.entity.neutrals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.item.ItemID;
import net.glowstone.entity.Angerable;
import net.glowstone.entity.Damager;
import net.glowstone.entity.monsters.GlowZombie;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowPigZombie extends GlowZombie implements PigZombie, Angerable {
    private int anger;
    private boolean angry;

    public GlowPigZombie(GlowServer server, GlowWorld world) {
        super(server, world, 57);
    }

    public int getAnger() {
        return anger;
    }

    public void setAnger(int level) {
        this.anger = level;
    }

    public void setAngry(boolean angry) {
        this.angry = angry;
    }

    public boolean isAngry() {
        return angry;
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0) loot.add(new ItemStack(ItemID.GRILLED_PORK, amount));
        return loot;
    }
}
