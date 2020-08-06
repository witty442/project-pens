package com.pens.util.copydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Utils;
import com.pens.util.EnvProperties;



public class CopyDB {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		String whereSQL  ="";
		Connection connSource = null;
		Connection connDest = null;
		String schemaSource = "pensso";
		String schemaDest = "pensso";
		try{
			connSource = getConnectionUAT_APPS();
			connDest = getConnectionProduction_APPS();
			
			
			/*****************copy uat to prod  (SO)********************/
			//processCopy(connSource,connDest,schemaSource,schemaDest,"C_DOCTYPE"," ");
			/* processCopy(connSource,connDest,schemaSource,schemaDest,"C_MESSAGE"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"C_LOCKBOX"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"C_REFERENCE"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_ORG_RULE"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_ORG_RULE_ITEM"," ");
			
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_DISTRICT"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_MAP_PROVINCE"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"MONITOR_ERROR_MAPPING"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"C_CONTROL_CODE"," ");*/
			
			//processCopy(connSource,connDest,schemaSource,schemaDest,"m_province"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"m_transport"," ");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(connSource != null){
					connSource.close();
				}
				if(connDest != null){
					connDest.close();
				}
			}catch(Exception e){
				
			}
		}
	}

    public static void processCopy(Connection connSource,Connection connDest,String schemaSource,String schemaDest,String tableName,String where) throws Exception{
		try{
			logger.debug("**start processProductionToUAT**");
			
			// gen row to insertsql data
			int countIns = generateInsertScript(connSource,connDest,schemaSource, schemaDest,tableName,where);
			
			logger.debug("Total Record Insert :"+countIns);
			logger.debug("**End processProductionToUAT**");
		}catch(Exception e){
			e.printStackTrace(); 
		}finally{
			
		}
	}
    
    private static int generateInsertScript(Connection connSource,Connection connDest
    		,String schemaSource,String schemaDest,String tableName,String where)
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
				
				
				logger.debug("field:"+config.getField()+",Type:"+config.getType());
			}
			
			/** Replace comma last **/
			if(columns != null && columns.length() >0){
				columns =columns.substring(0,columns.length()-1);
			}
			
			logger.debug("sql select data:"+" select * from  "+schemaSource+"."+tableName+" "+where);
			sql = new StringBuffer(" select * from  "+schemaSource+"."+tableName+" "+where);
			psSelect = connSource.prepareStatement(sql.toString());
			rsSelect = psSelect.executeQuery();
			
			logger.debug("configList size:"+configList.size());
			while(rsSelect.next()){
		       // logger.debug("found data");
				for(i = 0;i<configList.size();i++){
					DBBackUpConfig config = (DBBackUpConfig)configList.get(i);
					
					if(config.getType().toLowerCase().startsWith(("char"))){
						values +="'"+Utils.isNull(rsSelect.getString(config.getField()))+"',";
					}else if(config.getType().toLowerCase().startsWith(("varchar"))){
						values +="'"+replaceSingleQuote(Utils.isNull(rsSelect.getString(config.getField())))+"',";
					}else if(config.getType().toLowerCase().startsWith(("date"))){
						if(rsSelect.getDate(config.getField()) != null){
						   values +="STR_TO_DATE('"+Utils.stringValue(rsSelect.getDate(config.getField()),Utils.DD_MM_YYYY_WITHOUT_SLASH )+"','%d%m%Y'),";
						}else{
						   values += "NULL,";
						}
					}else if(config.getType().toLowerCase().startsWith(("timstamp"))){
						if(rsSelect.getDate(config.getField()) != null){
						   values +="STR_TO_DATE('"+Utils.stringValue(rsSelect.getDate(config.getField()),Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH )+"','%d%m%Y %H%i%s'),";
						}else{
						   values += "NULL,";
						}
					}else if(config.getType().toLowerCase().startsWith(("int"))){
						values +=rsSelect.getInt(config.getField())+",";
					}else if(config.getType().toLowerCase().startsWith(("decimal"))){
						values +=rsSelect.getDouble(config.getField())+",";
				    }else{
				    	values +="'"+Utils.isNull(rsSelect.getString(config.getField()))+"',";
				    }
				}//for
				
				if(values != null && values.length() >0){
					values = values.substring(0,values.length()-1);
					sqlInsert = "insert into "+schemaDest+"."+tableName+"("+columns+") values ("+values+")";
					logger.debug("sqlInsert:"+sqlInsert);
					//insert to DB Dest
					int update =excUpdateOneSql(connDest, sqlInsert);
					
					countIns +=update;
					logger.debug("Result Row:"+countIns);
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
    
    public static int excUpdateOneSql(Connection conn,String sqlOne) {
	    PreparedStatement ps =null;
	    int recordUpdate = 0;
		try{  
		    ps = conn.prepareStatement(sqlOne);
			recordUpdate = ps.executeUpdate();
		}catch(Exception e){
	      logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return recordUpdate;
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
	public  static Connection getConnectionUAT_APPS(){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = "jdbc:oracle:thin:@//192.168.38.186:1529/TEST";
			String username = "PENSSO";
			String password = "PENSSO";
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			_instanceInf = DriverManager.getConnection(url,username,password);	
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	public  static Connection getConnectionProduction_APPS(){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = "jdbc:oracle:thin:@//192.168.37.185:1521/PENS";
			String username = "pensso";
			String password = "pensso";
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			_instanceInf = DriverManager.getConnection(url,username,password);	
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	
	
}
