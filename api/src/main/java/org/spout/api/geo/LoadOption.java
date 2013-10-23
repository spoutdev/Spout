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
package org.spout.api.geo;

public class LoadOption {
	/**
	 * Do not load or generate chunk/region if not currently loaded
	 */
	public static final LoadOption NO_LOAD = new LoadOption(false, false, true);
	/**
	 * Load chunk/region if not currently loaded, but do not generate it if it does not yet exist
	 */
	public static final LoadOption LOAD_ONLY = new LoadOption(true, false, true);
	/**
	 * Load chunk/region if not currently loaded, and generate it if it does not yet exist
	 */
	public static final LoadOption LOAD_GEN = new LoadOption(true, true, true);
	/**
	 * Don't load the chunk if it has already been generated, only generate if it does not yet exist
	 */
	public static final LoadOption GEN_ONLY = new LoadOption(false, true, true);
	/**
	 * Load chunk/region if not currently loaded, and generate it if it does not yet exist. Do not wait for it to Load/Gen.
	 */
	public static final LoadOption LOAD_GEN_NOWAIT = new LoadOption(true, true, false);

	private final boolean load;
	private final boolean generate;
	private final boolean wait;

	public LoadOption(boolean load, boolean generate, boolean wait) {
		this.load = load;
		this.generate = generate;
		this.wait = wait;
	}

	/**
	 * Test if chunk/region should be loaded if not currently loaded
	 *
	 * @return true if yes, false if no
	 */
	public final boolean loadIfNeeded() {
		return load;
	}

	/**
	 * Test if chunk/region should be generated if it does not exist
	 *
	 * @return true if yes, false if no
	 */
	public final boolean generateIfNeeded() {
		return generate;
	}

	/**
	 * Test if chunk/region should be generated if it does not exist
	 *
	 * @return true if yes, false if no
	 */
	public final boolean waitForLoadOrGen() {
		return wait;
	}

	@Override
	public String toString() {
		return "LoadOption{" + "load=" + load + ", generate=" + generate + ", wait=" + wait + '}';
	}
}
