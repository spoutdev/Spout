package org.spout.api.component.components;

import org.spout.api.component.Component;
import org.spout.api.geo.World;

public interface WorldComponent extends Component {
	@Override
	public void attachTo(World world);
	
	@Override
	public World getHolder();
}
