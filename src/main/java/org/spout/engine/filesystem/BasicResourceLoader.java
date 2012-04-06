package org.spout.engine.filesystem;
import java.io.InputStream;
import java.net.URI;

import org.spout.api.resource.Resource;
import org.spout.api.resource.ResourceLoader;

public abstract class BasicResourceLoader<E extends Resource> implements ResourceLoader<E> {
	public abstract E getResource(InputStream stream);
	public E getResource(URI resource){
		return getResource(FileSystem.getResourceStream(resource));
	}
}
