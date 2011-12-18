package org.getspout.api.util.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.math.Vector3;

/**
 * This class implements a Cuboid common methods for a one dimensional array Cuboid Buffer
 * 
 * Elements are stored in column order and each column is +1 on the Z dimension relative to the previous one.  
 * 
 * Each YZ plane is followed by the plane corresponding to +1 on the X dimension.
 * 
 * It is assumed that the Cuboid has dimensions (SX, SY, SZ) and the base is set at the origin.
 * 
 * buffer[0]           = data(0,    0,    0   )
 * buffer[1]           = data(0,    1,    0   )
 * .....
 * buffer[SY-1]        = data(0,    SY-1, 0   )
 * buffer[SY]          = data(0,    0     1   )
 * ....
 * buffer[SZ*SY - 1]   = data(0,    SY-1, SZ-1)
 * buffer[SZ*SY]       = data(1,    0,    0   )
 * ....
 * buffer[SZ*SY*SX -1] = data(SX-1, SY-1, SZ-1)
 * 
 * TODO is this the best package to put this?
 */
public abstract class CuboidBuffer {
	
	private final World world; // TODO - should this be included?
	
	private final int sizeX;
	private final int sizeY;
	private final int sizeZ;
	
	private final int baseX;
	private final int baseY;
	private final int baseZ;
	
	protected final int Xinc;
	protected final int Yinc;
	protected final int Zinc;
	
	protected CuboidBuffer(World world, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this.world = world;
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		
		this.baseX = baseX;
		this.baseY = baseY;
		this.baseZ = baseZ;
		
		this.Xinc = sizeY * sizeZ;
		this.Yinc = 1;
		this.Zinc = sizeY;
	}
	
	protected CuboidBuffer(World world, double baseX, double baseY, double baseZ, double sizeX, double sizeY, double sizeZ) {
		this(    world,
			(int)baseX,
			(int)baseY,
			(int)baseZ,
			(int)sizeX,
			(int)sizeY,
			(int)sizeZ);
	}

	
	protected CuboidBuffer(Point base, Vector3 size) {
		this(base.getWorld(),
			 base.getX(),
			 base.getY(),
			 base.getZ(),
			 size.getX(),
			 size.getY(),
			 size.getZ());
	}
	
	/**
	 * Gets a Point representing the base of this CuboidBuffer
	 */
	public Point getBase() {
		return new Point(world, baseX, baseY, baseZ);
	}
	
	/**
	 * Gets a World the CuboidBuffer is located in
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Gets the size of the CuboidBuffer
	 */
	public Vector3 getSize() {
		return new Vector3(sizeX, sizeY, sizeZ);
	}
	
	protected CuboidBufferCopyRun getCopyRun(CuboidBuffer other) {
		return new CuboidBufferCopyRun(this, other);
	}
	
	protected static class CuboidBufferCopyRun {
		
		private int overlapBaseX;
		private int overlapBaseY;
		private int overlapBaseZ;
		
		private int overlapSizeX;
		private int overlapSizeY;
		private int overlapSizeZ;
		
		private int sourceIndex;
		private int targetIndex;
		
		public CuboidBufferCopyRun(CuboidBuffer source, CuboidBuffer target) {
			overlapBaseX = Math.max(source.baseX, target.baseX);
			overlapBaseY = Math.max(source.baseY, target.baseY);
			overlapBaseZ = Math.max(source.baseZ, target.baseZ);
			
			overlapSizeX = Math.min(source.sizeX + source.baseX, target.sizeX + target.baseX) - overlapBaseX;
			overlapSizeY = Math.min(source.sizeY + source.baseY, target.sizeY + target.baseY) - overlapBaseY;
			overlapSizeZ = Math.min(source.sizeZ + source.baseZ, target.sizeZ + target.baseZ) - overlapBaseZ;
			
			if (overlapSizeX < 0 || overlapSizeY < 0 || overlapSizeZ < 0) {
				sourceIndex = -1;
				targetIndex = -1;
				return;
			}
			
			sourceIndex =  (overlapBaseX - source.baseX) * source.Xinc;
			sourceIndex += (overlapBaseY - source.baseY) * source.Yinc;
			sourceIndex += (overlapBaseZ - source.baseZ) * source.Zinc;

			targetIndex =  (overlapBaseX - target.baseX) * target.Xinc;
			targetIndex += (overlapBaseY - target.baseY) * target.Yinc;
			targetIndex += (overlapBaseZ - target.baseZ) * target.Zinc;
		}
		
		public int getBaseSource() {
			return sourceIndex;
		}
		
		public int getBaseTarget() {
			return targetIndex;
		}
		
		public int getLength() {
			return overlapSizeY;
		}
		
		public int getInnerRepeats() {
			return overlapSizeZ;
		}

		public int getOuterRepeats() {
			return overlapSizeX;
		}
}
}
