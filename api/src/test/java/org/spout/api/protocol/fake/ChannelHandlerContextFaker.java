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
package org.spout.api.protocol.fake;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

public class ChannelHandlerContextFaker {
	private static FakeChannelHandlerContext context = null;
	private static Channel channel = null;
	private static ChannelConfig config = null;
	private static ByteBufAllocator alloc = null;

	public static FakeChannelHandlerContext setup() {
		if (context == null) {
			context = Mockito.mock(FakeChannelHandlerContext.class, Mockito.CALLS_REAL_METHODS);
			channel = Mockito.mock(Channel.class);
			config = Mockito.mock(ChannelConfig.class);
			alloc = Mockito.mock(ByteBufAllocator.class);
			Mockito.doReturn(channel).when(context).channel();
			Mockito.when(channel.config()).thenReturn(config);
			Mockito.when(config.getAllocator()).thenReturn(alloc);
			Mockito.when(alloc.buffer(Mockito.anyInt())).thenAnswer(new Answer<ByteBuf>() {
				@Override
				public ByteBuf answer(InvocationOnMock invocation) throws Throwable {
					ByteBuf buffer = Unpooled.buffer();
					buffer.retain();
					return buffer;
				}
			});
		}
		return context;
	}
}
