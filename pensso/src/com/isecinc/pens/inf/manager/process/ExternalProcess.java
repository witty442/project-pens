package com.isecinc.pens.inf.manager.process;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import util.AppversionVerify;
import util.ControlCode;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.db.backup.DBBackUpManager;
import com.isecinc.pens.inf.manager.batchwork.DownloadSalesAppWorker;
import com.isecinc.pens.inf.manager.batchwork.UploadDatabaseBackupWorker;
import com.isecinc.pens.web.runscriptdb.RunScriptDBAction;
import com.pens.util.manual.cleardb.ClearDupDB;

public class ExternalProcess {

	public static Logger logger = Logger.getLogger("PENS");
	
	/******* Import *********************************************/
	public void processImportBefore(HttpServletRequest request,User userLogin){
		  logger.info("--- Import Before Process Start ---");
		   
		  //RunScript From FTP Server Folder :Manual-script
		  //logger.info("--- 1.Start Run Script import_before ---");
		 // RunScriptDBAction.runManualScriptProcess("import_before",userLogin);
		 // logger.info("--- 1.End Run Script import_before ---");
		  
	}
	
	public void processImportAfter(HttpServletRequest request,User userLogin){
		  logger.info("--- After importProcess Start ---");
		  
		  //logger.info("--- 1.Start Run Script import_after ---");
		 // RunScriptDBAction.runManualScriptProcess("import_after",userLogin);
		 // logger.info("--- 1.End Run Script import_after ---");
		 
		  //Get AppVersion and MessageToSales
		/*  logger.info("--- 2.Start Run processAfterImport ---");
		  AppversionVerify.processAfterImport(userLogin);
		  logger.info("--- 2.End Run processAfterImport ---");
		  
		  //Clear cust address dup Case Van Type
		  if(User.VAN.equals(userLogin.getType())){
			  if(ControlCode.canExecuteMethod("ClearDupDB", "clearDupCustDB")){
				  logger.info("--- 3.Start Run clearDupCustDB processAfterImport ---");
				  ClearDupDB.clearDupCustDB();
				  logger.info("--- 3.End Run clearDupCustDB processAfterImport ---");
			  }
			  if(ControlCode.canExecuteMethod("ClearDupDB", "clearDupCustAddressDB")){
				  logger.info("--- 3.Start Run clearDupCustAddressDB processAfterImport ---");
				  ClearDupDB.clearDupCustAddressDB();
				  logger.info("--- 3.End Run clearDupCustAddressDB processAfterImport ---");
			  }
			  
			  if(ControlCode.canExecuteMethod("ClearDupDB", "clearDupCustContactDB")){
				  logger.info("--- 3.Start Run clearDupCustContactDB processAfterImport ---");
				  ClearDupDB.clearDupCustContactDB();
				  logger.info("--- 3.End Run clearDupCustContactDB processAfterImport ---");
			  }
		  }*/
	}

	/******* export *********************************************/
	public void processExportBefore(HttpServletRequest request,User userLogin){
		 logger.info("--- Before ExportProcess Start ---");
		   
		/* //RunScript From FTP Server Folder :Manual-script
		 logger.info("--- 1.Start Run Script export_before ---");
		 RunScriptDBAction.runManualScriptProcess("export_before",userLogin);
		 logger.info("--- 1.End Run Script export_before ---");
		 
		 //Generate Receipt case User:Van ,have order and not Gen Receipt (default cash receipt)
		 if("VAN".equalsIgnoreCase(userLogin.getType())){
			 logger.info("--- 2.Start Run genAutoReceiptCash ---");
		     GenerateAutoReceiptVan.genAutoReceiptCash(userLogin);
		     logger.info("--- 2.End Run genAutoReceiptCash ---");
		 }
		 
		//Case Van PDPAID ='N' AND MONEY_TO_PENS ='Y  
		//update bank(last transfer cash) in t_receipt_by and payment_method ='CS' (CASH) ONLY
		if(    "VAN".equalsIgnoreCase(userLogin.getType()) 
			&& "N".equalsIgnoreCase(userLogin.getPdPaid())){
			
			UpdateBankReceiptCaseVANProcess.process(userLogin);//t_receipt
		}*/
	}
	
	public void processExportAfter(HttpServletRequest request,User userLogin){
		logger.info("--- after ExportProcess Start ---");
		 
		//DB BackUp DB and Transfer TO Ftp Server Folder:DB_Backup
		// logger.info("--- 1.Run Process backup By Thread ---");
		//new DBBackUpManager().process(userLogin);
		 
		 //Backup By Thread despricate
		//new UploadDatabaseBackupWorker(userLogin).start();
		
		//RunScript From FTP Server Folder :Manual-script
		//logger.info("--- 2.Start Run Script export_after ---");
		//RunScriptDBAction.runManualScriptProcess("export_after",userLogin);
		//logger.info("--- 2.End Run Script export_after ---");
		
		//Run SalesAppVersion to Ftp Server
		//logger.info("--- 2.Run uploadSalesAppVersion ---");
		//MonitorSales.uploadSalesAppVersion(userLogin);
	}
	
}
