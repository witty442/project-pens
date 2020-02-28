package com.pens.util.importexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
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
import com.pens.util.seq.SequenceProcessAll;

public class ImportAllExcel {
	public static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		try{
			 String tableName ="pensbi.pensbme_mst_reference";
			 String[] columnArr = {"interface_value|STRING|N","interface_desc|STRING|N","pens_value|STRING|N","ID|NUMBER|SEQ","REFERENCE_CODE|STRING|$replenishment_priority"};
			 
			importExcel("ORACLE","UAT","D:\\Work_ISEC\\Project-BME\\DEV\\DataImport\\REP_ORDER\\rep_priority.xlsx",tableName,columnArr);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//Form page input
public static void importExcel(String dataBaseType,String db,InputStream is,String inputFile,String tableName,String[] columnArr,String userName) throws Exception{
	 Connection conn = null;
	 try{
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
		conn.setAutoCommit(false);
		importExcelModel(conn,is, inputFile, tableName,columnArr,userName);
		conn.commit();
	 }catch(Exception e){
		 conn.rollback();
	 }finally{
		 if(conn != null){
				conn.close();
			}
	 }
}
//Form page input
public static void importExcel(Connection conn,InputStream is,String inputFile,String tableName,String[] columnArr,String userName) throws Exception{
	 try{
		importExcelModel(conn,is, inputFile,tableName,columnArr,userName);
	 }catch(Exception e){
		throw e;
	 }finally{
		
	 }
}
//form manual code
public static void importExcel(String dataBaseType,String db,String inputFile,String tableName,String[] columnArr) throws Exception{
	 Connection conn = null;
	 try{
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
		conn.setAutoCommit(false);
		importExcelModel(conn,null, inputFile,tableName,columnArr,"");
		conn.commit();
	 }catch(Exception e){
		 conn.rollback();
	 }finally{
		 if(conn != null){
				conn.close();
			}
	 }
}
/**
 * initColumnList and sql insert from StringArr Column [store_code|STRIN,groupCode|STRING]
 * @param tableName
 * @param columnArr
 * @param addCreateDate
 * @return
 * @throws Exception
 * Note ,column External Excel after Excel alway 
 */
public static List<Object> initColumnList(String tableName,String[] columnArr) throws Exception{
	StringBuffer sql = new StringBuffer();
	List<Object> columnListObj = new ArrayList<Object>();
	int maxColumnNo = columnArr.length;//get MaxColumn wait for next step
	List<ColumnBean> columnList = new ArrayList<ColumnBean>();
	try{
		for (int colNo = 0; colNo < maxColumnNo; colNo++) {
			String columnStr = columnArr[colNo];
			String[] columnArrTemp = columnStr.split("\\|");
			ColumnBean columnBean = new ColumnBean();
			columnBean.setColumnName(columnArrTemp[0]);
			columnBean.setColumnType(columnArrTemp[1]);
			columnBean.setColumnFunction(columnArrTemp[2]);
			columnList.add(columnBean);
		}
		
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
		
		columnListObj.add(columnList);
		columnListObj.add(sql);
		
		return columnListObj;
	}catch(Exception e){
		throw e;
	}
}

public static void importExcelModel(Connection conn,InputStream is,String inputFile,String tableName,String[] columnArr,String userName) throws Exception{

	 StringBuffer sql = new StringBuffer();
	 UploadXLSUtil xslUtils = new UploadXLSUtil();
	 PreparedStatement ps = null;
	 Workbook wb1 = null;
	 XSSFWorkbook wb2 = null;
	 Sheet sheet = null;
	 String fileType = "";
	 Row row = null;
	 Cell cell = null;
	 int maxColumnNo = columnArr.length;
	 FileInputStream fis = null;
	 int colAdd = 0;
	 try{
		 logger.debug("is:"+is);
		 logger.debug("inputFile:"+inputFile);
		 
		//get fielType
		fileType = inputFile.substring(inputFile.indexOf(".")+1,inputFile.length());
		if(is ==null){
			//Get from local file
			fis = new FileInputStream(new File(inputFile));
			if("xls".equalsIgnoreCase(fileType)){
			   wb1 = new HSSFWorkbook(fis);//97-2003
			   sheet = wb1.getSheetAt(0);
			   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
			}else{
			   OPCPackage pkg = OPCPackage.open(fis);
			   wb2 = new XSSFWorkbook(pkg);
			   sheet = wb2.getSheetAt(0);
			   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
			}
		}else{
			// Get InputStream from Screen
			if("xls".equalsIgnoreCase(fileType)){
			   wb1 = new HSSFWorkbook(is);//97-2003
			   sheet = wb1.getSheetAt(0);
			   logger.debug("number of sheet: " + wb1.getNumberOfSheets());
			}else{
			   OPCPackage pkg = OPCPackage.open(is);
			   wb2 = new XSSFWorkbook(pkg);
			   sheet = wb2.getSheetAt(0);
			   logger.debug("number of sheet: " + wb2.getNumberOfSheets());
			}
		}
		
		//init column and sql insert
		List<Object> columnListObj = initColumnList(tableName, columnArr);
		List<ColumnBean> columnList = (List<ColumnBean>)columnListObj.get(0);
		sql = (StringBuffer)columnListObj.get(1);
		
		ps = conn.prepareStatement(sql.toString());
		
		//Loop data row
		int rowNo = 1;//rowValue Start
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
			colAdd = 0;
			//Insert data In Excel
			logger.debug("MaxColumn:"+maxColumnNo);
			for (int colNo = 0; colNo < maxColumnNo; colNo++) {
				ColumnBean columnBean = columnList.get(colNo);
				if( columnBean.getColumnFunction().equalsIgnoreCase("N")){
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
					colAdd++;
				}//if
			}//for
			
			//Insert data External Excel
			colAdd++;
			logger.debug("colAdd:"+colAdd);
			for (int colNo = 0; colNo < columnList.size(); colNo++) {
				ColumnBean columnBean = columnList.get(colNo);
				if( !columnBean.getColumnFunction().equalsIgnoreCase("N")){
					
					//ColumnFunction stratWitch $ is defaultValue 
					if(columnBean.getColumnFunction().startsWith("$")){
						if(columnBean.getColumnType().equalsIgnoreCase("STRING")){
							 ps.setString(colAdd,  columnBean.getColumnFunction().replaceAll("\\$", ""));
							 colAdd++;
						}else  if(columnBean.getColumnType().equalsIgnoreCase("NUMBER")){
							 ps.setDouble(colAdd,  Utils.convertStrToDouble(columnBean.getColumnFunction().replaceAll("\\$", "")));
							 colAdd++;
						}
					}else{
						//External Function
					    if(columnBean.getColumnFunction().equalsIgnoreCase("SYSDATE")){
						   ps.setTimestamp(colAdd, new java.sql.Timestamp(new Date().getTime()));	
						   colAdd++;
						}else  if(columnBean.getColumnFunction().equalsIgnoreCase("CURRENT_USER")){
						   ps.setString(colAdd,  userName);
						   colAdd++;
						}else  if(columnBean.getColumnFunction().equalsIgnoreCase("SEQ")){
						   ps.setBigDecimal(colAdd, getSeq(conn, tableName));	
						   colAdd++;
						}
					}
				}//if
			}//for
			
			ps.executeUpdate();
		}//for
	}catch(Exception e){
		e.printStackTrace();
		throw e;
	}finally{
		if(ps !=null){
			ps.close();
		}
		
		if(fis != null)fis.close();
		if(is != null)is.close();
		
	}
 }

   public static BigDecimal getSeq(Connection conn,String tableName) throws Exception{
	   BigDecimal seq = new BigDecimal("0");
	   try{
		   seq = SequenceProcessAll.getIns().getNextValue(conn, tableName.toLowerCase());
	   }catch(Exception e){
		   throw e;
	   }
	   return seq;
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
