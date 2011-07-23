import java.io.File;
import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;

public class Start
{

	public static void main(String[] args)
	{
		try
		{
			// set new minecraft data folder to prevent it from using the .minecraft folder
			// this makes it a portable version
			Field f = Minecraft.class.getDeclaredField("minecraftDir");
			Field.setAccessible(new Field[] { f }, true);
			f.set(null, new File("."));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		// start minecraft game application
		Minecraft.main(args);

		// get the minecraft instance
		final Minecraft mc;
		try
		{
			Field f = Minecraft.class.getDeclaredField("theMinecraft");
			Field.setAccessible(new Field[] { f }, true);
			mc = (Minecraft) f.get(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		// make sure no nagging message will come up during testing
		Thread noNagging = new Thread("nonagging") {

			@Override
			public void run()
			{
				while(mc.running)
				{
					if(mc.hasPaidCheckTime > 0)
						mc.hasPaidCheckTime = 0;

					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
					}
				}
			}

		};

		// start our no-nagging thread
		noNagging.start();
	}

}
