package com.isecinc.pens.model;

import static com.pens.util.ConvertNullUtil.convertToString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.process.SequenceProcess;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * I_Model Class
 * 
 * @author Atiz.b
 * @version $Id: I_Model.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MUser extends I_Model<User> {

	private static final long serialVersionUID = -6511820955495428770L;

	public static String TABLE_NAME = "c_user_info";
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
				,new java.sql.Timestamp(DateUtil.parse(user.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th).getTime())
				,Utils.isNull(user.getEndDate()).equals("")?null:new java.sql.Timestamp(DateUtil.parse(user.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th).getTime())};
		return super.save(TABLE_NAME, columns, values, user.getId(), conn);
	}
	
	public int insert(User user, int activeUserID, Connection conn) throws Exception {
		int id = SequenceProcess.getNextValue("c_user_info","user_id").intValue();
		Object[] values = { id, ConvertNullUtil.convertToString(user.getPassword()).trim(),
				user.getUserGroupId(), activeUserID 
				,Utils.isNull(user.getName()) ,Utils.isNull(user.getUserName())
				,new java.sql.Timestamp(DateUtil.parse(user.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th).getTime())
				,Utils.isNull(user.getEndDate()).equals("")?null:new java.sql.Timestamp(DateUtil.parse(user.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th).getTime())};
		    
		  super.saveNew(TABLE_NAME, columns, values,0, conn);
		return id;
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
	
	public User[] searchM(String whereClause,String orderBy) throws Exception {
		logger.debug("search " + this.getClass());
		Connection conn = null;
		PreparedStatement pstmt = null;
		java.sql.ResultSet rst = null;
		StringBuffer sql = new StringBuffer("");
		List<User> dataList = new ArrayList<User>();
		User[] array = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			sql.append(" SELECT  \n");
			sql.append("  CATEGORY,ORGANIZATION,START_DATE,\n");
			sql.append("  END_DATE,NAME,SOURCE_NAME,\n");
			sql.append("  ID_CARD_NO,USER_NAME,PASSWORD,\n");
			sql.append("  CODE,UPDATED,UPDATED_BY,TERRITORY,\n");
			sql.append("  USER_ID,USER_GROUP_ID,ROLE_SALESTARGET, ROLE_CR_STOCK,\n");
			sql.append(" (select max(s.user_group_name) from c_group_role s where A.user_group_id = s.user_group_id) as user_group_name \n");
			sql.append(" FROM c_user_info A  WHERE 1=1 \n");
			sql.append(whereClause);
			sql.append(" \n order by "+orderBy);
			
			logger.debug(sql.toString());
			pstmt = conn.prepareStatement(sql.toString());
			rst = pstmt.executeQuery();
			while(rst.next()){
				User u = new User();
				// Mandatory
				u.setId(rst.getInt("USER_ID"));
				u.setCode(convertToString(rst.getString("CODE")));
				u.setName(convertToString(rst.getString("NAME")));

				// oracle fields
				u.setCategory(convertToString(rst.getString("CATEGORY")));
				u.setOrganization(convertToString(rst.getString("ORGANIZATION")));
				u.setSourceName(convertToString(rst.getString("SOURCE_NAME")));
				u.setIdCardNo(convertToString(rst.getString("ID_CARD_NO")));
			
				//String dateStr = Utils.stringValue(new Date(rst.getDate("START_DATE",Calendar.getInstance(Locale.US)).getTime()),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				if( !Utils.isNull(rst.getString("START_DATE")).equals("")){
				  u.setStartDate(DateUtil.stringValue(new Date(rst.getDate("START_DATE",Calendar.getInstance(Locale.US)).getTime()),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				}
				if( !Utils.isNull(rst.getString("END_DATE")).equals("")){
				  u.setEndDate(DateUtil.stringValue(new Date(rst.getDate("END_DATE",Calendar.getInstance(Locale.US)).getTime()),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				}
				u.setTerritory(convertToString(rst.getString("TERRITORY")));

				// sales online fields
				u.setUserName(convertToString(rst.getString("USER_NAME")));
				u.setPassword(convertToString(rst.getString("PASSWORD")));
				u.setConfirmPassword(convertToString(rst.getString("PASSWORD")));
		         
				u.setUserGroupId(rst.getInt("USER_GROUP_ID"));
				u.setUserGroupName(rst.getString("user_group_name"));
				u.setActiveLabel(DateUtil.isStatusActiveByDate(u.getStartDate(),u.getEndDate())?"Active":"InActive");
				u.setRoleSalesTarget(convertToString(rst.getString("ROLE_SALESTARGET")));
				
				dataList.add(u);
			}
			
			if (dataList.size() == 0) return null;
			array = new User[dataList.size()];
			array = dataList.toArray(array);
			
		} catch (Exception e) {
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, pstmt, rst);
		}
		return array;
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
