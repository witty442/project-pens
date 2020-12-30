package com.pens.util.copydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Utils;
import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.SQLHelper;



public class CopyDB {
	private static Logger logger = Logger.getLogger("PENS");
	public static void main(String[] args) {
		copyPRODToUAT();
		
		//replaceSingleQuote("Bud's)");
	}
	public static void copyUATToPROD() {
		String whereSQL  ="";
		Connection connSource = null;
		Connection connDest = null;
		String schemaSource = "pensso";
		String schemaDest = "pensso";
		try{
			
			/*****************copy uat to prod  (SO)********************/
			//connSource = getConnectionUAT_APPS();
			//connDest = getConnectionProduction_APPS();
			logger.info("Copy from  UAT TO PRODUCTION");
			
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
	
	public static void copyPRODToUAT() {
		String whereSQL  ="";
		Connection connSource = null;
		Connection connDest = null;
		String schemaSource = "pensso";
		String schemaDest = "pensso";
		try{
			
			/*****************copy  prod to uat (SO)********************/
			connSource = getConnectionProduction_APPS("pensso"); //PROD
			connDest = getConnectionUAT_APPS("pensso"); //UAT
			
			logger.info("Copy from PRODUCTION TO UAT");
			
			//processCopy(connSource,connDest,schemaSource,schemaDest,"C_DOCTYPE"," ");
			// processCopy(connSource,connDest,schemaSource,schemaDest,"C_MESSAGE"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"C_LOCKBOX"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"C_REFERENCE"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_ORG_RULE"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_ORG_RULE_ITEM"," ");
			
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_DISTRICT"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_MAP_PROVINCE"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"MONITOR_ERROR_MAPPING"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"C_CONTROL_CODE"," ");
			
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_PROVINCE"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_TRANSPORT"," ");
		
			//processCopy(connSource,connDest,schemaSource,schemaDest,"AD_USER"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_CUSTOMER"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_CONTACT"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_ADDRESS"," ");
			
			//C4 and item
			//delete all 
			//SQLHelper.excUpdate(connDest,"delete from PENSSO.M_PRODUCT_CATEGORY");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_PRODUCT_CATEGORY"," ");
			
			/*SQLHelper.excUpdate(connDest,"delete from pensso.M_PRODUCT");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_PRODUCT_DIVIDE");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_PRODUCT_PRICE");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_UOM");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_UOM_CONVERSION");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_UOM_CLASS");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_UOM_CLASS_CONVERSION");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_PRICELIST");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_QUALIFIER");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_MODIFIER");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_MODIFIER_LINE");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_MODIFIER_ATTR");
			SQLHelper.excUpdate(connDest,"delete from pensso.M_RELATION_MODIFIER");
			
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_PRODUCT"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_PRODUCT_DIVIDE"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_PRODUCT_PRICE"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_UOM"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_UOM_CONVERSION"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_UOM_CLASS"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_UOM_CLASS_CONVERSION"," ");
			
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_PRICELIST"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_QUALIFIER"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_MODIFIER"," ");*/
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_MODIFIER_LINE"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_MODIFIER_ATTR"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"M_RELATION_MODIFIER"," ");
			
			/** sequence **/
			//SQLHelper.excUpdate(connDest,"delete from pensso.C_DOCTYPE_SEQUENCE");
			//SQLHelper.excUpdate(connDest,"delete from pensso.C_SEQUENCE");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"C_DOCTYPE_SEQUENCE"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"C_SEQUENCE"," ");
			
			/** Transaction **/
			//SQLHelper.excUpdate(connDest,"delete from pensso.T_INVOICE");
			//SQLHelper.excUpdate(connDest,"delete from pensso.T_INVOICE_LINE");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"T_INVOICE"," ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"T_INVOICE_LINE"," ");
			//SQLHelper.excUpdate(connDest,"delete from pensso.T_PICKING_TRANS");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"T_PICKING_TRANS"," ");
			/*SQLHelper.excUpdate(connDest,"delete from pensso.T_ORDER");
			processCopy(connSource,connDest,schemaSource,schemaDest,"T_ORDER"," ");
			SQLHelper.excUpdate(connDest,"delete from pensso.T_ORDER_LINE");
			processCopy(connSource,connDest,schemaSource,schemaDest,"T_ORDER_LINE"," ");*/
			
			/*SQLHelper.excUpdate(connDest,"delete from pensso.T_EDI");
			processCopy(connSource,connDest,schemaSource,schemaDest,"T_EDI"," ");
			SQLHelper.excUpdate(connDest,"delete from pensso.T_EDI_LINE");
			processCopy(connSource,connDest,schemaSource,schemaDest,"T_EDI_LINE"," ");*/
			
			//processCopy(connSource,connDest,schemaSource,schemaDest,"T_PICKING_TRANS"," where transaction_date <= to_date('28082020','ddmmyyyy')");
			
			/*schemaSource = "pensbi";
			schemaDest = "pensbi";
			connSource = getConnectionProduction_APPS("pensbi"); //PROD
			connDest = getConnectionUAT_APPS("pensbi"); //UAT
			
			SQLHelper.excUpdate(connDest,"delete from PENSBI.XXPENS_BI_MST_SUBBRAND");
			processCopy(connSource,connDest,schemaSource,schemaDest,"XXPENS_BI_MST_SUBBRAND"," ");*/
			
			
			processCopy(connSource,connDest,schemaSource,schemaDest,"M_CUSTOMER_NIS"," ");
			
			
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
			logger.debug("**start Process Copy DB**");
			
			// gen row to insert sql data
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
			logger.debug("connSource info:"+connSource.toString());
			
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
			if(configList.size()==0){
				logger.info("Not found config Table:"+tableName);
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
						values +="'"+replaceSingleQuote(Utils.isNull(rsSelect.getString(config.getField())))+"',";
					}else if(config.getType().toLowerCase().startsWith(("varchar"))){
						values +="'"+replaceSingleQuote(Utils.isNull(rsSelect.getString(config.getField())))+"',";
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
					//logger.debug("sqlInsert:"+sqlInsert);
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
			logger.debug("Error:sqlInsert:"+sqlInsert);
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
    

    public static int excUpdateOneSql(Connection conn,String sqlOne) throws Exception{
	    PreparedStatement ps =null;
	    int recordUpdate = 0;
		try{  
		    ps = conn.prepareStatement(sqlOne);
			recordUpdate = ps.executeUpdate();
		}catch(Exception e){
	      throw e;
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
			}catch(Exception e){
				throw e;
			}
		}
		return recordUpdate;
  }
    /**
	 * 
	 * @param str
	 * @return 1: 'xxx'  -> \'xxx\'
	 *         2:  (Bud's) -> \Bud'\'
	 */
	private static String replaceSingleQuote(String str){
		try{
			//logger.debug("before:"+str);
			//str = str.replaceAll("'", "\\\\'");
			str = str.replaceAll("'", "''");
			//logger.debug("after:"+str);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return str;
	}
	public  static Connection getConnectionUAT_APPS(String user){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = "jdbc:oracle:thin:@//192.168.38.186:1529/TEST";
			String username = user;
			String password = user;
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			_instanceInf = DriverManager.getConnection(url,username,password);	
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	public  static Connection getConnectionProduction_APPS(String user){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = "jdbc:oracle:thin:@//192.168.37.185:1521/PENS";
			String username = user;
			String password = user;
			
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
