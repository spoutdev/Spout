/*
 * This file is part of Spout (http://www.spout.org/).
 *
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
package org.spout.engine.util.thread.future;

import org.spout.api.util.future.SimpleFuture;

import org.spout.engine.util.thread.AsyncManager;

/**
 * This is a future that is linked to a particular AsyncManager
 */
public class ManagedFuture<T> extends SimpleFuture<T> {
	private AsyncManager manager;

	public ManagedFuture() {
		super();
	}

	public ManagedFuture(T result) {
		super(result);
	}

	public ManagedFuture(AsyncManager manager, T result) {
		super(result);
		this.manager = manager;
	}

	/**
	 * Gets manager associated with this future
	 * @return the manager
	 */
	public AsyncManager getManager() {
		return manager;
	}

	/**
	 * Sets the manager associated with this future
	 * @return the manager
	 */
	public void setManager(AsyncManager manager) {
		this.manager = manager;
	}
}
