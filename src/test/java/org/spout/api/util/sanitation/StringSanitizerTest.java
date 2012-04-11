package org.spout.api.util.sanitation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class StringSanitizerTest {

	@Test
	public void test() {
		
		assertTrue(StringSanitizer.isAlphaNumeric("Abc123"));
		assertTrue(StringSanitizer.isAlphaNumericMinusDot("Abc123"));
		
		assertFalse(StringSanitizer.isAlphaNumeric("XYZ-123"));
		assertTrue(StringSanitizer.isAlphaNumericMinusDot("XYZ-123"));
		
		assertFalse(StringSanitizer.isAlphaNumeric("Abc.890"));
		assertTrue(StringSanitizer.isAlphaNumericMinusDot("Abc.890"));
		
		assertFalse(StringSanitizer.isAlphaNumeric("Abc.1-23"));
		assertTrue(StringSanitizer.isAlphaNumericMinusDot("Abc.1-23"));
		
		assertFalse(StringSanitizer.isAlphaNumeric("Abc.@123#"));
		assertFalse(StringSanitizer.isAlphaNumericMinusDot("Abc.@123#"));
		
	}
	
}
