package com.pens.util;

/**
 * Convert Null Utilities Class
 * 
 * @author Atiz.b
 * @version $Id: ConvertNullUtil.java ,v 1.0 14/06/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ConvertNullUtil {

	/**
	 * Convert to String
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static String convertToString(String s) throws Exception {
		String strreturn = "";
		try {
			if (s == null) {
				strreturn = "";
			} else {
				strreturn = s;
			}
		} catch (Exception e) {
			throw e;
		}
		return strreturn;
	}
	
	public static String convertToString(Object s) throws Exception {
		String strreturn = "";
		try {
			if (s == null) {
				strreturn = "";
			} else {
				strreturn = (String)s;
			}
		} catch (Exception e) {
			throw e;
		}
		return strreturn;
	}

}
