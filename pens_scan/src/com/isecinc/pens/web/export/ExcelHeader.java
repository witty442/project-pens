package com.isecinc.pens.web.export;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHeader extends Excel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static int genHeader(XSSFSheet sheet ,XSSFWorkbook xssfWorkbook,ExcelStyle style,int row,String boxNo,String storeCode,String storeName){
		XSSFRow headerRow  = null;
		XSSFCell headerCell1 = null;
		XSSFCell headerCell2 = null;
		XSSFCell headerCell3 = null;
		XSSFCell headerCell4 = null;
		XSSFCell headerCell5 = null;
		XSSFCell headerCell6 = null;
		XSSFCell headerCell7 = null;
		XSSFCell headerCell8 = null;
		XSSFCell headerCell9 = null;
		XSSFCell headerCell10 = null;
		try{
			   // Create Header Row 0
	           headerRow = sheet.createRow(row);
	           
	           headerRow.setHeightInPoints((4*sheet.getDefaultRowHeightInPoints()));
	           
	           //add picture data to this workbook.
	           InputStream is = new FileInputStream("d:/images/head_excel_return_wacoal.png");
	           byte[] bytes = IOUtils.toByteArray(is);
	           int pictureIdx = xssfWorkbook.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
	           is.close();

	           //CreationHelper helper = xssfWorkbook.getCreationHelper();
		       // Create the drawing patriarch. This is the top level container for all shapes. 
		       Drawing drawing = sheet.createDrawingPatriarch();
		       //add a picture shape
		       ClientAnchor anchor = new XSSFClientAnchor();
		       //set top-left corner of the picture,
		       //subsequent call of Picture#resize() will operate relative to it
		       anchor.setRow1(row);
		       anchor.setCol1(1);
		       
		       anchor.setRow2(row);
		       anchor.setCol2(3);
		     
		       Picture pict = drawing.createPicture(anchor, pictureIdx);
		       //auto-size picture relative to its top-left corner
		       pict.resize();
		 
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.headerStyle);
	           headerCell1.setCellValue("");

	           headerCell2 = headerRow.createCell(1);
	           headerCell2.setCellStyle(style.headerStyle);
	           headerCell2.setCellValue("");
	           
	           headerCell3 = headerRow.createCell(2);
	           headerCell3.setCellStyle(style.headerStyle);
	           headerCell3.setCellValue("");
	           
	           headerCell4 = headerRow.createCell(3);
	           headerCell4.setCellStyle(style.headerStyle);
	           headerCell4.setCellValue("");
	           
	           headerCell5 = headerRow.createCell(4);
	           headerCell5.setCellStyle(style.headerStyle);
	           headerCell5.setCellValue("");
	           
	           headerCell6 = headerRow.createCell(5);
	           headerCell6.setCellStyle(style.headerStyle);
	           headerCell6.setCellValue("");
	           
	           headerCell7 = headerRow.createCell(6);
	           headerCell7.setCellStyle(style.headerStyle);
	           headerCell7.setCellValue("");
	           
	           headerCell8 = headerRow.createCell(7);
	           headerCell8.setCellStyle(style.headerStyle);
	           headerCell8.setCellValue("");
	           
	           headerCell9 = headerRow.createCell(8);
	           headerCell9.setCellStyle(style.headerStyle2);
	           headerCell9.setCellValue("กล่องที่ : "+boxNo);
	           
	          // sheet.addMergedRegion(new org.apache.poi.ss.util.Region(0,(short)7,0,(short)8));
	           sheet.addMergedRegion(new CellRangeAddress(row,row,8,9));

	           /*************************************************************/
	           // Create Header Row 4
	           row = row+2;
	           headerRow = sheet.createRow(row);
	           
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.styleHeaderCenter);
	           headerCell1.setCellValue("ใบส่งสินค้าคืนจากร้านค้า");

	           sheet.addMergedRegion(new CellRangeAddress(row,row,0,9));
	      
		       /*************************************************************/
	           // Create Header Row 6
	           row = row+2;
	           headerRow = sheet.createRow(row);
	           
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.styleHeader);
	           headerCell1.setCellValue("รหัสร้านค้า");

	           headerCell2 = headerRow.createCell(1);
	           headerCell2.setCellStyle(style.styleHeadButtomLine);
	           headerCell2.setCellValue(storeCode);
	           
	           headerCell3 = headerRow.createCell(2);
	           headerCell3.setCellStyle(style.styleHeader);
	           headerCell3.setCellValue("สาขา");
	           
	           headerCell4 = headerRow.createCell(3);
	           headerCell4.setCellStyle(style.styleHeadButtomLine);
	           headerCell4.setCellValue(storeName);
	           
	           headerCell5 = headerRow.createCell(4);
	           headerCell5.setCellStyle(style.styleHeadButtomLine);
	           headerCell5.setCellValue(appendBlank(10));
	           //Merg col 3,4
	           sheet.addMergedRegion(new CellRangeAddress(row,row,3,4));
	           
	           headerCell6 = headerRow.createCell(5);
	           headerCell6.setCellStyle(style.styleHeader);
	           headerCell6.setCellValue("");
	           
	           headerCell7 = headerRow.createCell(6);
	           headerCell7.setCellStyle(style.styleHeader);
	           headerCell7.setCellValue("");
	           
	           headerCell8 = headerRow.createCell(7);
	           headerCell8.setCellStyle(style.styleHeader);
	           headerCell8.setCellValue("");
	           
	           headerCell9 = headerRow.createCell(8);
	           headerCell9.setCellStyle(style.styleHeader);
	           headerCell9.setCellValue("");
	           
	           headerCell10 = headerRow.createCell(9);
	           headerCell10.setCellStyle(style.styleHeader);
	           headerCell10.setCellValue("");
	           
	           /*************************************************************/
	           // Create Header Row 7  FAIL
	           row++;
	           headerRow = sheet.createRow(row);
	           
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.styleHeader);
	           headerCell1.setCellValue("ผลิตภัณฑ์");

	           headerCell2 = headerRow.createCell(1);
	           headerCell2.setCellStyle(style.styleHeadButtomLine);
	           headerCell2.setCellValue(appendBlank(20));
	           
	           headerCell3 = headerRow.createCell(2);
	           headerCell3.setCellStyle(style.styleHeader);
	           headerCell3.setCellValue("ผู้รับผิดชอบ");
	           
	           headerCell4 = headerRow.createCell(3);
	           headerCell4.setCellStyle(style.styleHeadButtomLine);
	           headerCell4.setCellValue(appendBlank(10));
	           
	           headerCell5 = headerRow.createCell(4);
	           headerCell5.setCellStyle(style.styleHeader);
	           headerCell5.setCellValue("เหตุผลในการคืน");
	           
	           headerCell6 = headerRow.createCell(5);
	           headerCell6.setCellStyle(style.styleHeadButtomLine);
	           headerCell6.setCellValue(appendBlank(10));
	           
	           headerCell7 = headerRow.createCell(6);
	           headerCell7.setCellStyle(style.styleHeadButtomLine);
	           headerCell7.setCellValue(appendBlank(20));
	           
	           headerCell8 = headerRow.createCell(7);
	           headerCell8.setCellStyle(style.styleHeader);
	           headerCell8.setCellValue("คอนเนอร์");
	           
	           headerCell9 = headerRow.createCell(8);
	           headerCell9.setCellStyle(style.styleHeadButtomLine);
	           headerCell9.setCellValue(appendBlank(20));
	           
	           headerCell10 = headerRow.createCell(9);
	           headerCell10.setCellStyle(style.styleHeader);
	           headerCell10.setCellValue("");
	           
	           /*************************************************************/
	           // Create Header Row 8
	           row++;
	           headerRow = sheet.createRow(row);
	           
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.styleHeader);
	           headerCell1.setCellValue("วันที่ตรวจนับ");

	           headerCell2 = headerRow.createCell(1);
	           headerCell2.setCellStyle(style.styleHeadButtomLine);
	           headerCell2.setCellValue(appendBlank(20));
	           
	           headerCell3 = headerRow.createCell(2);
	           headerCell3.setCellStyle(style.styleHeader);
	           headerCell3.setCellValue("ชนิดสินค้าทั้งหมด");
	           
	           headerCell4 = headerRow.createCell(3);
	           headerCell4.setCellStyle(style.styleHeadButtomLine);
	           headerCell4.setCellValue(appendBlank(20));
	           
	           headerCell5 = headerRow.createCell(4);
	           headerCell5.setCellStyle(style.styleHeader);
	           headerCell5.setCellValue("เลขที่อ้างอิง");
	           
	           headerCell6 = headerRow.createCell(5);
	           headerCell6.setCellStyle(style.styleHeadButtomLine);
	           headerCell6.setCellValue(appendBlank(20));
	           
	           headerCell7 = headerRow.createCell(6);
	           headerCell7.setCellStyle(style.styleHeader);
	           headerCell7.setCellValue("วันที่");
	           
	           headerCell8 = headerRow.createCell(7);
	           headerCell8.setCellStyle(style.styleHeadButtomLine);
	           headerCell8.setCellValue(appendBlank(20));
	           
	           headerCell9 = headerRow.createCell(8);
	           headerCell9.setCellStyle(style.styleHeader);
	           headerCell9.setCellValue("จำนวนเงิน");
	           
	           headerCell10 = headerRow.createCell(9);
	           headerCell10.setCellStyle(style.styleHeadButtomLine);
	           headerCell10.setCellValue(appendBlank(20));
	           /*************************************************************/
	           // Create Header Row 9
	           row++;
	           headerRow = sheet.createRow(row);
	           
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.styleHeader);
	           headerCell1.setCellValue("");

	           headerCell2 = headerRow.createCell(1);
	           headerCell2.setCellStyle(style.styleHeader);
	           headerCell2.setCellValue("");
	           
	           headerCell3 = headerRow.createCell(2);
	           headerCell3.setCellStyle(style.styleHeader);
	           headerCell3.setCellValue(appendBlank(10));
	           
	           headerCell4 = headerRow.createCell(3);
	           headerCell4.setCellStyle(style.styleHeader);
	           headerCell4.setCellValue(appendBlank(10));
	           
	           headerCell5 = headerRow.createCell(4);
	           headerCell5.setCellStyle(style.styleHeader);
	           headerCell5.setCellValue("");
	           
	           headerCell6 = headerRow.createCell(5);
	           headerCell6.setCellStyle(style.styleHeadButtomLine);
	           headerCell6.setCellValue(appendBlank(20));
	           
	           headerCell7 = headerRow.createCell(6);
	           headerCell7.setCellStyle(style.styleHeader);
	           headerCell7.setCellValue("");
	           
	           headerCell8 = headerRow.createCell(7);
	           headerCell8.setCellStyle(style.styleHeadButtomLine);
	           headerCell8.setCellValue(appendBlank(20));
	           
	           headerCell9 = headerRow.createCell(8);
	           headerCell9.setCellStyle(style.styleHeader);
	           headerCell9.setCellValue("");
	           
	           headerCell10 = headerRow.createCell(9);
	           headerCell10.setCellStyle(style.styleHeadButtomLine);
	           headerCell10.setCellValue(appendBlank(20));

	           /*************************************************************/
	           // Create Header Row 10
	           row++;
	           headerRow = sheet.createRow(row);
	           
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.styleHeader);
	           headerCell1.setCellValue("");

	           headerCell2 = headerRow.createCell(1);
	           headerCell2.setCellStyle(style.styleHeader);
	           headerCell2.setCellValue("");
	           
	           headerCell3 = headerRow.createCell(2);
	           headerCell3.setCellStyle(style.styleHeader);
	           headerCell3.setCellValue(appendBlank(10));
	           
	           headerCell4 = headerRow.createCell(3);
	           headerCell4.setCellStyle(style.styleHeader);
	           headerCell4.setCellValue(appendBlank(10));
	           
	           headerCell5 = headerRow.createCell(4);
	           headerCell5.setCellStyle(style.styleHeader);
	           headerCell5.setCellValue("");
	           
	           headerCell6 = headerRow.createCell(5);
	           headerCell6.setCellStyle(style.styleHeadButtomLine);
	           headerCell6.setCellValue(appendBlank(20));
	           
	           headerCell7 = headerRow.createCell(6);
	           headerCell7.setCellStyle(style.styleHeader);
	           headerCell7.setCellValue("");
	           
	           headerCell8 = headerRow.createCell(7);
	           headerCell8.setCellStyle(style.styleHeadButtomLine);
	           headerCell8.setCellValue(appendBlank(20));
	           
	           headerCell9 = headerRow.createCell(8);
	           headerCell9.setCellStyle(style.styleHeader);
	           headerCell9.setCellValue("");
	           
	           headerCell10 = headerRow.createCell(9);
	           headerCell10.setCellStyle(style.styleHeadButtomLine);
	           headerCell10.setCellValue(appendBlank(20));
	           
	           /*************************************************************/
	           // Create Header Row 11
	           row++;
	           headerRow = sheet.createRow(row);
	           
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.styleHeader);
	           headerCell1.setCellValue("");

	           headerCell2 = headerRow.createCell(1);
	           headerCell2.setCellStyle(style.styleHeader);
	           headerCell2.setCellValue("");
	           
	           headerCell3 = headerRow.createCell(2);
	           headerCell3.setCellStyle(style.styleHeader);
	           headerCell3.setCellValue(appendBlank(10));
	           
	           headerCell4 = headerRow.createCell(3);
	           headerCell4.setCellStyle(style.styleHeader);
	           headerCell4.setCellValue("");
	           
	           headerCell5 = headerRow.createCell(4);
	           headerCell5.setCellStyle(style.styleHeader);
	           headerCell5.setCellValue(appendBlank(10));
	           
	           headerCell6 = headerRow.createCell(5);
	           headerCell6.setCellStyle(style.styleHeadButtomLine);
	           headerCell6.setCellValue(appendBlank(20));
	           
	           headerCell7 = headerRow.createCell(6);
	           headerCell7.setCellStyle(style.styleHeader);
	           headerCell7.setCellValue("");
	           
	           headerCell8 = headerRow.createCell(7);
	           headerCell8.setCellStyle(style.styleHeadButtomLine);
	           headerCell8.setCellValue(appendBlank(20));
	           
	           headerCell9 = headerRow.createCell(8);
	           headerCell9.setCellStyle(style.styleHeader);
	           headerCell9.setCellValue("");
	           
	           headerCell10 = headerRow.createCell(9);
	           headerCell10.setCellStyle(style.styleHeadButtomLine);
	           headerCell10.setCellValue(appendBlank(20));
	           
	           /***************Head Column *******************************/
	           // Create Header Row 14
	           row = row+3;
	           headerRow = sheet.createRow(row);
	           
	           headerCell1 = headerRow.createCell(0);
	           headerCell1.setCellStyle(style.dataStyle);
	           headerCell1.setCellValue("");

	           headerCell2 = headerRow.createCell(1);
	           headerCell2.setCellStyle(style.dataLineStyleHeaderColumnCenter);
	           headerCell2.setCellValue("NO.");
	           
	           headerCell3 = headerRow.createCell(2);
	           headerCell3.setCellStyle(style.dataLineStyleHeaderColumnCenter);
	           headerCell3.setCellValue("MAT-CODE");
	           
	           headerCell4 = headerRow.createCell(3);
	           headerCell4.setCellStyle(style.dataLineStyleHeaderColumnCenter);
	           headerCell4.setCellValue(appendBlank(10));
	           //Merg col 3,4
	           sheet.addMergedRegion(new CellRangeAddress(row,row,2,3));
	           
	           headerCell5 = headerRow.createCell(4);
	           headerCell5.setCellStyle(style.dataLineStyleHeaderColumnCenter);
	           headerCell5.setCellValue("QTY");
	           
	           headerCell6 = headerRow.createCell(5);
	           headerCell6.setCellStyle(style.dataLineStyleHeaderColumnCenter);
	           headerCell6.setCellValue("PRICE UNIT");
	           
	           headerCell7 = headerRow.createCell(6);
	           headerCell7.setCellStyle(style.dataLineStyleHeaderColumnCenter);
	           headerCell7.setCellValue("Total");
	           
	           headerCell8 = headerRow.createCell(7);
	           headerCell8.setCellStyle(style.dataStyle);
	           headerCell8.setCellValue("");
	           
	           headerCell9 = headerRow.createCell(8);
	           headerCell9.setCellStyle(style.dataStyle);
	           headerCell9.setCellValue("");
	           
	           headerCell10 = headerRow.createCell(9);
	           headerCell10.setCellStyle(style.dataStyle);
	           headerCell10.setCellValue(appendBlank(20));
	           
		}catch(Exception e){
			e.printStackTrace();
		}
		return row;
	}

}
