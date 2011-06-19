// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

// Referenced classes of package net.minecraft.src:
//            ThreadDownloadImageData, ImageBuffer

class ThreadDownloadImage extends Thread
{

    ThreadDownloadImage(ThreadDownloadImageData threaddownloadimagedata, String s, ImageBuffer imagebuffer)
    {
        imageData = threaddownloadimagedata;
        location = s;
        buffer = imagebuffer;
//        super();
    }

    public void run()
    {
        HttpURLConnection httpurlconnection = null;
        try
        {
            URL url = new URL(location);
            httpurlconnection = (HttpURLConnection)url.openConnection();
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(false);
            httpurlconnection.connect();
            if(httpurlconnection.getResponseCode() / 100 == 4)
            {
                return;
            }
            if(buffer == null)
            {
                imageData.image = ImageIO.read(httpurlconnection.getInputStream());
            } else
            {
                imageData.image = buffer.parseUserSkin(ImageIO.read(httpurlconnection.getInputStream()));
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            httpurlconnection.disconnect();
        }
    }

    final String location; /* synthetic field */
    final ImageBuffer buffer; /* synthetic field */
    final ThreadDownloadImageData imageData; /* synthetic field */
}
