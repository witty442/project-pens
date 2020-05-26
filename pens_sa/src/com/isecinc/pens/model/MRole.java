package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Role;
import com.isecinc.pens.report.salesanalyst.SAConstants;
import com.isecinc.pens.report.salesanalyst.SAInitial;
import com.isecinc.pens.report.salesanalyst.helper.SecurityHelper;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

public class MRole {
  
	private static Logger logger = Logger.getLogger("PENS");
	
	public static int saveRole(Role role,List<Role> roleList) throws Exception{
		Connection conn = null;
		int roleId  = 0;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			if( !"".equals(role.getRoleId())){
				logger.debug("Update Role");
				roleId = Integer.parseInt(role.getRoleId());
				updateAdRole(conn, role);
				deleteAdRoleAccess(conn,role.getRoleId());
				insertAdRoleAccess(conn, Integer.parseInt(role.getRoleId()),roleList);
			}else{
				logger.debug("Insert Role");
				roleId = insertAdRole(conn, role);
				insertAdRoleAccess(conn, roleId, roleList);
			}
			conn.commit();
		}catch(Exception e){
			logger.debug("Transaction Rollback");
			conn.rollback();
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
		return roleId;
	}
	
	private static int insertAdRole(Connection conn,Role role) throws Exception{
		PreparedStatement ps = null;
		int roleId = 0;
		try{
			ps = conn.prepareStatement("insert into c_role(role_id,role_name) values(?,?)");
			roleId =SequenceProcess.getNextValue("c_role", "role_id");
			logger.debug("roleId:"+roleId);
			
			ps.setInt(1, roleId);
			ps.setString(2, role.getRoleName());
			ps.execute();
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, ps, null);
		}
		return roleId;
	}
	
	private static boolean updateAdRole(Connection conn,Role role) throws Exception{
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("update c_role set role_name = ? where role_id =? ");
			ps.setString(1, role.getRoleName());
			ps.setInt(2, Integer.parseInt(role.getRoleId()));
			
			ps.execute();
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, ps, null);
		}
		return true;
	}
	
	private static boolean insertAdRoleAccess(Connection conn,int roleId,List<Role> roleList) throws Exception{
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("insert into c_role_access(role_id,role_column_access,role_data_access) values(?,?,?)");
			if(roleList != null && roleList.size() > 0){
				for(int i=0;i<roleList.size();i++){
					Role role =(Role)roleList.get(i);
					String[] roleDataAccessArr = Utils.isNull(role.getRoleDataAccess()).split(",");
					for(int a=0;a<roleDataAccessArr.length;a++){
						String roleDataAccessStr = roleDataAccessArr[a];
					    ps.setInt(1, roleId);
						ps.setString(2, role.getRoleColumnAccess());
						ps.setString(3, roleDataAccessStr);
						ps.addBatch();
					}
				}
				ps.executeBatch();
			}
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, ps, null);
		}
		return true;
	}
	
	
	private static boolean deleteAdRoleAccess(Connection conn,String roleId) throws Exception{
		PreparedStatement ps = null;
		try{
			logger.debug("role_id:"+roleId);
			ps = conn.prepareStatement("delete from c_role_access where role_id = ? ");
			ps.setString(1, roleId);
			ps.execute();
			
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, ps, null);
		}
		return true;
	}
	
	public static List<Role> findRoleList(Role roleCriteria) {
		List<Role> roleList = new ArrayList<Role>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int index =0;
		int indexDisp =0;
		try{
			String sql = "select distinct \n ";
	        sql += " r.role_id ,r.role_name, \n ";
	        sql += " rc.role_column_access  \n ";
	        sql += " from c_role r , c_role_access rc  where 1=1 \n";
	        sql += " and r.role_id = rc.role_id \n";
	        
			if( !Utils.isNull(roleCriteria.getRoleName()).equals("")){
				sql +=" and r.role_name LIKE '%"+Utils.isNull(roleCriteria.getRoleName())+"%' \n ";
			}
			if( !Utils.isNull(roleCriteria.getRoleColumnAccess()).equals("")){
				sql +=" and rc.role_column_access = '"+Utils.isNull(roleCriteria.getRoleColumnAccess())+"' \n ";
			}
			if( !Utils.isNull(roleCriteria.getRoleDataAccess()).equals("")){
				sql +=" and rc.role_data_access = '"+Utils.isNull(roleCriteria.getRoleDataAccess())+"' \n ";
			}
			
			sql +=" order by r.role_name asc \n";
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String roleId ="";
			
			while(rs.next()){
				
				Role role = new Role();
				role.setIndex(index);
				role.setRoleId(rs.getString("role_id"));
				role.setRoleName(rs.getString("role_name"));
				role.setRoleColumnAccess(rs.getString("role_column_access"));
				role.setRoleColumnAccessDesc(rs.getString("role_column_access"));
				
				//Get Description
               List<String> roleDataAccessArray = getRoleDataAccessArray(conn,role.getRoleId(),role.getRoleColumnAccess()," ,");
               String role_data_access_desc ="";
	           String role_data_access = "";
				for(int i=0;i<roleDataAccessArray.size();i++){
					String roleDataAccess =(String)roleDataAccessArray.get(i);
					role_data_access +=roleDataAccess+",";
					if("ALL".equalsIgnoreCase(roleDataAccess)){
						role_data_access_desc  += "ดูข้อมูลได้ทั้งหมด,";
					}else{
						logger.debug("RoledatAccess:"+roleDataAccess);
						if( !Utils.isNull(roleDataAccess).equalsIgnoreCase("")){
							List<References> dataList  = SAInitial.getInstance().getConditionValueList4Role(conn,role.getRoleColumnAccess(),roleDataAccess,null);	
							
							if(dataList !=null && dataList.size()> 1){
								role_data_access_desc  += ((References)dataList.get(1)).getName()+",";
							}else{
								role_data_access_desc  += ((References)dataList.get(0)).getName()+",";
							}
						}
					}
				}//for
				
				if( !Utils.isNull(role_data_access).equals("")){
					role_data_access = role_data_access.substring(0,role_data_access.length()-1);
				}
				if( !Utils.isNull(role_data_access_desc).equals("")){
					role_data_access_desc = role_data_access_desc.substring(0,role_data_access_desc.length()-1);
				}
				
				role.setRoleDataAccessDesc(role_data_access_desc);
				role.setRoleDataAccess(role_data_access);
				role.setStyleClass("group_odd");
				
				if(roleId.equals(rs.getString("role_name"))){
					role.setRoleIdDisp("");
					role.setIndexDisp("");
				}else{
					role.setRoleIdDisp(rs.getString("role_name"));
					indexDisp++;
					role.setIndexDisp(indexDisp+"");
				}
				roleId = rs.getString("role_name");
				
				roleList.add(role);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(conn, ps, rs);
		}
		return roleList;
	}
	
	
	public static List<Role> findRoleListByRole(Role roleCriteria) throws Exception {
		List<Role> roleList = new ArrayList<Role>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int index =0;
		try{
			String sql  = "select distinct r.role_name,r.role_id ,rc.role_column_access from c_role r ,c_role_access rc where 1=1 \n";
			       sql += " and r.role_id = rc.role_id \n";
			if( !Utils.isNull(roleCriteria.getRoleId()).equals("")){
				sql +=" and r.role_id = "+Utils.isNull(roleCriteria.getRoleId())+" \n ";
			}
			if( !Utils.isNull(roleCriteria.getRoleName()).equals("")){
				sql +=" and r.role_name = '"+Utils.isNull(roleCriteria.getRoleName())+"' \n ";
			}
			sql +=" order by role_id \n";
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				index++;
				Role role = new Role();
				role.setIndex(index);
				role.setRoleId(rs.getString("role_id"));
				role.setRoleName(rs.getString("role_name"));
				role.setRoleColumnAccess(rs.getString("role_column_access"));

				//Get Description
	            List<String> roleDataAccessArray = getRoleDataAccessArray(conn,role.getRoleId(),role.getRoleColumnAccess()," ,");
	            String role_data_access_desc ="";
	            String role_data_access = "";
				for(int i=0;i<roleDataAccessArray.size();i++){
					String roleDataAccess =(String)roleDataAccessArray.get(i);
					role_data_access +=roleDataAccess+",";
					
					if("ALL".equalsIgnoreCase(roleDataAccess)){
						role_data_access_desc  += "ดูข้อมูลได้ทั้งหมด,";
					}else{
						logger.debug("RoledatAccess:"+roleDataAccess);
						if( !Utils.isNull(roleDataAccess).equalsIgnoreCase("")){
							List<References> dataList  = SAInitial.getInstance().getConditionValueList4Role(conn,role.getRoleColumnAccess(),roleDataAccess,null);	
							
							if(dataList !=null && dataList.size()> 1){
								role_data_access_desc  += ((References)dataList.get(1)).getName()+",";
							}else{
								role_data_access_desc  += ((References)dataList.get(0)).getName()+",";
							}
						}
					}
				}//for
				
				if( !Utils.isNull(role_data_access).equals("")){
					role_data_access = role_data_access.substring(0,role_data_access.length()-1);
				}
				if( !Utils.isNull(role_data_access_desc).equals("")){
					role_data_access_desc = role_data_access_desc.substring(0,role_data_access_desc.length()-1);
				}
				
				role.setRoleDataAccessDesc(role_data_access_desc);
				role.setRoleDataAccess(role_data_access);
				
				roleList.add(role);
			}
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, ps, rs);
		}
		return roleList;
	}
	
	private static List<String> getRoleDataAccessArray(Connection conn,String roleId,String roleColumnAccess,String newLine) throws Exception {
		List<String> roleDataAccessS = new ArrayList<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int no = 0;
		try{
			String sql  = " select rc.role_data_access   \n";
			       sql += " from c_role_access rc where 1=1\n";
			       sql += " and rc.role_id = "+roleId+" \n";
			       sql += " and rc.role_column_access = '"+roleColumnAccess+"' \n";
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()){
				roleDataAccessS.add(rs.getString("role_data_access"));
				no++;
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(null, ps, rs);
		}
		return roleDataAccessS;
	}
	

	private  static String getRoleDataAccessDesc(Connection conn,String roleColumn,String roleAccess )throws Exception{
		String sql = "";
		String msg = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<References> returnList = new ArrayList<References>();
		try{
			
			if("ALL".equalsIgnoreCase(roleColumn)){
				msg = SAConstants.MSG_ALL_TH;
			}else if("inventory_item_id".equalsIgnoreCase(roleColumn)){
				sql  = "SELECT  CODE ,TH_NAME FROM c_reference where GROUP_NAME ='SKU' \n";
				sql +=" AND CODE ='"+roleAccess+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()){
					msg = rs.getString("TH_NAME");
				}
			}else if("Customer_Category".equalsIgnoreCase(roleColumn)){
				sql  = "SELECT  CODE ,TH_NAME FROM c_reference where GROUP_NAME ='CUST_CATE' \n";
				sql +=" AND CODE ='"+roleAccess+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()){
					msg = rs.getString("TH_NAME");
				}
			}else if("Division".equalsIgnoreCase(roleColumn)){
				msg = SAConstants.MSG_ALL_TH;
			}else if("Salesrep_id".equalsIgnoreCase(roleColumn)){
				sql  = "SELECT  CODE ,TH_NAME FROM c_reference where GROUP_NAME ='CUST_CATE' \n";
				sql +=" AND CODE ='"+roleAccess+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()){
					msg = rs.getString("TH_NAME");
				}
			}else if("Sales_Channel".equalsIgnoreCase(roleColumn)){
				if("ALL".equalsIgnoreCase(roleAccess)){
					msg = SAConstants.MSG_ALL_TH;
				}else{
					sql  = "SELECT  DISTINCT SALES_CHANNEL_NO AS CODE ,SALES_CHANNEL_DESC AS TH_NAME FROM XXPENS_BI_MST_SALES_CHANNEL \n";
					sql += " WHERE SALES_CHANNEL_NO ='"+roleAccess+"'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					
					if(rs.next()){
						msg = rs.getString("TH_NAME");
					}
				}
			}else if("Customer_Group".equalsIgnoreCase(roleColumn)){
				sql  = "SELECT  CODE ,TH_NAME FROM c_reference where GROUP_NAME ='CUST_CATE' \n";
				sql +=" AND CODE ='"+roleAccess+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()){
					msg = rs.getString("TH_NAME");
				}
			}else if("Customer_id".equalsIgnoreCase(roleColumn)){
				sql  = "SELECT  CODE ,TH_NAME FROM c_reference where GROUP_NAME ='CUST_CATE' \n";
				sql +=" AND CODE ='"+roleAccess+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				if(rs.next()){
					msg = rs.getString("TH_NAME");
				}
			}else if("Brand".equalsIgnoreCase(roleColumn)){
				if("ALL".equalsIgnoreCase(roleAccess)){
					msg = SAConstants.MSG_ALL_TH;
				}else{
					sql  = " select  brand_no as CODE,brand_desc as TH_NAME from XXPENS_BI_MST_BRAND where brand_no is not null \n";
					sql +=" AND brand_no ='"+roleAccess+"'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						msg = rs.getString("TH_NAME");
					}
				}
			}
			logger.debug("SQL:"+sql);
		}catch(Exception e){
			logger.debug("SQL:"+sql);
		   throw e;
		}finally{
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return msg;
	}
	
	private static String getGroupNameOfSubRole(String roleColumnAccess) throws Exception{
		String groupName = "";
		if( roleColumnAccess.equalsIgnoreCase("Customer_Category") || 
			roleColumnAccess.equalsIgnoreCase("Salesrep_id") ||
			roleColumnAccess.equalsIgnoreCase("Customer_id")
		  ){
			groupName = "'CUST_CATE','ALL'";
		}else if(roleColumnAccess.equalsIgnoreCase("inventory_item_id")){
			groupName = "'SKU','ALL'";
		}
		
		return groupName;
	}
	
	public static void initailRoleList(HttpServletRequest requestWeb){
		try{

			/** initail ColumnAccessList  ***/
			List<References> roleColumnAccessList = new ArrayList<References>();
			roleColumnAccessList.addAll(findCReferenceByType("GROUP_BY"));
			requestWeb.getSession().setAttribute("roleColumnAccessList", roleColumnAccessList);
			
			
			/** initail ColumnAccessList  ***/
//			List<References> roleDataAccessList = new ArrayList<References>();
//			roleDataAccessList.addAll(findCReferenceByType("SUB_ROLE"));
//			requestWeb.getSession().setAttribute("roleDataAccessList", roleDataAccessList);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static List<References> findCReferenceByType(String type) {
		try{
			return findCReferenceByType();
		}finally{
			
		}
	}
	
	/*
	COLUMN_KEY_MAP.put("Brand", "Brand");//Brand
	COLUMN_KEY_MAP.put("Division","Division"); //Division
	COLUMN_KEY_MAP.put("Sales_Channel", "Sales_Channel"); //Area ภาคตามพนักงานขาย
	COLUMN_KEY_MAP.put("Customer_Category","customer_category");//SaleType
	COLUMN_KEY_MAP.put("Salesrep_id", "customer_category");	//SalesMan
	*/
	public static List<References> findCReferenceByType() {
		List<References> dataList = new ArrayList<References>();
		try{
			References r = new References("Brand", SecurityHelper.COLUMN_KEY_MAP.get("Brand"));
			dataList.add(r);
			r = new References("Division",SecurityHelper.COLUMN_KEY_MAP.get("Division"));
			dataList.add(r);
			r = new References("Sales_Channel", SecurityHelper.COLUMN_KEY_MAP.get("Sales_Channel"));
			dataList.add(r);
			r = new References("Customer_Category",SecurityHelper.COLUMN_KEY_MAP.get("Customer_Category"));
			dataList.add(r);
			r = new References("Salesrep_id", SecurityHelper.COLUMN_KEY_MAP.get("Salesrep_id"));
			dataList.add(r);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			
		}
		return dataList;
	}
	
	
	@Deprecated
	public static List<References> findCReferenceByTypeTemp(Connection conn,String type,String code,String groupName) {
		List<References> dataList = new ArrayList<References>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select distinct code,th_name ,order_index from c_reference where 1=1  \n" ;
	        if( !Utils.isNull(type).equals("")){
	        	  sql += "and type ='"+type+"' \n" ;
	        }
	        if( !Utils.isNull(code).equals("")){
	        	  sql += "and code ='"+code+"' \n" ;
	        }
	        if( !Utils.isNull(groupName).equals("")){
	        	  sql += "and group_name in("+groupName+") \n" ;
	        }
			sql +="order by order_index \n";
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs.getString("CODE"),rs.getString("TH_NAME"));
				dataList.add(r);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(null, ps, rs);
		}
		return dataList;
	}
	
	public static String getRoleDataAccess(String type,String roleColumnAccess){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String str ="";
		try{
			String groupName = getGroupNameOfSubRole(roleColumnAccess);
			
			String sql = "select * from c_reference where 1=1  \n" ;
	        if( !Utils.isNull(type).equals("")){
	        	  sql += "and type ='"+type+"' \n" ;
	        }
	        if( !Utils.isNull(groupName).equals("")){
	        	  sql += "and group_name in("+groupName+") \n" ;
	        }
			sql +="order by order_index \n";
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				str +=rs.getString("code")+"|"+rs.getString("TH_NAME")+",";
			}
			
			if(str !=null && str.length()> 0){
				str = str.substring(0,str.length()-1);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(conn, ps, rs);
		}
		return str;
	}
	
	
	
	public static boolean isRoleNameExist(String roleName) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String str ="";
		try{
		    String sql = "select * from c_role where 1=1  \n" ;
	        if( !Utils.isNull(roleName).equals("")){
	        	  sql += "and role_name ='"+roleName+"' \n" ;
	        }
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
			return false;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, ps, rs);
		}
	}
}
