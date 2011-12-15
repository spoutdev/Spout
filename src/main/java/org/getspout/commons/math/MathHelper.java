package org.getspout.commons.math;

import org.getspout.commons.util.Color;

public class MathHelper {
	public static double lerp(double a, double b, double percent){
		return a + (b * percent);
	}
	public static int lerp(int a, int b, double percent){
		return (int)(a + (b * percent));
	}
	public static Vector3 lerp(Vector3 a, Vector3 b, double percent){
		return (a.add(b.scale(percent)));
	}
	
	public static Vector2 lerp(Vector2 a, Vector2 b, double percent){
		return (a.add(b.scale(percent)));
	}
	
	public static Color lerp(Color a, Color b, double percent){
		int red = lerp(a.getRedI(), b.getRedI(), percent);
		int blue = lerp(a.getBlueI(), b.getBlueI(), percent);
		int green = lerp(a.getGreenI(), b.getGreenI(), percent);
		int alpha = lerp(a.getAlphaI(), b.getAlphaI(), percent);
		return new Color(red, blue, green, alpha);
	}

	
	public static double clamp(double value, double low, double high){
		if(value < low) return low;
		if(value > high) return high;
		return value;
	}
	public static int clamp(int value, int low, int high){
		if(value < low) return low;
		if(value > high) return high;
		return value;
	}
}
