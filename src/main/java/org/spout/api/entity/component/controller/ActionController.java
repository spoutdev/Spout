/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.entity.component.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spout.api.entity.component.Controller;
import org.spout.api.entity.component.controller.action.EntityAction;
import org.spout.api.entity.component.controller.type.ControllerType;

public abstract class ActionController extends Controller {
	private List<EntityAction<Controller>> activeActions = new ArrayList<EntityAction<Controller>>();

	protected ActionController(ControllerType type) {
		super(type);
	}

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
		for (EntityAction<Controller> ai : activeActions) {
			if (ai.shouldRun(getParent(), this)) {
				ai.run(getParent(), this, dt);
			}
		}
	}
}
