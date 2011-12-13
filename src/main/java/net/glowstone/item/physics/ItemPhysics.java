package net.glowstone.item.physics;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowItemStack;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;

public interface ItemPhysics {
    public static MaterialData NO_PLACE = new MaterialData(-1);

    /**
     * Returns the block ID that should be placed for this block
     * @param data The data of the item being placed
     * @return -1 to not place anything
     */
    public MaterialData getPlacedBlock(BlockFace against, int data);

    /**
     * Perform an action when interacting while having an item in hand.
     * @param interactingPlayer The player interacting with this item
     * @param clicked The block being clicked in this action. May be null.
     * @param type The type of this interaction
     * @return Whether the normal interaction should happen.
     */
    public boolean interact(GlowPlayer interactingPlayer, GlowBlock clicked, GlowItemStack heldItem, Action type, BlockFace against);
}
