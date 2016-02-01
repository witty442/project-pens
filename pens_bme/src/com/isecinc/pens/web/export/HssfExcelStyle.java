package com.isecinc.pens.web.export;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class HssfExcelStyle {
	
	public  HSSFFont font;
	public  HSSFFont fontHead;
	public  HSSFFont fontHead2;
	public  HSSFFont fontHeadBoldUnderLine;
	public  HSSFFont fontData;
	public  HSSFFont fontDataHeadColumnBold;
	public  HSSFFont fontDataBold;
	
	public  HSSFCellStyle headerStyle;
	public  HSSFCellStyle headerStyle2;
	public  HSSFCellStyle headerStyleLeft2;
	public  HSSFCellStyle styleHeader;
	public  HSSFCellStyle styleHeaderCenter;
	public  HSSFCellStyle styleHeadButtomLine;
	
	public  HSSFCellStyle dataLineStyleHeaderColumnCenter;
	public  HSSFCellStyle dataLineStyleHeaderColumnRight;
	
	public  HSSFCellStyle dataStyle ;
	public  HSSFCellStyle dataLineStyle;
	public  HSSFCellStyle dataLineCenterStyle;
	public  HSSFCellStyle dataLineStyleRight;
	public  HSSFCellStyle dataLineBoldStyle;
	public  HSSFCellStyle dataLineBoldStyleRight;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public HssfExcelStyle(HSSFWorkbook xssfWorkbook){
		 //Color 
        //HSSFColor myColor = new HSSFColor();
     
		// Font setting for sheet.
        font = xssfWorkbook.createFont();
        font.setFontName("Angsana New");
        font.setFontHeightInPoints((short) 14);

        fontHead = xssfWorkbook.createFont();
        fontHead.setFontName("Angsana New");
        fontHead.setFontHeightInPoints((short) 14);
        fontHead.setBoldweight(Font.BOLDWEIGHT_BOLD);
        
        fontHead2 = xssfWorkbook.createFont();
        fontHead2.setFontName("Tahoma");
        fontHead2.setFontHeightInPoints((short) 12);
        fontHead2.setBoldweight(Font.BOLDWEIGHT_BOLD);
        
        fontHeadBoldUnderLine = xssfWorkbook.createFont();
        fontHeadBoldUnderLine.setFontHeightInPoints((short) 22);
        fontHeadBoldUnderLine.setFontName("Angsana New");
        fontHeadBoldUnderLine.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontHeadBoldUnderLine.setUnderline( Font.U_SINGLE);
        
        fontDataHeadColumnBold = xssfWorkbook.createFont();
        fontDataHeadColumnBold.setFontName("Tahoma");
        fontDataHeadColumnBold.setFontHeightInPoints((short) 11);
        fontDataHeadColumnBold.setBoldweight(Font.BOLDWEIGHT_BOLD);
        
        fontData = xssfWorkbook.createFont();
        fontData.setFontName("Tahoma");
        fontData.setFontHeightInPoints((short) 11);
        
        fontDataBold = xssfWorkbook.createFont();
        fontDataBold.setFontName("Tahoma");
        fontDataBold.setFontHeightInPoints((short) 11);
        fontDataBold.setBoldweight(Font.BOLDWEIGHT_BOLD);

        // Create Styles for sheet.
        headerStyle = xssfWorkbook.createCellStyle();
        headerStyle.setFont(fontHead);
        
        // Create Styles for sheet.
        headerStyle2 = xssfWorkbook.createCellStyle();
        headerStyle2.setFont(fontHead2);
        headerStyle2.setAlignment(CellStyle.ALIGN_CENTER);
        
        headerStyleLeft2 = xssfWorkbook.createCellStyle();
        headerStyleLeft2.setFont(fontHead2);
        headerStyleLeft2.setAlignment(CellStyle.ALIGN_LEFT);
        
        // Create Styles for sheet2.
        styleHeader = xssfWorkbook.createCellStyle();
        styleHeader.setFont(font);
        
       // Create Styles for sheet2.
        styleHeaderCenter = xssfWorkbook.createCellStyle();
        styleHeaderCenter.setFont(fontHeadBoldUnderLine);
        styleHeaderCenter.setAlignment(CellStyle.ALIGN_CENTER);

        // Create Styles for sheet3.
        styleHeadButtomLine = xssfWorkbook.createCellStyle();
        styleHeadButtomLine.setFont(font);
        styleHeadButtomLine.setBorderBottom(CellStyle.BORDER_THICK);
        
        // Create Styles for sheet2.
        dataLineStyleHeaderColumnCenter = xssfWorkbook.createCellStyle();
       // dataLineStyleHeaderColumnCenter.setFillForegroundColor(myColor);
        dataLineStyleHeaderColumnCenter.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        dataLineStyleHeaderColumnCenter.setFont(fontDataHeadColumnBold);
        dataLineStyleHeaderColumnCenter.setAlignment(CellStyle.ALIGN_CENTER);
        dataLineStyleHeaderColumnCenter.setBorderTop(CellStyle.BORDER_THIN);
        dataLineStyleHeaderColumnCenter.setBorderBottom(CellStyle.BORDER_THIN);
        dataLineStyleHeaderColumnCenter.setBorderLeft(CellStyle.BORDER_THIN);
        dataLineStyleHeaderColumnCenter.setBorderRight(CellStyle.BORDER_THIN);
        
        // Create Styles for sheet2.
        dataLineStyleHeaderColumnRight = xssfWorkbook.createCellStyle();
        //dataLineStyleHeaderColumnRight.setFillForegroundColor(myColor);
        dataLineStyleHeaderColumnRight.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        dataLineStyleHeaderColumnRight.setFont(fontDataHeadColumnBold);
        dataLineStyleHeaderColumnRight.setAlignment(CellStyle.ALIGN_RIGHT);
        dataLineStyleHeaderColumnRight.setBorderTop(CellStyle.BORDER_THIN);
        dataLineStyleHeaderColumnRight.setBorderBottom(CellStyle.BORDER_THIN);
        dataLineStyleHeaderColumnRight.setBorderLeft(CellStyle.BORDER_THIN);
        dataLineStyleHeaderColumnRight.setBorderRight(CellStyle.BORDER_THIN);
        
        dataStyle = xssfWorkbook.createCellStyle();
        dataStyle.setFont(fontData);

        dataLineStyle = xssfWorkbook.createCellStyle();
        dataLineStyle.setBorderTop(CellStyle.BORDER_THIN);
        dataLineStyle.setBorderBottom(CellStyle.BORDER_THIN);
        dataLineStyle.setBorderLeft(CellStyle.BORDER_THIN);
        dataLineStyle.setBorderRight(CellStyle.BORDER_THIN);
        dataLineStyle.setWrapText(true);
        dataLineStyle.setFont(fontData);
        
        dataLineCenterStyle = xssfWorkbook.createCellStyle();
        dataLineCenterStyle.setAlignment(CellStyle.ALIGN_CENTER);
        dataLineCenterStyle.setBorderTop(CellStyle.BORDER_THIN);
        dataLineCenterStyle.setBorderBottom(CellStyle.BORDER_THIN);
        dataLineCenterStyle.setBorderLeft(CellStyle.BORDER_THIN);
        dataLineCenterStyle.setBorderRight(CellStyle.BORDER_THIN);
        dataLineCenterStyle.setFont(fontData);
        dataLineCenterStyle.setWrapText(true);
        
        dataLineStyleRight = xssfWorkbook.createCellStyle();
        dataLineStyleRight.setBorderTop(CellStyle.BORDER_THIN);
        dataLineStyleRight.setBorderBottom(CellStyle.BORDER_THIN);
        dataLineStyleRight.setBorderLeft(CellStyle.BORDER_THIN);
        dataLineStyleRight.setBorderRight(CellStyle.BORDER_THIN);
        dataLineStyleRight.setFont(fontData);
        dataLineStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
        
        dataLineBoldStyle = xssfWorkbook.createCellStyle();
        dataLineBoldStyle.setFont(fontDataBold);
        dataLineBoldStyle.setBorderTop(CellStyle.BORDER_THIN);
        dataLineBoldStyle.setBorderBottom(CellStyle.BORDER_THIN);
        dataLineBoldStyle.setBorderLeft(CellStyle.BORDER_THIN);
        dataLineBoldStyle.setBorderRight(CellStyle.BORDER_THIN);
   
        
        dataLineBoldStyleRight = xssfWorkbook.createCellStyle();
        dataLineBoldStyleRight.setFont(fontDataBold);
        dataLineBoldStyleRight.setBorderTop(CellStyle.BORDER_THIN);
        dataLineBoldStyleRight.setBorderBottom(CellStyle.BORDER_THIN);
        dataLineBoldStyleRight.setBorderLeft(CellStyle.BORDER_THIN);
        dataLineBoldStyleRight.setBorderRight(CellStyle.BORDER_THIN);
        dataLineBoldStyleRight.setAlignment(CellStyle.ALIGN_RIGHT);
	}

}
