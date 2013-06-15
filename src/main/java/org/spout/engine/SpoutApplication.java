/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine;

import java.io.File;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.render.RenderMode;

import org.spout.engine.util.argument.PlatformConverter;
import org.spout.engine.util.argument.RenderModeConverter;

/**
 * A main class for launching various platforms
 */
public class SpoutApplication {
	@Parameter(names = {"--platform", "-platform", "--p", "-p"}, converter = PlatformConverter.class)
	public Platform platform = Platform.SERVER;
	@Parameter(names = {"--debug", "-debug", "--d", "-d"}, description = "Debug Mode")
	public boolean debug = false;
	@Parameter(names = {"--rendermode", "-rendermode", "--r", "-r"}, converter = RenderModeConverter.class, description = "Render Version.  Versions: GL11, GL20, GL30, GL40, GLES20")
	RenderMode renderMode = RenderMode.GL30;
	@Parameter(names = {"--ccoverride"}, description = "Override ARB_CREATE_CONTEXT for the client")
	public boolean ccoverride = false;
	@Parameter(names = {"--path"}, description = "Override path for the client")
	public String path = null;
	@Parameter(names = {"--protocol"}, description = "Server to connect to")
	public String protocol = null;
	@Parameter(names = {"--server"}, description = "Server to connect to")
	public String server = null;
	@Parameter(names = {"--port"}, description = "Server to connect to")
	public int port = -1;
	@Parameter(names = {"--user"}, description = "User to connect as")
	public String user = null;

	public static void main(String[] args) {
		try {
			SpoutApplication main = new SpoutApplication();
			JCommander commands = new JCommander(main);
			commands.parse(args);
			if (main.path != null) {
				File dir = new File(main.path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}

			SpoutEngine engine;
			switch (main.platform) {
				case CLIENT:
					engine = new SpoutClient();
					break;
				case SERVER:
					engine = new SpoutServer();
					break;
				case PROXY:
					engine = new SpoutProxy();
					break;
				default:
					throw new IllegalArgumentException("Unknown platform: " + main.platform);
			}

			Spout.setEngine(engine);
			engine.getFileSystem().init();
			engine.init(main);
			engine.start();
		} catch (Throwable t) {
			t.printStackTrace();
			Runtime.getRuntime().halt(1);
		}
	}
}
