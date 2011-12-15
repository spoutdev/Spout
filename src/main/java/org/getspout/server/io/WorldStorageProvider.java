package org.getspout.server.io;

import java.io.File;

import org.getspout.server.SpoutWorld;

public interface WorldStorageProvider {

    public ChunkIoService getChunkIoService();

    public WorldMetadataService getMetadataService();

    public void setWorld(SpoutWorld world);

    /** Get the folder holding the world data.
     * @return world folder
     */
    public File getFolder();

}
