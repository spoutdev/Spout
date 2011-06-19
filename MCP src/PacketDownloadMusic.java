package net.minecraft.src;
//BukkitContrib
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketDownloadMusic implements BukkitContribPacket{
    int x, y, z;
    int volume;
    boolean loc;
    String URL;
    public PacketDownloadMusic() {
        
    }

    @Override
    public int getNumBytes() {
        return 17 + URL.length();
    }

    @Override
    public void readData(DataInputStream input) throws IOException {
        URL = PacketUtil.readString(input, 255);
        loc = input.readBoolean();
        x = input.readInt();
        y = input.readInt();
        z = input.readInt();
        volume = input.readInt();
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        PacketUtil.writeString(output, URL);
        output.writeBoolean(loc);
        output.writeInt(x);
        output.writeInt(y);
        output.writeInt(z);
        output.writeInt(volume);
    }


    @Override
    public void run(int PlayerId) {
        (new MusicDownloadThread(URL, getFileName(), loc, x, y, z, volume)).start();
    }
    
    private String getFileName() {
        int slashIndex = URL.lastIndexOf('/');
        int dotIndex = URL.lastIndexOf('.', slashIndex);
        if (dotIndex == -1 || dotIndex < slashIndex) {
            return URL.substring(slashIndex + 1).replaceAll("%20", " ");
        }
        return URL.substring(slashIndex + 1, dotIndex).replaceAll("%20", " ");
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PacketDownloadMusic;
    }

}
