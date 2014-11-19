package com.isecinc.pens.inf.manager.process;

import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.EnvProperties;

public class FunctionHelper {

	protected static Logger logger = Logger.getLogger("PENS");
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param path
	 * @param tableBean
	 * @return
	 * @throws Exception
	 * readSQlExternalFunc  case read manual script delete fro delete promotion
	 */
	public static  List<String> readSQlExternalFunction(String function ,String tableName) throws Exception {
		logger.debug("readSQlExternalFunc function["+function+"] Table Name:["+tableName+"]");
		EnvProperties env = EnvProperties.getInstance();		
		List<String> sqlList = null;
		try {
		
		}catch(Exception e){
           logger.error(e.getMessage(),e);
		} finally {
			//FileUtil.close(br);
		}
		return sqlList;
	}
	
	public static String getPathFullName(String function ,String tableName){
		String pathManualScript ="";
		if("Pre".equalsIgnoreCase(function)){
		   pathManualScript = "/Manual-script/Script-Pre-Import/"+tableName+".sql";
		}else if("Post".equalsIgnoreCase(function)){
		   pathManualScript = "/Manual-script/Script-Post-Import/"+tableName+".sql";
		}
		return pathManualScript;
	}
}
