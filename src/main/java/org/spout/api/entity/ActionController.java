package org.spout.api.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ActionController extends Controller {


	private List<EntityAction<Controller>> activeActions = new ArrayList<EntityAction<Controller>>();

	@SuppressWarnings("unchecked")
	public void registerAction(EntityAction<?> ai) {
		activeActions.add((EntityAction<Controller>) ai);
	}

	public void unregisterAction(Class<? extends EntityAction<?>> type) {
		for (Iterator<EntityAction<Controller>> i = activeActions.iterator(); i.hasNext();) {
			if (type.isAssignableFrom(i.next().getClass())) {
				i.remove();
			}
		}
	}

	
	@Override
	public void onTick(float dt) {
		if (getParent() == null || getParent().getWorld() == null) {
			return;
		}
		for (EntityAction<Controller> ai : activeActions) {
			if (ai.shouldRun(getParent(), this)) {
				ai.run(getParent(), this, dt);
			}
		}
		
	}

}
