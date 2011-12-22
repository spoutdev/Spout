package org.getspout.unchecked.server.block.physics;

import org.getspout.unchecked.server.block.data.Door;

public class DoorPhysics extends ToggleableAttachablePhysics {
	protected final Door data;

	public DoorPhysics(int id) {
		super(new Door(id));
		data = (Door) super.data;
	}
}
