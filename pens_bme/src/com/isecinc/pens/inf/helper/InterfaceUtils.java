package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

public class InterfaceUtils {
	
	private static Logger logger = Logger.getLogger("PENS");
	
	public  static String getStatDesc(int status){
		String statusDesc ="";
		if(Constants.STATUS_START==status){
			statusDesc = "Start";
		}else 	if(Constants.STATUS_SUCCESS==status){
			statusDesc = "SUCCESS";
		}else 	if(Constants.STATUS_FAIL==status){
			statusDesc = "FAIL";
		}
		return statusDesc;
	}
	
	//Format File name =  FACT_ID || _MMDD. || SITE_ID     ตัวอย่างผลที่ได้ คือ PEN_1123.500 
	public static String getHisHerTextFileName(String transDate){
		String fileName ="";
		Connection conn = null;
		try{
			   conn = DBConnection.getInstance().getConnection();
			   //dd/mm/yyyy
			   String curDD = transDate.substring(0,2);
			   String curMonth = transDate.substring(3,5);
			  
			   fileName = ""+getConfigInterface(conn,"PENSTOCK","FILENAME_PREFIX")+"_"+curMonth+curDD+"."+getConfigInterface(conn,"PENSTOCK","SITE_ID");
			   
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			  conn.close();
			}catch(Exception e){}
		}
		return fileName;
	}
	
	public static String getHisHerItemMasterTextFileName(String transDate){
		String fileName ="";
		Connection conn = null;
		try{
			   conn = DBConnection.getInstance().getConnection();
			   //dd/mm/yyyy
			   String curDD = transDate.substring(0,2);
			   String curMonth = transDate.substring(3,5);
			  
			   fileName = ""+getConfigInterface(conn,"PENSTOCK","FILENAME_PREFIX")+"_"+curMonth+curDD+"."+getConfigInterface(conn,"PENSTOCK","SITE_ID");
			   
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			  conn.close();
			}catch(Exception e){}
		}
		return fileName;
	}
	
	//HISHER_ORDER_yymmdd.xls
	public static String getHisHerExcelFileName(String transDate){
		String fileName ="";
		Connection conn = null;
		try{
			   conn = DBConnection.getInstance().getConnection();
			   // dd/mm/yyyy
			   String curDD = transDate.substring(0,2);
			   String curMonth = transDate.substring(3,5);
			   String curYear = transDate.substring(8,10);
			   
			   fileName = "HISHER_ORDER_"+curYear+curMonth+curDD+".xls";
			   
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			  conn.close();
			}catch(Exception e){}
		}
		return fileName;
	}
	
	public static String genExportNameICC(String tableName,Map<String, String> batchParamMap){
		String fileName ="";
		Connection conn = null;
		String transDate = "";
		try{
			 conn = DBConnection.getInstance().getConnection();
			//logger.debug("transDate:"+transDate);
			 transDate = batchParamMap.get("TRANS_DATE");
			 
		   //dd/mm/yyyy
		   String curDD = transDate.substring(0,2);
		   String curMonth = transDate.substring(3,5);
		   String siteId = getConfigInterface(conn,"PENSTOCK","SITE_ID");
		   
		  if("PENSBME_ICC_HEAD".equalsIgnoreCase(tableName)){
		     fileName = "tax_"+curMonth+curDD+"."+siteId;
		  }else  if("PENSBME_ICC_DLYR".equalsIgnoreCase(tableName)){
			 fileName = "itm_"+curMonth+curDD+"."+siteId;
		  }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			  conn.close();
			}catch(Exception e){}
		}
		return fileName;
	}
	
	public static String getImportNameICC(String tableName,String transDate){
		String fileName ="";
		try{
			//logger.debug("transDate:"+transDate);
		   //dd/mm/yyyy
		   String curDD = transDate.substring(0,2);
		   String curMonth = transDate.substring(3,5);
		  if("PENSBME_ICC_HEAD".equalsIgnoreCase(tableName)){
		     fileName = "Head"+curMonth+curDD+"";
		  }else  if("PENSBME_ICC_DLYR".equalsIgnoreCase(tableName)){
			 fileName = "DlyR"+curMonth+curDD+"";
		  }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return fileName;
	}
	
	 public static String getConfigInterface(Connection conn,String subject,String fieldName) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String fieldValue ="";
			try {
				sql.delete(0, sql.length());
				sql.append("\n select field_Value FROM PENSBI.PENSBME_CONFIG_INTERFACE WHERE 1=1 ");
				sql.append("\n AND subject ='"+subject+"' \n");
				sql.append("\n AND field_name ='"+fieldName+"' \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					fieldValue = Utils.isNull(rst.getString("field_value"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return fieldValue;
		}
	 
	 public static Map<String,String> getConfigInterfaceAllBySubject(Connection conn,String subject) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Map<String,String> m = new HashedMap();
			try {
				sql.delete(0, sql.length());
				sql.append("\n select field_name,field_Value FROM PENSBI.PENSBME_CONFIG_INTERFACE WHERE 1=1 ");
				sql.append("\n AND subject ='"+subject+"' \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					m.put(Utils.isNull(rst.getString("field_name")), Utils.isNull(rst.getString("field_value")));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return m;
		}
	 
		/**
		 * 
		 * @param value
		 * @param fixLength
		 * @return
		 * @throws Exception
		 * Ex: value:100 ,FixLength :10
		 * Result:[100XXXXXXX]
		 */
		public static String appendNumLeft(String value,String cAppend ,int fixLength) throws Exception{
			int i = 0;
			int loopAppend = 0;
			String cAppendTemp ="";
			if(value.length() < fixLength){
				loopAppend = fixLength - value.length();
			}
			for(i=0;i<loopAppend;i++){
				cAppendTemp +=cAppend;
			}
			value = cAppendTemp+value;
			return value;
		}
		
		public static String appendDecRightByLength(String value,String cAppend ,int fixLength) throws Exception{
			int i = 0;
			int diff = fixLength-value.length();
			for(i=0;i<diff;i++){
				value +=cAppend;
			}
			//logger.debug("Append:length:"+fixLength+",:value["+value+"]");
			return value;
		}
		
		
		
		public static String appendRightByLength(String value,String cAppend ,int fixLength) throws Exception{
			int i = 0;
			int diff = fixLength-value.length();
			for(i=0;i<diff;i++){
				value +=cAppend;
			}
			//logger.debug("Append:length:"+fixLength+",:value["+value+"]");
			return value;
		}
}
