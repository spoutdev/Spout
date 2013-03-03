/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.entity;

import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.Command;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.protocol.Message;
import org.spout.engine.protocol.SpoutSession;

/**
 * A subclass of SpoutPlayer with modifications for the client
 */
public class SpoutClientPlayer extends SpoutPlayer {

	public SpoutClientPlayer(Engine engine, String name, Transform transform, int viewDistance) {
		super(engine, name, transform, viewDistance);
	}

	@Override
	public boolean sendRawMessage(ChatArguments message) {
		SpoutSession<?> session;
		try {
			session = getSession();
		} catch (IllegalArgumentException iae) {
			getEngine().getConsoles().addMessage(message);
			return true;
		}
		if (session==null) {
			((Client)getEngine()).getScreenStack().getConsole().addMessage(message);
		} else {
			session.getEngine().getConsoles().addMessage(message);
		}
		return true;
	}

	@Override
	public void sendCommand(String commandName, ChatArguments arguments) {
		Command command = getEngine().getRootCommand().getChild(commandName);
		if (command == null) {
			sendMessage(ChatStyle.RED, "Unknown command: ", commandName);
			return;
		}
		Message cmdMessage = getSession().getProtocol().getCommandMessage(command, arguments);
		if (cmdMessage == null) {
			return;
		}

		getSession().send(true, cmdMessage);
	}

}
