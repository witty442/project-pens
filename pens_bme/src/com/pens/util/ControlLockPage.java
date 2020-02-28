package com.pens.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;

public class ControlLockPage {
	protected static Logger logger = Logger.getLogger("PENS");
	
	/**
	 * @param conn
	 * @param orderDate
	 * @return 
	 * @throws Exception
	 */
	public static boolean canAccessPage(String pageName) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean r = true;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select lock_page from PENSBI.C_CONTROL_LOCK_PAGE where 1=1 \n" );
			sql.append(" and page_name ='"+pageName+"'"); 
	        logger.debug("sql:\n"+sql.toString());
	        
            conn = DBConnection.getInstance().getConnectionApps();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(Utils.isNull(rs.getString("lock_page")).equals("Y")){
				  r = false;
				}
			}
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	
	public static void controlLockPage(User user,String pageName,String lock) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
    	logger.debug("controlLockPage");
		try{
			sql.append(" UPDATE PENSBI.C_CONTROL_LOCK_PAGE \n");
			sql.append(" SET LOCK_PAGE =? ,UPDATE_USER =? ,UPDATE_DATE =?  \n" );
			sql.append(" WHERE PAGE_NAME =?  \n" );
			
			conn = DBConnection.getInstance().getConnectionApps();
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, lock);
			ps.setString(2, user.getUserName());
			ps.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
			ps.setString(4, pageName);
		    int u = ps.executeUpdate();
		    if(u==0){
		    	sql = new StringBuffer("");
		    	sql.append(" INSERT INTO PENSBI.C_CONTROL_LOCK_PAGE \n");
				sql.append(" (PAGE_NAME,LOCK_PAGE,CREATE_USER,CREATE_DATE)  \n" );
				sql.append(" VALUES(?,?,?,?)  \n" );
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(1, pageName);
				ps.setString(2, lock);
				ps.setString(3, user.getUserName());
				ps.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime()));
			
				ps.executeUpdate();
		    }
				
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	
}
