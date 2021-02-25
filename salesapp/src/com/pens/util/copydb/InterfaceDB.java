package com.pens.util.copydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/*import oracle.jdbc.driver.OracleConnection;*/

import org.apache.log4j.Logger;

import com.pens.util.DateUtil;
import com.pens.util.Utils;



public class InterfaceDB {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] args) {
		String whereSQL  ="";
		try{
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
    public static void process(String sourceType,Connection connSource,String destType,Connection connDest,String schema, String tableName,String whereSQL) throws Exception{
		try{
			logger.debug("**start process sourceType["+sourceType+"]to destType["+destType+"]**");
		
			// gen row to insertsql data
			int countIns = generateInsertScript(sourceType,connSource,sourceType,connDest, schema, tableName,whereSQL);
			
			logger.debug("Total Record Insert :"+countIns);
			logger.debug("**End processProductionToUAT**");
		}catch(Exception e){
			e.printStackTrace(); 
		}finally{
		}
	}

    
    private static int generateInsertScript(String sourceType,Connection connSource,String destType,Connection connDest
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
			if("ORACLE".equalsIgnoreCase(sourceType)){
				 sql.append("select * from user_tab_columns where table_name ='"+tableName+"' order by column_id \n");
			}else if("ORACLE".equalsIgnoreCase(sourceType)){
				 sql.append("SHOW COLUMNS FROM  t_stock_discount\n");
			}
			logger.debug("sql list column table:\n"+sql.toString());
			ps = connSource.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				//field,type,NULL(no,yes),key(pri,mul),default 
				DBBackUpConfig config = new DBBackUpConfig();
				if("ORACLE".equalsIgnoreCase(sourceType)){
				  config.setField(Utils.isNull(rs.getString("column_Name")));
				  config.setType(Utils.isNull(rs.getString("data_type")));
				  config.setNulls(Utils.isNull(rs.getString("Nullable")).equalsIgnoreCase("Y")?true:false);
				}else if("MYSQL".equalsIgnoreCase(sourceType)){
				  config.setField(Utils.isNull(rs.getString("field")));
				  config.setType(Utils.isNull(rs.getString("type")));
				  config.setNulls(Utils.isNull(rs.getString("Null")).equalsIgnoreCase("Y")?true:false);
				}
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
					
					if("ORACLE".equalsIgnoreCase(sourceType)){
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
					}else if("MYSQL".equals(sourceType)){
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
					}
				}//for
				
				if(values != null && values.length() >0){
					values = values.substring(0,values.length()-1);
					sqlInsert = "insert into "+schema+"."+tableName+"("+columns+") values ("+values+")";
					//logger.debug("sqlInsert:"+sqlInsert);
					//insert to DB Dest
					int update =excUpdateOneSql(connDest, sqlInsert);
					
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
	
}
