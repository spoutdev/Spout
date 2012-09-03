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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.util.SpoutToStringStyle;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.annotated.AnnotatedConfiguration;
import org.spout.api.util.config.annotated.Setting;
import org.spout.engine.SpoutServer;

/**
 * Handler for reading port bindings from the configuration
 */
public class PortBindings extends AnnotatedConfiguration {
	private final SpoutServer server;
	@Setting("addresses") private List<ConfigPortBinding> portBindings;

	public static class ConfigPortBinding extends AnnotatedConfiguration implements PortBinding {
		@Setting("protocol") private String protocolName;
		private transient Protocol protocol;
		@Setting("address") private String address = "0.0.0.0";
		@Setting("port") private int port = -1;

		public ConfigPortBinding(Configuration baseConfig) {
			super(baseConfig);
		}

		@Override
		public Protocol getProtocol() {
			return protocol;
		}

		@Override
		public SocketAddress getAddress() {
			return new InetSocketAddress(address, port);
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
					.append("protocolName", protocolName)
					.append("protocol", protocol)
					.append("address", address)
					.append("port", port)
					.toString();
		}
	}

	public PortBindings(SpoutServer server, Configuration baseConfig) {
		super(baseConfig);
		this.server = server;
	}

	public void bindAll() {
		addDefaults();

		for (ConfigPortBinding binding : portBindings) {
			if (binding.protocol == null) {
				Protocol protocol = Protocol.getProtocol(binding.protocolName);
				if (protocol == null) {
					server.getLogger().warning("Could not bind to port, unknown protocol '" + binding.protocolName + "'");
					continue;
				}
				binding.protocol = protocol;
			}

			if (binding.port != -1) {
				server.bind(binding);
			}
		}
	}

	public void addDefaults() {
		final Set<String> existingProtocols = new HashSet<String>();
		if (portBindings != null) {
			for (ConfigPortBinding binding : portBindings) {
				existingProtocols.add(binding.protocolName);
			}
		}

		if (portBindings == null) {
			portBindings = new ArrayList<ConfigPortBinding>();
		}

		for (Protocol proto : Protocol.getProtocols()) {
			if (!existingProtocols.contains(proto.getName())) {
				ConfigPortBinding binding = new ConfigPortBinding(getConfiguration());
				binding.protocolName = proto.getName();
				binding.protocol = proto;
				binding.port = proto.getDefaultPort();
				portBindings.add(binding);
			}
		}
	}
}
