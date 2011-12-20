package org.getspout.server.net;

import java.util.HashMap;
import java.util.Map;

import org.getspout.server.msg.Message;
import org.getspout.server.net.codec.ActivateItemCodec;
import org.getspout.server.net.codec.AnimateEntityCodec;
import org.getspout.server.net.codec.AttachEntityCodec;
import org.getspout.server.net.codec.BlockChangeCodec;
import org.getspout.server.net.codec.BlockPlacementCodec;
import org.getspout.server.net.codec.ChatCodec;
import org.getspout.server.net.codec.CloseWindowCodec;
import org.getspout.server.net.codec.CollectItemCodec;
import org.getspout.server.net.codec.CompressedChunkCodec;
import org.getspout.server.net.codec.CreateEntityCodec;
import org.getspout.server.net.codec.DestroyEntityCodec;
import org.getspout.server.net.codec.DiggingCodec;
import org.getspout.server.net.codec.EnchantItemCodec;
import org.getspout.server.net.codec.EntityActionCodec;
import org.getspout.server.net.codec.EntityEffectCodec;
import org.getspout.server.net.codec.EntityEquipmentCodec;
import org.getspout.server.net.codec.EntityInteractionCodec;
import org.getspout.server.net.codec.EntityMetadataCodec;
import org.getspout.server.net.codec.EntityRemoveEffectCodec;
import org.getspout.server.net.codec.EntityRotationCodec;
import org.getspout.server.net.codec.EntityStatusCodec;
import org.getspout.server.net.codec.EntityTeleportCodec;
import org.getspout.server.net.codec.EntityVelocityCodec;
import org.getspout.server.net.codec.ExperienceCodec;
import org.getspout.server.net.codec.ExperienceOrbCodec;
import org.getspout.server.net.codec.ExplosionCodec;
import org.getspout.server.net.codec.GroundCodec;
import org.getspout.server.net.codec.HandshakeCodec;
import org.getspout.server.net.codec.HealthCodec;
import org.getspout.server.net.codec.IdentificationCodec;
import org.getspout.server.net.codec.KickCodec;
import org.getspout.server.net.codec.LoadChunkCodec;
import org.getspout.server.net.codec.MapDataCodec;
import org.getspout.server.net.codec.MessageCodec;
import org.getspout.server.net.codec.MultiBlockChangeCodec;
import org.getspout.server.net.codec.OpenWindowCodec;
import org.getspout.server.net.codec.PingCodec;
import org.getspout.server.net.codec.PlayEffectCodec;
import org.getspout.server.net.codec.PlayNoteCodec;
import org.getspout.server.net.codec.PositionCodec;
import org.getspout.server.net.codec.PositionRotationCodec;
import org.getspout.server.net.codec.ProgressBarCodec;
import org.getspout.server.net.codec.QuickBarCodec;
import org.getspout.server.net.codec.RelativeEntityPositionCodec;
import org.getspout.server.net.codec.RelativeEntityPositionRotationCodec;
import org.getspout.server.net.codec.RespawnCodec;
import org.getspout.server.net.codec.RotationCodec;
import org.getspout.server.net.codec.ServerListPingCodec;
import org.getspout.server.net.codec.SetWindowSlotCodec;
import org.getspout.server.net.codec.SetWindowSlotsCodec;
import org.getspout.server.net.codec.SpawnItemCodec;
import org.getspout.server.net.codec.SpawnLightningStrikeCodec;
import org.getspout.server.net.codec.SpawnMobCodec;
import org.getspout.server.net.codec.SpawnPaintingCodec;
import org.getspout.server.net.codec.SpawnPlayerCodec;
import org.getspout.server.net.codec.SpawnPositionCodec;
import org.getspout.server.net.codec.SpawnVehicleCodec;
import org.getspout.server.net.codec.StateChangeCodec;
import org.getspout.server.net.codec.StatisticCodec;
import org.getspout.server.net.codec.TimeCodec;
import org.getspout.server.net.codec.TransactionCodec;
import org.getspout.server.net.codec.UpdateSignCodec;
import org.getspout.server.net.codec.UserListItemCodec;
import org.getspout.server.net.codec.WindowClickCodec;

/**
 * A class used to lookup message codecs.
 *
 * @author Graham Edgecombe
 */
public final class CodecLookupService {
	/**
	 * A table which maps opcodes to codecs. This is generally used to map
	 * incoming packets to a codec.
	 */
	private static final MessageCodec<?>[] opcodeTable = new MessageCodec<?>[256];

	/**
	 * A table which maps messages to codecs. This is generally used to map
	 * outgoing packets to a codec.
	 */
	private static final Map<Class<? extends Message>, MessageCodec<?>> classTable = new HashMap<Class<? extends Message>, MessageCodec<?>>();

	/**
	 * Populates the opcode and class tables with codecs.
	 */
	static {
		try {
			/* 0x00 */bind(PingCodec.class);
			/* 0x01 */bind(IdentificationCodec.class);
			/* 0x02 */bind(HandshakeCodec.class);
			/* 0x03 */bind(ChatCodec.class);
			/* 0x04 */bind(TimeCodec.class);
			/* 0x05 */bind(EntityEquipmentCodec.class);
			/* 0x06 */bind(SpawnPositionCodec.class);
			/* 0x07 */bind(EntityInteractionCodec.class);
			/* 0x08 */bind(HealthCodec.class);
			/* 0x09 */bind(RespawnCodec.class);
			/* 0x0A */bind(GroundCodec.class);
			/* 0x0B */bind(PositionCodec.class);
			/* 0x0C */bind(RotationCodec.class);
			/* 0x0D */bind(PositionRotationCodec.class);
			/* 0x0E */bind(DiggingCodec.class);
			/* 0x0F */bind(BlockPlacementCodec.class);
			/* 0x10 */bind(ActivateItemCodec.class);
			/* 0x12 */bind(AnimateEntityCodec.class);
			/* 0x13 */bind(EntityActionCodec.class);
			/* 0x14 */bind(SpawnPlayerCodec.class);
			/* 0x15 */bind(SpawnItemCodec.class);
			/* 0x16 */bind(CollectItemCodec.class);
			/* 0x17 */bind(SpawnVehicleCodec.class);
			/* 0x18 */bind(SpawnMobCodec.class);
			/* 0x19 */bind(SpawnPaintingCodec.class);
			/* 0x1A */bind(ExperienceOrbCodec.class);
			/* 0x1C */bind(EntityVelocityCodec.class);
			/* 0x1D */bind(DestroyEntityCodec.class);
			/* 0x1E */bind(CreateEntityCodec.class);
			/* 0x1F */bind(RelativeEntityPositionCodec.class);
			/* 0x20 */bind(EntityRotationCodec.class);
			/* 0x21 */bind(RelativeEntityPositionRotationCodec.class);
			/* 0x22 */bind(EntityTeleportCodec.class);
			/* 0x26 */bind(EntityStatusCodec.class);
			/* 0x27 */bind(AttachEntityCodec.class);
			/* 0x28 */bind(EntityMetadataCodec.class);
			/* 0x29 */bind(EntityEffectCodec.class);
			/* 0x2A */bind(EntityRemoveEffectCodec.class);
			/* 0x2B */bind(ExperienceCodec.class);
			/* 0x32 */bind(LoadChunkCodec.class);
			/* 0x33 */bind(CompressedChunkCodec.class);
			/* 0x34 */bind(MultiBlockChangeCodec.class);
			/* 0x35 */bind(BlockChangeCodec.class);
			/* 0x36 */bind(PlayNoteCodec.class);
			/* 0x3C */bind(ExplosionCodec.class);
			/* 0x3D */bind(PlayEffectCodec.class);
			/* 0x46 */bind(StateChangeCodec.class);
			/* 0x47 */bind(SpawnLightningStrikeCodec.class);
			/* 0x64 */bind(OpenWindowCodec.class);
			/* 0x65 */bind(CloseWindowCodec.class);
			/* 0x66 */bind(WindowClickCodec.class);
			/* 0x67 */bind(SetWindowSlotCodec.class);
			/* 0x68 */bind(SetWindowSlotsCodec.class);
			/* 0x69 */bind(ProgressBarCodec.class);
			/* 0x6A */bind(TransactionCodec.class);
			/* 0x6B */bind(QuickBarCodec.class);
			/* 0x6C */bind(EnchantItemCodec.class);
			/* 0x82 */bind(UpdateSignCodec.class);
			/* 0x83 */bind(MapDataCodec.class);
			/* 0xC8 */bind(StatisticCodec.class);
			/* 0xC9 */bind(UserListItemCodec.class);
			/* 0xFE */bind(ServerListPingCodec.class);
			/* 0xFF */bind(KickCodec.class);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Binds a codec by adding entries for it to the tables.
	 *
	 * @param clazz The codec's class.
	 * @param <T> The type of message.
	 * @param <C> The type of codec.
	 * @throws InstantiationException if the codec could not be instantiated.
	 * @throws IllegalAccessException if the codec could not be instantiated due
	 *             to an access violation.
	 */
	private static <T extends Message, C extends MessageCodec<T>> void bind(Class<C> clazz) throws InstantiationException, IllegalAccessException {
		MessageCodec<T> codec = clazz.newInstance();

		opcodeTable[codec.getOpcode()] = codec;
		classTable.put(codec.getType(), codec);
	}

	/**
	 * Finds a codec by opcode.
	 *
	 * @param opcode The opcode.
	 * @return The codec, or {@code null} if it could not be found.
	 */
	public static MessageCodec<?> find(int opcode) {
		return opcodeTable[opcode];
	}

	/**
	 * Finds a codec by message class.
	 *
	 * @param clazz The message class.
	 * @param <T> The type of message.
	 * @return The codec, or {@code null} if it could not be found.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Message> MessageCodec<T> find(Class<T> clazz) {
		return (MessageCodec<T>) classTable.get(clazz);
	}

	/**
	 * Default private constructor to prevent insantiation.
	 */
	private CodecLookupService() {
	}
}
