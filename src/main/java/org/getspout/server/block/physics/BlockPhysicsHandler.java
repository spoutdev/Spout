package org.getspout.server.block.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.getspout.server.block.SpoutBlock;
import org.getspout.server.block.SpoutBlockState;
import org.getspout.server.entity.SpoutPlayer;

public interface BlockPhysicsHandler {
    public boolean canPlaceAt(SpoutBlock block, BlockFace against);

    /**
     * Perform physics on a block from a direct update.
     * @param block The block that changed.
     * @return Whether this physics update changed anything.
     */
    public boolean doPhysics(SpoutBlock block);

    /**
     * Perform physics on this block after a change from a neighboring block.
     * @param block The block that changed
     * @param against The neighbor that triggered this update.
     * @return Whether this physics update changed anything.
     */
    public boolean postUpdateNeighbor(SpoutBlock block, BlockFace against);

    /**
     * Returns the metadata that should be returned
     *
     * @param placer The {@link SpoutPlayer} who is placing this block
     * @param current The metadata of the item stack that was used to place this or 0
     * @param against The block face this was placed against or SELF if 
     * @return The metadata that should be placed.
     */
    public int getPlacedMetadata(SpoutPlayer placer, int current, BlockFace against);

    /**
     * Performs a special action when placing the block.
     * @param block The block where a normal placement would occur.
     * @param data The type and data of the player's current item in hand.
     * @param against The blockface that this placement action occured against
     * @return Whether to place the block normally or just subtract an item.
     */
    public SpoutBlockState placeAgainst(SpoutPlayer player, SpoutBlockState block, MaterialData data, BlockFace against);


    /**
     * Performs an action on block interaction.
     * @param player The player who interacted with the block.
     * @param block The block this player interacted with.
     * @param rightClick Whether this interaction was a right click.
     * @param against The clicked face.
     * @return Whether the normal block actions can continue normally.
     */
    public boolean interact(SpoutPlayer player, SpoutBlock block, boolean rightClick, BlockFace against);
}
