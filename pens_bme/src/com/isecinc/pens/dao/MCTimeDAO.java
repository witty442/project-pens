package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class MCTimeDAO {
	
	  private static Logger logger = Logger.getLogger("PENS");
	 
	  public static void saveMCStaffTime(MCBean h, List<MCBean> itemList) throws Exception{
		  Connection conn=null;
		  try{
			  conn = DBConnection.getInstance().getConnection();
			  conn.setAutoCommit(false);
			  
			  for(MCBean item:itemList){
				  item.setEmpId(h.getEmpId());
				  item.setEmpRefId(h.getEmpRefId());
				  item.setCreateUser(h.getCreateUser());
				  item.setUpdateUser(h.getUpdateUser());
				  
				  int update = updateMCStaffTimeModel(conn, item);
				  if(update ==0){
					  insertMCStaffTimeModel(conn, item);
				  }
				  if(update==0){
					  logger.debug("insert");
				  }else{
					  logger.debug("update");
				  }
			  }
			  
			  conn.commit();
		  }catch(Exception e){
			  conn.rollback();
			  throw e;
			}finally{
				if(conn != null){
					conn.close();conn=null;
				}
			}
	  }
	  
	  public static void insertMCStaffTimeModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			//logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO MC_STAFF_TIME \n");
				sql.append(" (EMP_REF_ID, EMPLOYEE_ID,STAFF_DATE, START_TIME,END_TIME,TOTAL_TIME,REASON_LEAVE,NOTE, CREATE_DATE, CREATE_USER) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?, ?, ?, ?, ? ,?,?,?,?,?) \n");

				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getStaffDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				
				ps.setString(c++, o.getStartTime());
				ps.setString(c++, o.getEndTime());
				ps.setString(c++, o.getTotalTime());
				ps.setString(c++, o.getReasonLeave());
				ps.setString(c++, o.getNote());
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
		}
	 
	 public static int updateMCStaffTimeModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE MC_STAFF_TIME SET   \n");
				sql.append(" EMPLOYEE_ID =?,START_TIME = ? ,END_TIME=?, \n");
				sql.append(" TOTAL_TIME=?, REASON_LEAVE=?, NOTE=?,   \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE EMP_REF_ID = ? AND  STAFF_DATE =? \n" );

				logger.debug("Update by emp_ref_id["+o.getEmpRefId()+"]staffDate["+o.getStaffDate()+"]");
				ps = conn.prepareStatement(sql.toString());
	
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setString(c++, o.getStartTime());
				ps.setString(c++, o.getEndTime());
				ps.setString(c++, o.getTotalTime());
				ps.setString(c++, o.getReasonLeave());
				ps.setString(c++, o.getNote());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
				ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getStaffDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
	 
	  public static MCBean searchStaffTime(Connection conn,MCBean o,boolean getTrans ) throws Exception {
		  return searchStaffTimeModel(conn, o,getTrans);
	  }
	   
	   public static MCBean searchStaffTime(MCBean o ,boolean getTrans) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchStaffTimeModel(conn, o,getTrans);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	   
		public static MCBean searchStaffTimeModel(Connection conn,MCBean o ,boolean getTrans) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			MCBean h = null;
			List<MCBean> items = new ArrayList<MCBean>();
			int r = 1;
			try {
				   String christYear = (Integer.parseInt(o.getStaffYear())-543)+"";
					 
				   sql.append(" \n SELECT S.emp_ref_id ,s.employee_id,S.emp_type,S.title,S.name,S.surname,S.mobile1,S.mobile2 ");
				   sql.append(" \n,S.status,S.reason_leave, S.note,S.region  " );
				   
				   sql.append("\n,( SELECT M.pens_desc from MC_MST_REFERENCE M");
				   sql.append("\n   where 1=1 and reference_code ='StaffType' ");
				   sql.append("\n   and M.pens_value =S.emp_type ) emp_type_desc");
				   
				   sql.append("\n,( SELECT M.pens_desc from MC_MST_REFERENCE M");
				   sql.append("\n   where 1=1 and reference_code ='MCarea' ");
				   sql.append("\n   and M.pens_value =S.region ) region_desc");
					
				   sql.append(" \n,nvl((select count(*) as c from MC_STAFF_TIME T ");
				   sql.append("\n      where T.emp_ref_id =S.emp_ref_id " );
				   sql.append("\n      and EXTRACT(year FROM T.staff_date) = '"+christYear+"'");
				   sql.append("\n      and EXTRACT(month FROM T.staff_date) = '"+Utils.isNull(o.getStaffMonth())+"'");
				   sql.append("\n      group by T.emp_ref_id),0) as time_count " );
				   sql.append(" \n from MC_EMPLOYEE S " );
				   
				    sql.append("\n INNER JOIN ( ");
					sql.append("\n    SELECT distinct R.mc_area as region ,MT.emp_ref_id ,MT.employee_id ");
					sql.append("\n    ,MT.is_active ,MT.mc_route,R.route_name" );
					sql.append("\n    from MC_STAFF_ROUTE MT,MC_ROUTE R ");
					sql.append("\n    WHERE  MT.mc_route = R.route_id");
					sql.append("\n  )M  ON  M.region = S.region AND M.emp_ref_id = S.emp_ref_id ");
					
				   sql.append(" \n WHERE 1=1  ");
				   sql.append("\n and s.status = 'A' ");
				   if( !Utils.isNull(o.getMcArea()).equals("")){
					    sql.append("\n and s.region = '"+Utils.isNull(o.getMcArea())+"'");
					}
				   if( !Utils.isNull(o.getEmpRouteName()).equals("")){
					    sql.append("\n and M.route_name = '"+Utils.isNull(o.getEmpRouteName())+"'");
					}
				   if( !Utils.isNull(o.getEmpId()).equals("") && !Utils.isNull(o.getEmpId()).equalsIgnoreCase("ALL")){
						sql.append("\n and S.employee_id = "+Utils.isNull(o.getEmpId())+"");
				   }
				   if( !Utils.isNull(o.getEmpType()).equals("")){
						sql.append("\n and S.emp_type = '"+Utils.isNull(o.getEmpType())+"'");
				   }
				   if( !Utils.isNull(o.getName()).equals("")){
						sql.append("\n and S.name = '"+Utils.isNull(o.getName())+"'");
				   }

					sql.append("\n order by S.EMP_REF_ID DESC ");
					
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql.toString());
					rst = ps.executeQuery();
					
					while(rst.next()) {
					   h = new MCBean();
					   h.setNo(r);
					   h.setEmpRefId(Utils.isNull(rst.getString("emp_ref_id")));
					   if(rst.getInt("employee_id") != 0){
					     h.setEmpId(Utils.isNull(rst.getString("employee_id")));
					   }else{
						 h.setEmpId(""); 
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
					   if(rst.getInt("time_count") >0){
						   h.setStaffTimeExist(true);
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
		
	 public static 	List<MCBean> searchStaffTimeDetail(MCBean o ) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchStaffTimeDetailModel(conn, o);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	   
		public static List<MCBean> searchStaffTimeDetailModel(Connection conn,MCBean o ) throws Exception {
				PreparedStatement ps = null;
				ResultSet rst = null;
				StringBuilder sql = new StringBuilder();
				MCBean h = null;
				List<MCBean> items = new ArrayList<MCBean>();
				int r = 1;
				try {
				   String christYear = (Integer.parseInt(o.getStaffYear())-543)+"";
				   sql.append(" \n select * " );
				   sql.append(" \n from MC_STAFF_TIME  " );
				  
				   sql.append(" \n WHERE 1=1  ");
				   sql.append(" \n   and EXTRACT(year FROM staff_date) = '"+christYear+"'");
				   sql.append(" \n   and EXTRACT(month FROM staff_date) = '"+Utils.isNull(o.getStaffMonth())+"'");
                   sql.append(" \n   and emp_ref_id = "+Utils.isNull(o.getEmpRefId())+"");
					sql.append("\n order by EMP_REF_ID asc ");
					
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql.toString());
					rst = ps.executeQuery();
					
					while(rst.next()) {
					   h = new MCBean();
					   h.setNo(r);
					   h.setEmpRefId(Utils.isNull(rst.getString("emp_ref_id")));
					   h.setStaffDate(Utils.stringValue(rst.getDate("staff_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					   h.setStartTime(Utils.isNull(rst.getString("start_time")));
					   h.setEndTime(Utils.isNull(rst.getString("end_time")));
					   h.setTotalTime(Utils.isNull(rst.getString("total_time")));
					   h.setReasonLeave(Utils.isNull(rst.getString("reason_leave")));
					   h.setNote(Utils.isNull(rst.getString("note")));

					   if(Utils.isHoliday(h.getStaffDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)){
							h.setHoliday(true);
						}
					   items.add(h);
					   r++;
					   
					}//while
					
				} catch (Exception e) {
					throw e;
				} finally {
					try {
						rst.close();
						ps.close();
					} catch (Exception e) {}
				}
			return items;
		}
}
