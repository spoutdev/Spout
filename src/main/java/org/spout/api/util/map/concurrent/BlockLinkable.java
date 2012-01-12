package org.spout.api.util.map.concurrent;

import org.spout.api.geo.cuboid.Chunk;

/**
 * An interface for class which can be linked to a particular block
 */
public interface BlockLinkable {

	/**
	 * Links the object to a block.  The sequenceNumber should be stored as an AtomicInteger.<br>
	 * <br>
	 * The sequence number for a block should be read before and after any read.  If either sequence 
	 * number is DataTableSequenceNumber.UNSTABLE, then the read is unsafe.  Otherwise, if both 
	 * sequence numbers are the same, then the object can be considered read correctly.
	 * 
	 * @param chunk the chunk containing the block
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param sequenceNumber the sequence number
	 */
	public void linkToBlock(Chunk chunk, int x, int y, int z, int sequenceNumber);
	
}
