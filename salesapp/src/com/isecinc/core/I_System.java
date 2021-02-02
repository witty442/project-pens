package com.isecinc.core;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.pens.util.BundleUtil;

/**
 * I_System
 * 
 * @author Atiz.b
 * @version $Id: I_System.java,v 1.0 14/06/2010 00:00:00 atiz.b Exp $
 * 
 */
public abstract class I_System {

	private static Logger logger = Logger.getLogger("PENS");

	/**
	 * Get Caption
	 * 
	 * @param fileName
	 * @param message
	 * @param locale
	 * @return
	 */
	protected static String getCaption(String fileName, String message, Locale locale) {
		String caption = "";
		try {
			ResourceBundle bundle = BundleUtil.getBundle(fileName, locale);
			caption = bundle.getString(message);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return caption;
	}
}
