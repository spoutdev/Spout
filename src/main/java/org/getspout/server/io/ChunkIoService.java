package org.getspout.server.io;

import java.io.IOException;

import org.getspout.server.SpoutChunk;

/**
 * This interface should be implemented by classes which wish to provide some
 * way of performing chunk I/O e.g. the
 * {@link org.getspout.server.io.nbt.NbtChunkIoService}. This interface is
 * abstracted away from the implementation because a new format is due to arrive
 * soon (McRegion).
 *
 * @author Graham Edgecombe
 */
public interface ChunkIoService {
	/**
	 * Reads a single chunk.
	 *
	 * @param chunk The SpoutChunk to read into.
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @throws IOException if an I/O error occurs.
	 */
	public boolean read(SpoutChunk chunk, int x, int z) throws IOException;

	/**
	 * Writes a single chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param chunk The {@link SpoutChunk}.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write(int x, int z, SpoutChunk chunk) throws IOException;

	/**
	 * Unloads the service, to clean up excess stuff.
	 *
	 * @throws IOException
	 */
	public void unload() throws IOException;
}
