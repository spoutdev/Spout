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
package org.spout.engine.protocol.builtin;

import org.spout.api.protocol.CodecLookupService;
import org.spout.engine.protocol.builtin.codec.BlockUpdateCodec;
import org.spout.engine.protocol.builtin.codec.ChunkDataCodec;
import org.spout.engine.protocol.builtin.codec.ChunkDatatableCodec;
import org.spout.engine.protocol.builtin.codec.ClickRequestCodec;
import org.spout.engine.protocol.builtin.codec.ClickResponseCodec;
import org.spout.engine.protocol.builtin.codec.CommandCodec;
import org.spout.engine.protocol.builtin.codec.CuboidBlockUpdateCodec;
import org.spout.engine.protocol.builtin.codec.EntityDatatableCodec;
import org.spout.engine.protocol.builtin.codec.LoginCodec;
import org.spout.engine.protocol.builtin.codec.SyncedMapCodec;
import org.spout.engine.protocol.builtin.codec.UpdateEntityCodec;
import org.spout.engine.protocol.builtin.codec.WorldChangeCodec;

/**
 * 
 */
public class SpoutCodecLookupService extends CodecLookupService {
	public SpoutCodecLookupService() {
		super(256);
		try {
			/* 0x00 */ bind(LoginCodec.class);
			/* 0x01 */ bind(SyncedMapCodec.class);
			/* 0x02 */ bind(WorldChangeCodec.class);
			/* 0x03 */ bind(CommandCodec.class);
			/* 0x04 */ bind(UpdateEntityCodec.class);
			/* 0x05 */ bind(EntityDatatableCodec.class);
			/* 0x06 */ bind(ChunkDataCodec.class);
			/* 0x07 */ bind(BlockUpdateCodec.class);
			/* 0x08 */ bind(CuboidBlockUpdateCodec.class);
			/* 0x09 */ bind(ClickRequestCodec.class);
			/* 0x0A */ bind(ClickResponseCodec.class);
			/* 0x0B */ bind(ChunkDatatableCodec.class);
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}
}
