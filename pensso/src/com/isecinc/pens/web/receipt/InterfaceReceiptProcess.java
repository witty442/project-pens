package com.isecinc.pens.web.receipt;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.ControlCode;
import util.SQLHelper;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.inf.manager.UpdateSalesManager;
import com.isecinc.pens.inf.manager.process.ExportProcess;
import com.isecinc.pens.inf.manager.process.export.LockboxProcess;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.web.batchtask.BatchTaskConstants;
import com.isecinc.pens.web.batchtask.BatchTaskManualAction;
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

	public static void processImportReceipt(User user,HttpServletRequest request) throws Exception{
		try{
			//Wait for next Process (new)
			//new BatchTaskManualAction().runBatchTask(user, BatchTaskConstants.IMPORT_RECEIPT);
			
			//OldCode
			//t_receipt|UPDATE-TRANS-SALES
			//new ImportManager().importByRequestTable(user,user, "t_receipt", request,false,"UPDATE-TRANS-SALES");
			
			MonitorBean m = new ImportManager().importMain(user,user,"",request,false,"UPDATE-TRANS-SALES");
			logger.debug("Monitor.Transaction["+m.getTransactionId()+"]status["+m.getStatus()+"]");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static boolean process(User user,long receiptId,String orderNo) throws Exception{
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
