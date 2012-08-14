/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.chat.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.engine.SpoutEngine;

/**
 * A file-outputting console
 */
public class FileConsole extends AbstractConsole {
	private final SimpleDateFormat date;
	private final SpoutEngine engine;
	private final String fileNameFormat;
	private String logFileName;
	private File logFile;
	private LogFlushThread logFlush;
	private final ReentrantLock writerLock = new ReentrantLock();
	private OutputStreamWriter writer;

	public FileConsole(SpoutEngine engine) {
		this.engine = engine;
		this.fileNameFormat = engine.getLogFile();
		setDateFormat(new SimpleDateFormat("HH:mm:ss"));
		date = new SimpleDateFormat("yyyy-MM-dd");
		logFileName = calculateFilename();

		logFile = new File(logFileName);
		if (logFile.getParentFile() != null) {
			logFile.getParentFile().mkdirs();
		}
	}
	private String calculateFilename() {
		return fileNameFormat.replace("%D", date.format(new Date()));
	}

	protected void initImpl() {
		logFlush = new LogFlushThread();
		logFlush.start();
		try {
			writer = new OutputStreamWriter(new FileOutputStream(logFile, true));
		} catch (FileNotFoundException ex) {
			engine.getLogger().log(Level.SEVERE, "Unable to open {0} for writing: {1}", new Object[]{logFileName, ex.getMessage()});
			ex.printStackTrace();
		}
	}

	protected void closeImpl() {
		try {
			writer.close();
		} catch (IOException ignore) {
		}
		logFlush.interrupt();
	}

	protected void flush() {
		writerLock.lock();
		try {
			if (!logFileName.equals(calculateFilename())) {
				logFileName = calculateFilename();
				logFile = new File(logFileName);
				engine.getLogger().log(Level.INFO, "Log rotating to {0}...", logFileName);
				try {
					writer.close();
					writer = new OutputStreamWriter(new FileOutputStream(logFile, true));
				} catch (FileNotFoundException ex) {
					engine.getLogger().log(Level.SEVERE, "Unable to open {0} for writing: {1}", new Object[]{logFileName, ex.getMessage()});
					ex.printStackTrace();
				} catch (IOException ignore) {
				}
			}
			try {
				writer.flush();
			} catch (IOException ignore) {
			}
		} finally {
			writerLock.unlock();
		}
	}

	public void addMessage(ChatArguments message) {
		writerLock.lock();
		try {
			if (writer == null) {
				return;
			}
			appendDateFormat(writer);
			for (String line : message.asString().split("\n")) {
				writer.write(line);
				writer.write('\n');
			}
		} catch (IOException e) {
			return;
		} finally {
			writerLock.unlock();
		}

		if (Spout.debugMode()) {
			flush();
		}
	}

	private class LogFlushThread extends Thread {
		public LogFlushThread() {
			super("Log Flush Thread");
			this.setDaemon(true);
		}

		@Override
		public void run() {
			while(!this.isInterrupted()) {
				flush();
				try {
					sleep(60000);
				} catch (InterruptedException e) { }
			}
		}
	}
}
