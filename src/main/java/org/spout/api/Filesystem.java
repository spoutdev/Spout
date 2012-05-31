package org.spout.api;


import java.io.InputStream;
import java.net.URI;

import org.spout.api.resource.Resource;
import org.spout.api.resource.ResourceLoader;
import org.spout.api.resource.ResourceNotFoundException;

public interface Filesystem {

	public abstract void init();

	public abstract void postStartup();

	public abstract InputStream getResourceStream(URI path) throws ResourceNotFoundException;

	public abstract InputStream getResourceStream(String path);

	public abstract void registerLoader(String protocol, ResourceLoader<? extends Resource> loader);

	public abstract void loadResource(URI path) throws ResourceNotFoundException;

	public abstract void loadResource(String path);

	public abstract Resource getResource(URI path);

	public abstract Resource getResource(String path);

}