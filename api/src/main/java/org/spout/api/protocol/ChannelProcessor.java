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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface ChannelProcessor {
	/**
	 * Adds the data contained in the given channel buffer to the processor and returns the output channel buffer. The method may be called from multiple threads.
	 * {@code input.release} should NOT be called; it is done externally.
	 *
	 * @param ctx the channel handler context
	 * @param input the buffer containing the input data
	 */
	public ByteBuf write(ChannelHandlerContext ctx, ByteBuf input);

	/**
	 * Adds the data contained in the given channel buffer to the processor and returns the output channel buffer. The method may be called from multiple threads.
	 * {@code input.release} should NOT be called; it is done externally.
	 * {@code buffer.release} should NOT be called; it is done externally.
	 *
	 * @param ctx the channel handler context
	 * @param input the buffer containing the input data
	 * @param buffer the buffer to add the data to
	 */
	public ByteBuf write(ChannelHandlerContext ctx, ByteBuf input, ByteBuf buffer);
}
