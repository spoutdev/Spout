package org.getspout.server.item.physics;

import gnu.trove.map.hash.TIntIntHashMap;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.getspout.server.block.BlockID;
import org.getspout.server.block.SpoutBlock;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.inventory.SpoutItemStack;
import org.getspout.server.item.ItemID;

public class EmptyBucketPhysics extends DefaultItemPhysics {
    private final TIntIntHashMap mappings;
    public EmptyBucketPhysics(int[] source, int[] result) {
        super(ItemID.BUCKET);
        if (source.length != result.length) throw new IllegalArgumentException("Mismatched argument lengths!");
        mappings = new TIntIntHashMap(source, result);
    }

    @Override
    public boolean interact(SpoutPlayer player, SpoutBlock block, SpoutItemStack heldItem, Action action, BlockFace against) {
        if (action != Action.RIGHT_CLICK_BLOCK) return true;
        SpoutBlock target = block.getRelative(against);
        int mapping = mappings.get(target.getTypeId());
        if (mapping != mappings.getNoEntryValue()) {
            heldItem.setTypeId(mapping);
            target.setTypeId(BlockID.AIR);
            return false;
        }
        return super.interact(player, block, heldItem, action, against);
    }
}
