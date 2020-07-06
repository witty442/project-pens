package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class LocationReport {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuffer searchCustomerCheckInDataList(LocationBean c,boolean excel) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			StringBuffer html = new StringBuffer("");
			String tdTextClass = "td_text";
			String tdTextCenterClass = "td_text_center";
			int r = 0;
			try {
				conn = DBConnection.getInstance().getConnectionApps();
				
				if("real".equalsIgnoreCase(c.getTripType())){
				   sql = genSqlSearchCustomerCheckInDataByRealList(conn, c);
				   
				}else if("trip".equalsIgnoreCase(c.getTripType())){
				   sql = genSqlSearchCustomerCheckInDataByTripList(conn, c);
				   
			    }else if("NotEqualTrip".equalsIgnoreCase(c.getTripType())){ 
			       sql = genSqlSearchCustomerCheckInDataByNotEqualTripList(conn, c);
			    }
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					r++;//Count Row
					if(r==1){
						//Set Style HTML or Excel
						if(excel){
							html.append(ExcelHeader.EXCEL_HEADER);
							tdTextClass = "text";
							tdTextCenterClass = "text";
						}
						//Gen Head Table
						html.append(genHeadTable(c, excel));
					}
					html.append("</tr>");
					//filter case move customer to another sales
					//use order_number =SaleCode
					
					html.append("<td class='"+tdTextCenterClass+"' width='2%'>"+r+"</td>");
					if("report1".equalsIgnoreCase(c.getReportType())){
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='24%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+DateUtil.stringValueChkNull(rst.getDate("checkin_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th)+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+genFlagVisit(rst.getString("flag_sales"),rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
						
					}else if("report2".equalsIgnoreCase(c.getReportType())){
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("google_address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+DateUtil.stringValueChkNull(rst.getDate("checkin_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th)+"</td>");

						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+genFlagVisit(rst.getString("flag_sales"),rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
					}else if("report3".equalsIgnoreCase(c.getReportType())){
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("salesrep_code"))+"-"+Utils.isNull(rst.getString("salesrep_name"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+DateUtil.stringValueChkNull(rst.getDate("checkin_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th)+"</td>");
						
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+genFlagVisit(rst.getString("flag_sales"),rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
					}else if("report4".equalsIgnoreCase(c.getReportType())){
						html.append("<td class='"+tdTextClass+"' width='8%'>"+Utils.isNull(rst.getString("region_name"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("salesrep_code"))+"-"+Utils.isNull(rst.getString("salesrep_name"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='20%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+DateUtil.stringValueChkNull(rst.getDate("checkin_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th)+"</td>");
						
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+genFlagVisit(rst.getString("flag_sales"),rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
					}else if("report5".equalsIgnoreCase(c.getReportType())){
						
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("salesrep_code"))+"-"+Utils.isNull(rst.getString("salesrep_name"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='10%'>"+Utils.isNull(rst.getString("province"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='10%'>"+Utils.isNull(rst.getString("amphur"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='20%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+DateUtil.stringValueChkNull(rst.getDate("checkin_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th)+"</td>");
						
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+genFlagVisit(rst.getString("flag_sales"),rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
					}
				  html.append("</tr>");
				}//while
				
				logger.debug("r:"+r);
				
				if(r >= 1){
			       html.append("</table>");
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return html;
		}
	 
	private static String genFlagVisit(String salesFlag,String visitFlag){
		if(Utils.isNull(salesFlag).equalsIgnoreCase("Y")){
			return "Y";
		}else{
			if(Utils.isNull(visitFlag).equalsIgnoreCase("Y")){
				return "Y";
			}
		}
		return "";
	}
	 public static StringBuilder genSqlSearchCustomerCheckInDataByRealList(Connection conn,LocationBean c) throws Exception {
			StringBuilder sql = new StringBuilder();
			String province = "";
			String district = "";
			String condMonth = "";
			String orderBy = "";
			try {
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					logger.debug("chkMonth:"+c.getChkMonth().length);
					/** Set Group Display  **/
					for(int i=0;i<c.getChkMonth().length;i++){
						logger.debug("name:["+i+"]value:["+c.getChkMonth()[i]+"]");
						condMonth +="'"+c.getChkMonth()[i]+"',";
					}
				}//
				
				if( !Utils.isNull(c.getProvince()).equals("")){
					province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
				}
				if( !Utils.isNull(c.getDistrict()).equals("")){
					district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
				}
				sql.append("\n select A.* FROM ( ");
				sql.append("\n select ");
				sql.append("\n tc.order_number ,tc.checkin_date ");
				//sql.append("\n ,( CASE WHEN s.code like 'V%' THEN '3' || SUBSTR(s.code,2,5) ");
				//sql.append("\n         ELSE '2' || SUBSTR(s.code,2,5) END) as sales_code_check");
				sql.append("\n ,( CASE WHEN tc.flag ='Y' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_sales");
				sql.append("\n ,( CASE WHEN tc.flag ='N' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_visit");
				sql.append("\n ,( CASE WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip1 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip2 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip3 THEN 'Y' ");
				sql.append("\n         ELSE 'N' END) as flag_equals_trip");
				sql.append("\n ,(" );
				sql.append("\n   cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3)" );
				sql.append("\n  )as trip");
				sql.append("\n , cs.trip1 ");
				if("report1".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
					sql.append("\n ,cs.address ");
					
					orderBy = " A.checkin_date,A.order_number asc";
				}else if("report2".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
					sql.append("\n ,cs.address  ");
					sql.append("\n ,(select cl.address from xxpens_om_trip_cust_loc cl ");
					sql.append("\n   where cl.cust_account_id = c.cust_account_id");
					sql.append("\n   and cl.salesrep_id = cs.primary_salesrep_id) as google_address ");
					orderBy = " A.checkin_date,A.order_number asc";
					
				}else if("report3".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
					sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
					sql.append("\n ,cs.address ");
					
					orderBy = " A.checkin_date,A.order_number asc";
				}else if("report4".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,s.region ,s.region_name ");
					sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
					sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc  ");
					sql.append("\n ,cs.address ");
					
					orderBy = " A.checkin_date,A.order_number asc";
				}else if("report5".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
					sql.append("\n ,cs.province ,cs.amphur ");
					sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc  ");
					sql.append("\n ,cs.address ");
					
					orderBy = " A.checkin_date,A.order_number asc";
				}
				
			    sql.append("\n from xxpens_om_trip_checkin tc , ");
				sql.append("\n apps.xxpens_ar_cust_sales_vl cs ,  ");
				sql.append("\n xxpens_salesreps_v s , ");
				sql.append("\n xxpens_ar_customer_all_v c  ");
				sql.append("\n where 1=1 ");
				sql.append("\n and tc.account_number = c.account_number ");
				sql.append("\n and tc.cust_account_id = cs.cust_account_id ");
				
				sql.append("\n and cs.code = s.code ");
				sql.append("\n and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n and cs.cust_account_id =  c.cust_account_id  ");
				//new join 
				sql.append("\n and tc.sales_code = s.code ");
				
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  Date startDate = DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					  String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
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
			
				if( !Utils.isNull(c.getCustomerCode()).equals("")){
					sql.append("\n and c.account_number ='"+c.getCustomerCode()+"' ");
				}
				if( !Utils.isNull(c.getCustomerName()).equals("")){
					sql.append("\n and c.party_name LIKE '%"+c.getCustomerName()+"%' ");
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
				if( !Utils.isNull(c.getSalesZone()).equals("")){
				    sql.append("\n  and cs.primary_salesrep_id in(");
				    sql.append("\n    select salesrep_id from pensbi.XXPENS_BI_MST_SALES_ZONE ");
				    sql.append("\n    where zone = "+Utils.isNull(c.getSalesZone()) );
				    sql.append("\n  )");
				}
				//CustType
				sql.append("\n and ( cs.customer_class_code in('P','B','') or cs.customer_class_code is null)");
				
				if( !Utils.isNull(c.getCustomerType()).equals("")){
					if( Utils.isNull(c.getCustomerType()).equals("P")){
				        sql.append("\n and ( cs.customer_class_code IN('"+c.getCustomerType()+"','') or cs.customer_class_code is null) ");
					}else{
					    sql.append("\n and cs.customer_class_code ='"+c.getCustomerType()+"' ");
					}
				}
				sql.append("\n )A ");
				sql.append("\n  ORDER BY "+orderBy);

			} catch (Exception e) {
				throw e;
			} finally { 
				
			}
			return sql;
		}
	 
	 public static StringBuilder genSqlSearchCustomerCheckInDataByTripList(Connection conn,LocationBean c) throws Exception {
			StringBuilder sql = new StringBuilder();
			String condMonth = "";
			String orderBy = "";
			int startTrip = 0;
			int endTrip= 0;
			String startDateStr = "";
			String endDateStr = "";
			try {
				
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
				   Date startDate = DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				   startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  
				   Calendar cal = Calendar.getInstance();
				   cal.setTime(startDate);
				   startTrip = cal.get(Calendar.DAY_OF_MONTH);
				  
				   if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					  Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					  endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  
					  cal.setTime(endDate);
					  endTrip = cal.get(Calendar.DAY_OF_MONTH);
				   }
			  }else if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					logger.debug("chkMonth:"+c.getChkMonth().length);
					/** Set Group Display  **/
					for(int i=0;i<c.getChkMonth().length;i++){
						logger.debug("name:["+i+"]value:["+c.getChkMonth()[i]+"]");
						condMonth +="'"+c.getChkMonth()[i]+"',";
					}
					condMonth = condMonth.substring(0,condMonth.length()-1);
				}
				orderBy = " to_number(A.trip1) asc";
				
				sql.append("\n select A.* FROM ( ");
				sql.append("\n select tc.order_number ,tc.checkin_date ");
				sql.append("\n ,( CASE WHEN c.code like 'V%' THEN '3' || SUBSTR(c.code,2,5) ");
				sql.append("\n         ELSE '2' || SUBSTR(c.code,2,5) END) as sales_code_check");
				sql.append("\n ,( CASE WHEN tc.flag ='Y' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_sales");
				sql.append("\n ,( CASE WHEN tc.flag ='N' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_visit");
				sql.append("\n ,( CASE WHEN to_number(to_char(tc.checkin_date,'dd')) = c.trip1 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = c.trip2 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = c.trip3 THEN 'Y' ");
				sql.append("\n         ELSE 'N' END) as flag_equals_trip");
				sql.append("\n ,(" );
				sql.append("\n   c.trip1 || decode(c.trip2, null,null ,','||c.trip2) || decode(c.trip3, null,null ,','||c.trip3)" );
				sql.append("\n  )as trip");
				sql.append("\n ,c.trip1 ");
				//gen select column
				sql.append(genSelectColumnSqlSearchCustomerCheckInDataByTrip(conn, c));
				sql.append("\n from  ");
				sql.append("\n ( ");
				sql.append("\n select cs.trip1,cs.trip2,cs.trip3");
				sql.append("\n ,c.account_number,c.party_name,cs.address,s.code");
                sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
				sql.append("\n ,cs.province ,cs.amphur ");
	            sql.append("\n ,s.region ,s.region_name ");
				sql.append("\n from");
				sql.append("\n apps.xxpens_ar_cust_sales_vl cs ,  ");
				sql.append("\n xxpens_salesreps_v s , ");
				sql.append("\n xxpens_ar_customer_all_v c  ");
				sql.append("\n where 1=1 ");
				sql.append("\n and cs.code = s.code ");
				sql.append("\n and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n and cs.cust_account_id =  c.cust_account_id  ");
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
						  sql.append("\n and (");
						  sql.append("\n           (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
						  sql.append("\n        or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
						  sql.append("\n        or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
						  sql.append("\n     )");
					  }else{
						  sql.append("\n and (cs.trip1 = "+startTrip +" or cs.trip2 = "+startTrip +" or cs.trip3 ="+startTrip +")");
					  }//if
				}//if
				/** Case Month 1-98**/
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					  startTrip = 1;
					  endTrip = 98;
					  sql.append("\n and (");
					  sql.append("\n           (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
					  sql.append("\n        or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
					  sql.append("\n        or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
					  sql.append("\n     ) ");
				}
			    //genWhereSqlCond
				sql.append(genWhereCondSqlSearchCustomerCheckInData(conn, c));
				sql.append("\n )c ");
				sql.append("\n left outer join ");
				sql.append("\n ( ");
				sql.append("\n select tc.*" );
				sql.append("\n from xxpens_om_trip_checkin tc where 1=1");
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
			    sql.append("\n )tc  on tc.account_number = c.account_number ");
				sql.append("\n      and tc.sales_code = c.code ");
				sql.append("\n )A ");
				sql.append("\n  ORDER BY "+orderBy);
			} catch (Exception e) {
				throw e;
			} finally { 
				
			}
			return sql;
		}
	 public static StringBuilder genSqlSearchCustomerCheckInDataByNotEqualTripList(Connection conn,LocationBean c) throws Exception {
			StringBuilder sql = new StringBuilder();
			
			String condMonth = "";
			String orderBy = "";
			int startTrip = 0;
			int endTrip= 0;
			String startDateStr = "";
			String endDateStr = "";
			try {
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
				   Date startDate = DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				   startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  
				   Calendar cal = Calendar.getInstance();
				   cal.setTime(startDate);
				   startTrip = cal.get(Calendar.DAY_OF_MONTH);
				  
				   if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					  Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					  endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  
					  cal.setTime(endDate);
					  endTrip = cal.get(Calendar.DAY_OF_MONTH);
				   }
			  }else if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					logger.debug("chkMonth:"+c.getChkMonth().length);
					/** Set Group Display  **/
					for(int i=0;i<c.getChkMonth().length;i++){
						logger.debug("name:["+i+"]value:["+c.getChkMonth()[i]+"]");
						condMonth +="'"+c.getChkMonth()[i]+"',";
					}
					condMonth = condMonth.substring(0,condMonth.length()-1);
				}
				orderBy = " to_number(A.trip1) asc";
				
				sql.append("\n select A.* FROM ( ");
				

				//Customer is visit and sale but not equals trip set
				sql.append("\n select tc.order_number ,tc.checkin_date ");
				//sql.append("\n ,( CASE WHEN s.code like 'V%' THEN '3' || SUBSTR(s.code,2,5) ");
				//sql.append("\n         ELSE '2' || SUBSTR(s.code,2,5) END) as sales_code_check");
				sql.append("\n ,( CASE WHEN tc.flag ='Y' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_sales");
				sql.append("\n ,( CASE WHEN tc.flag ='N' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_visit");
				sql.append("\n ,( CASE WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip1 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip2 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip3 THEN 'Y' ");
				sql.append("\n         ELSE 'N' END) as flag_equals_trip");
				sql.append("\n ,(" );
				sql.append("\n   cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3)" );
				sql.append("\n  )as trip");
				sql.append("\n , cs.trip1 ");
				//gen select column
				sql.append(genSelectColumnSqlSearchCustomerCheckInData(conn, c));
				sql.append("\n from  ");
				sql.append("\n apps.xxpens_ar_cust_sales_vl cs ,  ");
				sql.append("\n xxpens_salesreps_v s , ");
				sql.append("\n xxpens_ar_customer_all_v c  ");
				sql.append("\n inner join ");
				sql.append("\n ( ");
				sql.append("\n select tc.* from xxpens_om_trip_checkin tc where 1=1");
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
				sql.append("\n  and (tc.flag ='N' or tc.flag ='Y')");
			    sql.append("\n )tc  on tc.account_number = c.account_number ");
				sql.append("\n where 1=1 ");
				sql.append("\n and cs.code = s.code ");
				sql.append("\n and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n and cs.cust_account_id =  c.cust_account_id  ");
				
				//new join 
				sql.append("\n and tc.sales_code = s.code ");
				
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
						  sql.append("\n and (");
						  sql.append("\n           (nvl(cs.trip1,0) < "+startTrip +" or nvl(cs.trip1,0) > "+endTrip +")");
						  sql.append("\n        and (nvl(cs.trip2,0) < "+startTrip +" or nvl(cs.trip2,0) > "+endTrip +")");
						  sql.append("\n        and (nvl(cs.trip3,0) < "+startTrip +" or nvl(cs.trip3,0) > "+endTrip +")");
						  sql.append("\n     )");
					  }else{
						  sql.append("\n and (nvl(cs.trip1,0) <> "+startTrip +" and nvl(cs.trip2,0) <> "+startTrip +" and nvl(cs.trip3,0) <>"+startTrip +")");
					  }//if
				}//if
				/** Case Month 1-98**/
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					  startTrip = 1;
					  endTrip = 98;
					  sql.append("\n and (");
					  sql.append("\n        (nvl(cs.trip1,0) < "+startTrip +" or nvl(cs.trip1,0) > "+endTrip +")");
					  sql.append("\n     or (nvl(cs.trip2,0) < "+startTrip +" or nvl(cs.trip2,0) > "+endTrip +")");
					  sql.append("\n     or (nvl(cs.trip3,0) < "+startTrip +" or nvl(cs.trip3,0) > "+endTrip +")");
					  sql.append("\n     ) ");
				}
			    //genWhereSqlCond
				sql.append(genWhereCondSqlSearchCustomerCheckInData(conn, c));
				sql.append("\n )A ");
				sql.append("\n  ORDER BY "+orderBy);

			} catch (Exception e) {
				throw e;
			} finally { 
				
			}
			return sql;
		}
	 public static StringBuilder genSqlSearchCustomerCheckInDataByTripList_V1(Connection conn,LocationBean c) throws Exception {
			StringBuilder sql = new StringBuilder();
			
			String condMonth = "";
			String orderBy = "";
			int startTrip = 0;
			int endTrip= 0;
			String startDateStr = "";
			String endDateStr = "";
			try {
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
				   Date startDate = DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				   startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  
				   Calendar cal = Calendar.getInstance();
				   cal.setTime(startDate);
				   startTrip = cal.get(Calendar.DAY_OF_MONTH);
				  
				   if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					  Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					  endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					  
					  cal.setTime(endDate);
					  endTrip = cal.get(Calendar.DAY_OF_MONTH);
				   }
			  }else if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					logger.debug("chkMonth:"+c.getChkMonth().length);
					/** Set Group Display  **/
					for(int i=0;i<c.getChkMonth().length;i++){
						logger.debug("name:["+i+"]value:["+c.getChkMonth()[i]+"]");
						condMonth +="'"+c.getChkMonth()[i]+"',";
					}
					condMonth = condMonth.substring(0,condMonth.length()-1);
				}
				orderBy = " to_number(A.trip1) asc";
				
				sql.append("\n select A.* FROM ( ");
				
				sql.append("\n select tc.order_number ,tc.checkin_date ");
				sql.append("\n ,( CASE WHEN tc.flag ='Y' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_sales");
				sql.append("\n ,( CASE WHEN tc.flag ='N' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_visit");
				sql.append("\n ,( CASE WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip1 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip2 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip3 THEN 'Y' ");
				sql.append("\n         ELSE 'N' END) as flag_equals_trip");
				sql.append("\n ,(" );
				sql.append("\n   cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3)" );
				sql.append("\n  )as trip");
				sql.append("\n , cs.trip1 ");
				//gen select column
				sql.append(genSelectColumnSqlSearchCustomerCheckInData(conn, c));
				sql.append("\n from  ");
				sql.append("\n apps.xxpens_ar_cust_sales_vl cs ,  ");
				sql.append("\n xxpens_salesreps_v s , ");
				sql.append("\n xxpens_ar_customer_all_v c  ");
				sql.append("\n left outer join ");
				sql.append("\n ( ");
				sql.append("\n select tc.* from xxpens_om_trip_checkin tc where 1=1");
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
			    sql.append("\n )tc  on tc.account_number = c.account_number ");
				sql.append("\n where 1=1 ");
				sql.append("\n and cs.code = s.code ");
				sql.append("\n and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n and cs.cust_account_id =  c.cust_account_id  ");
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
						  sql.append("\n and (");
						  sql.append("\n           (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
						  sql.append("\n        or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
						  sql.append("\n        or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
						  sql.append("\n     )");
					  }else{
						  sql.append("\n and (cs.trip1 = "+startTrip +" or cs.trip2 = "+startTrip +" or cs.trip3 ="+startTrip +")");
					  }//if
				}//if
				/** Case Month 1-98**/
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					  startTrip = 1;
					  endTrip = 98;
					  sql.append("\n and (");
					  sql.append("\n       (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
					  sql.append("\n        or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
					  sql.append("\n        or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
					  sql.append("\n     ) ");
				}
			    //genWhereSqlCond
				sql.append(genWhereCondSqlSearchCustomerCheckInData(conn, c));
				
				sql.append("\n UNION ");
				//Customer is visit and sale but not equals trip set
				sql.append("\n select tc.order_number ,tc.checkin_date ");
				sql.append("\n ,( CASE WHEN tc.flag ='Y' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_sales");
				sql.append("\n ,( CASE WHEN tc.flag ='N' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_visit");
				sql.append("\n ,( CASE WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip1 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip2 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip3 THEN 'Y' ");
				sql.append("\n         ELSE 'N' END) as flag_equals_trip");
				sql.append("\n ,(" );
				sql.append("\n   cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3)" );
				sql.append("\n  )as trip");
				sql.append("\n , cs.trip1 ");
				//gen select column
				sql.append(genSelectColumnSqlSearchCustomerCheckInData(conn, c));
				sql.append("\n from  ");
				sql.append("\n apps.xxpens_ar_cust_sales_vl cs ,  ");
				sql.append("\n xxpens_salesreps_v s , ");
				sql.append("\n xxpens_ar_customer_all_v c  ");
				sql.append("\n inner join ");
				sql.append("\n ( ");
				sql.append("\n select tc.* from xxpens_om_trip_checkin tc where 1=1");
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
				sql.append("\n  and (tc.flag ='N' or tc.flag ='Y')");
			    sql.append("\n )tc  on tc.account_number = c.account_number ");
				sql.append("\n where 1=1 ");
				sql.append("\n and cs.code = s.code ");
				sql.append("\n and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n and cs.cust_account_id =  c.cust_account_id  ");
				if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
					  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
						  sql.append("\n and (");
						  sql.append("\n           (nvl(cs.trip1,0) < "+startTrip +" or nvl(cs.trip1,0) > "+endTrip +")");
						  sql.append("\n        or (nvl(cs.trip2,0) < "+startTrip +" or nvl(cs.trip2,0) > "+endTrip +")");
						  sql.append("\n        or (nvl(cs.trip3,0) < "+startTrip +" or nvl(cs.trip3,0) > "+endTrip +")");
						  sql.append("\n     )");
					  }else{
						  sql.append("\n and (nvl(cs.trip1,0) <> "+startTrip +" or nvl(cs.trip2,0) <> "+startTrip +" or nvl(cs.trip3,0) <>"+startTrip +")");
					  }//if
				}//if
				/** Case Month 1-98**/
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					  startTrip = 1;
					  endTrip = 98;
					  sql.append("\n and (");
					  sql.append("\n        (nvl(cs.trip1,0) < "+startTrip +" or nvl(cs.trip1,0) > "+endTrip +")");
					  sql.append("\n     or (nvl(cs.trip2,0) < "+startTrip +" or nvl(cs.trip2,0) > "+endTrip +")");
					  sql.append("\n     or (nvl(cs.trip3,0) < "+startTrip +" or nvl(cs.trip3,0) > "+endTrip +")");
					  sql.append("\n     ) ");
				}
			    //genWhereSqlCond
				sql.append(genWhereCondSqlSearchCustomerCheckInData(conn, c));
				sql.append("\n )A ");
				sql.append("\n  ORDER BY "+orderBy);

			} catch (Exception e) {
				throw e;
			} finally { 
				
			}
			return sql;
		}
	 
	 private static StringBuffer genSelectColumnSqlSearchCustomerCheckInData(Connection conn,LocationBean c){
		 StringBuffer sql = new StringBuffer();
		 if("report1".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n ,cs.address ");
				
				//orderBy = " A.checkin_date,A.order_number asc";
			}else if("report2".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n ,cs.address  ");
				sql.append("\n ,(select cl.address from xxpens_om_trip_cust_loc cl ");
				sql.append("\n   where cl.cust_account_id = c.cust_account_id) as google_address ");
				//orderBy = " A.checkin_date,A.order_number asc";
				
			}else if("report3".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n ,cs.address ");
				
				//orderBy = " A.checkin_date,A.order_number asc";
			}else if("report4".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,s.region ,s.region_name ");
				sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc  ");
				sql.append("\n ,cs.address ");
				
				//orderBy = " A.checkin_date,A.order_number asc";
			}else if("report5".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
				sql.append("\n ,cs.province ,cs.amphur ");
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc  ");
				sql.append("\n ,cs.address ");
				
				//orderBy = " A.checkin_date,A.order_number asc";
			}
			return sql;
	 }
	 
	 private static StringBuffer genSelectColumnSqlSearchCustomerCheckInDataByTrip(Connection conn,LocationBean c){
		 StringBuffer sql = new StringBuffer();
		 if("report1".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n ,c.address ");
				
				//orderBy = " A.checkin_date,A.order_number asc";
			}else if("report2".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n ,c.address  ");
				sql.append("\n ,(select cl.address from xxpens_om_trip_cust_loc cl ");
				sql.append("\n   where cl.cust_account_id = c.cust_account_id) as google_address ");
				//orderBy = " A.checkin_date,A.order_number asc";
				
			}else if("report3".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,c.user_name as salesrep_code ,c.salesrep_name ");
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc ");
				sql.append("\n ,cc.address ");
				
				//orderBy = " A.checkin_date,A.order_number asc";
			}else if("report4".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,c.region ,c.region_name ");
				sql.append("\n ,c.user_name as salesrep_code ,c.salesrep_name ");
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc  ");
				sql.append("\n ,c.address ");
				
				//orderBy = " A.checkin_date,A.order_number asc";
			}else if("report5".equalsIgnoreCase(c.getReportType())){
				sql.append("\n ,c.user_name as salesrep_code ,c.salesrep_name ");
				sql.append("\n ,c.province ,cs.amphur ");
				sql.append("\n ,c.account_number as customer_code ,c.party_name as customer_desc  ");
				sql.append("\n ,c.address ");
				
				//orderBy = " A.checkin_date,A.order_number asc";
			}
			return sql;
	 }
	 
	 private static StringBuffer genWhereCondSqlSearchCustomerCheckInData(Connection conn,LocationBean c){
		 String province = "";
		 String district = "";
		 StringBuffer sql = new StringBuffer();
		 try{
			if( !Utils.isNull(c.getProvince()).equals("")){
				province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
			}
			if( !Utils.isNull(c.getDistrict()).equals("")){
				district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
			}
		    if( !Utils.isNull(c.getCustomerCode()).equals("")){
				sql.append("\n and c.account_number ='"+c.getCustomerCode()+"' ");
			}
			if( !Utils.isNull(c.getCustomerName()).equals("")){
				sql.append("\n and c.party_name LIKE '%"+c.getCustomerName()+"%' ");
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
			if( !Utils.isNull(c.getSalesZone()).equals("")){
			    sql.append("\n  and cs.primary_salesrep_id in(");
			    sql.append("\n    select salesrep_id from pensbi.XXPENS_BI_MST_SALES_ZONE ");
			    sql.append("\n    where zone = "+Utils.isNull(c.getSalesZone()) );
			    sql.append("\n  )");
			}
			//CustType
			sql.append("\n and ( cs.customer_class_code in('P','B','') or cs.customer_class_code is null)");
			
			if( !Utils.isNull(c.getCustomerType()).equals("")){
				if( Utils.isNull(c.getCustomerType()).equals("P")){
			        sql.append("\n and ( cs.customer_class_code IN('"+c.getCustomerType()+"','') or cs.customer_class_code is null) ");
				}else{
				    sql.append("\n and cs.customer_class_code ='"+c.getCustomerType()+"' ");
				}
			}
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 return sql;
	 }
	 
	 private static String getTrip(String trip){
		 if("0".equalsIgnoreCase(trip) || "".equals(trip)){
			 return "";
		 }
		 String newTrip = "";
		 String[] tripArr = trip.split("\\,");
		 if(tripArr.length >0){
			 for(int i=0;i<tripArr.length;i++){
				 String tripChk = Utils.isNull(tripArr[i]);
				 if( !tripChk.equalsIgnoreCase("0") && !tripChk.equalsIgnoreCase("0")){
					 if(tripChk.startsWith("0")){
						 tripChk = tripChk.substring(1,tripChk.length());
					 }
					 newTrip +=tripChk+",";
				 }
			 }
			 if(newTrip.length() >0)
			   newTrip = newTrip.substring(0,newTrip.length()-1);
		 }else{
			 newTrip = trip;
		 }
		 return newTrip;
	 }
	 
	 /**
		 * 
		 * @param columnNameArr
		 * @return
		 * @throws Exception
		 */
		private static StringBuffer genHeadTable(LocationBean c,boolean excel) throws Exception{
			StringBuffer h = new StringBuffer("");
			if(excel){
				h.append(ExcelHeader.EXCEL_HEADER);
			}
			String width="100%";
			
			h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			h.append("<tr> \n");
			  h.append(" <th nowrap >ลำดับ </th> \n");
			if("report1".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >ร้านค้า </th> \n");
			  h.append(" <th nowrap >ที่อยู่ </th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ได้ขาย </th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			}else if("report2".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >ร้านค้า </th> \n");
			  h.append(" <th nowrap >ที่อยู่ </th> \n");
			  h.append(" <th nowrap >ที่อยู่ตาม Google </th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ได้ขาย</th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			}else if("report3".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >พนักงานขาย</th> \n");
			  h.append(" <th nowrap >ร้านค้า</th> \n");
			  h.append(" <th nowrap >ที่อยู่</th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ได้ขาย</th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			}else if("report4".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >ภาคการขาย</th> \n");
			  h.append(" <th nowrap >พนักงานขาย</th> \n");
			  h.append(" <th nowrap >ร้านค้า</th> \n");
			  h.append(" <th nowrap >ที่อยู่</th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ได้ขาย </th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			}else if("report5".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >พนักงานขาย</th> \n");
			  h.append(" <th nowrap >จังหวัด</th> \n");
			  h.append(" <th nowrap >อำเภอ</th> \n");
			  h.append(" <th nowrap >ร้านค้า </th> \n");
			  h.append(" <th nowrap >ที่อยู่ </th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ได้ขาย</th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			 }
			
			h.append("</tr> \n");
			return h;
		}
}
