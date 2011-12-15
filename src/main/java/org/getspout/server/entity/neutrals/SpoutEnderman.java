package org.getspout.server.entity.neutrals;

import org.bukkit.entity.Enderman;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.block.BlockID;
import org.getspout.server.entity.Damager;
import org.getspout.server.entity.monsters.SpoutMonster;
import org.getspout.server.item.ItemID;
import org.getspout.server.util.Parameter;

import java.util.ArrayList;
import java.util.List;

public class SpoutEnderman extends SpoutMonster implements Enderman {

    /**
     * The material this enderman is currently carrying.
     */
    private MaterialData carriedMaterial;

    public SpoutEnderman(SpoutServer server, SpoutWorld world) {
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