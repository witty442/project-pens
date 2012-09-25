package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.GroupRole;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

public class MGroupRole {
	
	private static Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void initailRoleList(HttpServletRequest requestWeb){
		try{
			/** initail RoleList  ***/
			requestWeb.getSession().setAttribute("roleList", findRoleList());
		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void initailGroupList(HttpServletRequest requestWeb){
		try{
			/** initail GroupRoleList  ***/
			List<References> dataList = new ArrayList<References>();
			References r = new References("ALL","ALL");
			dataList.add(r);
			dataList.addAll(findGroupList());
			requestWeb.getSession().setAttribute("critriaGroupList", dataList);
			
			requestWeb.getSession().setAttribute("groupList", findGroupList());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static boolean saveGroupRole(List<GroupRole> roleList) throws Exception{
		Connection conn = null;
		PreparedStatement psIns = null;
		PreparedStatement psUpd = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			psIns = conn.prepareStatement("insert into ad_group_role(user_group_id,user_group_name,role_id) values(?,?,?)");
			psUpd = conn.prepareStatement("update ad_group_role set user_group_name =? ,role_id =? where user_group_id =? ");
			
			if(roleList != null && roleList.size() > 0){
				for(int i=0;i<roleList.size();i++){
					GroupRole role =(GroupRole)roleList.get(i);
					logger.debug("userGroupId:"+role.getUserGroupId());
					if( !Utils.isNull(role.getUserGroupId()).equals("")){
					   // Update 
					   psUpd.setString(1, role.getUserGroupName());
					   psUpd.setString(2, role.getRoleId());
					   psUpd.setInt(3,Utils.convertToInt(role.getUserGroupId()));
					   psUpd.executeUpdate();
					   
					}else{
					   // Insert 
					   int seqId = SequenceProcess.getNextValue(conn,"ad_group_role", "user_group_id");
					   logger.debug("seqId:"+seqId);
					   
					   psIns.setInt(1,seqId );
					   psIns.setString(2, role.getUserGroupName());
					   psIns.setString(3, role.getRoleId());
					   psIns.executeUpdate();
				    }
				}

			}
			conn.commit();
			
		}catch(Exception e){
			conn.rollback();
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(conn, psIns, null);
			DBConnection.getInstance().closeConn(null, psUpd, null);
		}
		return true;
	}
	
	
	public static boolean deleteGroupRole(Connection conn,String userGroupId){

		PreparedStatement ps = null;
		try{
			logger.debug("userGroupId:"+userGroupId);
			String sql = "delete from ad_group_role where 1=1 ";
			if( !Utils.isNull(userGroupId).equals("")){
				sql +=" and user_group_id = '"+userGroupId+"'";
			}
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql);
			
			ps.execute();
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(null, ps, null);
		}
		return true;
	}
	
	
	
	public static List<References> findRoleList(){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<References> dataList = new ArrayList<References>();
		try{
			String sql = "select distinct role_id ,role_name from ad_role where 1=1  \n" ;
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs.getString("role_id"),rs.getString("role_name"));
				dataList.add(r);
			}

		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(conn, ps, rs);
		}
		return dataList;
	}
	
	public static List<References> findGroupList(){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<References> dataList = new ArrayList<References>();
		try{
			String sql = "select distinct user_group_id ,user_group_name from ad_group_role where 1=1 order by user_group_id \n" ;
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				References r = new References(rs.getString("user_group_id"),rs.getString("user_group_name"));
				dataList.add(r);
			}

		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(conn, ps, rs);
		}
		return dataList;
	}
	
	public static String getUserGroupName(int userGroupId){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select user_group_id ,user_group_name from ad_group_role where user_group_id ="+userGroupId+"\n" ;
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
			   return rs.getString("user_group_name");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(conn, ps, rs);
		}
		return "";
	}
	
	public static List<GroupRole> findGroupRoleList(GroupRole groupRole){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<GroupRole> dataList = new ArrayList<GroupRole>();
		int index =0;
		try{
			String sql = "select user_group_id,user_group_name,role_id from ad_group_role where 1=1  \n" ;
			if( !Utils.isNull(groupRole.getUserGroupId()).equals("")){
				sql +=" and user_group_id ='"+groupRole.getUserGroupId()+"'";
			}
			sql += " order by user_group_name,user_group_id \n";
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				index++;
				GroupRole g = new GroupRole();
				g.setUserGroupId(rs.getString("user_group_id"));
				g.setUserGroupName(rs.getString("user_group_name"));
				g.setRoleId(rs.getString("role_id"));
				g.setIndex(index);
				g.setReadonly("readonly");
				g.setDisabled("disabled");
				dataList.add(g);
			}

		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			DBConnection.getInstance().closeConn(conn, ps, rs);
		}
		return dataList;
	}
	
}
