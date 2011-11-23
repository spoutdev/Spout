package net.glowstone.msg.handler;

import org.bukkit.Location;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.RotationMessage;
import net.glowstone.net.Session;

public final class RotationMessageHandler extends MessageHandler<RotationMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, RotationMessage message) {
        if (player == null)
            return;

        Location loc = player.getLocation();
        loc.setYaw(message.getRotation());
        float rot = (message.getRotation() - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        loc.setPitch(rot);
        player.setRawLocation(loc);
    }

}
