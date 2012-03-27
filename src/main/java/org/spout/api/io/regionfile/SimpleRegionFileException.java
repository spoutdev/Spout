package org.spout.api.io.regionfile;

import java.io.IOException;

public class SimpleRegionFileException extends IOException {

	private static final long serialVersionUID = 1L;

	public SimpleRegionFileException(String message, Throwable t) {
		super(message, t);
	}
	
	public SimpleRegionFileException(String message) {
		super(message);
	}
	
}
