package com.isecinc.pens.web.salesanalyst;

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
import com.isecinc.pens.report.salesanalyst.SAInitial;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class SecurityHelper {
	/**
	 * WITTY
	 */
	protected static  Logger logger = Logger.getLogger("PENS");
	public static Map<String,String> COLUMN_KEY_MAP = new HashMap<String, String>();
	
	public static Map<String,String> MASTER_COLUMN_SEARCH_KEY_MAP = new HashMap<String, String>();
	
	static{
		
		COLUMN_KEY_MAP.put("Brand", "Brand");//Brand
		COLUMN_KEY_MAP.put("Division","Division"); //Division
		COLUMN_KEY_MAP.put("Sales_Channel", "ภาคตามพนักงานขาย"); //ภาคตามพนักงานขาย Area
		COLUMN_KEY_MAP.put("Customer_Category","ประเภทขาย");//SaleType
		COLUMN_KEY_MAP.put("Salesrep_id", "พนักงานขาย");	//SalesMan
		
		
		MASTER_COLUMN_SEARCH_KEY_MAP.put("Brand", "Brand_no");//Brand
		MASTER_COLUMN_SEARCH_KEY_MAP.put("Division","div_no"); //Division
		MASTER_COLUMN_SEARCH_KEY_MAP.put("Sales_Channel", "SALES_CHANNEL_NO"); //ภาคตามพนักงานขาย Area
		MASTER_COLUMN_SEARCH_KEY_MAP.put("Customer_Category","cust_cat_no");//SaleType
		MASTER_COLUMN_SEARCH_KEY_MAP.put("Salesrep_id", "salesrep_code");	//SalesMan
		
		//COLUMN_KEY_MAP.put("Customer_id", "customer_category");
		//COLUMN_KEY_MAP.put("inventory_item_id", "inventory_item_id");
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
	
	public static String genHtmlUserRoleInfo(User user) throws Exception{
		Connection conn = null;
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
		String html = "";
		String roleColumnAccess= "";
		try{
			conn = DBConnection.getInstance().getConnection();
			sql += " select distinct rc.role_column_access  \n";
			sql += " from c_user_info a ,c_group_role ag ,c_role r ,c_role_access rc  \n";
			sql += " where 1=1  \n";
			sql += " and a.USER_GROUP_ID = ag.user_group_id  \n";
			sql += " and ag.ROLE_ID = r.role_id  \n";
			sql += " and r.ROLE_ID = rc.role_id  \n";
			sql += " and a.user_name = '"+user.getUserName()+"'";
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();	
			html +="<fieldset>";
			html +="<legend><b>สิทธิการเข้าถึงข้อมูลของคุณ :Role["+user.getUserGroupName()+"]</b></legend> ";
			html +="<div id='roleTab' style='display: block;'>";
			html +="<table border='0' align='center' width='80%'>\n";
			//html += "<tr><td colspan='3'> <b>สิทธิการเข้าถึงข้อมูลของคุณ :Role["+user.getUserGroupName()+"] </b></td></tr> \n";
			
			String roles[] = new String[5];
			int i=0;
			while(rs.next()){
				 roleColumnAccess = rs.getString("role_column_access");
			     String role= genRoleFilterByTableMaster(conn,user, roleColumnAccess);
			     roles[i] = role;
			     i++;
			}
			html +="<tr>\n";
			html += " <td>"+roles[0]+" : "+roles[1]+" : "+roles[2]+" : "+roles[3]+" : "+roles[4]+"</td>";  
			html +="<tr>\n";  
			html +="</table>\n";
			html +="</div>";
			html +="</fieldset>";
			return html;
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
			sql +=" from c_reference    \n";
			sql +=" where type ='GROUP_BY'  \n";
			sql +=" and code <> 'ALL' \n";
			sql +=" order by order_index  \n";
			
			logger.debug("sql:"+sql);
			
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
		     return genWhereSqlFilterByUserModel(conn,null,user, columnAccess,"");
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public static String genWhereSqlFilterByUser(Connection conn,User user,String columnAccess,String alias) throws Exception{
		 return genWhereSqlFilterByUserModel(conn,null,user, columnAccess,alias);
	}
	
	public static String genWhereSqlFilterByUser(HttpServletRequest request,String columnAccess) throws Exception{
		Connection conn = null;
		try{
		     conn = DBConnection.getInstance().getConnection();
		     return genWhereSqlFilterByUserModel(conn,request,null, columnAccess,"");
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public static String genWhereSqlFilterByUser(Connection conn,HttpServletRequest request,String columnAccess) throws Exception{
		  return genWhereSqlFilterByUserModel(conn,request,null, columnAccess,"");
	}
	
	public static String genWhereSqlFilterByUserModel(Connection conn,HttpServletRequest request,User user,String roleColumnAccess,String alias) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
		String whereSql = "";
		try{
			/** Case Type ="ALL" NO Filter **/
			if("ALL".equals(roleColumnAccess)){
				return "";
			}
			
			if(user == null && request != null){
			   user = (User) request.getSession(true).getAttribute("user");
			}
		
			conn =DBConnection.getInstance().getConnection();
			sql += " select distinct rc.role_column_access  \n";
			sql += " from c_user_info a ,c_group_role ag ,c_role r ,c_role_access rc  \n";
			sql += " where 1=1  \n";
			sql += " and a.USER_GROUP_ID = ag.user_group_id  \n";
			sql += " and ag.ROLE_ID = r.role_id  \n";
			sql += " and r.ROLE_ID = rc.role_id  \n";
			sql += " and a.user_name = '"+user.getUserName()+"'";
			if( !Utils.isNull(roleColumnAccess).equals("")){
				sql += " and rc.role_column_access = '"+roleColumnAccess+"'";	
			}
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();	
			whereSql +="/** Filter Data BY User **/ \n";
			//String roleColumnAccess= "";
			while(rs.next()){
				 roleColumnAccess = rs.getString("role_column_access");
			     String filterArray[] = genSqlFilterByTableMaster(conn,user, roleColumnAccess);
			     
			     String filterCond = filterArray[0];
			     String filterCode = filterArray[1];
			     if( !Utils.isNull(filterCond).equals("") && !Utils.isNull(filterCond).equals("'ALL'")){//not equals ALL
			    	// whereSql +="/** RoleColumn:"+roleColumnAccess+" :"+filterCode+"**/ \n";
			    	 whereSql +=" AND "+alias+roleColumnAccess+" IN("+filterCond+") \n";
			     }else{
			    	 //whereSql +="/** RoleColumn:"+roleColumnAccess+":"+filterCode+" **/ \n";
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
	
	
	public static String genWhereSqlFilterByUserForSearchPopup(Connection conn,User user,String roleColumnAccess,String alias) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
		String whereSql = "";
		try{
			conn =DBConnection.getInstance().getConnection();
			sql += " select distinct rc.role_column_access  \n";
			sql += " from c_user_info a ,c_group_role ag ,c_role r ,c_role_access rc  \n";
			sql += " where 1=1  \n";
			sql += " and a.USER_GROUP_ID = ag.user_group_id  \n";
			sql += " and ag.ROLE_ID = r.role_id  \n";
			sql += " and r.ROLE_ID = rc.role_id  \n";
			sql += " and a.user_name = '"+user.getUserName()+"'";
			if( !Utils.isNull(roleColumnAccess).equals("")){
				sql += " and rc.role_column_access = '"+roleColumnAccess+"'";	
			}
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();	
			whereSql +="/** Filter Data BY User **/ \n";
			//String roleColumnAccess= "";
			while(rs.next()){
				 roleColumnAccess = rs.getString("role_column_access");
			     String filterArray[] = genSqlFilterByTableMaster(conn,user, roleColumnAccess);
			     
			     String filterCond = filterArray[0];
			     String filterCode = filterArray[1];
			     if( !Utils.isNull(filterCond).equals("") && !Utils.isNull(filterCond).equals("'ALL'")){//not equals ALL
			    	// whereSql +="/** RoleColumn:"+roleColumnAccess+" :"+filterCode+"**/ \n";
			    	 whereSql +=" AND "+alias+MASTER_COLUMN_SEARCH_KEY_MAP.get(roleColumnAccess)+" IN("+filterCond+") \n";
			     }else{
			    	 //whereSql +="/** RoleColumn:"+roleColumnAccess+":"+filterCode+" **/ \n";
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
			sql1 += " from c_user_info a ,c_group_role ag ,c_role r ,c_role_access rc  \n";
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
			
			
			if("Division".equalsIgnoreCase(condType)){
				if( !Utils.isNull(condValue).equals("")){
				   sql = condValue;
				}
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				if( !Utils.isNull(condValue).equals("") ){
				  sql = condValue;
				}
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
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
	
	private static String genRoleFilterByTableMaster(Connection conn,User user,String condType) throws Exception{
		String sql = "";
		PreparedStatement ps =null;
		ResultSet rs =null;
		String condValue = "";
		String condDesc  ="";
		try{
			String sql1 = " select distinct rc.role_data_access  \n";
			sql1 += " from c_user_info a ,c_group_role ag ,c_role r ,c_role_access rc  \n";
			sql1 += " where 1=1  \n";
			sql1 += " and a.USER_GROUP_ID = ag.user_group_id  \n";
			sql1 += " and ag.ROLE_ID = r.role_id  \n";
			sql1 += " and r.ROLE_ID = rc.role_id  \n";
			sql1 += " and a.user_name = '"+user.getUserName()+"'";
			sql1 += " and rc.role_column_access = '"+condType+"'";
			
			logger.debug("sql:"+sql1);
			ps = conn.prepareStatement(sql1);
			rs = ps.executeQuery();	
			while(rs.next()){
				condValue = rs.getString("role_data_access");
				if( !Utils.isNull(condValue).equals("ALL")){
				   List<References> dataList  = SAInitial.getInstance().getConditionValueList4Role(conn,condType,condValue,null);	
				   if(dataList !=null && dataList.size()> 1){
				     condDesc  += ((References)dataList.get(1)).getName()+",";
				   }else{
					 condDesc  += ((References)dataList.get(0)).getName()+",";   
				   }
				}else{
				   condDesc  += "ALL,";
				}
			}
			if( !Utils.isNull(condDesc).equals("")){
				condDesc = condDesc.substring(0,condDesc.length()-1);
			}
			
			sql = "<b>"+COLUMN_KEY_MAP.get(condType)+"</b>["+condDesc +"],";
            
			return sql;
		}catch(Exception e){
			 throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, ps, rs);
		}
		
	}

}
