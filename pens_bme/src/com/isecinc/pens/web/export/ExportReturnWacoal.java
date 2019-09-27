package com.isecinc.pens.web.export;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class ExportReturnWacoal extends Excel {

	protected static Logger logger = Logger.getLogger("PENS");
	public static int MAX_ROW = 10;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


	public static XSSFWorkbook genExportToExcel(User user) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
		int sheet = 0;
		try{
			conn = DBConnection.getInstance().getConnection();
		
			sql.append("  SELECT DISTINCT STORE_CODE,BOX_NO FROM PENSBME_RETURN_WACOAL WHERE TRUNC(IMPORT_DATE) = TRUNC(?) ORDER BY  STORE_CODE,BOX_NO ASC \n");
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				logger.debug("Gen Excel StoreCode:"+rs.getString("STORE_CODE")+",BoxNo:"+rs.getString("BOX_NO"));
				genExportToExcelModel(conn,user,xssfWorkbook,rs.getString("STORE_CODE"),rs.getString("BOX_NO"));
				
				sheet++;
			}
			logger.debug("sql:"+sql);
			
		  //adjust width of the column
		  for(int s=0;s<sheet;s++){
		       XSSFSheet sheet2 = xssfWorkbook.getSheetAt(s);
		       /*sheet2.autoSizeColumn(0); 
		       sheet2.autoSizeColumn(1); 
		       sheet2.autoSizeColumn(3); 
		       sheet2.autoSizeColumn(4); 
		       sheet2.autoSizeColumn(5); 
		       for(int r=0;r < MAX_ROW;r++){
			     sheet2.autoSizeColumn(r); 
		       }*/
		       sheet2.setColumnWidth(0, 3000); 
		       sheet2.setColumnWidth(1, 3300);
		       sheet2.setColumnWidth(2, 3300);
		       sheet2.setColumnWidth(3, 3300);
		       sheet2.setColumnWidth(4, 3300);
		       sheet2.setColumnWidth(5, 3300);
		       sheet2.setColumnWidth(6, 2500);
		       sheet2.setColumnWidth(7, 3300);
		       sheet2.setColumnWidth(8, 2500);
		       sheet2.setColumnWidth(9, 3300);
		  }   
				
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rs.close();rs = null;
                ps.close();ps = null;
				conn.close();conn = null;
			} catch (Exception e) {}
		}
		return xssfWorkbook;
	}
		
	private static XSSFWorkbook genExportToExcelModel(Connection conn,User user,XSSFWorkbook xssfWorkbook ,String storeCodeP,String boxNo) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int row = 0;
		//int c0 = 0,c1=0,c2=0,c3=0,c4=0,c5=0,c6=0,c7=0,c8=0,c9=0,c10=0;
		try {
		   // Create Sheet.
           XSSFSheet sheet = xssfWorkbook.createSheet("Store_"+storeCodeP+"_BOX_NO_"+boxNo);

           ExcelStyle style = new ExcelStyle(xssfWorkbook);
        
           /*******************DATA ********************************/
            sql.append("select A.* \n");
			sql.append("FROM( \n");
			sql.append("  SELECT * FROM PENSBME_RETURN_WACOAL WHERE TRUNC(IMPORT_DATE) = TRUNC(?) and BOX_NO = ? and STORE_CODE =?  \n");
			sql.append(" )A \n");
			sql.append(" ORDER BY A.STORE_CODE,A.GROUP_ITEM,A.COLOR_SIZE ASC  \n");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(2, boxNo);
			ps.setString(3, storeCodeP);
			
			rst = ps.executeQuery();
	        int no = 0;
	        String groupItemTemp  ="";
	        String groupItemDisp  ="";
	        double totalQty = 0;
	        double total = 0;
	        double totalQtyByGroup = 0;
	        double totalByGroup = 0;

			while (rst.next()) {
				no++;
				if(no==1){
			       /*****************Gen Header*****************************/
				   String storeCode = Utils.isNull(rst.getString("store_code"));
				   String storeName = (new ImportDAO().getStoreName("Store", storeCode)).getPensDesc();
		           row = ExcelHeader.genHeader(sheet, xssfWorkbook, style,row,boxNo,storeCode,storeName);
		           logger.debug("After Gen Head Row="+row);
				}
				
				String GROUP_ITEM = Utils.isNull(rst.getString("GROUP_ITEM"));
				String COLOR_SIZE = Utils.isNull(rst.getString("COLOR_SIZE"));
				double RETURN_QTY = rst.getDouble("RETURN_QTY");
				double RETAIL_PRICE_BF =rst.getDouble("RETAIL_PRICE_BF");
				double TOTAL_RETAIL = rst.getDouble("RETURN_QTY")*rst.getDouble("RETAIL_PRICE_BF");

				groupItemDisp = "";
				 if(!groupItemTemp.equals(GROUP_ITEM)){
					groupItemDisp = GROUP_ITEM;
				}
				 
				 logger.debug("NO["+no+"]groupItemTemp["+groupItemTemp+"]--GROUP_ITEM["+GROUP_ITEM+"]");
		         if(no > 1 && !groupItemTemp.equals(GROUP_ITEM)){
				  //Summary
		           row++;
		           logger.debug("Create Summary Row:"+row);
		           XSSFRow dataRow = sheet.createRow(row);
		           genSummaryByGroupItem(style,sheet,dataRow,row,groupItemTemp,totalQtyByGroup,Utils.isNull(totalByGroup));
	
		           totalQtyByGroup =0;
		           totalByGroup=0;
					
		         }
				   // Create First Data Row
				   row++;
				   logger.debug("Create Row:"+row);
		           XSSFRow dataRow = sheet.createRow(row); 

		           XSSFCell cell1 = dataRow.createCell(0);
		           cell1.setCellStyle(style.dataStyle);
		           cell1.setCellValue("");
		           
		           XSSFCell cell2 = dataRow.createCell(1);
		           cell2.setCellStyle(style.dataLineStyle);
		           cell2.setCellValue(new XSSFRichTextString(no+""));
		           
		           XSSFCell cell3 = dataRow.createCell(2);
		           cell3.setCellStyle(style.dataLineStyle);
		           if("".equals(groupItemDisp)){
		              cell3.setCellValue(appendBlank(10));
		           }else{
		        	   cell3.setCellValue(groupItemDisp);  
		           }
		           XSSFCell cell4 = dataRow.createCell(3);
		           cell4.setCellStyle(style.dataLineStyle);
		           cell4.setCellValue(new XSSFRichTextString(COLOR_SIZE));
		           
		           XSSFCell cell5 = dataRow.createCell(4);
		           cell5.setCellStyle(style.dataLineStyle);
		           cell5.setCellValue(new XSSFRichTextString(Utils.decimalFormat(RETURN_QTY, Utils.format_current_no_disgit)));
		           
		           XSSFCell cell6 = dataRow.createCell(5);
		           cell6.setCellStyle(style.dataLineStyle);
		           cell6.setCellValue(new XSSFRichTextString(Utils.isNull(RETAIL_PRICE_BF)));
		           
		           XSSFCell cell7 = dataRow.createCell(6);
		           cell7.setCellStyle(style.dataLineStyle);
		           cell7.setCellValue(new XSSFRichTextString(Utils.isNull(TOTAL_RETAIL)));
		           
		           XSSFCell cell8 = dataRow.createCell(7);
		           cell8.setCellStyle(style.dataStyle);
		           cell8.setCellValue("");
		           
		           XSSFCell cell9 = dataRow.createCell(8);
		           cell9.setCellStyle(style.dataStyle);
		           cell9.setCellValue("");
		         
		           XSSFCell cell10 = dataRow.createCell(9);
		           cell10.setCellStyle(style.dataStyle);
		           cell10.setCellValue(appendBlank(20));
		           
		           //Calculate
		           totalQtyByGroup += RETURN_QTY;
		           totalByGroup += TOTAL_RETAIL;
		           
		           //Total ALL
		           totalQty += RETURN_QTY;
		           total += TOTAL_RETAIL;
		           
		         
				
				groupItemTemp = GROUP_ITEM;
	           
			}
			
			//Last Summary By group Item
			 row++;
			 logger.debug("Create Summary Last Row:"+row);
			 XSSFRow  dataRow = sheet.createRow(row);
			 genSummaryByGroupItem(style,sheet,dataRow,row,groupItemTemp,totalQtyByGroup,Utils.isNull(totalByGroup));
           
	         //Last Total Summary
			 row++;
			 logger.debug("Create Total Summary Row:"+row);
			 dataRow = sheet.createRow(row);
	         genTotalSummary(style,sheet,dataRow,row,groupItemTemp,totalQty,Utils.isNull(total));
	         
           /********************ROW************************/
           
           /******************* DATA *****************************/
         
		   
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();rst = null;
                ps.close();ps = null;
				
			} catch (Exception e) {}
		}
		return xssfWorkbook;
	}
	
	private static void genSummaryByGroupItem(ExcelStyle style,XSSFSheet sheet ,XSSFRow dataRow,int row,String groupItem,double totalQty,String total){
		//Summary
        dataRow = sheet.createRow(row);

        XSSFCell cell1 = dataRow.createCell(0);
        cell1.setCellStyle(style.dataStyle);
        cell1.setCellValue("");
        
        XSSFCell cell2 = dataRow.createCell(1);
        cell2.setCellStyle(style.dataLineStyle);
        cell2.setCellValue("");
        
        XSSFCell cell3 = dataRow.createCell(2);
        cell3.setCellStyle(style.dataLineBoldStyleRight);
        cell3.setCellValue(new XSSFRichTextString(groupItem));

        XSSFCell cell4 = dataRow.createCell(3);
        cell4.setCellStyle(style.dataLineStyle);
        cell4.setCellValue("");
       
        XSSFCell cell5 = dataRow.createCell(4);
        cell5.setCellStyle(style.dataLineBoldStyleRight);
        cell5.setCellValue(Utils.decimalFormat(totalQty, Utils.format_current_no_disgit));
        
        XSSFCell cell6 = dataRow.createCell(5);
        cell6.setCellStyle(style.dataLineStyle);
        cell6.setCellValue("");
        
        XSSFCell cell7 = dataRow.createCell(6);
        cell7.setCellStyle(style.dataLineBoldStyleRight);
        cell7.setCellValue(total);
        
        XSSFCell cell8 = dataRow.createCell(7);
        cell8.setCellStyle(style.dataStyle);
        cell8.setCellValue("");
        
        XSSFCell cell9 = dataRow.createCell(8);
        cell9.setCellStyle(style.dataStyle);
        cell9.setCellValue("");
      
        XSSFCell cell10 = dataRow.createCell(9);
        cell10.setCellStyle(style.dataStyle);
        cell10.setCellValue("");
	}
	
	private static void genTotalSummary(ExcelStyle style,XSSFSheet sheet ,XSSFRow dataRow,int row,String groupItem,double totalQty,String total){
		//Summary
        dataRow = sheet.createRow(row); 

        XSSFCell cell1 = dataRow.createCell(0);
        cell1.setCellStyle(style.dataStyle);
        cell1.setCellValue("");
        
        XSSFCell cell2 = dataRow.createCell(1);
        cell2.setCellStyle(style.dataLineBoldStyleRight);
        cell2.setCellValue("Total");
        
        XSSFCell cell3 = dataRow.createCell(2);
        cell3.setCellStyle(style.dataLineBoldStyle);
        cell3.setCellValue("");
      
        XSSFCell cell4 = dataRow.createCell(3);
        cell4.setCellStyle(style.dataLineStyle);
        cell4.setCellValue("");
        
        //Merg 1-3
        sheet.addMergedRegion(new CellRangeAddress(row,row,1,3));

        XSSFCell cell5 = dataRow.createCell(4);
        cell5.setCellStyle(style.dataLineBoldStyleRight);
        cell5.setCellValue(Utils.decimalFormat(totalQty, Utils.format_current_no_disgit));
        
        XSSFCell cell6 = dataRow.createCell(5);
        cell6.setCellStyle(style.dataLineStyle);
        cell6.setCellValue("");
        
        XSSFCell cell7 = dataRow.createCell(6);
        cell7.setCellStyle(style.dataLineBoldStyleRight);
        cell7.setCellValue(total);
        
        XSSFCell cell8 = dataRow.createCell(7);
        cell8.setCellStyle(style.dataStyle);
        cell8.setCellValue("");
        
        XSSFCell cell9 = dataRow.createCell(8);
        cell9.setCellStyle(style.dataStyle);
        cell9.setCellValue("");
      
        XSSFCell cell10 = dataRow.createCell(9);
        cell10.setCellStyle(style.dataStyle);
        cell10.setCellValue("");
	}
	
	
	public static StringBuffer exportExcelBK(){
		Connection conn = null;
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			h.append("<table > \n");
			
			h.append("<tr height='74px'> \n");
			h.append("<td align='center' colspan='8' ><img src='./head_excel_return_wacoal.png'></td> \n");
			h.append("<td align='right' colspan='2'>Box 1</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr  height='50px'> \n");
			h.append("<td align='left' colspan='10'>ใบส่งสินค้าคืนจากร้านค้า</td> \n");
			h.append("</tr> \n");
			//blank
			h.append("<tr height='10px'> \n");
			h.append("<td align='left' colspan='10' >&nbsp;</td> \n");
			h.append("</tr> \n");
			
			h.append("<tr height='30px'> \n");
				h.append("<td align='left'>รหัสร้านค้า</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>สาขา</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left' colspan ='5'>&nbsp;</td> \n");
			h.append("</tr> \n");
			h.append("<tr height='30px'> \n");
				h.append("<td align='left'>ผลิตภัณฑ์</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>ผู้รับผิดชอบ</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>เหตุผลในการคืน</td> \n");
				h.append("<td align='left' colspan='2'>____________________</td> \n"); //20
				h.append("<td align='left'>คอนเนอร์</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
		    h.append("</tr> \n");
		    h.append("<tr height='30px'> \n");
				h.append("<td align='left'>วันที่ตรวจนับ</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>ชนิดสินค้า  ทั้งหมด</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>เเลขที่อ้างอิง</td> \n");
				h.append("<td align='left'>__________</td> \n"); //20
				h.append("<td align='left'>วันที่</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>จำนวนเงิน</td> \n");
				h.append("<td align='left'>__________</td> \n");
		    h.append("</tr> \n");
		    h.append("<tr height='30px'> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n"); //20
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n");
		    h.append("</tr> \n");
			h.append("<tr height='30px'> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n"); //20
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n");
		    h.append("</tr> \n");
		    h.append("<tr height='30px'> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n"); //20
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n");
				h.append("<td align='left'>&nbsp;</td> \n");
				h.append("<td align='left'>__________</td> \n");
		    h.append("</tr> \n");
			h.append("</table> \n");

		
		}catch(Exception e){
			
		}
		return h;
	}

}
