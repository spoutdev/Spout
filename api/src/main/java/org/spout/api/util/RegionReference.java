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
package org.spout.api.util;

import java.lang.ref.WeakReference;

import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;

/**
 * This holds a {@code WeakReference<Region>} that can be used streamline the get() with a isLoaded check. It also adds a
 * store of a {@code Point} representing the base. Because of this, a RegionReference may contain only base info.
 */
public class RegionReference {
	private final Point base;
	private WeakReference<Region> region;
	public RegionReference(Region referent) {
		this.region = new WeakReference<>(referent);
		base = referent.getBase();
	}

	public RegionReference(Point base) {
		region = null;
		this.base = base;
	}

	public Region get() {
		Region get = region == null ? null : region.get();
		if (get != null) {
			if (!get.isLoaded()) {
				region = null;
				return null;
			}
		}
		return get;
	}

	public Region refresh(LoadOption opt) {
		Region newRegion = get();
		if (newRegion != null) return newRegion;
		newRegion = base.getRegion(opt);
		this.region = newRegion == null ? null : new WeakReference<>(newRegion);
		return newRegion;
	}

	@Override
	public int hashCode() {
		return base.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RegionReference) {
			return base.equals(((RegionReference) obj).base);
		}
		return false;
	}

	public Point getBase() {
		return base;
	}
}
