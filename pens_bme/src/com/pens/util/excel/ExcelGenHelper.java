package com.pens.util.excel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelGenHelper {
	 //Color 
    XSSFColor myColor = new XSSFColor(new java.awt.Color(141,180,226));
    XSSFCellStyle headerStyle = null;
    XSSFCellStyle dataStyle = null;
    
	public ExcelGenHelper(XSSFWorkbook xssfWorkbook){
		 // Font setting for sheet.
	    XSSFFont font = xssfWorkbook.createFont();
	    font.setBoldweight((short) 700);
	
	    // Create Styles for sheet.
	    XSSFCellStyle headerStyle = xssfWorkbook.createCellStyle();
	    headerStyle.setFillForegroundColor(myColor);
	    headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
	    headerStyle.setFont(font);
	    headerStyle.setWrapText(true);
	    
	    XSSFCellStyle dataStyle = xssfWorkbook.createCellStyle();
	    dataStyle.setWrapText(true);
	}
	
	public XSSFCell genCell( XSSFRow row ,XSSFCell cell,XSSFCellStyle style){
        cell.setCellStyle(headerStyle);
        cell.setCellValue("no");
        return cell;
	}
}
