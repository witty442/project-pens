package com.isecinc.pens.inf.helper;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author WITTY
 *
 */
public class Constants {
	
	public static final String delimeterPipeStr = "|";
	public static final String delimeterComma = ",";
	public static final String delimeterPipe = "\\|";
	public static final String newLine = "\n";
	public static final String assci_enter = "\n";
	
	public static final String COLUMN_BLANK = "BLANK";
	public static final String INSERT_STR_DEFAULT_BLANK = " ";
	
	public static Calendar calendarTH = Calendar.getInstance(new Locale("th","TH"));
	public static Calendar calendarUS = Calendar.getInstance(Locale.US);
	
	public static final String TRANSACTION_MASTER_TYPE = "MASTER";
	public static final String TRANSACTION_TRANS_TYPE = "TRANSACTION";
	public static final String TRANSACTION_UTS_TRANS_TYPE = "UPDATE-TRANS-SALES";
	public static final String TRANSACTION_REUTS_TRANS_TYPE = "UPDATE-RETRANS-SALES";
	public static final String TRANSACTION_WEB_MEMBER_TYPE = "WEB-MEMBER";
	
	public static final String TYPE_IMPORT = "IMPORT";
	public static final String TYPE_EXPORT = "EXPORT";
	
	public static final String TYPE_SALES = "SALES";
	public static final String TYPE_CENTER = "CENTER";
	
	public static final int STATUS_FTP_FAIL = -2;
	public static final int STATUS_FAIL = -1;
	public static final int STATUS_START = 0;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_REGEN = 2;
	
	public static final String SUB_INV_SOLOR = "Sorlor";
	
	public static final String FTP_ENCODING_UTF_8 = "UTF-8";
	public static final String FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620 = "tis620";
	public static final String FTP_IMPORT_TO_SALES_ENCODING_TIS_620 = "tis620";
	public static final String FTP_FILE_NAME_ALL = "ALL";
	
	public static final String STRING_EXPORT_CASE_ZERO= " ";
	
	public static final String EXPORT_FILL_SYMBOL_BLANK= "Blank";
	public static final String EXPORT_FILL_SYMBOL_ZERO= "Zero";
	
	public static final String EXPORT_STRING_SYMBOL_ZERO_DEFALUE= " ";
	
	public static final String PAYMENT_FLAG_Y= "Y";
	public static final String PAYMENT_FLAG_N= "N";
	
	public static final String INTERFACES_DOC_STATUS_SAVED = "SV"; //ORCL BOOK
	public static final String INTERFACES_DOC_STATUS_VOID = "VO";  //ORCL CANCEL
	
	
}
