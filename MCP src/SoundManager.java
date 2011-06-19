// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.File;
import java.io.PrintStream;
import java.util.Random;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

//BukkitContrib Start
import paulscode.sound.CommandObject;
import paulscode.sound.FilenameURL;
//BukkitContrib End

// Referenced classes of package net.minecraft.src:
//            SoundPool, GameSettings, CodecMus, SoundPoolEntry, 
//            EntityLiving, MathHelper

public class SoundManager
{

    public SoundManager()
    {
        soundPoolSounds = new SoundPool();
        soundPoolStreaming = new SoundPool();
        soundPoolMusic = new SoundPool();
        field_587_e = 0;
        rand = new Random();
        ticksBeforeMusic = rand.nextInt(12000);
    }

    public void loadSoundSettings(GameSettings gamesettings)
    {
        soundPoolStreaming.field_1657_b = false;
        options = gamesettings;
        if(!loaded && (gamesettings == null || gamesettings.soundVolume != 0.0F || gamesettings.musicVolume != 0.0F))
        {
            tryToSetLibraryAndCodecs();
        }
    }

    private void tryToSetLibraryAndCodecs()
    {
        try
        {
            float f = options.soundVolume;
            float f1 = options.musicVolume;
            options.soundVolume = 0.0F;
            options.musicVolume = 0.0F;
            options.saveOptions();
            SoundSystemConfig.addLibrary(paulscode.sound.libraries.LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", paulscode.sound.codecs.CodecJOrbis.class);
            SoundSystemConfig.setCodec("mus", net.minecraft.src.CodecMus.class);
            SoundSystemConfig.setCodec("wav", paulscode.sound.codecs.CodecWav.class);
            sndSystem = new SoundSystem();
            options.soundVolume = f;
            options.musicVolume = f1;
            options.saveOptions();
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
            System.err.println("error linking with the LibraryJavaSound plug-in");
        }
        loaded = true;
    }

    public void onSoundOptionsChanged()
    {
        if(!loaded && (options.soundVolume != 0.0F || options.musicVolume != 0.0F))
        {
            tryToSetLibraryAndCodecs();
        }
        if(loaded)
        {
            if(options.musicVolume == 0.0F)
            {
                sndSystem.stop("BgMusic");
            } else
            {
                sndSystem.setVolume("BgMusic", options.musicVolume);
            }
        }
    }

    public void closeMinecraft()
    {
        if(loaded)
        {
            sndSystem.cleanup();
        }
    }

    public void addSound(String s, File file)
    {
        soundPoolSounds.addSound(s, file);
    }

    public void addStreaming(String s, File file)
    {
        soundPoolStreaming.addSound(s, file);
    }

    public void addMusic(String s, File file)
    {
        soundPoolMusic.addSound(s, file);
    }

    public void playRandomMusicIfReady()
    {
        if(!loaded || options.musicVolume == 0.0F)
        {
            return;
        }
        if(!sndSystem.playing("BgMusic") && !sndSystem.playing("streaming"))
        {
            if(ticksBeforeMusic > 0)
            {
                ticksBeforeMusic--;
                return;
            }
            SoundPoolEntry soundpoolentry = soundPoolMusic.getRandomSound();
            if(soundpoolentry != null)
            {
                ticksBeforeMusic = rand.nextInt(12000) + 12000;
                sndSystem.backgroundMusic("BgMusic", soundpoolentry.soundUrl, soundpoolentry.soundName, false);
                sndSystem.setVolume("BgMusic", options.musicVolume);
                sndSystem.play("BgMusic");
            }
        }
    }

    public void func_338_a(EntityLiving entityliving, float f)
    {
        if(!loaded || options.soundVolume == 0.0F)
        {
            return;
        }
        if(entityliving == null)
        {
            return;
        } else
        {
            float f1 = entityliving.prevRotationYaw + (entityliving.rotationYaw - entityliving.prevRotationYaw) * f;
            double d = entityliving.prevPosX + (entityliving.posX - entityliving.prevPosX) * (double)f;
            double d1 = entityliving.prevPosY + (entityliving.posY - entityliving.prevPosY) * (double)f;
            double d2 = entityliving.prevPosZ + (entityliving.posZ - entityliving.prevPosZ) * (double)f;
            float f2 = MathHelper.cos(-f1 * 0.01745329F - 3.141593F);
            float f3 = MathHelper.sin(-f1 * 0.01745329F - 3.141593F);
            float f4 = -f3;
            float f5 = 0.0F;
            float f6 = -f2;
            float f7 = 0.0F;
            float f8 = 1.0F;
            float f9 = 0.0F;
            sndSystem.setListenerPosition((float)d, (float)d1, (float)d2);
            sndSystem.setListenerOrientation(f4, f5, f6, f7, f8, f9);
            return;
        }
    }

    public void playStreaming(String s, float f, float f1, float f2, float f3, float f4)
    {
        if(!loaded || options.soundVolume == 0.0F)
        {
            return;
        }
        String s1 = "streaming";
        if(sndSystem.playing("streaming"))
        {
            sndSystem.stop("streaming");
        }
        if(s == null)
        {
            return;
        }
        SoundPoolEntry soundpoolentry = soundPoolStreaming.getRandomSoundFromSoundPool(s);
        if(soundpoolentry != null && f3 > 0.0F)
        {
            if(sndSystem.playing("BgMusic"))
            {
                sndSystem.stop("BgMusic");
            }
            float f5 = 16F;
            sndSystem.newStreamingSource(true, s1, soundpoolentry.soundUrl, soundpoolentry.soundName, false, f, f1, f2, 2, f5 * 4F);
            sndSystem.setVolume(s1, 0.5F * options.soundVolume);
            sndSystem.play(s1);
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
        playMusic(music, id, 0, 0, 0, volume, false);
    }
    
    public void playMusic(String music, int id, int x, int y, int z, float volume, boolean loc)
    {
        if(!loaded || options.musicVolume == 0.0F)
        {
            return;
        }
        if(sndSystem.playing("BgMusic"))
        {
            sndSystem.stop("BgMusic");
        }
        if(sndSystem.playing("streaming"))
        {
            sndSystem.stop("streaming");
        }
        SoundPoolEntry soundpoolentry = soundPoolMusic.getSoundFromSoundPool(music, id);
        if(soundpoolentry != null)
        {
            ticksBeforeMusic = rand.nextInt(12000) + 12000;
            sndSystem.backgroundMusic("BgMusic", soundpoolentry.soundUrl, soundpoolentry.soundName, false);
            sndSystem.setVolume("BgMusic", options.musicVolume * volume);
            sndSystem.play("BgMusic");
        }
    }
    
    public boolean hasSound(String sound, int id) {
        return soundPoolMusic.getSoundFromSoundPool(sound, id) != null;
    }
    
    public void addCustomSound(String sound, File song) {
        soundPoolMusic.addCustomSound(sound, song);
    }
    //BukkitContrib end

    private static SoundSystem sndSystem;
    private SoundPool soundPoolSounds;
    private SoundPool soundPoolStreaming;
    private SoundPool soundPoolMusic;
    private int field_587_e;
    private GameSettings options;
    private static boolean loaded = false;
    private Random rand;
    private int ticksBeforeMusic;

}
