/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * The SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.event.server.permissions;

import java.util.ArrayList;
import java.util.List;
import org.getspout.api.event.Event;
import org.getspout.api.event.HandlerList;
import org.getspout.api.event.Result;
import org.getspout.api.geo.World;
import org.getspout.api.permissions.PermissionsSubject;

/**
 * This event is called when PermissionSubject.hasPermission() is called.
 */
public class PermissionNodeEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private World world;
	private PermissionsSubject subject;
	private String node;
	private Result result = Result.DENY;
	
	public PermissionNodeEvent(World world, PermissionsSubject subject, String node) {
		this.world = world;
		this.subject = subject;
		this.node = node;
	}
	
	public String[] getNodes(boolean wildcard) {
		if (wildcard) {
			List<String> nodes = new ArrayList<String>();
			nodes.add(node);
			//Checks all the parent nodes of this node
			//If this method is called with node equal
			//to this.is.a.perm.node, it will check the
			//nodes this.is.a.*, this.is.*, this.*, and *
			String[] split = node.split("\\.");
			for (int i = split.length - 1; i >= 0; --i) {
				
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < i; j++) {
					sb.append(split[j]);
					sb.append(".");
				}
				sb.append("*");
				
				nodes.add(sb.toString());
			}
			
			return nodes.toArray(new String[0]);
		} else {
			return new String[]{node};
		}
	}
	
	public String[] getNodes() {
		return getNodes(true);
	}
	
	public void setNode(String node) {
		this.node = node;
	}
	
	public PermissionsSubject getSubject() {
		return subject;
	}
	
	public void setSubject(PermissionsSubject subject) {
		this.subject = subject;
	}
	
	public Result getResult() {
		return result;
	}
	
	public void setResult(Result result) {
		this.result = result;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
