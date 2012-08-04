package org.spout.api.lang;

public class Locale {
	private String fullName, countryCode;
	//TODO: counting rules
	
	public Locale(String fullName, String countryCode) {
		this.fullName = fullName;
		this.countryCode = countryCode;
	}
	
	public String getCountryCode() {
		return countryCode;
	}

	public String getFullName() {
		return fullName;
	}
}
