/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.chunkcache;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unchecked")
public class PartitionChunk {
	private final static int MAX_HEIGHT = 16;

	private final static AtomicReference<int[]>[] startPoint;
	private final static AtomicReference<int[]>[] step1;
	private final static AtomicBoolean[] LUTSetup;

	static {
		startPoint = new AtomicReference[MAX_HEIGHT + 1];
		LUTSetup = new AtomicBoolean[MAX_HEIGHT + 1];
		step1 = new AtomicReference[MAX_HEIGHT + 1];
		for (int count = 0; count < MAX_HEIGHT; count++) {
			LUTSetup[count] = new AtomicBoolean(false);
		}
	}

	private static void updateLookupTable(int heightBits) {

		if (heightBits < 0 || heightBits > MAX_HEIGHT) {
			throw new IllegalArgumentException("Height of " + heightBits + " bits for chunk height is outside of range (0 - " + MAX_HEIGHT + ")");
		}

		if (LUTSetup[heightBits].get()) {
			return;
		}

		int height = 1 << heightBits;

		int blockSegments = height >> 3;

		int extraSegments = blockSegments >> 1;
		int extraSegmentsMask = extraSegments - 1;
		int extraSegmentSize = height << 7;

		int segments = blockSegments + 3 * extraSegments;

		int[] newStartPoint = new int[segments];
		int[] newStep1 = new int[segments];

		int cnt;
		for (cnt = 0; cnt < blockSegments; cnt++) {
			newStartPoint[cnt] = cnt << 3;
			newStep1[cnt] = height - 8;
		}

		int nextStart = extraSegmentSize << 1;
		for (cnt = blockSegments; cnt < segments; cnt++) {
			if ((cnt & extraSegmentsMask) == 0) {
				newStartPoint[cnt] = nextStart;
				nextStart += 16384;
			} else {
				newStartPoint[cnt] = newStartPoint[cnt - 1] + 8;
			}
			newStep1[cnt] = (height >> 1) - 8;
		}

		step1[heightBits] = new AtomicReference(newStep1);
		startPoint[heightBits] = new AtomicReference(newStartPoint);

		LUTSetup[heightBits].set(true);

	}

	static public void copyToChunkData(byte[] chunkData, int blockNum, byte[] partition, int heightBits) {

		updateLookupTable(heightBits);
		int start = startPoint[heightBits].get()[blockNum];
		int step = step1[heightBits].get()[blockNum];

		int pos = start;

		int partitionPos = 0;
		
		boolean clear = partition == null;

		for (int outer = 0; outer < 256; outer++) {
			for (int inner = 0; inner < 8; inner++) {
				if (clear) {
					chunkData[pos] = 0;
				} else {
					chunkData[pos] = partition[partitionPos++];
				}
				pos++;
			}
			pos += step;
		}

	}

	static public void copyFromChunkData(byte[] chunkData, int blockNum, byte[] partition, int heightBits) {

		updateLookupTable(heightBits);
		int start = startPoint[heightBits].get()[blockNum];
		int step = step1[heightBits].get()[blockNum];

		int pos = start;

		int partitionPos = 0;
		
		for (int outer = 0; outer < 256; outer++) {
			for (int inner = 0; inner < 8; inner++) {
				partition[partitionPos++] = chunkData[pos];
				pos++;
			}
			pos += step;
		}

	}

	static public long getHash(byte[] chunkData, int blockNum, int heightBits) {

		int height = 1 << heightBits;
		int size = ((height * 5) / 2) * 256;

		int p = blockNum * 8 + size;
		long hash = 0;
		hash = hash << 8 | (((long) chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long) chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long) chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long) chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long) chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long) chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long) chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long) chunkData[p++]) & 0xFFL);
		return hash;
	}

	static public void setHash(byte[] chunkData, int blockNum, long hash, int heightBits) {

		int height = 1 << heightBits;
		int size = ((height * 5) / 2) * 256;

		int p = blockNum * 8 + size;
		chunkData[p++] = (byte) (hash >> 56);
		chunkData[p++] = (byte) (hash >> 48);
		chunkData[p++] = (byte) (hash >> 40);
		chunkData[p++] = (byte) (hash >> 32);
		chunkData[p++] = (byte) (hash >> 24);
		chunkData[p++] = (byte) (hash >> 16);
		chunkData[p++] = (byte) (hash >> 8);
		chunkData[p++] = (byte) (hash >> 0);
	}

}
