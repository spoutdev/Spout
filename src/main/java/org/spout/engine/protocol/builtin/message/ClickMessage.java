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

public class ClickMessage extends SpoutMessage {

	public enum Action {

		LEFT, RIGHT, CENTER,
	}

	private final Action clickType;

	public ClickMessage(Action clickType) {
		this.clickType = clickType;
	}

	public ClickMessage(int clickType) {
		if (clickType < 0 || clickType >= Action.values().length) {
			throw new IllegalArgumentException("Unknown action ID " + clickType);
		}
		this.clickType = Action.values()[clickType];
	}

	public Action getClickType() {
		return clickType;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("clickType", clickType)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(87, 53)
				.append(clickType)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClickMessage) {
			final ClickMessage other = (ClickMessage) obj;
			return new EqualsBuilder()
					.append(clickType, other.clickType)
					.isEquals();
		} else {
			return false;
		}
	}
}
