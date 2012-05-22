package org.spout.engine.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.spout.api.resource.Resource;
import org.spout.api.resource.ResourceLoader;

public abstract class BasicResourceLoader<E extends Resource> implements ResourceLoader<E> {
	public abstract String getFallbackResourceName();
	
	@Override
	public abstract E getResource(InputStream stream);

	@Override
	public E getResource(URI resource) throws ResourceNotFoundException{
		InputStream s = FileSystem.getResourceStream(resource);
		E r = getResource(s);
		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}
}
