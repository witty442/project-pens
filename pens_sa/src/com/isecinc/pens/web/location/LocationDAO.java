package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.report.salesanalyst.ConfigBean;
import com.isecinc.pens.report.salesanalyst.SAInitial;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class LocationDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static List<LocationBean> searchCustomerLocationList(LocationBean c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<LocationBean> pos = new ArrayList<LocationBean>();
			LocationBean item = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String blank="";
			try {
				conn = DBConnection.getInstance().getConnectionApps();
				
				sql.append("\n select distinct c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n ,l.latitude ,l.longitude   ");
				sql.append("\n ,( cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3))as trip");
				sql.append("\n ,s.code as salesrep_code, s.salesrep_name ,cs.customer_class_code");
				
				sql.append("\n from xxpens_om_trip_cust_loc l ,  ");
				sql.append("\n apps.xxpens_ar_cust_sales_vs cs ,  ");
				sql.append("\n xxpens_salesreps_v s , ");
				sql.append("\n xxpens_ar_customer_all_v c ");
				sql.append("\n where 1=1 ");
				sql.append("\n and l.cust_account_id = c.cust_account_id  ");
				sql.append("\n and cs.code = s.code ");
				sql.append("\n and cs.cust_account_id = c.cust_account_id ");
				sql.append("\n and l.salesrep_id = cs.primary_salesrep_id ");
				sql.append("\n and l.latitude is not null and l.longitude is not null ");
				//Condition 
				sql.append(genWhereCond(conn, c,""));
				
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  Calendar cal = Calendar.getInstance();
					  Date startDate = DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					  cal.setTime(startDate);
					  int startTrip = cal.get(Calendar.DAY_OF_MONTH);
					  
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
						  Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
						  cal.setTime(endDate);
						  int endTrip = cal.get(Calendar.DAY_OF_MONTH);
						  
						  sql.append("\n and (");
						  sql.append("\n       ( ");
						  sql.append("\n          (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
						  sql.append("\n        or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
						  sql.append("\n        or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
						  sql.append("\n       )");
						  sql.append("\n      or ");
						  //show customer not set trip
						  sql.append("\n       ( cs.trip1 is null and cs.trip2 is null and cs.trip3 is null");
						  sql.append("\n       )");
						  sql.append("\n  )");
						  
					  }else{
						  sql.append("\n and (");
						  sql.append("\n       (cs.trip1 = "+startTrip +" or cs.trip2 = "+startTrip +" or cs.trip3 ="+startTrip +")");
						  sql.append("\n       or ");
						  //show customer not set trip
						  sql.append("\n       ( cs.trip1 is null and cs.trip2 is null and cs.trip3 is null");
						  sql.append("\n       )");
						  sql.append("\n     )");
					  }//if
				}//if
					
				/** Case Month 1-98**/
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					  String startTrip = "1";
					  String endTrip = "98";
					  sql.append("\n and (");
					  sql.append("\n       ( ");
					  sql.append("\n            (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
					  sql.append("\n          or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
					  sql.append("\n          or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
					  sql.append("\n       ) ");
					  sql.append("\n       or ");
					  //show customer not set trip
					  sql.append("\n       ( cs.trip1 is null and cs.trip2 is null and cs.trip3 is null");
					  sql.append("\n       )");
					  sql.append("\n    )");
					 
				}
				
				sql.append("\n  ORDER BY c.account_number asc ");
				
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					item = new LocationBean();
					item.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
					item.setCustomerName(Utils.isNull(rst.getString("customer_desc")));
					item.setCustomerType(Utils.isNull(rst.getString("customer_class_code")));
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
	 
	 public static List<LocationBean> searchCustomerCheckInMapList(LocationBean c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<LocationBean> pos = new ArrayList<LocationBean>();
			LocationBean item = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnectionApps();
				sql = genSqlSearchCustomerCheckInMapList(conn, c);
				//logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					item = new LocationBean();
					item.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
					item.setCustomerName(Utils.isNull(rst.getString("customer_desc")));
					item.setCustomerType(Utils.isNull(rst.getString("customer_class_code")));
					item.setLat(Utils.isNull(rst.getString("latitude")));
					item.setLng(Utils.isNull(rst.getString("longitude")));
					item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
					item.setSalesrepName(Utils.isNull(rst.getString("salesrep_name")));
					item.setTrip(Utils.isNull(rst.getString("trip")));
					item.setOrderNo(Utils.isNull(rst.getString("order_number")));
					
					//Set for case noorder get location from master
					item.setCustLat(Utils.isNull(rst.getString("cust_latitude")));
					item.setCustLng(Utils.isNull(rst.getString("cust_longitude")));
					
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
					}else{
						item.setLocationType("noorder");
					}
					//logger.debug("locationType:"+item.getLocationType());
					/** Disp Order Only **/
					 if( !"".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder()))){
						if(Utils.isNull(rst.getString("flag")).equalsIgnoreCase("Y")){
							pos.add(item);
						}
					 }
					/** Disp Visit only **/
					 if( !"".equalsIgnoreCase(Utils.isNull(c.getDispAllVisit()))){	
						if(Utils.isNull(rst.getString("flag")).equalsIgnoreCase("N")
						){
							pos.add(item);
						}
					 }
				    /** Disp No Order only **/
					if( !"".equalsIgnoreCase(Utils.isNull(c.getDispAllNoOrder()))){
						if(Utils.isNull(rst.getString("flag")).equalsIgnoreCase("")){
							pos.add(item);
						}
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
			return pos;
		}
	 
	 public static StringBuilder genSqlSearchCustomerCheckInMapList(Connection conn,LocationBean c) throws Exception {
			StringBuilder sql = new StringBuilder();
			String condMonth = "";
			int startTrip  =0;
			int endTrip=0;
			String startDateStr = "";
			String endDateStr ="";
			try {
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  Calendar cal = Calendar.getInstance();
					  Date startDate = DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					  startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  cal.setTime(startDate);
					  startTrip = cal.get(Calendar.DAY_OF_MONTH);
					  
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
						  Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
						  endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
						  cal.setTime(endDate);
						  endTrip = cal.get(Calendar.DAY_OF_MONTH);
					  }//if
				}else if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					logger.debug("chkMonth:"+c.getChkMonth().length);
					/** Set Group Display  **/
					for(int i=0;i<c.getChkMonth().length;i++){
						logger.debug("name:["+i+"]value:["+c.getChkMonth()[i]+"]");
						condMonth +="'"+c.getChkMonth()[i]+"',";
					}
				    //remove, last
					condMonth = condMonth.substring(0,condMonth.length()-1);
				}
				sql.append("\n SELECT A.* FROM(  ");
				//Customer Master 
				/** Customer Info **/
				sql.append("\n SELECT M.customer_code,M.customer_desc  ");
				sql.append("\n ,M.latitude as cust_latitude ,M.longitude as cust_longitude");
				sql.append("\n ,M.trip , M.salesrep_code ,M.salesrep_name ,M.customer_class_code ");
				/** Checkin_loc Info **/
				sql.append("\n ,T.latitude ,T.longitude ,T.flag ,T.order_number,T.checkin_date");
				/** check sales vs order_number */
				//sql.append("\n ,( CASE WHEN M.salesrep_code like 'V%' THEN '3' || SUBSTR(M.salesrep_code,2,5) ");
				//sql.append("\n         ELSE '2' || SUBSTR(M.salesrep_code,2,5) END) as sales_code_check");
				sql.append("\n FROM( ");//T
				/********* Customer have Trip **************************************************************/
				sql.append("\n   select distinct c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n   ,l.latitude ,l.longitude   ");
				sql.append("\n   ,( cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3))as trip");
				sql.append("\n   ,s.code as salesrep_code, s.salesrep_name ,cs.customer_class_code");
				sql.append("\n 	 from xxpens_om_trip_cust_loc l ,  ");
				sql.append("\n 	 apps.xxpens_ar_cust_sales_vs cs ,  ");
				sql.append("\n 	 xxpens_salesreps_v s , ");
				sql.append("\n 	 xxpens_ar_customer_all_v c ");
				sql.append("\n 	 where l.cust_account_id = cs.cust_account_id ");
				sql.append("\n 	 and l.salesrep_id = cs.primary_salesrep_id ");
				
				sql.append("\n 	 and cs.code = s.code ");
				sql.append("\n 	 and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n 	 and cs.cust_account_id = c.cust_account_id ");
				
				sql.append("\n   and l.latitude is not null and l.longitude is not null ");
				//Where Cond
				sql.append(genWhereCond(conn, c,""));
				
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
						  sql.append("\n  and ( ");
						  sql.append("\n           (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
						  sql.append("\n        or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
						  sql.append("\n        or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
						  sql.append("\n       )");
					  }else{
						  sql.append("\n  and (cs.trip1 = "+startTrip +" or cs.trip2 = "+startTrip +" or cs.trip3 ="+startTrip +")");
					  }//if
				}//if
					
				/** Case Month 1-98**/
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					  startTrip = 1;
					  endTrip = 98;
					  sql.append("\n  and (");
					  sql.append("\n            (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
					  sql.append("\n         or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
					  sql.append("\n         or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
					  sql.append("\n       ) ");
				}
				
				/**********Customer CheckIn*********************************************************/
				sql.append("\n   UNION  ");
				
				sql.append("\n   select distinct c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n   ,l.latitude ,l.longitude   ");
				sql.append("\n   ,( cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3))as trip");
				sql.append("\n   ,s.code as salesrep_code, s.salesrep_name ,cs.customer_class_code");
				
				sql.append("\n   from xxpens_om_trip_checkin tc ,  ");
				sql.append("\n   apps.xxpens_ar_cust_sales_vs cs ,  ");
				sql.append("\n   xxpens_salesreps_v s , ");
				sql.append("\n   xxpens_ar_customer_all_v c,  ");
				sql.append("\n   xxpens_om_trip_cust_loc l  ");
				sql.append("\n   where tc.account_number = c.account_number ");
				sql.append("\n   and tc.cust_account_id = cs.cust_account_id ");
				
				sql.append("\n   and cs.code = s.code ");
				sql.append("\n   and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n   and cs.cust_account_id =  c.cust_account_id  ");
				
				sql.append("\n   and l.cust_account_id = c.cust_account_id  ");
				sql.append("\n 	 and l.salesrep_id = cs.primary_salesrep_id ");
				sql.append("\n   and l.latitude is not null and l.longitude is not null ");
				
				//new join 
				sql.append("\n and tc.sales_code = s.code ");
				/********************************************************************************/
				//where Cond
				sql.append(genWhereCond(conn, c,"  "));
				
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
						  sql.append("\n  and trunc(tc.checkin_date) >= to_date('"+startDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
						  sql.append("\n  and trunc(tc.checkin_date) <= to_date('"+endDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
					  }else{
						  sql.append("\n  and trunc(tc.checkin_date) = to_date('"+startDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
					  }//if
					}//if
					
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					sql.append("\n  and to_char(tc.checkin_date,'yyyymm') in("+condMonth+")");
				}
				
				/********************************************************************************/
				sql.append("\n )M LEFT OUTER JOIN ");
				/** Checkin_loc Info **/
				sql.append("\n ( select c.account_number as customer_code ");
				sql.append("\n   ,tc.latitude ,tc.longitude ,tc.flag ");
				sql.append("\n   ,tc.order_number,tc.checkin_date");
				sql.append("\n   from xxpens_om_trip_checkin tc ,  ");
				sql.append("\n   apps.xxpens_ar_cust_sales_vs cs ,  ");
				sql.append("\n   xxpens_salesreps_v s , ");
				sql.append("\n   xxpens_ar_customer_all_v c ");
				sql.append("\n   where tc.account_number = c.account_number ");
				sql.append("\n   and tc.cust_account_id = cs.cust_account_id ");
				
				sql.append("\n   and cs.code = s.code ");
				sql.append("\n   and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n   and cs.cust_account_id =  c.cust_account_id  ");
				//new join 
				sql.append("\n and tc.sales_code = s.code ");
				/********************************************************************************/
				sql.append(genWhereCond(conn, c,""));
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
				   if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					  sql.append("\n and trunc(tc.checkin_date) >= to_date('"+startDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
					  sql.append("\n and trunc(tc.checkin_date) <= to_date('"+endDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
				   }else{
					  sql.append("\n and trunc(tc.checkin_date) = to_date('"+startDateStr+"','"+DateUtil.DD_MM_YYYY_WITH_SLASH+"')");
				   }//if
				}//if
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){	
					sql.append("\n and to_char(tc.checkin_date,'yyyymm') in("+condMonth+")");
				}
				/********************************************************************************/
				sql.append("\n  ) T ON M.customer_code =T.customer_code");
				
				sql.append("\n )A  WHERE 1=1");
				//sql.append("\n AND ( substr(A.order_number,1,4) = A.sales_code_check ");
				//sql.append("\n         or A.order_number is null ) ");
				if(!"".equalsIgnoreCase(Utils.isNull(c.getDispAllNoOrder())) ) {
					if(!"".equalsIgnoreCase(Utils.isNull(c.getDispAllVisit())) 
						&& !"".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder())) ){
					    sql.append("\n AND ( (NVL(A.flag,'F') ='F' ) OR ( A.flag ='Y' OR A.flag='N' AND A.latitude is not null )  )");
					}else if(!"".equalsIgnoreCase(Utils.isNull(c.getDispAllVisit())) 
						&& "".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder())) ){
						sql.append("\n AND ( (NVL(A.flag,'F') ='F' ) OR (A.flag='N' AND A.latitude is not null )  )");
					}else if("".equalsIgnoreCase(Utils.isNull(c.getDispAllVisit())) 
						&& !"".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder())) ){
					    sql.append("\n AND ( (NVL(A.flag,'F') ='F' ) OR (A.flag='Y' AND A.latitude is not null )  )");
					}else{
					    sql.append("\n AND ( NVL(A.flag,'F') ='F' ) ");
					}
				}else if(!"".equalsIgnoreCase(Utils.isNull(c.getDispAllVisit())) ){
					if(!"".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder())) ){
					   sql.append("\n AND ( A.flag ='Y' OR A.flag='N' AND A.latitude is not null  )");
					}else{
					   sql.append("\n AND ( A.flag='N' AND A.latitude is not null  )");
					}
				}else if(!"".equalsIgnoreCase(Utils.isNull(c.getDispAllOrder())) ){
					sql.append("\n AND ( A.flag ='Y' AND A.latitude is not null ) ");
				}

				sql.append("\n  ORDER BY A.customer_code asc ");
				
				logger.debug("sql:"+sql);
			} catch (Exception e) {
				throw e;
			} finally {
				
			}
			return sql;
		}
	 
	 private static StringBuffer genWhereCond(Connection conn,LocationBean c ,String blank){
		StringBuffer sql = new StringBuffer("");
		sql.append("\n "+blank+"  and ( cs.customer_class_code = 'P'  or cs.customer_class_code ='B' or cs.customer_class_code is null)");
		
		if( !Utils.isNull(c.getCustomerType()).equals("")){
			if( Utils.isNull(c.getCustomerType()).equals("P")){
		        sql.append("\n"+blank+"   and ( cs.customer_class_code IN('"+c.getCustomerType()+"','') or cs.customer_class_code is null) ");
			}else{
			    sql.append("\n"+blank+"   and cs.customer_class_code ='"+c.getCustomerType()+"' ");
			}
		}
			
	    if( !Utils.isNull(c.getCustomerCode()).equals("")){
			sql.append("\n"+blank+"   and c.account_number ='"+c.getCustomerCode()+"' ");
		}
		if( !Utils.isNull(c.getCustomerName()).equals("")){
			sql.append("\n"+blank+"   and c.party_name LIKE '%"+c.getCustomerName()+"%' ");
		}
		
		if( !Utils.isNull(c.getCustCatNo()).equals("")){
			sql.append("\n"+blank+"   and s.sales_channel = '"+Utils.isNull(c.getCustCatNo())+"' ");
		}
		if( !Utils.isNull(c.getSalesChannelNo()).equals("")){
			sql.append("\n"+blank+"   and s.region = '"+Utils.isNull(c.getSalesChannelNo())+"' ");
		}
		if( !Utils.isNull(c.getSalesrepCode()).equals("")){
			sql.append("\n"+blank+"   and cs.primary_salesrep_id = '"+Utils.isNull(c.getSalesrepCode())+"' ");
		}
		 if( !Utils.isNull(c.getProvince()).equals("")){
			String province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
			if( !Utils.isNull(province).equals("")){
				sql.append("\n"+blank+"   and cs.province = '"+province+"' ");
			}
		 }
		if( !Utils.isNull(c.getDistrict()).equals("")){
			String district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
		
		   if( !Utils.isNull(district).equals("")){
			sql.append("\n"+blank+"   and cs.amphur = '"+district+"' ");
		   }
		}
		if( !Utils.isNull(c.getSalesZone()).equals("")){
		    sql.append("\n  and cs.primary_salesrep_id in(");
		    sql.append("\n    select salesrep_id from pensbi.XXPENS_BI_MST_SALES_ZONE ");
		    sql.append("\n    where zone = "+Utils.isNull(c.getSalesZone()) );
		    sql.append("\n  )");
		}
		return sql;
	 }
}
