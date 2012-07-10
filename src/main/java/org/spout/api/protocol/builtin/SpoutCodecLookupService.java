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
package org.spout.api.protocol.builtin;

import org.spout.api.protocol.CodecLookupService;
import org.spout.api.protocol.builtin.codec.AddEntityCodec;
import org.spout.api.protocol.builtin.codec.BlockUpdateCodec;
import org.spout.api.protocol.builtin.codec.ChunkDataCodec;
import org.spout.api.protocol.builtin.codec.ClickCodec;
import org.spout.api.protocol.builtin.codec.CommandCodec;
import org.spout.api.protocol.builtin.codec.CuboidBlockUpdateCodec;
import org.spout.api.protocol.builtin.codec.EntityDatatableCodec;
import org.spout.api.protocol.builtin.codec.EntityPositionCodec;
import org.spout.api.protocol.builtin.codec.LoginCodec;
import org.spout.api.protocol.builtin.codec.PlayerInputCodec;
import org.spout.api.protocol.builtin.codec.RemoveEntityCodec;
import org.spout.api.protocol.builtin.codec.StringMapCodec;
import org.spout.api.protocol.builtin.codec.WorldChangeCodec;

/**
 * 
 */
public class SpoutCodecLookupService extends CodecLookupService {
	public SpoutCodecLookupService() {
		try {
			/* 0x00 */ bind(LoginCodec.class);
			/* 0x01 */ bind(StringMapCodec.class);
			/* 0x02 */ bind(WorldChangeCodec.class);
			/* 0x03 */ bind(CommandCodec.class);
			/* 0x04 */ bind(AddEntityCodec.class);
			/* 0x05 */ bind(RemoveEntityCodec.class);
			/* 0x06 */ bind(EntityDatatableCodec.class);
			/* 0x07 */ bind(EntityPositionCodec.class);
			/* 0x08 */ bind(ChunkDataCodec.class);
			/* 0x09 */ bind(BlockUpdateCodec.class);
			/* 0x0A */ bind(CuboidBlockUpdateCodec.class);
			/* 0x0B */ bind(ClickCodec.class);
			/* 0x0C */ bind(PlayerInputCodec.class);
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}
}
