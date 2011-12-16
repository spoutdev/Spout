package org.getspout.server.block.data;

import org.bukkit.block.BlockFace;

public abstract class Attachable extends BlockData {
	private final AttachmentType type;

	public Attachable(int id, PlaceRequirement requirement, AttachmentType type) {
		super(id, requirement);
		this.type = type;
	}

	/**
	 * Returns the BlockFace the applicable type is attached to.
	 * @param existing The block's current metadata
	 * @return The face this block is attached to.
	 */
	public abstract BlockFace getAttachedFace(int existing);

	/**
	 * Returns the metadata that a block should be set to to get the target attachment.
	 * @param existing The block's existing metadata.
	 * @param target The target attached face for the block.
	 * @return The metadata that should be set.
	 */
	public abstract int setAttachedFace(int existing, BlockFace target);

	public AttachmentType getAttachmentType() {
		return type;
	}
}
