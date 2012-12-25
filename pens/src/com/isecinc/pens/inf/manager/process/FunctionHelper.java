package com.isecinc.pens.inf.manager.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.EnvProperties;
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
	public static  List<String> readSQlExternalFunction(String function ,String tableName) throws Exception {
		logger.debug("readSQlExternalFunc function["+function+"] Table Name:["+tableName+"]");
		EnvProperties env = EnvProperties.getInstance();		
		List<String> sqlList = null;
		try {
			String pathFullName = getPathFullName(function,tableName);
			//logger.info("pathFullName["+pathFullName+"]");
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			String sqlAll = ftpManager.getDownloadFTPFileByName(pathFullName);
			//logger.info("SQLALL:"+sqlAll);
			
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
