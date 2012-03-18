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
package org.spout.server.io;

/**
 * An operation for the storage queue
 */
public abstract class StorageOperation implements Runnable {
	/**
	 * Can this operation run in parallel with other operation types
	 */
	public abstract boolean isParallel();

	/**
	 * The group for this operation (world name for example)
	 */
	public abstract String getGroup();

	/**
	 * Whether multiple of this operation can be in the storage queue at once
	 * @return
	 */
	public abstract boolean queueMultiple();

	/**
	 * The name of the operation being performed.
	 * @return
	 */
	public abstract String getOperation();

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StorageOperation)) {
			return false;
		}
		StorageOperation op = (StorageOperation) other;
		return getGroup().equals(op.getGroup()) && getOperation().equals(op.getOperation()) && isParallel() == op.isParallel() && queueMultiple() == op.queueMultiple();
	}
}