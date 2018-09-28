package com.isecinc.pens.web.imports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class FilePosBMEProcess {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static ActionForward importFilePostBME(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		ImportForm importForm = (ImportForm) form;
        UploadXLSUtil xslUtils = new UploadXLSUtil();
	    PreparedStatement ps = null;
	    PreparedStatement psDelete = null;
	    User user = (User) request.getSession().getAttribute("user");
	    String fileName = "";
	    String fileType = "";
		try {

			FormFile dataFile = importForm.getDataFile();
			if (dataFile != null) {
				fileName = dataFile.getFileName();
				fileType = fileName.substring(fileName.indexOf(".")+1,fileName.length());
				conn = DBConnection.getInstance().getConnection();
				
				logger.debug("contentType: " + dataFile.getContentType());
				logger.debug("fileName: " + dataFile.getFileName());
				
				 /** Delete All before Import **/
				psDelete = conn.prepareStatement("delete from FILE_POST_BME_TEMP");
				psDelete.executeUpdate();

				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO FILE_POST_BME_TEMP( \n");
				
				sql.append(" lot,no, pens_item, Group_code, item_name,  \n");//1-4
				sql.append(" Retail_Price_BF, Whole_Price_BF, Pens_Price_BF, Pens_Price_BF_Percent, \n");//5-8
				sql.append(" discount_Percent, Special_Price_BF,CREATE_DATE,CREATE_USER ,item_type ) \n");//9-12
				
				sql.append(" VALUES(?,?,?,?,?" +
						",?,?,?,?" +
						",?,?,?,?,?)");

				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql.toString());
				  
				int sheetNo = 0; // xls sheet no. or name
				int rowNo = 4; // row of begin data
				int maxColumnNo = 18; // max column of data per row
				
				String itemType = "";//1 bra ,2 underware
				Workbook wb1 = null;
				XSSFWorkbook wb2 = null;
				Sheet sheet = null;
				
				for(int s=0;s<2;s++){
					sheetNo = s;
					if(s==0){
						itemType = "1";
					}else{
						itemType = "2";
					}
					if("xls".equalsIgnoreCase(fileType)){
					   wb1 = new HSSFWorkbook(dataFile.getInputStream());//97-2003
					   sheet = wb1.getSheetAt(sheetNo);
					   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
					}else{
					   OPCPackage pkg = OPCPackage.open(dataFile.getInputStream());
					   wb2 = new XSSFWorkbook(pkg);
					   sheet = wb2.getSheetAt(sheetNo);
					   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
					}
					
					Row row = null;
					Cell cell = null;
	
					int index = 0;
					logger.debug("select sheet(" + (sheetNo + 1) + ") name: " + sheet.getSheetName());
		            logger.debug("getLastRowNum:"+sheet.getLastRowNum());
		            
					for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
						row = sheet.getRow(i);
						
						/** Check Row is null **/
						try{
							//logger.debug("row["+i+"]object["+row+"]");
							Cell cellCheck = row.getCell((short)9);
							//logger.debug("cell:"+cell.getStringCellValue());
							Object cellCheckValue = xslUtils.getCellValue((short)9, cellCheck);
							logger.debug("col[8]cellCheckValue["+cellCheckValue+"]");
							
							String rowCheck =  "";
							if(cellCheckValue != null)
								rowCheck =cellCheckValue.toString();
							
							logger.debug("rowCheck["+rowCheck+"]");
							
							if(cellCheckValue == null || rowCheck.equals("")){
								break;
							}
						}catch(Exception e){
							logger.debug("Error :Row["+i+"] Break");
							break;
						}
						//initial
						index = 1;
						for (int colNo = 0; colNo < maxColumnNo; colNo++) {
							cell = row.getCell((short) colNo);
							logger.debug("row["+i+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
							
							Object cellValue = xslUtils.getCellValue(colNo, cell);
							//lot
							if(colNo==0){
								ps.setString(index++, Utils.isNull(cellValue));
							}else if(colNo==8){
								//no
								ps.setString(index++, Utils.isNull(cellValue));
							}else if(colNo==9){
							   //pens_item
								String pensItem = Utils.isNull(cellValue).substring(0,Utils.isNull(cellValue).indexOf("."));
							    ps.setString(index++, pensItem);
							    //ps.setString(index++,"XX");
							}else if(colNo==10){
							   //Group_code
							   ps.setString(index++, Utils.isNull(cellValue));
							   
							}else if(colNo==11){
							  //item_name
							  ps.setString(index++,Utils.isNull(cellValue));
								    
							}else if(colNo==12){
							  //Retail_Price_BF 
							   ps.setDouble(index++,Utils.isDoubleNull2Digit(cellValue));
							
							}else if(colNo==13){
							  //Whole_Price_BF
							   ps.setDouble(index++,Utils.isDoubleNull2Digit(cellValue));
							 
							}else if(colNo==14){
							  //Pens_Price_BF
							   ps.setDouble(index++,Utils.isDoubleNull2Digit(cellValue));
						
							}else if(colNo==15){
							   //Pens_Price_BF_Percent
							   ps.setDouble(index++, Utils.isDoubleNull2Digit(cellValue));
				
							}else if(colNo==16){
							  //discount_Percent
								 ps.setDouble(index++, Utils.isDoubleNull2Digit(cellValue));
							
						    }else if(colNo==17){
							  //Special_Price_BF
							    ps.setDouble(index++, Utils.isDoubleNull2Digit(cellValue));
							
							}
							 
						}//for column
						 ps.setTimestamp(index++, new java.sql.Timestamp(new Date().getTime()));
						 ps.setString(index++, user.getUserName());
						 ps.setString(index++, itemType);
						 
						 ps.executeUpdate();
					}//for Row
				}//for sheet
			}
			
			 request.setAttribute("Message","Upload ‰ø≈Ï "+fileName+"  ”‡√Á®");
			conn.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message","Upload‰ø≈Ï ‰¡Ë‰¥È:"+e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				if(conn != null){
			    	 conn.close();conn=null;
			      }
			      if(ps != null){
			    	 ps.close();ps=null;
			      }
			      if(psDelete != null){
				     psDelete.close();psDelete=null;
				  }
			} catch (Exception e2) {}
		}

		return mapping.findForward("success");
	}
	
	public static StringBuffer genExcelHTML(HttpServletRequest request,ImportForm form){
		StringBuffer h = new StringBuffer("");
		int colspan = 12;
		Statement stmt = null;
		ResultSet rst = null;
		List<PopupForm> pos = new ArrayList<PopupForm>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		int no = 0;
		String groupCode = "";
		String itemType = "";
		String retailPrice = "";
		String ddText = "";
		double Discount_Percent = 0;
		double specialpriceDB = 0;
		double retailPriceDB =0;
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table border='1'> \n");
			
			sql.append("select * from FILE_POST_BME_TEMP order by item_type,pens_item");
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			while (rst.next()) {
				 groupCode = Utils.isNull(rst.getString("group_code"));
				 itemType = Utils.isNull(rst.getString("item_type"));
				 
				  //Validate Pens_Price_BF_Percent Mod 10 <> DD00 ->  √“§“¢“¬∑’Ë1=√“§“¢“¬æ‘‡»…
				 Discount_Percent = new Double(rst.getDouble("Discount_Percent")*100);
				 
				 if(Discount_Percent ==0){
					 ddText ="D00";
					 specialpriceDB = rst.getDouble("Special_Price_BF");
					// logger.debug("specialpriceDB:"+specialpriceDB);
					 retailPrice = Utils.decimalFormat(specialpriceDB,Utils.format_current_2_disgit);
				 }else{
					 if("870007".equals(rst.getString("pens_item"))){
					   logger.debug("Discount_Percent["+Discount_Percent+"]");
					   logger.debug("%10["+Discount_Percent % 10+"]" );
					 }
					 if(Discount_Percent % 10 != 0){
						 ddText ="D00";
						 specialpriceDB = rst.getDouble("Special_Price_BF");
						 //logger.debug("specialpriceDB:"+specialpriceDB);
						 retailPrice = Utils.decimalFormat(specialpriceDB,Utils.format_current_2_disgit);
					 }else{
						 ddText ="D"+Utils.decimalFormat(Discount_Percent,Utils.format_current_no_disgit); ;
						 retailPriceDB = rst.getDouble("Retail_Price_BF");
						 retailPrice = Utils.decimalFormat(retailPriceDB,Utils.format_current_2_disgit); 
					 }
				 }  
				h.append("<tr> \n");
				  h.append("<td class='text'>"+Utils.isNull(rst.getString("pens_item"))+"</td> \n");//1
				  h.append("<td class='text'>"+Utils.isNull(rst.getString("pens_item"))+"</td> \n"); //2 
				  h.append("<td class='text'>"+genDocNo(itemType,groupCode)+"</td> \n");//3
				  h.append("<td class='text'>"+Utils.isNull(rst.getString("item_name"))+"</td> \n");//4
				  h.append("<td class='text'>"+Utils.isNull(rst.getString("item_name"))+"</td> \n");//5
				  h.append("<td class='text'>001</td> \n");//6
				  h.append("<td class='text'>™‘Èπ</td> \n");//7
				  h.append("<td class='text'>V</td> \n");//8
				  h.append("<td class='text'>7</td> \n");//9
				  h.append("<td class='text'>"+Utils.decimalFormat(rst.getDouble("Pens_Price_BF"),Utils.format_current_2_disgit)+"</td> \n");//10
				  
				  h.append("<td class='text'>"+retailPrice+"</td> \n");//11
				  h.append("<td class='text'>"+ddText+"</td> \n");//12
				h.append("</tr>");
			}
			h.append("</table> \n");
				
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	/**
	 *  00010001	BRA - Type 0	0
		00010002	BRA - Type 1	1
		00010003	BRA - Type 2	2
		00010004	BRA - OTHERS	>2
		
		00020001	UNDER - Type 0	0
		00020002	UNDER - Type 1	1
		00020003	UNDER - Type 2	2
		00020004	UNDER - OTHERS	>2

	 * @param itemType
	 * @param groupCode
	 * @return
	 */
	public static String genDocNo(String itemType,String groupCode){
		String r = "";
		String s = groupCode.substring(3,4);
		//System.out.println("s:"+s);
		if("1".equals(itemType)){
			if("0".equals(s)){
				r ="00010001";
			}else if("1".equals(s)){
				r ="00010002";
			}else if("2".equals(s)){
				r = "00010003";
			}else{
				r = "00010004";
			}
		}else if("2".equals(itemType)){
			if("0".equals(s)){
				r ="00020001";
			}else if("1".equals(s)){
				r ="00020002";
			}else if("2".equals(s)){
				r = "00020003";
			}else{
				r = "00020004";
			}
		}
		
		return r;
	}
	
}
