package com.pens.utils;

import org.apache.log4j.Logger;

public class LoggerUtils {
	public static boolean debug = true;
	public static Logger logger = Logger.getLogger("PENS");
	private String className;
	
	public LoggerUtils(String className){
		this.className = className;
	}
	
	public void debug(String s){
		logger.debug(className+":"+s);
	}
	public void debug(String method,String s){
		logger.debug(className+":"+method+":"+s);
	}
	
	public void info(String s){
		logger.info(className+":"+s);
	}
	public void info(String method,String s){
		logger.info(className+":"+method+":"+s);
	}
	
	public void error(String s,Throwable e){
		logger.error(className+":"+s, e);
	}
	public void error(String method,String s,Throwable e){
		logger.error(className+":"+method+":"+s, e);
	}
	public void error(String s){
		logger.error(className+":"+s);
	}
	public void error(String method,String s){
		logger.error(className+":"+method+":"+s);
	}
}
