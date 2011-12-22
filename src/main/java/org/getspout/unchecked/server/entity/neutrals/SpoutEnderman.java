package org.getspout.unchecked.server.entity.neutrals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Enderman;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;
import org.getspout.unchecked.server.block.BlockID;
import org.getspout.unchecked.server.entity.Damager;
import org.getspout.unchecked.server.entity.monsters.SpoutMonster;
import org.getspout.unchecked.server.item.ItemID;
import org.getspout.unchecked.server.util.Parameter;

public class SpoutEnderman extends SpoutMonster implements Enderman {
	/**
	 * The material this enderman is currently carrying.
	 */
	private MaterialData carriedMaterial;

	/**
	 * Creates a new enderman.
	 *
	 * @param server This server this enderman is on.
	 * @param world The world this enderman is in.
	 */
	public SpoutEnderman(SpoutServer server, SpoutWorld world) {
		super(server, world, 58);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		int amount = random.nextInt(3);
		if (amount > 0) {
			loot.add(new ItemStack(ItemID.ENDER_PEARL, amount));
		}
		return loot;
	}

	@Override
	public MaterialData getCarriedMaterial() {
		return carriedMaterial;
	}

	@Override
	public void setCarriedMaterial(MaterialData material) {
		carriedMaterial = material;
		if (carriedMaterial == null) {
			carriedMaterial = new MaterialData(BlockID.AIR);
		}
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte) carriedMaterial.getItemTypeId()));
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 17, carriedMaterial.getData()));
	}
}
