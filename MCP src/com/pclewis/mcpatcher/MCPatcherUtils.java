package com.pclewis.mcpatcher;

import java.io.Closeable;
import java.io.IOException;
import java.util.zip.ZipFile;

public class MCPatcherUtils {
	public static void close(Closeable var0) {
		if(var0 != null) {
			try {
				var0.close();
			} catch (IOException var2) {
				var2.printStackTrace();
			}
		}

	}

	public static void close(ZipFile var0) {
		if(var0 != null) {
			try {
				var0.close();
			} catch (IOException var2) {
				var2.printStackTrace();
			}
		}

	}
}
