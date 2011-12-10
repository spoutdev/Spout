package net.glowstone.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.ItemID;
import net.glowstone.entity.Damager;
import net.glowstone.util.Parameter;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GlowSlime extends GlowMonster implements Slime {
    private int size;
    private int[] sizes = new int[] {0, 1, 2, 4};

    public GlowSlime(GlowServer server, GlowWorld world) {
        super(server, world, 55);
        setSize(sizes[new Random().nextInt(sizes.length)]);
    }

    protected GlowSlime(GlowServer server, GlowWorld world, int id) {
        super(server, world, id);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int sz) {
        size = sz;
        setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)size));
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0 && getSize() == 1) loot.add(new ItemStack(ItemID.SLIME_BALL, amount));
        return loot;
    }
}
