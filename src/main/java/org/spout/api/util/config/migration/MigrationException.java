package org.spout.api.util.config.migration;

/**
 * This exception is thrown when an error occurs while migrating the config
 */
public class MigrationException extends Exception {
	public MigrationException(String message) {
		super(message);
	}

	public MigrationException(Throwable cause) {
		super(cause);
	}
}
