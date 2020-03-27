package com.pens.util.copydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import oracle.jdbc.driver.OracleConnection;

import org.apache.log4j.Logger;

import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class CopyDB {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		String whereSQL  ="";
		try{
			//Parameter
			//String schema ="pensbi";
			//String tableName ="PENSBME_MST_REFERENCE";
			//String where =" where id =1";

			/*****************copy  product to test********************/
			//processProductionToUAT("pensbi","PENSBME_MST_REFERENCE"," ");
			//processProductionToUAT("pensbi","PENSBME_MST_REFERENCE"," where Reference_code ='Store' ");
			
			//processProductionToUAT("pensbi","PENSBME_STYLE_MAPPING"," ");
			
		   // processProductionToUAT("pensbi","PENSBME_ONHAND_BME_LOCKED","");
		   // processProductionToUAT("pensbi","PENSBME_STYLE_MAPPING","");
			//processProductionToUAT("pensbi","PENSBME_ONHAND_BME","");
			//processProductionToUAT("pensbi","PENSBME_ORDER"," WHERE order_date =to_date('20/08/2018','dd/mm/yyyy')");
			
			//processProductionToUAT("pensbi","pensbme_scan_checkout"," where issue_req_no ='T61040029' ");
			//processProductionToUAT("pensbi","pensbme_scan_checkout_item"," where issue_req_no ='T61040029' ");
			//processProductionToUAT("pensbi","pensbme_stock_issue"," where issue_req_no ='T61040029' ");
			//processProductionToUAT("pensbi","pensbme_stock_issue_item"," where issue_req_no ='T61040029' ");
			
			//processProductionToUAT("pensbi","C_CONSTANTS ","");
			//processProductionToUAT("pensbi","C_USER_INFO","");
			//processProductionToUAT("pensbi","XXPENS_BI_MST_SALES_ZONE","");
			
			//SalesTarget
			//processProductionToUAT("pensbi","XXPENS_BI_SALES_TARGET_TT","where period ='AUG-19'");
			
			//whereSQL = "where id in("+
					         //"select id from XXPENS_BI_SALES_TARGET_TT where period ='AUG-19')";
			//processProductionToUAT("pensbi","XXPENS_BI_SALES_TARGET_TT_L",whereSQL);
			
			//processProductionToUAT("pensbi","XXPENS_BI_SALES_TARGET_TEMP","where period ='JUL-19'");
			//whereSQL = "where id in("+
	         //"select id from XXPENS_BI_SALES_TARGET_TEMP where period ='JUL-19')";
            // processProductionToUAT("pensbi","XXPENS_BI_SALES_TARGET_TEMP_L",whereSQL);
			
			//processProductionToUAT("pensbi","PENSBME_PRICELIST","");
			
			/*****************copy Test to product********************/
			//copy Test to product
			//processUATToProduction("pensbi","PENSBME_ONHAND_BME_LOCKED_FRI","");
			//processUATToProductionUserApps("apps","XXPENS_OM_TRIP_TONGFA_BAK","");
			
			//processUATToProduction("PENSBI","PENSBME_PICK_JOB","where job_id in ( 11897 ,11866, 11865,11864,11863,11862,11861,11860 )");
			//processUATToProduction("PENSBI","PENSBME_PICK_BARCODE","where job_id in ( 11897 ,11866, 11865,11864,11863,11862,11861,11860 )");
			//processUATToProduction("PENSBI","PENSBME_PICK_BARCODE_ITEM","where job_id in ( 11897 ,11866, 11865,11864,11863,11862,11861,11860 )");
		
			//processUATToProduction("PENSBI","XXPENS_BI_SALES_TARGET_TEMP","where id in (539,540,541,546,589,591,593 )");
			//processUATToProduction("PENSBI","XXPENS_BI_SALES_TARGET_TEMP_L","where id in (539,540,541,546,589,591,593 )");
			
			//processUATToProduction("PENSBI","PENSBME_TT_BRANCH","where oracle_cust_no ='25105003'");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
    public static void processProductionToUAT(String schema,String tableName,String where) throws Exception{
    	Connection connSource = null;
    	Connection connDest = null;
		try{
			logger.debug("**start processProductionToUAT**");
			connSource = getConnectionProd();
			connDest = getConnectionUAT();
			
			// gen row to insertsql data
			int countIns = generateInsertScript(connSource,connDest, schema, tableName,where);
			
			logger.debug("Total Record Insert :"+countIns);
			logger.debug("**End processProductionToUAT**");
		}catch(Exception e){
			e.printStackTrace(); 
		}finally{
			if(connSource != null){
				connSource.close();
			}
			if(connDest != null){
				connDest.close();
			}
		}
	}
    public static void processUATToProduction(String schema,String tableName,String where) throws Exception{
    	Connection connSource = null;
    	Connection connDest = null;
		try{
			logger.debug("**start processUATToProduction **");
			connSource = getConnectionUAT();
			connDest = getConnectionProd();
			
			// gen row to insertsql data
			int countIns = generateInsertScript(connSource,connDest, schema, tableName,where);
			
			logger.debug("Total Record Insert :"+countIns);
			logger.debug("**end processUATToProduction**");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(connSource != null){
				connSource.close();
			}
			if(connDest != null){
				connDest.close();
			}
		}
	}
    public static void processUATToProductionUserApps(String schema,String tableName,String where) throws Exception{
    	Connection connSource = null;
    	Connection connDest = null;
		try{
			logger.debug("**start processUATToProduction APPS **");
			connSource = getConnectionUATApps();
			connDest = getConnectionProdApps();
			
			// gen row to insertsql data
			int countIns = generateInsertScript(connSource,connDest, schema, tableName,where);
			
			logger.debug("Total Record Insert :"+countIns);
			logger.debug("**end processUATToProduction**");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(connSource != null){
				connSource.close();
			}
			if(connDest != null){
				connDest.close();
			}
		}
	}
    private static int generateInsertScript(Connection connSource,Connection connDest
    		,String schema,String tableName,String where)
    throws Exception{
		//logger.debug("generateInsertScript:"+tableName);
		PreparedStatement ps =null;
		ResultSet rs = null;
		PreparedStatement psSelect =null;
		ResultSet rsSelect = null;
		int countIns = 0;
		StringBuffer sql = new StringBuffer("");
	    String columns = "";
	    String values ="";
	    String sqlInsert = "";
	    List<DBBackUpConfig> configList = new ArrayList<DBBackUpConfig>();
	    int i = 0;
		try{
			sql.append("select * from user_tab_columns where table_name ='"+tableName+"' order by column_id \n");
			logger.debug("sql list column table:\n"+sql.toString());
			ps = connSource.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				//field,type,NULL(no,yes),key(pri,mul),default 
				DBBackUpConfig config = new DBBackUpConfig();
				config.setField(Utils.isNull(rs.getString("column_Name")));
				config.setType(Utils.isNull(rs.getString("data_type")));
				config.setNulls(Utils.isNull(rs.getString("Nullable")).equalsIgnoreCase("Y")?true:false);
				//config.setDefaults(Utils.isNull(rs.getString("default")));
				configList.add(config);
				columns += config.getField()+",";
				
				//logger.debug("field:"+config.getField()+",Type:"+config.getType());
			}
			
			/** Replace comma last **/
			if(columns != null && columns.length() >0){
				columns =columns.substring(0,columns.length()-1);
			}
			
			logger.debug("sql select data:"+" select * from  "+schema+"."+tableName+" "+where);
			sql = new StringBuffer(" select * from  "+schema+"."+tableName+" "+where);
			psSelect = connSource.prepareStatement(sql.toString());
			rsSelect = psSelect.executeQuery();
			
			logger.debug("configList size:"+configList.size());
			while(rsSelect.next()){
		       // logger.debug("found data");
				for(i = 0;i<configList.size();i++){
					DBBackUpConfig config = (DBBackUpConfig)configList.get(i);
					
					if(config.getType().toLowerCase().startsWith(("char"))){
						values +="'"+Utils.isNull(rsSelect.getString(config.getField()))+"',";
					}else if(config.getType().toLowerCase().startsWith(("varchar2"))){
						if(config.getField().equalsIgnoreCase("error_msg")){
							//\'3003\';
						   values +="'"+replaceSingleQuote(Utils.isNull(rsSelect.getString(config.getField())))+"',";
						}else{
						   values +="'"+replaceSingleQuote(Utils.isNull(rsSelect.getString(config.getField())))+"',";
						}
					}else if(config.getType().toLowerCase().startsWith(("date"))){
						if(rsSelect.getDate(config.getField()) != null){
						   values +="TO_DATE('"+DateUtil.stringValue(rsSelect.getDate(config.getField()),DateUtil.DD_MM_YYYY_WITHOUT_SLASH )+"','ddmmyyyy'),";
						}else{
						   values += "NULL,";
						}
					}else if(config.getType().toLowerCase().startsWith(("timstamp"))){
						if(rsSelect.getDate(config.getField()) != null){
						   values +="TO_DATE('"+DateUtil.stringValue(rsSelect.getDate(config.getField()),DateUtil.DD_MM_YYYY_WITHOUT_SLASH )+"','ddmmyyyy'),";
						}else{
						   values += "NULL,";
						}
					}else if(config.getType().toLowerCase().startsWith(("number"))){
						values +=rsSelect.getDouble(config.getField())+",";
				    }else{
				    	values +="'"+Utils.isNull(rsSelect.getString(config.getField()))+"',";
				    }
				}//for
				
				if(values != null && values.length() >0){
					values = values.substring(0,values.length()-1);
					sqlInsert = "insert into "+schema+"."+tableName+"("+columns+") values ("+values+")";
					//logger.debug("sqlInsert:"+sqlInsert);
					//insert to DB Dest
					int update =SQLHelper.excUpdateOneSql(connDest, sqlInsert);
					
					countIns +=update;
					logger.debug("Row:"+countIns);
				}

                //initail
				values ="";
			}//while
		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			if(ps != null){
				ps.close();
			}
			if(rsSelect != null){
				rsSelect.close();
			}
			if(psSelect != null){
				psSelect.close();
			}
		}
		return countIns;
	}
    
    /**
	 * 
	 * @param str
	 * @return  'xxx'  -> \'xxx\'
	 */
	private static String replaceSingleQuote(String str){
		try{
			str = str.replaceAll("'", "\\\\'");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return str;
	}
	public  static Connection getConnectionProd(){		
		Connection _instanceInf =null;
		try {	
			//String driver = env.getProperty("db.driver_class");
			String url = "jdbc:oracle:thin:@//192.168.37.185:1521/PENS";
			String username = "pensbi";
			String password = "pensbi";
			
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
	
	public static Connection getConnectionUAT(){		
		Connection _instanceInf =null;
		try {	
			//String driver = env.getProperty("db.driver_class");
			String url = "jdbc:oracle:thin:@//192.168.38.186:1529/TEST";
			String username = "pensbi";
			String password = "pensbi";
			
			logger.debug("GetConnection  DB(UAT) :"+url+","+username+","+password);
			
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
			
			logger.debug("GetConnection  DB(UAT) :"+url+","+username+","+password);
			
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
}
