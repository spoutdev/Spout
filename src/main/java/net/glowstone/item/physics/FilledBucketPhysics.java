package net.glowstone.item.physics;

import net.glowstone.block.BlockID;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowItemStack;
import net.glowstone.item.ItemID;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

public class FilledBucketPhysics extends DefaultItemPhysics {

    private final int placedFluidId;
    private final boolean fillCauldron;

    public FilledBucketPhysics(int itemId, int placedFluidId, boolean fillCauldron) {
        super(itemId);
        this.placedFluidId = placedFluidId;
        this.fillCauldron = fillCauldron;
    }

    @Override
    public boolean interact(GlowPlayer player, GlowBlock block, GlowItemStack heldItem, Action action, BlockFace against) {
        if (action != Action.RIGHT_CLICK_BLOCK) return true;
        GlowBlock target = block.getRelative(against);
        if (fillCauldron && block.getTypeId() == BlockID.CAULDRON) {
            if (block.getData() < 3) {
                block.setData((byte) 3);
                heldItem.setTypeId(ItemID.BUCKET);
            }
            return false;
        } else if (target.isEmpty()) {
            target.setTypeId(placedFluidId);
            heldItem.setTypeId(ItemID.BUCKET);
            return false;
        }
        return super.interact(player, block, heldItem, action, against);
    }
}
