/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.logging.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * A formatted rotating file handler, with separate files for each day of logs
 */
public class RotatingFileHandler extends Handler {
	private final SimpleDateFormat date;
	private final String fileNameFormat;
	private final File logDir;
	private final boolean autoFlush;
	private String logFileName;
	private final LogFlushThread logFlush;
	private final ReentrantLock writerLock = new ReentrantLock();
	private OutputStreamWriter writer;
	private DateFormat dateFormat;
	private final AtomicBoolean closed = new AtomicBoolean(false);

	/**
	 * Creates a rotating file handler with the specified date handler. Use %D for a placeholder for the date in the log name.
	 *
	 * @param logDir the directory to create logs in
	 * @param autoFlush whether to automatically flush after every log record
	 */
	public RotatingFileHandler(File logDir, String fileNameFormat, boolean autoFlush) {
		this.logDir = logDir;
		this.fileNameFormat = fileNameFormat;
		this.autoFlush = autoFlush;
		setDateFormat(new SimpleDateFormat("HH:mm:ss"));
		date = new SimpleDateFormat("yyyy-MM-dd");
		logFileName = calculateFilename();
		initImpl();
		logFlush = new LogFlushThread();
		logFlush.start();
	}

	private File getLogFile() {
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		return new File(logDir, logFileName);
	}

	private String calculateFilename() {
		return fileNameFormat.replace("%D", date.format(new Date()));
	}

	private void initImpl() {
		File logFile = getLogFile();
		try {
			writer = new OutputStreamWriter(new FileOutputStream(logFile, true));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Unable to write to " + logFile.getName() + " at " + logFile.getPath());
		}
	}

	@Override
	public void close() throws SecurityException {
		if (closed.compareAndSet(false, true)) {
			closeImpl();
		}
	}

	private void closeImpl() {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (logFlush != null) {
			logFlush.interrupt();
		}
	}

	@Override
	public void flush() {
		writerLock.lock();
		try {
			if (!logFileName.equals(calculateFilename())) {
				logFileName = calculateFilename();
				try {
					writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				initImpl();
			}
			try {
				writer.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} finally {
			writerLock.unlock();
		}
	}

	public void setDateFormat(DateFormat format) {
		this.dateFormat = format;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}

	@Override
	public void publish(LogRecord record) {
		writerLock.lock();
		try {
			if (writer == null) {
				return;
			}
			String message;
			Formatter formatter = getFormatter();
			if (formatter != null) {
				message = formatter.format(record);
			} else {
				message = record.getMessage();
			}
			for (String line : message.split("\n")) {
				appendDateFormat(writer);
				writer.write(line);
				writer.write('\n');
			}

			if (autoFlush) {
				flush();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			writerLock.unlock();
		}
	}

	private void appendDateFormat(Writer writer) throws IOException {
		DateFormat dateFormat = getDateFormat();
		if (dateFormat != null) {
			writer.write("[");
			writer.write(dateFormat.format(new Date()));
			writer.write("] ");
		}
	}

	private class LogFlushThread extends Thread {
		public LogFlushThread() {
			super("Log Flush Thread");
			this.setDaemon(true);
		}

		@Override
		public void run() {
			while (!this.isInterrupted()) {
				flush();
				try {
					sleep(60000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
