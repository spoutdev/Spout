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
package org.spout.api.plugin.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spout.api.Spout;
import org.spout.api.geo.Protection;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.plugin.ServiceProvider;

/**
 * The protection service is a basic service that can be extended and registered as a service provider.
 * To implement your own ProtectionService, create a new class which extends ProtectionService and overrides
 * the abstract methods.
 * To register your ProtectionService you will need to do something similar to:
 * <code>getServiceManager().register(ProtectionService.class, myProtectionServiceInstance, myPlugin, ServicePriority)</code>
 * 
 * The static methods provided are intended for direct usage by any plugins.  Plugins should NOT be getting the
 * protection services directly as it may result in incomplete data from the server.
 *
 */
public abstract class ProtectionService {

	private static boolean isRegistered() {
		return Spout.getEngine().getServiceManager().getRegistrations(ProtectionService.class) != null;
	}

	/**
	 * Gets the list of all active protections on the server, this may be slow depending on how many protections there are.
	 * 
	 * @return list of all active protections on the server
	 */
	public static List<Protection> getProtections() {
		List<Protection> protections = new ArrayList<Protection>();
		if (isRegistered()) {
			for (ServiceProvider<ProtectionService> rsp : Spout.getEngine().getServiceManager().getRegistrations(ProtectionService.class)) {
				protections.addAll(rsp.getProvider().getAllProtections());
			}
		}
		return protections;
	}

	/**
	 * Gets the list of all active protections on the server that are at the specific point.
	 * This may be slow, depending on how optimized specific Protection plugins are in finding their protections.
	 * 
	 * @param point to test protections at.
	 * @return List of all protections that are at the point
	 */
	public static List<Protection> getProtections(Point point) {
		List<Protection> protections = new ArrayList<Protection>();
		if (isRegistered()) {
			for (ServiceProvider<ProtectionService> rsp : Spout.getEngine().getServiceManager().getRegistrations(ProtectionService.class)) {
				protections.addAll(rsp.getProvider().getAllProtections(point));
			}
		}
		return protections;
	}

	/**
	 * Gets the list of all active protections on the server that are on the specific world.
	 * This may be slow, depending on how optimized specific Protection plugins are in finding their protections.
	 * 
	 * @param world to get protections from.
	 * @return List of all protections that are at the point
	 */
	public static List<Protection> getProtections(World world) {
		List<Protection> protections = new ArrayList<Protection>();
		if (isRegistered()) {
			for (ServiceProvider<ProtectionService> rsp : Spout.getEngine().getServiceManager().getRegistrations(ProtectionService.class)) {
				protections.addAll(rsp.getProvider().getAllProtections(world));
			}
		}
		return protections;
	}

	/**
	 * Attempts to lookup the protection by name.
	 * 
	 * @param name
	 * @return the region
	 */
	public abstract Protection getProtection(String name);

	/**
	 * Gets all protections on the given world.
	 * 
	 * @param world
	 * @return a collection of protections.
	 */
	public abstract Collection<Protection> getAllProtections(World world);

	/**
	 * Gets all protections at the given point.
	 * 
	 * @param point
	 * @return a collection of protections
	 */
	public abstract Collection<Protection> getAllProtections(Point point);

	/**
	 * Gets a collection that contains all regions on the server.
	 * @return all protections registered
	 */
	public abstract Collection<Protection> getAllProtections();
}
