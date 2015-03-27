package com.isecinc.pens.web.managepath;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OnhandSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class ManagePath {

	private static Logger logger = Logger.getLogger("PENS");
	
	static Map<String, String> PATH_MAP = new HashMap<String, String>();
	static{
		PATH_MAP.put("/jsp/order/order.jsp", "Order");
		PATH_MAP.put("/jsp/orderAction.do", "Order");
	}
	public ManagePath() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static String savePath(User user,String path){
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement psUpdate = null;
		String msg = "";
		try{
			
			if(PATH_MAP.get(path) != null){
			  path = PATH_MAP.get(path);
			}
			//Param
			logger.debug("User Name:"+user.getUserName());
			logger.debug("Path Name:"+path);

			conn = DBConnection.getInstance().getConnection();
			
			StringBuffer sql = new StringBuffer(" UPDATE PENSBME_MANAGE_PATH \n"); 
			sql.append("SET PATH_NAME=? ,CREATE_DATE = ? WHERE USER_NAME = ?  \n");
			
			psUpdate = conn.prepareStatement(sql.toString());
			psUpdate.setString(1, Utils.isNull(path));
			psUpdate.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			psUpdate.setString(3, Utils.isNull(user.getUserName()));
			
			int r = psUpdate.executeUpdate();
			
			if(r==0){
				sql = new StringBuffer(" INSERT INTO PENSBME_MANAGE_PATH \n"); 
				sql.append("(USER_NAME,PATH_NAME,CREATE_DATE) \n");
				sql.append("VALUES (?, ?, ?) \n");
				
			    ps = conn.prepareStatement(sql.toString());
			    ps.setString(1, Utils.isNull(user.getUserName()));
			    ps.setString(2, Utils.isNull(path));
			    ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			   
			    ps.execute();
			}
	      
		}catch(Exception e){
			e.printStackTrace();
			msg = e.getMessage();
		}finally{
			try{
				if(ps != null){
				   ps.close();ps=null;
				}
				if(psUpdate != null){
				   psUpdate.close();psUpdate=null;
				}
				if(conn != null){
				   conn.close();conn=null;
				}
				
			}catch(Exception e){}
		}
		return msg;
	}
	
	 public static String canAccessPath(User user,String path) throws Exception{
		   Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String userAccessPath = "";
			try {
				if(PATH_MAP.get(path) != null){
				  path = PATH_MAP.get(path);
				}
				
				conn = DBConnection.getInstance().getConnection();
				
				sql.delete(0, sql.length());
				sql.append("\n  SELECT h.*  from PENSBME_MANAGE_PATH h ");
				sql.append("\n  where 1=1  and trunc(create_date) = trunc(sysdate) ");
				sql.append("\n  and path_name = '"+path+"'");
				sql.append("\n  order by create_date asc ");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
				   if( !Utils.isNull(rst.getString("USER_NAME")).equals(user.getUserName())){
					   userAccessPath= Utils.isNull(rst.getString("USER_NAME"));
				   }
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return userAccessPath;
	    }

}
