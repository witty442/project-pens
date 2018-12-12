package util;

public class ExcelHeader {
	static String a= "@";
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
		
		EXCEL_HEADER.append(" .colum_head{ \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .row_hilight{ \n");
		EXCEL_HEADER.append("   background-color: #909090; \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .currency{ \n");
		EXCEL_HEADER.append("   mso-number-format:\\#\\,\\#\\#0\\.00; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append(" .currency_bold{ \n");
		EXCEL_HEADER.append("   mso-number-format:\\#\\,\\#\\#0\\.00; \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		
		EXCEL_HEADER.append("</style> \n");
		
	}
}
