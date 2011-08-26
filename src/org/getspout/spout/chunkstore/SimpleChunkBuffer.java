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
package org.getspout.spout.chunkstore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SimpleChunkBuffer extends ByteArrayOutputStream  {

	final SimpleRegionFile rf;
	final int index;
	
	SimpleChunkBuffer(SimpleRegionFile rf, int index) {
		super(1024);
		this.rf = rf;
		this.index = index;
	}
	
	@Override
	public void close() throws IOException {
		rf.write(index, buf, count);
	}
	
}
