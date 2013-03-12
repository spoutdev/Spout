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
	 * Creates a rotating file handler with the specified date handler.
	 * Use %D for a placeholder for the date in the log name.
	 * 
	 * @param logDir the directory to create logs in
	 * @param fileNameFormat
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
		} catch (IOException ignore) {}
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
				} catch (IOException ignore) { }
				initImpl();
			}
			try {
				writer.flush();
			} catch (IOException ignore) {}
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
			appendDateFormat(writer);
			String message;
			Formatter formatter = getFormatter();
			if (formatter != null) {
				message = formatter.format(record);
			} else {
				message = record.getMessage();
			}
			for (String line :message.split("\n")) {
				writer.write(line);
				writer.write('\n');
			}

			if (autoFlush) {
				flush();
			}
		} catch (IOException e) {
			return;
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
