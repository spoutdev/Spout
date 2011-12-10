package net.glowstone.msg.handler;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.net.Session;

public final class PositionRotationMessageHandler extends MessageHandler<PositionRotationMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, PositionRotationMessage message) {
        if (player == null) {
            return;
        }

        float rot = (message.getRotation() - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        PlayerMoveEvent event = EventFactory.onPlayerMove(player, player.getLocation(), new Location(player.getWorld(), message.getX(), message.getY(), message.getZ(), rot, message.getPitch()));

        if (event.isCancelled()) {
            return;
        }

        player.setRawLocation(event.getTo());
    }

}
