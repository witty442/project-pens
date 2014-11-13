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
	private static int maxRetryBackup = 3;
	private static int countRetryBackup = 0;
	
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
	
	/**
	 * Main Process Backup DB();
	 * @param request
	 * @param user
	 * @return
	 */
	public String[] process(HttpServletRequest request,User user ){
		String[] resultPath = new String[2];
		try{
			resultPath = processBackup(request,user);
		}catch(OutOfMemoryError e){
			//countRetryBackup++;
			System.gc();
			//logger.info("countRetryBackup["+countRetryBackup+"]");
			//if(countRetryBackup<=maxRetryBackup){
			//	processBackup(request,user);
			//}
		}catch(Exception ee){
			logger.error(ee.getMessage(),ee);
		}
		return resultPath;
	}
	
	public String[] processOnLocalOnly(HttpServletRequest request,User user ){
		String[] resultPath = new String[2];
		try{
			resultPath = processBackupOnLocalOnly(request,user);
		}catch(OutOfMemoryError e){
			//countRetryBackup++;
			System.gc();
			//logger.info("countRetryBackup["+countRetryBackup+"]");
			//if(countRetryBackup<=maxRetryBackup){
			//	processBackup(request,user);
			//}
		}catch(Exception ee){
			logger.error(ee.getMessage(),ee);
		}
		return resultPath;
	}
	
	private  String[] processBackup(HttpServletRequest request,User user ) throws OutOfMemoryError{
		Connection conn = null;
		EnvProperties env = EnvProperties.getInstance();
		List<List<DBBean>> allList = new ArrayList<List<DBBean>>();
		List<DBBean> tableList = new ArrayList<DBBean>();
		//List<DBBean> tableUnAvaiableList = new ArrayList<DBBean>();
		int i = 0;
		StringBuffer scriptStr = new StringBuffer("");
		BufferedReader br = null;
		String schema = "pens";
		String pathSqlFull = "";
		String pathZipFull = "";
		String sqlFileName = "";
		String zipFileName = "";
		String[] resultPath = new String[2];
		String ftpFilePath = "";
		try{
			logger.info("Start processBackup ");
			
			//ADMIN and DD no BackUp DB
			if( "ADMIN".equalsIgnoreCase(user.getType()) || "DD".equalsIgnoreCase(user.getType())){
				return null;
			}
			
			// get Schema name by select by requestContextPath
			schema = getSchemaName();
			
			//Gen FileName
			sqlFileName = getFileName(schema,user,"sql");
			zipFileName = getFileName(schema,user,"zip");
					
			// get Path + fileName
			pathSqlFull = getLocalPath(request)+sqlFileName;
			pathZipFull = getLocalPath(request)+zipFileName;

			conn = DBConnection.getInstance().getConnection();
			/** Step 1 **/
			allList = listTableBackUp(schema,conn);
			tableList = (List<DBBean>)allList.get(0);
			//tableUnAvaiableList = (List<DBBean>)allList.get(1);
			
			logger.debug("Generate Insert Statement ");
			
            if(tableList != null && tableList.size() >0){
            	// Get Header Mysqldump format 
            	br = FileUtil.getBufferReaderFromClassLoader("mysql_dump_head.txt");
            	String lineStr = null;
    			while ((lineStr = br.readLine()) != null) {
    				scriptStr.append(lineStr+"\n");
    			}
                
    			//Difinitions 
    			scriptStr.append("\n--");
    			scriptStr.append("\n-- Create schema "+s+schema+s+"");
    			scriptStr.append("\n--");
    			
    			// Gen Script create DB 
            	scriptStr.append("\n CREATE DATABASE IF NOT EXISTS "+s+schema+s+"; \n");
            	scriptStr.append("USE "+s+schema+s+";\n");
            	
				for(i=0;i<tableList.size();i++){
					DBBean dbBean = (DBBean)tableList.get(i);
					String tableName = dbBean.getTableName();
                    String tableType = dbBean.getTableType();
					
					if( !"view".equalsIgnoreCase(tableType)){
						scriptStr.append("\n -- ");
						scriptStr.append("\n -- Definition of table "+s+tableName+s+"  ");
						scriptStr.append("\n -- \n");
					}else{
						scriptStr.append("\n -- ");
						scriptStr.append("\n -- Definition of view "+s+tableName+s+"  ");
						scriptStr.append("\n -- \n");
					}
					scriptStr.append(generateCreateScript(schema, conn, dbBean) +"  \n");
					
					
					if( !"view".equalsIgnoreCase(tableType)){
						scriptStr.append("\n -- ");
						scriptStr.append("\n -- Dumping data for table "+s+tableName+s+"  ");
						scriptStr.append("\n -- \n");
						
						scriptStr.append("\n\n /*!40000 ALTER TABLE "+s+tableName+s+" DISABLE KEYS */; \n");
						scriptStr.append(generateInsertScript(schema, conn, tableName) +"  \n");
						scriptStr.append("\n /*!40000 ALTER TABLE "+s+tableName+s+" ENABLE KEYS */; \n");
					}
				}
					
				scriptStr.append("\n/*!40101 SET SQL_MODE=@OLD_SQL_MODE */; ");
				scriptStr.append("\n/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */; ");
				scriptStr.append("\n/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */; ");
				scriptStr.append("\n/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */; ");
				scriptStr.append("\n/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */; ");
				scriptStr.append("\n/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */; ");
				scriptStr.append("\n/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */; ");

				
				logger.debug("Write File to d:/DB_Backup/");
				/** Create file SQl **/
				FileUtil.writeFile(pathSqlFull,scriptStr,"utf-8");
				
				logger.debug("Zip File to d:/DB_Backup/");
				/** Zip Sql File **/
				FileUtil.zipFile(pathSqlFull,pathZipFull,sqlFileName); 
				
				//Move DBbackupFile.zip to FtpServer
				ftpFilePath = getFtpPath(request);
				logger.debug("ftpFilePath:"+ftpFilePath);
				logger.debug("localZipFile:"+pathZipFull);
				logger.debug("zipFileName:"+zipFileName);
				
				
				if(Utils.isNull(user.getType()).equals(User.TT)){
					ftpFilePath += "Credit/"+zipFileName;
				 }else if(Utils.isNull(user.getType()).equals(User.VAN)){
					 ftpFilePath += "Van/"+zipFileName;
				 }
				
				logger.debug("Upload Zip File To FTP Server");
				FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
				ftpManager.uploadBackUpDBZipFileToFTP_OPT3(user, ftpFilePath, pathZipFull);
			}

            /*if(tableUnAvaiableList != null && tableUnAvaiableList.size() > 0){
            	for(int n=0;n<tableUnAvaiableList.size();n++){
            		logger.debug("table UnAvaiable :"+((DBBean)tableUnAvaiableList.get(n)).getTableName());
            	}
            }*/
            
            logger.debug("Delete Zip File IN d:/DB_Backup/");
            /** Delete Sql Temp File **/
			FileUtil.deleteFile(pathSqlFull);
			
			resultPath[0] = pathZipFull;//local
			resultPath[1] = ftpFilePath;//remote FTP server
			
			logger.info("--End processBackup-- ");
			
		}catch(OutOfMemoryError e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			
		}finally{
			try{
			   allList = null;
			   tableList = null;
			   //tableUnAvaiableList = null;
			   scriptStr = null;
			   FileUtil.close(br);
			   DBConnection.close(conn);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return resultPath;
	}
	

	private  String[] processBackupOnLocalOnly(HttpServletRequest request,User user ) throws OutOfMemoryError{
		Connection conn = null;
		EnvProperties env = EnvProperties.getInstance();
		List<List<DBBean>> allList = new ArrayList<List<DBBean>>();
		List<DBBean> tableList = new ArrayList<DBBean>();
		//List<DBBean> tableUnAvaiableList = new ArrayList<DBBean>();
		int i = 0;
		StringBuffer scriptStr = new StringBuffer("");
		BufferedReader br = null;
		String schema = "pens";
		String pathSqlFull = "";
		String pathZipFull = "";
		String sqlFileName = "";
		String zipFileName = "";
		String[] resultPath = new String[2];
		String ftpFilePath = "";
		try{
			logger.info("Start processBackup ");
			
			//ADMIN and DD no BackUp DB
			if( "ADMIN".equalsIgnoreCase(user.getType()) || "DD".equalsIgnoreCase(user.getType())){
				return null;
			}
			
			// get Schema name by select by requestContextPath
			schema = getSchemaName();
			
			//Gen FileName
			sqlFileName = getFileName(schema,user,"sql");
			zipFileName = getFileName(schema,user,"zip");
					
			// get Path + fileName
			pathSqlFull = getLocalPath(request)+sqlFileName;
			pathZipFull = getLocalPath(request)+zipFileName;

			conn = DBConnection.getInstance().getConnection();
			/** Step 1 **/
			allList = listTableBackUp(schema,conn);
			tableList = (List<DBBean>)allList.get(0);
			//tableUnAvaiableList = (List<DBBean>)allList.get(1);
			
			logger.debug("Generate Insert Statement ");
			
            if(tableList != null && tableList.size() >0){
            	// Get Header Mysqldump format 
            	br = FileUtil.getBufferReaderFromClassLoader("mysql_dump_head.txt");
            	String lineStr = null;
    			while ((lineStr = br.readLine()) != null) {
    				scriptStr.append(lineStr+"\n");
    			}
                
    			//Difinitions 
    			scriptStr.append("\n--");
    			scriptStr.append("\n-- Create schema "+s+schema+s+"");
    			scriptStr.append("\n--");
    			
    			// Gen Script create DB 
            	scriptStr.append("\n CREATE DATABASE IF NOT EXISTS "+s+schema+s+"; \n");
            	scriptStr.append("USE "+s+schema+s+";\n");
            	
				for(i=0;i<tableList.size();i++){
					DBBean dbBean = (DBBean)tableList.get(i);
					String tableName = dbBean.getTableName();
                    String tableType = dbBean.getTableType();
					
					if( !"view".equalsIgnoreCase(tableType)){
						scriptStr.append("\n -- ");
						scriptStr.append("\n -- Definition of table "+s+tableName+s+"  ");
						scriptStr.append("\n -- \n");
					}else{
						scriptStr.append("\n -- ");
						scriptStr.append("\n -- Definition of view "+s+tableName+s+"  ");
						scriptStr.append("\n -- \n");
					}
					scriptStr.append(generateCreateScript(schema, conn, dbBean) +"  \n");
					
					
					if( !"view".equalsIgnoreCase(tableType)){
						scriptStr.append("\n -- ");
						scriptStr.append("\n -- Dumping data for table "+s+tableName+s+"  ");
						scriptStr.append("\n -- \n");
						
						scriptStr.append("\n\n /*!40000 ALTER TABLE "+s+tableName+s+" DISABLE KEYS */; \n");
						scriptStr.append(generateInsertScript(schema, conn, tableName) +"  \n");
						scriptStr.append("\n /*!40000 ALTER TABLE "+s+tableName+s+" ENABLE KEYS */; \n");
					}
				}
					
				scriptStr.append("\n/*!40101 SET SQL_MODE=@OLD_SQL_MODE */; ");
				scriptStr.append("\n/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */; ");
				scriptStr.append("\n/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */; ");
				scriptStr.append("\n/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */; ");
				scriptStr.append("\n/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */; ");
				scriptStr.append("\n/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */; ");
				scriptStr.append("\n/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */; ");

				
				logger.debug("Write File to d:/DB_Backup/");
				/** Create file SQl **/
				FileUtil.writeFile(pathSqlFull,scriptStr,"utf-8");
				
				logger.debug("Zip File to d:/DB_Backup/");
				/** Zip Sql File **/
				FileUtil.zipFile(pathSqlFull,pathZipFull,sqlFileName); 

			}

            /*if(tableUnAvaiableList != null && tableUnAvaiableList.size() > 0){
            	for(int n=0;n<tableUnAvaiableList.size();n++){
            		logger.debug("table UnAvaiable :"+((DBBean)tableUnAvaiableList.get(n)).getTableName());
            	}
            }*/
            
            logger.debug("Delete Zip File IN d:/DB_Backup/");
            /** Delete Sql Temp File **/
			FileUtil.deleteFile(pathSqlFull);
			
			resultPath[0] = pathZipFull;//local
			resultPath[1] = "LOCAL ONLY";//remote FTP server
			
			logger.info("--End processBackup-- ");
			
		}catch(OutOfMemoryError e){
			logger.error(e.getMessage(), e);
			throw e;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			
		}finally{
			try{
			   allList = null;
			   tableList = null;
			   //tableUnAvaiableList = null;
			   scriptStr = null;
			   FileUtil.close(br);
			   DBConnection.close(conn);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return resultPath;
	}
	
	private  String getFileName(String schema,User user,String typeFile){
		String fileName= "";
		try{
			fileName = Utils.format(new Date(), "yyyyMMddHHmm")+"_"+user.getUserName()+"."+typeFile;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return fileName;
	}
	
	private  String getLocalPath(HttpServletRequest request){
		String path = "D:/DB_Backup/";
		try{
			File directory = new File(path);
			if(!directory.exists()){
				FileUtils.forceMkdir(directory);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			try{
				path = "C:/DB_Backup/";
				File directory = new File(path);
				if(!directory.exists()){
					FileUtils.forceMkdir(directory);
				}
			}catch(Exception ee){
				logger.error(ee.getMessage(),ee);
			}
		}
		return path;
	}
	
	private  String getFtpPath(HttpServletRequest request){
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
	
	private  String getSchemaName(){
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
	
	private  List<List<DBBean>> listTableBackUp(String schema,Connection conn) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<List<DBBean>> allList = new ArrayList<List<DBBean>>();
		List<DBBean> tableList = new ArrayList<DBBean>();
		List<DBBean> tableUnAvaiableList = new ArrayList<DBBean>();
		logger.info("listTableBackUp");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT table_name, table_type, engine \n");
			sql.append("  FROM information_schema.tables \n");
			sql.append("  WHERE table_schema = '"+schema+"' \n");
			sql.append("  and engine ='InnoDB' \n");
			
			sql.append(" union all ");
			
			sql.append("  SELECT table_name, table_type, engine \n");
			sql.append("  FROM information_schema.tables \n");
			sql.append("  WHERE table_schema = '"+schema+"' \n");
			sql.append("  and table_name ='m_sales_target_new_v' \n");
			
			sql.append("  ORDER BY table_name asc \n");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				String tableName = Utils.isNull(rs.getString("table_name"));
				String tableType = Utils.isNull(rs.getString("table_type"));
				DBBean dbBean = new DBBean();
				dbBean.setTableName(tableName);
				dbBean.setTableType(tableType);
				
				if(isTableAvaiable(schema, conn, tableName ,tableType) ){
				   tableList.add(dbBean);
				}else{
				   tableUnAvaiableList.add(dbBean);
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
	
	private  boolean isTableAvaiable(String schema,Connection conn,String tableName,String type) {
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean r = true;
		//logger.debug("isTableAvaiable:"+tableName);
		try{
			StringBuffer sql = new StringBuffer("");
			if("view".equalsIgnoreCase(type)){
			  sql.append(" show create view  "+s+tableName+s+" \n");
			}else{
			  sql.append(" show create table  "+s+tableName+s+" \n");
			}
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
	
	private  String generateCreateScript(String schema,Connection conn,DBBean dbBean) {
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer createTableScript = new StringBuffer("");
		//logger.debug("createScript:"+dbBean.getTableName());
		try{
			StringBuffer sql = new StringBuffer("");
			if("view".equalsIgnoreCase(dbBean.getTableType())){
			   sql.append(" show create view  "+s+dbBean.getTableName()+s+" \n");
			}else{
			   sql.append(" show create table  "+s+dbBean.getTableName()+s+" \n");
			}
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				
				if("view".equalsIgnoreCase(dbBean.getTableType())){
					createTableScript.append(" DROP VIEW IF EXISTS "+s+dbBean.getTableName()+s+";\n");
					createTableScript.append( Utils.isNull(rs.getString("Create View"))+";");
				}else{
					createTableScript.append(" DROP TABLE IF EXISTS "+s+dbBean.getTableName()+s+";\n");
					createTableScript.append( Utils.isNull(rs.getString("Create Table"))+";");
				}
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.close(rs);
			DBConnection.close(ps);
		}
		return createTableScript.toString();
	}
	

	/*private static String generateViewCreateScript(String schema,Connection conn,DBBean dbBean) {
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer createTableScript = new StringBuffer("");
		logger.debug("createScript:"+dbBean.getTableName());
		try{
			StringBuffer sql = new StringBuffer("");
		    sql.append(" SHOW COLUMNS FROM "+schema+"."+s+dbBean.getTableName()+s+" \n");
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
		
			createTableScript.append(" create view "+s+dbBean.getTableName()+s+"(\n");
			
			while(rs.next()){
				createTableScript.append(s+Utils.isNull(rs.getString("Field"))+s+" "+Utils.isNull(rs.getString("Type"))+" \n ,");
			}
			if(createTableScript != null && createTableScript.length() >0) {
				createTableScript = new StringBuffer(createTableScript.substring(0, createTableScript.length()-1));
			}
			createTableScript.append(" )\n");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.close(rs);
			DBConnection.close(ps);
		}
		return createTableScript.toString();
	}
	*/
	private  StringBuffer generateInsertScript(String schema,Connection conn,String tableName){
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
			
			//sqlInsert.append("insert into "+s+tableName+s+"("+columns+") values  \n");
			int maxStatement = 180;
			int countSt = 0;
			while(rsSelect.next()){
				countSt++;
				foundRecord = true;
				//sqlInsert.append("(");
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
						
						//0000-00-00 00:00:00
						//2014-02-17 22:17:35.0
						try{
							if(rsSelect.getTimestamp(config.getField()) != null ){
								values += "'"+rsSelect.getString(config.getField())+"',";
								//values +="STR_TO_DATE('"+Utils.stringValue(rsSelect.getTimestamp(config.getField()),Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH )+"','%d%m%Y %H%i%S'),";
							}else{
							   values += "NULL,";
							}
						}catch(Exception e){
							logger.debug("error timestamp:"+e.getMessage());
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
				
				if(countSt==1){
					sqlInsert.append("insert into "+s+tableName+s+"("+columns+") values  \n");
				}
	
				if(countSt < maxStatement){
				    //add values
				    sqlInsert.append("("+values+")\n ,");
				}else if(countSt >=maxStatement){
				   //add values
				    sqlInsert.append("("+values+") ; \n");
					countSt = 0;
				}
				
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
	private  String replaceSingleQuote(String str){
		try{
			str = str.replaceAll("'", "\\\\'");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return str;
	}
	
}
