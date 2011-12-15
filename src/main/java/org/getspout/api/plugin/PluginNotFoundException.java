package org.getspout.api.plugin;

public class PluginNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 8293365135525250896L;
	public PluginNotFoundException(int databaseId) {
		super("The Addon with id "+databaseId+" was not found in the database.");
	}
	
	public PluginNotFoundException(String name) {
		super("The Addon \""+name+"\" was not found in the database.");
	}
}