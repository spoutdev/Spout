package org.spout.engine.util;

import org.spout.api.render.RenderMode;

<<<<<<< HEAD
public class RenderModeConverter extends EnumConverter<RenderMode> {
	public RenderModeConverter() {
		super(RenderMode.class);
=======
import com.beust.jcommander.IStringConverter;

public class RenderModeConverter implements IStringConverter<RenderMode> {

	@Override
	public RenderMode convert(String arg0) {
		if(arg0.equalsIgnoreCase("GL11")){
			return RenderMode.GL11;
		} else if (arg0.equalsIgnoreCase("GL20")){
			return RenderMode.GL20;
		} else if (arg0.equalsIgnoreCase("GL30")){
			return RenderMode.GL30;
		} else if (arg0.equalsIgnoreCase("GLES20")){
			return RenderMode.GLES20;
		}
		throw new IllegalArgumentException("Unknown Render Mode: " + arg0);
>>>>>>> parent of 751169a... Cleanup of SpoutEngine. You are welcome @RoyAwesome.
	}

}
