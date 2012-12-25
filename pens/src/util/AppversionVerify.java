package util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

public class AppversionVerify {

	protected static Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	public static String checkAppVersion(HttpServletRequest request){
		EnvProperties env = EnvProperties.getInstance();
		FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
		String msg = "";
		try{
			if(request.getSession().getAttribute("appVersionCheckMsg") == null){
				String appVersionLatest = Utils.isNull(ftpManager.getDownloadFTPFileByName("/Manual-script/app-version.txt"));
				String appVersion = SystemProperties.getCaption("AppVersion", new Locale("TH","th"));
				logger.info("appVersionLatest :"+appVersionLatest);
				logger.info("CurrentAppVersion :"+appVersion);
				
				if( !"".equals(appVersionLatest) && !appVersion.equalsIgnoreCase(appVersionLatest)){
					//appVersion not match
					msg = "!!!."+SystemMessages.getCaption("AppVersionNotMatch", new Locale("TH","th"));
				}else{
					msg = "";
				}
				
				request.getSession().setAttribute("appVersionCheckMsg",msg);
			}else{
				msg = Utils.isNull(request.getSession().getAttribute("appVersionCheckMsg"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return msg;
	}

	public static String getMessageToSales(HttpServletRequest request){
		EnvProperties env = EnvProperties.getInstance();
		FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
		String msg = "";
		try{
			if(request.getSession().getAttribute("massageToSales") == null){
				msg = ftpManager.getDownloadFTPFileByName("/Manual-script/message-to-sales.txt");
				request.getSession().setAttribute("massageToSales",msg);
			}else{
				msg = Utils.isNull(request.getSession().getAttribute("massageToSales"));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return msg;
	}
}
