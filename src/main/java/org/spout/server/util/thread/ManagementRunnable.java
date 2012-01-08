/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.util.thread;

import java.io.Serializable;

import org.spout.server.util.thread.future.ManagedFuture;

/**
 * This task must support being serialized and then the deserialized object being run instead
 *
 * This task does not have a return a value
 */
public abstract class ManagementRunnable extends ManagementTask {

	private static final long serialVersionUID = 1L;

	/**
	 * A Runnable doesn't return a value, so has no associated Future
	 */
	public final ManagedFuture<Serializable> getFuture() {
		return null;
	}

	/**
	 * A Runnable doesn't return a value, so has no associated Future
	 */
	public final void setFuture(ManagedFuture<Serializable> future) {
		if (future != null) {
			future.set(null);
		}
	}

}
