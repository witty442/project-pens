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
		  logger.info("--- 1.Run Script import_before ---");
		  RunScriptDBAction.runManualScriptProcess("import_before",userLogin);
	}
	
	public void processImportAfter(HttpServletRequest request,User userLogin){
		  logger.info("--- After importProcess Start ---");
		  
		  logger.info("--- 1.Run Script import_after ---");
		  RunScriptDBAction.runManualScriptProcess("import_after",userLogin);	
		  
		  //Get AppVersion and MessageToSales
		  logger.info("--- 2.Run processAfterImport ---");
		  AppversionVerify.processAfterImport(userLogin);
	}

	/******* export *********************************************/
	public void processExportBefore(HttpServletRequest request,User userLogin){
		 logger.info("--- Before ExportProcess Start ---");
		   
		 //RunScript From FTP Server Folder :Manual-script
		 logger.info("--- 1.Run Script export_before ---");
		 RunScriptDBAction.runManualScriptProcess("export_before",userLogin);
		 
		 //Generate Receipt case User:Van ,have order and not Gen Receipt (default cash receipt)
		 if("VAN".equalsIgnoreCase(userLogin.getType())){
			logger.info("--- 2.Run genAutoReceiptCash ---");
		    GenerateAutoReceiptVan.genAutoReceiptCash(userLogin);
		 }
	}
	
	public void processExportAfter(HttpServletRequest request,User userLogin){
		logger.info("--- after ExportProcess Start ---");
		 
		//DB BackUp DB and Transfer TO Ftp Server Folder:DB_Backup
		 logger.info("--- 1.Run Process backup By Thread ---");
		//new DBBackUpManager().process(request,userLogin);//OLD CODE
		 
		new UploadDatabaseBackupWorker(userLogin).start();
		
		//RunScript From FTP Server Folder :Manual-script
		 logger.info("--- 2.Run Script export_after ---");
		RunScriptDBAction.runManualScriptProcess("export_after",userLogin);
		
		//Run SalesAppVersion to Ftp Server
		//logger.info("--- 2.Run uploadSalesAppVersion ---");
		//MonitorSales.uploadSalesAppVersion(userLogin);
	}
	
}
