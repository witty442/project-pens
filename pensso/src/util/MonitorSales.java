package util;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

public class MonitorSales {

	protected static  Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		User u = new User();
		u.setCode("V105");
		uploadSalesAppVersion(u);
	}
	
	public static String monitorSales(BigDecimal transactionId){
		EnvProperties env = EnvProperties.getInstance();
		FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
		String msg = "";
		try{
			/*String dateStr  = Utils.format(new Date(), "yyyyMM");
            String fileName = dateStr+"-Monitor-Sales.csv";
					
			String lastMonitorFile = Utils.isNull(ftpManager.getDownloadFTPFileByName("/Manual-script/Monitor-Sales/"+fileName));
			StringBuffer m = new StringBuffer(lastMonitorFile);
			//Get Last import or export
			m.append(getLastInterfaces(transactionId));
			
			//Upload to FTP
			ftpManager.uploadFileToFTP("/Manual-script/Monitor-Sales/", fileName, m.toString(), "UTF-8");*/
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return msg;
	}
	
	public static void uploadSalesAppVersion(User u){
		try{
			EnvProperties env = EnvProperties.getInstance();
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			String path = "D:/SalesApp/";
			
			String currentAppVersion = Utils.isNull(FileUtil.readFile(path+"current-app-version.txt", "UTF-8"));
			String latestAppVersion  = Utils.isNull(FileUtil.readFile(path+"Lastest-app-version.txt", "UTF-8"));
			if("".equals(currentAppVersion)){
				FileUtil.writeFile(path+"current-app-version.txt", "");
			}
			if("".equals(latestAppVersion)){
				FileUtil.writeFile(path+"Lastest-app-version.txt", "");
			}
			
			String dateStr  = Utils.format(new Date(), "yyyyMM");
            String fileName = u.getCode()+".txt";
            
            if(!currentAppVersion.equalsIgnoreCase(latestAppVersion)){
            	
               //CreateFolder FTP
            	ftpManager.createFolderFTP("/Manual-script/Monitor-Sales/SalesAppVersion/", dateStr);
            	
			   //Upload File to FTP
			   ftpManager.uploadFileToFTP("/Manual-script/Monitor-Sales/SalesAppVersion/"+dateStr+"/", fileName, currentAppVersion, "UTF-8");
            }
            
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	
	public static void getAllSalesAppVersion(){
		try{
			EnvProperties env = EnvProperties.getInstance();
			FTPManager ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			String path = "D:/SalesApp/";
			
			String yyyymm = Utils.format(new Date(), "yyyyMM");
			
			StringBuffer data = ftpManager.downloadAllFileInFolder("/Manual-script/Monitor-Sales/SalesAppVersion/"+yyyymm+"/");
			
			FileUtil.writeFile(path+"SalesAppVersionAll.csv", data.toString(), "UTF-8");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	public static StringBuffer getLastInterfaces(BigDecimal transactionId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Connection conn = null;
		StringBuffer m = new StringBuffer("");
		try{
			conn = DBConnection.getInstance().getConnection();
			StringBuffer sql = new StringBuffer("");
			sql.append(" select create_user , \n");
			sql.append(" concat(Transaction_id,concat('-',type)) as Transaction,\n");
			sql.append(" create_date,\n");
			sql.append(" status , \n");
			sql.append(" concat(error_code,concat('-',error_msg)) as error \n");
			sql.append("  from monitor \n ");
			sql.append(" where transaction_id ="+transactionId+" \n");
			sql.append(" and monitor_id = ( \n ");
			sql.append(" select max(monitor_id) from monitor \n ");
			sql.append(" where transaction_id ="+transactionId+" ) \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				m.append("\n "+rs.getString("create_user")+",");
				m.append(rs.getString("Transaction")+",");
				m.append(Utils.stringValue(rs.getTimestamp("create_date"), Utils.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,Utils.local_th)+",");
				m.append(convertStatusToDesc(rs.getInt("status"))+",");
				m.append(Utils.isNull(rs.getString("error"))+",");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(conn != null){
			   conn.close();conn = null;
			}
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return m;
	} 
	
	/**
	 * public static final int STATUS_FTP_FAIL = -2;
	public static final int STATUS_FAIL = -1;
	public static final int STATUS_START = 0;
	public static final int STATUS_SUCCESS = 1;
	 * @param status
	 * @return
	 */
	private static String convertStatusToDesc(int status){
		String r ="unknow";
		if(status == 0){
			r ="Task Start";
		}else if(status == 1){
			r ="Task Success";
		}else if(status == -1){
			r ="Task Fail";		
		}else if(status == -2){
			r ="Task FTP Fail";
		}
		return r;
	}
}
