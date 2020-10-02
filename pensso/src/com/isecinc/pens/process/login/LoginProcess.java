package com.isecinc.pens.process.login;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.Database;
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
			String sql = "SELECT * FROM pensso.ad_user WHERE ISACTIVE = 'Y' AND USER_NAME = ? AND PASSWORD = ?";
			List<User> users = Database.query(sql, new Object[] { userName, password }, User.class, conn);
			if (users.size() > 0) user = users.get(0);
		} catch (Exception e) {
			logger.error(e.toString());
			throw e;
		}
		return user;
	}
}
