package org.getspout.server.msg;

public final class SpawnVehicleMessage extends Message {
	private final int id, type, x, y, z, fireballId, fireballX, fireballY,
			fireballZ;

	public SpawnVehicleMessage(int id, int type, int x, int y, int z) {
		this(id, type, x, y, z, 0, 0, 0, 0);
	}

	public SpawnVehicleMessage(int id, int type, int x, int y, int z, int fbId, int fbX, int fbY, int fbZ) {
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		fireballId = fbId;
		fireballX = fbX;
		fireballY = fbY;
		fireballZ = fbZ;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public boolean hasFireball() {
		return fireballId != 0;
	}

	public int getFireballId() {
		return fireballId;
	}

	public int getFireballX() {
		return fireballX;
	}

	public int getFireballY() {
		return fireballY;
	}

	public int getFireballZ() {
		return fireballZ;
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder("SpawnVehicleMessage{id=");
		build.append(id).append(",type=").append(type).append(",x=").append(x).append(",y=").append(y);
		build.append(",z=").append(z).append(",fireballId=").append(fireballId);
		if (hasFireball()) {
			build.append(",fireballX=").append(fireballX).append(",fireballY=").append(fireballY).append(",fireballZ=").append(fireballZ);
		}
		build.append("}");
		return build.toString();
	}
}
