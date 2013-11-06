/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.api.protocol;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import org.spout.api.Client;
import org.spout.api.Engine;
import org.spout.api.Server;
import org.spout.api.Spout;

public abstract class MessageCodec<T extends Message> {
	private final Class<T> clazz;
	private boolean dynamic;
	private int toClientOpcode, toServerOpcode;

	public MessageCodec(Class<T> clazz, int opcode) {
		this(clazz, opcode, opcode);
	}

	public MessageCodec(Class<T> clazz, int toServerOpcode, int toClientOpcode) {
		this.clazz = clazz;
		this.toClientOpcode = toClientOpcode;
		this.toServerOpcode = toServerOpcode;
	}

	public final Class<T> getType() {
		return clazz;
	}

	public final int getToServerOpcode() {
		return toServerOpcode;
	}

	void setToServerOpcode(int opcode) {
		this.toServerOpcode = opcode;
	}

	public final int getToClientOpcode() {
		return toClientOpcode;
	}

	void setToClientOpcode(int opcode) {
		this.toClientOpcode = opcode;
	}

	public int getOutgoingOpcode() {
		Engine e = Spout.getEngine();
		if (e instanceof Server) {
			return getToClientOpcode();
		} else if (e instanceof Client) {
			return getToServerOpcode();
		} else {
			return -1;
		}	}

	public int getIncomingOpcode() {
		Engine e = Spout.getEngine();
		if (e instanceof Server) {
			return getToServerOpcode();
		} else if (e instanceof Client) {
			return getToClientOpcode();
		} else {
			return -1;
		}
	}

	@Deprecated
	public int getOpcode() {
		return getOutgoingOpcode();
	}

	public boolean isDynamic() {
		return dynamic;
	}

	void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public final ByteBuf encode(boolean onClient, T message) throws IOException {
		return onClient ? encodeToServer(message) : encodeToClient(message);
	}

	public ByteBuf encode(T message) throws IOException {
		return null;
	}

	public ByteBuf encodeToClient(T message) throws IOException {
		return encode(message);
	}

	public ByteBuf encodeToServer(T message) throws IOException {
		return encode(message);
	}

	public final T decode(boolean onClient, ByteBuf buffer) throws IOException {
		return onClient ? decodeFromServer(buffer) : decodeFromClient(buffer);
	}

	public T decode(ByteBuf buffer) throws IOException {
		return null;
	}

	public T decodeFromClient(ByteBuf buffer) throws IOException {
		return decode(buffer);
	}

	public T decodeFromServer(ByteBuf buffer) throws IOException {
		return decode(buffer);
	}
}
