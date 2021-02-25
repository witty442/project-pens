package com.isecinc.pens.interfaces.exports;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.FileImportTransBean;
import com.isecinc.pens.bean.KeyNoImportTransBean;
import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.bean.TableBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.exception.ExceptionHandle;
import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.ExternalFunctionHelper;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.TransactionHelper;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.process.ExportProcess;
import com.isecinc.pens.inf.manager.process.ResultImportBean;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptFunction;
import com.isecinc.pens.inf.manager.process.imports.ImportReceiptHelper;
import com.isecinc.pens.interfaces.exports.process.LockboxProcess;
import com.isecinc.pens.interfaces.imports.ImportOrderProcess;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.pens.util.DBConnection;
import com.pens.util.EnvProperties;
import com.pens.util.Utils;

/**
 * @author WITTY
 *
 */
public class ExportReceiptManager {
   
	public static Logger logger = Logger.getLogger("PENS");
	//public static LoggerUtils logger = new LoggerUtils("UpdateSalesManager");
	
    public static boolean debug = true;
    
    public static void main(String[] a){
		try{
			User user = new User();
			user.setId(100000047);
			user.setCode("S001");
			user.setName("XXXX");
			user.setUserName("S001");
			References ref = new References("ROLE","TT","CreditSales");
			user.setRole(ref);
			//process(user,2,"RS00163070002");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    //Run By Scheduler all sales
    //parameter SalesCode :by sales
    public MonitorBean process(User userLogin,String salesCode) throws Exception{
		Connection connMonitor = null;
		Connection conn = null;
		MonitorBean monitorModel = null;
		InterfaceDAO dao = new InterfaceDAO();
		FTPManager ftpManager = null;
		EnvProperties env = EnvProperties.getInstance();
		ExportProcess exProcess = new ExportProcess();
		try{
			connMonitor = DBConnection.getInstance().getConnection();
			
			/** insert to monitor_interface **/
			monitorModel = new MonitorBean();
			monitorModel.setName("Export Receipt(Lockbox)");
			monitorModel.setType(BatchTaskConstants.TYPE_EXPORT);
			monitorModel.setStatus(BatchTaskConstants.STATUS_START);
			monitorModel.setCreateUser(userLogin.getUserName());
			monitorModel.setTransactionType("TRANSACTION");
			monitorModel = dao.insertMonitor(connMonitor,monitorModel);
				
			logger.debug(" **********Start Export Receipt (LockBox)Sales  ******************");
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			//initial FTP Manager
			ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			//List all sales 
			List<User> salesrepList = new MUser().getSalesList(salesCode);
			if(salesrepList != null && salesrepList.size()>0) {
				for(User salesUser:salesrepList) {
			       
				logger.info("--Start Export t_receipt(lockbox) all sales --");
				TableBean tableBean = new TableBean();
				tableBean.setTableName("t_receipt");
				tableBean.setFileFtpName("SALESRECEIPT");
				tableBean.setSource("SALES");
				tableBean.setDestination("ORACLE");
				tableBean.setTransactionType("TRANSACTION");
				tableBean.setAuthen("AD|TT|VAN");
				
				tableBean.setFileFtpNameFull(ExportHelper.genFileName(tableBean,salesUser));
				tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.transaction.sales.out"));
				
				tableBean = new LockboxProcess().exportSalesReceiptLockBoxProcess(conn, tableBean,salesUser);	
				
				//Upload by step by one file
				ftpManager.uploadAllFileToFTP_BY_FILE("",tableBean);	
        	
				//update Exported =Y
				logger.debug("***** -Start  Update Exported=Y  Flag ALL TABLE*************");
			    int updateRecord = exProcess.updateExportFlag(conn, salesUser,tableBean.getSqlUpdateExportFlagList());
			    logger.debug("***** -Result Update Exported=Y  Flag ALL TABLE TotalRec:"+updateRecord+" *************");
				}//for
			}//if
			logger.info("--End Export t_receipt(lockbox) --");
			conn.commit();
			
			logger.debug(" **********Result Export Receipt(Lockbox) All Sales :"+monitorModel.getStatus()+" ******************");
	
			//Stamp Task to Success
			logger.info("startTaskStatus transactionId["+monitorModel.getTransactionId()+"]monitorId["+monitorModel.getMonitorId()+"]");
			dao.updateTaskStatus(monitorModel.getTransactionId(),monitorModel.getMonitorId(),"0"); //Start BatchTask = 0;
			
		}catch(Exception e){
			conn.rollback();
			logger.error(e.getMessage(),e);
		}finally{
			if(connMonitor != null){
				connMonitor.close();
				connMonitor=null;
			}
			if(conn != null){
				conn.close();
				conn=null;
			}
		}
	    return monitorModel;
	}
    
    //Run By User Action
	public static boolean processManual(User user) throws Exception{
		boolean r = true;
		Connection conn = null;
		FTPManager ftpManager = null;
		EnvProperties env = EnvProperties.getInstance();
		ExportProcess exProcess = new ExportProcess();
		try{
			conn = DBConnection.getInstance().getConnection();
			//initial FTP Manager
			ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			logger.info("--Start Export t_receipt(lockbox) --");
			TableBean tableBean = new TableBean();
			tableBean.setTableName("t_receipt");
			tableBean.setFileFtpName("SALESRECEIPT");
			tableBean.setSource("SALES");
			tableBean.setDestination("ORACLE");
			tableBean.setTransactionType("TRANSACTION");
			tableBean.setAuthen("AD|TT|VAN");
			
			tableBean.setFileFtpNameFull(ExportHelper.genFileName(tableBean,user));
			tableBean.setExportPath(EnvProperties.getInstance().getProperty("path.transaction.sales.out"));
			
			tableBean = new LockboxProcess().exportSalesReceiptLockBoxProcess(conn, tableBean,user);	
			
			//Upload by step by one file
			ftpManager.uploadAllFileToFTP_BY_FILE("",tableBean);	
        	
			//update Exported =Y
			logger.debug("***** -Start  Update Exported=Y  Flag ALL TABLE*************");
		    int updateRecord = exProcess.updateExportFlag(conn, user,tableBean.getSqlUpdateExportFlagList());
		    logger.debug("***** -Result Update Exported=Y  Flag ALL TABLE TotalRec:"+updateRecord+" *************");
			
			logger.info("--End Export t_receipt(lockbox) --");
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return r;
	}
	
	

}
