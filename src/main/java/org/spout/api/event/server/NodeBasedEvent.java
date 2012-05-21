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
package org.spout.api.event.server;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.event.Event;

/**
 * This event is a parent event for any event that uses nodes.
 */
public abstract class NodeBasedEvent extends Event {
	private final String node;

	public NodeBasedEvent(String node) {
		this.node = node;
	}

	public String[] getNodes() {
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

		return nodes.toArray(new String[nodes.size()]);
	}

	public String getNode() {
		return node;
	}
}
