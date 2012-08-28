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
package org.spout.api.material.range;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.material.block.BlockFace;
import org.spout.api.material.block.BlockFaces;
import org.spout.api.math.IntVector3;
import org.spout.api.util.LogicUtil;
import org.spout.api.util.map.TByteShortByteKeyedHashSet;

public class ListEffectRange extends EffectRangeImpl {
	private final List<IntVector3> effectList;

	public ListEffectRange(EffectRange ... ranges) {
		this(combineRanges(ranges), false);
	}

	public ListEffectRange(BlockFaces blockFaces) {
		this(IntVector3.createList(blockFaces));
	}

	public ListEffectRange(BlockFace... blockFaces) {
		this(IntVector3.createList(blockFaces));
	}

	public ListEffectRange(List<IntVector3> effectList) {
		this(effectList, true);
	}

	protected ListEffectRange(List<IntVector3> effectList, boolean copy) {
		super(effectList);
		if (copy) {
			this.effectList = new ArrayList<IntVector3>(effectList.size());
			for (IntVector3 v : effectList) {
				this.effectList.add(v.copy());
			}
			LogicUtil.removeDuplicates(this.effectList);
		} else {
			this.effectList = effectList;
		}
	}

	@Override
	public void initEffectIterator(EffectIterator i) {
		i.resetAsList(effectList);
	}

	@Override
	public EffectRange translate(IntVector3 offset) {
		List<IntVector3> newEffectList = new ArrayList<IntVector3>(effectList.size());
		for (IntVector3 effect : effectList) {
			IntVector3 translated = effect.copy();
			translated.add(offset);
			newEffectList.add(translated);
		}
		return new ListEffectRange(newEffectList, false);
	}

	private static List<IntVector3> combineRanges(EffectRange[] ranges) {
		List<IntVector3> list = new ArrayList<IntVector3>();
		EffectIterator i = new EffectIterator();
		TByteShortByteKeyedHashSet set = new TByteShortByteKeyedHashSet(8);
		for (EffectRange e : ranges) {
			e.initEffectIterator(i);
			while (i.hasNext()) {
				IntVector3 v = i.next();
				if (set.add(v.getX(), v.getY() & 0xFF, v.getZ())) {
					list.add(v.copy());
				}
			}
		}
		return list;
	}
}
