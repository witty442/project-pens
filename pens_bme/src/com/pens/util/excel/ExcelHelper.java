package com.pens.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;

public class ExcelHelper {
	
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//readExcel();
		//readExcelXlsx();
		
		//convert xls to Csv
		//File inputFile = new File("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\ImportOrderExcel\\temp.xls");
        //File outputFile = new File("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\ImportOrderExcel\\temp_gen.csv");
        //convertXlsToCsv(inputFile, outputFilie);
	}
	
	/** Case Normal **/
	public static void readExcel(){
		Row row = null;
		Cell cell = null;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		try{
			File inputFile = new File("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\ImportOrderExcel\\temp.xls");
			//File inputFile = new File("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\ImportOrderExcel\\test.xls");
			InputStream wrappedStream = POIFSFileSystem.createNonClosingInputStream(new FileInputStream(inputFile));
			HSSFWorkbook workbook = new HSSFWorkbook(wrappedStream);
	        HSSFSheet sheet = workbook.getSheetAt(0);
	         
		    Cell cellCheck = null;
		    Object cellCheckValue = null;
		    Object cellObjValue = "";
		   
		    /** Loop Row **/
			for (int r = 4; r <=sheet.getLastRowNum() ; r++) {
				row = sheet.getRow(r);
				//logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
				   logger.debug("read row["+r+"] value["+cellCheck.toString()+"]");
			       //cellCheckValue = xslUtils.getCellValue(0, cellCheck);
				   //logger.debug("RowCheck[c,r][0,"+r+"]value["+Utils.isNull(cellCheckValue.toString())+"]");
				}else{
				   cellCheckValue = null;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void readExcelXlsx(){
		Row row = null;
		Cell cell = null;
		UploadXLSUtil xslUtils = new UploadXLSUtil();
		try{
			File inputFile = new File("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\ImportOrderExcel\\Temp.xlsx");
			//File inputFile = new File("D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\ImportOrderExcel\\test.xls");
			InputStream wrappedStream = POIFSFileSystem.createNonClosingInputStream(new FileInputStream(inputFile));
			XSSFWorkbook workbook = new XSSFWorkbook(wrappedStream);
	        XSSFSheet sheet = workbook.getSheetAt(0);
	         
		    Cell cellCheck = null;
		    Object cellCheckValue = null;
		    Object cellObjValue = "";

		    /** Loop Row **/
			for (int r = 4; r <=sheet.getLastRowNum() ; r++) {
				row = sheet.getRow(r);
				//logger.debug("read row["+r+"]");
				
				//Check Stop Loop Col 1
				if(row !=null){
				   cellCheck = row.getCell((short) 0);
				   logger.debug("read row["+r+"] value["+cellCheck.toString()+"]");
			       //cellCheckValue = xslUtils.getCellValue(0, cellCheck);
				   //logger.debug("RowCheck[c,r][0,"+r+"]value["+Utils.isNull(cellCheckValue.toString())+"]");
				}else{
				   cellCheckValue = null;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void convertXlsToCsv(File inputFile, File outputFile) {
	   // For storing data into CSV files
	    StringBuffer data = new StringBuffer();
	    try{
	        FileOutputStream fos = new FileOutputStream(outputFile);

	        // Get the workbook object for XLS file
	        HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(inputFile));
	        // Get first sheet from the workbook
	        HSSFSheet sheet = workbook.getSheetAt(0);
	        Cell cell;
	        Row row;

	        // Iterate through each rows from first sheet
	        Iterator<Row> rowIterator = sheet.iterator();
	        while (rowIterator.hasNext()) {
                row = rowIterator.next();
                // For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                        cell = cellIterator.next();
                        
                        switch (cell.getCellType()) 
                        {
                        case Cell.CELL_TYPE_BOOLEAN:
                              data.append(cell.getBooleanCellValue() + ",");
                              break;
                        case Cell.CELL_TYPE_NUMERIC:
                              data.append(cell.getNumericCellValue() + ",");
                              break;     
                        case Cell.CELL_TYPE_STRING:
                              data.append(cell.getStringCellValue() + ",");
                              break;
                        case Cell.CELL_TYPE_BLANK:
                              data.append("" + ",");
                              break;
                        default:
                              data.append(cell + ",");
                        } 
                }//while cell
                data.append('\n'); 
	        }//while row

	        fos.write(data.toString().getBytes());
	        fos.close();
	        }catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	        catch (IOException e)  {
	            e.printStackTrace();
	        }
	 }
}