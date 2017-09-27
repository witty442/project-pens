package com.isecinc.pens.inf.manager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.FTPFileBean;
import com.isecinc.pens.inf.bean.TableBean;
import com.isecinc.pens.inf.exception.FTPException;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;

/**
 * @author WITTY
 *
 */
public class FTPManagerWacoal {

	
	private static Logger logger = Logger.getLogger("PENS"); 
	
	public static String server;
	public static String userFtp;
	public static String passwordFtp;
	public static EnvProperties env = EnvProperties.getInstance();
	
	public FTPManagerWacoal(String server,String userFtp,String passwordFtp){
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
		   FTPManagerWacoal ftpManager = new FTPManagerWacoal(env.getProperty("ftp.wacoal.ip.server"), env.getProperty("ftp.wacoal.username"), env.getProperty("ftp.wacoal.password"));
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
	
	
	public int downloadFileMappingToTableWacoal(User user,LinkedHashMap<String,TableBean> controlTableMap ,String path,String realPathTemp) throws Exception
	{
		FTPClient ftp = null;
		String ftpResponse = "";
		int i = 0;
		int countFileMap = 0;
		try {			
			ftp = new FTPClient();
			ftp.connect(server);
			//ftp.enterLocalPassiveMode();
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
    					canGetFtpFile = false;
    					String tableName = (String) it.next();
    					//logger.debug("tableName:"+tableName);
    					TableBean tableBean = (TableBean) controlTableMap.get(tableName);
                        String ftpFileName = Utils.isNull(file.getName());
                        
    					if(!ftpFileName.equals(".") && !ftpFileName.equals("..")){
    						//logger.debug("tableBean.getFileFtpName():"+tableBean.getFileFtpName()+":"+ftpFileName.toLowerCase().startsWith(tableBean.getFileFtpName()));
    						
    					   if(ftpFileName.toLowerCase().startsWith(tableBean.getFileFtpName().toLowerCase()) ){
    					       canGetFtpFile = true;
    					   }
    					}
    					
    					if(canGetFtpFile==true){
    						logger.debug("Get File Table["+tableName+"]fileFtpname["+ftpFileName+"]canGetFtpFile["+canGetFtpFile+"]");
    						
    						countFileMap++;
	                		/** Set Full File Name **/
	                		tableBean.setFileFtpNameFull(ftpFileName);
	                		//logger.debug("canGetFtpFile No["+countFileMap+"]put FtpName:"+tableBean.getFileFtpNameFull());
		                    
		                	/** 2 **/
		                	String dataStreamStr = convertStreamToString(ftp,ftpFileName,realPathTemp,path);
		                	
		                	//ftp.completePendingCommand();
		                	logger.debug("FTP Response "+ftp.getControlEncoding()+" :"+ftpResponse);

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
	
	public static String convertStreamToString(FTPClient ftpClient,String ftpFileName,String realPathTemp,String ftpPath) throws Exception {
	   String data = "";
       String pathTemp = realPathTemp+"\\temp.xls";
       try{
			logger.debug("Start Step 1 read and and write Excel to Temp Folder :"+pathTemp);
			downloadFileBySunFtp(ftpPath,ftpFileName,pathTemp);
	    	logger.debug("End Step 1 read and and write Excel to Temp Folder :");
		    try{
		    	logger.info("Start Step 2 Convert xls to Text");
		    	data = convertXlsToText(new File(pathTemp),pathTemp,ftpFileName).toString();
		    	logger.info("End Step 2 Convert xls to text");
		    }catch(Exception ee){
		    	logger.info("Case Error File Excel invalid Header try to convert to csv");
		    	pathTemp = realPathTemp+"\\temp.csv";
		    	
		    	logger.debug("Start Step 1 read and and write Excel to Temp Folder :"+pathTemp);
				downloadFileBySunFtp(ftpPath,ftpFileName,pathTemp);
		    	logger.debug("End Step 1 read and and write Excel to Temp Folder :");
		    	
		    	logger.info("Start Step 2 Convert Csv to Text");
		    	data = convertCsvToText(new File(pathTemp),pathTemp,ftpFileName).toString();
		    	logger.info("End Step 2 Convert Csv to text");
		    }
       }catch(Exception e){
    	   throw e;
       }
        return data;
    }
	
	private static StringBuffer convertXlsToText(File inputFile,String pathTempName ,String ftpFileName) throws Exception{
        // For storing data into CSV files
        StringBuffer data = new StringBuffer();
        StringBuffer dataRow = new StringBuffer();
        String rowCheck = "";
        try {
	         // Get the workbook object for XLS file
             HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(inputFile));
	          HSSFSheet sheet = workbook.getSheetAt(0);
	        
	       // OPCPackage pkg = OPCPackage.open(inputFile);
	       // XSSFWorkbook   wb2 = new XSSFWorkbook(pkg);
	        //XSSFSheet sheet = wb2.getSheetAt(0); 
			   
	        Cell cell;
	        Row row;
	
	        // Iterate through each rows from first sheet
	        Iterator<Row> rowIterator = sheet.iterator();
	        while (rowIterator.hasNext())  {
                row = rowIterator.next();
                if(row.getCell(0).getCellType() ==Cell.CELL_TYPE_NUMERIC){
                   rowCheck = String.valueOf(row.getCell(0).getNumericCellValue());
                }else{
                   rowCheck = row.getCell(0).getStringCellValue();
                }
               // logger.debug("RowNum Check:"+row.getRowNum()+":["+rowCheck+"]");
                
                if( row.getRowNum() >0){
                	 if(!rowCheck.equals("")){
		                // For each row, iterate through each columns
		                Iterator<Cell> cellIterator = row.cellIterator();
		                dataRow = new StringBuffer();
		                while (cellIterator.hasNext()){
		                        cell = cellIterator.next();
		                       // logger.debug("cellType["+cell.getCellType()+"]");
		                        switch (cell.getCellType())  {
			                        case Cell.CELL_TYPE_BOOLEAN:
			                        	 dataRow.append(cell.getBooleanCellValue() + "|");
			                             break;
			                                
			                        case Cell.CELL_TYPE_NUMERIC:
			                        	//logger.debug(cell.getNumericCellValue());
			                        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
			                        		//logger.debug("Date");
			                                dataRow.append(Utils.stringValue(cell.getDateCellValue(),Utils.DD_MM_YYYY_WITH_SLASH) + "|");
			                            }else{
			                        	    dataRow.append( Utils.convertToNumberSpecial(new BigDecimal(cell.getNumericCellValue()))+ "|");
			                            }
			                             break;
			                                
			                        case Cell.CELL_TYPE_STRING:
			                        
			                        	if(StringUtils.isNumeric(cell.getStringCellValue())){
			                        		dataRow.append( Utils.convertToNumberSpecial(new BigDecimal(cell.getStringCellValue()))+ "|");
			                        	}else{
			                        	   dataRow.append(cell.getStringCellValue() + "|");
			                        	}
			                            break;
			                            
			                        case Cell.CELL_TYPE_BLANK:
			                        	dataRow.append("" + "|");
			                            break;
			                        
			                        default:
			                        	dataRow.append(cell + "|");
			                      }  
		                }
		                logger.debug("dataRow:"+dataRow.toString());
		                
	                     data.append(ftpFileName+"|"+(row.getRowNum()+1)+"|"+dataRow.toString().substring(0,dataRow.toString().length()-1)+'\n'); 
            	  }else{
              	     break;
                  }
               }
	        }
        }catch (Exception e) {
           throw e;
        }
      return data;
  }
	
	public static StringBuffer convertCsvToText(File inputFile,String pathTempName ,String ftpFileName) throws Exception {
		StringBuffer data = new StringBuffer();
        int row =1;
	    Scanner scanner = null;
	    InputStream fis = null;
	    String lineStr = "";
		 try {
			  fis = new FileInputStream(inputFile);
			  scanner = new Scanner(fis, "TIS-620");
		      while (scanner.hasNextLine()){
		    	  lineStr = scanner.nextLine();
		    	  logger.info("lineStr:"+lineStr);
		    	  if(row <2){
		    		  logger.debug("["+row+"]["+lineStr+"]");
		    	  }
		    	  if(row > 1){
			    	  lineStr = lineStr.replace("	", "|");
			    	  data.append(ftpFileName+"|"+row+"|"+lineStr+'\n'); 
		    	  }
		          row++;
		      }
		}catch(Exception e){
			throw e;
		}finally{
		   scanner.close();
		   fis.close();
	    }
		return data;
	}
	
	
	/**
	 * moveFileFTP 
	 * @param source
	 * @param destination
	 * @param controlTableMap
	 * @throws Exception
	 * Step Move file and Delete from Source File
	 */
	public void moveFileFTP(String source ,String destination ,List fileImportSuccessList) throws Exception{
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
	
	public static void downloadFileBySunFtp(String pathFtp ,String ftpFileName,String localFile){

	    byte[] buffer = new byte[10240];
	    FileOutputStream out = null;
	    InputStream in = null;
		sun.net.ftp.FtpClient ftp = null;
		try{
			ftp = new sun.net.ftp.FtpClient(server, 21);
		    ftp.login(userFtp, passwordFtp);
		    ftp.binary();
		    ftp.cd(pathFtp);
		    
		    out = new FileOutputStream(localFile);
		    in = ftp.get(ftpFileName);
		    int counter = 0;
		     while (true) {
		        int bytes = in.read(buffer);
		        if (bytes < 0)
		          break;

		        out.write(buffer, 0, bytes);
		        counter += bytes;
		      }
		  //  logger.info("download success");
		    out.flush();
		    in.close();
		}catch(Exception e){
			e.printStackTrace();
			//logger.error(e.getMessage(),e);
		}finally{
			try{
				if(ftp != null){
					ftp.closeServer();
					ftp = null;
				}
				if(out != null){
					out.close();
				}
				if(in != null){
				   in.close();
				}
			}catch(Exception e){}
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

public void uploadExcelFileFromLocal(String path,String ftpFilePath,String localFile) throws Exception{
	FTPClient ftp = null;
	String reply = "";
	OutputStream out = null;
	logger.info("uploadExcelFileFromLocal");
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
		 
		logger.info("Path:"+path);
		logger.info("Write To pathFull:"+ftpFilePath);
		
		ftp.changeWorkingDirectory(path);
		logger.info("Write To Path FTP Response "+ftp.getControlEncoding()+" :"+ftp.getReplyString()); 

		InputStream input;
        input = new FileInputStream(localFile);
        //store the file in the remote server
        ftp.storeFile(ftpFilePath, input);
        //close the stream
        input.close();

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

public void uploadBackUpDBZipFileToFTP_OPT3(User user,String ftpFilePath,String localFile) throws Exception{
		
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
