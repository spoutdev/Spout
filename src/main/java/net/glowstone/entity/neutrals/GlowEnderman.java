package net.glowstone.entity.neutrals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.BlockID;
import net.glowstone.block.ItemID;
import net.glowstone.entity.Damager;
import net.glowstone.entity.monsters.GlowMonster;
import net.glowstone.util.Parameter;
import org.bukkit.entity.Enderman;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class GlowEnderman extends GlowMonster implements Enderman {

    /**
     * The material this enderman is currently carrying.
     */
    private MaterialData carriedMaterial;

    public GlowEnderman(GlowServer server, GlowWorld world) {
        super(server, world, 58);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        List<ItemStack> loot = new ArrayList<ItemStack>();
        int amount = random.nextInt(3);
        if (amount > 0) loot.add(new ItemStack(ItemID.ENDER_PEARL, amount));
        return loot;
    }

    public MaterialData getCarriedMaterial() {
        return carriedMaterial;
    }

    public void setCarriedMaterial(MaterialData material) {
        this.carriedMaterial = material;
        if (carriedMaterial == null) {
            carriedMaterial = new MaterialData(BlockID.AIR);
        }
        setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)carriedMaterial.getItemTypeId()));
        setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 17, carriedMaterial.getData()));
    }
}