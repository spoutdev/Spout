package org.spout.api.entity.component;

import org.spout.api.entity.Component;
import org.spout.api.entity.Entity;
import org.spout.api.tickable.BasicTickable;

public class TickableComponent extends BasicTickable implements Component {
	private Entity parent;

	@Override
	public void onTick(float dt) {

	}

	@Override
	public void attachToEntity(Entity parent) {
		this.parent = parent;
	}

	@Override
	public Entity getParent() {
		return parent;
	}

	@Override
	public void onAttached() {

	}

	@Override
	public void onDetached() {

	}
}
