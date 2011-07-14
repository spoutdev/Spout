package org.bukkitcontrib.packet;

import java.util.HashMap;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet0KeepAlive;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet101CloseWindow;
import net.minecraft.server.Packet102WindowClick;
import net.minecraft.server.Packet103SetSlot;
import net.minecraft.server.Packet104WindowItems;
import net.minecraft.server.Packet105CraftProgressBar;
import net.minecraft.server.Packet106Transaction;
import net.minecraft.server.Packet10Flying;
import net.minecraft.server.Packet11PlayerPosition;
import net.minecraft.server.Packet12PlayerLook;
import net.minecraft.server.Packet130UpdateSign;
import net.minecraft.server.Packet131;
import net.minecraft.server.Packet13PlayerLookMove;
import net.minecraft.server.Packet14BlockDig;
import net.minecraft.server.Packet15Place;
import net.minecraft.server.Packet16BlockItemSwitch;
import net.minecraft.server.Packet17;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet19EntityAction;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.Packet200Statistic;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet21PickupSpawn;
import net.minecraft.server.Packet22Collect;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet255KickDisconnect;
import net.minecraft.server.Packet25EntityPainting;
import net.minecraft.server.Packet27;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet2Handshake;
import net.minecraft.server.Packet30Entity;
import net.minecraft.server.Packet31RelEntityMove;
import net.minecraft.server.Packet32EntityLook;
import net.minecraft.server.Packet33RelEntityMoveLook;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet39AttachEntity;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.Packet40EntityMetadata;
import net.minecraft.server.Packet4UpdateTime;
import net.minecraft.server.Packet50PreChunk;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.Packet52MultiBlockChange;
import net.minecraft.server.Packet53BlockChange;
import net.minecraft.server.Packet54PlayNoteBlock;
import net.minecraft.server.Packet5EntityEquipment;
import net.minecraft.server.Packet60Explosion;
import net.minecraft.server.Packet61;
import net.minecraft.server.Packet6SpawnPosition;
import net.minecraft.server.Packet70Bed;
import net.minecraft.server.Packet71Weather;
import net.minecraft.server.Packet7UseEntity;
import net.minecraft.server.Packet8UpdateHealth;
import net.minecraft.server.Packet9Respawn;

public enum CorePacketType {
	KEEP_ALIVE(0, Packet0KeepAlive.class),
	LOGIN(1, Packet1Login.class),
	HANDSHAKE(2, Packet2Handshake.class),
	CHAT(3, Packet3Chat.class),
	UPDATE_TIME(4, Packet4UpdateTime.class),
	ENTITY_EQUIPMENT(5, Packet5EntityEquipment.class),
	SPAWN_POSITION(6, Packet6SpawnPosition.class),
	USE_ENTITY(7, Packet7UseEntity.class),
	UPDATE_HEALTH(8, Packet8UpdateHealth.class),
	RESPAWN(9, Packet9Respawn.class),
	FLYING(10, Packet10Flying.class),
	PLAYER_POSITION(11, Packet11PlayerPosition.class),
	PLAYER_LOOK(12, Packet12PlayerLook.class),
	PLAYER_LOOK_MOVE(13, Packet13PlayerLookMove.class),
	BLOCK_DIG(14, Packet14BlockDig.class),
	PLACE(15, Packet15Place.class),
	BLOCK_ITEM_SWITCH(16, Packet16BlockItemSwitch.class),
	NEW_STATE(17, Packet17.class),
	ARM_ANIMATION(18, Packet18ArmAnimation.class),
	ENTITY_ACTION(19, Packet19EntityAction.class),
	NAMED_ENTITY_SPAWN(20, Packet20NamedEntitySpawn.class),
	PICKUP_SPAWN(21, Packet21PickupSpawn.class),
	COLLECT(22, Packet22Collect.class),
	VEHICLE_SPAWN(23, Packet23VehicleSpawn.class),
	MOB_SPAWN(24, Packet24MobSpawn.class),
	ENTITY_PAINTING(25, Packet25EntityPainting.class),
	STANCE_UPDATE(27, Packet27.class),
	ENTITY_VELOCITY(28, Packet28EntityVelocity.class),
	DESTROY_ENTITY(29, Packet29DestroyEntity.class),
	ENTITY(30, Packet30Entity.class),
	REL_ENTITY_MOVE(31, Packet31RelEntityMove.class),
	ENTITY_LOOK(32, Packet32EntityLook.class),
	REL_ENTITY_MOVE_LOOK(33, Packet33RelEntityMoveLook.class),
	ENTITY_TELEPORT(34, Packet34EntityTeleport.class),
	ENTITY_STATUS(38, Packet38EntityStatus.class),
	ATTACH_ENTITY(39, Packet39AttachEntity.class),
	ENTITY_METADATA(40, Packet40EntityMetadata.class),
	PRE_CHUNK(50, Packet50PreChunk.class),
	MAP_CHUNK(51, Packet51MapChunk.class),
	MULTI_BLOCK_CHANGE(52, Packet52MultiBlockChange.class),
	BLOCK_CHANGE(53, Packet53BlockChange.class),
	PLAY_NOTE_BLOCK(54, Packet54PlayNoteBlock.class),
	EXPLOSION(60, Packet60Explosion.class),
	SOUND_EFFECT(61, Packet61.class),
	BED(70, Packet70Bed.class),
	WEATHER(71, Packet71Weather.class),
	OPEN_WINDOW(100, Packet100OpenWindow.class),
	CLOSE_WINDOW(101, Packet101CloseWindow.class),
	WINDOW_CLICK(102, Packet102WindowClick.class),
	SET_SLOT(103, Packet103SetSlot.class),
	WINDOW_ITEMS(100, Packet104WindowItems.class),
	CRAFT_PROGRESS_BAR(100, Packet105CraftProgressBar.class),
	TRANSACTION(100, Packet106Transaction.class),
	UPDATE_SIGN(130, Packet130UpdateSign.class),
	MAP_DATA(131, Packet131.class),
	STATISTIC(200, Packet200Statistic.class),
	KICK_DISCONNECT(255, Packet255KickDisconnect.class),
	;

	private final int id;
	private final Class<? extends Packet> packetClass;
	private static final HashMap<Integer, CorePacketType> lookupId = new HashMap<Integer, CorePacketType>();
	CorePacketType(final int type, final Class<? extends Packet> packetClass) {
		id = type;
		this.packetClass = packetClass;
	}

	public int getId() {
		return id;
	}

	public Class<? extends Packet> getPacketClass() {
		return packetClass;
	}

	public static CorePacketType getPacketFromId(int id) {
		return lookupId.get(id);
	}

	static {
		for (CorePacketType packet : values()) {
			lookupId.put(packet.getId(), packet);
		}
	}
}
