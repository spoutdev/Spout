/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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

package org.getspout.api.event.server;

import org.getspout.api.command.CommandSource;
import org.getspout.api.event.Cancellable;
import org.getspout.api.event.Event;
import org.getspout.api.event.HandlerList;

/**
 * This event is called before a command goes through the full command handling process.
 * This can be used for command blocking, and really shouldn't be useful anywhere else,
 * since we support dynamic command registration.
 */
public class PreCommandEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private String message;
	private CommandSource source;

	public PreCommandEvent(CommandSource source, String message) {
		this.source = source;
		this.message = message;
	}

	public CommandSource getCommandSource() {
		return source;
	}
	
	public String getCommand() {
		return message.substring(0, message.indexOf(" "));
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
