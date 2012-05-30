package org.spout.engine.util;

import jline.Completor;
import org.spout.api.Engine;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A copy of {@link jline.SimpleCompletor} that uses a set of strings sourced
 * from the list of commands registered with the commands manager
 */
public class SpoutCommandCompletor implements Completor {
	private final Engine engine;

	public SpoutCommandCompletor(Engine engine) {
		this.engine = engine;
	}
	@Override
	public int complete(String buffer, int cursor, @SuppressWarnings("rawtypes") List rawCandidates) {
		@SuppressWarnings("unchecked")
		List<String> candidates = rawCandidates;
		String start = (buffer == null) ? "" : buffer;
		TreeSet<String> all = new TreeSet<String>();
		all.addAll(engine.getRootCommand().getChildNames());

		SortedSet<String> matches = all.tailSet(start);

		for (String can : matches) {

			if (!(can.startsWith(start))) {
				break;
			}

			/*if (delimiter != null) {
				int index = can.indexOf(delimiter, cursor);

				if (index != -1) {
					can = can.substring(0, index + 1);
				}
			}*/

			candidates.add(can);
		}

		if (candidates.size() == 1) {
			candidates.set(0, candidates.get(0) + " ");
		}

		// the index of the completion is always from the beginning of
		// the buffer.
		return (candidates.size() == 0) ? (-1) : 0;
	}
}
