package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.*;
import org.getspout.sound.Music;
import org.getspout.sound.SoundEffect;

public class PacketPlaySound implements BukkitContribPacket{
	short soundId;
	boolean location = false;
	int x, y, z;
	int volume, intensity;
	
	public PacketPlaySound() {
		
	}

	@Override
	public int getNumBytes() {
		return 23;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		soundId = input.readShort();
		location = input.readBoolean();
		x = input.readInt();
		y = input.readInt();
		z = input.readInt();
		intensity = input.readInt();
		volume = input.readInt();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeShort(soundId);
		output.writeBoolean(location);
		if (!location) {
				output.writeInt(-1);
				output.writeInt(-1);
				output.writeInt(-1);
				output.writeInt(-1);
		}
		else {
				output.writeInt(x);
				output.writeInt(y);
				output.writeInt(z);
				output.writeInt(intensity);
		}
		output.writeInt(volume);
	}

	@Override
	public void run(int entityId) {
		EntityPlayer e = BukkitContrib.getPlayerFromId(entityId);
		if (e != null) {
				SoundManager sndManager = BukkitContrib.getGameInstance().sndManager;
				if (soundId > -1 && soundId <= SoundEffect.getMaxId()) {
					SoundEffect effect = SoundEffect.getSoundEffectFromId(soundId);
					if (!location) {
						sndManager.playSoundFX(effect.getName(), 0.5F, 0.7F, effect.getSoundId(), volume / 100F);
					}
					else {
						sndManager.playSound(effect.getName(), x, y, z, 0.5F, (intensity / 16F), effect.getSoundId(), volume / 100F);
					}
				}
				soundId -= (1 + SoundEffect.getMaxId());
				System.out.println(soundId);
				if (soundId > -1 && soundId <= Music.getMaxId()) {
					Music music = Music.getMusicFromId(soundId);
					sndManager.playMusic(music.getName(), music.getSoundId(), volume / 100F);
				}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketPlaySound;
	}

}
