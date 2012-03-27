package org.spout.api.util.sanitation;

import java.util.regex.Pattern;

public class CompiledPattern {
	
	private Pattern p;
	
	public CompiledPattern(String regex) {
		p = Pattern.compile(regex);	
	}
	
	public boolean matches(String input) {
		return p.matcher(input).matches();
	}

}
