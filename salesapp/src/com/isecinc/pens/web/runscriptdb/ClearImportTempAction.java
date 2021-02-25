package com.isecinc.pens.web.runscriptdb;

import java.sql.Connection;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;

public class ClearImportTempAction {
	protected static  Logger logger = Logger.getLogger("PENS");
	private static String schema ="pens";
	private static String table_control ="c_control_run_script_db";
    private static String path_script_db = "/script_db/";
    private static String STATUS_RUN_SUCCESS ="success";
    private static String STATUS_RUN_BLANK ="blank";
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void runProcessOnStartSalesApp(ServletContext sc ){
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			clearImportTemp(conn);
		

		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();conn= null;
				}
			}catch(Exception e){}
		}
	}
	
	private static boolean clearImportTemp(Connection conn){
		boolean success = false;
		StringBuffer sql = new StringBuffer("");
		try{
			logger.info("*** Start clearImportTemp  ***************");
			
			sql.append(" select distinct pens.t_temp_import_trans  \n");
			sql.append("  select id from monitor_item where monitor_id in ( \n");
			sql.append("    select monitor_id from monitor \n");
			sql.append("    where submit_date < SUBDATE(NOW(),INTERVAL 1 MONTH) \n");
			sql.append("   ) \n");

			logger.info("*** Success clearImportTemp  ***************");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return success;
  }
	
}
