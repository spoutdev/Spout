package org.spout.api.signal;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

public class SignalArgumentTaglet implements Taglet {

	@Override
	public String getName() {
		return "sarg";
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
		if(tag.text().equalsIgnoreCase("none")) {
			return "<i>No arguments</i>";
		}
		String splt[] = tag.text().split(" ", 2);
		if(splt.length == 2) {
			return "<span style='font-family:monospace'>"+splt[0]+"</span> - " + splt[1];
		}
		return tag.text();
	}

	@Override
	public String toString(Tag[] tags) {
		if(tags.length == 0) {
			return "";
		}
		String text = "";
		if(tags.length == 1 && !tags[0].text().equalsIgnoreCase("none")) {
			text += "<dt><b>Argument:</b></dt>";
		} else {
			text += "<dt><b>Arguments:</b></dt>";
		}
		if(tags[0].text().equalsIgnoreCase("none")) {
			text += "<dd><i>No arguments</i></dd>";
		} else {
			text += "<dd><ol>";
			for(Tag tag:tags) {
				text += "<li>";
				text += toString(tag);
				text += "</li>";
			}
			text += "</ol></dd>";
		}
		
		return text;
	}
	
	public static void register(Map tagletMap) {
	    tagletMap.put("sarg", new SignalArgumentTaglet());
	}

}
