package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
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
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.batchwork.DownloadSalesAppWorker;
import com.isecinc.pens.inf.manager.batchwork.DownloadWorker;

public class AppversionVerify {
	private static AppversionVerify app ;
	protected static Logger logger = Logger.getLogger("PENS");
	public static Map<String,String> initAllMap = new HashMap<String, String>();
	static String initPathFileDropbox = "https://www.dropbox.com/s/0i7ibswl3qw2s4w/initPathFile.txt?dl=1";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	public static AppversionVerify getIns(){
		if(app ==null){
		   app = new AppversionVerify();
		   String urlTestInternetConnection = "https://www.google.co.th";
		   boolean isInternetConnect = Utils.isInternetConnect(urlTestInternetConnection);
		   logger.info("is internet is connected["+isInternetConnect+"]");
		   if(isInternetConnect){
		     initAllMap = getInitAllToMap();
		  }
		}
		return app;
	}
	public static AppversionVerify clearIns(){
		app = new AppversionVerify();
		return app;
	}
	
	  /** Download Software After Import  separate Thread **/
    public static void downloadSoftware(User user){
    	logger.info("downloadSoftware");
    	
    	AppversionVerify.clearIns();
    	
    	AppversionVerify.getIns().downloadSalesAppUpdater(true);
    	
    	AppversionVerify.getIns().downloadSoftware4SalesApp();
    	
    	AppversionVerify.getIns().downloadC4SalesApp(user);
    	
    	AppversionVerify.getIns().downloadPlanSalesApp(user);
    	
    	// Start batch Download Pensclient.war from dropbox
		new DownloadSalesAppWorker().start();
    }
    
	/** process run mainpage.jsp footer.jsp */
	 public String[] checkAppVersion(HttpServletRequest request){
		logger.info("checkAppVersion");
		String[] msg = new String[2];
		try{
			logger.debug("request:"+request.getSession());
			if(request.getSession().getAttribute("appVersionCheckMsg") == null){
				String appVersion = SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
				//Temp Version No Check (Credit only)
				if(appVersion.indexOf("Credit") != -1 || appVersion.indexOf("Van") != -1 ){
					msg[0] = "";
					msg[1] = "";
				}else{
				    String localSalesAppPath = getLocalPathSalesApp();
	
				    //get Latest version from local
					String appVersionLatest = Utils.isNull(FileUtil.readFile(localSalesAppPath+"Lastest-app-version.txt", "UTF-8"));
					
					logger.debug("appVersionLatest :"+appVersionLatest);
					logger.debug("CurrentAppVersion :"+appVersion);
					
					if( !"".equals(appVersionLatest) && !appVersion.equalsIgnoreCase(appVersionLatest)){
						logger.debug("AppVersion Not match");
						//appVersion not match
						msg[0] =  getAppVersionMessageToSales(request);//SystemMessages.getCaption("AppVersionNotMatch", new Locale("TH","th"));
						       
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
		}
		return msg;
	}
   
	 public static Map<String,String> getInitAllToMap(){
	        Map<String,String> pathFileMap = new HashMap<String, String>();
	        logger.info("***Start getInitPathFile***");
	        try{                   
	            URL url = new URL(initPathFileDropbox);
	            url.openConnection();
	            InputStream inStream = url.openStream();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
	            String line = null;
	            String[] lineArr = null;
	            try {
	                while ((line = reader.readLine()) != null) {
	                    //System.out.println(line);
	                   lineArr = line.split("\\,");
	                   logger.info(lineArr[0]);
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
	 
	//Process Run After Import:
	public static void processAfterImport(User user){
		EnvProperties env = EnvProperties.getInstance();
		try{
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			String currentAppVersion = SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
			
			String appVersionLatest = Utils.isNull(initAllMap.get("Lastest-app-version"));
			String msgToSales = ftpManager.getDownloadFTPFileByName("/Manual-script/message-to-sales.txt");
			String appVerionmsgToSales = ftpManager.getDownloadFTPFileByName("/Manual-script/appversion-message-to-sales.txt");
			
			logger.info("appVersionLatest :"+appVersionLatest);
			logger.info("msgToSales :"+msgToSales);
			logger.info("appVerionmsgToSales :"+appVerionmsgToSales);
			
			String localSalesAppPath = getLocalPathSalesApp();
			
			//Write AppVersion Latest
			FileUtil.writeFile(localSalesAppPath+"Lastest-app-version.txt", appVersionLatest, "UTF-8");
			
			//Write Current AppVersion
			FileUtil.writeFile(localSalesAppPath+"current-app-version.txt", currentAppVersion, "UTF-8");
			
			//Write Message To Sales
			FileUtil.writeFile(localSalesAppPath+"message-to-sales.txt", msgToSales, "UTF-8");
			
			//Write App Version Message To Sales
			FileUtil.writeFile(localSalesAppPath+"appversion-message-to-sales.txt", appVerionmsgToSales, "UTF-8");
			
			//** Download Software for netbook sales **/
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
				String localSalesAppPath = getLocalPathSalesApp();
				msg = Utils.isNull(FileUtil.readFile(localSalesAppPath+"message-to-sales.txt", "UTF-8"));
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
				String localSalesAppPath = getLocalPathSalesApp();
				msg = Utils.isNull(FileUtil.readFile(localSalesAppPath+"appversion-message-to-sales.txt", "UTF-8"));
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
				logger.info("Plan is no update");
			}
			
			logger.info("*** End Process download Plan.pdf ***");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
   
    
    /** Software SalesAppUpdater.jar **/
	public  void downloadSalesAppUpdater(boolean checkVersion){
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
