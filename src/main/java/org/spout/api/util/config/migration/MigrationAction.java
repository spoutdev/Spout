package org.spout.api.util.config.migration;

/**
 * Represents the two sides of migrating an existing configuration key:
 * Converting the key and converting the value
 */
public interface MigrationAction {
	/**
	 * This method converts the old configuration key to its migrated value.
	 *
	 * @param key The existing configuration key
	 * @return The key modified to its new value
	 */
	public String[] convertKey(String[] key);
	/**
	 * This method converts the old configuration value to its migrated value.
	 *
	 * @param value The existing configuration value
	 * @return The value modified to its new value
	 */
	public Object convertValue(Object value);
}
