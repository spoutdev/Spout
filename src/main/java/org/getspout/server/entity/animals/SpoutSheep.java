package org.getspout.server.entity.animals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.block.BlockID;
import org.getspout.server.entity.Damager;
import org.getspout.server.util.Parameter;

public class SpoutSheep extends SpoutAnimals implements Sheep {
	/**
	 * Whether this sheep is sheared
	 */
	private boolean sheared;

	/**
	 * The wool color this sheep has
	 */
	private DyeColor dyeColor = DyeColor.WHITE;

	/**
	 * Creates a new monster.
	 *
	 * @param world The world this monster is in.
	 * @param type  The type of monster.
	 */
	public SpoutSheep(SpoutServer server, SpoutWorld world) {
		super(server, world, 91);
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		List<ItemStack> loot = new ArrayList<ItemStack>();
		if (!isSheared()) {
			loot.add(new ItemStack(BlockID.WOOL, 1, dyeColor.getData()));
		}
		return loot;
	}

	public boolean isSheared() {
		return sheared;
	}

	public void setSheared(boolean flag) {
		this.sheared = flag;
		byte meta = (byte)(flag ? 1 : 0);
		byte existingMeta = getMetadata(16) == null ? 0 : (Byte)getMetadata(16).getValue();
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)(existingMeta & 0x0F |  meta & 0x10)));
	}

	public DyeColor getColor() {
		return dyeColor;
	}

	public void setColor(DyeColor color) {
		this.dyeColor = color;
		byte existingMeta = getMetadata(16) == null ? 0 : (Byte)getMetadata(16).getValue();
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 16, (byte)(existingMeta & 0x10 |  color.getData() >> 4)));
	}
}
