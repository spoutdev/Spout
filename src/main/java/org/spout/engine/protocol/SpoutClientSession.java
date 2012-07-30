/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.protocol;

import org.jboss.netty.channel.Channel;
import org.spout.api.protocol.Protocol;
import org.spout.engine.SpoutClient;

/**
 * @author zml2008
 */
public class SpoutClientSession extends SpoutSession<SpoutClient> {
	/**
	 * Creates a new session.
	 *
	 * @param engine The server this session belongs to.
	 * @param channel The channel associated with this session.
	 */
	public SpoutClientSession(SpoutClient engine, Channel channel, Protocol bootstrapProtocol, boolean proxy) {
		super(engine, channel, bootstrapProtocol, proxy);
	}
}
