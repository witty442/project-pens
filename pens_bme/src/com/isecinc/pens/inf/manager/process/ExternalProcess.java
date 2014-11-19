package com.isecinc.pens.inf.manager.process;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;

public class ExternalProcess {

	public static Logger logger = Logger.getLogger("PENS");
	
	/******* Import *********************************************/
	public void processImportBefore(HttpServletRequest request,User userLogin){
		  logger.info("--- Import Before Process Start ---");

	}
	
	public void processImportAfter(HttpServletRequest request,User userLogin){
		  logger.info("--- After importProcess Start ---");
		   
	}

	/******* export *********************************************/
	public void processExportBefore(HttpServletRequest request,User userLogin){
		 logger.info("--- Before ExportProcess Start ---");
		
	}
	
	public void processExportAfter(HttpServletRequest request,User userLogin){
		logger.info("--- after ExportProcess Start ---");
		 
	}
	
}
