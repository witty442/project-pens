package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.bean.User;

public class PageVisit {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void processPageVisit(HttpServletRequest request,String pageName){
		Connection conn = null;
		try{
			User user = (User)request.getSession().getAttribute("user");
			conn = DBConnection.getInstance().getConnectionApps();
			
			//Get PageName Thai from SystemProperty
			String pageNameTH = SystemProperties.getCaption(pageName,Locale.getDefault());
			
			int update = updatePageVisit(conn, user.getUserName(), pageNameTH);
			if(update==0){
				insertPageVisit(conn, user.getUserName(), pageNameTH);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception ee){}
		}
	}
	
	private static int insertPageVisit(Connection conn ,String userName,String pageName) throws Exception {
		int result = 0;
		PreparedStatement ps = null;
		int index = 0;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO pensbi.page_visit( \n");
			sql.append(" user_name,page_name,page_count,update_date) \n"); //6 ) \n");//22
			sql.append(" VALUES (?,?,1,?) \n");//
			logger.debug("SQL:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, Utils.isNull(userName));//1
			ps.setString(++index, Utils.isNull(pageName));//1
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			
			result = ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	private static int updatePageVisit(Connection conn ,String userName,String pageName) throws Exception {
		int result = 0;
		PreparedStatement ps = null;
		int index = 0;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" update pensbi.page_visit \n");
			sql.append(" set page_count = (page_count+1) ,update_date =? \n");
			sql.append(" where user_name = ? \n"); 
			sql.append(" and page_name = ? \n");
			
			logger.debug("SQL:"+sql);
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, Utils.isNull(userName));
			ps.setString(++index, Utils.isNull(pageName));
			
			result = ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
}
