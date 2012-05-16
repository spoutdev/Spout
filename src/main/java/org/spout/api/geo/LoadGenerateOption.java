/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.geo;

public enum LoadGenerateOption {
	/**
	 * Do not load or generate chunk/region if not currently loaded
	 */
	NO_LOAD(false, false),
	/**
	 * Load chunk/region if not currently loaded, but do not generate it if it does not yet exist
	 */
	LOAD_IF_NEEDED(true, false),
	/**
	 * Load chunk/region if not currently loaded, and generate it if it does not yet exist
	 */
	LOAD_OR_GENERATE_IF_NEEDED(true, true);
	
	private final boolean load;
	private final boolean generate;
	
	private LoadGenerateOption(boolean load, boolean generate) {
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
