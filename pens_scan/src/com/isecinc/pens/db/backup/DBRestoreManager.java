package com.isecinc.pens.db.backup;

import java.io.BufferedReader;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;

/**
 * @author WITTY
 *
 */
public class DBRestoreManager {
   
	protected static  Logger logger = Logger.getLogger("PENS");
	private static String s ="`";
	private static String PATH_DEFAULT ="c:/DB_Backup/";
	
	private static String[] mysqlCommand = new String[]{
	     "SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT;"
		,"SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS ;"
		,"SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION ;"
		,"SET NAMES utf8;"
		,"SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 ;"
		,"SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 ;"
		,"SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';"};
	
	public static String process(HttpServletRequest request){
		Connection conn = null;
		PreparedStatement psCommand = null;
		PreparedStatement psStatement = null;
		Statement st = null;
		ResultSet rs = null;
		int i = 0;
		BufferedReader br = null;
		String schema = "pens";
		String pathFull = "";
		try{
			conn = DBConnection.getInstance().getConnection();
			//GET FileName lastest from folder back
			//pathFull = "c:/DB_backup/pens_201105191420.sql";//c_message
			pathFull = "c:/DB_backup/pens_201105181608.sql";//all
			logger.debug("Success step1");
			
			//run command Mysql disable foreignkey 
			st = conn.createStatement();
			for(i=0;i<mysqlCommand.length;i++){
				//logger.debug("command:"+mysqlCommand[i] +"->result:"+st.executeUpdate(mysqlCommand[i]));
				rs = st.executeQuery(mysqlCommand[i]);
				//logger.debug("rs Exc:"+)
			}
            logger.debug("Success step2");
			
			//read content file and split by comma  
			String fileStr = FileUtil.readFile(pathFull,"UTF-8");
			logger.debug("fileStr: \n"+fileStr);
			logger.debug("Success step3");
			String fileStrArray[] = fileStr.split("\\;");
			for(i=0;i<fileStrArray.length;i++){
				String sqlStatement = fileStrArray[i];
				if( !Utils.isNull(sqlStatement).equals("")){
				   logger.debug("sqlStatement["+i+"]:"+sqlStatement+" \n result:"+st.executeUpdate(sqlStatement));
				}
			}
			logger.debug("Success step4");
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			try{
			   FileUtil.close(br);
			   DBConnection.close(conn);
			   DBConnection.close(psCommand);
			   DBConnection.close(psStatement);
			   DBConnection.close(st);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return pathFull;
	}
		
}
