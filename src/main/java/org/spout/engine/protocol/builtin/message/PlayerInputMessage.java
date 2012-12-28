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
package org.spout.engine.protocol.builtin.message;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.util.SpoutToStringStyle;

public class PlayerInputMessage extends SpoutMessage {

	private final short inputFlags;
	private final short mouseDx, mouseDy;

	public PlayerInputMessage(short inputFlags, short mouseDx, short mouseDy) {
		this.inputFlags = inputFlags;

		this.mouseDx = mouseDx;
		this.mouseDy = mouseDy;
	}

	public short getInputFlags() {
		return inputFlags;
	}

	public short getMouseDx() {
		return mouseDx;
	}

	public short getMouseDy() {
		return mouseDy;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("inputFlags", inputFlags)
				.append("mouseDx", mouseDx)
				.append("mouseDy", mouseDy)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(5, 9)
				.append(inputFlags)
				.append(mouseDx)
				.append(mouseDy)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayerInputMessage) {
			final PlayerInputMessage other = (PlayerInputMessage) obj;
			return new EqualsBuilder()
					.append(inputFlags, other.inputFlags)
					.append(mouseDx, other.mouseDx)
					.append(mouseDy, other.mouseDy)
					.isEquals();
		} else {
			return false;
		}
	}
}
