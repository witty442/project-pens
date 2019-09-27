package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.report.salesanalyst.ConfigBean;
import com.isecinc.pens.report.salesanalyst.SAInitial;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class BK_LocationDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static List<LocationBean> searchCustomerLocationList(LocationBean c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<LocationBean> pos = new ArrayList<LocationBean>();
			LocationBean item = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String province = "";
			String district = "";
			try {
				conn = DBConnection.getInstance().getConnectionApps();
				
				if( !Utils.isNull(c.getProvince()).equals("")){
					province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
				}
				if( !Utils.isNull(c.getDistrict()).equals("")){
					district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
				}
				
				sql.append("\n select distinct c.customer_code ,c.customer_desc ");
				sql.append("\n ,l.latitude ,l.longitude   ");
				sql.append("\n ,( cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3))as trip");
				sql.append("\n ,s.code as salesrep_code, s.salesrep_name");
				sql.append("\n from xxpens_om_trip_cust_loc l ,  ");
				sql.append("\n xxpens_ar_cust_sales_all cs ,  ");
				sql.append("\n xxpens_salesreps_v s , ");
				sql.append("\n xxpens_bi_mst_customer c ");
				sql.append("\n where 1=1 ");
				sql.append("\n and l.cust_account_id = c.customer_id  ");
				sql.append("\n and cs.code = s.code ");
				sql.append("\n and cs.cust_account_id = c.customer_id ");
				
				if( !Utils.isNull(c.getCustomerCode()).equals("")){
					sql.append("\n and c.customer_code ='"+c.getCustomerCode()+"' ");
				}
				if( !Utils.isNull(c.getCustomerName()).equals("")){
					sql.append("\n and c.customer_desc LIKE '%"+c.getCustomerName()+"%' ");
				}
				if( !Utils.isNull(c.getCustCatNo()).equals("")){
					sql.append("\n and s.sales_channel = '"+Utils.isNull(c.getCustCatNo())+"' ");
				}
				if( !Utils.isNull(c.getSalesChannelNo()).equals("")){
					sql.append("\n and s.region = '"+Utils.isNull(c.getSalesChannelNo())+"' ");
				}
				if( !Utils.isNull(c.getSalesrepCode()).equals("")){
					sql.append("\n and cs.primary_salesrep_id = '"+Utils.isNull(c.getSalesrepCode())+"' ");
				}
				if( !Utils.isNull(province).equals("")){
					sql.append("\n and cs.province = '"+province+"' ");
				}
				if( !Utils.isNull(district).equals("")){
					sql.append("\n and cs.amphur = '"+district+"' ");
				}
				
				sql.append("\n  ORDER BY c.customer_code asc ");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					item = new LocationBean();
					item.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
					item.setCustomerName(Utils.isNull(rst.getString("customer_desc")));
					item.setLat(Utils.isNull(rst.getString("latitude")));
					item.setLng(Utils.isNull(rst.getString("longitude")));
					item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
					item.setSalesrepName(Utils.isNull(rst.getString("salesrep_name")));
					item.setTrip(Utils.isNull(rst.getString("trip")));
					item.setLocationType("customer");
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
	 
	 @Deprecated
	 public static String getTripAllBySalesrepCode(Connection conn,String salesrepCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String tripArr = "";
			try {
				sql.append("\n select trip1 from xxpens_ar_cust_sales_v ");
				sql.append("\n where 1=1 ");
				sql.append("\n and code = '"+salesrepCode+"'");
				sql.append("\n ORDER BY trip asc ");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					tripArr +=Utils.isNull(rst.getString("trip1"))+",";
					
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return tripArr;
		}
	 
	 public static List<LocationBean> searchCustomerCheckInList(LocationBean c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<LocationBean> pos = new ArrayList<LocationBean>();
			LocationBean item = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnectionApps();
				sql = genSqlSearchCustomerCheckInList(conn, c);
				//logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					item = new LocationBean();
				
					item.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
					item.setCustomerName(Utils.isNull(rst.getString("customer_desc")));
					item.setLat(Utils.isNull(rst.getString("latitude")));
					item.setLng(Utils.isNull(rst.getString("longitude")));
					item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
					item.setSalesrepName(Utils.isNull(rst.getString("salesrep_name")));
					item.setTrip(Utils.isNull(rst.getString("trip")));
					item.setOrderNo(Utils.isNull(rst.getString("order_number")));
					//FLAG =  
					/*'(Y)  Key ORDER */
		            /* (N)  Visit  ONLY*/
			        /* (Y and error_flag is not null  ) key fail order */
					if("Y".equalsIgnoreCase(Utils.isNull(rst.getString("flag")))){
						 item.setLocationType("order");
						 item.setOrderDate(DateUtil.stringValue(rst.getDate("checkin_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
					} else if("N".equalsIgnoreCase(Utils.isNull(rst.getString("flag")))){
						item.setLocationType("visit");
						item.setVisitDate(DateUtil.stringValue(rst.getDate("checkin_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
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
	 
	 public static StringBuilder genSqlSearchCustomerCheckInList(Connection conn,LocationBean c) throws Exception {
			StringBuilder sql = new StringBuilder();
			String province = "";
			String district = "";
			String condMonth = "";
			try {
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					logger.debug("chkMonth:"+c.getChkMonth().length);
					/** Set Group Display  **/
					for(int i=0;i<c.getChkMonth().length;i++){
						logger.debug("name:["+i+"]value:["+c.getChkMonth()[i]+"]");
						condMonth +="'"+c.getChkMonth()[i]+"',";
					}
				}
				
				if( !Utils.isNull(c.getProvince()).equals("")){
					province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
				}
				if( !Utils.isNull(c.getDistrict()).equals("")){
					district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
				}
				
				sql.append("\n select c.customer_code ,c.customer_desc ");
				sql.append("\n ,tc.latitude ,tc.longitude ,tc.flag ");
				sql.append("\n ,tc.order_number,tc.checkin_date");
				sql.append("\n ,s.code as salesrep_code, s.salesrep_name");
				sql.append("\n ,( cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3))as trip");
				sql.append("\n from xxpens_om_trip_checkin tc ,  ");
				sql.append("\n xxpens_ar_cust_sales_all cs ,  ");
				sql.append("\n xxpens_salesreps_v s , ");
				sql.append("\n xxpens_bi_mst_customer c ");
				sql.append("\n where 1=1 ");
				sql.append("\n and tc.account_number = c.customer_code ");
				sql.append("\n and cs.code = s.code ");
				sql.append("\n and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n and cs.cust_account_id =  c.customer_id  ");
				
				if( !Utils.isNull(c.getCustomerCode()).equals("")){
					sql.append("\n and c.customer_code ='"+c.getCustomerCode()+"' ");
				}
				if( !Utils.isNull(c.getCustomerName()).equals("")){
					sql.append("\n and c.customer_desc LIKE '%"+c.getCustomerName()+"%' ");
				}
				if( !Utils.isNull(c.getCustCatNo()).equals("")){
					sql.append("\n and s.sales_channel = '"+Utils.isNull(c.getCustCatNo())+"' ");
				}
				if( !Utils.isNull(c.getSalesChannelNo()).equals("")){
					sql.append("\n and s.region = '"+Utils.isNull(c.getSalesChannelNo())+"' ");
				}
				if( !Utils.isNull(c.getSalesrepCode()).equals("")){
					sql.append("\n and cs.primary_salesrep_id = '"+Utils.isNull(c.getSalesrepCode())+"' ");
				}
				if( !Utils.isNull(province).equals("")){
					sql.append("\n and cs.province = '"+province+"' ");
				}
				if( !Utils.isNull(district).equals("")){
					sql.append("\n and cs.amphur = '"+district+"' ");
				}
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
				  Date startDate = DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				  String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				  
				  if(!Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("")){
					  Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					  String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  
					  sql.append("\n and trunc(tc.checkin_date) >= to_date('"+startDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
					  sql.append("\n and trunc(tc.checkin_date) <= to_date('"+endDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
				  }else{
					  sql.append("\n and trunc(tc.checkin_date) = to_date('"+startDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
				  }//if
				}//if
				
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					condMonth = condMonth.substring(0,condMonth.length()-1);
					sql.append("\n and to_char(tc.checkin_date,'yyyymm') in("+condMonth+")");
				}
				
				/** Disp Order and  Visit **/
				if( !"".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder())) 
				   && !"".equalsIgnoreCase(Utils.isNull(c.getDispAllVisit()))){
					
				/** Disp Order Only **/
				}else if( !"".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder())) 
				   && "".equalsIgnoreCase(Utils.isNull(c.getDispAllVisit()))	){
					sql.append("\n and tc.flag ='Y' ");
					
				/** Disp Visit only **/
				}else if( "".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder())) 
				   && !"".equalsIgnoreCase(Utils.isNull(c.getDispAllVisit()))	){
				   sql.append("\n and tc.flag ='N' ");
				}

				sql.append("\n  ORDER BY c.customer_code asc ");
				
				logger.debug("sql:"+sql);
				
				
			} catch (Exception e) {
				throw e;
			} finally {
				
			}
			return sql;
		}
}
