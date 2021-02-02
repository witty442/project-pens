package com.pens.util;

import org.apache.log4j.Logger;

public class Debug {

	private Logger logger = Logger.getLogger("PENS");
	private boolean isDebug = true;
	
	public boolean isDebugEnable(){
		return logger.isDebugEnabled();
	}
	
	public Debug(boolean isDebug){
		this.isDebug = isDebug;
	}

	public void debug(String message){
		if(isDebug){
		  logger.debug(message);
		}
	}
	
	public void info(String message){
		if(isDebug){
		  logger.info(message);
		}
	}
}
