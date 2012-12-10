/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.component.components;

import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;

public class PredictableTransformComponent extends TransformComponent {

	private static final int ref = 3;
	private static final long late = 1000 / 30;//Late of 1.5 engine cycle

	private Transform []transformsReferences = new Transform[ref];
	private long []timeReferences = new long[ref];
	private float []weightReferences = new float[ref];

	private Transform transformRender = new Transform();

	@Override
	public void onAttached(){
		super.onAttached();

		for(int i = 0; i < transformsReferences.length; i++){
			transformsReferences[i] = new Transform();
			timeReferences[i] = System.currentTimeMillis();
		}

	}

	public void updateRender(float dt) {
		Vector3 translation = Vector3.ZERO;
		Vector3 rotation = Vector3.ZERO;
		Vector3 scale = Vector3.ZERO;

		long currentTime = System.currentTimeMillis() - late;

		long maxDiff = Math.max( Math.abs(currentTime - timeReferences[ref - 1]), Math.abs(currentTime - timeReferences[0]));
		long weightSum = 0;
		
		for(int i = 0; i < ref; i++){
			weightReferences[i] = maxDiff - Math.abs(currentTime - timeReferences[i]);
			weightSum += weightReferences[i];
		}
		
		for(int i = 0; i < ref; i++){
			weightReferences[i] /= weightSum;
			
			translation = translation.add(transformsReferences[i].getPosition().multiply(weightReferences[i]));

			Quaternion q = transformsReferences[i].getRotation();
			rotation = rotation.add(q.getPitch() * weightReferences[i],
					q.getYaw() * weightReferences[i],
					q.getRoll() * weightReferences[i]);

			scale = scale.add(transformsReferences[i].getScale().multiply(weightReferences[i]));
		}


		transformRender.set(new Transform(new Point(translation, getOwner().getWorld()), MathHelper.rotation(rotation.getX(), rotation.getY(), rotation.getZ()), scale));
	}

	public Transform getRenderTransform() {
		return transformRender;
	}


	@Override
	public void copySnapshot(){
		for(int i = 0; i < ref - 1; i ++){
			transformsReferences[i] = transformsReferences[i+1];
			timeReferences[i] = timeReferences[i+1];
		}

		transformsReferences[ref - 1] = getTransform();
		timeReferences[ref - 1] = System.currentTimeMillis();

		super.copySnapshot();
	}
}
