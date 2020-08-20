package com.pens.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.batchwork.DownloadWorker;
import com.sun.org.apache.bcel.internal.generic.ISTORE;

public class AppversionVerify {
	private static AppversionVerify app ;
	protected static Logger logger = Logger.getLogger("PENS");
	public static Map<String,String> initAllMap = null;
	static String initPathFileDropbox = "https://www.dropbox.com/s/0i7ibswl3qw2s4w/initPathFile.txt?dl=1";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	public static AppversionVerify getApp(){
		logger.debug("AppVerify:"+app);
		if(app ==null){
		   app = new AppversionVerify();
		   if(initAllMap==null){
				//initAllMap = getInitAllToMapFromDB();
			}
		}else{
			if(initAllMap==null){
				//initAllMap = getInitAllToMapFromDB();
			}
		}
		return app;
	}
	
	public static void initAppVersion(){
		logger.info("initAppVersion By LoginAction");
		Connection conn = null;
		try{
		   conn = DBConnection.getInstance().getConnection();
		   // Check 1 time for day and get Config From Dropbox and insert to DB
		   boolean isToDayCheck = isTodayCheck(conn);
		   logger.info("isToDayCheck:"+isToDayCheck +",No initAppVersion");
		   if(isToDayCheck==false){
			  /* String urlTestInternetConnection = "https://www.google.co.th";
			   boolean isInternetConnect = Utils.isInternetConnect(urlTestInternetConnection);
			   logger.info("is internet is connected["+isInternetConnect+"]");*/
			   if(true){
				   if(initAllMap==null){
					   initAllMap =  new HashMap<String, String>();
				   }
			      initAllMap = getInitAllToMapToDB(conn);
			  }
		   }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception ee){}
		}
	
	}
	
	public static boolean isTodayCheck(Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		boolean isTodayeCheck = false;
		try {
			String sql = " select count(*) as c from c_control_salesapp_version  \n";
			       sql+= " where Date(updated) = Date(sysdate()) \n";

				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql);
				
				if(rst.next()){
					if(rst.getInt("c") >0){
						isTodayeCheck = true;
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
				} catch (Exception e2) {}
				try {
					stmt.close();
				} catch (Exception e2) {}
			}
		return isTodayeCheck;
	}
	
	/** process run mainpage.jsp footer.jsp */
	 public String[] checkAppVersion(HttpServletRequest request){
		logger.info("checkAppVersion");
		String[] msg = new String[2];
		Connection conn = null;
		try{
			logger.debug("request:"+request.getSession());
			if(request.getSession().getAttribute("appVersionCheckMsg") == null){
				//get from DB 
				if(initAllMap==null){
					conn = DBConnection.getInstance().getConnection();
					initAllMap =  new HashMap<String, String>();
				    initAllMap = getInitAllToMapFromDB(conn);
				}
				   
				String appVersion = SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
				//Temp Version No Check (Credit only)
				if(appVersion.indexOf("Credit") != -1 || appVersion.indexOf("Van") != -1 ){
					msg[0] = "";
					msg[1] = "";
				}else{
				   
				    //get Latest version from Server
					String appVersionLatestInServer = Utils.isNull(initAllMap.get("Lastest-app-version"));
					
					logger.debug("appVersionLatestInServer :"+appVersionLatestInServer);
					logger.debug("CurrentAppVersion :"+appVersion);
					
					if( !"".equals(appVersionLatestInServer) && !appVersion.equalsIgnoreCase(appVersionLatestInServer)){
						logger.debug("AppVersion Not match");
						//appVersion not match
						msg[0] =  getAppVersionMessageToSales(request);
						       
						msg[1] = "";
							
					}else{
						msg[0] = "";
						msg[1] = "";
					}
			     }
				request.getSession().setAttribute("appVersionCheckMsg",msg);
				
				logger.debug("new msg :"+msg[0]+":"+msg[1]);
			}else{
				msg = (String[])request.getSession().getAttribute("appVersionCheckMsg");
				logger.debug("old msg :"+msg[0]+":"+msg[1]);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			request.getSession().setAttribute("appVersionCheckMsg","e");
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
			}catch(Exception ee){}
		}
		return msg;
	}
  
	public static AppversionVerify clearIns(){
		app = new AppversionVerify();
		return app;
	}
	
	  /** Download Software After Import  separate Thread **/
    public static void downloadSoftware(User user){
    	logger.info("downloadSoftware");
    	
    	AppversionVerify.clearIns();
    	
    	AppversionVerify.getApp().downloadSalesAppUpdater(true);
    	
    	AppversionVerify.getApp().downloadSoftware4SalesApp();
    	
    	AppversionVerify.getApp().downloadC4SalesApp(user);
    	
    	AppversionVerify.getApp().downloadPlanSalesApp(user);
    	
    	// Start batch Download Pensclient.war from dropbox
		//new DownloadSalesAppWorker().start();
    }
    
    public static Map<String,String> getInitAllToMapToDB(){
    	Connection conn = null;
    	Map<String,String> dataMap = null;
    	try{
    		conn = DBConnection.getInstance().getConnection();
    		dataMap= getInitAllToMapToDBModel(conn);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		try{
    		  conn.close();
    		}catch(Exception ee){}
    	}
        return dataMap;
    }
    public static Map<String,String> getInitAllToMapToDB(Connection conn){
    	return getInitAllToMapToDBModel(conn);
    }
    
	 public static Map<String,String> getInitAllToMapToDBModel(Connection conn){
	        Map<String,String> pathFileMap = new HashMap<String, String>();
	        logger.info("***Start getInitPathFile To DB***");
	        try{                   
	            URL url = new URL(initPathFileDropbox);
	            url.openConnection();
	            InputStream inStream = url.openStream();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,"TIS-620"));
	            String line = null;
	            String[] lineArr = null;
	            try {
	                while ((line = reader.readLine()) != null) {
	                    //System.out.println(line);
	                   lineArr = line.split("\\,");
	                  // logger.info(lineArr[0]);
	                   insertControlSalesAppVersion(conn, Utils.isNull(lineArr[0]), Utils.isNull(lineArr[1]));
	                   pathFileMap.put(Utils.isNull(lineArr[0]), Utils.isNull(lineArr[1]));
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    inStream.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	        return pathFileMap;
	  }
	 public static Map<String,String> getInitAllToMapFromDB(){
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getInitAllToMapFromDB(conn);
		 }catch(Exception e){
			 logger.error(e.getMessage(),e);
		 }finally{
			 try{
				 if(conn != null){
					 conn.close();
				 }
			 }catch(Exception ee){}
		 }
		return null;
	 }
	 public static Map<String,String> getInitAllToMapFromDB(Connection conn){
        Map<String,String> pathFileMap = new HashMap<String, String>();
    	Statement stmt = null;
    	ResultSet rs = null;
    	String sql = "";
        logger.info("***Start getInitPathFile From DataBase***");
        try{                   
        	sql = "select config_type ,value from c_control_salesapp_version ";
        	stmt = conn.createStatement();
        	rs = stmt.executeQuery(sql);
            while (rs.next()) {
                 insertControlSalesAppVersion(conn, Utils.isNull(rs.getString("config_type")), Utils.isNull(rs.getString("value")));
                 pathFileMap.put(Utils.isNull(rs.getString("config_type")), Utils.isNull(rs.getString("value")));
              }
        }catch(Exception e){
            e.printStackTrace();
        }
        return pathFileMap;
	  }
	 
	 public static void insertControlSalesAppVersion(Connection conn,String configType,String value) throws Exception {
			Statement stmt = null;
			try {
				String sql = "update c_control_salesapp_version set value ='"+value+"' , updated = sysdate() \n";
				       sql += "where config_type ='"+configType+"' \n";
					//logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				int r = stmt.executeUpdate(sql);
				if(r ==0){
					sql = "insert into c_control_salesapp_version values('"+configType+"','"+value+"',sysdate()) \n";
					//logger.debug("sql:"+sql);
					stmt = conn.createStatement();
				    r = stmt.executeUpdate(sql);
				}
		
			} catch (Exception e) {
				throw e;
			} finally {
			
				try {
					stmt.close();
				} catch (Exception e2) {}
			}
		}
	 
	//Process Run After Import:
	public static void processAfterImport(User user){
		try{
			String currentAppVersion = SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
				
			String localSalesAppPath = getLocalPathSalesApp();

			//Write Current AppVersion to Local
			FileUtil.writeFile(localSalesAppPath+"current-app-version.txt", currentAppVersion, "UTF-8");
		
			//** Download Software for Tablet sales **/
			new DownloadWorker(user).start();
			
			
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/** Process run mainpage.jsp */
	public static String getMessageToSales(HttpServletRequest request){
		String msg = "";
		try{
			if(request.getSession().getAttribute("massageToSales") == null){
				msg = Utils.isNull(initAllMap.get("message-to-sales"));
				request.getSession().setAttribute("massageToSales",msg);
			}else{
				msg = Utils.isNull(request.getSession().getAttribute("massageToSales"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
			request.getSession().setAttribute("massageToSales","e");
		}
		return msg;
	}
	
	// Get From Local_file (msg from ftp server run after import )
	public static String getAppVersionMessageToSales(HttpServletRequest request){
		String msg = "";
		try{
			if(request.getSession().getAttribute("appVersionMassageToSales") == null){
				logger.debug("Session :"+request.getSession().getAttribute("appVersionMassageToSales"));
				msg = Utils.isNull(initAllMap.get("appversion-message-to-sales"));
				logger.debug("msg:"+msg);
				request.getSession().setAttribute("appVersionMassageToSales",msg);
			}else{
				msg = Utils.isNull(request.getSession().getAttribute("appVersionMassageToSales"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
			request.getSession().setAttribute("appVersionMassageToSales","e");
		}
		return msg;
	}
	 
    /** Process run SalesAppUpdater.jsp by User press UpdateSalesApp button**/
    public static void startSalesAppUpdater(){
        String line;
        OutputStream stdin = null;
        InputStream stderr = null;
        InputStream stdout = null;
        try{
            System.out.println("startSalesAppUpdater");
            //Runtime.getRuntime().exec("cmd ");
            String command = "D:/SalesApp/SalesAppUpdater/startSalesAppUpdater.bat";
            Process process = Runtime.getRuntime().exec(command);
            
            //System.out.println("startTomcat:"+child.exitValue());
            stdin = process.getOutputStream ();
            stderr = process.getErrorStream ();
            stdout = process.getInputStream ();

            // "write" the parms into stdin
            line = "param1" + "\n";
            stdin.write(line.getBytes() );
            stdin.flush();

            line = "param2" + "\n";
            stdin.write(line.getBytes() );
            stdin.flush();

            line = "param3" + "\n";
            stdin.write(line.getBytes() );
            stdin.flush();

           // System.out.println("stdin:"+stdin.toString());
            stdin.close();

            // clean up if any output in stdout
            BufferedReader brCleanUp = new BufferedReader (new InputStreamReader(stdout));
            while ((line = brCleanUp.readLine ()) != null) {
              System.out.println ("[Stdout] " + line);
            }
            brCleanUp.close();

            // clean up if any output in stderr
            brCleanUp =new BufferedReader (new InputStreamReader (stderr));
            while ((line = brCleanUp.readLine ()) != null) {
              System.out.println ("[Stderr] " + line);
            }
            brCleanUp.close();
        }catch(Exception e){
            e.printStackTrace();
        }
   }
	
    
    /** Software For Sales App **/
    public  void downloadSoftware4SalesApp(){
		String localSalesAppPath = getLocalPathSalesApp();
		if(initAllMap != null){
			String sourcePath =  initAllMap.get("Software4SalesApp.zip");
			String sourcePath2 =  initAllMap.get("Software4SalesApp.zip");
	        String destPath  = localSalesAppPath+"Software4SalesApp.zip";
	        String dest2Path = localSalesAppPath+"Software4SalesApp";
			try{
				                                         
				boolean isLatestVersion = isLatestVersion("Software4SalesApp");
				logger.info("isLatestVersion:"+isLatestVersion);
				if( !isLatestVersion){
					logger.info("download Software4SalesApp");
					
					String logs = "\n           Download File From http...From("+sourcePath+") to ("+destPath+")";
					System.out.println(logs);
					URL url = null;
		            InputStream reader = null;
		            try{
			            url = new URL(sourcePath);
			            url.openConnection();
			            reader = url.openStream();
		            }catch(Exception e){
		            	logger.error("Error Source1 retry Source2");
		            	url = new URL(sourcePath2);
			            url.openConnection();
			            reader = url.openStream();
		            }
		
		            FileOutputStream writer = new FileOutputStream(destPath);
		            byte[] buffer = new byte[1024];
		            //int totalBytesRead = 0;
		            int bytesRead = 0;
		            while ((bytesRead = reader.read(buffer)) > 0){  
		              writer.write(buffer, 0, bytesRead);
		              //buffer = new byte[153600];
		            }
		            writer.close();
		            reader.close();
		            
		            FileUtils.deleteDirectory(new File(dest2Path));
		            
		            unzipFileIntoDirectory(destPath,dest2Path);
		            
		          //delete Zip file
		            //logger.info("delete :"+destPath);
		           // FileUtil.deleteFile(destPath);
				}else{
					logger.info("Software4SalesApp is no update");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
    
    public  void downloadC4SalesApp(User user){
    	String localSalesAppPath = getLocalPathPensDocument();
    	try{
    		 //Get Old Version 
			String localVersion = Utils.isNull(FileUtil.readFile(localSalesAppPath+"C4-version.txt", "UTF-8"));
			
    		boolean isLatestVersion = isLatestVersionC4("C4");
    		if(user.getType().equalsIgnoreCase(User.TT)){
    			//Get Credit
        		getC4SalesApp(localSalesAppPath,isLatestVersion,"CREDIT",localVersion);
    			
    		}else{
    			//Get VAN
        		getC4SalesApp(localSalesAppPath,isLatestVersion,"VAN",localVersion);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void downloadPlanSalesApp(User user){
    	String localSalesAppPath = getLocalPathPensDocument();
    	String fileName = "";
    	try{
    		 //Get Old Version 
			String localVersion = Utils.isNull(FileUtil.readFile(localSalesAppPath+"plan-version.txt", "UTF-8"));
			
    		boolean isLatestVersion = false;
    		if(user.getType().equalsIgnoreCase(User.TT)){
    		   fileName ="CREDIT_TALK";
    		   isLatestVersion = isLatestVersionPlan("plan");
    		   
    		   //Get Credit
       		   getPlanSalesApp(localSalesAppPath,isLatestVersion,"CREDIT",localVersion,fileName);
       		   
    		}else{
    		   fileName ="VAN_TALK";
    		   isLatestVersion = isLatestVersionPlan("plan");
    		   
    			//Get VAN
       		   getPlanSalesApp(localSalesAppPath,isLatestVersion,"VAN",localVersion,fileName);
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void getC4SalesApp(String localSalesAppPath,boolean isLatestVersion,String userType,String localVersion){
		
		String sourcePath =  initAllMap.get("C4-"+userType+".pdf");
		String sourcePath2 =  initAllMap.get("C4-"+userType+".pdf");
        String destPath  = localSalesAppPath+"C4-"+userType+".pdf";
		try{ 
			logger.info("Start Process Download C4 "+userType);
           
			if( !isLatestVersion){
				logger.info("*** Start download C4.pdf ***");
				if( !Utils.isNull(localVersion).equals("")){
					/*String backupPath  = localSalesAppPath+"C4-"+userType+"-"+localVersion+".pdf";
					
					logger.info(" Copy Old File to BK File Path:"+backupPath);
					
					InputStream reader1 = new FileInputStream(destPath);
		            FileOutputStream writer1 = new FileOutputStream(backupPath);
		            byte[] buffer1 = new byte[1024];
		            //int totalBytesRead = 0;
		            int bytesRead1 = 0;
		            while ((bytesRead1 = reader1.read(buffer1)) > 0){  
		              writer1.write(buffer1, 0, bytesRead1);
		              //buffer = new byte[153600];
		            }
		            writer1.close();
		            reader1.close();*/
				}
				/********************************************************/
				logger.info("Download File From http...From("+sourcePath+") to ("+destPath+")");
				URL url = null;
	            InputStream reader = null;
	            try{
		            url = new URL(sourcePath);
		            url.openConnection();
		            reader = url.openStream();
	            }catch(Exception e){
	            	logger.error("Error Source1 retry Source2");
	            	url = new URL(sourcePath2);
		            url.openConnection();
		            reader = url.openStream();
	            }
	
	            /******************************************************/
	        	logger.info("Write File to Local ");  
	            FileOutputStream writer = new FileOutputStream(destPath);
	            byte[] buffer = new byte[1024];
	            //int totalBytesRead = 0;
	            int bytesRead = 0;
	            while ((bytesRead = reader.read(buffer)) > 0){  
	              writer.write(buffer, 0, bytesRead);
	              //buffer = new byte[153600];
	            }
	            writer.close();
	            reader.close();
	            
			}else{
				logger.info("C4 is no update");
			}
			
			logger.info("*** End Process download C4.pdf ***");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    
public static void getPlanSalesApp(String localSalesAppPath,boolean isLatestVersion,String userType,String localVersion,String fileName){
		
		String sourcePath =  initAllMap.get(fileName);
        String destPath  = "";
        if("CREDIT".equalsIgnoreCase(userType)){
        	destPath  = localSalesAppPath+"เอกสารแผนงาน ของ Credit.pdf";
        }else if("VAN".equalsIgnoreCase(userType)){
        	destPath  = localSalesAppPath+"เอกสารแผนงาน ของ Van.pdf";
        }
		try{ 
			logger.info("Start Process Download plan "+userType);
           
			if( !isLatestVersion){
				logger.info("*** Start download plan.pdf ***");
				if( !Utils.isNull(localVersion).equals("")){
					/*String backupPath  = localSalesAppPath+"C4-"+userType+"-"+localVersion+".pdf";
					
					logger.info(" Copy Old File to BK File Path:"+backupPath);
					
					InputStream reader1 = new FileInputStream(destPath);
		            FileOutputStream writer1 = new FileOutputStream(backupPath);
		            byte[] buffer1 = new byte[1024];
		            //int totalBytesRead = 0;
		            int bytesRead1 = 0;
		            while ((bytesRead1 = reader1.read(buffer1)) > 0){  
		              writer1.write(buffer1, 0, bytesRead1);
		              //buffer = new byte[153600];
		            }
		            writer1.close();
		            reader1.close();*/
				}
				/********************************************************/
				logger.info("Download File From http...From("+sourcePath+") to ("+destPath+")");
				URL url = null;
	            InputStream reader = null;
	            try{
		            url = new URL(sourcePath);
		            url.openConnection();
		            reader = url.openStream();
	            }catch(Exception e){
	            	logger.error("Error Source1 retry Source2");
	            	url = new URL(sourcePath);
		            url.openConnection();
		            reader = url.openStream();
	            }
	
	            /******************************************************/
	        	logger.info("Write File to Local plan-version.txt:"+destPath);  
	            FileOutputStream writer = new FileOutputStream(destPath);
	            byte[] buffer = new byte[1024];
	            //int totalBytesRead = 0;
	            int bytesRead = 0;
	            while ((bytesRead = reader.read(buffer)) > 0){  
	              writer.write(buffer, 0, bytesRead);
	              //buffer = new byte[153600];
	            }
	            writer.close();
	            reader.close();
	            
			}else{
				logger.info("Plan is no update");
			}
			
			logger.info("*** End Process download Plan.pdf ***");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
   
    
    /** Software SalesAppUpdater.jar **/
	public  void downloadSalesAppUpdater(boolean checkVersion){
		if(initAllMap != null){
			String localSalesAppPath = getLocalPathSalesApp();
			String sourcePath  =  initAllMap.get("SalesAppUpdater.zip");
			String sourcePath2 =  initAllMap.get("SalesAppUpdater.zip");
	        String destPath  = localSalesAppPath+"SalesAppUpdater.zip";
	        String dest2Path = localSalesAppPath+"SalesAppUpdater";
			try{
				boolean canDownload = false;
				if(checkVersion == true){
					boolean isLatestVersion = isLatestVersion("SalesAppUpdater");
					logger.info("isLatestVersion:"+isLatestVersion);
					
					if(!isLatestVersion){
						canDownload = true;
					}else{
						canDownload = false;
					}
					
				}else{
					canDownload = true;
				}
				
				if(canDownload){
					logger.info("download SalesAppUpdater");
					
					String logs = "\n           Download File From http...From("+sourcePath+") to ("+destPath+")";
					System.out.println(logs);
					URL url = null;
		            InputStream reader = null;
		            try{
			            url = new URL(sourcePath);
			            url.openConnection();
			            reader = url.openStream();
		            }catch(Exception e){
		            	logger.error("Error Source1 retry Source2");
		            	url = new URL(sourcePath2);
			            url.openConnection();
			            reader = url.openStream();
		            }
		            
		            FileOutputStream writer = new FileOutputStream(destPath);
		            byte[] buffer = new byte[1024];
		            //int totalBytesRead = 0;
		            int bytesRead = 0;
		            while ((bytesRead = reader.read(buffer)) > 0){  
		              writer.write(buffer, 0, bytesRead);
		            //  buffer = new byte[153600];
		            }
		            writer.close();
		            reader.close();
		            
		            //delete old folder
		            FileUtils.deleteDirectory(new File(dest2Path+"SalesAppUpdater"));
		            
		            //unzip Folder to Tomcat Path
		            logs += "\n           UnZip...From("+destPath+"SalesAppUpdater.zip) to ("+dest2Path+"SalesAppUpdater)";
		           // System.out.println(logs);
		            unzipFileIntoDirectory(destPath,dest2Path);
		            
		            //delete Zip file
		           // logger.info("delete :"+destPath);
		            //FileUtil.deleteFile(destPath);
				}else{
					logger.info("SalesAppUpdater is no update");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	 /** Run As Thread: downloadSalesAppWar pensclient.war  Action run:check SaleApp version is out of date**/
		public   void downloadSalesAppWar(){
			String localSalesAppPath = getLocalPathSalesApp();
			String sourcePath  =  initAllMap.get("pensclient.war");
			String sourcePath2 =  initAllMap.get("pensclient.war");
	        String destPath  = localSalesAppPath+"pensclient.war";
	        boolean canDownload = false;
	        Map<String,String> configMap = new HashMap<String, String>();
			try{
				//check Internet connection
				String urlTestInternetConnection = "https://www.google.co.th";
				boolean isInternetConnect = Utils.isInternetConnect(urlTestInternetConnection);
				if(isInternetConnect){
					String appVersionInApp = SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
					String appVersionInServer = Utils.isNull(initAllMap.get("Lastest-app-version"));
					String downloadAppVersionLocal = Utils.isNull(FileUtil.readFile(localSalesAppPath+"download-app-version.txt", "UTF-8"));
					String forceDownloadSalesApp =  Utils.isNull(initAllMap.get("forceDownloadSalesApp"));
					
					logger.info("appVersionInApp:"+appVersionInApp);
					logger.info("appVersionInServer:"+appVersionInServer);
					logger.info("downloadAppVersionLocal:"+downloadAppVersionLocal);
					logger.info("forceDownloadSalesApp:"+forceDownloadSalesApp);//Download No check Version
					
					//Write controlDeploySalesAppAtStartWindow to Local for check deploy or not
					String controlDeploySalesAppAtStartWindow = Utils.isNull(initAllMap.get("controlDeploySalesAppAtStartWindow"));
					logger.info("Write file "+localSalesAppPath+"controlDeploySalesAppAtStartWindow.txt ["+controlDeploySalesAppAtStartWindow+"]");
					FileUtil.writeFile(localSalesAppPath+"controlDeploySalesAppAtStartWindow.txt", controlDeploySalesAppAtStartWindow);
					 
					//Control Download Or not
					String controlDownloadSalesApp = Utils.isNull(initAllMap.get("controlDownloadSalesApp"));
					logger.info("controlDownloadSalesApp :"+controlDownloadSalesApp);
					
					//salesApp out of date can download
					if( (controlDownloadSalesApp.equalsIgnoreCase("true")
						&&	!Utils.isNull(appVersionInApp).equals(Utils.isNull(appVersionInServer))
						&&	!Utils.isNull(downloadAppVersionLocal).equals(Utils.isNull(appVersionInServer)) //No Download again
					  ) 
				      ||
				      (forceDownloadSalesApp.equalsIgnoreCase("true"))
					){
						  if(isInternetConnect){
							  canDownload = true;
							  configMap = getConfigInstallFromDropbox();
							  logger.info("installType:"+configMap.get("installType"));
							  if(configMap.get("installType").equalsIgnoreCase("update")){
									sourcePath  =  initAllMap.get("pensclient.zip");
									sourcePath2 =  initAllMap.get("pensclient.zip");
									destPath  = localSalesAppPath+"pensclient.zip";
							  }
						  }
					}
				
					if(canDownload){
						logger.info("start download pensclient.war ");
						
						String logs = "\n  Download File From http...From("+sourcePath+") to ("+destPath+")";
						System.out.println(logs);
						URL url = null;
			            InputStream reader = null;
			            try{
				            url = new URL(sourcePath);
				            url.openConnection();
				            reader = url.openStream();
			            }catch(Exception e){
			            	logger.error("Error Source1 retry Source2");
			            	url = new URL(sourcePath2);
				            url.openConnection();
				            reader = url.openStream();
			            }
			            
			            FileOutputStream writer = new FileOutputStream(destPath);
			            byte[] buffer = new byte[1024];
			            //int totalBytesRead = 0;
			            int bytesRead = 0;
			            while ((bytesRead = reader.read(buffer)) > 0){  
			              writer.write(buffer, 0, bytesRead);
			            //  buffer = new byte[153600];
			            }
			            writer.close();
			            reader.close();
			           
			            // write file install.txt
			            FileUtil.writeFile(localSalesAppPath+"install.txt", "runinstall");
			            
			            //Write Download AppVersion for check No download again
						FileUtil.writeFile(localSalesAppPath+"download-app-version.txt", appVersionInServer, "UTF-8");
						
			            logger.info("end download pensclient.war ");
			            
			            //Delete Tomcat Shortcut in Startup
			            //deleteTomcatShortcutAtStartup();
			            
					}else{
						logger.info("SalesAppWar is no update");
					}
				}//if check Internet connection
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public static void deleteTomcatShortcutAtStartup(){
	        try{
	            //Check tomcat.exe found in startup
	           String pathStartup = System.getProperty("user.home") ;
	           logger.debug("pathStartup:"+pathStartup);
	           String pathToDelete = pathStartup +"\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\tomcat6.exe";
	           logger.info("pathToDeleteTomcatShutcutInStartup: \n"+pathToDelete);
	  
	           File fDelete = new File(pathToDelete);
	           
	           logger.info("result delete:"+fDelete.delete());
	          
	        }catch(Exception e){
	            logger.error(e.getMessage(),e);
	        }
		}
	
		 private Map<String,String> getConfigInstallFromDropbox(){
			 String localSalesAppPath = getLocalPathSalesApp();
			 Map<String,String> configMap = new HashMap<String, String>();
		        try{
		            String configStrLine = initAllMap.get("config-install");
		            if( !Utils.isNull(configStrLine).equalsIgnoreCase("")){
	                    String[] dataArray = configStrLine.split("\\|");
	                    System.out.println("config_data array["+dataArray.length+"]");
	                    String installType = dataArray[0].substring(dataArray[0].indexOf("=")+1,dataArray[0].length());
	                    String installSource = dataArray[1].substring(dataArray[1].indexOf("=")+1,dataArray[1].length());
	                    String installSourcePath = "";//despricate  dataArray[2].substring(dataArray[2].indexOf("=")+1,dataArray[2].length());
	                    String installLocal = dataArray[3].substring(dataArray[3].indexOf("=")+1,dataArray[3].length());
	                    
	                    logger.debug("installType["+installType+"]");
	                    logger.debug("installSource["+installSource+"]");
	                    logger.debug("installSourcePath["+installSourcePath+"]");
	                    logger.debug("installLocal["+installLocal+"]");
	                    
	                    configMap.put("installType", installType);
	                    configMap.put("installSource", installSource);
	                    configMap.put("installSourcePath", installSourcePath);
	                    configMap.put("installLocal", installLocal);
	                    
	                    //write config_install to local
	                    logger.info("Write file "+localSalesAppPath+"config-install.txt ["+configStrLine+"]");
						FileUtil.writeFile(localSalesAppPath+"config-install.txt", configStrLine);
		            }
		        }catch(Exception e){
		            e.printStackTrace();
		        }
		        return configMap;
		    }
	public static boolean isLatestVersion(String name){
		boolean r = true;
		try{
			String localSalesAppPath = getLocalPathSalesApp();
			String localVersion = Utils.isNull(FileUtil.readFile(localSalesAppPath+name+"-version.txt", "UTF-8"));
			String latestVersionInServer = Utils.isNull(initAllMap.get(name+"-version"));
			
			logger.info("localVersion["+localVersion+"],latestVersionInServer["+latestVersionInServer+"]");
			if( !localVersion.equalsIgnoreCase(latestVersionInServer)){ //no Latest Version
				r = false;
				logger.info("write file version:"+localSalesAppPath+name+"-version.txt");
				FileUtil.writeFile(localSalesAppPath+name+"-version.txt", latestVersionInServer, "UTF-8");
			}
		}catch(Exception e){
			e.printStackTrace();
			return true;
		}
		return r;
	}
	
	public static boolean isLatestVersionC4(String name){
		boolean r = true;
		try{
			String localSalesAppPath = getLocalPathPensDocument();
			String localVersion = Utils.isNull(FileUtil.readFile(localSalesAppPath+name+"-version.txt", "UTF-8"));
			String latestVersionInServer = Utils.isNull(initAllMap.get(name+"-version"));
			
			logger.info("localVersion["+localVersion+"],latestVersionInServer["+latestVersionInServer+"]");
			if( !localVersion.equalsIgnoreCase(latestVersionInServer)){ //no Latest Version
				r = false;
				logger.info("write file version:"+localSalesAppPath+name+"-version.txt");
				FileUtil.writeFile(localSalesAppPath+name+"-version.txt", latestVersionInServer, "UTF-8");
			}
		}catch(Exception e){
			e.printStackTrace();
			return true;
		}
		return r;
	}
	
	public static boolean isLatestVersionPlan(String name){
		boolean r = true;
		try{
			String localSalesAppPath = getLocalPathPensDocument();
			String localVersion = Utils.isNull(FileUtil.readFile(localSalesAppPath+name+"-version.txt", "UTF-8"));
			String latestVersionInServer = Utils.isNull(initAllMap.get(name+"-version"));
			
			logger.info("localVersion["+localVersion+"],latestVersionInServer["+latestVersionInServer+"]");
			if( !localVersion.equalsIgnoreCase(latestVersionInServer)){ //no Latest Version
				r = false;
				logger.info("write file version:"+localSalesAppPath+name+"-version.txt");
				FileUtil.writeFile(localSalesAppPath+name+"-version.txt", latestVersionInServer, "UTF-8");
			}
		}catch(Exception e){
			e.printStackTrace();
			return true;
		}
		return r;
	}
	
	public static void unzipFileIntoDirectory(String sourcePath,String destPath) throws Exception {
	     try{
            ZipFile zipFile = new ZipFile(sourcePath);
            Enumeration files = zipFile.entries();
            File f = null;
            FileOutputStream fos = null;
            
            while (files.hasMoreElements()) {
              try {
                ZipEntry entry = (ZipEntry) files.nextElement();
                InputStream eis = zipFile.getInputStream(entry);
                byte[] buffer = new byte[1024];
                int bytesRead = 0;

                //System.out.println("Entry Name:"+ entry.getName());
                String subFolder = entry.getName();
                subFolder = subFolder.substring(subFolder.indexOf("/"),subFolder.length());
                //System.out.println("ResultOutputPath:"+destPath + subFolder);
                 
                f = new File(destPath + subFolder);

                if (entry.isDirectory()) {
                  f.mkdirs();
                  continue;
                } else {
                  f.getParentFile().mkdirs();
                  f.createNewFile();
                }

                fos = new FileOutputStream(f);

                while ((bytesRead = eis.read(buffer)) != -1) {
                  fos.write(buffer, 0, bytesRead);
                }
              } catch (IOException e) {
                  e.printStackTrace();
                  throw e;
                  //continue;
              } finally {
                if (fos != null) {
                  try {
                    fos.close();
                  } catch (IOException e) {
                    // ignore
                  }
                }
              }
         }//while
	    }catch(Exception e){
	        e.printStackTrace();   
	        throw e;
	    }
	}
	
	/*private static String getLatestSalesVersion_OLD(String name){
        String appVersion = "";
        try{
        	String str = initAllMap.get(name);
        	logger.info("url:"+str);
            URL url = new URL(str);
            url.openConnection();
            InputStream inStream = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            StringBuilder builder = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                appVersion = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return appVersion;
	 }*/
	
	
	private  static String getLocalPathSalesApp(){
		String path = "D:/SalesApp/";
		try{
			File directory = new File(path);
			if(!directory.exists()){
				FileUtils.forceMkdir(directory);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			try{
				path = "D:/SalesApp/";
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
	
	private  static String getLocalPathPensDocument(){
		String path = "D:/Pens Document/";
		try{
			File directory = new File(path);
			if(!directory.exists()){
				FileUtils.forceMkdir(directory);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			try{
				path = "D:/Pens Document/";
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

	
}
