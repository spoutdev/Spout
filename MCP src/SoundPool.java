package net.minecraft.src;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.src.SoundPoolEntry;

public class SoundPool {

	private Random rand = new Random();
	private Map nameToSoundPoolEntriesMapping = new HashMap();
	private List allSoundPoolEntries = new ArrayList();
	public int numberOfSoundPoolEntries = 0;
	public boolean field_1657_b = true;


	public SoundPoolEntry addSound(String var1, File var2) {
		try {
			String var3 = var1;
			var1 = var1.substring(0, var1.indexOf("."));
			if(this.field_1657_b) {
				while(Character.isDigit(var1.charAt(var1.length() - 1))) {
					var1 = var1.substring(0, var1.length() - 1);
				}
			}

			var1 = var1.replaceAll("/", ".");
			if(!this.nameToSoundPoolEntriesMapping.containsKey(var1)) {
				this.nameToSoundPoolEntriesMapping.put(var1, new ArrayList());
			}

			SoundPoolEntry var4 = new SoundPoolEntry(var3, var2.toURI().toURL());
			((List)this.nameToSoundPoolEntriesMapping.get(var1)).add(var4);
			this.allSoundPoolEntries.add(var4);
			++this.numberOfSoundPoolEntries;
			return var4;
		} catch (MalformedURLException var5) {
			var5.printStackTrace();
			throw new RuntimeException(var5);
		}
	}

	public SoundPoolEntry getRandomSoundFromSoundPool(String var1) {
		List var2 = (List)this.nameToSoundPoolEntriesMapping.get(var1);
		return var2 == null?null:(SoundPoolEntry)var2.get(this.rand.nextInt(var2.size()));
	}

	public SoundPoolEntry getRandomSound() {
		return this.allSoundPoolEntries.size() == 0?null:(SoundPoolEntry)this.allSoundPoolEntries.get(this.rand.nextInt(this.allSoundPoolEntries.size()));
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
}
