package com.isecinc.pens.db.backup;

import java.io.BufferedReader;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

/**
 * @author WITTY
 *
 */
public class DBBackUpManager {
   
	protected static  Logger logger = Logger.getLogger("PENS");
	private static String s ="`";
	
	private static String getFileName(String schema,User user,String typeFile){
		String fileName= "";
		try{
			fileName = schema+"_"+user.getUserName()+"_"+Utils.format(new Date(), "yyyyMMddHHmm")+"."+typeFile;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return fileName;
	}
	
	private static String getLocalPath(HttpServletRequest request){
		String path = "D://DB_Backup//";
		try{
			File directory = new File(path);
			if(!directory.exists()){
				FileUtils.forceMkdir(directory);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return path;
	}
	
	private static String getFtpPath(HttpServletRequest request){
		String path = "/DB_Backup/"; //PROD Default
		try{
			logger.debug("contextPath:"+request.getLocalAddr());
	        if("/penstest".equalsIgnoreCase(request.getContextPath())){
	        	//UAT
				path = "/DB_Backup/";
	        }else if("127.0.0.1".equalsIgnoreCase(request.getLocalAddr())){
	        	//UAT
				path = "/DB_Backup/";
			}else{
				//PROD
				path = "/DB_Backup/";
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return path;
	}
	
	private static String getSchemaName(){
		String schema = "pens";
		try{
			// Get Schema From Connection URL 
			// jdbc:mysql://localhost:3306/pens?useUnicode=true&amp;characterEncoding=UTF-8
			Configuration hibernateConfig = new Configuration();
			hibernateConfig.configure();
			String url = hibernateConfig.getProperty("connection.url");
			schema = url.substring(url.lastIndexOf("/")+1,url.indexOf("?"));
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return schema;
	}
	
	public static String processToLocal(HttpServletRequest request,User user ){
		Connection conn = null;
		List<List> allList = new ArrayList<List>();
		List<String> tableList = new ArrayList<String>();
		List<String> tableUnAvaiableList = new ArrayList<String>();
		int i = 0;
		StringBuffer scriptStr = new StringBuffer("");
		BufferedReader br = null;
		String schema = "pens";
		String pathSqlFull = "";
		String pathZipFull = "";
		try{
			//ADMIN and DD no BackUp DB
			if( "ADMIN".equalsIgnoreCase(user.getType()) || "DD".equalsIgnoreCase(user.getType())){
				return null;
			}
			
			// get Schema name by select by requestContextPath
			schema = getSchemaName();
			
			// get Path + fileName
			pathSqlFull = getLocalPath(request)+getFileName(schema,user,"sql");
			pathZipFull = getLocalPath(request)+getFileName(schema,user,"zip");

			conn = DBConnection.getInstance().getConnection();
			/** Step 1 **/
			allList = listTableBackUp(schema,conn);
			tableList = (List)allList.get(0);
			tableUnAvaiableList = (List)allList.get(1);
			
            if(tableList != null && tableList.size() >0){
            	// Get Header Mysqldump format 
            	br = FileUtil.getBufferReaderFromClassLoader("mysql_dump_head.txt");
            	String lineStr = null;
    			while ((lineStr = br.readLine()) != null) {
    				scriptStr.append(lineStr+"\n");
    			}
                
    			// Gen Script create DB 
            	scriptStr.append("\n CREATE DATABASE IF NOT EXISTS "+s+schema+s+"; \n");
            	scriptStr.append(" USE "+s+schema+s+";\n");
            	
				for(i=0;i<tableList.size();i++){
					String tableName = Utils.isNull(tableList.get(i));
					scriptStr.append("\n -- **** Script Create Table :"+tableName+" ***********--  \n");
					scriptStr.append(generateCreateScript(schema, conn, tableName) +"  \n");
					scriptStr.append("\n -- **** Script Insert Table:"+tableName+" ************--  \n");
					scriptStr.append(generateInsertScript(schema, conn, tableName) +"  \n");
				}
				
				logger.debug("Write File");
				/** Create file SQl **/
				FileUtil.writeFile(pathSqlFull,scriptStr.toString(),"utf-8");
				/** Zip Sql File **/
				FileUtil.zipFile(pathSqlFull, pathZipFull,getFileName(schema,user,"sql"));
				
				/** Delete Sql File **/
				FileUtil.deleteFile(pathSqlFull);
			}

            
            if(tableUnAvaiableList != null && tableUnAvaiableList.size() > 0){
            	for(int n=0;n<tableUnAvaiableList.size();n++){
            		logger.debug("table UnAvaiable :"+(String)tableUnAvaiableList.get(n));
            	}
            }
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			try{
			   FileUtil.close(br);
			   DBConnection.close(conn);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return pathSqlFull;
	}
	
	
	public static String processToFTPServer(HttpServletRequest request,User user){
		Connection conn = null;
		List<List> allList = new ArrayList<List>();
		List<String> tableList = new ArrayList<String>();
		List<String> tableUnAvaiableList = new ArrayList<String>();
		int i = 0;
		StringBuffer scriptStr = new StringBuffer("");
		BufferedReader br = null;
		String schema = "pens";
		String pathFtp = "";
		String pathLocal = "";
		String fileName = "";
		EnvProperties env = EnvProperties.getInstance();
		try{
			//ADMIN and DD no BackUp DB
			if( "ADMIN".equalsIgnoreCase(user.getType()) || "DD".equalsIgnoreCase(user.getType())){
				return null;
			}
			
			// get Schema name by select by requestContextPath
			schema = getSchemaName();
			fileName = getFileName(schema,user,"txt");
			
			// gen FTP Path + fileName
			pathFtp = getFtpPath(request);
			// get Local Path + fileName
			pathLocal = getLocalPath(request);
			
			conn = DBConnection.getInstance().getConnection();
			/** Step 1 **/
			allList = listTableBackUp(schema,conn);
			tableList = (List)allList.get(0);
			tableUnAvaiableList = (List)allList.get(1);
			
            if(tableList != null && tableList.size() >0){
            	// Get Header Mysqldump format 
            	br = FileUtil.getBufferReaderFromClassLoader("mysql_dump_head.txt");
            	String lineStr = null;
    			while ((lineStr = br.readLine()) != null) {
    				scriptStr.append(lineStr+"\n");
    			}
                
    			// Gen Script create DB 
            	scriptStr.append("\n CREATE DATABASE IF NOT EXISTS "+s+schema+s+"; \n");
            	scriptStr.append(" USE "+s+schema+s+";\n");
            	
				for(i=0;i<tableList.size();i++){
					String tableName = Utils.isNull(tableList.get(i));
					scriptStr.append("\n -- **** Script Create Table :"+tableName+" ***********--  \n");
					scriptStr.append(generateCreateScript(schema, conn, tableName) +"  \n");
					scriptStr.append("\n -- **** Script Insert Table:"+tableName+" ************--  \n");
					scriptStr.append(generateInsertScript(schema, conn, tableName) +"  \n");
				}
				
				
				//String tempPathFile = pathLocal+"temp.sql";
				//logger.debug("Write File To Local : "+tempPathFile);
				//FileUtil.writeFile(tempPathFile,scriptStr.toString(),"utf-8");
				
				//FileUtil.zipFile(tempPathFile,pathLocal+"temp.zip");
				//FileUtil.compress(tempPathFile,pathLocal+"temp.zip");
				//logger.debug("Zip To Local :"+pathLocal+"temp.zip");
				
				FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
				ftpManager.exportBackUpDBFileToFTP(user,pathFtp,fileName,scriptStr);
				
				//FTPManager ftpManager2 = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
				//ftpManager2.exportBackUpDBZipFileToFTP(user,pathFtp,fileName,pathLocal+"temp.zip");
			}

            
            if(tableUnAvaiableList != null && tableUnAvaiableList.size() > 0){
            	for(int n=0;n<tableUnAvaiableList.size();n++){
            		logger.debug("table UnAvaiable :"+(String)tableUnAvaiableList.get(n));
            	}
            }
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			try{
			   FileUtil.close(br);
			   DBConnection.close(conn);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			scriptStr = null;
		}
		return "";
	}
	
	private static List<List> listTableBackUp(String schema,Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<List> allList = new ArrayList<List>();
		List<String> tableList = new ArrayList<String>();
		List<String> tableUnAvaiableList = new ArrayList<String>();
		logger.debug("listTableBackUp");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT table_name, table_type, engine \n");
			sql.append("  FROM information_schema.tables \n");
			sql.append("  WHERE table_schema = '"+schema+"' \n");
			//test
			//sql.append("  AND table_name ='ad_user' \n");
			sql.append("  ORDER BY table_name asc \n");
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				String tableName = Utils.isNull(rs.getString("table_name"));
				if(isTableAvaiable(schema, conn, tableName)){
				   tableList.add(tableName);
				}else{
				   tableUnAvaiableList.add(tableName);
				}
			}
			
			allList.add(tableList);
			allList.add(tableUnAvaiableList);
			
			return allList;
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.close(rs);
			DBConnection.close(ps);
			
		}
	}
	
	private static boolean isTableAvaiable(String schema,Connection conn,String tableName) {
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean r = true;
		logger.debug("isTableAvaiable:"+tableName);
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" show create table  "+s+tableName+s+" \n");
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				r = true;
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			r = false;
		}finally{
			DBConnection.close(rs);
			DBConnection.close(ps);
		}
		return r;
	}
	
	private static String generateCreateScript(String schema,Connection conn,String tableName) {
		PreparedStatement ps =null;
		ResultSet rs = null;
		String createTableScript = "";
		logger.debug("createScript:"+tableName);
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" show create table  "+s+tableName+s+" \n");
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				createTableScript ="DROP TABLE IF EXISTS "+s+tableName+s+";\n";
				createTableScript += Utils.isNull(rs.getString("Create Table"))+";";
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.close(rs);
			DBConnection.close(ps);
		}
		return createTableScript;
	}
	
	private static StringBuffer generateInsertScript(String schema,Connection conn,String tableName){
		logger.debug("generateInsertScript:"+tableName);
		PreparedStatement ps =null;
		ResultSet rs = null;
		PreparedStatement psSelect =null;
		ResultSet rsSelect = null;
		StringBuffer sqlInsert = new StringBuffer("");
		StringBuffer sql = new StringBuffer("");
	    String columns = "";
	    String values ="";
	    List<DBBackUpConfig> configList = new ArrayList<DBBackUpConfig>();
	    boolean foundRecord = false;
	    int i = 0;
		try{
			sql.append(" DESCRIBE "+s+tableName+s+" \n");
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				//field,type,NULL(no,yes),key(pri,mul),default 
				DBBackUpConfig config = new DBBackUpConfig();
				config.setField(Utils.isNull(rs.getString("field")));
				config.setType(Utils.isNull(rs.getString("type")));
				config.setNulls(Utils.isNull(rs.getString("Null")).equalsIgnoreCase("YES")?true:false);
				config.setDefaults(Utils.isNull(rs.getString("default")));
				configList.add(config);
				columns += s+config.getField()+s+",";
				
				//logger.debug("field:"+config.getField()+",Type:"+config.getType());
			}
			
			/** Replace comma last **/
			if(columns != null && columns.length() >0){
				columns =columns.substring(0,columns.length()-1);
			}
			
			sql = new StringBuffer(" select * from  "+schema+"."+tableName+" \n");
			psSelect = conn.prepareStatement(sql.toString());
			rsSelect = psSelect.executeQuery();
			
			sqlInsert.append("insert into "+s+tableName+s+"("+columns+") values  \n");
			
			while(rsSelect.next()){
				foundRecord = true;
				sqlInsert.append("(");
				for(i = 0;i<configList.size();i++){
					DBBackUpConfig config = (DBBackUpConfig)configList.get(i);
					
					if(config.getType().startsWith(("char"))){
						values +="'"+Utils.isNull(rsSelect.getString(config.getField()))+"',";
					}else if(config.getType().startsWith(("varchar"))){
						if(config.getField().equalsIgnoreCase("error_msg")){
							//\'3003\';
						  values +="'"+replaceSingleQuote(Utils.isNull(rsSelect.getString(config.getField())))+"',";
						}else{
						   values +="'"+replaceSingleQuote(Utils.isNull(rsSelect.getString(config.getField())))+"',";
						}
					}else if(config.getType().startsWith(("date"))){
						if(rsSelect.getDate(config.getField()) != null){
							values += "'"+rsSelect.getString(config.getField())+"',";
						   //values +="STR_TO_DATE('"+Utils.stringValue(rsSelect.getDate(config.getField()),Utils.DD_MM_YYYY_WITHOUT_SLASH )+"','%d%m%Y'),";
						}else{
						   values += "NULL,";
						}
					}else if(config.getType().startsWith(("timestamp"))){
						if(rsSelect.getTimestamp(config.getField()) != null){
							values += "'"+rsSelect.getString(config.getField())+"',";
							//values +="STR_TO_DATE('"+Utils.stringValue(rsSelect.getTimestamp(config.getField()),Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH )+"','%d%m%Y %H%i%S'),";
						}else{
						   values += "NULL,";
						}
					}else if(config.getType().startsWith(("int"))){
						values +=rsSelect.getInt(config.getField())+",";
					}else if(config.getType().startsWith(("bigint"))){
						values +=rsSelect.getInt(config.getField())+",";
					}else if(config.getType().startsWith(("decimal"))){
						values += rsSelect.getDouble(config.getField())+",";
				    }else{
				    	values +="'"+Utils.isNull(rsSelect.getString(config.getField()))+"',";
				    }
				}//for
				
				if(values != null && values.length() >0){
					values = values.substring(0,values.length()-1);
				}
				//add values
				sqlInsert.append(values+")\n ,");
				
                //initail
				values ="";
			}//while
			
			
			if(foundRecord){
				if(sqlInsert != null && sqlInsert.length() > 0){
					sqlInsert = sqlInsert.replace(sqlInsert.length()-1, sqlInsert.length(), "").append(";");
				}
			}else{
				sqlInsert = new StringBuffer("");
			}
			//logger.debug(sqlInsert);

		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.close(rs);
			DBConnection.close(ps);
			DBConnection.close(rsSelect);
			DBConnection.close(psSelect);
		}
		return sqlInsert;
	}
	
	/**
	 * 
	 * @param str
	 * @return  'xxx'  -> \'xxx\'
	 */
	private static String replaceSingleQuote(String str){
		try{
			str = str.replaceAll("'", "\\\\'");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return str;
	}
	
	public static void main(String[] a){
		try{
			String r = "p'เพื่อนเกษตร";
			String c = "\\\\";
			String rr = r.replaceAll("'", "\\\\'");
			System.out.println(rr);
			rr = r.replaceAll(s, "\\\\'");
			System.out.println(rr);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
