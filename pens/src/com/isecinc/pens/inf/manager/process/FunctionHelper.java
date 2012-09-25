package com.isecinc.pens.inf.manager.process;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

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
	public static  List<String> readSQlExternalFunction(String functionName) throws Exception {
		logger.debug("readSQlExternalFunc Name:["+functionName+"]");
		EnvProperties env = EnvProperties.getInstance();		
		List<String> sqlList = null;
		try {
			String pathFullName = getPathFullName(functionName);
			logger.info("pathFullName["+pathFullName+"]");
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			String sqlAll = ftpManager.getDownloadFTPFileByName(pathFullName);
			logger.info("SQLALL:"+sqlAll);
			
			sqlList = new ArrayList<String>();
			
			String[] splitSql = sqlAll.split("\\;");
			if(splitSql != null && splitSql.length > 0){
				for(int i=0;i<splitSql.length;i++){
				   sqlList.add(splitSql[i]);
				}
			}
		}catch(Exception e){
           logger.error(e.getMessage(),e);
		} finally {
			//FileUtil.close(br);
		}
		return sqlList;
	}
	
	public static String getPathFullName(String functionName){
		String pathManualScript  = "/Manual-script/";
		if(functionName.indexOf("promotion") != -1){
			pathManualScript += "Promotion/"+functionName+".sql";
		}else if(functionName.indexOf("product") != -1){
			pathManualScript += "Product/"+functionName+".sql";
		}
		return pathManualScript;
	}
}
