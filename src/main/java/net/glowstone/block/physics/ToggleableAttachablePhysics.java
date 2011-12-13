package net.glowstone.block.physics;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.data.ToggleableAttachable;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;

public class ToggleableAttachablePhysics extends AttachablePhysics {
    protected final ToggleableAttachable data;

    public ToggleableAttachablePhysics(ToggleableAttachable data) {
        super(data);
        this.data = data;
    }

    public boolean interact(GlowPlayer player, GlowBlock block, boolean rightClick, BlockFace against) {
        block.setData((byte)data.toggleOpen(block.getData()));
        return false;
    }
}
