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

import java.io.Serializable;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.component.BasicEntityComponent;
import org.spout.api.entity.component.Controller;
import org.spout.api.entity.component.controller.type.ControllerType;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.map.DefaultedMap;

public abstract class BasicController extends BasicEntityComponent implements Controller {

	private final ControllerType type;
	private final DatatableMap datatableMap = new GenericDatatableMap();
	private final DataMap dataMap = new DataMap(datatableMap);

	protected BasicController(ControllerType type) {
		this.type = type;
	}

	@Override
	public void onDeath() {
	}

	@Override
	public void onSync() {
	}


	@Override
	public void preSnapshot() {
	}

	@Override
	public void finalizeTick() {
	}

	@Override
	public void onCollide(Entity other) {

	}

	@Override
	public void onCollide(Block other) {

	}

	@Override
	public void onInteract(Entity entity, Action type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ControllerType getType() {
		return type;
	}

	@Override
	public DefaultedMap<String, Serializable> data() {
		return dataMap;
	}

	@Override
	public void onSave() {
		
	}
	
	/**
	 * Called when this controller is attached to an entity.
	 * <br/><br/>
	 * If this controller was serialized and deserialized, any serializable
	 * information stored in {@link #data()} will be available.
	 */
	@Override
	public abstract void onAttached();

	@Override
	public boolean isSavable() {
		return true;
	}

	@Override
	public boolean isImportant() {
		return false;
	}
}