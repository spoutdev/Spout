package org.spout.api.io.regionfile;


public class SimpleRegionFileClosedException extends SimpleRegionFileException {

	private static final long serialVersionUID = 1L;

	public SimpleRegionFileClosedException(String message, Throwable t) {
		super(message, t);
	}
	
	public SimpleRegionFileClosedException(String message) {
		super(message);
	}
	
}
