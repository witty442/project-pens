package util;

public class ExcelHeader {
	static String a= "@";
	public static StringBuffer EXCEL_HEADER = new StringBuffer("");
	static{
		EXCEL_HEADER.append("<style> \n");
		EXCEL_HEADER.append(" .num { \n");
		EXCEL_HEADER.append("  mso-number-format:General; \n");
		EXCEL_HEADER.append(" } \n");
		EXCEL_HEADER.append(" .text{ \n");
		EXCEL_HEADER.append("   mso-number-format:'"+a+"'; \n");
		EXCEL_HEADER.append(" } \n");
		EXCEL_HEADER.append(" .colum_head{ \n");
		EXCEL_HEADER.append("   font-weight: bold; \n");
		EXCEL_HEADER.append(" } \n");
		EXCEL_HEADER.append("</style> \n");
	
	}
}
