package com.isecinc.pens.inf.manager.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ImportDAO;
import com.isecinc.pens.dao.OrderDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.MonitorItemBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.web.export.ExcelStyle;
import com.isecinc.pens.web.export.ExportTimeSheetGroup;
import com.isecinc.pens.web.export.HssfExcelStyle;

public class GenerateOrderExcel extends InterfaceUtils{
	private static Logger logger = Logger.getLogger("PENS");
	public final static String PARAM_FILE_NAME = "FILE_NAME";
	public final static String PARAM_OUTPUT_PATH = "OUTPUT_PATH";
	public final static String PARAM_CUST_GROUP = "CUST_GROUP";
	public final static String PARAM_TRANS_DATE = "TRANS_DATE";//Budish Date
	public final static String PARAM_REAL_PATH_TEMP = "REAL_PATH_TEMP";
    private static int row = 0;
	
	public static MonitorItemBean runProcess(User user,MonitorItemBean monitorItemBean,Map<String, String> batchParamMap) throws Exception{
		EnvProperties env = EnvProperties.getInstance();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps =null;
		StringBuffer sql = new StringBuffer("");
		int result = Constants.STATUS_FAIL;
		boolean nextStep = true;
		boolean foundData = false;
		int successCount = 0;
		int failCount = 0;
	    Map<String, String> configMap = new HashMap<String, String>();
	    String serverTempPath = "";
	    
		//Field
		HSSFWorkbook workbook = new HSSFWorkbook();
		List<StoreBean> storeList = new ArrayList<StoreBean>();
		List<Order> orderList = new ArrayList<Order>();
		row = 0;
		try{
			//Create Connection
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//Prepare FTP Manager **/
			/** Check Status FTP Server Alive  if Cant Connection Throw Exception*/
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.icc.ip.server"), env.getProperty("ftp.icc.username"), env.getProperty("ftp.icc.password"));
			ftpManager.canConnectFTPServer();
			
			//Prepare parameter
			Date date = Utils.parse(batchParamMap.get(PARAM_TRANS_DATE), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String realPathTemp  = batchParamMap.get(PARAM_REAL_PATH_TEMP);
			
			//Get Config Map ALL
			configMap = InterfaceUtils.getConfigInterfaceAllBySubject(conn, "GEN-ORDER");
			
		    /** Init Excel Object **/
	        HSSFSheet sheet = workbook.createSheet("orders");
	        HssfExcelStyle style = new HssfExcelStyle(workbook);
	        
	        /** Gen Header Excel **/
	        genTopHeaderExcel(sheet, workbook,style,configMap);

			/** Get StoreList **/
	        storeList = getStoreList(conn,configMap,batchParamMap);
	        
	        /** Gen Excel Sub Head Sheet **/
	        genSubHeaderExcel(sheet, workbook, style, storeList, "รหัสคอนเนอร์");
	        genSubHeaderExcel(sheet, workbook, style, storeList, "รหัสร้านค้า");
	        genSubHeaderExcel(sheet, workbook, style, storeList, "ชื่อร้านค้า");
  
	        /** Gen Excel Coumn Head Sheet **/
	        genColumnHeaderExcel(sheet, workbook, style,storeList);
	        
	        /** Get Data Order By StoreList **/
	        orderList = getOrderByStoreList(conn, storeList, batchParamMap);
	        
	        /** Gen Excel Detail Sheet **/
	        Map<String,String> totalByStoreMap = genDetailExcel(sheet, workbook, style,orderList,storeList,configMap);
	        /** Gen GranTotal Row **/
	        genGranTotalSExcel(sheet, workbook, style, orderList, storeList, totalByStoreMap);
	        
	      //adjust width of the column
	       HSSFSheet sheet2 = workbook.getSheetAt(0);
	       sheet2.autoSizeColumn(0);
	       sheet2.autoSizeColumn(1);
	       sheet2.autoSizeColumn(2);
	       sheet2.autoSizeColumn(3);
	       sheet2.autoSizeColumn(4);
	       sheet2.autoSizeColumn(5);
	       if(storeList !=null){
	    	   int column = 6;
	    	   for(int c=0;c<storeList.size();c++){
	    		   //sheet2.autoSizeColumn(column);
	    		   column++;
	    	   }
	       }
			  
			/** Case Found Error No Gen File **/
			if(nextStep && foundData==false){
				 result = Constants.STATUS_SUCCESS;

			     //write file to local
				 serverTempPath = env.getProperty("path.backup.icc.hisher.export.orderexcel")+"/"+batchParamMap.get(PARAM_FILE_NAME);
				 FileUtil.writeExcel(serverTempPath, workbook);
				 logger.debug("localTempPath:"+serverTempPath);
				 
			   //  String ftpFullPathName = env.getProperty("path.icc.hisher.export.orderexcel")+"/"+batchParamMap.get(PARAM_FILE_NAME);
			    // logger.debug("ftpFullPathName:"+ftpFullPathName);
			     
			 	//  logger.debug("Step Upload Excel File To FTP Server");
				 // ftpManager.uploadExcelByFTP(localTempPath,env.getProperty("path.icc.hisher.export.orderexcel"),ftpFullPathName,workbook);
			 	  
				 // ftpManager.uploadExcelByFTP_Method1(env.getProperty("path.icc.hisher.export.orderexcel"),ftpFullPathName,workbook);
				  
			     //Update Exported ='Y' in BME_ORDER
			     logger.debug("Update Exported ='G' in BME_ORDER");
			     int updateCount = updateOrderExportFlag(conn, user.getUserName(), date, batchParamMap.get(PARAM_CUST_GROUP));
			     logger.debug("updateCount:"+updateCount);

			     logger.debug("Update Exported ='G' in STOCK_ISSUE");
			     updateCount = updateStockIssueExportFlag(conn, user.getUserName(), date, batchParamMap.get(PARAM_CUST_GROUP));
			     logger.debug("updateCount:"+updateCount);
			}else if(!foundData){
				
				result = Constants.STATUS_SUCCESS;
			}
			
			logger.debug("Con Commit");
			conn.commit();
			
			//monitorItemBean.setOutputFile(outputFile);
			monitorItemBean.setStatus(result);
			monitorItemBean.setSuccessCount(successCount);
			monitorItemBean.setFailCount(failCount);
			monitorItemBean.setFileName(batchParamMap.get(PARAM_FILE_NAME));
			
		}catch(Exception e){
			conn.rollback();
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
				if(ps != null){
					ps.close();ps=null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}catch(Exception e){}
		}
		return monitorItemBean;	
	}
	
	public static void genTopHeaderExcel(HSSFSheet sheet ,HSSFWorkbook xssfWorkbook,HssfExcelStyle style,Map<String, String> configMap){
		HSSFRow headerRow  = null;
		HSSFCell headerCell1 = null;
		HSSFCell headerCell2 = null;
		HSSFCell headerCell3 = null;
		HSSFCell headerCell4 = null;
		HSSFCell headerCell5 = null;
		HSSFCell headerCell6 = null;
		HSSFCell headerCell7 = null;
		try{
		   /****  Create Header Row 1 ***/
           headerRow = sheet.createRow(row);
           headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
           //Header Row 0
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellValue("");
           
           headerCell2 = headerRow.createCell(1);
           headerCell2.setCellValue("");
           
           headerCell3 = headerRow.createCell(2);
           headerCell3.setCellValue("");
           
           headerCell4 = headerRow.createCell(3);
           headerCell4.setCellValue("");
           
           headerCell5 = headerRow.createCell(4);
           headerCell5.setCellValue("");
          
           headerCell6 = headerRow.createCell(5);
           headerCell6.setCellValue("");
           
           headerCell7 = headerRow.createCell(6);
           headerCell7.setCellValue("");
           
		   /****  Create Header Row 2 ***/
           row = row +1;
           headerRow = sheet.createRow(row);
           headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.dataStyle);
           headerCell1.setCellValue("รหัส ธุรกิจ");
           
           headerCell2 = headerRow.createCell(1);
           headerCell2.setCellStyle(style.dataLineStyle);
           headerCell2.setCellValue(configMap.get("BUS_CODE"));
           
           headerCell3 = headerRow.createCell(2);
           headerCell3.setCellStyle(style.dataStyle);
           headerCell3.setCellValue("รหัส ผลิตภัณฑ์");
           
           headerCell4 = headerRow.createCell(3);
           headerCell4.setCellStyle(style.dataLineStyle);
           headerCell4.setCellValue(configMap.get("DEPT_CODE")+configMap.get("PRODUCT_CODE")+configMap.get("SPD_CODE"));
           
           headerCell5 = headerRow.createCell(4);
           headerCell5.setCellStyle(style.dataStyle);
           headerCell5.setCellValue("เกรดสินค้า");
          
           headerCell6 = headerRow.createCell(5);
           headerCell6.setCellStyle(style.dataLineStyle);
           headerCell6.setCellValue(Utils.convertStrToDouble(configMap.get("GRADE")));
           
           headerCell7 = headerRow.createCell(6);
           headerCell7.setCellValue("");
           
           /***** Create Header Row 3 ***/
           row = row +1;
           headerRow = sheet.createRow(row);
           headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
           
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.dataStyle);
           headerCell1.setCellValue("รหัส ค่าใช้จ่าย");
           
           headerCell2 = headerRow.createCell(1);
           headerCell2.setCellStyle(style.dataLineStyle);
           headerCell2.setCellValue(configMap.get("EXPENSE_CODE"));
           
           headerCell3 = headerRow.createCell(2);
           headerCell3.setCellStyle(style.dataStyle);
           headerCell3.setCellValue("รหัส ผู้แบ่งสินค้า");
           
           headerCell4 = headerRow.createCell(3);
           headerCell4.setCellStyle(style.dataLineStyle);
           headerCell4.setCellValue(Utils.convertStrToDouble(configMap.get("SP_PROD_ID")));
           
           headerCell5 = headerRow.createCell(4);
           headerCell5.setCellValue("");
          
           headerCell6 = headerRow.createCell(5);
           headerCell6.setCellValue("");
           
           headerCell7 = headerRow.createCell(6);
           headerCell7.setCellValue("");
           
           /**** Create Header Row 4 ***/
           row = row +1;
           headerRow = sheet.createRow(row);
           headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
         
           headerCell1 = headerRow.createCell(0);
           headerCell1.setCellStyle(style.dataStyle);
           headerCell1.setCellValue("รหัส SITE");
           
           headerCell2 = headerRow.createCell(1);
           headerCell2.setCellStyle(style.dataLineStyle);
           headerCell2.setCellValue(configMap.get("SITE_ID"));
           
           headerCell3 = headerRow.createCell(2);
           headerCell3.setCellStyle(style.dataStyle);
           headerCell3.setCellValue("รหัส WH");
           
           headerCell4 = headerRow.createCell(3);
           headerCell4.setCellStyle(style.dataLineStyle);
           headerCell4.setCellValue(Utils.convertStrToDouble(configMap.get("WAREHOUSE")));
           
           headerCell5 = headerRow.createCell(4);
           headerCell5.setCellValue("");
          
           headerCell6 = headerRow.createCell(5);
           headerCell6.setCellValue("");
           
           headerCell7 = headerRow.createCell(6);
           headerCell7.setCellValue("");

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void genSubHeaderExcel(HSSFSheet sheet ,HSSFWorkbook xssfWorkbook,HssfExcelStyle style,List<StoreBean> storeList,String subTxt){
		HSSFRow headerRow  = null;
		HSSFCell headerCell1 = null;
		row++;
		int columnAt = 0;
		try{
			  headerRow = sheet.createRow(row);
	          headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
	      
	          headerCell1 = headerRow.createCell(0);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue(subTxt);
	          
	          headerCell1 = headerRow.createCell(1);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);

	          headerCell1 = headerRow.createCell(2);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          
	          headerCell1 = headerRow.createCell(3);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);

	          headerCell1 = headerRow.createCell(4);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);

	          headerCell1 = headerRow.createCell(5);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);

	          headerCell1 = headerRow.createCell(6);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	        
	          sheet.addMergedRegion(new CellRangeAddress(row,row,0,6));//Merge Cell 0-6
	          
	          columnAt = 7;
	          //Gen Column by StoreList
	          if(storeList != null){
	        	  for(int c=0;c<storeList.size();c++){
	        		  StoreBean s = storeList.get(c);
	        		  
	        		  headerCell1 = headerRow.createCell(columnAt);
	        		  headerCell1.setCellStyle(style.dataLineCenterStyle);
	        		  
	        		  if("รหัสคอนเนอร์".equalsIgnoreCase(subTxt)){
	    	             headerCell1.setCellValue(s.getStoreCorner());
	        		  }else  if("รหัสร้านค้า".equalsIgnoreCase(subTxt)){
	        			 headerCell1.setCellValue(Utils.convertStrToDouble(s.getStoreNo()));
                      }else  if("ชื่อร้านค้า".equalsIgnoreCase(subTxt)){
                    	 headerCell1.setCellValue(""); 
	        		  }
	    	          columnAt++;
	        	  }
	          }
	          
	          //Gen Column Gran Total
	    	  headerCell1 = headerRow.createCell(columnAt);
	    	  headerCell1.setCellStyle(style.dataLineStyle);
	          headerCell1.setCellValue("");
			
		}catch(Exception e){
		  e.printStackTrace();
		}
	}
	
	public static  void genColumnHeaderExcel(HSSFSheet sheet ,HSSFWorkbook xssfWorkbook,HssfExcelStyle style,List<StoreBean> storeList){
		HSSFRow headerRow  = null;
		HSSFCell headerCell1 = null;
		row++;
		int columnAt = 0;
		try{
			  headerRow = sheet.createRow(row);
	          headerRow.setHeightInPoints((8*sheet.getDefaultRowHeightInPoints()));
	          
	          headerCell1 = headerRow.createCell(0);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("รหัส");
	          
	          headerCell1 = headerRow.createCell(1);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("ราคา");
	          
	          headerCell1 = headerRow.createCell(2);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("สี");
	          
	          headerCell1 = headerRow.createCell(3);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("ไซร์");
	          
	          headerCell1 = headerRow.createCell(4);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("แถม");
	          
	          headerCell1 = headerRow.createCell(5);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("WH");
	          
	          headerCell1 = headerRow.createCell(6);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("SKU");
	          
	          columnAt = 7;
	          //Gen Column by StoreList
	          if(storeList != null){
	        	  for(int c=0;c<storeList.size();c++){
	        		  StoreBean s = storeList.get(c);
	        		  
	        		  headerCell1 = headerRow.createCell(columnAt);
	        		  headerCell1.setCellStyle(style.dataLineCenterStyle);
	        		  headerCell1.setCellValue(s.getStoreName());
	        		
	    	          columnAt++;
	        	  }
	          }
	          
	          //Gen Column Gran Total
	    	  headerCell1 = headerRow.createCell(columnAt);
	    	  headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("Grand Total");
			
		}catch(Exception e){
		  e.printStackTrace();
		}
	}
	
	public static Map<String,String> genDetailExcel(HSSFSheet sheet ,HSSFWorkbook xssfWorkbook,HssfExcelStyle style
			,List<Order> orderList, List<StoreBean> storeList,Map<String, String> configMap){
		
		HSSFRow headerRow  = null;
		HSSFCell headerCell1 = null;
		row++;
		int columnAt = 0;
		String mat = "";
		Map<String,String> totalByStoreMap = new HashMap<String, String>();
		try{
			 if(orderList != null && orderList.size() >0){
				 logger.debug("orderList:"+orderList.size());
				 
				 for(int r=0;r<orderList.size();r++){
					  Order o = orderList.get(r);
					  mat = o.getMaterialMaster();
					  //ME1186A5ME
					  logger.debug("mat["+mat+"]length["+mat.length()+"]");
					  
					  headerRow = sheet.createRow(row);
			          headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
			      
			          headerCell1 = headerRow.createCell(0);
			          headerCell1.setCellStyle(style.dataLineCenterStyle);
			          headerCell1.setCellValue(o.getMaterialMaster());
			          
			          headerCell1 = headerRow.createCell(1);
			          headerCell1.setCellStyle(style.dataLineCenterStyle);
			         headerCell1.setCellValue(Utils.convertStrToDouble(o.getRetailPriceBF()));
			           
			          headerCell1 = headerRow.createCell(2);
			          headerCell1.setCellStyle(style.dataLineCenterStyle);
			          headerCell1.setCellValue(mat.substring(8,10));
			          
			          headerCell1 = headerRow.createCell(3);
			          headerCell1.setCellStyle(style.dataLineCenterStyle);
			          headerCell1.setCellValue(mat.substring(6,8));
			          
			          headerCell1 = headerRow.createCell(4);
			          headerCell1.setCellStyle(style.dataLineCenterStyle);
			          headerCell1.setCellValue("N");
			          
			          headerCell1 = headerRow.createCell(5);
			          headerCell1.setCellStyle(style.dataLineCenterStyle);
			          headerCell1.setCellValue(Utils.convertStrToDouble(configMap.get("WAREHOUSE")));
			          
			          headerCell1 = headerRow.createCell(6);
			          headerCell1.setCellStyle(style.dataLineCenterStyle);
			          headerCell1.setCellValue(Utils.convertStrToDouble(o.getOnhandQty()));//Summary Gran Total
			         
			          columnAt = 7;
			          //Gen Column by StoreList
			          if(o.getStoreItemList() != null && o.getStoreItemList().size()>0){ 
				        	for(int c=0;c<o.getStoreItemList().size();c++){
				              StoreBean storeItem = (StoreBean)o.getStoreItemList().get(c);
			        		  
			        		  headerCell1 = headerRow.createCell(columnAt);
			        		  headerCell1.setCellStyle(style.dataLineCenterStyle);
			        		  headerCell1.setCellValue(Utils.convertStrToDouble(storeItem.getQty()));
			        		
			    	          columnAt++;
			    	          
			    	          //Sum Qty by StoreCode
			    	          if(totalByStoreMap.get(storeItem.getStoreCode()) != null){
			    	        	  int totalQty = Utils.convertStrToInt(totalByStoreMap.get(storeItem.getStoreCode())) + Utils.convertStrToInt(storeItem.getQty());
			    	        	  totalByStoreMap.put(storeItem.getStoreCode(),String.valueOf(totalQty));
			    	          }else{
			    	        	  int totalQty = Utils.convertStrToInt(storeItem.getQty());
			    	        	  totalByStoreMap.put(storeItem.getStoreCode(),String.valueOf(totalQty));
			    	          }
			        	  }//for 2
			          }//if 
			          
			          //Gen Column Gran Total
			    	  headerCell1 = headerRow.createCell(columnAt);
			    	  headerCell1.setCellStyle(style.dataLineCenterStyle);
			          headerCell1.setCellValue(Utils.convertStrToDouble(o.getOnhandQty()));
			          
			          row++;
				 }//for 1
			 }//if orderList
			 
		}catch(Exception e){
		  e.printStackTrace();
		}
		return totalByStoreMap;
	}
	
	public static void genGranTotalSExcel(HSSFSheet sheet ,HSSFWorkbook xssfWorkbook,HssfExcelStyle style,
			List<Order> orderList, List<StoreBean> storeList,Map<String,String> totalByStoreMap){
		
		HSSFRow headerRow  = null;
		HSSFCell headerCell1 = null;
		int columnAt = 0;
		int grandTotalQty = 0;
		try{
			  headerRow = sheet.createRow(row);
	          headerRow.setHeightInPoints((1*sheet.getDefaultRowHeightInPoints()));
	      
	          headerCell1 = headerRow.createCell(0);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("");
	          
	          headerCell1 = headerRow.createCell(1);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("");
	           
	          headerCell1 = headerRow.createCell(2);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("");
	          
	          headerCell1 = headerRow.createCell(3);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("");
	          
	          headerCell1 = headerRow.createCell(4);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("");
	          
	          headerCell1 = headerRow.createCell(5);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("");
	          
	          headerCell1 = headerRow.createCell(6);
	          headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue("");//Summary Gran Total
	         
	          columnAt = 7;
	          //Gen Column by StoreList
	          if(storeList !=null){ 
		        	for(int c=0;c<storeList.size();c++){
		              StoreBean storeItem = (StoreBean)storeList.get(c);
		              String totalQty = totalByStoreMap.get(storeItem.getStoreCode());
		              grandTotalQty += Utils.convertStrToInt(totalQty);
		            
	        		  headerCell1 = headerRow.createCell(columnAt);
	        		  headerCell1.setCellStyle(style.dataLineCenterStyle);
	        		  headerCell1.setCellValue(Utils.convertStrToDouble(totalQty));
	        		
	    	          columnAt++;
	    	          
	   
	        	  }//for 2
	          }//if 
	          
	          //Gen Column Gran Total
	    	  headerCell1 = headerRow.createCell(columnAt);
	    	  headerCell1.setCellStyle(style.dataLineCenterStyle);
	          headerCell1.setCellValue(Utils.convertStrToDouble(grandTotalQty+""));
	          
	          row++;
		}catch(Exception e){
		  e.printStackTrace();
		}
	}
	
	public static boolean validateExportedFlagN(Connection connMonitor,String storeType,String transDate) {
		return getExportedFlag(connMonitor,storeType,transDate,"N");
	}
	public static boolean validateExportedFlagG(Connection connMonitor,String storeType,String transDate) {
		return getExportedFlag(connMonitor,storeType,transDate,"G");
	}
	
	public static boolean getExportedFlag(Connection connMonitor,String storeType,String transDate,String exportedFlag) {
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean found = false;
		try{
			Date date = Utils.parse(transDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(*) as c from PENSBME_ORDER where ORDER_DATE = ? \n");
			sql.append(" and store_type ='"+storeType+"'");
			if(Utils.isNull(exportedFlag).equals("N")){
				sql.append(" and ( exported = '"+exportedFlag+"' OR exported is  null) \n");
			}else{
				sql.append(" and exported = '"+exportedFlag+"'\n");
			}
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = connMonitor.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(date.getTime()));
			rs = ps.executeQuery();
			
			if(rs.next()){
				if(rs.getInt("c") >0){
					found = true;
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
				if(rs != null){
				   rs.close();rs = null;
				}
			}catch(Exception e){}
		}
		return found;
	}
	
	
	public static List<StoreBean> getStoreList(Connection conn,Map<String, String> configMap,Map<String, String> batchParamMap) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<StoreBean> storeList = new ArrayList<StoreBean>();
		StringBuilder sql = new StringBuilder();
		try{
			Date orderDate = Utils.parse(batchParamMap.get(PARAM_TRANS_DATE), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			sql.append("\n SELECT DISTINCT A.store_code ,A.store_no ,A.store_name FROM(");
			
			/** ORDER **/
			sql.append("\n 	SELECT O.store_code, M.interface_value as store_no,M.pens_desc as store_name");
			sql.append("\n 	FROM PENSBME_ORDER O  ");
			sql.append("\n 	INNER JOIN PENSBME_MST_REFERENCE M	 ");
			sql.append("\n  ON  O.store_code = M.pens_value   ");
			sql.append("\n  AND M.reference_code = 'Store'    ");
			sql.append("\n 	where O.store_type = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 	and O.order_date = ?  ");
			sql.append("\n 	and O.exported = 'Y'");
			
			sql.append("\n 	UNION ALL ");
			
			/** Stock_ISSUE **/
			sql.append("\n 	SELECT H.customer_no as store_code ,h.store_no ");
			sql.append("\n 	,(select Max(M.pens_desc) from PENSBME_MST_REFERENCE M where M.reference_code = 'Store' and M.pens_value = H.customer_no) as store_name ");
			sql.append("\n 	from PENSBME_STOCK_ISSUE H ,PENSBME_STOCK_ISSUE_ITEM I  ");
			sql.append("\n 	WHERE 1=1 ");
			sql.append("\n 	and H.ISSUE_REQ_NO = I.ISSUE_REQ_NO ");
			sql.append("\n 	and H.STATUS = '"+PickConstants.STATUS_ISSUED+"'");
			sql.append("\n 	and H.cust_group = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 	and H.delivery_date = ?  ");
			sql.append("\n 	and H.exported = 'Y' ");
			
			sql.append("\n)A ");
			
			logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			ps.setDate(2, new java.sql.Date(orderDate.getTime()));
			
			rs = ps.executeQuery();
			while(rs.next()){
				StoreBean m = new StoreBean();
				m.setStoreCode(Utils.isNull(rs.getString("store_code")));
				m.setStoreName(Utils.isNull(rs.getString("store_name")));
				m.setStoreNo(Utils.isNull(rs.getString("store_no")));
				m.setStoreCorner(configMap.get("CONNER"));
				storeList.add(m);
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return storeList;
	} 
	
	public static List<Order> getOrderByStoreList(Connection conn,List<StoreBean> storeList,Map<String, String> batchParamMap) throws Exception {
		logger.debug("prepareNewOrder");
		PreparedStatement ps = null;
		ResultSet rst = null;
		List<Order> orderList = new ArrayList<Order>();
		StringBuilder sql = new StringBuilder();
		int n=0;
		List<StoreBean> storeItemList = null;
		StoreBean s = null;
		String keyMap = "";
		StoreBean store = null;
		try {
			Date orderDate = Utils.parse(batchParamMap.get(PARAM_TRANS_DATE), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			sql.append("\n SELECT DISTINCT " );
			sql.append("\n A.mat ,A.WHOLE_PRICE_BF, A.RETAIL_PRICE_BF ");
			sql.append("\n FROM( ");
			sql.append("\n 		SELECT  DISTINCT " );
			sql.append("\n      ( select max(M.interface_value) from PENSBME_MST_REFERENCE  M");
			sql.append("\n        WHERE  O.barcode = M.interface_desc     ");
			sql.append("\n        AND M.reference_code = 'LotusItem' )  as mat ");
			sql.append("\n      ,NVL(P.WHOLE_PRICE_BF,0) as WHOLE_PRICE_BF, NVL(P.RETAIL_PRICE_BF,0) as RETAIL_PRICE_BF ");
			sql.append("\n 		FROM PENSBME_ORDER O  ");
			sql.append("\n 		LEFT OUTER JOIN PENSBME_PRICELIST P   ");
			sql.append("\n   		ON  P.group_code = O.group_code ");
			sql.append("\n   		AND P.STORE_TYPE ='"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		where O.store_type = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		and O.order_date = ?  ");
			sql.append("\n 		and O.exported = 'Y'");
			
			sql.append("\n 	    UNION ALL ");
			/** Stock_ISSUE **/
			sql.append("\n 		SELECT DISTINCT" );
			sql.append("\n      ( select max(M.interface_value) from PENSBME_MST_REFERENCE  M");
			sql.append("\n        WHERE  I.barcode = M.interface_desc     ");
			sql.append("\n        AND M.reference_code = 'LotusItem' )  as mat ");
			sql.append("\n      ,NVL(P.WHOLE_PRICE_BF,0) as WHOLE_PRICE_BF, NVL(P.RETAIL_PRICE_BF,0) as RETAIL_PRICE_BF ");
			sql.append("\n 		from PENSBME_STOCK_ISSUE H ,PENSBME_STOCK_ISSUE_ITEM I  ");
			sql.append("\n 		LEFT OUTER JOIN PENSBME_PRICELIST P   ");
			sql.append("\n 		    ON P.group_code = I.group_code ");
			sql.append("\n   		AND P.STORE_TYPE ='"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		WHERE 1=1 ");
			sql.append("\n 		and H.ISSUE_REQ_NO = I.ISSUE_REQ_NO ");
			sql.append("\n 		and H.STATUS = '"+PickConstants.STATUS_ISSUED+"'");
			sql.append("\n 		and H.cust_group = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		and H.delivery_date = ?  ");
			sql.append("\n 		and H.exported = 'Y' ");

			sql.append("\n )A ");
			sql.append("\n order by A.mat ");
			
			logger.debug("sql:"+sql.toString());
			
			//Get StoreBeanMap 
			Map<String, StoreBean> storeBeanOrderMap = getStoreBeanOrderMap(conn, batchParamMap);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			ps.setDate(2, new java.sql.Date(orderDate.getTime()));
			
			rst = ps.executeQuery();

			while (rst.next()) {
				Order item = new Order();
                item.setMaterialMaster(Utils.isNull(rst.getString("mat")));
				item.setWholePriceBF(Utils.decimalFormat(rst.getDouble("Whole_Price_BF"),Utils.format_current_2_disgit)+" ");
				item.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("Retail_Price_BF"),Utils.format_current_2_disgit)+" ");

				int lineQty = 0;
				//** add Store 
				//** OrderNo ,qty by StoreCode
				if(storeList != null && storeList.size() >0){
					storeItemList = new ArrayList<StoreBean>();
					for(int c =0;c<storeList.size();c++){
						s = (StoreBean)storeList.get(c);
						
						keyMap = s.getStoreCode()+"_"+item.getMaterialMaster();
						//logger.debug("KeyMap["+keyMap+"]");
						store = storeBeanOrderMap.get(keyMap)!=null?(StoreBean)storeBeanOrderMap.get(keyMap):null;
						if(store!= null){
							store.setStoreName(s.getStoreName());
							//System.out.println("StoreCode["+order.getStoreCode()+"]OrderNo["+order.getOrderNo()+"]item["+order.getItem()+"]qty["+order.getQty()+"]");
							lineQty += Utils.convertStrToInt(store.getQty());
						}else{
							store = s;
							store.setQty("0");
						}
						store.setStoreStyle(s.getStoreStyle());
						
						storeItemList.add(store);

					}//for
					
					//RemainOmhandQty + lineQty display
					item.setOnhandQty((Utils.convertStrToInt(item.getOnhandQty())+lineQty)+"");	
					
				}//if
				
				//logger.debug("storeItemList Size:"+storeItemList.size());
				item.setStoreItemList(storeItemList);
				orderList.add(item);
				n++;
				
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(rst !=null){
				  rst.close();rst=null;
				}
				if(ps != null){
				  ps.close();ps=null;
				}
			} catch (Exception e) {}
		}
		return orderList;
	}
	
	
	public static Map<String,StoreBean> getStoreBeanOrderMap(Connection conn,Map<String, String>  batchParamMap) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String,StoreBean> map = new HashMap<String,StoreBean>();
		try{
			Date orderDate = Utils.parse(batchParamMap.get(PARAM_TRANS_DATE), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			StringBuffer sql = new StringBuffer("");
			sql.append("\n SELECT " );
			sql.append("\n A.store_code,A.mat , NVL(sum(A.qty),0) as qty");
			sql.append("\n FROM( ");
			sql.append("\n 		SELECT O.store_code" );
			sql.append("\n      ,( select max(M.interface_value) from PENSBME_MST_REFERENCE  M");
			sql.append("\n        WHERE  O.barcode = M.interface_desc     ");
			sql.append("\n        AND M.reference_code = 'LotusItem' )  as mat ");
			sql.append("\n      ,NVL(sum(qty),0) as qty ");
			sql.append("\n 		FROM PENSBME_ORDER O  ");
			sql.append("\n 		where O.store_type = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		and O.order_date = ?  ");
			sql.append("\n 		and O.exported = 'Y'");
			sql.append("\n 		group by  O.store_code ,o.barcode");
			
			sql.append("\n 	    UNION ALL ");
			/** Stock_ISSUE **/
			sql.append("\n 		SELECT H.customer_no as store_code " );
			sql.append("\n      ,( select max(M.interface_value) from PENSBME_MST_REFERENCE  M");
			sql.append("\n        WHERE  I.barcode = M.interface_desc     ");
			sql.append("\n        AND M.reference_code = 'LotusItem' )  as mat ");
			sql.append("\n      ,NVL(sum(I.issue_qty),0) as qty  ");
			sql.append("\n 		from PENSBME_STOCK_ISSUE H ,PENSBME_STOCK_ISSUE_ITEM I  ");
			sql.append("\n 		WHERE 1=1 ");
			sql.append("\n 		and H.ISSUE_REQ_NO = I.ISSUE_REQ_NO ");
			sql.append("\n 		and H.STATUS = '"+PickConstants.STATUS_ISSUED+"'");
			sql.append("\n 		and H.cust_group = '"+batchParamMap.get(PARAM_CUST_GROUP)+"'");
			sql.append("\n 		and H.delivery_date = ?  ");
			sql.append("\n 		and H.exported = 'Y' ");
			sql.append("\n 		group by H.customer_no,I.barcode");
			
			sql.append("\n )A ");
			sql.append("\n group by A.store_code,A.mat ");
		    logger.debug("sql: \n"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			
			ps.setTimestamp(1, new java.sql.Timestamp(orderDate.getTime()));
			ps.setTimestamp(2, new java.sql.Timestamp(orderDate.getTime()));
			
			rs = ps.executeQuery();
			String keyMap = "";
			while(rs.next()){
				StoreBean m = new StoreBean();
				m.setStoreCode(Utils.isNull(rs.getString("store_code")));
				m.setMaterialMaster(Utils.isNull(rs.getString("mat")));
				m.setQty(Utils.isNull(rs.getString("qty")));
				
				keyMap = m.getStoreCode()+"_"+m.getMaterialMaster();
				map.put(keyMap, m);
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return map;
	} 

	private static int  updateOrderExportFlag(Connection conn,String userName,Date orderDate,String storeType) throws Exception {
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int updateCount = 0;
		try{
			sql.append("\n UPDATE PENSBME_ORDER O  ");
			sql.append("\n SET EXPORTED ='G' ,update_user ='"+userName+"' ,update_date = sysdate");
			sql.append("\n where O.store_type = '"+storeType+"'");
			sql.append("\n and O.order_date = ?  ");
			sql.append("\n and O.exported = 'Y' ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			
			updateCount = ps.executeUpdate();
			
			return updateCount;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	private static int  updateStockIssueExportFlag(Connection conn,String userName,Date orderDate,String storeType) throws Exception {
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int updateCount = 0;
		try{
			sql.append("\n UPDATE PENSBME_STOCK_ISSUE O  ");
			sql.append("\n SET EXPORTED ='G' ,update_user ='"+userName+"' ,update_date = sysdate");
			sql.append("\n where O.cust_group = '"+storeType+"'");
			sql.append("\n and O.delivery_date = ?  ");
			sql.append("\n and O.exported = 'Y'	 ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(orderDate.getTime()));
			
			updateCount = ps.executeUpdate();
			
			return updateCount;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	private static void insertMonitorItemResult(Connection conn,BigDecimal monitorItemId,String status,String msg) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO MONITOR_ITEM_RESULT(MONITOR_ITEM_ID, STATUS, MESSAGE)VALUES(?,?,?) ";
			//logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, monitorItemId);
			ps.setString(++index,status);
			ps.setString(++index,msg);
			
			int r = ps.executeUpdate();
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	private static String debug(int no,String s){
		return "";//"[No["+no+"]["+s.length()+"] \n";
	}
	
	
	
}
