package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

public class MCDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 
	 public static void insertMCStaffModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.MC_STAFF \n");
				sql.append(" (STAFF_ID, NAME, SURNAME, MC_AREA, MOBILE, IS_ACTIVE, CREATE_DATE, CREATE_USER, MC_ROUTE, STAFF_TYPE) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?, ?, ?, ?, ?, ?, ?,?,?,?) \n");

				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getSureName());
				ps.setString(c++, o.getMcArea());
				ps.setString(c++, o.getMobile());
				ps.setString(c++, "Y");
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getMcRoute());
				ps.setString(c++, Utils.isNull(o.getStaffType()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static int updateMCStaffModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.MC_STAFF SET   \n");
				sql.append(" NAME =? ,SURNAME = ?,   \n");
				sql.append(" MC_AREA =? ,MOBILE = ?,   \n");
				sql.append(" IS_ACTIVE =? ,MC_ROUTE = ?,   \n");
				sql.append(" STAFF_TYPE =? ,  \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE STAFF_ID = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getSureName());
				ps.setString(c++, o.getMcArea());
				ps.setString(c++, o.getMobile());
				ps.setString(c++, Utils.isNull(o.getActive()));
				ps.setString(c++, o.getMcRoute());
				ps.setString(c++, Utils.isNull(o.getStaffType()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
	 
	 public static int updateMCStaffModelByDummyStaffId(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.MC_STAFF SET   \n");
				sql.append(" NAME =? ,SURNAME = ?,   \n");
				sql.append(" MC_AREA =? ,MOBILE = ?,   \n");
				sql.append(" IS_ACTIVE =? ,MC_ROUTE = ?,   \n");
				sql.append(" STAFF_TYPE =? ,  \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ? ,STAFF_ID = ? \n");
				sql.append(" WHERE STAFF_ID = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getSureName());
				ps.setString(c++, o.getMcArea());
				ps.setString(c++, o.getMobile());
				ps.setString(c++, Utils.isNull(o.getActive()));
				ps.setString(c++, o.getMcRoute());
				ps.setString(c++, Utils.isNull(o.getStaffType()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setInt(c++, Utils.convertStrToInt(o.getOrgStaffId()));
				
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
	 
	 public static void insertMCTransModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.MC_TRANS \n");
				sql.append(" (STAFF_ID, NAME, SURENAME, MC_AREA, MONTH_TRIP, CREATE_DATE, CREATE_USER,MC_ROUTE,REMARK,STAFF_TYPE) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?, ?, ?, ?, ?, ?, ?,?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getSureName());
				ps.setString(c++, o.getMcArea());
				ps.setString(c++, o.getMonthTrip());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getMcRoute());
				ps.setString(c++, Utils.isNull(o.getRemark()));
				ps.setString(c++, Utils.isNull(o.getStaffType()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void insertMCTransDetail(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.MC_TRANS_DETAIL \n");
				sql.append(" (STAFF_ID, MONTH_TRIP,DAY,DETAIL, CREATE_DATE, CREATE_USER ,DAY_OF_WEEK) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?, ?, ?, ?, ?, ? ,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, o.getMonthTrip());
				ps.setString(c++, o.getDay());
				ps.setString(c++, o.getDetail());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getDayOfWeek());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	

		public static int updateMCTransModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.MC_TRANS SET REMARK = ? ,  \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE STAFF_ID = ? and MONTH_TRIP = ? \n" );

				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getRemark()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, Utils.isNull(o.getMonthTrip()));

				int r =ps.executeUpdate();
				logger.debug("Update staff_id ="+o.getStaffId()+",month_trip="+o.getMonthTrip()+",result:"+r);
				
				return r;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
		
	public static int updateMCTransDetailModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.MC_TRANS_DETAIL SET   \n");
				sql.append(" DETAIL= ? ,UPDATE_USER =? ,UPDATE_DATE = ? ,DAY_OF_WEEK = ?  \n");
				
				sql.append(" WHERE STAFF_ID = ? and MONTH_TRIP = ? and DAY = ? \n" );

				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getDetail()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getDayOfWeek());
				
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, Utils.isNull(o.getMonthTrip()));
				ps.setInt(c++, Utils.convertStrToInt(o.getDay()));
				
				int r =ps.executeUpdate();
				logger.debug("Update Result:"+r);
				
				return r;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
		
   public static MCBean searchHead(Connection conn,MCBean o,boolean getTrans ) throws Exception {
	  return searchHeadModel(conn, o,getTrans);
	}
   
   public static MCBean searchHead(MCBean o ,boolean getTrans) throws Exception {
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
   
	public static MCBean searchHeadModel(Connection conn,MCBean o ,boolean getTrans) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			MCBean h = null;
			List<MCBean> items = new ArrayList<MCBean>();
			int r = 1;
			try {
				sql.append("select M.* ");
				sql.append("\n,(select A.pens_desc from mc_mst_reference A where A.pens_value=M.mc_area and reference_code='MCarea')as mc_area_desc" );
				sql.append("\n,(select A.route_name from mc_route A where A.route_id= M.mc_route and A.mc_area=M.mc_area)as mc_route_desc" );
				sql.append("\n from ("); 
				
				   sql.append(" \n select S.staff_id,S.staff_type,S.name,S.surname,S.mc_area,S.mc_route,S.mobile from MC_STAFF S WHERE 1=1");
				   sql.append("\n and S.staff_id not in(  "); 
				   sql.append("\n   select staff_id from MC_TRANS WHERE  month_trip = '"+Utils.isNull(o.getMonthTrip())+"'");
				   sql.append("\n )");
				   
				   if( !Utils.isNull(o.getStaffId()).equals("") && !Utils.isNull(o.getStaffId()).equalsIgnoreCase("ALL")){
						String sqlIn = Utils.converToTextSqlIn(Utils.isNull(o.getStaffId()));
						sql.append("\n and S.staff_id in( "+sqlIn+")");
					}
					if( !Utils.isNull(o.getMcArea()).equals("")){
						sql.append("\n and S.mc_area = "+Utils.isNull(o.getMcArea())+"");
					}
					if( !Utils.isNull(o.getMcRoute()).equals("")){
						sql.append("\n and S.mc_route = "+Utils.isNull(o.getMcRoute())+"");
					}
					if( !Utils.isNull(o.getStaffType()).equals("")){
						sql.append("\n and S.staff_type = '"+Utils.isNull(o.getStaffType())+"'");
					}

				   sql.append(" \n UNION ALL ");
				   
				   sql.append(" \n select S.staff_id,S.staff_type,S.name,S.surename,S.mc_area,S.mc_route" +
				   		     "  \n  ,(select A.mobile from MC_STAFF A WHERE A.staff_id = S.staff_id) as mobile" +
				   		     "  \n  from MC_TRANS S WHERE 1=1 \n");
				   if( !Utils.isNull(o.getStaffId()).equals("") && !Utils.isNull(o.getStaffId()).equalsIgnoreCase("ALL")){
						String sqlIn = Utils.converToTextSqlIn(Utils.isNull(o.getStaffId()));
						sql.append("\n and S.staff_id in( "+sqlIn+")");
					}
					if( !Utils.isNull(o.getMcArea()).equals("")){
						sql.append("\n and S.mc_area = "+Utils.isNull(o.getMcArea())+"");
					}
					if( !Utils.isNull(o.getMcRoute()).equals("")){
						sql.append("\n and S.mc_route = "+Utils.isNull(o.getMcRoute())+"");
					}
					if( !Utils.isNull(o.getStaffType()).equals("")){
						sql.append("\n and S.staff_type = '"+Utils.isNull(o.getStaffType())+"'");
					}
					if( !Utils.isNull(o.getMonthTrip()).equals("")){
						sql.append("\n and S.month_trip = '"+Utils.isNull(o.getMonthTrip())+"'");
					}
			    sql.append(" ) M WHERE 1=1");

				sql.append("\n order by M.staff_id asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new MCBean();
				   h.setNo(r);
				   h.setStaffId(Utils.isNull(rst.getString("staff_id")));
				   h.setStaffType(Utils.isNull(rst.getString("staff_type")));
				   h.setName(rst.getString("name"));
				   h.setSureName(rst.getString("surname"));
				   h.setMcArea(rst.getString("mc_area"));
				   h.setMcAreaDesc(rst.getString("mc_area_desc"));
				   
				   h.setMcRoute(Utils.isNull(rst.getString("mc_route")));
				   h.setMcRouteDesc(Utils.isNull(rst.getString("mc_route_desc")));
				   
	               h.setMobile(Utils.isNull(rst.getString("mobile")));
	               h.setMonthTrip(o.getMonthTrip());
	               h.setMonthTripDesc(getMonthTripDesc(conn, o.getMonthTrip()));
	               
	               Map<String,String> daysMap = searchTransDetail(conn,h);
	               h.setDaysMap(daysMap);
	               
	               //get Trans head
	               if(getTrans){
		               MCBean hTrans = searchTrans(conn,h);
		               h.setRemark(hTrans.getRemark());
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
	 
	 public static void copyFromLastMonthModel(Connection conn,String staffId,String currentMonthTrip,String prevMonthTrip) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String detail = "";
			int nextDay = 0;
			String nextDayStr = "";
			try {
				sql.append("\n select D.* from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1  \n");
			    sql.append("\n and D.staff_id = "+Utils.isNull(staffId)+"");
				sql.append("\n and D.month_trip = '"+Utils.isNull(currentMonthTrip)+"'");
				sql.append("\n order by D.day asc ");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
					String day = rst.getString("DAY");
				    String dayOfWeek = rst.getString("DAY_OF_WEEK");
				    
				    if("01".equals(day)){
				        //Get First Detail By prevMonthTrip,staffId,dayOfWeek
					    MCBean firstDayOfWeekBean = getFirstTransDetailBydayOfWeek(conn, staffId, prevMonthTrip, dayOfWeek);
					    detail = firstDayOfWeekBean.getDetail();
					    
					    //set NextDay = firstDayOfWeek +1;
					    nextDay = Integer.parseInt(firstDayOfWeekBean.getDay()) +1;
					    
				    }else{
				    	
				    	nextDayStr = String.valueOf(nextDay).length()==1?"0"+String.valueOf(nextDay):String.valueOf(nextDay);
				    	 //Get Detail By prevMonthTrip,staffId,dayOfWeek
						detail = getTransDetailByDay(conn, staffId, prevMonthTrip, nextDayStr);
						
						 //set NextDay = NextDay+1;
					    nextDay += 1;
				    }
					//update detail
					MCBean transDetail = new MCBean();
					transDetail.setStaffId(staffId);
					transDetail.setMonthTrip(currentMonthTrip);
					transDetail.setDay(day);
					transDetail.setDayOfWeek(dayOfWeek);
					transDetail.setDetail(detail);
					
					int update = MCDAO.updateMCTransDetailModel(conn, transDetail);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		
	}
	 
	 public static void copyFromLastMonthModelCaseUpdateByDayOfWeek(Connection conn,String staffId,String currentMonthTrip,String prevMonthTrip) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			int r = 1;
			try {
				sql.append("\n select D.* from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1  \n");
			    sql.append("\n and D.staff_id = "+Utils.isNull(staffId)+"");
				sql.append("\n and D.month_trip = '"+Utils.isNull(currentMonthTrip)+"'");
				sql.append("\n order by D.day asc ");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
					String day = rst.getString("DAY");
				    String dayOfWeek = rst.getString("DAY_OF_WEEK");
				    
				    //Get Detail By prevMonthTrip,staffId,dayOfWeek
					String detail = getTransDetail(conn, staffId, prevMonthTrip, dayOfWeek);
					
					//update detail
					MCBean transDetail = new MCBean();
					transDetail.setStaffId(staffId);
					transDetail.setMonthTrip(currentMonthTrip);
					transDetail.setDay(day);
					transDetail.setDayOfWeek(dayOfWeek);
					transDetail.setDetail(detail);
					
					int update = MCDAO.updateMCTransDetailModel(conn, transDetail);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		
	}
	 
	 public static  MCBean getFirstTransDetailBydayOfWeek(Connection conn,String staffId,String monthTrip,String dayOfWeek) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			MCBean firstMcBean = new MCBean();
			try {
				sql.append("\n select D.day ,D.detail from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1 \n");
				sql.append("\n and D.staff_id = "+Utils.isNull(staffId)+"");
				sql.append("\n and D.month_trip = '"+Utils.isNull(monthTrip)+"'");
				sql.append("\n and D.day_of_week = '"+Utils.isNull(dayOfWeek)+"'");
				sql.append("\n order by D.day asc ");
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					firstMcBean.setDetail(Utils.isNull(rst.getString("detail")));
					firstMcBean.setDay(Utils.isNull(rst.getString("day")));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return firstMcBean;
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
	 public static MCBean searchStaff(Connection conn,MCBean o) throws Exception {
		  return searchStaffModel(conn, o);
	}
	   
	 public static MCBean searchStaff(MCBean o ) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchStaffModel(conn, o);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
	 }
	 
	 private static MCBean searchStaffModel(Connection conn,MCBean o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			MCBean h = null;
			List<MCBean> items = new ArrayList<MCBean>();
			int r = 1;
			try {
				sql.append("\n select M.* \n" +
						",(select A.pens_desc from mc_mst_reference A where A.pens_value=M.mc_area and reference_code='MCarea')as mc_area_desc \n" +
						",(select A.route_name from mc_route A where A.route_id= M.mc_route and A.mc_area=M.mc_area)as mc_route_desc \n" +
						"from MC_STAFF M ");
				sql.append("\n where 1=1  \n");
				
				if( !Utils.isNull(o.getStaffId()).equals("") && !Utils.isNull(o.getStaffId()).equalsIgnoreCase("ALL")){
					String sqlIn = Utils.converToTextSqlIn(Utils.isNull(o.getStaffId()));
					sql.append("\n and M.staff_id in( "+sqlIn+")");
				}
				if( !Utils.isNull(o.getMcArea()).equals("")){
					sql.append("\n and M.mc_area = "+Utils.isNull(o.getMcArea())+"");
				}
				if( !Utils.isNull(o.getMcRoute()).equals("")){
					sql.append("\n and M.mc_route = "+Utils.isNull(o.getMcRoute())+"");
				}
				if( !Utils.isNull(o.getStaffType()).equals("")){
					sql.append("\n and M.staff_type = '"+Utils.isNull(o.getStaffType())+"'");
				}
				sql.append("\n order by M.staff_id asc ");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new MCBean();
				   h.setNo(r);
				   h.setStaffId(Utils.isNull(rst.getString("staff_id")));
				   h.setOrgStaffId(Utils.isNull(rst.getString("staff_id")));
				   h.setStaffType(Utils.isNull(rst.getString("staff_type")));
				   h.setName(rst.getString("name"));
				   h.setSureName(rst.getString("surname"));
				   h.setMcArea(rst.getString("mc_area"));
				   h.setMcAreaDesc(rst.getString("mc_area_desc"));
				   
				   h.setMcRoute(Utils.isNull(rst.getString("mc_route")));
				   h.setMcRouteDesc(Utils.isNull(rst.getString("mc_route_desc")));
				   
	               h.setMobile(Utils.isNull(rst.getString("mobile"))); 
	               h.setActive(Utils.isNull(rst.getString("is_active")));
	               
	               if( Utils.isNull(h.getActive()).equals("Y")){
	   				  h.setActive("on");
		   		   }else{
		   			  h.setActive("");
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
	 
	 public static boolean isDuplicateStaffId(Connection conn,String staffId ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean dup = false;
			try {
				sql.append("\n select count(*) as c from MC_STAFF H ");
				sql.append("\n where 1=1   \n");
			    sql.append("\n and H.staff_id = "+Utils.isNull(staffId)+"");
			

				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
				   if(rst.getInt("c") >0){
					   dup = true;
				   }
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return dup;
		}
	 
	 public static boolean canEditStaff(Connection conn,String staffId ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean canEdit = true;
			try {
				sql.append("\n select count(*) as c from MC_TRANS H ");
				sql.append("\n where 1=1   \n");
				if( !Utils.isNull(staffId).equals("")){
					sql.append("\n and H.staff_id = "+Utils.isNull(staffId)+"");
				}

				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
				   if(rst.getInt("c") >0){
					   canEdit = false;
				   }
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return canEdit;
		}
	 
	 public static  MCBean searchTrans(Connection conn,MCBean o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			MCBean b = new MCBean();
			try {
				sql.append("\n select H.* from MC_TRANS H ");
				sql.append("\n where 1=1   \n");
				if( !Utils.isNull(o.getStaffId()).equals("")){
					sql.append("\n and H.staff_id = "+Utils.isNull(o.getStaffId())+"");
				}
				if( !Utils.isNull(o.getMonthTrip()).equals("")){
					sql.append("\n and H.month_trip = '"+Utils.isNull(o.getMonthTrip())+"'");
				}
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
				   b.setStaffId(o.getStaffId());
				   b.setMonthTrip(o.getMonthTrip());
				   b.setRemark(Utils.isNull(rst.getString("remark")));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
					
				} catch (Exception e) {}
			}
			return b;
		}
	 
	 public static  Map<String,String> searchTransDetail(Connection conn,MCBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Map<String,String> daysMap = new HashMap<String,String>();
		try {
			sql.append("\n select D.day,D.detail from MC_TRANS H ,MC_TRANS_DETAIL D");
			sql.append("\n where H.staff_id = D.staff_id and H.month_trip = D.month_trip    \n");
			if( !Utils.isNull(o.getStaffId()).equals("")){
				sql.append("\n and H.staff_id = "+Utils.isNull(o.getStaffId())+"");
			}
			if( !Utils.isNull(o.getMonthTrip()).equals("")){
				sql.append("\n and H.month_trip = '"+Utils.isNull(o.getMonthTrip())+"'");
			}
			sql.append("\n order by H.staff_id asc ");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			
			while(rst.next()) {
			  daysMap.put(Utils.isNull(rst.getString("day"))+o.getMonthTrip(), Utils.isNull(rst.getString("detail")));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				
			} catch (Exception e) {}
		}
		return daysMap;
	}

	 public static List<PopupForm> searchStaffList(PopupForm c,String operation,String mcArea,String mcRoute,String staffType) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n  SELECT M.* from MC_STAFF M where 1=1 ");
				if( !Utils.isNull(mcArea).equals("")){
					sql.append("\n  and M.mc_area ="+mcArea);
				}
				if( !Utils.isNull(mcRoute).equals("")){
				   sql.append("\n  and M.mc_route ="+mcRoute);
				}
				if( !Utils.isNull(staffType).equals("")){
				   sql.append("\n  and M.staff_type ='"+staffType+"'");
				}
				
				if("equals".equals("")){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and staff_id LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and name LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and staff_id = '"+c.getCodeSearch()+"' \n");
					}
				}
				sql.append("\n  ORDER BY staff_id asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("staff_id")));
					item.setDesc(Utils.isNull(rst.getString("name"))+" "+Utils.isNull(rst.getString("surname")));
					
					pos.add(item);
					
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
			return pos;
		}
	 
	 public static List<PopupForm> searchMcTripList(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* \n");
				sql.append("\n  from MC_MST_REFERENCE M");
				
				sql.append("\n  where 1=1 and reference_code ='MCmonthlytrip' ");
			
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("pens_value")));
					item.setDesc(Utils.isNull(rst.getString("pens_desc")));
					pos.add(item);
					
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
			return pos;
		}

	 public static List<PopupForm> searchAreaList(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* \n");
				sql.append("\n  from MC_MST_REFERENCE M");
				
				sql.append("\n  where 1=1 and reference_code ='MCarea' ");
			
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("pens_value")));
					item.setDesc(Utils.isNull(rst.getString("pens_desc")));
					pos.add(item);
					
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
			return pos;
		}
	 
	 public static List<PopupForm> searchStaffTypeList(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* \n");
				sql.append("\n  from MC_MST_REFERENCE M");
				
				sql.append("\n  where 1=1 and reference_code ='StaffType' ");
			
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and pens_value LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and pens_desc LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("pens_desc")));
					item.setDesc(Utils.isNull(rst.getString("pens_desc")));
					pos.add(item);
					
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
			return pos;
		}
	 
	 public static List<MCBean> searchRouteList(String mcArea) throws Exception {
		 return searchRouteListModel(mcArea,"");
	 }
	 
	 public static List<MCBean> searchRouteList(String mcArea,String isActive) throws Exception {
		 return searchRouteListModel(mcArea,isActive);
	 }
	 
	 public static List<MCBean> searchRouteListModel(String mcArea,String isActive) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<MCBean> pos = new ArrayList<MCBean>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* from MC_ROUTE M");
				sql.append("\n  where 1=1  ");

				if( !Utils.isNull(mcArea).equals("")){
					sql.append(" and mc_area ="+mcArea+" \n");
				}
				if( !Utils.isNull(isActive).equals("")){
					sql.append(" and (is_active ='"+isActive+"' or is_active is null) \n");
				}
				
				//sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					MCBean item = new MCBean();
					
					item.setMcRoute(Utils.isNull(rst.getString("route_id")));
					item.setMcRouteDesc(Utils.isNull(rst.getString("route_name")));
					
					pos.add(item);
					
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
			return pos;
		}
	 
	 public static String getMonthTripDesc(Connection conn,String monthTrip) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String storeName ="";
			try {
				sql.delete(0, sql.length());
				sql.append("\n select pens_value , pens_desc  ");
				sql.append("\n FROM ");
				sql.append("\n PENSBI.MC_MST_REFERENCE WHERE 1=1  and reference_code = 'MCmonthlytrip' ");
				sql.append("\n AND pens_value ='"+monthTrip+"' \n");
				sql.append("\n \n");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					storeName = Utils.isNull(rst.getString("pens_desc"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return storeName;
		}
	 
	 public static String genDummyStaffId() throws Exception {
		    Connection conn = null;
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String newStaffId ="";
			try {
				conn = DBConnection.getInstance().getConnection();
				
				sql.append("\n select max(staff_id) as max_staff_id ");
				sql.append("\n FROM MC_STAFF WHERE 1=1   ");
				sql.append("\n AND staff_id like '5%'");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					String maxStaffId = Utils.isNull(rst.getString("max_staff_id"));
					if("".equals(maxStaffId)){
						newStaffId = "5001";
					}else{
						maxStaffId = maxStaffId.substring(1,maxStaffId.length());
						int nextId = Integer.parseInt(maxStaffId)+1;
						newStaffId = "5"+new DecimalFormat("000").format(nextId);
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
			return newStaffId;
		}
}
