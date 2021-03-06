package com.isecinc.pens.inf.manager.process;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.AppversionVerify;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.db.backup.DBBackUpManager;
import com.isecinc.pens.inf.manager.batchwork.DownloadSalesAppWorker;
import com.isecinc.pens.inf.manager.batchwork.UploadDatabaseBackupWorker;
import com.isecinc.pens.web.runscriptdb.RunScriptDBAction;

public class ExternalProcess {

	public static Logger logger = Logger.getLogger("PENS");
	
	/******* Import *********************************************/
	public void processImportBefore(HttpServletRequest request,User userLogin){
		  logger.info("--- Import Before Process Start ---");
		   
		  //RunScript From FTP Server Folder :Manual-script
		  logger.info("--- 1.Start Run Script import_before ---");
		  RunScriptDBAction.runManualScriptProcess("import_before",userLogin);
		  logger.info("--- 1.End Run Script import_before ---");
	}
	
	public void processImportAfter(HttpServletRequest request,User userLogin){
		  logger.info("--- After importProcess Start ---");
		  
		  logger.info("--- 1.Start Run Script import_after ---");
		  RunScriptDBAction.runManualScriptProcess("import_after",userLogin);
		  logger.info("--- 1.End Run Script import_after ---");
		 
		  //Get AppVersion and MessageToSales
		  logger.info("--- 2.Start Run processAfterImport ---");
		  AppversionVerify.processAfterImport(userLogin);
		  logger.info("--- 2.End Run processAfterImport ---");
	}

	/******* export *********************************************/
	public void processExportBefore(HttpServletRequest request,User userLogin){
		 logger.info("--- Before ExportProcess Start ---");
		   
		 //RunScript From FTP Server Folder :Manual-script
		 logger.info("--- 1.Start Run Script export_before ---");
		 RunScriptDBAction.runManualScriptProcess("export_before",userLogin);
		 logger.info("--- 1.End Run Script export_before ---");
		 
		 //Generate Receipt case User:Van ,have order and not Gen Receipt (default cash receipt)
		 if("VAN".equalsIgnoreCase(userLogin.getType())){
			 logger.info("--- 2.Start Run genAutoReceiptCash ---");
		     GenerateAutoReceiptVan.genAutoReceiptCash(userLogin);
		     logger.info("--- 2.End Run genAutoReceiptCash ---");
		 }
	}
	
	public void processExportAfter(HttpServletRequest request,User userLogin){
		logger.info("--- after ExportProcess Start ---");
		 
		//DB BackUp DB and Transfer TO Ftp Server Folder:DB_Backup
		 logger.info("--- 1.Run Process backup By Thread ---");
		new DBBackUpManager().process(userLogin);
		 
		 //Backup By Thread
		//new UploadDatabaseBackupWorker(userLogin).start();
		
		//RunScript From FTP Server Folder :Manual-script
		logger.info("--- 2.Start Run Script export_after ---");
		RunScriptDBAction.runManualScriptProcess("export_after",userLogin);
		logger.info("--- 2.End Run Script export_after ---");
		
		//Run SalesAppVersion to Ftp Server
		//logger.info("--- 2.Run uploadSalesAppVersion ---");
		//MonitorSales.uploadSalesAppVersion(userLogin);
	}
	
}
