package com.pens.util.importexcel;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pens.util.UploadXLSUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHelper;

public class ImportManualExcel {
	public static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		try{
			
			//importExcel("ORACLE","UAT","d://dev_temp//temp//ImportManualExcel/ข้อมูลตั้งต้น ของแต่ละห้าง.xlsx",7);
			//importExcel("ORACLE","PRODUCTION","d://dev_temp//temp//ImportManualExcel/ข้อมูลตั้งต้น ของแต่ละห้าง.xlsx",7);
			
			//importExcel("MYSQL","UAT","d://dev_temp//temp//ImportManualExcel/data_stock_return.xls",5);
		
			//importExcel("ORACLE","PRODUCTION","d://dev_temp//temp//ImportManualExcel/SALES_ZONE.xlsx",3);
			//importExcel("ORACLE","UAT","d://dev_temp//temp//ImportManualExcel/SALES_ZONE.xlsx",3);
			
			
			//importExcel("ORACLE","UAT","d://dev_temp//temp//ImportManualExcel/XXPENS_BI_MST_CUST_CAT_MAP_TT.xlsx",6);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
public static void importExcel(String dataBaseType,String db,String inputFile,int maxColumn) throws Exception{
	 Connection conn = null;
	 StringBuffer sql = new StringBuffer();
	 UploadXLSUtil xslUtils = new UploadXLSUtil();
	 PreparedStatement ps = null;
	 Workbook wb1 = null;
	 XSSFWorkbook wb2 = null;
	 Sheet sheet = null;
	 String fileType = "";
	 Row row = null;
	 Cell cell = null;
	 int maxColumnNo = maxColumn;//get MaxColumn wait for next step
	 try{
		 logger.debug("inputFile:"+inputFile);
		 
		//get fielType
		fileType = inputFile.substring(inputFile.indexOf(".")+1,inputFile.length());
		 
		FileInputStream dataFile = new FileInputStream(new File(inputFile));
		
		if("xls".equalsIgnoreCase(fileType)){
		   wb1 = new HSSFWorkbook(dataFile);//97-2003
		   sheet = wb1.getSheetAt(0);
		   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
		}else{
		   OPCPackage pkg = OPCPackage.open(dataFile);
		   wb2 = new XSSFWorkbook(pkg);
		   sheet = wb2.getSheetAt(0);
		   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
		}
		
		//init table_name row =0
		row = sheet.getRow(0);
		Cell cellTableName = row.getCell((short) 0);
		String tableName = Utils.isNull(xslUtils.getCellValue(0, cellTableName));
		logger.debug("table_name");
		
		//init column list row =1
		List<ColumnBean> columnList = new ArrayList<ColumnBean>();
		row = sheet.getRow(1);
		for (int colNo = 0; colNo < maxColumnNo; colNo++) {
			cell = row.getCell((short) colNo);
			logger.debug("row["+1+"]col[("+colNo+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
			
			String columnStr =Utils.isNull(xslUtils.getCellValue(colNo, cell));
			String[] columnArr = columnStr.split("\\|");
			ColumnBean columnBean = new ColumnBean();
			columnBean.setColumnName(columnArr[0]);
			columnBean.setColumnType(columnArr[1]);
			columnList.add(columnBean);
		}
		//row 3 description column
		
		//get insert statement 
		if(columnList != null && columnList.size()>0){
			sql.append(" INSERT INTO "+tableName+"( \n");
			for(int i=0;i<columnList.size();i++){
				ColumnBean c = columnList.get(i);
				if(i == (columnList.size()-1)){
					sql.append(c.getColumnName());
				}else{
					sql.append(c.getColumnName()+",");
				}
			}
			sql.append(") VALUES ( \n");
			for(int i=0;i<columnList.size();i++){
				if(i == (columnList.size()-1)){
					sql.append("?");
				}else{
					sql.append("?,");
				}
			}
			sql.append(") \n");
		}
		logger.debug("sql:"+sql.toString());
		if("ORACLE".equalsIgnoreCase(dataBaseType)){
			if("PRODUCTION".equalsIgnoreCase(db)){
			   logger.debug("IMPORT TO DB "+db);
			   conn = getConnectionProdApps();
			}else{
			   conn = getConnectionUATApps();
			   logger.debug("IMPORT TO DB "+db);
			}
		}else{
			  conn = getConnectionMysqlLocal();
		}
		ps = conn.prepareStatement(sql.toString());
		
		//Loop data row
		int rowNo = 3;
		for (int i = rowNo; i < sheet.getLastRowNum()+1; i++) {
			row = sheet.getRow(i);
			
			/** Check Row is null **/
			Cell cellCheck = row.getCell((short) 1);
			Object cellCheckValue = xslUtils.getCellValue(1, cellCheck);
			String rowCheck =Utils.isNull(cellCheckValue);
			logger.debug("row["+i+"]rowCheck["+rowCheck+"]");
			if("".equals(rowCheck)){
				break;
			}
			
			for (int colNo = 0; colNo < maxColumnNo; colNo++) {
				ColumnBean columnBean = columnList.get(colNo);
				cell = row.getCell((short) colNo);
				logger.debug("row["+i+"]col[("+colNo+"]type["+columnBean.getColumnType()+"]value["+xslUtils.getCellValue(colNo, cell)+"]");
				
				Object cellValue = xslUtils.getCellValue(colNo, cell);
				
				if(columnBean.getColumnType().equalsIgnoreCase("STRING")){
				   String str = ExcelHelper.isCellNumberOrText((cellValue));
				   ps.setString((colNo+1),  str.trim());
				}else  if(columnBean.getColumnType().equalsIgnoreCase("NUMBER")){
				   double doubleData = Utils.isDoubleNull(cellValue);
				   ps.setDouble((colNo+1),  doubleData);
				}
			}//for
			ps.executeUpdate();
		}
	}catch(Exception e){
		throw e;
	}finally{
		if(ps !=null){
			ps.close();
		}
		if(conn != null){
			conn.close();
		}
	}
 }
	public  static Connection getConnectionProdApps(){		
		Connection _instanceInf =null;
		try {	
			//String driver = env.getProperty("db.driver_class");
			String url = "jdbc:oracle:thin:@//192.168.37.185:1521/PENS";
			String username = "apps";
			String password = "apps";
			
			logger.debug("GetConnection Source DB(Prod):"+url+","+username+","+password);
			
			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			//props.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "2000");

			_instanceInf = DriverManager.getConnection(url, props);
			
			//logger.debug("Connection:"+_instanceInf);
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	public static Connection getConnectionUATApps(){		
		Connection _instanceInf =null;
		try {	
			//String driver = env.getProperty("db.driver_class");
			String url = "jdbc:oracle:thin:@//192.168.38.186:1529/TEST";
			String username = "apps";
			String password = "apps";
			
			logger.debug("GetConnection Dest DB(UAT) :"+url+","+username+","+password);
			
			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			//props.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "2000");

			_instanceInf = DriverManager.getConnection(url, props);
			
			//logger.debug("Connection:"+_instanceInf);
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	
	public  static Connection getConnectionMysqlLocal(){		
		Connection _instanceInf =null;
		try {	
			//String driver = env.getProperty("db.driver_class");
			String url = "jdbc:mysql://localhost:3306/pens?useUnicode=true&amp;characterEncoding=UTF-8";
			String username = "root";
			String password = "pens";
			
			logger.debug("GetConnection Source DB(Prod):"+url+","+username+","+password);
			
			 Class.forName("com.mysql.jdbc.Driver");
			 
		//	_instanceInf = DriverManager.getConnection(url, props);
			_instanceInf = DriverManager.getConnection(url,username,password);	
			//logger.debug("Connection:"+_instanceInf);
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
}
