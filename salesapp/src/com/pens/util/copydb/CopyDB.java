package com.pens.util.copydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pens.util.DateUtil;
import com.pens.util.EnvProperties;
import com.pens.util.Utils;



public class CopyDB {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		String whereSQL  ="";
		Connection connSource = null;
		Connection connDest = null;
		String schemaSource = "pens_last";
		String schemaDest = "pens";
		try{
			connSource = getConnectionPensLast();
			connDest = getConnectionPens();
			
			//Parameter
			//String schema ="pensbi";
			//String tableName ="PENSBME_MST_REFERENCE";
			//String where =" where id =1";
			
			//MoveOrder
			//processCopy(connSource,connDest,schemaSource,schemaDest,"t_move_order"," where request_date >= '2020-06-01' ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"t_move_order_line"," where request_number in( select request_number from t_move_order where request_date >= '2020-06-01') ");
			
			//processCopy(connSource,connDest,schemaSource,schemaDest,"t_order"," where order_date >= '2020-06-01'  ");
			//processCopy(connSource,connDest,schemaSource,schemaDest,"t_order_line"," ");
			
			/*Manual
			 * processCopy(connSource,connDest,schemaSource,schemaDest,"m_customer"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"m_address"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"m_contact"," ");*/
			
			/*****************copy pens_last to pens********************/
			processCopy(connSource,connDest,schemaSource,schemaDest,"c_doctype_sequence"," ");
			
			processCopy(connSource,connDest,schemaSource,schemaDest,"m_trip"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_bank_transfer"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_bill_plan"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_bill_plan_line"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_move_order"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_move_order_line"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_order"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_order_line"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_pd_receipt"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_prod_show"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_prod_show_line"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt_by"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt_cn"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt_cn_his"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt_his"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt_line"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt_match"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt_match_cn"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_receipt_pdpaid_no"," ");
			processCopy(connSource,connDest,schemaSource,schemaDest,"t_stamp_print_order"," ");
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
			
			sql.append("SHOW COLUMNS FROM  "+tableName+"\n");
			logger.debug("sql list column table:\n"+sql.toString());
			ps = connSource.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				//field,type,NULL(no,yes),key(pri,mul),default 
				DBBackUpConfig config = new DBBackUpConfig();
				config.setField(Utils.isNull(rs.getString("field")));
				config.setType(Utils.isNull(rs.getString("type")));
				config.setNulls(Utils.isNull(rs.getString("Null")).equalsIgnoreCase("Y")?true:false);
				//config.setDefaults(Utils.isNull(rs.getString("default")));
				configList.add(config);
				columns += config.getField()+",";
				
				//logger.debug("field:"+config.getField()+",Type:"+config.getType());
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
						   values +="STR_TO_DATE('"+DateUtil.stringValue(rsSelect.getDate(config.getField()),DateUtil.DD_MM_YYYY_WITHOUT_SLASH )+"','%d%m%Y'),";
						}else{
						   values += "NULL,";
						}
					}else if(config.getType().toLowerCase().startsWith(("timstamp"))){
						if(rsSelect.getDate(config.getField()) != null){
						   values +="STR_TO_DATE('"+DateUtil.stringValue(rsSelect.getDate(config.getField()),DateUtil.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH )+"','%d%m%Y %H%i%s'),";
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
	public  static Connection getConnectionPens(){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = env.getProperty("connection.url");
			String username = env.getProperty("connection.username");
			String password = env.getProperty("connection.password");
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			_instanceInf = DriverManager.getConnection(url,username,password);	
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	public  static Connection getConnectionPensLast(){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = "jdbc:mysql://localhost:3306/pens_last?useUnicode=true&amp;characterEncoding=UTF-8";
			String username = env.getProperty("connection.username");
			String password = env.getProperty("connection.password");
			
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
