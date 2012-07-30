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

import java.net.SocketAddress;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.util.SpoutToStringStyle;

/**
 * Implementation of {@link PortBinding}
 */
public class PortBindingImpl implements PortBinding {
	private final Protocol protocol;
	private final SocketAddress address;
	private int hashCode;

	public PortBindingImpl(Protocol protocol, SocketAddress address) {
		this.protocol = protocol;
		this.address = address;
		hashCode = new HashCodeBuilder(479, 185)
				.append(this.protocol)
				.append(this.address).toHashCode();
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public SocketAddress getAddress() {
		return address;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {return false;}
		if (getClass() != obj.getClass()) {return false;}
		final PortBindingImpl other = (PortBindingImpl) obj;
		return new EqualsBuilder()
				.append(this.protocol, other.protocol)
				.append(this.address, other.address)
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("protocol", protocol)
				.append("address", address)
				.append("hashCode", hashCode)
				.toString();
	}
}
