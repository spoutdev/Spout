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

/**
 * Implementers of this class represent the data of a packet to be sent through the SpoutAPI networking system. There are a few rules that messages should follow: <ul> <li>All message fields should be
 * immutable. This ensures thread-safety and makes it so Message objects can be safely stored</li> <li>Message subclasses should override {@link #toString()}, {@link #equals(Object)} , and {@link
 * #hashCode()}. </li> <li>All fields in a Message should be protocol-primitive (can be written directly via ByteBuf methods or via a *single* ByteBufUtils method)</li> </ul> Any committer
 * who violates these rules (except for the hashCode() one) will be hunted down and beaten with a shovel by zml2008 (Offer only valid in the Portland, OR area)
 */
public interface Message {
	public static final int DEFAULT_CHANNEL = 0;

	@Override
	public String toString();

	@Override
	public boolean equals(Object other);

	@Override
	public int hashCode();

	public boolean isAsync();

	/**
	 * Gets the channel id for this messages.  The ordering of messages with different channel ids by the network library is undefined.<br> Channels are used to allow certain messages that may have
	 * time-consuming encode or decode methods to be sent on a separate thread, Usually you can just have this return {@link #DEFAULT_CHANNEL}.<br> <br> Channels from 0 to 7 are guaranteed to exist.
	 * Channels outside this range may be aliased back into this range.
	 */
	public int getChannelId();

	public boolean requiresPlayer();
}
