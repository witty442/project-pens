package com.pens.util.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelGenHelper {
	 //Color 
    XSSFColor myColor = new XSSFColor(new java.awt.Color(141,180,226));
    //XSS
    XSSFCellStyle headerStyle = null;
    XSSFCellStyle textStyle = null;
    XSSFCellStyle numStyle = null;
    XSSFCellStyle currencyStyle = null;
    
    //HSSF
    
	public ExcelGenHelper(XSSFWorkbook xssfWorkbook){
		CreationHelper createHelper = xssfWorkbook.getCreationHelper();
	    //style cell
		numStyle= xssfWorkbook.createCellStyle();
		currencyStyle= xssfWorkbook.createCellStyle();
		textStyle= xssfWorkbook.createCellStyle();
		
	    numStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));
	    currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
	}
	
	//XSSF (xlsx) new version
	public XSSFCell genCell( XSSFRow row ,int cellNo,String cellType,Object cellValue){
		XSSFCell cell = row.createCell(cellNo);
		if("NUMBER".equalsIgnoreCase(cellType)){
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(numStyle);
			cell.setCellValue((Double)cellValue);
		}else  if("CURRENCY".equalsIgnoreCase(cellType)){
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(currencyStyle);
			cell.setCellValue((Double)cellValue);
		}else{
			//Text
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellStyle(currencyStyle);
			cell.setCellValue((String)cellValue);
		}
        return cell;
	}
	//HSSF (xls) excel 93
	public Cell genCell(Row row ,int cellNo,String cellType,Object cellValue){
		Cell cell = row.createCell(cellNo);
		if("NUMBER".equalsIgnoreCase(cellType)){
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(numStyle);
			cell.setCellValue((Double)cellValue);
		}else  if("CURRENCY".equalsIgnoreCase(cellType)){
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(currencyStyle);
			cell.setCellValue((Double)cellValue);
		}else{
			//Text
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellStyle(currencyStyle);
			cell.setCellValue((String)cellValue);
		}
        return cell;
	}
}
