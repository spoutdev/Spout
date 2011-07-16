package net.minecraft.src;

import java.util.List;
import java.lang.String;
import net.minecraft.client.Minecraft;

public class ReconnectManager {
  static String hostName = null;
  static int portNum = -1;

  static public void detectKick(String infoString, String reason,
      Object[] objects) {
    portNum = -1;

    if (infoString == null || reason == null || objects == null) {
      return;
    }

    if (objects.length == 0 || !(objects[0] instanceof String))
      return;

    reason = (String) objects[0];

    if (infoString.contains("disconnect.disconnected")) {
      if (reason.indexOf("[Serverport]") == 0
          || reason.indexOf("[Redirect]") == 0) {
        String[] split = reason.split(":");
        if (split.length == 3) {
          hostName = split[1].trim();
          try {
            portNum = Integer.parseInt(split[2].trim());
          } catch (Exception e) {
            portNum = -1;
          }
        } else if (split.length == 2) {
          hostName = split[1].trim();
          portNum = 25565;
        }
      }
    }

  }

  static public void teleport(net.minecraft.client.Minecraft mc) {
    if (portNum != -1 && hostName != null) {
      mc.displayGuiScreen(new GuiConnecting(mc, hostName, portNum));
    }
  }

}

