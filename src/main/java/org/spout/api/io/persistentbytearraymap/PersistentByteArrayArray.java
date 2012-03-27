package org.spout.api.io.persistentbytearraymap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Classes which implement this interface provide thread safe persistent storage for an array of byte arrays.<br>
 * <br>
 * Each entry of the array is referred to as a block.  Each block is a byte array.<br>
 * <br>
 * The number of blocks in the array is determined at creation.
 */
public interface PersistentByteArrayArray {

	/**
	 * Gets a DataInputStream for reading a block.<br>
	 * <br>
	 * This method creates a snapshot of the block.
	 * 
	 * @param i the index of the block
	 * @return a DataInputStream for the block
	 * @throws IOException on error
	 */
	public DataInputStream getInputStream(int i) throws IOException;
	
	/**
	 * Gets a DataOutputStream for writing to a block.<br>
	 * <br>
	 * WARNING:  This locks the block until the output stream is closed.<br>
	 * 
	 * @param i the block index
	 * @return a DataOutputStream for the block
	 * @throws IOException
	 */
	public DataOutputStream getOutputStream(int i) throws IOException;
	
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

}
