package org.getspout.api.plugin;

import java.net.URL;

import org.getspout.api.plugin.exceptions.RestrictedClassException;

public class ClientClassLoader extends CommonClassLoader {

	public ClientClassLoader(CommonPluginLoader loader, URL[] urls, ClassLoader parent) {
		super(loader, urls, parent);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith("net.minecraft")) 
			throw new RestrictedClassException("Accessing net.minecraft is not allowed");		
		return findClass(name, true);
	}

}
