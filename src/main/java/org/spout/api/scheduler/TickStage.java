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
package org.spout.api.scheduler;

import org.spout.api.exception.IllegalTickSequenceException;

/**
 * Represents the various tick stages.<br>
 * <br>
 * The exact bit fields used are subject to change
 */
public class TickStage {

	/**
	 * All tasks submitted to the main thread are executed during TICKSTART.<br>
	 * <br>
	 * This stage is single threaded
	 */
	public final static int TICKSTART = 1 << 0;

	/**
	 * This is the first stage of the execution of the tick
	 */
	public final static int STAGE1 = 1 << 1;

	/**
	 * This is the second and subsequent stages of the tick
	 */
	public final static int STAGE2P = 1 << 2;

	/**
	 * This is the final stage before entering the pre-snapshot stage.<br>
	 * <br>
	 * This is for minor changes prior to the snapshot process.
	 */
	public final static int FINALIZE = 1 << 3;

	/**
	 * This stage occurs before the snapshot stage.<br>
	 * <br>
	 * This is a MONITOR ONLY stage, no changes should be made during the stage.
	 */
	public final static int PRESNAPSHOT = 1 << 4;

	/**
	 * This is the snapshot copy stage.<br>
	 * <br>
	 * All snapshots are updated to the equal to the live value.
	 */
	public final static int SNAPSHOT = 1 << 5;

	public static String getStage(int num) {
		switch (num) {
			case 1 << 0:
				return "TICKSTART";
			case 1 << 1:
				return "STAGE1";
			case 1 << 2:
				return "STAGE2P";
			case 1 << 3:
				return "FINALIZE";
			case 1 << 4:
				return "PRESNAPSHOT";
			case 1 << 5:
				return "SNAPSHOT";
			default:
				return "UNKNOWN";
		}
	}

	public static String getAllStages(int num) {
		int scan = 1;
		boolean first = true;
		StringBuilder sb = new StringBuilder();

		while (scan != 0) {
			int checkNum = num & scan;
			if (checkNum != 0) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(getStage(checkNum));
			}
			scan = scan << 1;
		}
		return sb.toString();
	}

	private static int stage;

	/**
	 * Sets the current stage. This is not synchronised, so should only be
	 * called during the stable period between stages.
	 *
	 * @param stage the stage
	 */
	public static void setStage(int stage) {
		TickStage.stage = stage;
	}

	/**
	 * Checks if the current stages is one of the valid allowed stages.
	 *
	 * @param allowedStages the OR of all the allowed stages
	 */
	public static void checkStage(int allowedStages) {
		if (!testStage(allowedStages)) {
			throw new IllegalTickSequenceException(allowedStages, stage);
		}
	}
	
	/**
	 * Checks if the current stages is one of the valid allowed stages, but does not throw an exception.
	 *
	 * @param allowedStages the OR of all the allowed stages
	 * @return true if the current stage is one of the allowed stages
	 */
	public static boolean testStage(int allowedStages) {
		return (stage & allowedStages) != 0;
	}
	
	/**
	 * Checks if the current thread is the owner thread and the current stage is one of the restricted stages, or that the current stage is one of the open stages
	 *
	 * @param allowedStages the OR of all the open stages
	 * @param restrictedStages the OR of all restricted stages
	 * @param ownerThread the thread that has restricted access
	 */
	public static void checkStage(int allowedStages, int restrictedStages, Thread ownerThread) {
		if ((stage & allowedStages) == 0 && (((stage & restrictedStages) == 0) || Thread.currentThread() != ownerThread)) {
			throw new IllegalTickSequenceException(allowedStages, restrictedStages, ownerThread, stage);
		}
	}
	
	/**
	 * Checks if the current thread is the owner thread and the current stage is one of the restricted stages
	 *
	 * @param restrictedStages the OR of all restricted stages
	 * @param ownerThread the thread that has restricted access
	 */
	public static void checkStage(int restrictedStages, Thread ownerThread) {
		if (((stage & restrictedStages) == 0) || Thread.currentThread() != ownerThread) {
			throw new IllegalTickSequenceException(restrictedStages, 0, ownerThread, stage);
		}
	}

}
