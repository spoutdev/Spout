package org.getspout.server.block;

import org.bukkit.block.Sign;

import org.getspout.server.SpoutChunk;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.UpdateSignMessage;

public class SpoutSign extends SpoutBlockState implements Sign {
	private String[] lines;

	public SpoutSign(SpoutBlock block) {
		super(block);
		if (block.getTypeId() != BlockID.WALL_SIGN && block.getTypeId() != BlockID.SIGN_POST) {
			throw new IllegalArgumentException("SpoutSign: expected WALL_SIGN or SIGN_POST, got " + block.getType());
		}
		lines = new String[4];
	}

	@Override
	public String[] getLines() {
		return lines.clone();
	}

	@Override
	public String getLine(int index) throws IndexOutOfBoundsException {
		return lines[index];
	}

	@Override
	public void setLine(int index, String line) throws IndexOutOfBoundsException {
		if (index < 0 || index >= lines.length) {
			throw new IndexOutOfBoundsException();
		}
		lines[index] = line;
	}

	@Override
	public boolean update(boolean force) {
		boolean result = super.update(force);
		if (result) {
			SpoutChunk.Key key = new SpoutChunk.Key(getChunk().getX(), getChunk().getZ());
			UpdateSignMessage message = new UpdateSignMessage(getX(), getY(), getZ(), getLines());
			for (SpoutPlayer player : getWorld().getRawPlayers()) {
				if (player.canSee(key)) {
					player.getSession().send(message);
				}
			}
		}
		return result;
	}

	@Override
	public void update(SpoutPlayer player) {
		player.getSession().send(new UpdateSignMessage(getX(), getY(), getZ(), getLines()));
	}

	// Internal mechanisms

	@Override
	public SpoutSign shallowClone() {
		SpoutSign result = new SpoutSign(getBlock());
		result.lines = lines;
		return result;
	}

	@Override
	public void destroy() {
		for (String line : lines) {
			line = "";
		}
	}
}
