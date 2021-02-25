package com.isecinc.pens.web.batchtask;

public class BatchTaskConstants {
 /** Gobal Parameter Map Name **/
 public static String BATCH_PARAM_MAP  ="BATCH_PARAM_MAP";
 public static String DATA_FILE  ="DATA_FILE";
	
 public static String IMPORT_RECEIPT = "ImportReceipt";
 public static String EXPORT_RECEIPT = "ExportReceipt";
 
 public static final String TRANSACTION_TYPE = "Transaction";
 public static final String TYPE_IMPORT = "IMPORT";
 public static final String TYPE_EXPORT = "EXPORT";
 
 public static final int STATUS_FTP_FAIL = -2;
 public static final int STATUS_FAIL = -1;
 public static final int STATUS_START = 0;
 public static final int STATUS_SUCCESS = 1;
 public static final int STATUS_REGEN = 2;
}