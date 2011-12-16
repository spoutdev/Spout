package org.getspout.server.temp.commons.util;

public class InputCertification {
	/**
	 * Checks if a String is alphanumeric and not more than a maximum length.
	 *
	 * @param string the String
	 * @param maxLength the maximum allowable length
	 * @return true if the string passes the check
	 */
	public static boolean isAlphaNumberic(String string, int maxLength) {
		if (string.length() > maxLength) {
			return false;
		}
		return isAlphaNumberic(string);
	}

	/**
	 * Checks if a String is alphanumeric.
	 *
	 * @param string the String
	 * @return true if the string passes the check
	 */
	public static boolean isAlphaNumberic(String string) {
		return string.matches("^[a-zA-Z0-9]+$");
	}

	/**
	 * Checks if a String contains only numbers, letters, periods (.) and minuses (-) and is not more than a maximum length.
	 *
	 * @param string the String
	 * @param maxLength the maximum allowable length
	 * @return true if the string passes the check
	 */
	public static boolean isAlphaNumbericDotMinus(String string, int maxLength) {
		if (string.length() > maxLength) {
			return false;
		}
		return isAlphaNumbericDotMinus(string);
	}

	/**
	 * Checks if a String contains only numbers, letters, periods (.) and minuses (-)
	 *
	 * @param string the String
	 * @param maxLength the maximum allowable length
	 * @return true if the string passes the check
	 */
	public static boolean isAlphaNumbericDotMinus(String string) {
		return string.matches("^[a-zA-Z0-9\\.\\-]+$");
	}
}
