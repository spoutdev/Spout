package net.glowstone.item.physics;

import net.glowstone.block.BlockID;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowItemStack;
import net.glowstone.item.ItemID;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;


public class GlassBottlePhysics extends DefaultItemPhysics {

    public GlassBottlePhysics() {
        super(ItemID.GLASS_BOTTLE);
    }

    @Override
    public boolean interact(GlowPlayer player, GlowBlock block, GlowItemStack heldItem, Action action, BlockFace against) {
        if (action != Action.RIGHT_CLICK_BLOCK) return true;
        if (block.getTypeId() == BlockID.CAULDRON && block.getData() > 0) {
            block.setData((byte)(block.getData()-1));
            heldItem.setTypeId(ItemID.POTION);
            return false;
        }
        return super.interact(player, block, heldItem, action, against);
    }
}
