package org.getspout.server;

import java.util.UUID;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;

public class SpoutWorld implements World {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getAge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setTime(int time) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDayLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setDayLength(int time) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getBlock(Point point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID getUID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Region getRegion(int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int hashCode() {
		UUID uid = getUID();
		long hash = uid.getMostSignificantBits();
		hash += (hash << 5) + uid.getLeastSignificantBits();
		
		return (int)(hash ^ (hash >> 32));
	}
	
	public boolean equals(Object obj) {
		
		if (obj == null) {
			return false;
		} else if (!(obj instanceof SpoutWorld)) {
			return false;
		} else {
			SpoutWorld world = (SpoutWorld)obj;
			
			return world.getUID().equals(getUID());
		}
		
	}

}
