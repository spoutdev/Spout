package org.getspout.server.io.mcregion;

import java.io.File;

import org.getspout.server.SpoutWorld;
import org.getspout.server.io.ChunkIoService;
import org.getspout.server.io.WorldMetadataService;
import org.getspout.server.io.WorldStorageProvider;
import org.getspout.server.io.nbt.NbtWorldMetadataService;

public class McRegionWorldStorageProvider implements WorldStorageProvider {
	private SpoutWorld world;
	private final File dir;
	private McRegionChunkIoService service;
	private NbtWorldMetadataService meta;

	public McRegionWorldStorageProvider(String name) {
		this(new File(name));
	}

	public McRegionWorldStorageProvider(File dir) {
		this.dir = dir;
	}

	public void setWorld(SpoutWorld world) {
		if (this.world != null)
			throw new IllegalArgumentException("World is already set");
		this.world = world;
		service = new McRegionChunkIoService(dir);
		meta = new NbtWorldMetadataService(world, dir);
	}

	public ChunkIoService getChunkIoService() {
		return service;
	}

	public WorldMetadataService getMetadataService() {
		return meta;
	}

	public File getFolder() {
		return dir;
	}
}
