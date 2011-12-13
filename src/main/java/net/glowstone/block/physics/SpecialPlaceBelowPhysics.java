package net.glowstone.block.physics;

import gnu.trove.set.hash.TIntHashSet;
import net.glowstone.block.GlowBlock;
import org.bukkit.block.BlockFace;

public class SpecialPlaceBelowPhysics extends DefaultBlockPhysics {
    private final int type;
    private final TIntHashSet allowedGround;
    private final boolean whitelist;
    public SpecialPlaceBelowPhysics(int type, boolean whitelist, int ... belowTypes) {
        this.type = type;
        this.allowedGround = new TIntHashSet(belowTypes);
        this.whitelist = whitelist;
    }

    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        int below = block.getWorld().getBlockTypeIdAt(block.getX(), block.getY() - 1, block.getZ());
        return whitelist ? allowedGround.contains(below) : !allowedGround.contains(below);
    }
}
