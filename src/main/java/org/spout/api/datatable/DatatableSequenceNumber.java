/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.datatable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gets a unique sequence number for Datatable updates.
 *
 * If a record has the same sequence number before and a read, then the read can be considered to have completed correctly.
 */
public class DatatableSequenceNumber {
	private static AtomicInteger sequenceNumber = new AtomicInteger(0);

	/**
	 * Sequence number that indicates the record is unstable
	 */
	public static final int UNSTABLE = 1;

	/**
	 * Sequence number that indicates that the read was atomic, and so always is valid
	 */
	public static final int ATOMIC = 3;

	/**
	 * Gets a unique sequence number.
	 *
	 * @return the number
	 */
	public static int get() {
		return sequenceNumber.getAndAdd(2);
	}
}
