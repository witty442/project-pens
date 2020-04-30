package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.groovy.ast.stmt.ThrowStatement;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class GeneralDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	//Return is set map :true
	public static boolean isUserMapCustSalesTT(User user){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		boolean isMap = false;
		try{
			sql.append("\n  SELECT count(*) as c from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT M  ");
			sql.append("\n  where user_name='"+user.getUserName()+"'");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnectionApps();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				if(rst.getInt("c")>0){
					isMap = true;
				}
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	 return isMap;
	}
	
	/**
	 * 
	 * @param user
	 * @return zone map by user in XXPENS_BI_MST_CUST_CAT_MAP_TT
	 */
	public static String getSalesZoneMapCustTTByUser(User user){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		String userZoneAll = "";
		try{
			if(user.getUserName().equalsIgnoreCase("admin")){
				userZoneAll = "ALL";
				return userZoneAll;
			}
			sql.append("\n  SELECT distinct zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT M  ");
			sql.append("\n  where user_name='"+user.getUserName()+"'");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnectionApps();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				userZoneAll += rst.getString("zone")+",";
			}//while
			if(userZoneAll.length()>1){
				userZoneAll =userZoneAll.substring(0,userZoneAll.length()-1);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	 return userZoneAll;
	}
	
	public static String getCustCatName(String custCatNo) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getCustCatName(conn,custCatNo);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	public static String getCustCatName(Connection conn,String custCatNo){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			if(Utils.isNull(custCatNo).equals("")){
				return "";
			}
			sql.append("\n  SELECT cust_cat_no,cust_cat_desc from PENSBI.XXPENS_BI_MST_CUST_CAT M  ");
			sql.append("\n  where  cust_cat_no ='"+custCatNo+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("cust_cat_desc"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
}
