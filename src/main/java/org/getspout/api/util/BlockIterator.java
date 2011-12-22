package org.getspout.api.util;

import java.util.Iterator;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.discrete.EntityTransform;
import org.getspout.api.geo.discrete.Pointm;
import org.getspout.api.math.MathHelper;
import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector3m;

/**
 * This class performs ray tracing and iterates along blocks on a line
 */
public class BlockIterator implements Iterator<Block> {
	// TODO -- need to actually code this :)
	
	private final Pointm position;
	private final Vector3m direction;
	private final Block[] blockBuffer = new Block[3];
	private int bufferSize = 0;
	private int blocksRead;
	private int maxDistance;
	private boolean done = false;
	
    /**
     * Constructs the BlockIterator
     *
     * @param world The world to use for tracing
     * @param eye the eyeline to trace
     * @param yOffset The trace begins vertically offset from the start vector by this value
     * @param maxDistance This is the maximum distance in blocks for the trace.  Setting this value above 140 may lead to problems with unloaded chunks.  A value of 0 indicates no limit
     *
     */
    public BlockIterator(World world, EntityTransform pos, int maxDistance) {
    	position = new Pointm(pos.getPosition());
    	direction = new Vector3m(MathHelper.getDirectionVector(pos.getRotation()));
    	
    	float max = Math.abs(direction.getX());
    	max = (Math.abs(direction.getY()) > max) ? Math.abs(direction.getY()) : max;
    	max = (Math.abs(direction.getZ()) > max) ? Math.abs(direction.getY()) : max;
    	
    	if (max == 0) {
    		throw new IllegalArgumentException("Direction may not be a zero vector");
    	}
    	
    	direction.scale(1/max);
    	
    	blocksRead = 0;
    	this.maxDistance = maxDistance;
    }


	public boolean hasNext() {
		return !done && blocksRead < maxDistance;
	}


	public Block next() {
		if (done) {
			throw new IllegalStateException("Iterator has already completed");
		}
		if (bufferSize == 0) {
			//updateBuffer();
		}
		Block block = blockBuffer[--bufferSize];
		if (block == null) {
			done = true;
		}
		blocksRead++;
		return block;
	}
	
	public void remove() {
		throw new UnsupportedOperationException("Block removal is not supported by this iterator");		
	}
}
