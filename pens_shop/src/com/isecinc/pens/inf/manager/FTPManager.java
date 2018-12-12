package com.isecinc.pens.inf.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import util.ControlCode;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.ImageFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.FTPException;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.ExportHelper;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.ImportHelper;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.process.bean.FileImportTransBean;
import com.isecinc.pens.inf.manager.process.bean.KeyNoImportTransBean;

/**
 * @author WITTY
 *
 */
public class FTPManager {

	
	private static Logger logger = Logger.getLogger("PENS"); 
	
	public String server;
	public String userFtp;
	public String passwordFtp;
	EnvProperties env = EnvProperties.getInstance();
	
	public FTPManager(String server,String userFtp,String passwordFtp){
		this.server = server;
		this.userFtp = userFtp;
		this.passwordFtp = passwordFtp;
		
		//verify server
		//Case 1  server 1 no active -> server 2
		//Case 2  server 2 no active -> server 1 (default)
		
		//chooseFtpServer();
	}
	
	public void chooseFtpServer() {
		//test main server
		boolean canServer1Active = isServerActive(server);
		logger.debug("server1["+server+"]:canServer1Active["+canServer1Active+"]");
		
		if( !canServer1Active){
		   this.server = env.getProperty("ftp.ip.server.temp");
		   
		   boolean canServer2Active = isServerActive(server);
		   logger.debug("server2["+server+"]:canServer2Active["+canServer2Active+"]");
		   
		   /** case server temp not active  default = server1 **/
		   if( !canServer2Active){
			   this.server = env.getProperty("ftp.ip.server");
		   }
		   
		}
		
		logger.debug("After process default server["+server+"]");
	}
	
	private boolean isServerActive(String serverTest) {
		boolean can = false;
		FTPClient ftp = null;
		int replyCode = 0;
		try{
			ftp = new FTPClient();
			ftp.connect(serverTest);
			replyCode = ftp.getReplyCode();
			logger.debug("["+server+"]:replayCode["+replyCode+"]");
		   // if(FTPClient.)
			can = true;
		}catch(Exception e){
			e.printStackTrace();
			can = false;
		} finally {
			try{
				if(ftp != null && ftp.isConnected()) {
					// Logout from the FTP Server and disconnect
					ftp.logout();
					ftp.disconnect();
					logger.info("ftp disconnect : "+ftp.getReplyString());
					ftp = null;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return can;
	}
	
	public boolean testConnectionURL() {
	   boolean re = true;
	   FTPClient ftp = null;
    	try{
			ftp = new FTPClient();
			//ftp.connect(server);
			ftp.isConnected();
		}catch(Exception e){
			re = false;
		} finally {
			try{
				if(ftp != null && ftp.isConnected()) {
					// Logout from the FTP Server and disconnect
					ftp.logout();
					ftp.disconnect();
					logger.info("ftp disconnect : "+ftp.getReplyString());
					ftp = null;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	    return re;
	}
	
	
	
	/**
	 * canConnectFTPServer
	 * @return true if can connection
	 */
	public void canConnectFTPServer() throws Exception{
		FTPClient ftp = null;
		String replyDesc = "";
		try{
			logger.debug("["+server+"]:"+replyDesc);
			
			ftp = new FTPClient();
			//ftp.setDefaultPort(FTPClient.DEFAULT_PORT);
			//ftp.setDefaultPort(FTPClient.DEFAULT_PORT);
			ftp.connect(server);
			replyDesc = ftp.getReplyString();
			logger.debug("["+server+"]:["+replyDesc+"]");
		}catch(Exception e){
			e.printStackTrace();
			throw new FTPException("Could not connect to FTP server :"+replyDesc);
		} finally {
			try{
				if(ftp != null && ftp.isConnected()) {
					// Logout from the FTP Server and disconnect
					ftp.logout();
					ftp.disconnect();
					logger.info("ftp disconnect : "+ftp.getReplyString());
					ftp = null;
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 
	 * @param controlTableMap
	 * @param path
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public int downloadFileMappingToTable(User user,LinkedHashMap<String,TableBean> controlTableMap ,String path,User userBean,String transType,boolean importAll) throws Exception
	{
		FTPClient ftp = null;
		String ftpResponse = "";
		int i = 0;
		int countFileMap = 0;
		try {			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
			ftp.changeWorkingDirectory(path);
			FTPFile[] ftpFiles = ftp.listFiles(path);
			ftpResponse = ftp.getReplyString();

			if (ftpFiles != null && ftpResponse.startsWith("226")) {
				logger.debug("Start init FTP File ");
				
				List<FTPFileBean> outStreamList = null;
                for(i=0;i<ftpFiles.length;i++){
                	FTPFile file = (FTPFile)ftpFiles[i];
                	//logger.debug("FTP fileName:"+file.getName());
                	
                	Set s = controlTableMap.keySet();
    				Iterator it = s.iterator();
    				boolean canGetFtpFile = false;
    				for (int j = 1; it.hasNext(); j++) {
    					canGetFtpFile = false; 
    					String tableName = (String) it.next();
    					TableBean tableBean = (TableBean) controlTableMap.get(tableName);
                        String ftpFileName = Utils.isNull(file.getName());
                        
    					if(!file.getName().equals(".") && !file.getName().equals("..")){
    					   String typeName = "";
    					   if( !ftpFileName.equals("") && ftpFileName.indexOf(".") != -1){
    						   typeName = ftpFileName.substring(ftpFileName.indexOf(".")+1,ftpFileName.length());
    					   }
    					   if(typeName.equalsIgnoreCase("txt")){
    					       canGetFtpFile = ImportHelper.canGetFtpFile(userBean, transType, tableBean, ftpFileName,importAll);
    					   }
    					}
    					//logger.info("Table["+tableName+"]fileFtpname["+ftpFileName+"]canGetFtpFile["+canGetFtpFile+"]");
    					
    					if(canGetFtpFile==true){
    						
    						countFileMap++;
	                		/** Set Full File Name **/
	                		tableBean.setFileFtpNameFull(ftpFileName);
	                		//logger.debug("canGetFtpFile No["+countFileMap+"]put FtpName:"+tableBean.getFileFtpNameFull());
		                	//ByteArrayOutputStream out = new ByteArrayOutputStream();
		                    
		                	/** 1 Write file to Path */
		                	//ftp.retrieveFile(file.getName(),out);
		                	//String dataStreamStr =  out.toString();
		                	
		                	/** 2 **/
		                	String dataStreamStr = convertStreamToString(ftp.retrieveFileStream(ftpFileName),tableName,userBean);
		                	ftp.completePendingCommand();
		                	//logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);
		                	
		                	/** 3 **/
		                	/*InputStream in =ftp.retrieveFileStream(file.getName());
		                	ftp.completePendingCommand();
		                	String dataStreamStr = "";
		                	int c ;
		                	 byte[] bufferByte = new byte[1024];
		                	while((c=in.read()) != -1){
		                		dataStreamStr +=Character.valueOf((char)c);
		                	}*/
		                	//in.close();
		                	//in.reset();
		                	
		                	
		                	//logger.debug("dataStreamStrXX:"+dataStreamStr);
		                	
		                    /** Store DataStream in TableBean **/

		                	String[] dataLineTextArr =  new String(dataStreamStr).trim().split(Constants.newLine);
		                	/** Case one table have more one file **/
		                	if(tableBean.getDataLineList() ==null){
		                		outStreamList = new ArrayList<FTPFileBean>();
		                		FTPFileBean ftpBean = new FTPFileBean();
		                		ftpBean.setFileName(file.getName());
		                		ftpBean.setDataLineText(dataLineTextArr);
		                		ftpBean.setFileSize(FileUtils.byteCountToDisplaySize(file.getSize()));
		                		ftpBean.setFileCount(Utils.isNull(dataStreamStr).equals("")?0:dataLineTextArr.length);
		                		outStreamList.add(ftpBean);
		                		tableBean.setDataLineList(outStreamList);
		                	}else{
		                		FTPFileBean ftpBean = new FTPFileBean();
		                		ftpBean.setFileName(file.getName());
		                		ftpBean.setDataLineText(dataLineTextArr);
		                		ftpBean.setFileSize(FileUtils.byteCountToDisplaySize(file.getSize()));
		                		ftpBean.setFileCount(Utils.isNull(dataStreamStr).equals("")?0:dataLineTextArr.length);
		                		tableBean.getDataLineList().add(ftpBean);
		                	}
		                	/** Case Get Ftp File Master ,Product,C4 get Latest One File only **/
		                	/** Exm 201308051515-UOMCVS.txt **/
		                	if(("Y").equals(tableBean.getCheckDupFile())){
		                		//logger.info("GetFileFtp CheckDupFile["+tableBean.getCheckDupFile()+"] tabelName["+tableBean.getTableName()+"]");
		                		if(tableBean.getDataLineList() != null && tableBean.getDataLineList().size()>1){
		                			long fileNameLongLatest = 0;
		                			long fileNameLongTemp = 0;
		                			FTPFileBean ftpBeanLastest = null;
		                			
		                			for(int n=0;n<tableBean.getDataLineList().size();n++){
		                				FTPFileBean ftpBean = (FTPFileBean)tableBean.getDataLineList().get(n);
		                				String fileNameLongStr = ftpBean.getFileName().substring(0,ftpBean.getFileName().indexOf("-")-1);
		                				
		                				fileNameLongTemp = Long.parseLong(fileNameLongStr);
		                				//logger.info("fileNameLongStr["+fileNameLongStr+"]fileNameLongTemp["+fileNameLongTemp+"]");
		                				if(n==0){
		                					fileNameLongLatest = fileNameLongTemp;
		                					ftpBeanLastest = ftpBean;
		                				}else{
		                					if(fileNameLongTemp > fileNameLongLatest){
		                						fileNameLongLatest = fileNameLongTemp;
		                						ftpBeanLastest = ftpBean;
		                					}
		                				}
		                			}
		                			
		                			//set On file to dataLineList
		                			List<FTPFileBean> fileBeanList = new ArrayList<FTPFileBean>();
		                			fileBeanList.add(ftpBeanLastest);
		                			
		                			tableBean.setDataLineList(fileBeanList);
		                		}
		                	}
		                	
		                	/** put to Map Main **/
		                	controlTableMap.put(tableBean.getTableName(), tableBean);
		                	
		                	//out.flush();
		                	//out.close();
	                	}//if
    				}//for 2
                }//for 1
			}
			
			logger.debug("End init FTP File Total Add To Map:"+countFileMap);

			return countFileMap;
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null && ftp.isConnected()) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}

	public void verifyFileInFTPServer(User userBean,LinkedHashMap<String,TableBean> controlTableMap,String path) throws Exception{
		try {		
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			while(it.hasNext()) {
				String fileControlName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(fileControlName);
				logger.debug("export image fileControlName:"+fileControlName); 
				
				compareFileLocalVsServer(tableBean);	
			
			}//for		
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			
		}	
	}
	public String  compareFileLocalVsServer(TableBean tableBean) throws Exception{
		FTPClient ftp = null;
		String dataStreamStr = "";
		try {			
			ftp = new FTPClient();
			ftp.connect(server);
			//ftp.enterLocalPassiveMode();
			//ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			//ftp.setFileType(FTPClient.);
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			logger.debug("pathGetFileFull:"+tableBean.getExportPath());
            logger.debug("fileName:"+tableBean.getFileFtpNameFull()+"fileSize:");
					
            ftp.sendCommand("SIZE", tableBean.getExportPath()+"\\"+tableBean.getFileFtpNameFull());
			String reply = ftp.getReplyString();
		    logger.debug("Reply Server file SIZE: " + reply);
	
			logger.debug("Get Stream FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyCode()+"-"+ftp.getReplyString());
			
			return dataStreamStr;
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	/**
	 * getFTPFileByName
	 * @param pathFullName
	 * @return
	 * @throws Exception
	 */
	public String  getDownloadFTPFileByName(String pathFullName) throws Exception{
		FTPClient ftp = null;
		String dataStreamStr = "";
		try {			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			logger.debug("pathGetFileFull:"+pathFullName);
			
			dataStreamStr = convertStreamToString(ftp.retrieveFileStream(pathFullName));
			logger.debug("Get Stream FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyCode()+"-"+ftp.getReplyString());
			
			return dataStreamStr;
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	public String  getDownloadFTPFileByName(String pathFullName,String encoding) throws Exception{
		FTPClient ftp = null;
		String dataStreamStr = "";
		try {			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			logger.debug("pathGetFileFull:"+pathFullName);
			
			dataStreamStr = convertStreamToString(ftp.retrieveFileStream(pathFullName),encoding);
			logger.debug("Get Stream FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyCode()+"-"+ftp.getReplyString());
			
			return dataStreamStr;
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	
	public String[]  getManualScriptImportExport(String prefix,String salesType,String salesCode,String encoding) throws Exception{
		FTPClient ftp = null;
		String[] dataStreamStr = new String[3];
		String pathFullName = "";
		try {			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			//ftp.setFileType(FTPClient.);
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
			//1 AllSales/import_before_script.sql
			pathFullName =env.getProperty("path.manual.AllSales")+prefix+"_script.sql";
			logger.debug("AllSales pathGetFileFull:"+pathFullName);
			dataStreamStr[0] = convertStreamToString(ftp.retrieveFileStream(pathFullName),encoding);
			logger.debug("Get Stream FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyCode()+"-"+ftp.getReplyString());
			
			ftp.completePendingCommand();
			
			//2 SalesType/van_import_after_script.sql
			pathFullName =env.getProperty("path.manual.BySalesType")+salesType+"_"+prefix+"_script.sql";
			logger.debug("SalesType pathGetFileFull:"+pathFullName);
			dataStreamStr[1] = convertStreamToString(ftp.retrieveFileStream(pathFullName),encoding);
			logger.debug("Get Stream FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyCode()+"-"+ftp.getReplyString());
			
			ftp.completePendingCommand();
			
			//3 BySales/import_before/V207_script.sql
			pathFullName = env.getProperty("path.manual.BySales")+prefix+"/script_"+salesCode+".sql";
			logger.debug("BySales pathGetFileFull:"+pathFullName);
			dataStreamStr[2] = convertStreamToString(ftp.retrieveFileStream(pathFullName),encoding);
			logger.debug("Get Stream FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyCode()+"-"+ftp.getReplyString());
			
			return dataStreamStr;
		} catch (SocketException e) {
			e.printStackTrace();
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	/**
	 * getFTPFileByName
	 * @param pathFullName
	 * @return
	 * @throws Exception
	 */
	public String  getDownloadFTPFileByName(String pathFullName,HttpServletResponse response) throws Exception
	{
		FTPClient ftp = null;
		String dataStreamStr = "";
		try {			
			ftp = new FTPClient();
			//ftp.setControlEncoding("UTF-8");
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			//ftp.setFileTransferMode(FTPClient.COMPRESSED_TRANSFER_MODE);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			logger.debug("pathGetFileFull:"+pathFullName);
			//response.setHeader("Content-Disposition", "attachment; filename=logs." + "csv");
			//response.addHeader("X-Download-Options","open"); 
			//response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=logs.txt");
			response.setContentType("text/plain");
			//response.reset();
			
			ftp.retrieveFile(pathFullName, response.getOutputStream());
			logger.debug("Get Stream FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyCode()+"-"+ftp.getReplyString());
			
			response.getOutputStream().flush();
			response.getOutputStream().close();
			
			return dataStreamStr;
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	/**
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static String convertStreamToString(InputStream is)  throws IOException {
		return convertStreamToString(is,null,null);
	}
    
	/**
	 * 
	 * @param is
	 * @param tableName
	 * @param userBean
	 * @return
	 * @throws IOException
	 */
	public static String convertStreamToString(InputStream is,String tableName,User userBean) throws IOException {
        /*
          * To convert the InputStream to String we use the
	      * Reader.read(char[] buffer) method. We iterate until the
          * Reader return -1 which means there's no more data to
	      * read. We use the StringWriter class to produce the string.
        */
	       if (is != null) {
           Writer writer = new StringWriter();
           Reader reader = null;
	       char[] buffer = new char[1024];
           //byte[] buffer= new byte[1024];
	            try {
	            	reader = new BufferedReader(new InputStreamReader(is,Constants.FTP_IMPORT_TO_SALES_ENCODING_TIS_620));
	                int n;
	                while ((n = reader.read(buffer)) != -1) {
	                   writer.write(buffer, 0, n);
                     }
	            }finally {
	                if(is != null){
	                	is.close();
	                	is =null;
	                }
	                if(reader != null){
	                  reader.close();
	                  reader = null;
	                }
	                writer.flush();
	            }
	            return writer.toString();
	        } else {       
	            return "";
	        }
    }
	

	public static String convertStreamToString(InputStream is,String encoding) throws IOException {
        /*
          * To convert the InputStream to String we use the
	      * Reader.read(char[] buffer) method. We iterate until the
          * Reader return -1 which means there's no more data to
	      * read. We use the StringWriter class to produce the string.
        */
	       if (is != null) {
           Writer writer = new StringWriter();
           Reader reader = null;
	       char[] buffer = new char[1024];
           //byte[] buffer= new byte[1024];
	            try {
	            	reader = new BufferedReader(new InputStreamReader(is,encoding));
	                int n;
	                while ((n = reader.read(buffer)) != -1) {
	                   writer.write(buffer, 0, n);
                     }
	            }finally {
	                if(is != null){
	                	is.close();
	                	is =null;
	                }
	                if(reader != null){
	                  reader.close();
	                  reader = null;
	                }
	                writer.flush();
	            }
	            return writer.toString();
	        } else {       
	            return "";
	        }
    }
	
/**
 * writeFileToFTP
 * @param path
 * @param controlTableMap
 * @throws Exception
 */
	public void uploadFileToFTP(String path,LinkedHashMap<String,TableBean> controlTableMap,User userBean) throws Exception{
		String ftpResponse = "";
		FTPClient ftp = null;
		try {		
			ftp = new FTPClient();
			//ftp.setControlEncoding("UTF-8");
			ftp.connect(server);
			//ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			 
			ftp.changeWorkingDirectory(path);
			ftpResponse = ftp.getReplyString();
			logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse); 
			
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) {
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(tableName);
				 
				 if(tableBean.getDataStrBuffList() != null && tableBean.getDataStrBuffList().size() > 0){
					/** case have more one file **/
					for(int k =0;k<tableBean.getDataStrBuffList().size();k++){
						FTPFileBean fileBean = (FTPFileBean)tableBean.getDataStrBuffList().get(k);
	
						String dataStrBuff = fileBean.getDataResultStr();
						
						if(dataStrBuff!= null ){
							logger.debug("Write fileName:"+tableBean.getTableName());
							OutputStream out = ftp.storeFileStream(fileBean.getFileName()+"-"+userBean.getUserName()+".log");
							logger.debug("out:"+out);
							ftpResponse = ftp.getReplyString();
						    logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);
						    
							out.write(dataStrBuff.toString().getBytes());
							
							out.flush();
							out.close(); 
					
							ftp.completePendingCommand();
							
							ftpResponse = ftp.getReplyString();
						    logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);
						}else{
							// File not Found
			            }

					}//for
				 }
			}//for		
			
			
			ftp.logout();
		    
        	ftpResponse = ftp.getReplyString();
		    logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);
		    
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
			
	}
	
	/**
	 * writeFileToFTP
	 * @param controlTableMap
	 * @throws Exception
	 * ExportManager Call
	 */
	/** Process Work on Window or (some tablet V107) **/
	@Deprecated
	public void uploadAllFileToFTP_ORG1(LinkedHashMap<String,TableBean> controlTableMap,String path) throws Exception{
		FTPClient ftp = null;
		String reply = "";
		try {		
			ftp = new FTPClient();
			ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
			ftp.connect(server);
			//ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			 
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) {
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(tableName);
				
				if(tableBean.getFileExportList() != null && tableBean.getFileExportList().size() > 0){
					
	                for(int f=0;f<tableBean.getFileExportList().size();f++){
	                	TableBean fileExportBean =(TableBean)tableBean.getFileExportList().get(f);
	               
	                	if("".equalsIgnoreCase(path)){
	 					   path = fileExportBean.getExportPath();
	 					}
	 					logger.info("Write To Path:"+path);
	 					
	 					ftp.changeWorkingDirectory(path);
	 					logger.info("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

	 					logger.info("Write fileName:"+fileExportBean.getTableName());
	 					OutputStream out = ftp.storeFileStream(fileExportBean.getFileFtpNameFull());
	 				    logger.info("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
	 				    
	 				    /** new Code ******************************************************/
	 				    Writer w = new BufferedWriter(new OutputStreamWriter(out,Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620)); 
	 				    w.write(fileExportBean.getDataStrExport().toString());
	 				    w.flush();
	 				    w.close();
	 				    
	 				    out.flush();
	 				    out.close();
	 					/******************************************************************/
	 			         
	 				   /** Close Command FTP **/
					    logger.info(" Start completePendingCommand ");
						boolean completed = ftp.completePendingCommand();
						String ftpResponse = ftp.getReplyString();
						logger.info("ftpResponse["+ftpResponse+"]");
					    logger.info(" End completePendingCommand completed["+completed+"]FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    
					    //SITE CHMOD 777 201011101437-SALESREP.txt
					    /** Set Permission File ***/
					    logger.info(" Start CHMOD 777 ");
					    ftp.sendSiteCommand("CHMOD 777 "+tableBean.getFileFtpNameFull());
					    logger.info("End CHMOD 777 FTP sendSiteCommand Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					    
	                }//for
	                
	            }else if(tableBean.getDataStrExport() != null && !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
					if("".equalsIgnoreCase(path)){
					   path = tableBean.getExportPath();
					}
					logger.info("Write To Path:"+path);
					
					ftp.changeWorkingDirectory(path);
					logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

					logger.info("Write fileName:"+tableBean.getTableName());
					OutputStream out = ftp.storeFileStream(tableBean.getFileFtpNameFull());
				    logger.info("Write fileName FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    
				    /** new Code ******************************************************/
				    logger.info("Start Write OutputStreamWriter data:");
				    Writer w = new BufferedWriter(new OutputStreamWriter(out,Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620)); 
				    w.write(tableBean.getDataStrExport().toString());
				    w.flush();
				    w.close();
				    logger.info("End Write OutputStreamWriter data:");
				    
				    out.flush();
				    out.close();
					/******************************************************************/
			         
				    //Test Write File Local
				    String fileTemp = "D:/temp/"+tableBean.getFileFtpNameFull();
				    logger.info("Write File To Temp Test:"+fileTemp);
				    FileUtil.writeFile(fileTemp, tableBean.getDataStrExport().toString());
				    /******************************************************************/
				    
				    /** Close Command FTP **/
				    logger.info(" Start completePendingCommand ");
					boolean completed = ftp.completePendingCommand();
					String ftpResponse = ftp.getReplyString();
					logger.info("ftpResponse["+ftpResponse+"]");
				    logger.info(" End completePendingCommand completed["+completed+"]FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    
				    //SITE CHMOD 777 201011101437-SALESREP.txt
				    /** Set Permission File ***/
				    logger.info(" Start CHMOD 777 ");
				    ftp.sendSiteCommand("CHMOD 777 "+tableBean.getFileFtpNameFull());
				    logger.info("End CHMOD 777 FTP sendSiteCommand Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    
				}else{
					// Data not Found
					logger.info("Data not found");
				}
			}//for		
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null && ftp.isConnected()) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
		}	
	}
	
	/**
	 * Main Method upload file to FTP 
	 * @param controlTableMap
	 * @param path
	 * @throws Exception
	 */
	public void uploadAllFileToFTP(User userBean,LinkedHashMap<String,TableBean> controlTableMap,String path) throws Exception{
		try {		
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			while(it.hasNext()) {
				String fileControlName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(fileControlName);
				logger.debug("export image fileControlName:"+fileControlName); 
				
				if("LOCATION".equalsIgnoreCase(fileControlName)){
					/** Case Export Customer Export Location **/
				
					if(tableBean.getDataStrExport() != null && !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
            	        uploadAllFileToFTP_BY_FILE(path,tableBean);
					}
            	     
            	     /** Upload image file to Ftp Server **/
					if(tableBean.getImageFileList() != null && tableBean.getImageFileList().size() >0){
						try{
							uploadImageAllFileToFTP(userBean,tableBean);
						}catch(Exception e){
							logger.info("No throw Exception Error upload image:"+e.getMessage());
						}
					}
				}else if("PRODSHOW".equalsIgnoreCase(fileControlName)){
					/** Case Export Customer Export ProdShow Export Txt and Images **/
					if(tableBean.getDataStrExport() != null && !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
            	        uploadAllFileToFTP_BY_FILE(path,tableBean);
					}
            	     /** Upload image by file to FTP Server **/
					if(tableBean.getImageFileList() != null && tableBean.getImageFileList().size() >0){
						try{
							uploadImageAllFileToFTP(userBean,tableBean);
						}catch(Exception e){
							e.printStackTrace();
							logger.info("No throw Exception Error upload image:"+e.getMessage());
						}
					}
				}else{
					//More 1 file
					if(tableBean.getFileExportList() != null && tableBean.getFileExportList().size() > 0){
		                for(int f=0;f<tableBean.getFileExportList().size();f++){
		                	TableBean fileExportBean =(TableBean)tableBean.getFileExportList().get(f);
		                	//Upload by step by one file
		                	
		                	uploadAllFileToFTP_BY_FILE(path,fileExportBean);
		                }//for
		                
		            }else if(tableBean.getDataStrExport() != null && !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
		            	//Upload by step by one file
		            	uploadAllFileToFTP_BY_FILE(path,tableBean);	
					}else{
						// Data not Found
						logger.info(fileControlName+":Data not found");
					}
				}//if
			}//for		
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			
		}	
	}
	private void uploadAllFileToFTP_BY_FILE(String path,TableBean tableBean) throws Exception{
		if(ControlCode.canExecuteMethod("FTPManager", "uploadAllFileToFTP_BY_FILE_NEW")){
			//NEW Code
			//No Complete command ftp
			 uploadAllFileToFTP_BY_FILE_NEW(path,tableBean);
		}else{
			//OLD Code
			 uploadAllFileToFTP_BY_FILE_OLD(path,tableBean);
	    }
	}
	
	/** Process Work on Window 8(tablet not work V107(tablet) **/
	private void uploadAllFileToFTP_BY_FILE_OLD(String path,TableBean tableBean) throws Exception{
		FTPClient ftp = null;
		String reply = "";
		Writer w = null;
		OutputStream out = null;
		logger.info("uploadAllFileToFTP_BY_FILE_OLD");
		try {		
			ftp = new FTPClient();
			ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
			ftp.connect(server);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			//ftp.setConnectTimeout(10000);

			logger.info("connection timeout:"+ftp.getConnectTimeout());
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			 
			 if(tableBean.getDataStrExport() != null && !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
					if("".equalsIgnoreCase(path)){
					   path = tableBean.getExportPath();
					}
					logger.info("Write To Path:"+path);
					
					ftp.changeWorkingDirectory(path);
					logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

					logger.info("Write fileName:"+tableBean.getTableName()+":"+tableBean.getFileFtpNameFull());
					out = ftp.storeFileStream(tableBean.getFileFtpNameFull());
				    logger.info("Write fileName FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    
				    /** new Code ******************************************************/
				    logger.info("Start Write OutputStreamWriter data:");
				    w = new BufferedWriter(new OutputStreamWriter(out,Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620)); 
				    w.write(tableBean.getDataStrExport().toString());
				    w.flush();
				    w.close();
				    
				    logger.info("End Write OutputStreamWriter data:");
				    
				    out.flush();
				    out.close();
					/******************************************************************/
			         
				 /*   //Test Write File Local
				    String fileTemp = "D:/temp/"+tableBean.getFileFtpNameFull();
				    logger.info("Write File To Temp Test:"+fileTemp);
				    FileUtil.writeFile(fileTemp, tableBean.getDataStrExport().toString());*/
				    /******************************************************************/
				    
				    /** Close Command FTP **/
				    try{
					    logger.info(" Start completePendingCommand ");
						boolean completed = ftp.completePendingCommand();
						String ftpResponse = ftp.getReplyString();
						logger.info("ftpResponse["+ftpResponse+"]");
					    logger.info(" End completePendingCommand completed["+completed+"]FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    }catch(Exception e){
				        logger.error(e.getMessage(),e);
				    }
				    
				    //SITE CHMOD 777 201011101437-SALESREP.txt
				    /** Set Permission File ***/
				    try{
					    logger.info(" Start CHMOD 777 ");
					    ftp.sendSiteCommand("CHMOD 777 "+tableBean.getFileFtpNameFull());
					    logger.info("End CHMOD 777 FTP sendSiteCommand Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    }catch(Exception e){
				    	logger.error(e.getMessage(),e);	
				    }
				}else{
					// Data not Found
					logger.info("Data not found");
				}
	
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(w != null){
				//w.close();
			}
			if(out != null){
			  // out.close();
			}
			if(ftp != null && ftp.isConnected()) {
				try{
					ftp.logout();
					ftp.disconnect();
					//logger.info("ftp disconnect : "+ftp.getReplyString());
					ftp = null;
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}	
	}
	
	/** Process Work on Window 10 **/
	private void uploadAllFileToFTP_BY_FILE_NEW(String path,TableBean tableBean) throws Exception{
		FTPClient ftp = null;
		String reply = "";
		Writer w = null;
		OutputStream out = null;
		logger.info("uploadAllFileToFTP_OPT2_BY_FILE_NEW");
		try {		
			ftp = new FTPClient();
			ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
			ftp.connect(server);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			//ftp.setConnectTimeout(10000);
			logger.info("connection timeout:"+ftp.getConnectTimeout());
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			 
			 if(tableBean.getDataStrExport() != null 
					&& !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
					if("".equalsIgnoreCase(path)){
					   path = tableBean.getExportPath();
					}
					logger.info("Write To Path:"+path);
					
					ftp.changeWorkingDirectory(path);
					logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

					logger.info("Write fileName:"+tableBean.getTableName()+":"+tableBean.getFileFtpNameFull());
					out = ftp.storeFileStream(tableBean.getFileFtpNameFull());
				    logger.info("Write fileName FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    
				    /** new Code ******************************************************/
				    logger.info("Start Write OutputStreamWriter data:");
				    w = new BufferedWriter(new OutputStreamWriter(out,Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620)); 
				    w.write(tableBean.getDataStrExport().toString());
				    w.flush();
				    w.close();
				    
				    logger.info("End Write OutputStreamWriter data:");
				    
				    out.flush();
				    out.close();
					/******************************************************************/
			         
				   /* //Test Write File Local
				    String fileTemp = "D:/temp/"+tableBean.getFileFtpNameFull();
				    logger.info("Write File To Temp Test:"+fileTemp);
				    FileUtil.writeFile(fileTemp, tableBean.getDataStrExport().toString());*/
				    /******************************************************************/
				    
				    /** Close Command FTP **/
				    try{
					    logger.info(" Start completePendingCommand ");
						boolean completed = ftp.completePendingCommand();
						String ftpResponse = ftp.getReplyString();
						logger.info("ftpResponse["+ftpResponse+"]");
					    logger.info(" End completePendingCommand completed["+completed+"]FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    }catch(Exception e){
				        logger.error(e.getMessage(),e);
				    }
				    
				    //SITE CHMOD 777 201011101437-SALESREP.txt
				    /** Set Permission File ***/
				    try{
					    logger.info(" Start CHMOD 777 ");
					    ftp.sendSiteCommand("CHMOD 777 "+tableBean.getFileFtpNameFull());
					    logger.info("End CHMOD 777 FTP sendSiteCommand Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    }catch(Exception e){
				    	logger.error(e.getMessage(),e);	
				    }
				}else{
					// Data not Found
					logger.info("Data not found");
				}
	
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {

			if(ftp != null && ftp.isConnected()) {
				try{
					ftp.logout();
					ftp.disconnect();
					//logger.info("ftp disconnect : "+ftp.getReplyString());
					ftp = null;
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}	
	}
	
	private void uploadImageAllFileToFTP(User user,TableBean tableBean) throws Exception{
		FTPClient ftp = null;
		String replyMsg = "";
		int replayCode = 0;
		Writer w = null;
		OutputStream out = null;
		logger.info("uploadImageAllFileToFTP");
		String path = "";
		//String overWrite = "";
		try {		
			/** Get init config **/
			logger.debug("uploadImageAllFileToFTP tableName:"+tableBean.getTableName());
			logger.debug("imageFileList size :"+tableBean.getImageFileList().size());
			
			if("m_customer_location".equalsIgnoreCase(tableBean.getTableName())){
			   path = EnvProperties.getInstance().getProperty("path.master.customer.images");
			}else if("t_prod_show".equalsIgnoreCase(tableBean.getTableName())){
			   path = EnvProperties.getInstance().getProperty("path.transaction.sales.out.photo");
			}
			//overWrite = EnvProperties.getInstance().getProperty("path.master.customer.images.overwrite");
			logger.debug("path:"+path);
			
			ftp = new FTPClient();
			//ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
			ftp.connect(server);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();

			logger.info("connection timeout:"+ftp.getConnectTimeout());
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			 
			 if(tableBean.getImageFileList() != null && tableBean.getImageFileList().size() >0){
					//logger.info("Write To Path:"+path);
					//check isExist ? Create Folder by UserCode
					 if("m_customer_location".equalsIgnoreCase(tableBean.getTableName())){
						String pathCheck = path+ user.getUserName();
						ftp.changeWorkingDirectory(pathCheck);
						replayCode = ftp.getReplyCode();
						
						if (replayCode == 550) {
							//Create Folder
							logger.debug("not found Folder :mkdir:"+path+user.getUserName());
							ftp.changeWorkingDirectory(path);
							ftp.makeDirectory(user.getUserName());
						}
					 }
					if("m_customer_location".equalsIgnoreCase(tableBean.getTableName())){
						ftp.changeWorkingDirectory(path+user.getUserName());
						logger.debug("path:"+path+user.getUserName());
					}else if("t_prod_show".equalsIgnoreCase(tableBean.getTableName())){
						ftp.changeWorkingDirectory(path);
					}
					
					logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

					for(int i=0;i<tableBean.getImageFileList().size();i++){
						ImageFileBean im = tableBean.getImageFileList().get(i);
						
						logger.info("Write imageFileName:"+im.getGenerateImageFileName());
						ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
					    
					    /***** Method 1 ******************************************************/
					    File localFile = new File(im.getImageFileName());
					    
						String remoteFile = im.getGenerateImageFileName();
						InputStream inputStream = new FileInputStream(localFile);
						
						logger.info("Start uploading  file");
						boolean done = ftp.storeFile(remoteFile, inputStream);
						if(done){
						   inputStream.close();
						   logger.info("Upload Image File FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 
						   logger.info("The is uploaded successfully.");
						}
						
					    /** Close Command FTP **/
					   /* try{
						    logger.info(" Start completePendingCommand ");
							boolean completed = ftp.completePendingCommand();
							String ftpResponse = ftp.getReplyString();
							logger.info("ftpResponse["+ftpResponse+"]");
						    logger.info(" End completePendingCommand completed["+completed+"]FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					    }catch(Exception e){
					        logger.error(e.getMessage(),e);
					    }*/
			        }//for
				}else{
					// Data not Found
					logger.info("Data not found");
				}
	
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(w != null){
				//w.close();
			}
			if(out != null){
			  // out.close();
			}
			if(ftp != null && ftp.isConnected()) {
				try{
					ftp.logout();
					ftp.disconnect();
					logger.info("ftp disconnect : "+ftp.getReplyString());
					ftp = null;
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}	
	}
	
	private void uploadImageOneFileToFTP(User user,TableBean tableBean,ImageFileBean imageFileBean) throws Exception{
		FTPClient ftp = null;
		String replyMsg = "";
		int replayCode = 0;
		Writer w = null;
		OutputStream out = null;
		logger.info("uploadImageAllToFTP");
		String path = "";
		//String overWrite = "";
		try {		
			/** Get init config **/
			logger.debug("uploadImageOneToFTP tableName:"+tableBean.getTableName());
			
			if("m_customer_location".equalsIgnoreCase(tableBean.getTableName())){
			   path = EnvProperties.getInstance().getProperty("path.master.customer.images");
			}else if("t_prod_show".equalsIgnoreCase(tableBean.getTableName())){
			   path = EnvProperties.getInstance().getProperty("path.transaction.sales.out.photo");
			}
			//overWrite = EnvProperties.getInstance().getProperty("path.master.customer.images.overwrite");
			logger.debug("path:"+path);
			
			ftp = new FTPClient();
			//ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
			ftp.connect(server);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();

			//logger.info("connection timeout:"+ftp.getConnectTimeout());
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			 
			 if(imageFileBean != null){
				//logger.info("Write To Path:"+path);
				//check isExist ? Create Folder by UserCode
				if("m_customer_location".equalsIgnoreCase(tableBean.getTableName())){
					String pathCheck = path+ user.getUserName();
					ftp.changeWorkingDirectory(pathCheck);
					replayCode = ftp.getReplyCode();
					
					if (replayCode == 550) {
						//Create Folder
						logger.debug("not found Folder :mkdir:"+path+user.getUserName());
						ftp.changeWorkingDirectory(path);
						ftp.makeDirectory(user.getUserName());
					}
				}
				if("m_customer_location".equalsIgnoreCase(tableBean.getTableName())){
					ftp.changeWorkingDirectory(path+user.getUserName());
					logger.debug("path:"+path+user.getUserName());
				}else if("t_prod_show".equalsIgnoreCase(tableBean.getTableName())){
					ftp.changeWorkingDirectory(path);
				}
				
				logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				logger.info("Write imageFileName:"+imageFileBean.getGenerateImageFileName());
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			    
			    /***** Method 1 ******************************************************/
			    File localFile = new File(imageFileBean.getImageFileName());
				String remoteFile = imageFileBean.getGenerateImageFileName();
				InputStream inputStream = new FileInputStream(localFile);
				
				logger.info("Start uploading  file");
				boolean done = ftp.storeFile(remoteFile, inputStream);
				if(done){
				   inputStream.close();
				   logger.info("Upload Image File FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 
				   logger.info("The is uploaded successfully.");
				}	
			}else{
				// Data not Found
				logger.info("Data not found");
			}
	
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(w != null){
				//w.close();
			}
			if(out != null){
			  // out.close();
			}
			if(ftp != null && ftp.isConnected()) {
				try{
					ftp.logout();
					ftp.disconnect();
					logger.info("ftp disconnect : "+ftp.getReplyString());
					ftp = null;
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}	
	}
	
public void uploadImageByFileName(String ftpFilePath,String localFile) throws Exception{
		
		DataOutputStream dos = null;
		sun.net.TelnetOutputStream tos = null;
		FileInputStream fos = null;
		sun.net.ftp.FtpClient ftp = null;
		try{
			logger.info("start upload:"+ftpFilePath);
			ftp = new sun.net.ftp.FtpClient(server, 21);
		    ftp.login(userFtp, passwordFtp);
		    ftp.binary();
		    
		    fos = new FileInputStream(localFile);
		    tos = ftp.put(ftpFilePath);
		    dos = new DataOutputStream(tos);
		    
		    byte[] buffer = new byte[2048 * 2048];
		    for (int length; (length = fos.read(buffer)) > 0;) {
		        dos.write(buffer, 0, length);
		    }
		    tos.flush();
		    dos.flush();
		    logger.info("upload success");
		    logger.info("resonse msg :"+ftp.getResponseString());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(ftp != null){
				ftp = null;
			}
			if(tos != null){
				tos.close();
			}
			if(fos != null){
				fos.close();
			}
			if(dos != null){
				dos.close();
			}
		}
	}

	
	/**
	 * 
	 * @param controlTableMap
	 * @param path
	 * @throws Exception
	 */
	public void deleteAllFileInFTPCaseRollback(LinkedHashMap<String,TableBean> controlTableMap,String path) throws Exception{
		FTPClient ftp = null;
		try {		
			ftp = new FTPClient();
			ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
			ftp.connect(server);
			//ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			 
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) {
				String tableName = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(tableName);
				if(tableBean.getFileExportList() != null && tableBean.getFileExportList().size() > 0){
	                for(int f=0;f<tableBean.getFileExportList().size();f++){
	                	TableBean fileExportBean =(TableBean)tableBean.getFileExportList().get(f);
	               
	                	if("".equalsIgnoreCase(path)){
	 					   path = fileExportBean.getExportPath();
	 					}
	 					logger.debug("Write To Path:"+path);
	 					
	 					ftp.changeWorkingDirectory(path);
	 					logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
	 					
	 				    ftp.deleteFile(tableBean.getFileFtpNameFull());
	 					logger.debug("Delete "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 
	 			
	 				    /** Close Command FTP **/
	 					ftp.completePendingCommand();
	 				    logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());

	                }//for
	                
	            }else if(tableBean.getDataStrExport() != null && !Utils.isNull(tableBean.getDataStrExport().toString()).equals("")){
					if("".equalsIgnoreCase(path)){
					   path = tableBean.getExportPath();
					}
					logger.debug("Write To Path:"+path);
					
					ftp.changeWorkingDirectory(path);
					logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

					ftp.deleteFile(tableBean.getFileFtpNameFull());
	 			    logger.debug("Delete "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 
	 					
				    /** Close Command FTP **/
					ftp.completePendingCommand();
				    logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    
				}else{
					// Data not Found
					logger.debug("Data not found");
				}
			}//for		

		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null && ftp.isConnected()) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
		}	
	}
	
	/**
	 * 
	 * @param controlTableMap
	 * @param path
	 * @throws Exception
	 */
	public StringBuffer downloadAllFileInFolder(String path) throws Exception{
		FTPClient ftp = null;
		StringBuffer data = new StringBuffer("");
		try {		
			ftp = new FTPClient();
			ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
			ftp.connect(server);
			//ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			logger.debug("Write To Path:"+path);
				
			ftp.changeWorkingDirectory(path);
			logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
			
			FTPFile[] files = ftp.listFiles();
			for(FTPFile file: files){
				if( !file.getName().startsWith(".")){
	             
	              String line = convertStreamToString(ftp.retrieveFileStream(file.getName()),"",null);
	              logger.info("fileName:"+file.getName()+",data["+line+"]");
	              
	              data.append(file.getName().substring(0,file.getName().indexOf("."))+","+line +"\n");
	              ftp.completePendingCommand();
				}
	        }
			
			logger.info("data:"+data.toString());
			
			logger.info("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
            
			ftp.logout();
			
			return data;
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null && ftp.isConnected()) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
		}	
	}
	
	
	/**
	 * writeFileToFTP
	 * @param controlTableMap
	 * @throws Exception
	 * ExportManager Call
	 */
	public String uploadFileToFTP(String path,String fileName,String data,String encoding) throws Exception{
		String ftpResponse = "";
		FTPClient ftp = null;
		String fileSize = "0";
		try {		
			ftp = new FTPClient();
			if(encoding !=null){
			   ftp.setControlEncoding(encoding);
			}
			ftp.connect(server);
			//ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			//ftp.setFileTransferMode(FTPClient.ASCII_FILE_TYPE);

			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}

			logger.debug("Write To Path:"+path);
			
			ftp.changeWorkingDirectory(path);
			ftpResponse = ftp.getReplyString();
			logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse); 
				
			logger.debug("Write fileName:"+fileName);
			OutputStream out = ftp.storeFileStream(fileName);
			//logger.debug("out:"+out);
			ftpResponse = ftp.getReplyString();
		    logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);
		    
			
			/** new Code ******************************************************/
		    Writer w = new BufferedWriter(new OutputStreamWriter(out,encoding)); 
		    w.write(data);
		    w.flush();
		    w.close();
			/******************************************************************/
	
			ftp.completePendingCommand();
			ftpResponse = ftp.getReplyString();
		    logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);
			 
		
			ftp.logout();
		    
        	ftpResponse = ftp.getReplyString();
		    logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);
		    
		    return fileSize;
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
		}	
	}
	
	
	/**
	 * moveFileFTP 
	 * @param source
	 * @param destination
	 * @param controlTableMap
	 * @throws Exception
	 * Step Move file and Delete from Source File
	 */
	public void moveFileFTP(String source ,String destination ,LinkedHashMap<String,TableBean> controlTableMap) throws Exception{
		String ftpResponse = "";
		FTPClient ftpFrom = null;
		FTPClient ftpTo = null;
		try {	
			logger.debug("Move File From :"+source+" TO :"+destination);
			
			ftpFrom = new FTPClient();
			//ftpFrom.setControlEncoding("UTF-8");
			ftpFrom.connect(server);
			ftpFrom.enterLocalPassiveMode();
			
			if(!ftpFrom.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
			ftpTo = new FTPClient();
			//ftpTo.setControlEncoding("UTF-8");
			ftpTo.connect(server);
			ftpTo.enterLocalPassiveMode();
			
			if(!ftpTo.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
			ftpFrom.changeWorkingDirectory(source);
			ftpResponse = ftpFrom.getReplyString();
			logger.debug("FtpFrom FTP Response "+ftpFrom.getControlEncoding()+" :"+ftpResponse);
			
			ftpTo.changeWorkingDirectory(destination);
			ftpResponse = ftpTo.getReplyString();
			logger.debug("FtpTo FTP Response "+ftpFrom.getControlEncoding()+" :"+ftpResponse);
			
			FTPFile[] tempFromFiles = ftpFrom.listFiles();
			for (int k = 0; k < tempFromFiles.length; k++) { //for 1
				Set s = controlTableMap.keySet();
				Iterator it = s.iterator();
				for (int i = 1; it.hasNext(); i++) { //for 2
					String tableName = (String) it.next();
					TableBean tableBean = (TableBean) controlTableMap.get(tableName);
	                
					if(tableBean.getDataLineList() != null && tableBean.getDataLineList().size() > 0){
			        	/** case have more one file **/
						for(int n =0;n<tableBean.getDataLineList().size();n++){ //for 3
							FTPFileBean ftpBean = (FTPFileBean)tableBean.getDataLineList().get(n);	
							
							FTPFile formFile = tempFromFiles[k];
							
							if(Utils.isNull(ftpBean.getFileName()).equalsIgnoreCase(formFile.getName())){
							
								if (formFile.getName().equals(".") || formFile.getName().equals("..")){
									continue;
								}
								
								logger.debug("Move file:"+ftpBean.getFileName());
								InputStream in = ftpFrom.retrieveFileStream(formFile.getName());
								OutputStream out = ftpTo.storeFileStream(formFile.getName());
								
								byte[] b = new byte[1024];
								while (in.read(b) != -1) {
									out.write(b);
								}
								out.flush();
								out.close();
								in.close();
								
								/** delete from source file */
								logger.debug("delete file:"+ftpBean.getFileName());
								//ftpFrom.deleteFile(ftpBean.getFileName());
								
								ftpTo.completePendingCommand();
								logger.info("[ftpTo] completePendingCommand" + ftpTo.getReplyString());
								ftpFrom.completePendingCommand();
								logger.info("[ftpFrom] completePendingCommand" + ftpFrom.getReplyString());
						   }//
						}//for 3
					 }//if 
				}//for 2
			}//for 1    
			
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftpFrom != null) {
				ftpFrom.disconnect();
				//logger.info("ftp disconnect : "+ftpFrom.getReplyString());
				ftpFrom = null;
			}
			if(ftpTo != null) {
				ftpTo.disconnect();
				//logger.info("ftp disconnect : "+ftpTo.getReplyString());
				ftpTo = null;
			}
		}
	}

	/**
	 * moveFileFTP 
	 * @param source
	 * @param destination
	 * @param controlTableMap
	 * @throws Exception
	 * Step Move file and Delete from Source File
	 */
	public void moveFileFTP_NEW(String source ,String destination ,List<FTPFileBean> fileImportSuccessList) throws Exception{
		FTPClient ftp = null;
		try {	
			logger.debug("Move File From :"+source+" TO :"+destination);
			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
	        if(fileImportSuccessList != null && fileImportSuccessList.size() > 0){
				for(int n =0;n<fileImportSuccessList.size();n++){ //for 3
					FTPFileBean ftpBean = (FTPFileBean)fileImportSuccessList.get(n);	
					logger.debug("fileNameFull:"+ftpBean.getFileName());
					
					//Step1 set Dir Active
					ftp.changeWorkingDirectory(source);
					logger.debug("Step1 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					
					//step 2 
					ftp.sendCommand("RNFR "+ftpBean.getFileName());
					logger.debug("RNFR:"+ftpBean.getFileName());
					logger.debug("step2 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					
					//step3
					String repath = destination+ftpBean.getFileName();
					ftp.sendCommand("RNTO "+repath);
					logger.debug("RNTO:"+repath);
					logger.debug("step3 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					
				}//for 3
	        }

		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
		}
	}
	
	public void moveFileFTP(String source ,String destination ,List<FileImportTransBean> fileImportSuccessList) throws Exception{
		FTPClient ftp = null;
		try {	
			logger.debug("Move File From :"+source+" TO :"+destination);
			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
	        if(fileImportSuccessList != null && fileImportSuccessList.size() > 0){
				for(int n =0;n<fileImportSuccessList.size();n++){ //for 3
					FileImportTransBean ftpBean = (FileImportTransBean)fileImportSuccessList.get(n);	
					logger.debug("fileNameFull:"+ftpBean.getFileName());
					
					//Step1 set Dir Active
					ftp.changeWorkingDirectory(source);
					logger.debug("Step1 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					
					//step 2 
					ftp.sendCommand("RNFR "+ftpBean.getFileName());
					logger.debug("RNFR:"+ftpBean.getFileName());
					logger.debug("step2 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					
					//step3
					String repath = destination+ftpBean.getFileName();
					ftp.sendCommand("RNTO "+repath);
					logger.debug("RNTO:"+repath);
					logger.debug("step3 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					
				}//for 3
	        }

		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	public void createFolderFTP(String rootPath ,String newFolder) throws Exception{
		FTPClient ftp = null;
		try {	
			//logger.debug("createFolderFTP :"+rootPath+" NewFolder :"+newFolder);
			//System.out.println("createFolderFTP :"+rootPath+" NewFolder :"+newFolder);
			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}

			//Step1 Check Folder
			ftp.changeWorkingDirectory(rootPath+newFolder);
			int response = ftp.getReplyCode();
			//System.out.println("Step1 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
			
			if(response==550){
				ftp.changeWorkingDirectory(rootPath);
				ftp.makeDirectory(newFolder);
			   logger.debug("Step1 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
			}
					
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	/**
	 * deleteFileFTP
	 * @param source
	 * @param controlTableMap
	 * @throws Exception
	 */
	public void deleteFileFTP(String source  ,LinkedHashMap<String,TableBean> controlTableMap) throws Exception{
		String ftpResponse = "";
		FTPClient ftp = null;
		try {			
			ftp = new FTPClient();
			ftp.setControlEncoding("UTF-8");
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
			/** Delete source File */
			ftp.changeWorkingDirectory(source);
			ftpResponse = ftp.getReplyString();
			logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);

			logger.debug("Delete file from  "+source);
			Set s = controlTableMap.keySet();
			Iterator it = s.iterator();
			for (int i = 1; it.hasNext(); i++) {
				//Meter m = new Meter();
				String k = (String) it.next();
				TableBean tableBean = (TableBean) controlTableMap.get(k);
				ftp.deleteFile(tableBean.getFileFtpNameFull());
				logger.debug("Delete FTP Response fileName: "+tableBean.getFileFtpNameFull()+":"+ftp.getControlEncoding()+" :"+ftpResponse);
			}//for
			
			/** Logout FTP **/
            ftp.logout();
		    
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	/**
	 * deleteFileFTP
	 * @param source
	 * @param controlTableMap
	 * @throws Exception
	 */
	public void deleteFileFTP(String source ,String fileName) throws Exception{
		String ftpResponse = "";
		FTPClient ftp = null;
		try {	
			
			ftp = new FTPClient();
			ftp.setControlEncoding("UTF-8");
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
			/** Delete source File */
			ftp.changeWorkingDirectory(source);
			ftpResponse = ftp.getReplyString();
			logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);

			logger.debug("Delete file from  "+source);
			ftp.deleteFile(fileName);
			
			/** Logout FTP **/
            ftp.logout();
            
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			
		}
	}
	
	/**
	 * exportBackUpDBFileToFTP
	 * @param user
	 * @param parentPath
	 * @param fileName
	 * @param zipOut
	 * @throws Exception
	 */
	public void uploadBackUpDBFileToFTP(User user,String parentPath,String fileName,StringBuffer dataFiles) throws Exception{
		FTPClient ftp = null;
		File file = null;
		FileInputStream fis = null;
		try {		
			ftp = new FTPClient();
			ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
			ftp.connect(server);
			//ftp.enterLocalActiveMode();
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			
	        
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			logger.debug("Parent path:"+parentPath);
			
			String userTypePath = user.getType()+"/"+user.getUserName()+"/";
			
			/** Check Path BY User is created    /parentpath/VAN/CC401/  **/
			ftp.changeWorkingDirectory(parentPath+userTypePath);
			logger.debug("0 FTP Response "+parentPath+userTypePath+" :"+ftp.getReplyString()); 
			
		    if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())){
		    	logger.debug("not fount path :"+parentPath+userTypePath);
				// Folder not create 
		    	ftp.changeWorkingDirectory(parentPath+user.getType()+"/");
		
		    	logger.debug("Path:"+parentPath+user.getType()+"/"+":"+ftp.getReplyString()); 
		    	ftp.makeDirectory(parentPath+userTypePath);
		    	logger.debug("makeDirectory :"+ftp.getReplyString()); 
		    	
		    	ftp.changeWorkingDirectory(parentPath+userTypePath);
		    	/** changemode folder **/
		    	ftp.sendSiteCommand("CHMOD 777 "+parentPath+userTypePath);
			    logger.debug("FTP sendSiteCommand Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
			}

		    logger.debug("fileFullName:"+parentPath+userTypePath+fileName);
		    try{
		    	
			    OutputStream out = ftp.storeFileStream(parentPath+userTypePath+fileName);
			    logger.debug("1 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReply());
			    
			    /** new Code ******************************************************/
			    Writer w = new BufferedWriter(new OutputStreamWriter(out,Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620)); 
			    
			    w.write(dataFiles.toString());
			    w.flush();
			    w.close();
				/******************************************************************/
				    
			    logger.debug("2 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
			    
			    /** Close Command FTP **/
				ftp.completePendingCommand();
			    logger.debug("3 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
		    }catch(Exception e){
		    	e.printStackTrace();
		    }
		   // ftp.sendSiteCommand("CHMOD 777 "+fileName);
		   // logger.debug("FTP sendSiteCommand Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
		} catch (SocketException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (UnknownHostException e) {
			throw new FTPException("Could not connect to FTP server");
		} catch (IOException e) {
			throw new FTPException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new FTPException(e.getMessage());
		} finally {
			if(ftp != null && ftp.logout()) {
				//logger.info("ftp disconnect : "+ftp.getReplyString());
			}
			if(ftp != null && ftp.isConnected()) {
				ftp.disconnect();
				//logger.info("ftp disconnect : "+ftp.getReplyString());
				ftp = null;
			}
			if(file != null){
				file = null;
			}
			if(fis != null){
			   fis.close();fis = null;
			}
		}	
	}
	
public void uploadBackUpDBZipFileToFTP(String rootFtpPath, String ftpFilePath,String localFile) throws Exception{
		
		DataOutputStream dos = null;
		sun.net.TelnetOutputStream tos = null;
		FileInputStream fos = null;
		sun.net.ftp.FtpClient ftp = null;
		try{
			ftp = new sun.net.ftp.FtpClient(server, 21);
		    ftp.login(userFtp, passwordFtp);
		    ftp.binary();
		    
		    fos = new FileInputStream(localFile);
		    tos = ftp.put(ftpFilePath);
		    dos = new DataOutputStream(tos);
		    
		    byte[] buffer = new byte[2048 * 2048];
		    for (int length; (length = fos.read(buffer)) > 0;) {
		        dos.write(buffer, 0, length);
		    }
		    tos.flush();
		    dos.flush();
		    logger.info("upload success");
		    
		    /** Change Mode file to 777 **/
		    changeModeFile(rootFtpPath,ftpFilePath);
		    
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(ftp != null){
				ftp = null;
			}
			if(tos != null){
				tos.close();
			}
			if(fos != null){
				fos.close();
			}
			if(dos != null){
				dos.close();
			}
		}
	}
	
public void changeModeFile(String rootFtpPath,String ftpFilePath) throws Exception{
	FTPClient ftp = null;
	String reply = "";
	try {		
		
		ftp = new FTPClient();
		ftp.connect(server);
		//ftp.enterLocalActiveMode();
		ftp.enterLocalPassiveMode();
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

		if(!ftp.login(userFtp, passwordFtp)){
			throw new FTPException("FTP Username or password invalid! ");
		}

		logger.info("Write To Path:"+rootFtpPath);
		ftp.changeWorkingDirectory(rootFtpPath);
		logger.info("FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 
 
	    //SITE CHMOD 777 201011101437-SALESREP.txt
	    /** Set Permission File ***/
	    logger.info(" Start CHMOD 777 ");
	    ftp.sendSiteCommand("CHMOD 777 "+ftpFilePath);
	    logger.info("End CHMOD 777 FTP sendSiteCommand Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());

	} catch (SocketException e) {
		throw new FTPException("Could not connect to FTP server");
	} catch (UnknownHostException e) {
		throw new FTPException("Could not connect to FTP server");
	} catch (IOException e) {
		throw new FTPException(e.getLocalizedMessage());
	} catch (Exception e) {
		throw new FTPException(e.getMessage());
	} finally {
		if(ftp != null && ftp.isConnected()) {
			ftp.disconnect();
			//logger.info("ftp disconnect : "+ftp.getReplyString());
			ftp = null;
		}
	}	
}
}
