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
package org.spout.api.geo;

public enum LoadOption {
	/**
	 * Do not load or generate chunk/region if not currently loaded
	 */
	NO_LOAD(false, false),
	/**
	 * Load chunk/region if not currently loaded, but do not generate it if it does not yet exist
	 */
	LOAD_ONLY(true, false),
	/**
	 * Load chunk/region if not currently loaded, and generate it if it does not yet exist
	 */
	LOAD_GEN(true, true),
	/**
	 * Don't load the chunk if it has already been generated, only generate if it does not yet exist
	 */
	GEN_ONLY(false, true);

	private final boolean load;
	private final boolean generate;

	private LoadOption(boolean load, boolean generate) {
		this.load = load;
		this.generate = generate;
	}
	/**
	 * Test if chunk/region should be loaded if not currently loaded
	 * @return true if yes, false if no
	 */
	public final boolean loadIfNeeded() {
		return load;
	}
	/**
	 * Test if chunk/region should be generated if it does not exist
	 * @return true if yes, false if no
	 */
	public final boolean generateIfNeeded() {
		return generate;
	}
}
