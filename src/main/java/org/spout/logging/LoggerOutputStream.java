package org.spout.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerOutputStream extends ByteArrayOutputStream {
	private final String separator = System.getProperty("line.separator");
	private final Logger logger;
	private final Level level;

	public LoggerOutputStream(Logger logger, Level level) {
		super();
		this.logger = logger;
		this.level = level;
	}

	@Override
	public synchronized void flush() throws IOException {
		super.flush();
		String record = this.toString();
		super.reset();

		if (record.length() > 0 && !record.equals(separator)) {
			logger.logp(level, "LoggerOutputStream", "log" + level, record);
		}
	}
}