package org.getspout.api.plugin.exceptions;

public class UnknownSoftDependencyException extends UnknownDependencyException {

	private static final long serialVersionUID = 3265856380040527690L;

	public UnknownSoftDependencyException() {
		this(null, "Unknown Soft Dependency");
	}

	public UnknownSoftDependencyException(Throwable throwable) {
		this(throwable, "Unknown Soft Dependency");
	}

	public UnknownSoftDependencyException(final String message) {
		this(null, message);
	}

	public UnknownSoftDependencyException(final Throwable throwable, final String message) {
		super(throwable, message);
	}

}
