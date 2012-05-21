package org.spout.api.signal;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

public class SignalTaglet implements Taglet {

	@Override
	public String getName() {
		return "signal";
	}

	@Override
	public boolean inConstructor() {
		return false;
	}

	@Override
	public boolean inField() {
		return true;
	}

	@Override
	public boolean inMethod() {
		return false;
	}

	@Override
	public boolean inOverview() {
		return false;
	}

	@Override
	public boolean inPackage() {
		return false;
	}

	@Override
	public boolean inType() {
		return false;
	}

	@Override
	public boolean isInlineTag() {
		return false;
	}

	@Override
	public String toString(Tag tag) {
		String splt[] = tag.text().split(" ", 2);
		if(splt.length == 2) {
			String text = "<dt><b>Signal Description:</b></dt><dd>";
			text += getDefinition("Name:", splt[0]);
			text += getDefinition("Called:", splt[1]);
			text += "</dd>";
			return text;
		}
		return "";
	}
	
	private String getDefinition(String topic, String definition) {
		return "<dl><dt><b>"+topic+"</b></dt><dd>"+definition+"</dd></dl>";
	}

	@Override
	public String toString(Tag[] tags) {
		String text = "";
		for(Tag tag:tags) {
			text += toString(tag);
		}
		return text;
	}
	
	public static void register(Map tagletMap) {
	    tagletMap.put("signal", new SignalTaglet());
	}

}
