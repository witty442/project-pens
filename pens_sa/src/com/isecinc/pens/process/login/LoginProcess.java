package com.isecinc.pens.process.login;

import java.sql.Connection;
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
		try {
			String sql = "SELECT A.USER_GROUP_ID ,  \n";
			sql +="   A.CATEGORY, A.ORGANIZATION, A.START_DATE,  \n";
			sql +="   A.END_DATE, A.NAME, A.SOURCE_NAME,  \n";
			sql +="   A.ID_CARD_NO, A.USER_NAME, A.PASSWORD,  \n";
			sql +="   A.CODE, A.UPDATED, A.UPDATED_BY,  \n";
			sql +="   A.TERRITORY, A.USER_ID      \n";  
			sql +="  FROM c_user_info A WHERE 1=1 \n";
			sql +=" AND(  ( START_DATE <= SYSDATE and END_DATE >= SYSDATE AND END_DATE IS NOT NULL) \n";
                   sql +="      OR  \n";
                   sql +="       (START_DATE <= SYSDATE  AND END_DATE IS NULL) \n";
                   sql +="     ) \n";
                   sql +=" AND USER_NAME = ? AND PASSWORD = ? \n" ;
			List<User> users = Database.query(sql, new Object[] { userName, password }, User.class, conn);
			if (users.size() > 0) user = users.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
			throw e;
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
