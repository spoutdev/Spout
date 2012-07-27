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
package org.spout.api.security;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.spout.api.protocol.CommonChannelProcessor;

public class EncryptionChannelProcessor extends CommonChannelProcessor {
	
	private final BufferedBlockCipher cipher;
	private final byte[] processed;
	private int stored = 0;
	private int position = 0;
	
	public EncryptionChannelProcessor(BufferedBlockCipher cipher, int capacity) {
		super(capacity);
		this.cipher = cipher;
		processed = new byte[capacity * 2];
	}

	@Override
	protected void write(byte[] buf, int length) {
		if (stored > position) {
			throw new IllegalStateException("Stored data must be completely read before writing more data");
		}
		stored = cipher.processBytes(buf, 0, length, processed, 0);
		position = 0;
	}

	@Override
	protected int read(byte[] buf) {
		if (position >= stored) {
			return 0;
		} else {
			int toRead = Math.min(buf.length, stored - position);
			for (int i = 0; i < toRead; i++) {
				buf[i] = processed[position + i];
			}
			position += toRead;
			return toRead;
		}
	}

	
	
}
