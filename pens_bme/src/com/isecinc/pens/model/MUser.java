package com.isecinc.pens.model;

import static util.ConvertNullUtil.convertToString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

	public static String TABLE_NAME = "pensbme_c_user_info";
	public static String COLUMN_ID = "User_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "PASSWORD", "USER_GROUP_ID", "UPDATED_BY","NAME","USER_NAME","START_DATE","END_DATE" };
	
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

	public User findByUserName(String userName) throws Exception {
		return super.find(userName, TABLE_NAME, "USER_NAME", User.class);
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

	public User[] search(String whereCause,String orderBy) throws Exception {
		List<User> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause,orderBy, User.class);
		if (pos.size() == 0) return null;
		User[] array = new User[pos.size()];
		array = pos.toArray(array);
		return array;
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
	public boolean update(User user, int activeUserID, Connection conn) throws Exception {
		Object[] values = { user.getId(), ConvertNullUtil.convertToString(user.getPassword()).trim(),
				user.getUserGroupId(), activeUserID 
				,Utils.isNull(user.getName()) ,Utils.isNull(user.getUserName())
				,new java.sql.Timestamp(Utils.parse(user.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th).getTime())
				,Utils.isNull(user.getEndDate()).equals("")?null:new java.sql.Timestamp(Utils.parse(user.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th).getTime())};
		return super.save(TABLE_NAME, columns, values, user.getId(), conn);
	}
	
	
	public boolean changeActive(String active, String[] ids, int activeUserID, Connection conn) throws Exception {
		return super.changeActive(TABLE_NAME, COLUMN_ID, active, ids, activeUserID, conn);
	}
	
	public boolean changeUserGroup(String userGroupId, String[] ids, int activeUserID,
			Connection conn) throws Exception {
		logger.debug("Change Active " + this.getClass());
		PreparedStatement pstmt = null;
		String id = "";
		try {
			for (String s : ids)
				id += "," + s;
			id = id.substring(1);
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE " + TABLE_NAME +" SET \n");
		    sql.append("  USER_GROUP_ID = '"+userGroupId+"' \n");
			sql.append(", UPDATED = CURRENT_TIMESTAMP \n");
			sql.append(", UPDATED_BY = " + activeUserID +"\n");
			sql.append(" WHERE " + COLUMN_ID + " IN (" + id + ") \n");
			logger.debug(sql.toString());
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		}finally{
			if(pstmt != null){
				pstmt.close();pstmt=null;
			}
		}
		return true;
	}
	
	
	public boolean changePassword(Connection conn,int userId,String newPassword) throws Exception {
		logger.debug("changePassword " + this.getClass());
		PreparedStatement pstmt = null;
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE " + TABLE_NAME +" SET \n");
		    sql.append("  PASSWORD = '"+newPassword+"' \n");
			sql.append(", UPDATED = CURRENT_TIMESTAMP \n");
			sql.append(", UPDATED_BY = " + userId +"\n");
			sql.append(" WHERE USER_ID = "+userId+"\n");
			logger.debug(sql.toString());
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			throw e;
		}finally{
			if(pstmt != null){
				pstmt.close();pstmt=null;
			}
		}
	}
}
