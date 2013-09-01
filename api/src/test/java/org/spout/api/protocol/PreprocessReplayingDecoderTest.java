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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import org.spout.api.protocol.fake.ChannelHandlerContextFaker;
import org.spout.api.protocol.fake.FakeChannelHandlerContext;

import static org.junit.Assert.assertTrue;

public class PreprocessReplayingDecoderTest {
	private final int LENGTH = 65536;
	private final int BREAK = 17652;

	@Test
	public void test() throws Exception {

		Preprocessor p = new Preprocessor(512, BREAK, LENGTH);


		FakeChannelHandlerContext fake = ChannelHandlerContextFaker.setup();
		fake.setList(new LinkedList<byte[]>());

		Random r = new Random();

		byte[] input = new byte[LENGTH];
		r.nextBytes(input);

		int i = 0;
		while (i < input.length) {
			int burstSize = r.nextInt(512);
			if (r.nextInt(10) == 0) {
				burstSize *= 10;
			}

			if (i + burstSize > input.length) {
				burstSize = input.length - i;
			}

			final ByteBuf buf = Unpooled.buffer(burstSize);
			buf.writeBytes(input, i, burstSize);
			i += burstSize;

			p.channelRead(fake, buf);
		}
		List<byte[]> outputList = fake.getList();

		byte[] output = new byte[LENGTH];

		i = 0;
		for (byte[] array : outputList) {
			for (int j = 0; j < array.length; j++) {
				output[i++] = array[j];
			}
		}

		for (i = 0; i < input.length; i++) {
			byte expected = i < BREAK ? input[i] : (byte) ~input[i];
			if (output[i] != expected) {
				for (int j = i - 10; j <= i + 10; j++) {
					System.out.println(j + ") " + Integer.toBinaryString(input[j] & 0xFF) + " " + Integer.toBinaryString(output[j] & 0xFF));
				}
			}

			if (i < BREAK) {
				assertTrue("Input/Output mismatch at position " + i, output[i] == input[i]);
			} else {
				assertTrue("Input/Output mismatch at position " + i + ", after the processor change", output[i] == (byte) ~input[i]);
			}
		}
	}

	private static class Preprocessor extends PreprocessReplayingDecoder {
		private final int breakPoint;
		private final int length;
		private int position = 0;
		private boolean breakOccured;
		private Random r = new Random();

		public Preprocessor(int capacity, int breakPoint, int length) {
			super(capacity);
			this.breakPoint = breakPoint;
			this.length = length;
		}

		@Override
		public Object decodeProcessed(ChannelHandlerContext ctx, Channel channel, ByteBuf buffer) throws Exception {

			int packetSize = r.nextInt(128) + 1;
			if (r.nextInt(10) == 0) {
				packetSize *= 20;
			}

			if (position + packetSize > breakPoint && !breakOccured) {
				packetSize = breakPoint - position;
			}
			if (position + packetSize > length) {
				packetSize = length - position;
			}

			if (packetSize == 0) {
				return null;
			}

			byte[] buf = new byte[packetSize];

			buffer.readBytes(buf);

			position += packetSize;

			if (position == breakPoint) {
				this.setProcessor(new NegatingProcessor(512));
				breakOccured = true;
			}

			return buf;
		}
	}

	private static class NegatingProcessor extends CommonChannelProcessor {
		byte[] buffer = new byte[65536];
		int readPointer = 0;
		int writePointer = 0;
		int mask = 0xFFFF;

		public NegatingProcessor(int capacity) {
			super(capacity);
		}

		@Override
		protected void write(byte[] buf, int length) {
			for (int i = 0; i < length; i++) {
				buffer[(writePointer++) & mask] = (byte) ~buf[i];
			}
		}

		@Override
		protected int read(byte[] buf) {
			int i;
			for (i = 0; i < buf.length && readPointer < writePointer; i++) {
				buf[i] = buffer[(readPointer++) & mask];
			}
			return i;
		}
	}
}
