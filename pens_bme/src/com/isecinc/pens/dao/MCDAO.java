package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MCBean;
import com.isecinc.pens.bean.MTTBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

public class MCDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 
	 public static void saveHeadModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.MC_TRANS \n");
				sql.append(" (STAFF_ID, NAME, SURENAME, MC_AREA, MONTH_TRIP, CREATE_DATE, CREATE_USER) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?, ?, ?, ?, ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getSureName());
				ps.setString(c++, o.getMcArea());
				ps.setString(c++, o.getMonthTrip());
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
	 
	 public static void saveItemModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.MC_TRANS_DETAIL \n");
				sql.append(" (STAFF_ID, MONTH_TRIP,DAY,DETAIL, CREATE_DATE, CREATE_USER) \n");
				sql.append(" VALUES \n"); 
				sql.append(" (?, ?, ?, ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, o.getMonthTrip());
				ps.setString(c++, o.getDay());
				ps.setString(c++, o.getDetail());
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
	

		public static int updateHeadModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update staff_id ="+o.getStaffId()+",month_trip="+o.getMonthTrip());
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.MC_TRANS SET  \n");
				sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE STAFF_ID = ? and MONTH_TRIP = ? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, Utils.isNull(o.getMonthTrip()));

				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
		public static int updateItemModel(Connection conn,MCBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.MC_TRANS_DETAIL SET  \n");
				sql.append(" DETAIL= ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE STAFF_ID = ? and MONTH_TRIP = ? and DAY = ? \n" );

				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getDetail()));
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			
				ps.setInt(c++, Utils.convertStrToInt(o.getStaffId()));
				ps.setString(c++, Utils.isNull(o.getMonthTrip()));
				ps.setInt(c++, Utils.convertStrToInt(o.getDay()));
				
				return ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
   public static MCBean searchHead(Connection conn,MCBean o ) throws Exception {
	  return searchHeadModel(conn, o);
	}
   
   public static MCBean searchHead(MCBean o ) throws Exception {
	   Connection conn = null;
	   try{
		  conn = DBConnection.getInstance().getConnection();
		  return searchHeadModel(conn, o);
	   }catch(Exception e){
		   throw e;
	   }finally{
		   conn.close();
	   }
	}
   
	 public static MCBean searchHeadModel(Connection conn,MCBean o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			MCBean h = null;
			List<MCBean> items = new ArrayList<MCBean>();
			int r = 1;
			try {
				sql.append("\n select M.* \n" +
						",(select A.pens_desc from mc_mst_reference A where A.pens_value=M.mc_area and reference_code='MCarea')as mc_area_desc \n" +
						"from MC_STAFF M ");
				sql.append("\n where 1=1   \n");
				
				if( !Utils.isNull(o.getStaffId()).equals("")){
					sql.append("\n and staff_id = "+Utils.isNull(o.getStaffId())+"");
				}
				if( !Utils.isNull(o.getMcArea()).equals("")){
					sql.append("\n and mc_area = "+Utils.isNull(o.getMcArea())+"");
				}
				sql.append("\n order by staff_id asc ");
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				   h = new MCBean();
				   h.setNo(r);
				   h.setStaffId(Utils.isNull(rst.getString("staff_id")));
				   h.setName(rst.getString("name"));
				   h.setSureName(rst.getString("surname"));
				   h.setMcArea(rst.getString("mc_area"));
				   h.setMcAreaDesc(rst.getString("mc_area_desc"));
	               h.setMobile(Utils.isNull(rst.getString("mobile")));
	               h.setMonthTrip(o.getMonthTrip());
	               h.setMonthTripDesc(getMonthTripDesc(conn, o.getMonthTrip()));
	               
	               Map<String,String> daysMap = searchTransDetail(conn,h);
	               h.setDaysMap(daysMap);
	               
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
				if( !Utils.isNull(o.getMcArea()).equals("")){
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
	 
   
	 
	 public static List<PopupForm> searchStaffList(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* \n");
				sql.append("\n  from MC_STAFF M");
				sql.append("\n  where 1=1 ");
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
					item.setDesc(Utils.isNull(rst.getString("name")));
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
}
