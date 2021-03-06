package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;
import com.isecinc.pens.inf.manager.batchwork.DownloadWorker;

public class AppversionVerify {

	protected static Logger logger = Logger.getLogger("PENS");
	private static String URL_DROPBOX_ = "https://dl.dropbox.com/u/24337336/pens/BME/";
	private static String URL_DROPBOX_2 = "https=://dl.dropbox.com/u/24337336/pens/BME/";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	//Process Run After Import:
	public static void processAfterImport(){
		EnvProperties env = EnvProperties.getInstance();
		try{
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			
			String currentAppVersion = SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
			
			String appVersionLatest = Utils.isNull(getLatestSalesVersion("Lastest-app-version.txt"));
			String msgToSales = ftpManager.getDownloadFTPFileByName("/BME/Manual-script/message-to-sales.txt");
			
			logger.info("appVersionLatest :"+appVersionLatest);
			logger.info("msgToSales :"+msgToSales);
			
			String localSalesAppPath = getLocalPathSalesApp();
			
			//Write AppVersion Latest
			FileUtil.writeFile(localSalesAppPath+"Lastest-app-version.txt", appVersionLatest, "UTF-8");
			
			//Write Current AppVersion
			FileUtil.writeFile(localSalesAppPath+"current-app-version.txt", currentAppVersion, "UTF-8");
			
			//Write Message To Sales
			FileUtil.writeFile(localSalesAppPath+"message-to-sales.txt", msgToSales, "UTF-8");
			
			//** Download Sofware for netbook sales **/
			new DownloadWorker().start();
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	/** process run mainpage.jsp footer.jsp */
	public static String[] checkAppVersion(HttpServletRequest request){
		//String msg = "";
		String[] msg = new String[2];
		try{
			if(request.getSession().getAttribute("appVersionCheckMsg") == null){
			    String localSalesAppPath = getLocalPathSalesApp();

			    //get Latest version from local
				String appVersionLatest = Utils.isNull(FileUtil.readFile(localSalesAppPath+"Lastest-app-version.txt", "UTF-8"));

				String appVersion = SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
				
				logger.debug("appVersionLatest :"+appVersionLatest);
				logger.debug("CurrentAppVersion :"+appVersion);
				
				if( !"".equals(appVersionLatest) && !appVersion.equalsIgnoreCase(appVersionLatest)){
					//appVersion not match
					msg[0] = ""+SystemMessages.getCaption("AppVersionNotMatch", new Locale("TH","th")) ; 
					msg[1] ="<a href='https://dl.dropboxusercontent.com/u/24337336/pens/BME/pens_scan.war'>Download</a>";
				}else{
					msg[0] = "";
					msg[1] = "";
				}
				request.getSession().setAttribute("appVersionCheckMsg",msg);
				
				logger.debug("new msg :"+msg);
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
	 
    /** Process run SalesAppUpdater.jsp by User press UpdateSalesApp button**/
    public static void startSalesAppUpdater(){
        String line;
        OutputStream stdin = null;
        InputStream stderr = null;
        InputStream stdout = null;
        try{
            System.out.println("startSalesAppUpdater");
            //Runtime.getRuntime().exec("cmd ");
            String command = "d:/SalesApp/SalesAppUpdater/startSalesAppUpdater.bat";
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
	
    /** Download Software After Import  separate Thread **/
    public static void downloadSoftware(){
    	logger.info("downloadSoftware");
    	getSoftware4SalesApp();
    }
    
    /** Software For Sales App **/
    public static void getSoftware4SalesApp(){
		String localSalesAppPath = getLocalPathSalesApp();
		String sourcePath = URL_DROPBOX_+"Software4SalesApp.zip";
		String sourcePath2 = URL_DROPBOX_2+"Software4SalesApp.zip";
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
    
	public static boolean isLatestVersion(String name){
		boolean r = true;
		try{
			String localSalesAppPath = getLocalPathSalesApp();
			String localVersion = Utils.isNull(FileUtil.readFile(localSalesAppPath+name+"-version.txt", "UTF-8"));
			String latestVersionInServer = Utils.isNull(getLatestSalesVersion(name+"-version.txt"));
			
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
	
	private static String getLatestSalesVersion(String name){
        String appVersion = "";
        try{
        	String str = URL_DROPBOX_+name+"";
        	logger.debug("url:"+str);
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
	 }
	
	private  static String getLocalPathSalesApp(){
		String path = "D:/BMEApp/";
		try{
			File directory = new File(path);
			if(!directory.exists()){
				FileUtils.forceMkdir(directory);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			try{
				path = "D:/BMEApp/";
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
