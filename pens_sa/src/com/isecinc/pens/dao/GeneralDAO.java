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
		
		public static String getBrandGroup(Connection conn,String brandNo){
			String salesChannelDesc = "";
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			try{
				if(Utils.isNull(brandNo).equals("")){
					return "";
				}
				
				sql.append("\n  SELECT brand_group_no from XXPENS_BI_MST_BRAND_GROUP M  ");
				sql.append("\n  where  brand_no ='"+brandNo+"' \n");

				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					salesChannelDesc =Utils.isNull(rst.getString("brand_group_no"));
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
		
		public static String getBrandName(Connection conn,String brandNo){
			String salesChannelDesc = "";
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			try{
				if(Utils.isNull(brandNo).equals("")){
					return "";
				}
				
				sql.append("\n  SELECT brand_desc from XXPENS_BI_MST_BRAND M  ");
				sql.append("\n  where  brand_no ='"+brandNo+"' \n");

				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					salesChannelDesc =Utils.isNull(rst.getString("brand_desc"));
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
		
	
		
		public static String getCustName(String custCode) throws Exception{
			 Connection conn = null;
			 try{
				 conn = DBConnection.getInstance().getConnection();
				 return getCustName(conn,custCode);
			 }catch(Exception e){
				 throw e;
			 }finally{
				conn.close();
			 }
		}
		public static String getCustName(Connection conn,String custCode){
			String salesChannelDesc = "";
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			try{
				if(Utils.isNull(custCode).equals("")){
					return "";
				}
				sql.append("\n  SELECT customer_desc from PENSBI.XXPENS_BI_MST_CUSTOMER M  ");
				sql.append("\n  where  customer_code ='"+custCode+"' \n");

				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					salesChannelDesc =Utils.isNull(rst.getString("customer_desc"));
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
		
		public static String getCustCatName(String custCatNo) throws Exception{
			 Connection conn = null;
			 try{
				 conn = DBConnection.getInstance().getConnection();
				 return getCustName(conn,custCatNo);
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
