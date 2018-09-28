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

import javax.net.ssl.KeyManager;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.FTPException;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.InterfaceHelper;
import com.isecinc.pens.inf.helper.InterfaceUtils;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

/**
 * @author WITTY
 *
 */
public class FTPManager {

	
	private static Logger logger = Logger.getLogger("PENS"); 
	
	public String server;
	public String userFtp;
	public String passwordFtp;
	public static EnvProperties env = EnvProperties.getInstance();
	
	public FTPManager(String server,String userFtp,String passwordFtp){
		this.server = server;
		this.userFtp = userFtp;
		this.passwordFtp = passwordFtp;
		
		//verify server
		//Case 1  server 1 no active -> server 2
		//Case 2  server 2 no active -> server 1 (default)
		
		//chooseFtpServer();
	}
	public static void main(String[] s){
		try{
		   FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
		   ftpManager.testConnectionSFTP();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public  boolean testConnectionSFTP() {
	   boolean re = true;
	   FTPSClient sftp = null;
    	try{
    		logger.debug("server:"+"192.168.38.186");
    		sftp = new FTPSClient("SSL");
    		String[] protocolVersions = {"SSLv3"};
			
    		//sftp.setConnectTimeout(120000);
			sftp.setEnabledProtocols(protocolVersions);
			sftp.connect("192.168.38.186",990);
			
			int replyDesc = sftp.getReplyCode();
			logger.debug("["+server+"]:["+replyDesc+"]");
		
		}catch(Exception e){
			e.printStackTrace();
			re = false;
		}
	    return re;
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
				logger.debug("PathImport:"+path);
				logger.debug("Start init FTP File ");
				
				List<FTPFileBean> outStreamList = null;
                for(i=0;i<ftpFiles.length;i++){
                	FTPFile file = (FTPFile)ftpFiles[i];
                	logger.debug("FTP fileName:"+file.getName());
                	
                	Set s = controlTableMap.keySet();
    				Iterator it = s.iterator();
    				boolean canGetFtpFile = false;
    				for (int j = 1; it.hasNext(); j++) {
    					canGetFtpFile = false; 
    					String tableName = (String) it.next();
    					logger.debug("tableName:"+tableName);
    					TableBean tableBean = (TableBean) controlTableMap.get(tableName);
                        String ftpFileName = Utils.isNull(file.getName());
                        
    					if(!ftpFileName.equals(".") && !ftpFileName.equals("..")){
    					   String typeName = "";
    					   if( !ftpFileName.equals("") && ftpFileName.indexOf(".") != -1){
    						   typeName = ftpFileName.substring(ftpFileName.indexOf(".")+1,ftpFileName.length());
    					   }
    					   logger.debug("typeName:"+typeName);
    					   if(typeName.equalsIgnoreCase("txt")){
    					       canGetFtpFile = true;
    					   }
    					}
    					logger.debug("Table["+tableName+"]fileFtpname["+ftpFileName+"]canGetFtpFile["+canGetFtpFile+"]");
    					
    					if(canGetFtpFile==true){
    						
    						countFileMap++;
	                		/** Set Full File Name **/
	                		tableBean.setFileFtpNameFull(ftpFileName);
	                		logger.debug("canGetFtpFile No["+countFileMap+"]put FtpName:"+tableBean.getFileFtpNameFull());
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
	
	public int downloadFileMappingToTableICC(User user,LinkedHashMap<String,TableBean> controlTableMap ,String path,String transDate) throws Exception
	{
		FTPClient ftp = null;
		String ftpResponse = "";
		int i = 0;
		int countFileMap = 0;
		String fileNameFixByDate  ="";
		try {			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			
			ftp.changeWorkingDirectory(path);
			logger.debug("Ftp changeWorkingDirectory response :"+ftp.getReplyString());
			FTPFile[] ftpFiles = ftp.listFiles();
			ftpResponse = ftp.getReplyString();
            logger.debug("Ftp listFiles response :"+ftpResponse);
            
			if (ftpFiles != null && ftpResponse.startsWith("226")) {
				logger.debug("PathImport:"+path);
				logger.debug("Start Load FTP File ");
				
				List<FTPFileBean> outStreamList = null;
                for(i=0;i<ftpFiles.length;i++){
                	FTPFile file = (FTPFile)ftpFiles[i];
                	logger.debug("FTP fileName:"+file.getName());
                	
                	Set s = controlTableMap.keySet();
    				Iterator it = s.iterator();
    				boolean canGetFtpFile = false;
    				for (int j = 1; it.hasNext(); j++) {
    					fileNameFixByDate = "";
    					canGetFtpFile = false; 
    					String tableName = (String) it.next();
    					//logger.debug("tableName:"+tableName);
    					TableBean tableBean = (TableBean) controlTableMap.get(tableName);
                        String ftpFileName = Utils.isNull(file.getName());
                        
    					if(!ftpFileName.equals(".") && !ftpFileName.equals("..")){
    					   fileNameFixByDate = InterfaceUtils.getImportNameICC(tableName, transDate);
    					   // Fix File Name
    					   
    					   if(ftpFileName.toLowerCase().startsWith(fileNameFixByDate.toLowerCase())){
    					       canGetFtpFile = true;
    					   }
    					}
    					
    					if(canGetFtpFile==true){
    						logger.debug("Get File Table["+tableName+"]fileNameFixByDate["+fileNameFixByDate+"]fileFtpname["+ftpFileName+"]canGetFtpFile["+canGetFtpFile+"]");
    						
    						countFileMap++;
	                		/** Set Full File Name **/
	                		tableBean.setFileFtpNameFull(ftpFileName);
	                		logger.debug("canGetFtpFile No["+countFileMap+"]put FtpName:"+tableBean.getFileFtpNameFull());
		                	//ByteArrayOutputStream out = new ByteArrayOutputStream();
		                    
		                	/** 1 Write file to Path */
		                	//ftp.retrieveFile(file.getName(),out);
		                	//String dataStreamStr =  out.toString();
		                	
		                	/** 2 **/
		                	String dataStreamStr = convertStreamToString(ftp.retrieveFileStream(ftpFileName),tableName,user);
		                	ftp.completePendingCommand();
		                	//logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);

		                	//logger.debug("dataStreamStrXX:"+dataStreamStr);
		                	
		                    /** Store DataStream in TableBean **/

		                	String[] dataLineTextArr =  new String(dataStreamStr).split(Constants.newLine);
		                	/** Case one table have more one file **/
		                	if(tableBean.getDataLineList() ==null){
		                		outStreamList = new ArrayList<FTPFileBean>();
		                		FTPFileBean ftpBean = new FTPFileBean();
		                		ftpBean.setFileName(file.getName());
		                		ftpBean.setDataLineText(dataLineTextArr);
		                		ftpBean.setFileSize(FileUtils.byteCountToDisplaySize(file.getSize()));
		                		ftpBean.setFileCount(Utils.isNull(dataStreamStr).equals("")?0:dataLineTextArr.length-1);
		                		outStreamList.add(ftpBean);
		                		tableBean.setDataLineList(outStreamList);
		                	}else{
		                		FTPFileBean ftpBean = new FTPFileBean();
		                		ftpBean.setFileName(file.getName());
		                		ftpBean.setDataLineText(dataLineTextArr);
		                		ftpBean.setFileSize(FileUtils.byteCountToDisplaySize(file.getSize()));
		                		ftpBean.setFileCount(Utils.isNull(dataStreamStr).equals("")?0:dataLineTextArr.length-1);
		                		tableBean.getDataLineList().add(ftpBean);
		                	}
		                	
		                	/** put to Map Main **/
		                	controlTableMap.put(tableBean.getTableName(), tableBean);
		                	
		                	//out.flush();
		                	//out.close();
	                	}//if
    				}//for 2
                }//for 1
			}
			
			logger.debug("End Load FTP File Total Add To Map:"+countFileMap);

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
	
	/**
	 * 
	 * @param controlTableMap
	 * @param path
	 * @param userBean
	 * @return
	 * @throws Exception
	 */
	public List<FTPFileBean> downloadFileFromFTP(User user,String path) throws Exception
	{
		FTPClient ftp = null;
		String ftpResponse = "";
		int i = 0;
		int countFileMap = 0;
		List<FTPFileBean> ftpFileBeanList = new ArrayList<FTPFileBean>();
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
				
                for(i=0;i<ftpFiles.length;i++){
                	FTPFile file = (FTPFile)ftpFiles[i];
                	logger.debug("FTP fileName:"+file.getName());
               
                    String ftpFileName = Utils.isNull(file.getName());
                    
					if(!file.getName().equals(".") && !file.getName().equals("..")){
					
						/** 2 **/
	                	String dataStreamStr = convertStreamToString(ftp.retrieveFileStream(ftpFileName));
	                	ftp.completePendingCommand();
	                	//logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);
	                	
		                /** Store DataStream in TableBean **/
		               String[] dataLineTextArr =  new String(dataStreamStr).trim().split(Constants.newLine);
		               
		               FTPFileBean ftpBean = new FTPFileBean();
	               	   ftpBean.setFileName(file.getName());
	               	   ftpBean.setDataLineText(dataLineTextArr);
	               	   ftpBean.setFileSize(FileUtils.byteCountToDisplaySize(file.getSize()));
	               	   ftpBean.setFileCount(Utils.isNull(dataStreamStr).equals("")?0:dataLineTextArr.length);
	               	   
	               	   ftpFileBeanList.add(ftpBean);	
					}
                }//for 1
			}//if 1
			
			logger.debug("End init FTP File Total Add To Map:"+countFileMap);

			return ftpFileBeanList;
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
	
	public static String convertStreamToString(InputStream is) throws IOException {
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
	/**
	 * moveFileFTP 
	 * @param source
	 * @param destination
	 * @param controlTableMap
	 * @throws Exception
	 * Step Move file and Delete from Source File
	 */
	public void moveFileFTP_NEW(String source ,String destination ,List fileImportSuccessList) throws Exception{
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
	
	public void deleteFileFTPByFileName(String path,String fullFileName) throws Exception{
		FTPClient ftp = null;
		try {	
			logger.debug("Delete fullFileName:"+fullFileName);
			
			ftp = new FTPClient();
			ftp.connect(server);
			ftp.enterLocalPassiveMode();
			
			if(!ftp.login(userFtp, passwordFtp)){
				throw new FTPException("FTP Username or password invalid! ");
			}
			//Step1 set Dir Active
			ftp.changeWorkingDirectory(path);
			logger.debug("Step1 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
			
			//step 2 
			ftp.deleteFile(fullFileName);
			logger.debug("delete:"+fullFileName);
			logger.debug("step2 FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
			
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

		/** Process Work on Window 8(tablet not work V107(tablet) **/
		public void uploadAllFileToFTP_OPT2_BY_FILE(String path,String pathFull,StringBuffer data) throws Exception{
			FTPClient ftp = null;
			String reply = "";
			Writer w = null;
			OutputStream out = null;
			logger.info("uploadAllFileToFTP_OPT2_BY_FILE");
			try {		
				ftp = new FTPClient();
				ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
				ftp.connect(server);
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				
				//ftp.setConnectTimeout(connectTimeout);

				//logger.info("connection timeout:"+ftp.getConnectTimeout());
				
				if(!ftp.login(userFtp, passwordFtp)){
					throw new FTPException("FTP Username or password invalid! ");
				}
	
				if(data != null && data.toString().length() >0){		
						logger.info("Path:"+path);
						
						//ftp.changeWorkingDirectory(path);
						//logger.info("changeWorkingDirectory FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 
						
						logger.info("Write To pathFull:"+pathFull);
						out = ftp.storeFileStream(pathFull);
					    logger.info("Write fileName FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					    
					    /** new Code ******************************************************/
					    logger.info("Start Write OutputStreamWriter data:");
					    w = new BufferedWriter(new OutputStreamWriter(out,Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620)); 
					    w.write(data.toString());
					    w.flush();
					    w.close();
					    
					    logger.info("End Write OutputStreamWriter data:");
					    
					    out.flush();
					    out.close();
						/******************************************************************/
				         
					    //Test Write File Local
					  /*  String fileTemp = "D:/temp/"+tableBean.getFileFtpNameFull();
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
						    ftp.sendSiteCommand("CHMOD 777 "+pathFull);
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
				e.printStackTrace();
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
		
		public void uploadFileToFTP(String pathFull,StringBuffer dataStr) throws Exception{
			FTPClient ftp = null;
			String reply = "";
			Writer w = null;
			OutputStream out = null;
			logger.info("uploadAllFileToFTP_OPT2_BY_FILE");
			try {		
				ftp = new FTPClient();
				ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
				ftp.connect(server);
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				
				if(!ftp.login(userFtp, passwordFtp)){
					throw new FTPException("FTP Username or password invalid! ");
				}
	
				if(dataStr != null && dataStr.toString().length() >0){		
						logger.info("Write To pathFull:"+pathFull);
						out = ftp.storeFileStream(pathFull);
					    logger.info("Write fileName FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
					    
					    /** new Code ******************************************************/
					    logger.info("Start Write OutputStreamWriter data:");
					    w = new BufferedWriter(new OutputStreamWriter(out,Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620)); 
					    w.write(dataStr.toString());
					    w.flush();
					    w.close();
					    
					    logger.info("End Write OutputStreamWriter data:");
					    
					    out.flush();
					    out.close();
						
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
						    ftp.sendSiteCommand("CHMOD 777 "+pathFull);
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
				e.printStackTrace();
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
		
		public void uploadExcelByFTP(String localTempPath,String path,String pathFtpFull,HSSFWorkbook xssfWorkbook) throws Exception{
			FTPClient ftp = null;
			String reply = "";
			logger.info("uploadExcelByFTP");
			try {		
				ftp = new FTPClient();
				ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
				ftp.connect(server);
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftp.enterLocalPassiveMode();
				 
				//ftp.setConnectTimeout(connectTimeout);

				//logger.info("connection timeout:"+ftp.getConnectTimeout());
				
				if(!ftp.login(userFtp, passwordFtp)){
					throw new FTPException("FTP Username or password invalid! ");
				}
				 
				if(xssfWorkbook != null ){		
					logger.info("Path:"+path);
					logger.info("Write To pathFtpFull:"+pathFtpFull);
					
					ftp.changeWorkingDirectory(path);
					logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

					  // APPROACH #2: uploads second file using an OutputStream
		            File localFile = new File(localTempPath);
		            InputStream inputStream = new FileInputStream(localFile);
		 
		            System.out.println("Start uploading second file");
		            OutputStream outputStream = ftp.storeFileStream(pathFtpFull);
		            byte[] bytesIn = new byte[4096];
		            int read = 0;
		 
		            while ((read = inputStream.read(bytesIn)) != -1) {
		                outputStream.write(bytesIn, 0, read);
		            }
		            inputStream.close();
		            outputStream.close();
		            
		            logger.info("StoreFile To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 
		            
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
		
		public void uploadExcelByFTP_Method2(String localTempPath,String path,String pathFull,XSSFWorkbook xssfWorkbook) throws Exception{
			FTPClient ftp = null;
			String reply = "";
			OutputStream out = null;
			logger.info("uploadExcelByFTP");
			try {		
				ftp = new FTPClient();
				ftp.setControlEncoding(Constants.FTP_ENCODING_UTF_8);
				ftp.connect(server);
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				
				//ftp.setConnectTimeout(connectTimeout);

				//logger.info("connection timeout:"+ftp.getConnectTimeout());
				
				if(!ftp.login(userFtp, passwordFtp)){
					throw new FTPException("FTP Username or password invalid! ");
				}
				 
				if(xssfWorkbook != null ){		
					logger.info("Path:"+path);
					logger.info("Write To pathFull:"+pathFull);
					
					ftp.changeWorkingDirectory(path);
					logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

					InputStream input;
		            input = new FileInputStream(localTempPath);
		            //store the file in the remote server
		            ftp.storeFile(pathFull, input);
		            //close the stream
		            input.close();

				    /** Close Command FTP **/
				    try{
					    //logger.info(" Start completePendingCommand ");
						//boolean completed = ftp.completePendingCommand();
						//String ftpResponse = ftp.getReplyString();
						//logger.info("ftpResponse["+ftpResponse+"]");
					   // logger.info(" End completePendingCommand completed["+completed+"]FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString());
				    }catch(Exception e){
				        logger.error(e.getMessage(),e);
				    }
				    
				    //SITE CHMOD 777 201011101437-SALESREP.txt
				    /** Set Permission File ***/
				    try{
					    logger.info(" Start CHMOD 777 ");
					   // ftp.sendSiteCommand("CHMOD 777 "+pathFull);
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
		public void uploadExcelByFTP_Method1(String path,String pathFull,HSSFWorkbook xssfWorkbook) throws Exception{
			FTPClient ftp = null;
			String reply = "";
			OutputStream out = null;
			logger.info("uploadExcelByFTP");
			try {		
				ftp = new FTPClient();
				//ftp.setControlEncoding(Constants.FTP_EXPORT_TO_ORACLE_ENCODING_TIS_620);
				ftp.connect(server);
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				
				//ftp.setConnectTimeout(connectTimeout);

				//logger.info("connection timeout:"+ftp.getConnectTimeout());
				
				if(!ftp.login(userFtp, passwordFtp)){
					throw new FTPException("FTP Username or password invalid! ");
				}
				 
				if(xssfWorkbook != null ){		
						logger.info("Path:"+path);
						logger.info("Write To pathFull:"+pathFull);
						
						ftp.changeWorkingDirectory(path);
						logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

						/*InputStream input;
			            input = new FileInputStream(localPathFull);
			            //store the file in the remote server
			            ftp.storeFile(pathFull, input);
			            //close the stream
			            input.close();*/
						
						 /** new Code ******************************************************/
					    logger.info("Start Write OutputStreamWriter data:");
					    
					    out = ftp.storeFileStream(pathFull);
					   
					    xssfWorkbook.write(out);
					    
					    logger.info("End Write OutputStreamWriter data:");
					    
					    out.flush();
					    out.close();

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
						    ftp.sendSiteCommand("CHMOD 777 "+pathFull);
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
		
		public void uploadFileFromLocal(String ftpFilePath,String localFile) throws Exception{
			
			DataOutputStream dos = null;
			sun.net.TelnetOutputStream tos = null;
			FileInputStream fos = null;
			sun.net.ftp.FtpClient ftp = null;
			try{
				ftp = new sun.net.ftp.FtpClient(server, 21);
			    ftp.login(userFtp, passwordFtp);
			    ftp.binary();
			   // ftp.ascii();
			    
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
		
}
