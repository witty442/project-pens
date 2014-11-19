package com.isecinc.pens;

import java.util.Locale;

import com.isecinc.core.I_System;

/**
 * SystemProperties Class
 * 
 * @author Atiz.b
 * @version $Id: SystemProperties.java,v 1.0 14/06/2010 00:00:00 atiz.b Exp $
 * 
 */
public class SystemProperties extends I_System {

	/** Project Name */
	public static final String PROJECT_NAME = "ProjectName";

	/** Menu - Function */
	public static final String SYSADMINISTER = "Administer";
	public static final String SYSMASTERDATA = "MasterData";
	public static final String SYSTRANSACTION = "Transaction";
	public static final String SYSREPORT = "Report";

	/** Create New Record */
	public static final String CREATE_NEW_RECORD = "CreateNewRecord";

	/** Records Found */
	public static final String RECORDS_FOUND = "RecordsFound";
	public static final String RECORDS = "Records";
	public static final String LOOKUP = "Lookup";

	/** Status */
	public static final String ACTIVE = "Active";
	public static final String INACTIVE = "Inactive";
	public static final String DEFAULT = "Default";
	public static final String NOT_DEFAULT = "NotDefault";
	public static final String DRAFT = "Draft";
	public static final String COMPLETE = "Complete";
	public static final String VOID = "Void";

	/** Button */
	public static final String SEARCH = "Search";
	public static final String CLEAR = "Clear";
	public static final String SAVE = "Save";
	public static final String SAVE_EDIT = "SaveEdit";
	public static final String CANCEL = "Cancel";
	public static final String EDIT = "Edit";

	/** File Name */
	private static final String FILE_NAME = "SystemProperties";

	/** Other */
	public static final String LineNo = "No";

	/**
	 * Get Caption
	 * 
	 * @param message
	 * @param locale
	 * @return
	 */
	public static String getCaption(String message, Locale locale) {
		return getCaption(FILE_NAME, message, new Locale("th", "TH"));
	}

}
