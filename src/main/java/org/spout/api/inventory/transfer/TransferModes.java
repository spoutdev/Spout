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
package org.spout.api.inventory.transfer;

public class TransferModes {
	/**
	 * Performs a merge and fill at the same time, filling and merging with the first item slot it can find
	 */
	public static final TransferMode MERGE_AND_FILL = new MergedStackTransferMode(true, true);
	/**
	 * Performs a merge-only operation on the first item slot it can find
	 */
	public static final TransferMode MERGE = new MergedStackTransferMode(true, false);
	/**
	 * Performs a fill-only operation on the first item slot it can find
	 */
	public static final TransferMode FILL = new MergedStackTransferMode(false, true);

	/**
	 * An array of transfer modes used by default if no transfer modes are specified<br><br>
	 * <b>By default it first merges the item, and then fills it</b>
	 */
	public static final TransferMode[] DEFAULT_TRANSFER_MODES = new TransferMode[] {MERGE, FILL};
}
