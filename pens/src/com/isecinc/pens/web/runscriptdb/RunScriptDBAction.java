package com.isecinc.pens.web.runscriptdb;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

public class RunScriptDBAction {
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
			
			String appVersion =SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
			String status = findStatusRunnScriptDB(conn,appVersion);
			logger.info("appVersion:"+appVersion+",status run:"+status);
			
			if(STATUS_RUN_BLANK.equalsIgnoreCase(status)){
				runScriptDBUpdate(sc, conn);
				
				/** Drop table no use or update or clear  wrong data**/
				dropTableOrClearWrongData(conn);
			}
			
			//Purg data in First Day of Month 1/xx/xxxx 
			Calendar c = Calendar.getInstance();
			int day = c.get(Calendar.DATE);
			logger.info("Check Day for PrugeMonitor Day(1,2,25,26,27) now_day:"+day);
			
			//Delete All Monitor  submit_date < 1 month 
			if(day==1 || day ==2 || day ==25 || day==26 || day==27){
				purgDataMonitor(conn);	
			}
				
			//Delete m_sales_target_new back 2 month
			if(day==5){
				purgDataSalesTarget(conn);	
			}
			
			//clear Task running for next run
			InterfaceDAO dao = new InterfaceDAO();
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_IMPORT);
			dao.updateControlMonitor(new BigDecimal(0),Constants.TYPE_EXPORT);

			//Update location is unvalid format
			
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
	
	public static void runManualScriptProcess(String prefix,User user){
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			
			/** Run All Sales **/
			runManualScriptProcessAllSales(conn,prefix,user.getUserName());
			
			/** Run ManualScript By Sales Type VAN or Credit **/
			logger.debug("role:"+user.getRole().getKey());
			String salesType = "van";
			if("TT".equals(user.getRole().getKey())){
				salesType  ="credit";
			}
			runManualScriptProcessBySalesType(conn,prefix,salesType);
			
			/** Run ManualScript By SalesCode **/
			runManualScriptProcessBySalesCode(conn,prefix,user.getUserName());
			
			
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
	
	
	/**
	 * 
	 * @param conn
	 * @param userName
	 * process : run In BatchExportManager : run manaul script db all salesCode
	 * ex fileName : /AllSales/script.sql  -->success no move   and stamp sales is run to ResultSalesRun.txt;
	 */
	public static void runManualScriptProcessAllSales(Connection conn,String prefix,String userName){
		EnvProperties env = EnvProperties.getInstance();
		try{
			logger.info("Start runManualScriptProcessAllSale: "+env.getProperty("path.manual.AllSales")+prefix+"_script.sql");
			//read data from FTP /Manual_script 
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			String scriptData = ftpManager.getDownloadFTPFileByName(env.getProperty("path.manual.AllSales")+prefix+"_script.sql","TIS-620");
			
			logger.debug("scriptData:"+scriptData);
			
			// Excute Script
			if( !Utils.isNull(scriptData).equals("")){
				String resultStr = excUpdate(conn,Utils.isNull(scriptData));
				logger.debug("resultExeSctipt:"+resultStr);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @param userName
	 * process : run In BatchExportManager : run manaul script db all salesCode
	 * ex fileName : /AllSales/script.sql  -->success no move   and stamp sales is run to ResultSalesRun.txt;
	 */
	public static void runManualScriptProcessBySalesType(Connection conn,String prefix,String salesType){
		EnvProperties env = EnvProperties.getInstance();
		try{
			logger.debug("Start runManualScriptProcessAllSale: "+env.getProperty("path.manual.BySalesType")+salesType+"_"+prefix+"_script.sql");
			//read data from FTP /Manual_script 
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			String scriptData = ftpManager.getDownloadFTPFileByName(env.getProperty("path.manual.BySalesType")+salesType+"_"+prefix+"_script.sql","TIS-620");
			
			logger.debug("scriptData:"+scriptData);
			
			// Excute Script
			if( !Utils.isNull(scriptData).equals("")){
				String resultStr = excUpdate(conn,Utils.isNull(scriptData));
				logger.debug("resultExeSctipt:"+resultStr);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * runManualScriptProcessBySalesCode
	 * @param userName
	 * process : run In BatchExportManager : run manaul script db by SalesCode
	 * ex fileName : /BySales/script_V107.sql  >success move to /BySales/In-Processed/
	 */
	public static void runManualScriptProcessBySalesCode(Connection conn,String prefix,String userName){
		EnvProperties env = EnvProperties.getInstance();
		String resultStr ="";
		try{
			logger.info("Start runManualScriptProcessBySalesCode : "+env.getProperty("path.manual.BySales")+prefix+"/script_"+userName+".sql");
			//read data from FTP /Manual_script 
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			String scriptData = ftpManager.getDownloadFTPFileByName(env.getProperty("path.manual.BySales")+prefix+"/script_"+userName+".sql","TIS-620");
			
			logger.info("scriptData:"+scriptData);
			
			// Excute Script
			if( !Utils.isNull(scriptData).equals("")){
				resultStr = excUpdate(conn,Utils.isNull(scriptData));
				logger.debug("resultExeSctipt:"+resultStr);
			
				// delete and Create new  File Ftp To In Processs
				ftpManager.deleteFileFTP(env.getProperty("path.manual.BySales")+prefix+"/", "script_"+userName+".sql");
			
				//rename fileName
				String fileName = "script_"+userName+"_"+Utils.format(new Date(), Utils.YYYY_MM_DD_WITHOUT_SLASH)+".sql";
				ftpManager.uploadFileToFTP(env.getProperty("path.manual.BySales")+prefix+"/"+"In-Processed/", fileName, resultStr, "TIS-620");
			
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	
	/**
	 * purgDataMonitor
	 * @param conn
	 * @return
	 * submit_date < 1 month 
	 */
	private static boolean purgDataMonitor(Connection conn){
		boolean success = false;
		StringBuffer sql = new StringBuffer("");
		try{
			logger.info("*** Start PurgData Monitor ***************");
			
			sql.append(" delete  from monitor_item_detail where monitor_item_id in( \n");
			sql.append("  select id from monitor_item where monitor_id in ( \n");
			sql.append("    select monitor_id from monitor \n");
			sql.append("    where submit_date < SUBDATE(NOW(),INTERVAL 1 MONTH) \n");
			sql.append("   ) \n");
			sql.append(" ) ; \n");

			sql.append(" delete from monitor_item where monitor_id in( \n");
			sql.append("   select monitor_id from monitor \n");
			sql.append("   where submit_date < SUBDATE(NOW(),INTERVAL 1 MONTH) \n");
			sql.append(" ); \n");

			sql.append(" delete from monitor where submit_date < SUBDATE(NOW(),INTERVAL 1 MONTH); \n");

			logger.info(excUpdate(conn,sql.toString()));
			
			logger.info("*** Success PurgData Monitor ***************");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return success;
  }
	
	
	//Delete Data m_sales_target_new back 2 month
	private static boolean purgDataSalesTarget(Connection conn){
		boolean success = false;
		StringBuffer sql = new StringBuffer("");
		try{
			logger.debug("*** Start PurgData m_sales_target_new back 2 month ***************");
			
			sql.append(" delete from m_sales_target_new where sales_target_id in (  \n");
			sql.append(" select A.* from( select sales_target_id from m_sales_target_new   \n");
			sql.append(" where target_from < SUBDATE(NOW(),INTERVAL 2 MONTH)   \n");
			sql.append(" )A ) \n");

			logger.debug(excUpdate(conn,sql.toString()));
			
			logger.info("*** Success PurgData m_sales_target_new back 2 month ***************");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return success;
  }

	private static boolean runScriptDBUpdate(ServletContext sc ,Connection conn){
		boolean success = false;
		try{
			logger.info("*** Start runScriptDBUpdater ***************");
			
		   //Read script from /script_db/script_db.sql
			String pathScriptDB = sc.getRealPath(path_script_db)+"/script_db.sql";
			logger.debug("read path:"+pathScriptDB);
			String dataFile = FileUtil.readFile(pathScriptDB, "TIS-620");
			
			//logger.info("Data File:"+dataFile);
			
		   //run script split by ";"
			logger.info(excUpdate(conn,dataFile));
			logger.info("*** Success runScriptDBUpdater ***************");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			
		}
		return success;
	}
	
	/**
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private static String findStatusRunnScriptDB(Connection conn,String appVersion) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		String status = STATUS_RUN_BLANK;
		try{
			//Check table Exist
			sql.append(" SELECT table_name, table_type, engine \n");
			sql.append("  FROM information_schema.tables \n");
			sql.append("  WHERE table_schema = '"+schema+"' \n");
			sql.append("  and engine ='InnoDB' \n");
			sql.append("  and table_name ='"+table_control+"' \n");
		
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){//Found Table
				
				sql = new StringBuffer("select status ,app_version, create_date from "+table_control +" where app_version ='"+appVersion+"'");
				logger.debug("sqlectSQl:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				
				if(rs.next()){ //found 
					status = rs.getString("status");
					/** Check Appverison is if run done then nothing else set status =run_done */
				    if( !STATUS_RUN_SUCCESS.equals(Utils.isNull(status))){
				    	// not run  -->set status to run_done
				    	logger.debug("status "+status);
				    	status = STATUS_RUN_BLANK;
				    	ps = conn.prepareStatement("update "+table_control+" set status =? ,create_date =? where app_version = ?");
						ps.setString(1, STATUS_RUN_SUCCESS);
						ps.setDate(2, new java.sql.Date(new Date().getTime()));
						ps.setString(3, appVersion);
						ps.execute();
				    }
			    }else{//not found record insert new record
			    	status = STATUS_RUN_BLANK;
			    	ps = conn.prepareStatement(" insert into "+table_control+"(status,app_version,create_date)values(?,?,?)");
					ps.setString(1, STATUS_RUN_SUCCESS);
					ps.setString(2, appVersion);
					ps.setDate(3, new java.sql.Date(new Date().getTime()));
					ps.execute();
			    	
			    }
				
			}else{//Not Found Table
				
				sql = new StringBuffer(" CREATE TABLE "+table_control+" (status varchar(15) not null,app_version varchar(20) ,create_date timestamp not null) ");
				ps = conn.prepareStatement(sql.toString());
				ps.execute();
				logger.debug("Create table "+table_control);
				
				ps = conn.prepareStatement(" insert into "+table_control+"(status,app_version,create_date)values(?,?,?)");
				ps.setString(1, STATUS_RUN_SUCCESS);
				ps.setString(2, appVersion);
				ps.setDate(3, new java.sql.Date(new Date().getTime()));
				ps.execute();
				
				status = STATUS_RUN_BLANK;
				logger.debug("Insert value table "+table_control);
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
		return status;
	} 
	
	
	private static String excUpdate(Connection conn,String sql) {
	    PreparedStatement ps =null;
        StringBuffer str = new StringBuffer("");
		try{  
			
			String[] sqlArr = sql.split("\\;");
			if(sqlArr != null && sqlArr.length>0){
			  //str.append("\n ------ Result ----------------------- ");
			   for(int i=0;i<sqlArr.length;i++){
				 
				 if( !Utils.isNull(sqlArr[i]).equals("")){
					 try{
					     ps = conn.prepareStatement(sqlArr[i]);
					     int recordUpdate = ps.executeUpdate();
					     //str.append("["+i+"] SQL Execute  :"+sqlArr[i]);
					     str.append("\n"+sqlArr[i]+"- eff:"+recordUpdate+" ");
					 }catch(Exception ee){
						 str.append("\n"+sqlArr[i]+"- err:"+ee.getMessage()+" "); 
					 }
			     }
			   }//for
			  // str.append("\n ----------------------------------\n");
			}
		}catch(Exception e){
	      logger.error(e.getMessage(),e);
	      str.append("err: \n"+e.getMessage() +"\n");
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return str.toString();
  }
	
	private static void dropTableOrClearWrongData(Connection conn) {
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		try{
			logger.info("--dropTableOrClearWrongData--");
			
			//Check table Exist
			/*sql.append(" SELECT table_name, table_type, engine \n");
			sql.append("  FROM information_schema.tables \n");
			sql.append("  WHERE table_schema = '"+schema+"' \n");
			sql.append("  and engine ='InnoDB' \n");
			sql.append("  and table_name ='temp_run_script_db' \n");
		
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){//Found Table
				sql = new StringBuffer("drop table temp_run_script_db");
				logger.debug("drop table :"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				ps.executeUpdate();
			}	*/

			/** Update monitor_item proe_rel fileName wrong **/
			 /** 2012 10 15 1030 1-PROREL.txt **/
			
			/*sql.append("  select id from monitor_item where id in(select max(id) from monitor_item where table_name ='m_relation_modifier') \n");
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				sql = new StringBuffer("");
				sql.append(" UPDATE monitor_item \n");
				sql.append(" SET file_name =  (CONCAT( DATE_FORMAT(NOW(), '%Y%m%d%H%i')  ,'-PROREL.txt') ) \n");
				sql.append(" WHERE  id = "+rs.getInt("id")+" \n") ;
						
				logger.debug("update monitor_item :"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				logger.debug("update monitor_item result:"+ps.executeUpdate());
			}	
			*/
		
		}catch(Exception e){
	     logger.error("Error don't check drop table temp"+e.getMessage());
		}finally{
			try{
				if(ps != null){
				   ps.close();ps = null;
				}
				if(rs != null){
				   rs.close();rs = null;
				}
			}catch(Exception e){
			}
		}
	}

}
