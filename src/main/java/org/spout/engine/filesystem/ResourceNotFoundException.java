package org.spout.engine.filesystem;

public class ResourceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException(String resource){
		super("Cannot find: " + resource);
	}

}
