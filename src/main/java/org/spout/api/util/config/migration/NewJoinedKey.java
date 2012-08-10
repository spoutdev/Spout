package org.spout.api.util.config.migration;

import org.apache.commons.lang3.StringUtils;
import org.spout.api.util.config.Configuration;

/**
 * This implementation of MigrationAction converts configuration keys based on predefined
 * stringpath patterns where % is replaced by the old key. The stringpath pattern is split by the configuration's path separator
 *
 * @see org.spout.api.util.config.Configuration#getPathSeparator()
 */
public final class NewJoinedKey implements MigrationAction {
	private final String newKeyPattern;
	private final Configuration config;

	public NewJoinedKey(String newKeyPattern, Configuration config) {
		this.newKeyPattern = newKeyPattern;
		this.config = config;
	}

	public String[] convertKey(String[] key) {
		String oldKey = StringUtils.join(key, config.getPathSeparator());
		return config.getPathSeparatorPattern().split(newKeyPattern.replace("%", oldKey));
	}

	public Object convertValue(Object value) {
		return value;
	}
}
