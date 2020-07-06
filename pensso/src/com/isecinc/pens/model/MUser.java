package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

/**
 * I_Model Class
 * 
 * @author Atiz.b
 * @version $Id: I_Model.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MUser extends I_Model<User> {

	private static final long serialVersionUID = -6511820955495428770L;

	public static String TABLE_NAME = "ad_user";
	public static String COLUMN_ID = "User_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "PASSWORD", "ROLE", "ISACTIVE", "UPDATED_BY" ,"PD_PAID"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public User find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, User.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public User[] search(String whereCause) throws Exception {
		List<User> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, User.class);
		if (pos.size() == 0) return null;
		User[] array = new User[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	public User  getCurrentUserName() {
		Statement stmt = null;
		ResultSet rst = null;
		Connection conn = null;
		User userDefault = new User();
		int count = 0;
		try{
			String sql ="\n select * from ad_user where user_name <> 'admin'" ;
			     // sql +="\n and 1=1 and user_id in(select user_id from t_order where created in(select max(created) from t_order))";
			      
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){ 
				//userDefault.setUserName(Utils.isNull(rst.getString("user_name")));
				//userDefault.setPassword(Utils.isNull(rst.getString("password")));
				//userDefault.setId(rst.getInt("user_id"));
				userDefault = new User(rst);
				count++;
			}
			if(count >1){
				userDefault.setUserName("");
				userDefault.setPassword("1234");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		} finally {
			try {
				rst.close();
				stmt.close(); 
				conn.close();
			} catch (Exception e2) {}
		}
		return userDefault;
	}
	public User  getActiveUserName() {
		Statement stmt = null;
		ResultSet rst = null;
		Connection conn = null;
		User userDefault = new User();
		int count = 0;
		try{
			String sql ="\n select * from ad_user where user_name <> 'admin'" ;
			      sql +="\n and 1=1 and user_id in(select user_id from t_order where created in(select max(created) from t_order))";
			      
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){ 
				//userDefault.setUserName(Utils.isNull(rst.getString("user_name")));
				//userDefault.setPassword(Utils.isNull(rst.getString("password")));
				//userDefault.setId(rst.getInt("user_id"));
				userDefault = new User(rst);
				count++;
			}
			if(count >1){
				userDefault.setUserName("");
				userDefault.setPassword("1234");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				rst.close();
				stmt.close(); 
				conn.close();
			} catch (Exception e2) {}
		}
		return userDefault;
	}
	/**
	 * Save
	 * 
	 * @param user
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(User user, int activeUserID, Connection conn) throws Exception {
		Object[] values = { user.getId(), ConvertNullUtil.convertToString(user.getPassword()).trim(),
				ConvertNullUtil.convertToString(user.getType()).trim(), user.getActive(), activeUserID ,user.getPdPaid()};
		return super.save(TABLE_NAME, columns, values, user.getId(), conn);
	}

	/**
	 * Change Active
	 * 
	 * @param ids
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean changeActive(String active, String[] ids, int activeUserID, Connection conn) throws Exception {
		return super.changeActive(TABLE_NAME, COLUMN_ID, active, ids, activeUserID, conn);
	}
}
