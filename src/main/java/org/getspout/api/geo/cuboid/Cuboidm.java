package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.point.Point;
import org.getspout.api.geo.point.Pointm;
import org.getspout.api.math.Vector3;
import org.getspout.api.math.Vector3m;

public class Cuboidm extends Cuboid {
	
	public Cuboidm(Point base, Vector3 size) {
		super(base, size);
	}
	
	public void setBase(Point base) {
		this.base = new Pointm(base);
	}
	
	public void setSize(Vector3 size) {
		this.size = new Vector3m(size);
	}
	
	public void setX(int x) {
		base.setX(x * size.getX());
	}

	public void setY(int y) {
		base.setY(y * size.getY());
	}
	
	public void setZ(int z) {
		base.setZ(z * size.getZ());
	}
}
