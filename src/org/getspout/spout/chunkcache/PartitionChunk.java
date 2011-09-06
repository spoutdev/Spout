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

public class PartitionChunk {
	private final static int[] startPoint;
	private final static int[] step1;

	static {

		startPoint = new int[40];
		step1 = new int[40];

		int cnt;
		for(cnt=0;cnt<16;cnt++) {
			startPoint[cnt] = cnt<<3;
			step1[cnt] = 120;
		}

		int nextStart = 32768;
		for(cnt=16;cnt<40;cnt++) {
			if((cnt & 0x0007) == 0) {
				startPoint[cnt] = nextStart;
				nextStart += 16384;
			} else {
				startPoint[cnt] = startPoint[cnt-1] + 8;
			}
			step1[cnt] = 56;
		}

	}
	
	static public void copyToChunkData(byte[] chunkData, int blockNum, byte[] partition) {
		
		int start = startPoint[blockNum];
		int step = step1[blockNum];
		
		int pos = start;
		
		int partitionPos = 0;
		
		boolean clear = partition == null;
		
		for(int outer=0;outer<256;outer++) {
			for(int inner=0;inner<8;inner++) {
				if(clear) {
					chunkData[pos] = 0;
				} else {
					chunkData[pos] = partition[partitionPos++];
				}
				pos++;
			}
			pos+=step;
		}
		
	}
	
	static public void copyFromChunkData(byte[] chunkData, int blockNum, byte[] partition) {
		
		int start = startPoint[blockNum];
		int step = step1[blockNum];
		
		int pos = start;
		
		int partitionPos = 0;
		
		for(int outer=0;outer<256;outer++) {
			for(int inner=0;inner<8;inner++) {
				partition[partitionPos++] = chunkData[pos];
				pos++;
			}
			pos+=step;
		}
		
	}

	static public long getHash(byte[] chunkData, int blockNum) {
		int p = blockNum * 8 + 81920;
		long hash = 0;
		hash = hash << 8 | (((long)chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long)chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long)chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long)chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long)chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long)chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long)chunkData[p++]) & 0xFFL);
		hash = hash << 8 | (((long)chunkData[p++]) & 0xFFL);
		return hash;
	}

	static public void setHash(byte[] chunkData, int blockNum, long hash) {
		int p = blockNum * 8 + 81920;
		chunkData[p++] = (byte)(hash >> 56);
		chunkData[p++] = (byte)(hash >> 48);
		chunkData[p++] = (byte)(hash >> 40);
		chunkData[p++] = (byte)(hash >> 32);
		chunkData[p++] = (byte)(hash >> 24);
		chunkData[p++] = (byte)(hash >> 16);
		chunkData[p++] = (byte)(hash >> 8);
		chunkData[p++] = (byte)(hash >> 0);
	}

}
