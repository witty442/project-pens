package com.isecinc.pens.web.receipt;

import java.math.BigDecimal;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.inf.manager.ImportTransReceiptBudsSalesManager;
import com.isecinc.pens.inf.manager.UpdateSalesManager;
import com.isecinc.pens.inf.manager.process.ExportProcess;
import com.isecinc.pens.inf.manager.process.export.LockboxProcess;
import com.pens.util.EnvProperties;

public class InterfaceReceiptProcess {

	public static Logger logger = Logger.getLogger("PENS");
	
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

	public static void processImportReceiptBySales(User user,HttpServletRequest request) throws Exception{
		try{
			MonitorBean m = new UpdateSalesManager().importMain(user, user, "", request, false);
			logger.debug("Monitor.Transaction["+m.getTransactionId()+"]status["+m.getStatus()+"]");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static BigDecimal processImportReceiptAllSales(User user) throws Exception{
		try{
			logger.info("start processImportReceiptAllSales");
			MonitorBean m = new ImportTransReceiptBudsSalesManager().initImport(user,false);
			logger.debug("Monitor.Transaction["+m.getTransactionId()+"]status["+m.getStatus()+"]");
			
			return m.getTransactionId();
		}catch(Exception e){
			throw e;
		}
	}
	public static boolean processExportReceiptLockBox(User user,long receiptId,String orderNo) throws Exception{
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
	public static boolean process(User user) throws Exception{
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
