package com.isecinc.pens.web.export;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelStyle {
	
	public  XSSFFont font;
	public  XSSFFont fontHead;
	public  XSSFFont fontHead2;
	public  XSSFFont fontHeadBoldUnderLine;
	public  XSSFFont fontData;
	public  XSSFFont fontDataHeadColumnBold;
	public  XSSFFont fontDataBold;
	
	public  XSSFCellStyle headerStyle;
	public  XSSFCellStyle headerStyle2;
	public  XSSFCellStyle headerStyleLeft2;
	public  XSSFCellStyle styleHeader;
	public  XSSFCellStyle styleHeaderCenter;
	public  XSSFCellStyle styleHeadButtomLine;
	
	public  XSSFCellStyle dataLineStyleHeaderColumnCenter;
	public  XSSFCellStyle dataLineStyleHeaderColumnRight;
	
	public  XSSFCellStyle dataStyle ;
	public  XSSFCellStyle dataLineStyle;
	public  XSSFCellStyle dataLineStyleRight;
	public  XSSFCellStyle dataLineBoldStyle;
	public  XSSFCellStyle dataLineBoldStyleRight;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public ExcelStyle(XSSFWorkbook xssfWorkbook){
		 //Color 
        XSSFColor myColor = new XSSFColor(new java.awt.Color(141,180,226));
     
		// Font setting for sheet.
        font = xssfWorkbook.createFont();
        font.setFontName("Angsana New");
        font.setFontHeightInPoints((short) 14);

        fontHead = xssfWorkbook.createFont();
        fontHead.setFontName("Angsana New");
        fontHead.setFontHeightInPoints((short) 14);
        fontHead.setBold(true);
        
        fontHead2 = xssfWorkbook.createFont();
        fontHead2.setFontName("Tahoma");
        fontHead2.setFontHeightInPoints((short) 12);
        fontHead2.setBold(true);
        
        fontHeadBoldUnderLine = xssfWorkbook.createFont();
        fontHeadBoldUnderLine.setFontHeightInPoints((short) 22);
        fontHeadBoldUnderLine.setFontName("Angsana New");
        fontHeadBoldUnderLine.setBold(true);
        fontHeadBoldUnderLine.setUnderline(FontUnderline.SINGLE);
        
        fontDataHeadColumnBold = xssfWorkbook.createFont();
        fontDataHeadColumnBold.setFontName("Tahoma");
        fontDataHeadColumnBold.setFontHeightInPoints((short) 11);
        fontDataHeadColumnBold.setBold(true);
        
        fontData = xssfWorkbook.createFont();
        fontData.setFontName("Tahoma");
        fontData.setFontHeightInPoints((short) 11);
        
        fontDataBold = xssfWorkbook.createFont();
        fontDataBold.setFontName("Tahoma");
        fontDataBold.setFontHeightInPoints((short) 11);
        fontDataBold.setBold(true);

        // Create Styles for sheet.
        headerStyle = xssfWorkbook.createCellStyle();
        headerStyle.setFont(fontHead);
        
        // Create Styles for sheet.
        headerStyle2 = xssfWorkbook.createCellStyle();
        headerStyle2.setFont(fontHead2);
        headerStyle2.setAlignment(HorizontalAlignment.CENTER);
        
        headerStyleLeft2 = xssfWorkbook.createCellStyle();
        headerStyleLeft2.setFont(fontHead2);
        headerStyleLeft2.setAlignment(HorizontalAlignment.LEFT);
        
        // Create Styles for sheet2.
        styleHeader = xssfWorkbook.createCellStyle();
        styleHeader.setFont(font);
        
       // Create Styles for sheet2.
        styleHeaderCenter = xssfWorkbook.createCellStyle();
        styleHeaderCenter.setFont(fontHeadBoldUnderLine);
        styleHeaderCenter.setAlignment(HorizontalAlignment.CENTER);

        // Create Styles for sheet3.
        styleHeadButtomLine = xssfWorkbook.createCellStyle();
        styleHeadButtomLine.setFont(font);
        styleHeadButtomLine.setBorderBottom(BorderStyle.THICK);
        
        // Create Styles for sheet2.
        dataLineStyleHeaderColumnCenter = xssfWorkbook.createCellStyle();
        dataLineStyleHeaderColumnCenter.setFillForegroundColor(myColor);
        dataLineStyleHeaderColumnCenter.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        dataLineStyleHeaderColumnCenter.setFont(fontDataHeadColumnBold);
        dataLineStyleHeaderColumnCenter.setAlignment(HorizontalAlignment.CENTER);
        dataLineStyleHeaderColumnCenter.setBorderTop(BorderStyle.THIN);
        dataLineStyleHeaderColumnCenter.setBorderBottom(BorderStyle.THIN);
        dataLineStyleHeaderColumnCenter.setBorderLeft(BorderStyle.THIN);
        dataLineStyleHeaderColumnCenter.setBorderRight(BorderStyle.THIN);
        
        // Create Styles for sheet2.
        dataLineStyleHeaderColumnRight = xssfWorkbook.createCellStyle();
        dataLineStyleHeaderColumnRight.setFillForegroundColor(myColor);
        dataLineStyleHeaderColumnRight.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        dataLineStyleHeaderColumnRight.setFont(fontDataHeadColumnBold);
        dataLineStyleHeaderColumnRight.setAlignment(HorizontalAlignment.RIGHT);
        dataLineStyleHeaderColumnRight.setBorderTop(BorderStyle.THIN);
        dataLineStyleHeaderColumnRight.setBorderBottom(BorderStyle.THIN);
        dataLineStyleHeaderColumnRight.setBorderLeft(BorderStyle.THIN);
        dataLineStyleHeaderColumnRight.setBorderRight(BorderStyle.THIN);
        
        dataStyle = xssfWorkbook.createCellStyle();
        dataStyle.setFont(fontData);
        
        dataLineStyle = xssfWorkbook.createCellStyle();
        dataLineStyle.setBorderTop(BorderStyle.THIN);
        dataLineStyle.setBorderBottom(BorderStyle.THIN);
        dataLineStyle.setBorderLeft(BorderStyle.THIN);
        dataLineStyle.setBorderRight(BorderStyle.THIN);
        dataLineStyle.setFont(fontData);
        
        dataLineStyleRight = xssfWorkbook.createCellStyle();
        dataLineStyleRight.setBorderTop(BorderStyle.THIN);
        dataLineStyleRight.setBorderBottom(BorderStyle.THIN);
        dataLineStyleRight.setBorderLeft(BorderStyle.THIN);
        dataLineStyleRight.setBorderRight(BorderStyle.THIN);
        dataLineStyleRight.setFont(fontData);
        dataLineStyleRight.setAlignment(HorizontalAlignment.RIGHT);
        
        dataLineBoldStyle = xssfWorkbook.createCellStyle();
        dataLineBoldStyle.setFont(fontDataBold);
        dataLineBoldStyle.setBorderTop(BorderStyle.THIN);
        dataLineBoldStyle.setBorderBottom(BorderStyle.THIN);
        dataLineBoldStyle.setBorderLeft(BorderStyle.THIN);
        dataLineBoldStyle.setBorderRight(BorderStyle.THIN);
   
        
        dataLineBoldStyleRight = xssfWorkbook.createCellStyle();
        dataLineBoldStyleRight.setFont(fontDataBold);
        dataLineBoldStyleRight.setBorderTop(BorderStyle.THIN);
        dataLineBoldStyleRight.setBorderBottom(BorderStyle.THIN);
        dataLineBoldStyleRight.setBorderLeft(BorderStyle.THIN);
        dataLineBoldStyleRight.setBorderRight(BorderStyle.THIN);
        dataLineBoldStyleRight.setAlignment(HorizontalAlignment.RIGHT);
	}

}
