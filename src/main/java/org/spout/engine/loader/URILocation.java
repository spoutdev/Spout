package org.spout.engine.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.spout.api.Spout;
import org.spout.api.plugin.Plugin;


public class URILocation implements ResourceLocation {

	public InputStream getResourceAsStream(String ref) {
		URI uri;
		try {
			uri = getResource(ref).toURI();
		} catch (URISyntaxException e) {
			return null;
		}
		if(uri == null) return null;
		
		String plugin = uri.getHost();
		String path = uri.getPath();
		Plugin p = Spout.getGame().getPluginManager().getPlugin(plugin);
		if(p == null) throw new RuntimeException("Plugin " + plugin + " Not Found");
		try {
			JarFile pfile = new JarFile(p.getFile());
			JarEntry entry = pfile.getJarEntry(path);
			return pfile.getInputStream(entry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
	}

	public URL getResource(String ref) {
		try {
			URI a = new URI(ref);
			return a.toURL();
		} catch (Exception e) {
			return null;
		}
	}

}
