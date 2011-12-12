package org.getspout.commons.addon;

public class AddonNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 8293365135525250896L;
	public AddonNotFoundException(int databaseId) {
		super("The Addon with id "+databaseId+" was not found in the database.");
	}
	
	public AddonNotFoundException(String name) {
		super("The Addon \""+name+"\" was not found in the database.");
	}
}