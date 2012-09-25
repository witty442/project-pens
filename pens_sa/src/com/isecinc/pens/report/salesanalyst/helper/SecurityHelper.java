package com.isecinc.pens.report.salesanalyst.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;

public class SecurityHelper {
	/**
	 * WITTY
	 */
	protected static  Logger logger = Logger.getLogger("PENS");
	private static Map<String,String> COLUMN_KEY_MAP = new HashMap<String, String>();
	
	static{
		COLUMN_KEY_MAP.put("Customer_Category","customer_category");
		COLUMN_KEY_MAP.put("Salesrep_id", "customer_category");		
		COLUMN_KEY_MAP.put("Customer_id", "customer_category");
		COLUMN_KEY_MAP.put("inventory_item_id", "inventory_item_id");
		COLUMN_KEY_MAP.put("Sales_Channel", "Sales_Channel");
		COLUMN_KEY_MAP.put("Brand", "Brand");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
         try{
        	 System.out.println("x1:"+(100%100));
        	 System.out.println("x2:"+(49%100));
        	 System.out.println("x3:"+(50%100));
         }catch(Exception e){
        	 e.printStackTrace();
         }
	}
	public static List<References> filterDisplayColumnByUser(Connection conn ) throws Exception{
		
		return filterDisplayColumnByUserModel(conn);
	}
	
	public static List<References> filterDisplayColumnByUser() throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return filterDisplayColumnByUserModel(conn);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	private static List<References> filterDisplayColumnByUserModel(Connection conn) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<References> dataList = new ArrayList<References>();
		boolean all = true;
		try{
			
			if(all){
				dataList.addAll(getAllColumnAccessList(conn));
			}
		
			return dataList;
		}catch(Exception e){
			 logger.error(e.getMessage(),e);
			 throw e;
		}finally{
			
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		
	}
	
	
	public static List<References> getAllColumnAccessList(Connection conn) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<References> dataList = new ArrayList<References>();
		try{
			sql +=" select distinct  \n";
			sql +=" code as ROLE_COLUMN_ACCESS   \n"; 
			sql +=" ,th_name  as ROLE_COLUMN_ACCESS_DESC   \n";
			sql +=" ,order_index as ROLE_COLUMN_ACCESS_INDEX   \n";
			sql +=" from ad_reference    \n";
			sql +=" where type ='GROUP_BY'  \n";
			sql +=" and code <> 'ALL' \n";
			sql +=" order by order_index  \n";
			
			//logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				dataList.add(new References(Utils.isNull(rs.getString("ROLE_COLUMN_ACCESS")),Utils.isNull(rs.getString("ROLE_COLUMN_ACCESS_DESC"))));
			}
		
			return dataList;
		}catch(Exception e){
			 logger.error(e.getMessage(),e);
			 throw e;
		}finally{
			
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		
	}
	
	public static String genWhereSqlFilterByUser(User user,String columnAccess) throws Exception{
		Connection conn = null;
		try{
		     conn = DBConnection.getInstance().getConnection();
		     return genWhereSqlFilterByUserModel(conn,null,user, columnAccess);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public static String genWhereSqlFilterByUser(Connection conn,User user,String columnAccess) throws Exception{
		 return genWhereSqlFilterByUserModel(conn,null,user, columnAccess);
	}
	
	
	public static String genWhereSqlFilterByUser(HttpServletRequest request,String columnAccess) throws Exception{
		Connection conn = null;
		try{
		     conn = DBConnection.getInstance().getConnection();
		     return genWhereSqlFilterByUserModel(conn,request,null, columnAccess);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public static String genWhereSqlFilterByUser(Connection conn,HttpServletRequest request,String columnAccess) throws Exception{
		  return genWhereSqlFilterByUserModel(conn,request,null, columnAccess);
	}
	
	public static String genWhereSqlFilterByUserModel(Connection conn,HttpServletRequest request,User user,String columnAccess) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
		String whereSql = "";
		try{
			/** Case Type ="ALL" NO Filter **/
			if("ALL".equals(columnAccess)){
				return "";
			}
			
			if(user == null && request != null){
			   user = (User) request.getSession(true).getAttribute("user");
			}
		
			conn =DBConnection.getInstance().getConnection();
			sql += " select distinct rc.role_column_access  \n";
			sql += " from ad_user_info a ,ad_group_role ag ,ad_role r ,ad_role_access rc  \n";
			sql += " where 1=1  \n";
			sql += " and a.USER_GROUP_ID = ag.user_group_id  \n";
			sql += " and ag.ROLE_ID = r.role_id  \n";
			sql += " and r.ROLE_ID = rc.role_id  \n";
			sql += " and a.user_name = '"+user.getUserName()+"'";
			
			//logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();	
			whereSql +="/** Filter Data BY User **/ \n";
			String roleColumnAccess= "";
			while(rs.next()){
				 roleColumnAccess = rs.getString("role_column_access");
			     String filterArray[] = genSqlFilterByTableMaster(conn,user, roleColumnAccess);
			     
			     String filterCond = filterArray[0];
			     String filterCode = filterArray[1];
			     if( !Utils.isNull(filterCond).equals("") && !Utils.isNull(filterCond).equals("'ALL'")){//not equals ALL
			    	 whereSql +="/** RoleColumn:"+roleColumnAccess+" :"+filterCode+"**/ \n";
			    	 whereSql +=" AND "+Utils.isNull(COLUMN_KEY_MAP.get(roleColumnAccess))+" IN("+filterCond+") \n";
			     }else{
			    	 whereSql +="/** RoleColumn:"+roleColumnAccess+":"+filterCode+" **/ \n";
			     }	
			}
			return whereSql;
		}catch(Exception e){
			 throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		
	}
	
	
	private static String[] genSqlFilterByTableMaster(Connection conn,User user,String condType) throws Exception{
		String sql = "";
		PreparedStatement ps =null;
		ResultSet rs =null;
		String condValue = "";
		String re[] = new String[2];
		try{
			String sql1 = " select distinct rc.role_data_access  \n";
			sql1 += " from ad_user_info a ,ad_group_role ag ,ad_role r ,ad_role_access rc  \n";
			sql1 += " where 1=1  \n";
			sql1 += " and a.USER_GROUP_ID = ag.user_group_id  \n";
			sql1 += " and ag.ROLE_ID = r.role_id  \n";
			sql1 += " and r.ROLE_ID = rc.role_id  \n";
			sql1 += " and a.user_name = '"+user.getUserName()+"'";
			sql1 += " and rc.role_column_access = '"+condType+"'";
			
			//logger.debug("sql:"+sql1);
			
			ps = conn.prepareStatement(sql1);
			rs = ps.executeQuery();	
			while(rs.next()){
				condValue += "'"+rs.getString("role_data_access")+"',";
			}
			if( !Utils.isNull(condValue).equals("")){
				condValue = condValue.substring(0,condValue.length()-1);
			}
			
			
			if("inventory_item_id".equalsIgnoreCase(condType)){
				if( !Utils.isNull(condValue).equals("") && !Utils.isNull(condValue).equals("'ALL'") ){
					sql = "SELECT M.INVENTORY_ITEM_ID as ID from XXPENS_BI_MST_ITEM M \n";
					sql += " WHERE M.INVENTORY_ITEM_DESC LIKE '"+condValue.replace("'", "")+"%' \n";
				}
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				if( !Utils.isNull(condValue).equals("") ){
				  sql = condValue;
				}
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				if( !Utils.isNull(condValue).equals("")){
				   sql = condValue;
				}
			}else if("Customer_id".equalsIgnoreCase(condType)){
				if( !Utils.isNull(condValue).equals("")){
				   sql = condValue;
				}
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				if( !Utils.isNull(condValue).equals("")){
					   sql = condValue;
				}
			}else if("Brand".equalsIgnoreCase(condType)){
				if( !Utils.isNull(condValue).equals("")){
					   sql = condValue;
				}
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				//sql = "select M.cust_group_no as ID from XXPENS_BI_MST_CUST_GROUP M ";
			}else if("Division".equalsIgnoreCase(condType)){
				//sql = "select M.div_no as ID from XXPENS_BI_MST_DIVISION M ";
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				//sql = "select M.Invoice_Date as ID from XXPENS_BI_MST_INVOICE_DATE M ";
			}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
				//sql = "select M.ORDER_DATE as ID from XXPENS_BI_MST_ORDER_DATE M ";
			}else if("Province".equalsIgnoreCase(condType)){
				//sql = "select M.PROVINCE as ID from XXPENS_BI_MST_PROVINCE M ";
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				//sql = "select M.AMPHOR as ID from XXPENS_BI_MST_AMPHOR M ";
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				//sql = "select M.TAMBOL as ID from XXPENS_BI_MST_TAMBOL M ";
			}else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				//sql = "select distinct M.SALES_ORDER_NO as ID from XXPENS_BI_SALES_ANALYSIS M ";
			}else if("INVOICE_NO".equalsIgnoreCase(condType)){
				//sql = "select distinct  M.INVOICE_NO as desc_ from XXPENS_BI_SALES_ANALYSIS M ";
			}else if("IR_AMT".equalsIgnoreCase(condType)){
				//sql = "select  distinct M.INVOICED_AMT-M.RETURNED_AMT as desc_ from XXPENS_BI_SALES_ANALYSIS M ";
			}
			
			re[0] = sql;
			re[1] = condValue;
			
			return re;
		}catch(Exception e){
			 throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, ps, rs);
		}
		
	}

}
