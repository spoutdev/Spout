package org.getspout.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleOperations;
import jline.ConsoleReader;
import jline.NullCompletor;
import jline.SimpleCompletor;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.getspout.api.Server;

import com.grahamedgecombe.jterminal.JTerminal;

/**
 * A meta-class to handle all logging and input-related console improvements.
 * Portions are heavily based on CraftBukkit.
 */
public final class ConsoleManager {
	private final Server server;

	private ConsoleReader reader;
	private ConsoleCommandThread thread;
	private final FancyConsoleHandler consoleHandler;
	private final RotatingFileHandler fileHandler;

	private JFrame jFrame = null;
	private JTerminal jTerminal = null;
	private JTextField jInput = null;

	private boolean running = true;
	private boolean jLine = false;

	public ConsoleManager(Server server, String mode) {
		this.server = server;

		if (mode.equalsIgnoreCase("gui")) {
			JTerminalListener listener = new JTerminalListener();

			jFrame = new JFrame("Spout");
			jTerminal = new JTerminal();
			jInput = new JTextField(80) {
				/**
				 *
				 */
				private static final long serialVersionUID = 620432435961476505L;

				@Override
				public void setBorder(Border border) {
				}
			};
			jInput.paint(null);
			jInput.setFont(new Font("Monospaced", Font.PLAIN, 12));
			jInput.setBackground(Color.BLACK);
			jInput.setForeground(Color.WHITE);
			jInput.setMargin(new Insets(0, 0, 0, 0));
			jInput.addKeyListener(listener);

			JLabel caret = new JLabel("> ");
			caret.setFont(new Font("Monospaced", Font.PLAIN, 12));
			caret.setForeground(Color.WHITE);

			JPanel ipanel = new JPanel();
			ipanel.add(caret, BorderLayout.WEST);
			ipanel.add(jInput, BorderLayout.EAST);
			ipanel.setBorder(BorderFactory.createEmptyBorder());
			ipanel.setBackground(Color.BLACK);
			ipanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			ipanel.setSize(jTerminal.getWidth(), ipanel.getHeight());

			jFrame.getContentPane().add(jTerminal, BorderLayout.NORTH);
			jFrame.getContentPane().add(ipanel, BorderLayout.SOUTH);
			jFrame.addWindowListener(listener);
			jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			jFrame.setLocationRelativeTo(null);
			jFrame.pack();
			jFrame.setVisible(true);
		} else if (mode.equalsIgnoreCase("jline")) {
			jLine = true;
		}

		consoleHandler = new FancyConsoleHandler();

		String logFile = server.getLogFile();
		if (new File(logFile).getParentFile() != null) {
			new File(logFile).getParentFile().mkdirs();
		}
		fileHandler = new RotatingFileHandler(logFile);

		consoleHandler.setFormatter(new DateOutputFormatter(new SimpleDateFormat("HH:mm:ss")));
		fileHandler.setFormatter(new DateOutputFormatter(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")));

		Logger logger = Logger.getLogger("");
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
		logger.addHandler(consoleHandler);
		logger.addHandler(fileHandler);

		try {
			reader = new ConsoleReader();
		} catch (IOException ex) {
			server.getLogger().log(Level.SEVERE, "Exception inintializing console reader: {0}", ex.getMessage());
			ex.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new ServerShutdownThread());

		System.setOut(new PrintStream(new LoggerOutputStream(Level.INFO), true));
		System.setErr(new PrintStream(new LoggerOutputStream(Level.SEVERE), true));
	}

	public void stop() {
		consoleHandler.flush();
		fileHandler.flush();
		fileHandler.close();
		running = false;
		if (jFrame != null) {
			jFrame.dispose();
		}
	}

	public void setupConsole() {
		thread = new ConsoleCommandThread();

		if (jTerminal == null) {
			thread.setDaemon(true);
			thread.start();
		}
	}

	public void refreshCommands() {
		for (Object c : new ArrayList(reader.getCompletors())) {
			reader.removeCompletor((Completor) c);
		}

		Completor[] list = new Completor[] {new SimpleCompletor(server.getAllCommands()), new NullCompletor()};
		reader.addCompletor(new ArgumentCompletor(list));
	}

	public String colorize(String string) {
		if (!string.contains("\u00A7")) {
			return string;
		} else if ((!jLine || !reader.getTerminal().isANSISupported()) && jTerminal == null) {
			return ChatColor.stripColor(string);
		} else {
			return string.replace(ChatColor.RED.toString(), "\033[1;31m").replace(ChatColor.YELLOW.toString(), "\033[1;33m").replace(ChatColor.GREEN.toString(), "\033[1;32m").replace(ChatColor.AQUA.toString(), "\033[1;36m").replace(ChatColor.BLUE.toString(), "\033[1;34m").replace(ChatColor.LIGHT_PURPLE.toString(), "\033[1;35m").replace(ChatColor.BLACK.toString(), "\033[0;0m").replace(ChatColor.DARK_GRAY.toString(), "\033[1;30m").replace(ChatColor.DARK_RED.toString(), "\033[0;31m").replace(ChatColor.GOLD.toString(), "\033[0;33m").replace(ChatColor.DARK_GREEN.toString(), "\033[0;32m").replace(ChatColor.DARK_AQUA.toString(), "\033[0;36m").replace(ChatColor.DARK_BLUE.toString(), "\033[0;34m").replace(ChatColor.DARK_PURPLE.toString(), "\033[0;35m").replace(ChatColor.GRAY.toString(), "\033[0;37m").replace(ChatColor.WHITE.toString(), "\033[1;37m") + "\033[0m";
		}
	}

	private class ConsoleCommandThread extends Thread {
		@Override
		public void run() {
			String command;
			while (running) {
				try {
					if (jLine) {
						command = reader.readLine(">", null);
					} else {
						command = reader.readLine();
					}

					if (command == null || command.trim().length() == 0) {
						continue;
					}

					((SpoutServer)server).queueTask(new CommandTask(command.trim()));
				} catch (CommandException ex) {
					System.out.println("Exception while executing command: " + ex.getMessage());
					ex.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private class ServerShutdownThread extends Thread {
		@Override
		public void run() {
			server.shutdown();
		}
	}

	private class CommandTask implements Runnable {
		private String command;

		public CommandTask(String command) {
			this.command = command;
		}

		@Override
		public void run() {
			if (command.equals("stop")) {
				server.shutdown();
			}
			/*command = EventFactory.onServerCommand(sender, command).getCommand();

			if (!server.dispatchCommand(sender, command)) {
				String firstword = command;
				if (command.indexOf(' ') >= 0) {
					firstword = command.substring(0, command.indexOf(' '));
				}

				System.out.println("Command not found: " + firstword);
			}*/
		}
	}

	// TODO - convert to command source
	private class ColoredCommandSender {
		//private final PermissibleBase perm = new PermissibleBase(this);

		public String getName() {
			return "CONSOLE";
		}

		//public void sendMessage(String text) {
		//	server.getLogger().info(text);
		//}

		//public boolean isOp() {
		//	return true;
		//}

		//public void setOp(boolean value) {
		//	throw new UnsupportedOperationException("Cannot change operator status of server console");
		//}

		//public Server getServer() {
		//	return server;
		//}

		//public boolean isPermissionSet(String name) {
		//	return perm.isPermissionSet(name);
		//}

		//public boolean isPermissionSet(Permission perm) {
		//	return this.perm.isPermissionSet(perm);
		//}

		//public boolean hasPermission(String name) {
		//	return perm.hasPermission(name);
		//}

		//public boolean hasPermission(Permission perm) {
		//	return this.perm.hasPermission(perm);
		//}

		//public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		//	return perm.addAttachment(plugin, name, value);
		//}

		//public PermissionAttachment addAttachment(Plugin plugin) {
		//	return perm.addAttachment(plugin);
		//}

		//public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		//	return perm.addAttachment(plugin, name, value, ticks);
		//}

		//public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		//	return perm.addAttachment(plugin, ticks);
		//}

		//public void removeAttachment(PermissionAttachment attachment) {
		//	perm.removeAttachment(attachment);
		//}

		//public void recalculatePermissions() {
		//	perm.recalculatePermissions();
		//}

		//public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		//	return perm.getEffectivePermissions();
		//}
	}

	private class LoggerOutputStream extends ByteArrayOutputStream {
		private final String separator = System.getProperty("line.separator");
		private final Level level;

		public LoggerOutputStream(Level level) {
			super();
			this.level = level;
		}

		@Override
		public synchronized void flush() throws IOException {
			super.flush();
			String record = this.toString();
			super.reset();

			if (record.length() > 0 && !record.equals(separator)) {
				server.getLogger().logp(level, "LoggerOutputStream", "log" + level, record);
			}
		}
	}

	private class FancyConsoleHandler extends ConsoleHandler {
		public FancyConsoleHandler() {
			if (jTerminal != null) {
				setOutputStream(new TerminalOutputStream());
			}
		}

		@Override
		public synchronized void flush() {
			try {
				if (jLine && jTerminal == null) {
					reader.printString(ConsoleOperations.RESET_LINE + "");
					reader.flushConsole();
					super.flush();
					try {
						reader.drawLine();
					} catch (Throwable ex) {
						reader.getCursorBuffer().clearBuffer();
					}
					reader.flushConsole();
				} else {
					super.flush();
				}
			} catch (IOException ex) {
				server.getLogger().severe("I/O exception flushing console output");
				ex.printStackTrace();
			}
		}
	}

	private class RotatingFileHandler extends StreamHandler {
		private final SimpleDateFormat date;
		private final String logFile;
		private String filename;

		public RotatingFileHandler(String logFile) {
			this.logFile = logFile;
			date = new SimpleDateFormat("yyyy-MM-dd");
			filename = calculateFilename();
			try {
				setOutputStream(new FileOutputStream(filename, true));
			} catch (FileNotFoundException ex) {
				server.getLogger().log(Level.SEVERE, "Unable to open {0} for writing: {1}", new Object[] {filename, ex.getMessage()});
				ex.printStackTrace();
			}
		}

		@Override
		public synchronized void flush() {
			if (!filename.equals(calculateFilename())) {
				filename = calculateFilename();
				server.getLogger().log(Level.INFO, "Log rotating to {0}...", filename);
				try {
					setOutputStream(new FileOutputStream(filename, true));
				} catch (FileNotFoundException ex) {
					server.getLogger().log(Level.SEVERE, "Unable to open {0} for writing: {1}", new Object[] {filename, ex.getMessage()});
					ex.printStackTrace();
				}
			}
			super.flush();
		}

		private String calculateFilename() {
			return logFile.replace("%D", date.format(new Date()));
		}
	}

	private class DateOutputFormatter extends Formatter {
		private final SimpleDateFormat date;

		public DateOutputFormatter(SimpleDateFormat date) {
			this.date = date;
		}

		@Override
		public String format(LogRecord record) {
			StringBuilder builder = new StringBuilder();

			builder.append(date.format(record.getMillis()));
			builder.append(" [");
			builder.append(record.getLevel().getLocalizedName().toUpperCase());
			builder.append("] ");
			builder.append(colorize(formatMessage(record)));
			builder.append('\n');

			if (record.getThrown() != null) {
				StringWriter writer = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(writer));
				builder.append(writer.toString());
			}

			return builder.toString();
		}
	}

	private class JTerminalListener implements WindowListener, KeyListener {
		@Override
		public void windowOpened(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowActivated(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void windowClosing(WindowEvent e) {
			server.shutdown();
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == '\n') {
				String command = jInput.getText().trim();
				if (command.length() > 0) {
					((SpoutServer)server).queueTask(new CommandTask(command));
				}
				jInput.setText("");
			}
		}
	}

	private class TerminalOutputStream extends ByteArrayOutputStream {
		private final String separator = System.getProperty("line.separator");

		@Override
		public synchronized void flush() throws IOException {
			super.flush();
			String record = this.toString();
			super.reset();

			if (record.length() > 0 && !record.equals(separator)) {
				jTerminal.print(record);
				jFrame.repaint();
			}
		}
	}
}

