package net.minecraft.src;

import java.io.File;
import java.util.Random;
import net.minecraft.src.CodecMus;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.GameSettings;
import net.minecraft.src.MathHelper;
import net.minecraft.src.SoundPool;
import net.minecraft.src.SoundPoolEntry;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

//BukkitContrib Start
import paulscode.sound.CommandObject;
import paulscode.sound.FilenameURL;
import org.getspout.spout.packet.*;
import org.getspout.spout.sound.Music;
import org.getspout.spout.sound.SoundEffect;
//BukkitContrib End

public class SoundManager {

	private static SoundSystem sndSystem;
	private SoundPool soundPoolSounds = new SoundPool();
	private SoundPool soundPoolStreaming = new SoundPool();
	private SoundPool soundPoolMusic = new SoundPool();
	private int field_587_e = 0;
	private GameSettings options;
	private static boolean loaded = false;
	private Random rand = new Random();
	private int ticksBeforeMusic;


	public SoundManager() {
		this.ticksBeforeMusic = this.rand.nextInt(12000);
	}

	public void loadSoundSettings(GameSettings var1) {
		this.soundPoolStreaming.field_1657_b = false;
		this.options = var1;
		if(!loaded && (var1 == null || var1.soundVolume != 0.0F || var1.musicVolume != 0.0F)) {
			this.tryToSetLibraryAndCodecs();
		}

	}

	private void tryToSetLibraryAndCodecs() {
		try {
			float var1 = this.options.soundVolume;
			float var2 = this.options.musicVolume;
			this.options.soundVolume = 0.0F;
			this.options.musicVolume = 0.0F;
			this.options.saveOptions();
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("mus", CodecMus.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			sndSystem = new SoundSystem();
			this.options.soundVolume = var1;
			this.options.musicVolume = var2;
			this.options.saveOptions();
		} catch (Throwable var3) {
			var3.printStackTrace();
			System.err.println("error linking with the LibraryJavaSound plug-in");
		}

		loaded = true;
	}

	public void onSoundOptionsChanged() {
		if(!loaded && (this.options.soundVolume != 0.0F || this.options.musicVolume != 0.0F)) {
			this.tryToSetLibraryAndCodecs();
		}

		if(loaded) {
			if(this.options.musicVolume == 0.0F) {
				sndSystem.stop("BgMusic");
			} else {
				sndSystem.setVolume("BgMusic", this.options.musicVolume);
			}
		}

	}

	public void closeMinecraft() {
		if(loaded) {
			sndSystem.cleanup();
		}

	}

	public void addSound(String var1, File var2) {
		this.soundPoolSounds.addSound(var1, var2);
	}

	public void addStreaming(String var1, File var2) {
		this.soundPoolStreaming.addSound(var1, var2);
	}

	public void addMusic(String var1, File var2) {
		this.soundPoolMusic.addSound(var1, var2);
	}

	public void playRandomMusicIfReady() {
		if(loaded && this.options.musicVolume != 0.0F) {
			if(!sndSystem.playing("BgMusic") && !sndSystem.playing("streaming")) {
				if(this.ticksBeforeMusic > 0) {
					--this.ticksBeforeMusic;
					return;
				}

				SoundPoolEntry var1 = this.soundPoolMusic.getRandomSound();
				if(var1 != null) {
					//BukkitContrib start
					if (BukkitContrib.getVersion() > 7) {
						EntityPlayer player = BukkitContrib.getGameInstance().thePlayer;
						if (player instanceof EntityClientPlayerMP) {
							if (waitingSound == null) {
								Music music = Music.getMusicFromName(var1.soundName);
								if (music != null) {
									waitingSound = var1;
									((EntityClientPlayerMP)player).sendQueue.addToSendQueue(new CustomPacket(new PacketMusicChange(music.getId(), (int)options.musicVolume * 100)));
									return;
								}
							}
							else if (allowed) {
								var1 = waitingSound;
								waitingSound = null;
								allowed = false;
								cancelled = false;
							}
							else if (cancelled) {
								var1 = null;
								allowed = false;
								cancelled = false;
								ticksBeforeMusic = rand.nextInt(12000) + 12000;
								return;
							}
							else {
								return;
							}
						}
					}
					//BukkitContrib end
					this.ticksBeforeMusic = this.rand.nextInt(12000) + 12000;
					sndSystem.backgroundMusic("BgMusic", var1.soundUrl, var1.soundName, false);
					sndSystem.setVolume("BgMusic", this.options.musicVolume);
					sndSystem.play("BgMusic");
				}
			}

		}
	}

	public void func_338_a(EntityLiving var1, float var2) {
		if(loaded && this.options.soundVolume != 0.0F) {
			if(var1 != null) {
				float var3 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2;
				double var4 = var1.prevPosX + (var1.posX - var1.prevPosX) * (double)var2;
				double var6 = var1.prevPosY + (var1.posY - var1.prevPosY) * (double)var2;
				double var8 = var1.prevPosZ + (var1.posZ - var1.prevPosZ) * (double)var2;
				float var10 = MathHelper.cos(-var3 * 0.017453292F - 3.1415927F);
				float var11 = MathHelper.sin(-var3 * 0.017453292F - 3.1415927F);
				float var12 = -var11;
				float var13 = 0.0F;
				float var14 = -var10;
				float var15 = 0.0F;
				float var16 = 1.0F;
				float var17 = 0.0F;
				sndSystem.setListenerPosition((float)var4, (float)var6, (float)var8);
				sndSystem.setListenerOrientation(var12, var13, var14, var15, var16, var17);
			}
		}
	}

	public void playStreaming(String var1, float var2, float var3, float var4, float var5, float var6) {
		if(loaded && this.options.soundVolume != 0.0F) {
			String var7 = "streaming";
			if(sndSystem.playing("streaming")) {
				sndSystem.stop("streaming");
			}

			if(var1 != null) {
				SoundPoolEntry var8 = this.soundPoolStreaming.getRandomSoundFromSoundPool(var1);
				if(var8 != null && var5 > 0.0F) {
					if(sndSystem.playing("BgMusic")) {
						sndSystem.stop("BgMusic");
					}

					float var9 = 16.0F;
					sndSystem.newStreamingSource(true, var7, var8.soundUrl, var8.soundName, false, var2, var3, var4, 2, var9 * 4.0F);
					sndSystem.setVolume(var7, 0.5F * this.options.soundVolume);
					sndSystem.play(var7);
				}

			}
		}
	}
	//BukkitContrib start
	public void playSound(String s, float f, float f1, float f2, float f3, float f4) {
		playSound(s, f, f1, f2, f3, f4, -1, 1.0F);
	}
	
	public void playSound(String s, float f, float f1, float f2, float f3, float f4, int soundId, float volume)
	{
		if(!loaded || options.soundVolume == 0.0F)
		{
			return;
		}
		SoundPoolEntry soundpoolentry = soundPoolSounds.getRandomSoundFromSoundPool(s);
		if(soundpoolentry != null && f3 > 0.0F)
		{
			field_587_e = (field_587_e + 1) % 256;
			String s1;
			if (soundId == -1) s1 = (new StringBuilder()).append("sound_").append(field_587_e).toString();
			else s1 = (new StringBuilder()).append("sound_").append(soundId).toString();
			float f5 = 16F;
			if(f3 > 1.0F)
			{
				f5 *= f3;
			}
			sndSystem.newSource(f3 > 1.0F, s1, soundpoolentry.soundUrl, soundpoolentry.soundName, false, f, f1, f2, 2, f5);
			sndSystem.setPitch(s1, f4);
			if(f3 > 1.0F)
			{
				f3 = 1.0F;
			}
			f3 *= volume;
			sndSystem.setVolume(s1, f3 * options.soundVolume);
			sndSystem.play(s1);
		}
	}

	public void playSoundFX(String s, float f, float f1) {
		playSoundFX(s, f, f1, -1, 1.0F);
	}

	public void playSoundFX(String s, float f, float f1, int soundId, float volume)
	{
		if(!loaded || options.soundVolume == 0.0F)
		{
			return;
		}
		SoundPoolEntry soundpoolentry = soundPoolSounds.getRandomSoundFromSoundPool(s);
		if(soundpoolentry != null)
		{
			field_587_e = (field_587_e + 1) % 256;
			String s1;
			if (soundId == -1) s1 = (new StringBuilder()).append("sound_").append(field_587_e).toString();
			else s1 = (new StringBuilder()).append("sound_").append(soundId).toString();
			sndSystem.newSource(false, s1, soundpoolentry.soundUrl, soundpoolentry.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
			if(f > 1.0F)
			{
				f = 1.0F;
			}
			f *= 0.25F;
			f *= volume;
			sndSystem.setPitch(s1, f1);
			sndSystem.setVolume(s1, f * options.soundVolume);
			sndSystem.play(s1);
		}
	}
	
	public void playMusic(String music, int id, float volume) {
		playMusic(music, id, 0, 0, 0, volume, 0F);
	}
	
	public void playMusic(String music, int id, int x, int y, int z, float volume, float distance) {
		if(!loaded || options.musicVolume == 0.0F)
		{
			return;
		}
		stopMusic();
		SoundPoolEntry soundpoolentry = soundPoolMusic.getSoundFromSoundPool(music, id);
		if(soundpoolentry != null) {
			ticksBeforeMusic = rand.nextInt(12000) + 12000;
			if (distance > 0F) {
				sndSystem.removeSource("BgMusic");
				sndSystem.newStreamingSource(false, "BgMusic", soundpoolentry.soundUrl, soundpoolentry.soundName, false, x, y, z, 2, distance);
			}
			else {
				sndSystem.backgroundMusic("BgMusic", soundpoolentry.soundUrl, soundpoolentry.soundName, false);
			}
			sndSystem.setVolume("BgMusic", options.musicVolume * volume);
			sndSystem.play("BgMusic");
		}
	}

	public void playCustomSoundEffect(String effect, float volume) {
		playCustomSoundEffect(effect, 0, 0, 0, volume, 0F);
	}
	
	public void playCustomSoundEffect(String music, int x, int y, int z, float volume, float distance) {
		if(!loaded || options.soundVolume == 0.0F)
		{
			return;
		}
		SoundPoolEntry soundpoolentry = soundPoolSounds.getRandomSoundFromSoundPool(music);
		if (soundpoolentry != null) {
			String source;
			if (distance > 0F) {
				source = sndSystem.quickStream(false, soundpoolentry.soundUrl, soundpoolentry.soundName, false, x, y, z, 2, distance);
			}
			else {
				source = sndSystem.quickStream(false, soundpoolentry.soundUrl, soundpoolentry.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
			}
			sndSystem.setVolume(source, volume * options.soundVolume);
			sndSystem.play(source);
		}
	}
	
	public void preload(String music) {
		sndSystem.loadSound(music);
	}
	
	public boolean hasMusic(String sound, int id) {
		return soundPoolMusic.getSoundFromSoundPool(sound, id) != null;
	}
	
	public boolean hasSoundEffect(String sound, int id) {
		return soundPoolSounds.getSoundFromSoundPool(sound, id) != null;
	}
	
	public void addCustomSoundEffect(String sound, File song) {
		soundPoolSounds.addCustomSound(sound, song);
	}
	
	 public void addCustomMusic(String sound, File song) {
		soundPoolMusic.addCustomSound(sound, song);
	}
	
	public void stopMusic() {
		if (sndSystem != null) {
			if(sndSystem.playing("BgMusic")) {
				sndSystem.stop("BgMusic");
			}
			if(sndSystem.playing("streaming")){
				sndSystem.stop("streaming");
			}
		}
	}
	
	public void fadeOut(int time){
		if(sndSystem.playing("BgMusic")) {
			sndSystem.fadeOut("BgMusic", null, time);
		}
		if(sndSystem.playing("streaming")){
			sndSystem.fadeOut("streaming", null, time);
		}
	}
	
	public void resetTime() {
		ticksBeforeMusic = rand.nextInt(12000) + 12000;
	}
	
	public SoundPoolEntry waitingSound = null;
	public boolean allowed = false;
	public boolean cancelled = false;
	//BukkitContrib end
}
