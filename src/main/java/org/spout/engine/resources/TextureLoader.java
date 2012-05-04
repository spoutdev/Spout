package org.spout.engine.resources;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.spout.api.render.Texture;

import org.spout.engine.filesystem.BasicResourceLoader;

public class TextureLoader extends BasicResourceLoader<Texture> {
	@Override
	public Texture getResource(InputStream stream) {
		Texture t = null;
		try {
			BufferedImage image = ImageIO.read(stream);
			t = new ClientTexture(image);
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return t;
	}
}
