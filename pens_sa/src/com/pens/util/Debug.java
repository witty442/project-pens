package com.pens.util;

import org.apache.log4j.Logger;

public class Debug {

	private Logger logger = Logger.getLogger("PENS");
	private boolean isDebug = true;
	private int level = 0;
	public static int level_0 = 0;//debug all;
	public static int level_1 = 1;//debug request;

	
	public Debug(boolean isDebug,int level){
		this.isDebug = isDebug;
		this.level = level;
	}

	public void debug(String message){
		if(isDebug){
			if(level==level_0){
		      //logger.debug(message);
			}
		}
	}
	
	public void debug(String message,int level){
		if(isDebug){
			if(level == level_1){
		     // logger.debug(message);
			}
		}
	}
	
	public void info(String message){
		if(isDebug){
			if(level==level_0){
		      logger.info(message);
			}
		}
	}
	
	public void info(String message,int level){
		if(isDebug){
			if(level == level_1){
		      logger.info(message);
			}
		}
	}
}
