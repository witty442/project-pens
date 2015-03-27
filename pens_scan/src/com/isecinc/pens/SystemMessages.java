package com.isecinc.pens;

import java.util.Locale;

import com.isecinc.core.I_System;

/**
 * SystemMessage Class
 * 
 * @author Atiz.b
 * @version $Id: SystemMessage.java,v 1.0 14/06/2010 00:00:00 atiz.b Exp $
 * 
 */
public class SystemMessages extends I_System {

	/** Validation Message */
	public static final String CREDENTIAL_REQUIRE = "CredentialRequire";
	public static final String INVALID_CREDENTIAL = "InvalidCredential";
	public static final String SEARCH_NOT_FOUND = "SearchNotFound";
	public static final String SAVE_SUCCESS = "SaveSucess";
	public static final String INVALID_PASSWORD = "InvalidPassword";
	public static final String SELECT_AT_LEAST_ONE_RECORD = "SelectAtLeastOneRecord";
	public static final String INVALID_MANDATORY = "InvalidMandatory";

	// Aneak.t
	public static final String DUPLICATE_DATA = "DuplicateData";

	/** File Name */
	public static final String FILE_NAME = "SystemMessages";

	public static String getCaption(String message, Locale locale) {
		return getCaption(FILE_NAME, message, new Locale("th", "TH"));
	}
}
