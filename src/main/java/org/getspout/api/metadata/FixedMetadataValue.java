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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.metadata;

import java.util.concurrent.Callable;

import org.getspout.api.plugin.Plugin;

/**
 * A FixedMetadataValue is a special case metadata item that contains the same value forever after initialization.
 * Invalidating a FixedMetadataValue has no affect.
 */
public class FixedMetadataValue extends LazyMetadataValue {

	/**
	 * Initializes a FixedMetadataValue as an int
	 * @param owningPlugin
	 * @param value
	 */
	public FixedMetadataValue(Plugin owningPlugin, final int value) {
		super(owningPlugin, CacheStrategy.CACHE_ETERNALLY, new Callable<Object>() {

			public Object call() throws Exception {
				return value;
			}

		});
	}

	/**
	 * Initializes a FixedMetadataValue as a boolean
	 * @param owningPlugin
	 * @param value
	 */
	public FixedMetadataValue(Plugin owningPlugin, final boolean value) {
		super(owningPlugin, CacheStrategy.CACHE_ETERNALLY, new Callable<Object>() {

			public Object call() throws Exception {
				return value;
			}

		});
	}

	/**
	 * Initializes a FixedMetadataValue as a double
	 * @param owningPlugin
	 * @param value
	 */
	public FixedMetadataValue(Plugin owningPlugin, final double value) {
		super(owningPlugin, CacheStrategy.CACHE_ETERNALLY, new Callable<Object>() {

			public Object call() throws Exception {
				return value;
			}

		});
	}

	/**
	 * Initializes a FixedMetadataValue as a string
	 * @param owningPlugin
	 * @param value
	 */
	public FixedMetadataValue(Plugin owningPlugin, final String value) {
		super(owningPlugin, CacheStrategy.CACHE_ETERNALLY, new Callable<Object>() {

			public Object call() throws Exception {
				return value;
			}

		});
	}

}
