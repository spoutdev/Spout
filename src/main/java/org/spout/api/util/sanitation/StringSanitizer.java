package org.spout.api.util.sanitation;

public class StringSanitizer {
	
	private static CompiledPattern alphaNumeric = new CompiledPattern("^[a-zA-Z0-9]+");
	
	public static boolean isAlphaNumeric(String s) {
		return alphaNumeric.matches(s);
	}
	
	private static CompiledPattern alphaNumericMinusDot = new CompiledPattern("^[a-zA-Z0-9\\.\\-]+");
	
	public static boolean isAlphaNumericMinusDot(String s) {
		return alphaNumericMinusDot.matches(s);
	}

}
