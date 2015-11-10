package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MCEmpBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class MCEmpDAO {

	 private static Logger logger = Logger.getLogger("PENS");
	 
	 public static MCEmpBean insertMCEmpModel(Connection conn,MCEmpBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertMCEmpModel");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("INSERT INTO MC_EMPLOYEE \n");
				sql.append("(EMP_REF_ID, EMPLOYEE_ID, EMP_TYPE, TITLE,  \n");
				sql.append("NAME, SURNAME, MOBILE1, MOBILE2,  \n");
				sql.append("STATUS, START_DATE,NOTE, REGION,  \n");
				sql.append("CREATE_DATE, CREATE_USER ) \n");
				sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n");

				ps = conn.prepareStatement(sql.toString());
				
				//Gen EmpRefId
				int empRefId = SequenceProcess.getNextValue("MC_EMPLOYEE");
				o.setEmpRefId(empRefId+"");
				
				int c =1;
				ps.setInt(c++, empRefId);
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setString(c++, Utils.isNull(o.getEmpType()));
				ps.setString(c++, Utils.isNull(o.getTitle()));
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getSurName());
				ps.setString(c++, o.getMobile1());
				ps.setString(c++, o.getMobile2());
				ps.setString(c++, o.getStatus());
				ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				ps.setString(c++, Utils.isNull(o.getNote()));
				ps.setString(c++, Utils.isNull(o.getRegion()));
				
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
	

				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return o;
		}
	 
	 public static int updateMCEmpModel(Connection conn,MCEmpBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE MC_EMPLOYEE SET   \n");
				sql.append(" EMPLOYEE_ID =? ,EMP_TYPE = ?,TITLE =?,  \n");
				sql.append(" NAME =? ,SURNAME = ?,MOBILE1 =?,   \n");
				sql.append(" MOBILE2 =? , NOTE =? ,REGION = ?,  \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ? ,START_DATE =? ,END_DATE = ? ,REASON_LEAVE =?  \n");
				sql.append(" WHERE EMP_REF_ID = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
	
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setString(c++, Utils.isNull(o.getEmpType()));
				ps.setString(c++, Utils.isNull(o.getTitle()));
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getSurName());
				ps.setString(c++, o.getMobile1());
				ps.setString(c++, o.getMobile2());
				ps.setString(c++, Utils.isNull(o.getNote()));
				ps.setString(c++, Utils.isNull(o.getRegion()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				if( !Utils.isNull(o.getStartDate()).equals("")){
				    ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				    ps.setTimestamp(c++,null);	
				}
				if( !Utils.isNull(o.getEndDate()).equals("")){
				    ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
				    ps.setTimestamp(c++,null);	
				}
				ps.setString(c++, Utils.isNull(o.getReasonLeave()));
				
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
				
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
	 

   public static MCEmpBean searchHead(Connection conn,MCEmpBean o,boolean getTrans ) throws Exception {
	  return searchHeadModel(conn, o,getTrans);
	}
   
   public static MCEmpBean searchHead(MCEmpBean o ,boolean getTrans) throws Exception {
	   Connection conn = null;
	   try{
		  conn = DBConnection.getInstance().getConnection();
		  return searchHeadModel(conn, o,getTrans);
	   }catch(Exception e){
		   throw e;
	   }finally{
		   conn.close();
	   }
	}
   
	public static MCEmpBean searchHeadModel(Connection conn,MCEmpBean o ,boolean getTrans) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			MCEmpBean h = null;
			List<MCEmpBean> items = new ArrayList<MCEmpBean>();
			int r = 1;
			try {
			   sql.append(" \n select S.*" );
			   sql.append("\n ,(select pens_desc from MC_MST_REFERENCE M where M.reference_code='MCarea' and M.pens_value=S.region ) as region_desc");
			   sql.append("\n ,(SELECT M.pens_desc from MC_MST_REFERENCE M");
			   sql.append("\n   where 1=1 and reference_code ='StaffType' ");
			   sql.append("\n   and M.pens_value =S.emp_type ) emp_type_desc");
			   
			   sql.append("\n  from MC_EMPLOYEE S WHERE 1=1");
			   if( !Utils.isNull(o.getEmpId()).equals("") && !Utils.isNull(o.getEmpId()).equalsIgnoreCase("ALL")){
					String sqlIn = Utils.converToTextSqlIn(Utils.isNull(o.getEmpId()));
					sql.append("\n and S.employee_id in( "+sqlIn+")");
			   }
			   if( !Utils.isNull(o.getEmpRefId()).equals("")){
					sql.append("\n and S.emp_ref_id ="+o.getEmpRefId());
				}
			   
				if( !Utils.isNull(o.getEmpType()).equals("")){
					sql.append("\n and S.emp_type = '"+Utils.isNull(o.getEmpType())+"'");
				}
				sql.append("\n order by S.EMP_REF_ID asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new MCEmpBean();
				   h.setNo(r);
				   h.setEmpRefId(Utils.isNull(rst.getString("emp_ref_id")));
				   if(rst.getInt("employee_id") != 0){
				     h.setEmpId(Utils.isNull(rst.getString("employee_id")));
				     h.setOrgEmpId(Utils.isNull(rst.getString("employee_id")));
				   }else{
					 h.setEmpId(""); 
					 h.setOrgEmpId("");
				   }
				   h.setEmpType(Utils.isNull(rst.getString("emp_type")));
				   h.setEmpTypeDesc(Utils.isNull(rst.getString("emp_type_desc")));
				   h.setTitle(Utils.isNull(rst.getString("title")));
				   h.setName(Utils.isNull(rst.getString("name")));
				   h.setSurName(Utils.isNull(rst.getString("surname")));
				   h.setMobile1(Utils.isNull(rst.getString("mobile1")));
				   h.setMobile2(Utils.isNull(rst.getString("mobile2")));
				   h.setStatus(Utils.isNull(rst.getString("status")));
				   h.setReasonLeave(Utils.isNull(rst.getString("reason_leave")));
				   h.setNote(Utils.isNull(rst.getString("note")));
				   h.setRegion(Utils.isNull(rst.getString("region")));
				   h.setRegionDesc(Utils.isNull(rst.getString("region_desc")));
				   
				   h.setStartDate(Utils.stringValue(rst.getDate("start_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   if(rst.getDate("end_date") !=null){
				     h.setEndDate(Utils.stringValue(rst.getDate("end_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					 h.setEndDate("");
				   }
				   items.add(h);
				   r++;
				   
				}//while
				
				//set Result 
				o.setItems(items);
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		return o;
	}
	 
	
	 public static  String getTransDetail(Connection conn,String staffId,String monthTrip,String dayOfWeek) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String detail = "";
			try {
				sql.append("\n select D.detail from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1 \n");
				sql.append("\n and D.staff_id = "+Utils.isNull(staffId)+"");
				sql.append("\n and D.month_trip = '"+Utils.isNull(monthTrip)+"'");
				sql.append("\n and D.day_of_week = '"+Utils.isNull(dayOfWeek)+"'");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
				  detail = Utils.isNull(rst.getString("detail"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return detail;
		}
	 
	 public static  String getTransDetailByDay(Connection conn,String staffId,String monthTrip,String day) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String detail = "";
			try {
				sql.append("\n select D.detail from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1 \n");
				sql.append("\n and D.staff_id = "+Utils.isNull(staffId)+"");
				sql.append("\n and D.month_trip = '"+Utils.isNull(monthTrip)+"'");
				sql.append("\n and D.day = '"+Utils.isNull(day)+"'");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
				  detail = Utils.isNull(rst.getString("detail"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return detail;
		}
	
}
