package com.isecinc.pens.process.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.Database;
import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;

public class LoginProcess {
	private Logger logger = Logger.getLogger("PENS");

	/**
	 * Login
	 * 
	 * @param userName
	 * @param password
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public User login(String userName, String password, Connection conn) throws Exception {
		logger.debug(String.format("User Login %s", userName));
		User user = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT A.* FROM c_user_info A WHERE 1=1 \n";
			sql +=" AND(  ( START_DATE <= SYSDATE and END_DATE >= SYSDATE AND END_DATE IS NOT NULL) \n";
                   sql +="      OR  \n";
                   sql +="       (START_DATE <= SYSDATE  AND END_DATE IS NULL) \n";
                   sql +="     ) \n";
                   sql +=" AND USER_NAME = ? AND PASSWORD = ? \n" ;
                   
			//List<User> users = Database.query(sql, new Object[] { userName, password }, User.class, conn);
			//if (users.size() > 0) user = users.get(0);
                  
            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, password);
            
            rs = ps.executeQuery();
            if(rs.next()){
            	user = new User(rs);
            }
            
		} catch (Exception e) {
			logger.error(e.toString());
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return user;
	}
	
	public User loginDummy(String userName, String password, Connection conn) throws Exception {
		logger.debug(String.format("User Login %s", userName));
		User user = null;
		try {
			if("admin".equalsIgnoreCase(userName) && "admin".equalsIgnoreCase(password)){
				user = new User();
				user.setUserName(userName);
				user.setName("Administrator");
				user.setRole(new References("Admin", "Admin"));
				user.setPassword(password);
				
			}
		} catch (Exception e) {
			logger.error(e.toString());
			throw e;
		}
		return user;
	}
}
