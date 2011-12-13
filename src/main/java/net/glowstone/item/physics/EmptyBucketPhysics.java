package net.glowstone.item.physics;

import gnu.trove.map.hash.TIntIntHashMap;
import net.glowstone.block.BlockID;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowItemStack;
import net.glowstone.item.ItemID;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

public class EmptyBucketPhysics extends DefaultItemPhysics {
    private final TIntIntHashMap mappings;
    public EmptyBucketPhysics(int[] source, int[] result) {
        super(ItemID.BUCKET);
        if (source.length != result.length) throw new IllegalArgumentException("Mismatched argument lengths!");
        mappings = new TIntIntHashMap(source, result);
    }

    @Override
    public boolean interact(GlowPlayer player, GlowBlock block, GlowItemStack heldItem, Action action, BlockFace against) {
        if (action != Action.RIGHT_CLICK_BLOCK) return true;
        GlowBlock target = block.getRelative(against);
        int mapping = mappings.get(target.getTypeId());
        if (mapping != mappings.getNoEntryValue()) {
            heldItem.setTypeId(mapping);
            target.setTypeId(BlockID.AIR);
            return false;
        }
        return super.interact(player, block, heldItem, action, against);
    }
}
