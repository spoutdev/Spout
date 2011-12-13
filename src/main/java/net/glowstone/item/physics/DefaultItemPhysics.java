package net.glowstone.item.physics;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowItemStack;
import net.glowstone.item.ItemProperties;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;

public class DefaultItemPhysics implements ItemPhysics {
    private final int id;

    public DefaultItemPhysics(int itemId) {
        this.id = itemId;
    }

    public MaterialData getPlacedBlock(BlockFace against, int data) {
        MaterialData type = ItemProperties.get(id).getPlacedBlock();
        if (type == null) type = NO_PLACE;
        return type;
    }

    public boolean interact(GlowPlayer interactingPlayer, GlowBlock clicked, GlowItemStack heldItem, Action type, BlockFace against) {
        return true;
    }
}
