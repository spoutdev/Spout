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
package org.spout.engine.command;

import java.util.HashSet;
import java.util.Set;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.event.EventManager;
import org.spout.api.event.player.PlayerBanKickEvent;
import org.spout.api.event.server.BanChangeEvent;
import org.spout.api.exception.CommandException;
import org.spout.api.entity.Player;
import org.spout.api.plugin.Platform;

import org.spout.engine.SpoutEngine;
import org.spout.engine.SpoutServer;

/**
 * Commands which handle banning and kicking
 */
public class BanNestedCommands {

	private final EventManager eventManager = Spout.getEventManager();
	private final SpoutEngine engine;
	private SpoutServer server = null;
	private final Set<String> ILLEGAL_IPS = new HashSet<String>() {{
		add("127.0.0.1"); add("0.0.0.0"); add("255.255.255.255"); }};

	public BanNestedCommands(SpoutEngine engine) {
		this.engine = engine;
		if (Spout.getPlatform() == Platform.SERVER){
			server = (SpoutServer) Spout.getEngine();
		}
	}

	@Command(aliases = {"ip","-i"},  usage = "<IP|OnlinePlayer> [message]", desc = "Ban or Unban a player via his IP", min = 1, max = -1)
	@CommandPermissions("spout.command.ban.ip")
	public void onBanIpCommand(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER) {
			source.sendMessage(ChatStyle.RED, "Ban IP is available only in server-mode.");
			return;
		}
		String ip = args.getString(0);
		ChatArguments message;
		if (args.length() >= 2) {
			message = args.getJoinedString(1);
		} else {
			message = new ChatArguments("You have been banned from the server.");
		}
		// let's check if the ip is really an ip or the player
		Player player = Spout.getEngine().getPlayer(ip, true);
		if (player == null) {
			Player[] onlinePlayers = Spout.getEngine().getOnlinePlayers();
			for (Player checkPlayer : onlinePlayers){
				if (checkPlayer != null) {
					if (checkPlayer.getAddress().getHostAddress().equals(ip)){
						player = checkPlayer;
						break;
					}
				}
			}
		}  else {
			ip = player.getAddress().getHostAddress();
		}
		// do some sanity check of the ip
		if (ILLEGAL_IPS.contains(ip)){
			throw new CommandException("Illegal IP " + ip);
		}
		// trigger PlayerBanKickEvent if we have a player
		if (player != null){
			PlayerBanKickEvent playerBanKickEvent = eventManager.callEvent(new PlayerBanKickEvent(player, BanChangeEvent.BanType.IP,message.asString()));
			if (playerBanKickEvent.isCancelled()){
				source.sendMessage("Ban IP was cancelled by a plugin");
				return;
			}
			message = new ChatArguments(playerBanKickEvent.getMessage());
		}
		// check if the ip is already banned
		boolean isAlreadyBanned =  server.getBanManager().isIpBanned(ip);
		server.getBanManager().setIpBanned(ip,!isAlreadyBanned);
		// IP wasn't banned so we send a message to the source and kick the player if he exists and is online
		if (!isAlreadyBanned){
			if (player != null){
				if (player.isOnline()) {
					player.kick(message);
					ChatArguments retMsg = new ChatArguments(ChatStyle.BRIGHT_GREEN, "Kicked player '", player.getName(), "'");
					if (!message.getPlainString().isEmpty()) {
						retMsg.append(" for reason '").append(message).append("'");
					}
					source.sendMessage(retMsg);
				}
			}
			source.sendMessage("IP ", ip, " was banned.");
		} else {
			source.sendMessage("IP ", ip, " was unbanned.");
		}
	}

	@Command(aliases = {"player","-p"},  usage = "<Player> [message]", desc = "Ban or Unban a player", min = 1, max = -1)
	@CommandPermissions("spout.command.ban.player")
	public void onBanPlayerCommand(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER) {
			source.sendMessage(ChatStyle.RED, "Ban Player is available only in server-mode.");
			return;
		}
		String playerName = args.getString(0);
		Player player = Spout.getEngine().getPlayer(playerName, true);
		if (player == null) {
			throw new CommandException("Unknown player: " + playerName);
		}
		ChatArguments message;
		if (args.length() >= 2) {
			message = args.getJoinedString(1);
		} else {
			message = new ChatArguments("You have been banned from the server.");
		}
		// trigger PlayerBanKickEvent
		PlayerBanKickEvent playerBanKickEvent = eventManager.callEvent(new PlayerBanKickEvent(player, BanChangeEvent.BanType.PLAYER,message.asString()));
		if (playerBanKickEvent.isCancelled()){
			source.sendMessage("Ban Player was cancelled by a plugin");
			return;
		}
		message = new ChatArguments(playerBanKickEvent.getMessage());
		// check if the player is already banned
		boolean isAlreadyBanned =  server.getBanManager().isBanned(playerName);
		server.getBanManager().setBanned(playerName,!isAlreadyBanned);
		// Player wasn't banned so we send a message to the source and kick the player if he exists and is online
		if (!isAlreadyBanned){
			if (player.isOnline()) {
				player.kick(message);
				ChatArguments retMsg = new ChatArguments(ChatStyle.BRIGHT_GREEN, "Kicked player '", player.getName(), "'");
				if (!message.getPlainString().isEmpty()) {
					retMsg.append(" for reason '").append(message).append("'");
				}
				source.sendMessage(retMsg);
			}
			source.sendMessage("Player ", playerName, " was banned.");
		} else {
			source.sendMessage("Player ", playerName, " was unbanned.");
		}
	}

	@Command(aliases = {"whitelist","-w"},  usage = "<Player> [message]", desc = "Whitelist or unwhitelist a player", min = 1, max = -1)
	@CommandPermissions("spout.command.ban.whitelist")
	public void onWhitelistPlayerCommand(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getPlatform() != Platform.SERVER) {
			source.sendMessage(ChatStyle.RED, "Ban Whitelist is available only in server-mode.");
			return;
		}
		String playerName = args.getString(0);
		Player player = Spout.getEngine().getPlayer(playerName, true);
		if (player == null) {
			throw new CommandException("Unknown player: " + playerName);
		}
		ChatArguments message;
		if (args.length() >= 2) {
			message = args.getJoinedString(1);
		} else {
			message = new ChatArguments("You have been whitelisted on the server.");
		}
		// trigger PlayerBanKickEvent
		PlayerBanKickEvent playerBanKickEvent = eventManager.callEvent(new PlayerBanKickEvent(player, BanChangeEvent.BanType.WHITELIST,message.asString()));
		if (playerBanKickEvent.isCancelled()){
			source.sendMessage("Ban Whitelist was cancelled by a plugin");
			return;
		}
		message = new ChatArguments(playerBanKickEvent.getMessage());
		// check if the player is already whitelisted
		boolean isAlreadyWhitelisted =  server.getBanManager().isWhitelisted(playerName);
		server.getBanManager().setWhitelisted(playerName,!isAlreadyWhitelisted);
		// Player is already whitelisted so we send a message to the source and kick the player if he exists and is online
		if (isAlreadyWhitelisted){
			if (player.isOnline()) {
				player.kick(message);
				ChatArguments retMsg = new ChatArguments(ChatStyle.BRIGHT_GREEN, "Kicked player '", player.getName(), "'");
				if (!message.getPlainString().isEmpty()) {
					retMsg.append(" for reason '").append(message).append("'");
				}
				source.sendMessage(retMsg);
			}
			source.sendMessage("Player ", playerName, " was unwhitelisted.");
		} else {
			source.sendMessage("Player ", playerName, " was whitelisted.");
		}
	}
}

