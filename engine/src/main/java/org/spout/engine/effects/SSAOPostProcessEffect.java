/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.effects;

import java.awt.Color;
import java.util.Random;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.math.GenericMath;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.render.effect.RenderEffect;
import org.spout.api.render.effect.SnapshotRender;

import org.spout.engine.filesystem.resource.ClientTexture;

//Crysis method of SSAO
//Following the excellent John-chapman tutorial here: http://www.john-chapman.net/content.php?id=8
public class SSAOPostProcessEffect implements RenderEffect {

	//TODO: Make this settable from config.  It determines the 'quality' of SSAO in exchange for render time.
	public static final int kernelSize = 20;
	
	//Noise demensions.  texture is square.  
	public static final int noiseSize = 2;
	
	public static final float radius = 1.0f;
	
	final Vector3[] kernel;
	final ClientTexture noise;
	final Vector2 noiseScale;
	
	
	public SSAOPostProcessEffect()	{
		kernel = new Vector3[kernelSize];
		Random rng = new Random();
		for(int i = 0; i < kernelSize; i++){
			//Create a set of random vectors along the surface of a hemisphere.
			kernel[i] = new Vector3((rng.nextFloat() * 2) - 1, (rng.nextFloat() * 2) - 1, rng.nextFloat());
			//Normalize the vector
			kernel[i] = kernel[i].normalize();		
			
			
			//Scale it into the hemisphere so the vectors aren't all along the surface. 
			//We want the distance from the origin to fall off as we generate more points.  
			float scale = (float)i / (float)kernelSize;
			scale = GenericMath.lerp(0.1f, 1.0f, scale * scale);
			
			kernel[i] = kernel[i].multiply(scale);
		}
		
		//Generate the noise texture.
		int[] texture = new int[noiseSize * noiseSize];
		for(int i = 0; i < noiseSize * noiseSize; i++){
			Color c = new Color(rng.nextFloat(), rng.nextFloat(), 0);
			texture[i] = c.getRGB();			
		}
		Vector2 resolution = ((Client)Spout.getEngine()).getResolution();
		noiseScale = new Vector2(resolution.getX() / noiseSize, resolution.getY() / noiseSize);
		noise = new ClientTexture(texture, noiseSize, noiseSize);
	}
	
	@Override
	public void preRender(SnapshotRender snapshotRender) {
		
		snapshotRender.getMaterial().getShader().setUniform("noiseScale", noiseScale);
		snapshotRender.getMaterial().getShader().setUniform("noise", noise);
		snapshotRender.getMaterial().getShader().setUniform("kernel", kernel);
		snapshotRender.getMaterial().getShader().setUniform("kernelSize", kernel.length);
		snapshotRender.getMaterial().getShader().setUniform("radius", radius);
	
		
	}

	@Override
	public void postRender(SnapshotRender snapshotRender) {
		// TODO Auto-generated method stub
		
	}

}
