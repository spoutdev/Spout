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
package org.spout.api.protocol.dynamicid;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import org.spout.api.protocol.CommonHandler;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.MessageCodec;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;

/**
 * Process dynamically registered messages before sending them to give extra usefulness
 */
public class DynamicMessageEncoder extends MessageToMessageEncoder<Message> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Message o, List<Object> out) throws Exception {
		Session session = ctx.pipeline().get(CommonHandler.class).getSession();
		if (session != null) {
			Protocol protocol = session.getProtocol();
			MessageCodec<?> codec = protocol.getCodecLookupService().find(o.getClass());
			if (codec != null && codec.isDynamic()) {
				o = protocol.getWrappedMessage(o);
			}
		}
		out.add(o);
	}
}
