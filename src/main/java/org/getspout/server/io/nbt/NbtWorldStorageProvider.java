package org.getspout.server.io.nbt;

import java.io.File;

import org.getspout.server.SpoutWorld;
import org.getspout.server.io.ChunkIoService;
import org.getspout.server.io.WorldMetadataService;
import org.getspout.server.io.WorldStorageProvider;

public class NbtWorldStorageProvider implements WorldStorageProvider {
	private final File dir;
	private NbtChunkIoService service;
	private NbtWorldMetadataService meta;

	public NbtWorldStorageProvider(String name) {
		this(new File(name));
	}

	public NbtWorldStorageProvider(File dir) {
		this.dir = dir;

	}

	@Override
	public void setWorld(SpoutWorld world) {
		if (world != null) {
			throw new IllegalArgumentException("World is already set");
		}
		service = new NbtChunkIoService();
		meta = new NbtWorldMetadataService(world, dir);
	}

	@Override
	public ChunkIoService getChunkIoService() {
		return service;
	}

	@Override
	public WorldMetadataService getMetadataService() {
		return meta;
	}

	@Override
	public File getFolder() {
		return dir;
	}
}
