package com.pens.util.excel;

public class ExcelHeader {
	public static String a= "@";
	static String a2= "#";
	public static StringBuffer EXCEL_HEADER = new StringBuffer("");
	static{
		EXCEL_HEADER.append("<style> \n");
		
		EXCEL_HEADER.append(" .num { \n");
		EXCEL_HEADER.append("  mso-number-format:General; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .num_currency{ \n");
		EXCEL_HEADER.append("   mso-number-format:\\#\\,\\#\\#0; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .num_bold { \n");
		EXCEL_HEADER.append("  mso-number-format:General; \n");
		EXCEL_HEADER.append("  font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .num_currency_bold{ \n");
		EXCEL_HEADER.append("   mso-number-format:\\#\\,\\#\\#0; \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .text{ \n");
		EXCEL_HEADER.append("   mso-number-format:'"+a+"'; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .text_center_bold{ \n");
		EXCEL_HEADER.append("    mso-number-format:'"+a+"'; \n");
		EXCEL_HEADER.append("    font-weight: bold; \n");
		EXCEL_HEADER.append("    text-align: center; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .text_bold{ \n");
		EXCEL_HEADER.append("   mso-number-format:'"+a+"'; \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .text_with_bt_line{ \n");
		EXCEL_HEADER.append("   mso-number-format:'"+a+"'; \n");
		EXCEL_HEADER.append("   border-bottom: 3px solid black; ");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .colum_head{ \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .row_hilight{ \n");
		EXCEL_HEADER.append("   background-color: #909090; \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .currency_with_bt_line{ \n");
		EXCEL_HEADER.append("   mso-number-format:\\#\\,\\#\\#0\\.00; \n");
		EXCEL_HEADER.append("   border-bottom: 3px solid black; ");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .currency{ \n");
		EXCEL_HEADER.append("   mso-number-format:\\#\\,\\#\\#0\\.00; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .currency_bold{ \n");
		EXCEL_HEADER.append("   mso-number-format:\\#\\,\\#\\#0\\.00; \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .currency_bold_with_bt_line{ \n");
		EXCEL_HEADER.append("   mso-number-format:\\#\\,\\#\\#0\\.00; \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append("   border-bottom: 5px solid black; ");
		EXCEL_HEADER.append("   vertical-align: middle; ");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .tr_with_bt_line{ \n");
		EXCEL_HEADER.append("  border-bottom: 4px solid black; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append("</style> \n");
		
		//set support thai unicode  for export excel (table)
		EXCEL_HEADER.append("<meta charset='utf-8'>");
		
	}
}
