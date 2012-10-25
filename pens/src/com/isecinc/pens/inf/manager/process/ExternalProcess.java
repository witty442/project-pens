package com.isecinc.pens.inf.manager.process;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.db.backup.DBBackUpManager;
import com.isecinc.pens.web.runscriptdb.RunScriptDBAction;

public class ExternalProcess {

	protected Logger logger = Logger.getLogger("PENS");
	
	/******* Import *********************************************/
	public void processImportBefore(HttpServletRequest request,User userLogin){
		  logger.info("--- Import Before Process Start ---");
		   
		  //RunScript From FTP Server Folder :Manual-script
		  RunScriptDBAction.runManualScriptProcess("import_before",userLogin);
		 		
	}
	
	public void processImportAfter(HttpServletRequest request,User userLogin){
		  logger.info("--- After importProcess Start ---");
		   
		  RunScriptDBAction.runManualScriptProcess("import_after",userLogin);
		 		
	}
	
	
	/******* export *********************************************/
	public void processExportBefore(HttpServletRequest request,User userLogin){
		  logger.info("--- Before ExportProcess Start ---");
		   
		 //RunScript From FTP Server Folder :Manual-script
		 RunScriptDBAction.runManualScriptProcess("export_before",userLogin);
		 		
	}
	
	public void processExportAfter(HttpServletRequest request,User userLogin){
		logger.info("--- PostExportProcess Start ---");
		 
		//DB BackUp DB and Transafer TO Ftp Server Folder:DB_Backup
		new DBBackUpManager().process(request,userLogin);
		
		//RunScript From FTP Server Folder :Manual-script
		RunScriptDBAction.runManualScriptProcess("export_after",userLogin);
		
		//OLD ManualScript Next version wil be remove 
		RunScriptDBAction.runManualScriptProcess_OLDCODE(userLogin.getUserName());
				
	}
	
}
