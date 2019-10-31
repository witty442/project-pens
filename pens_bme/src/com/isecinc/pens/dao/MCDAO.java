package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.bean.MCEmpBean;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class MCDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 
	 public static void insertMCStaffModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO MC_STAFF_ROUTE \n");
				sql.append(" (EMP_REF_ID, EMPLOYEE_ID, IS_ACTIVE, CREATE_DATE, CREATE_USER, MC_ROUTE) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?, ?, ?, ?, ?, ?) \n");

				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setString(c++, o.getActive());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getMcRoute());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static int updateMCStaffModelByOrgEmpRefId(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			logger.debug("updateMCStaffModelByOrgEmpRefId o.getOrgEmpRefId()["+o.getOrgEmpRefId()+"]");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE MC_STAFF_ROUTE SET   \n");
				sql.append(" EMP_REF_ID =? ,EMPLOYEE_ID =?, \n");
				sql.append(" IS_ACTIVE =? ,MC_ROUTE = ?,   \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE EMP_REF_ID = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
				
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setString(c++, Utils.isNull(o.getActive()));
				ps.setString(c++, o.getMcRoute());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Utils.convertStrToInt(o.getOrgEmpRefId()));
				
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
	}
	 
	 public static int updateMCStaffModelByEmpRefId(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			int  c = 1;
			logger.debug("updateMCStaffModelByEmpRefId o.getEmpRefId()["+o.getEmpRefId()+"]");
			logger.debug("mcRoute:"+o.getMcRoute());
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE MC_STAFF_ROUTE SET   \n");
				sql.append(" EMPLOYEE_ID =?, \n");
				sql.append(" IS_ACTIVE =? ,MC_ROUTE = ?,   \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE EMP_REF_ID = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
				
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setString(c++, Utils.isNull(o.getActive()));
				ps.setString(c++, o.getMcRoute());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
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
	 
	 public static int updateMCStaffModelByDummyStaffId1(Connection conn,MCBean o) throws Exception{
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
				ps.setString(c++, o.getSurName());
				ps.setString(c++, o.getMcArea());
				ps.setString(c++, o.getMobile1());
				ps.setString(c++, Utils.isNull(o.getActive()));
				ps.setString(c++, o.getMcRoute());
				ps.setString(c++, Utils.isNull(o.getEmpType()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setInt(c++, Utils.convertStrToInt(o.getOrgEmpId()));
				
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
				sql.append(" INSERT INTO MC_TRANS \n");
				sql.append(" (EMP_REF_ID,EMPLOYEE_ID, NAME, SURENAME, MC_AREA, MONTH_TRIP, CREATE_DATE, CREATE_USER,MC_ROUTE,REMARK,STAFF_TYPE) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?,?, ?, ?, ?, ?, ?, ?,?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getSurName());
				ps.setString(c++, o.getMcArea());
				ps.setString(c++, o.getMonthTrip());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getMcRoute());
				ps.setString(c++, Utils.isNull(o.getRemark()));
				ps.setString(c++, Utils.isNull(o.getEmpType()));
				
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
				sql.append(" INSERT INTO MC_TRANS_DETAIL \n");
				sql.append(" (EMP_REF_ID,EMPLOYEE_ID, MONTH_TRIP,DAY,DETAIL, CREATE_DATE, CREATE_USER ,DAY_OF_WEEK) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?,?, ?, ?, ?, ?, ? ,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpId()));
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
				sql.append(" UPDATE MC_TRANS SET REMARK = ? ,  \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE EMP_REF_ID = ? and MONTH_TRIP = ? \n" );

				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getRemark()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
				ps.setString(c++, Utils.isNull(o.getMonthTrip()));

				int r =ps.executeUpdate();
				logger.debug("Update getEmpRefId ="+o.getEmpRefId()+"empId["+o.getEmpId()+"],month_trip="+o.getMonthTrip()+",result:"+r);
				
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
				
				sql.append(" WHERE EMP_REF_ID = ? and MONTH_TRIP = ? and DAY = ? \n" );

				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getDetail()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getDayOfWeek());
				
				ps.setInt(c++, Utils.convertStrToInt(o.getEmpRefId()));
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
				sql.append("\n,(select A.pens_desc from mc_mst_reference A where A.pens_value=M.region and reference_code='MCarea')as mc_area_desc" );
				sql.append("\n,(select A.pens_desc from mc_mst_reference A where A.pens_value=M.emp_type and reference_code='StaffType')as emp_type_desc" );
				sql.append("\n,(select A.route_name from mc_route A where A.route_id= M.mc_route and A.mc_area = M.region)as mc_route_desc" );
				sql.append("\n from ("); 
				
				   sql.append("\n select S.emp_ref_id,S.employee_id,E.name,E.surname,E.title,E.emp_type " );
				   sql.append("\n ,E.region ,S.mc_route,E.mobile1,E.mobile2" );
				   sql.append("\n from MC_EMPLOYEE E " );
				    sql.append("\n INNER JOIN ( ");
					sql.append("\n    SELECT distinct R.mc_area as region ,MT.emp_ref_id ,MT.employee_id ");
					sql.append("\n    ,MT.is_active ,MT.mc_route,R.route_name" );
					sql.append("\n    from MC_STAFF_ROUTE MT,MC_ROUTE R ");
					sql.append("\n    WHERE  MT.mc_route = R.route_id");
					sql.append("\n  )S  ON  S.region = E.region AND S.emp_ref_id = E.emp_ref_id ");
					
				   sql.append("\n WHERE 1=1"); 
				   sql.append("\n AND S.emp_ref_id not in(  "); 
				   sql.append("\n   select emp_ref_id from MC_TRANS WHERE  month_trip = '"+Utils.isNull(o.getMonthTrip())+"'");
				   sql.append("\n )");
				   sql.append("\n AND S.IS_ACTIVE ='Y' "); 
				   sql.append("\n AND E.status ='A'");
				   
				   if( !Utils.isNull(o.getEmpId()).equals("") && !Utils.isNull(o.getEmpId()).equalsIgnoreCase("ALL")){
						String sqlIn = SQLHelper.converToTextSqlIn(Utils.isNull(o.getEmpId()));
						sql.append("\n and E.employee_id in( "+sqlIn+")");
					}
					if( !Utils.isNull(o.getMcArea()).equals("")){
						sql.append("\n and E.region = '"+Utils.isNull(o.getMcArea())+"'");
					}
					if( !Utils.isNull(o.getMcRouteDesc()).equals("")){
						sql.append("\n and S.route_name = '"+Utils.isNull(o.getMcRouteDesc())+"'");
					}
					if( !Utils.isNull(o.getEmpType()).equals("")){
						sql.append("\n and E.emp_type = '"+Utils.isNull(o.getEmpType())+"'");
					}

				   sql.append(" \n UNION ALL ");
				   
				   sql.append("\n select  S.emp_ref_id,S.employee_id,E.name,E.surname,E.title,E.emp_type " );
				   sql.append("\n ,E.region,S.mc_route,E.mobile1,E.mobile2" );
				   sql.append("\n from MC_EMPLOYEE E ");
				   sql.append("\n INNER JOIN ( ");
					sql.append("\n    SELECT distinct R.mc_area as region ,MT.emp_ref_id ,MT.employee_id ");
					sql.append("\n    ,MT.month_trip ,MT.mc_route,R.route_name" );
					sql.append("\n    from MC_TRANS MT, MC_ROUTE R ");
					sql.append("\n    WHERE  MT.mc_route = R.route_id");
					sql.append("\n  )S  ON  S.region = E.region AND S.emp_ref_id = E.emp_ref_id ");
					
				   sql.append("\n AND S.EMP_REF_ID IN( ");
				   sql.append("\n   SELECT EMP_REF_ID FROM MC_STAFF_ROUTE MT,MC_ROUTE R WHERE MT.mc_route = R.route_id and MT.IS_ACTIVE ='Y' "); 
				      if( !Utils.isNull(o.getMcRouteDesc()).equals("")){
						  sql.append("\n and R.route_name = '"+Utils.isNull(o.getMcRouteDesc())+"'");
					   }
				   sql.append("\n ) "); 
				   
				   sql.append("\n AND E.status ='A'");
				   if( !Utils.isNull(o.getEmpId()).equals("") && !Utils.isNull(o.getEmpId()).equalsIgnoreCase("ALL")){
						String sqlIn = SQLHelper.converToTextSqlIn(Utils.isNull(o.getEmpId()));
						sql.append("\n and E.employee_id in( "+sqlIn+")");
					}
					if( !Utils.isNull(o.getMcArea()).equals("")){
						sql.append("\n and E.region = '"+Utils.isNull(o.getMcArea())+"'");
					}
					if( !Utils.isNull(o.getMcRouteDesc()).equals("")){
						sql.append("\n and S.route_name = '"+Utils.isNull(o.getMcRouteDesc())+"'");
					}
					if( !Utils.isNull(o.getEmpType()).equals("")){
						sql.append("\n and E.emp_type = '"+Utils.isNull(o.getEmpType())+"'");
					}
					if( !Utils.isNull(o.getMonthTrip()).equals("")){
						sql.append("\n and S.month_trip = '"+Utils.isNull(o.getMonthTrip())+"'");
					}
			    sql.append(" ) M WHERE 1=1");

				sql.append("\n order by M.employee_id asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new MCBean();
				  // logger.debug("Found");
				   h.setNo(r);
				   h.setEmpRefId(Utils.isNull(rst.getString("emp_ref_id")));
				   h.setEmpId(Utils.isNull(rst.getString("employee_id")));
				   h.setEmpType(Utils.isNull(rst.getString("emp_type")));
				   h.setEmpTypeDesc(Utils.isNull(rst.getString("emp_type_desc")));
				   h.setName(rst.getString("name"));
				   h.setSurName(rst.getString("surname"));
				   h.setMcArea(rst.getString("region"));
				   h.setMcAreaDesc(rst.getString("mc_area_desc"));
				   
				   h.setMcRoute(Utils.isNull(rst.getString("mc_route")));
				   h.setMcRouteDesc(Utils.isNull(rst.getString("mc_route_desc")));
				   
	               h.setMobile1(Utils.isNull(rst.getString("mobile1")));
	               h.setMobile2(Utils.isNull(rst.getString("mobile2")));
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
				
				logger.debug("Items Size:"+o.getItems().size());
				
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
	
	
	 
	 public static void copyFromLastMonthModel(Connection conn,String empRefId,String empId,String currentMonthTrip,String prevMonthTrip) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String detail = "";
			int nextDay = 0;
			String nextDayStr = "";
			try {
				sql.append("\n select D.* from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1  \n");
			    sql.append("\n and D.emp_ref_id = "+Utils.isNull(empRefId)+"");
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
					    MCBean firstDayOfWeekBean = getFirstTransDetailBydayOfWeek(conn, empRefId, prevMonthTrip, dayOfWeek);
					    detail = firstDayOfWeekBean.getDetail();
					    
					    //set NextDay = firstDayOfWeek +1;
					    nextDay = Integer.parseInt(firstDayOfWeekBean.getDay()) +1;
					    
				    }else{
				    	
				    	nextDayStr = String.valueOf(nextDay).length()==1?"0"+String.valueOf(nextDay):String.valueOf(nextDay);
				    	 //Get Detail By prevMonthTrip,staffId,dayOfWeek
						detail = getTransDetailByDay(conn, empRefId, prevMonthTrip, nextDayStr);
						
						 //set NextDay = NextDay+1;
					    nextDay += 1;
				    }
					//update detail
					MCBean transDetail = new MCBean();
					transDetail.setEmpRefId(empRefId);
					transDetail.setEmpId(empId);
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
					transDetail.setEmpId(staffId);
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
	 
	 public static  MCBean getFirstTransDetailBydayOfWeek(Connection conn,String emRefId,String monthTrip,String dayOfWeek) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			MCBean firstMcBean = new MCBean();
			try {
				sql.append("\n select D.day ,D.detail from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1 \n");
				sql.append("\n and D.emp_ref_id = "+Utils.isNull(emRefId)+"");
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
	 
	 public static  String getTransDetailByDay(Connection conn,String emRefId,String monthTrip,String day) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String detail = "";
			try {
				sql.append("\n select D.detail from MC_TRANS_DETAIL D");
				sql.append("\n where 1=1 \n");
				sql.append("\n and D.emp_ref_id = "+Utils.isNull(emRefId)+"");
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
				sql.append("\n select " );
				sql.append("\n (select A.pens_desc from mc_mst_reference A where A.pens_value = E.region and reference_code = 'MCarea')as mc_area_desc ");
				sql.append("\n ,(select A.pens_desc from mc_mst_reference A where A.pens_value = E.emp_type and reference_code = 'StaffType')as emp_type_desc ");
				sql.append("\n ,(select A.route_name from mc_route A where A.route_id = S.mc_route and A.mc_area = E.region)as mc_route_desc ");
				sql.append("\n ,E.emp_ref_id,E.employee_id,E.name,E.surname,E.title,E.emp_type " );
			    sql.append("\n ,E.region as mc_area,S.mc_route,E.mobile1,E.mobile2,S.is_active" );
				sql.append("\n from MC_EMPLOYEE E ");
				sql.append("\n INNER JOIN ( ");
				sql.append("\n    SELECT distinct R.mc_area as region ,MT.emp_ref_id ");
				sql.append("\n    ,MT.is_active ,MT.mc_route,R.route_name" );
				sql.append("\n    from MC_STAFF_ROUTE MT,MC_ROUTE R ");
				sql.append("\n    WHERE  MT.mc_route = R.route_id");
				sql.append("\n  )S  ON  S.region = E.region AND S.emp_ref_id = E.emp_ref_id ");
						
				sql.append("\n WHERE 1=1 "); 
				sql.append("\n AND E.status <> 'L'"); 
				
				if( !Utils.isNull(o.getEmpId()).equals("0") && !Utils.isNull(o.getEmpId()).equals("") ){
					sql.append("\n and E.employee_id  = "+Utils.isNull(o.getEmpId()));
				}
				if( !Utils.isNull(o.getEmpRefId()).equals("0") && !Utils.isNull(o.getEmpRefId()).equals("") ){
					sql.append("\n and E.emp_ref_id = "+Utils.isNull(o.getEmpRefId()));
				}
				if( !Utils.isNull(o.getMcArea()).equals("")){
					sql.append("\n and E.region = '"+Utils.isNull(o.getMcArea())+"'");
				}
				if( !Utils.isNull(o.getMcRouteDesc()).equals("")){
					sql.append("\n and S.route_name = '"+Utils.isNull(o.getMcRouteDesc())+"'");
				}
				if( !Utils.isNull(o.getEmpType()).equals("")){
					sql.append("\n and E.emp_type = '"+Utils.isNull(o.getEmpType())+"'");
				}
				if( !Utils.isNull(o.getActive()).equals("")){
					sql.append("\n and S.is_active = '"+Utils.isNull(o.getActive())+"'");
				}
				sql.append("\n order by E.employee_id asc ");
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new MCBean();
				   h.setNo(r);
				   h.setOrgEmpRefId(Utils.isNull(rst.getString("emp_ref_id")));
				   h.setEmpRefId(Utils.isNull(rst.getString("emp_ref_id")));
				   
				   if( !Utils.isNull(rst.getString("employee_id")).equals("0")){
				      h.setEmpId(Utils.isNull(rst.getString("employee_id")));
				   }else{
					  h.setEmpId("");
				   }
				   h.setOrgEmpId(Utils.isNull(rst.getString("employee_id")));
				   h.setEmpType(Utils.isNull(rst.getString("emp_type")));
				   h.setEmpTypeDesc(Utils.isNull(rst.getString("emp_type_desc")));
				   h.setName(rst.getString("name"));
				   h.setFullName(rst.getString("name")+" "+rst.getString("surname"));
				   h.setSurName(rst.getString("surname"));
				   h.setMcArea(rst.getString("mc_area"));
				   h.setMcAreaDesc(rst.getString("mc_area_desc"));
				   
				   h.setMcRoute(Utils.isNull(rst.getString("mc_route")));
				   h.setMcRouteDesc(Utils.isNull(rst.getString("mc_route_desc")));
				   
	               h.setMobile1(Utils.isNull(rst.getString("mobile1"))); 
	               h.setMobile2(Utils.isNull(rst.getString("mobile2")));
	               h.setActive(Utils.isNull(rst.getString("is_active")));
	               
	             /*  if( Utils.isNull(h.getActive()).equals("Y")){
	   				  h.setActive("on");
		   		   }else{
		   			  h.setActive("");
		   		   }*/
	               
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
	 
	 public static boolean isDuplicateEmployeeId(Connection conn,String newEmpId ,String empRefId ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean dup = false;
			try {
				sql.append("\n select count(*) as c from MC_EMPLOYEE H ");
				sql.append("\n where 1=1   \n");
				if( !Utils.isNull(empRefId).equals("")){
					sql.append("\n and H.emp_ref_id <> "+Utils.isNull(empRefId)+"");
					sql.append("\n and H.employee_id  ="+Utils.isNull(newEmpId)+"");
				}else{
					sql.append("\n and H.employee_id = "+Utils.isNull(newEmpId)+"");
					
				}
			    
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					if(rst.getInt("c") > 0){
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
	 
	 public static boolean canEditStaff(Connection conn,String empRefId ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			boolean canEdit = true;
			try {
				sql.append("\n select count(*) as c from MC_TRANS H ");
				sql.append("\n where 1=1   \n");
				if( !Utils.isNull(empRefId).equals("")){
					sql.append("\n and H.emp_ref_id = "+Utils.isNull(empRefId)+"");
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
				if( !Utils.isNull(o.getEmpId()).equals("")){
					sql.append("\n and H.employee_id = "+Utils.isNull(o.getEmpId())+"");
				}
				if( !Utils.isNull(o.getMonthTrip()).equals("")){
					sql.append("\n and H.employee_id = '"+Utils.isNull(o.getMonthTrip())+"'");
				}
				
				logger.debug("sql:"+sql);

				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
				   b.setEmpId(o.getEmpId());
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
			sql.append("\n where H.employee_id = D.employee_id and H.month_trip = D.month_trip    \n");
			if( !Utils.isNull(o.getEmpId()).equals("")){
				sql.append("\n and H.employee_id = "+Utils.isNull(o.getEmpId())+"");
			}
			if( !Utils.isNull(o.getMonthTrip()).equals("")){
				sql.append("\n and H.month_trip = '"+Utils.isNull(o.getMonthTrip())+"'");
			}
			sql.append("\n order by H.employee_id asc ");
			//logger.debug("sql:"+sql);

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

	 public static List<PopupForm> searchStaffList(PopupForm c,String operation,String mcArea,String mcRoute,String staffType,String active) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				logger.debug("operation:"+operation+",descSearch:"+c.getDescSearch());
				
				sql.append("\n SELECT E.emp_ref_id,E.employee_id,E.name,E.surname,E.mobile1,E.mobile2,E.title,E.emp_type,E.region");
				sql.append("\n ,(select A.pens_desc from mc_mst_reference A where A.pens_value = E.emp_type and reference_code = 'StaffType')as emp_type_desc \n");
				sql.append("\n from MC_EMPLOYEE E ");
				sql.append("\n LEFT OUTER JOIN MC_STAFF_ROUTE M ");
				sql.append("\n ON M.emp_ref_id = E.emp_ref_id ");
				
				sql.append("\n  where 1=1 ");
				if( !Utils.isNull(mcArea).equals("")){
					sql.append("\n  and E.region ='"+mcArea+"'");
				}
				if( !Utils.isNull(mcRoute).equals("")){
				   sql.append("\n  and M.mc_route ="+mcRoute);
				}
				if( !Utils.isNull(staffType).equals("")){
				   sql.append("\n  and E.emp_type ='"+staffType+"'");
				}
				if( !Utils.isNull(active).equals("")){
					sql.append("\n  and E.status ='"+active+"'");
					sql.append("\n  and E.end_date is null ");
				}
				
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and E.employee_id LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and E.name LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and E.employee_id = '"+c.getCodeSearch()+"' \n");
					}
				}
				sql.append("\n  ORDER BY E.emp_ref_id desc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				MCEmpBean empBean = null;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					if(rst.getString("employee_id").equals("0")){
					  item.setCode("");
					}else{
					  item.setCode(Utils.isNull(rst.getString("employee_id")));
					}
					item.setDesc(Utils.isNull(rst.getString("name"))+" "+Utils.isNull(rst.getString("surname")));
					
					empBean = new MCEmpBean();
					 if(rst.getInt("employee_id") != 0){
						 empBean.setEmpId(Utils.isNull(rst.getString("employee_id")));
					   }else{
						   empBean.setEmpId(""); 
					   }
					 empBean.setEmpRefId(Utils.isNull(rst.getString("emp_ref_id")));
					 empBean.setEmpType(Utils.isNull(rst.getString("emp_type")));
					 empBean.setEmpTypeDesc(Utils.isNull(rst.getString("emp_type_desc")));
					 empBean.setTitle(Utils.isNull(rst.getString("title")));
					 empBean.setName(Utils.isNull(rst.getString("name")) +" "+rst.getString("surname"));
					 empBean.setSurName(Utils.isNull(rst.getString("surname")));
					 empBean.setMobile1(Utils.isNull(rst.getString("mobile1")));
					 empBean.setMobile2(Utils.isNull(rst.getString("mobile2")));
					 empBean.setRegion(Utils.isNull(rst.getString("region")));
					 
					 item.setMcEmpBean(empBean);
					 
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
	 
	
	 //LeaveReason
	 //Title
	 //EmpStatus
	 //StaffType
	 //MCarea =Region
	 //MCmonthlytrip
	 public static List<PopupForm> searchMCRefList(PopupForm c,String equals,String refCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* from MC_MST_REFERENCE M");
				sql.append("\n  where 1=1 and reference_code ='"+refCode+"' ");
				if("equals".equals(equals)){
					sql.append("\n  and M.pens_value ='"+c.getCodeSearch()+"'");
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
					if("Line-SA".equals(refCode)){
					  item.setCode(Utils.isNull(rst.getString("pens_value")));
					  item.setDesc(Utils.isNull(rst.getString("pens_value")));
					}else{
					  item.setCode(Utils.isNull(rst.getString("pens_value")));
					  item.setDesc(Utils.isNull(rst.getString("pens_desc")));
					}
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
	 
	 
	 public static List<PopupForm> searchRouteListList(String mcArea,String isActive,String empType) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
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
				if( Utils.isNull(empType).equals("SA")){
					sql.append(" and route_name like 'สายงาน%' \n");
				}
				//sql.append("\n  ORDER BY pens_value asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("route_id")));
					item.setDesc(Utils.isNull(rst.getString("route_name")));
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
	 
	 public static List<PopupForm> searchYearList(int countYear) throws Exception {
			List<PopupForm> pos = new ArrayList<PopupForm>();
			try {
				Calendar cl = Calendar.getInstance();
				int yearCurrent = cl.get(Calendar.YEAR)+543;
				
				PopupForm item = new PopupForm();
				item.setCode(yearCurrent+"");
				item.setDesc(yearCurrent+"");
				pos.add(item);
				
				for(int i=0;i<countYear;i++){
					yearCurrent = yearCurrent-1;
					
					item = new PopupForm();
					item.setCode(yearCurrent+"");
					item.setDesc(yearCurrent+"");
					
					pos.add(item);
				}
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
				
					
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
				
				//logger.debug("sql:"+sql);

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
	 
	 
	 public static String getRouteIdByRouteName(Connection conn,String routeName,String region) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String routeId ="";
			try {
				sql.delete(0, sql.length());
				sql.append("\n select route_id FROM MC_ROUTE WHERE  route_name ='"+routeName+"' and mc_area ='"+region+"' \n");	
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					routeId = Utils.isNull(rst.getString("route_id"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return routeId;
		}
	 
	 
}
