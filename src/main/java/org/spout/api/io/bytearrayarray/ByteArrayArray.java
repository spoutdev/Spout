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
package org.spout.api.io.bytearrayarray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Classes which implement this interface provide thread safe persistent storage for an array of byte arrays.<br>
 * <br>
 * Each entry of the array is referred to as a block.  Each block is a byte array.<br>
 * <br>
 * The number of blocks in the array is determined at creation.
 */
public interface ByteArrayArray {

	/**
	 * Gets a DataInputStream for reading a block.<br>
	 * <br>
	 * This method creates a snapshot of the block.
	 * 
	 * @param i the index of the block
	 * @return a DataInputStream for the block
	 * @throws IOException on error
	 */
	public InputStream getInputStream(int i) throws IOException;
	
	/**
	 * Gets a DataOutputStream for writing to a block.<br>
	 * <br>
	 * WARNING:  This locks the block until the output stream is closed.<br>
	 * 
	 * @param i the block index
	 * @return a DataOutputStream for the block
	 * @throws IOException
	 */
	public OutputStream getOutputStream(int i) throws IOException;
	
	/**
	 * Attempts to close the map.  This method will only succeed if no block DataOutputStreams are active.
	 * 
	 * @return true on success
	 * @throws IOException 
	 */
	public boolean attemptClose() throws IOException;
	
	/**
	 * Checks if the access timeout has expired
	 * 
	 * @return true on timeout
	 */
	public boolean isTimedOut();
	
	/**
	 * Attempts to close map if the file has timed out.<br>
	 * <br>
	 * This will fail if there are any open DataOutputStreams
	 */
	public void closeIfTimedOut() throws IOException;
	
	/**
	 * Gets if the map is closed
	 * 
	 * @return true if the file is closed
	 */
	public boolean isClosed();

	/**
	 * Checks if any data exists at the block index.
	 * 
	 * @param i the block index
	 * @return true if it exists
	 * @throws IOException
	 */
	boolean exists(int i) throws IOException;

}
