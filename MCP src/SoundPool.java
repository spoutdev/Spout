// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

// Referenced classes of package net.minecraft.src:
//            SoundPoolEntry

public class SoundPool
{

    public SoundPool()
    {
        rand = new Random();
        nameToSoundPoolEntriesMapping = new HashMap();
        allSoundPoolEntries = new ArrayList();
        numberOfSoundPoolEntries = 0;
        field_1657_b = true;
    }

    public SoundPoolEntry addSound(String s, File file)
    {
        try
        {
            String s1 = s;
            s = s.substring(0, s.indexOf("."));
            if(field_1657_b)
            {
                for(; Character.isDigit(s.charAt(s.length() - 1)); s = s.substring(0, s.length() - 1)) { }
            }
            s = s.replaceAll("/", ".");
            if(!nameToSoundPoolEntriesMapping.containsKey(s))
            {
                nameToSoundPoolEntriesMapping.put(s, new ArrayList());
            }
            SoundPoolEntry soundpoolentry = new SoundPoolEntry(s1, file.toURI().toURL());
            ((List)nameToSoundPoolEntriesMapping.get(s)).add(soundpoolentry);
            allSoundPoolEntries.add(soundpoolentry);
            numberOfSoundPoolEntries++;
            return soundpoolentry;
        }
        catch(MalformedURLException malformedurlexception)
        {
            malformedurlexception.printStackTrace();
            throw new RuntimeException(malformedurlexception);
        }
    }
    
    //BukkitContrib Start
    public SoundPoolEntry getSoundFromSoundPool(String s, int id) {
        List list = (List)nameToSoundPoolEntriesMapping.get(s);
        if (list == null) {
            return null;
        }
        return (SoundPoolEntry)list.get(id);
    }
    
    public SoundPoolEntry addCustomSound(String sound, File file) {
        try {
            if(!nameToSoundPoolEntriesMapping.containsKey(sound)) {
                nameToSoundPoolEntriesMapping.put(sound, new ArrayList());
            }
            SoundPoolEntry soundpoolentry = new SoundPoolEntry(sound, file.toURI().toURL());
            ((List)nameToSoundPoolEntriesMapping.get(sound)).add(soundpoolentry);
            return soundpoolentry;
        }
        catch(MalformedURLException malformedurlexception) {
            return null;
        }
    }
    
    //BukkitContrib End

    public SoundPoolEntry getRandomSoundFromSoundPool(String s)
    {
        List list = (List)nameToSoundPoolEntriesMapping.get(s);
        if(list == null)
        {
            return null;
        } else
        {
            return (SoundPoolEntry)list.get(rand.nextInt(list.size()));
        }
    }

    public SoundPoolEntry getRandomSound()
    {
        if(allSoundPoolEntries.size() == 0)
        {
            return null;
        } else
        {
            return (SoundPoolEntry)allSoundPoolEntries.get(rand.nextInt(allSoundPoolEntries.size()));
        }
    }

    private Random rand;
    private Map nameToSoundPoolEntriesMapping;
    private List allSoundPoolEntries;
    public int numberOfSoundPoolEntries;
    public boolean field_1657_b;
}
