package org.spout.api.util.config.migration;

/**
 * This implementation of MigrationAction changes the key of a configuration value to a predefined new key
 */
public final class NewKey implements MigrationAction {
	private final String[] newKey;

	public NewKey(String... key) {
		this.newKey = key;
	}

	public String[] convertKey(String[] key) {
		return newKey;
	}

	public Object convertValue(Object value) {
		return value;
	}
}
