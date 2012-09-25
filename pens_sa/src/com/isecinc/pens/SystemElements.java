package com.isecinc.pens;

import java.util.Locale;

import com.isecinc.core.I_System;

/**
 * SystemElement Class
 * 
 * @author Atiz.b
 * @version $Id: SystemElement.java,v 1.0 14/06/2010 00:00:00 atiz.b Exp $
 * 
 */
public class SystemElements extends I_System {

	/** Default Elements */
	public static final String CODE = "Code";
	public static final String NAME = "Name";
	public static final String DESCRIPTION = "Description";
	public static final String ADDRESS = "Address";
	public static final String ACTIVE = "Active";
	public static final String CREATED = "Created";
	public static final String CREATED_BY = "CreatedBy";
	public static final String UPDATED = "Updated";
	public static final String UPDATED_BY = "UpdatedBy";

	/** User Elements */
	public static final String USER_CODE = "Usercode";
	public static final String PASSWORD = "Password";
	public static final String CONFIRM_PASSWORD = "ConfirmPassword";

	/** Document Elements */
	public static final String DOCUMENT_NO = "DocumentNo";
	public static final String DOCUMENT_DATE = "DocumentDate";
	public static final String DOCUMENT_STATUS = "DocStatus";

	/** Line Elements */
	public static final String LINE_NO = "LineNo";
	public static final String SEQ = "Seq";

	/** DM Storage Elements */
	public static final String EXPIRE_DATE = "ExpireDate";
	public static final String BARCODE = "Barcode";
	public static final String UOM_Net1 = "UOM_Net1";
	public static final String UOM_Net2 = "UOM_Net2";

	/** FLAG Elements **/
	public static final String ISDEFAULT = "isDefault";
	public static final String PRINTED = "Printed";
	public static final String STATUS = "Status";

	/** File Name */
	private static final String FILE_NAME = "SystemElements";

	/** Month **/
	public static final String JAN = "JAN";
	public static final String FEB = "FEB";
	public static final String MAR = "MAR";
	public static final String APR = "APR";
	public static final String MAY = "MAY";
	public static final String JUN = "JUN";
	public static final String JUL = "JUL";
	public static final String AUG = "AUG";
	public static final String SEP = "SEP";
	public static final String OCT = "OCT";
	public static final String NOV = "NOV";
	public static final String DEC = "DEC";

	/** Day **/
	public static final String SUN = "Sun";
	public static final String MON = "Mon";
	public static final String TUE = "Tue";
	public static final String WED = "Wed";
	public static final String THU = "Thu";
	public static final String FRI = "Fri";
	public static final String SAT = "Sat";

	public static final String ADMIN = "admin";
	public static final String USER = "user";
	
	/** Report Format **/
	public static final String EXCEL = "XLS";
	public static final String PDF = "PDF";
	public static final String HTML = "HTML";
	public static final String WORD = "DOC";
	public static final String PRINTER = "PRINTER";
	
	/** Follow By **/
	public static final String Telephone = "Telephone";
	public static final String Email = "Email";
	public static final String Other = "Other";

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
