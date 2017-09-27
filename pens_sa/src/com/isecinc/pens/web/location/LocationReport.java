package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import util.ExcelHeader;

import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

public class LocationReport {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuffer searchCustomerCheckInList(LocationBean c,boolean excel) throws Exception {
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
				
				sql = genSqlSearchCustomerCheckInList(conn, c);
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
					if("report1".equalsIgnoreCase(c.getReportType())){
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='24%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.stringValueChkNull(rst.getDate("checkin_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
						
					}else if("report2".equalsIgnoreCase(c.getReportType())){
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("google_address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.stringValueChkNull(rst.getDate("checkin_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
					}else if("report3".equalsIgnoreCase(c.getReportType())){
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("salesrep_code"))+"-"+Utils.isNull(rst.getString("salesrep_name"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='8%'>"+Utils.stringValueChkNull(rst.getDate("checkin_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
					}else if("report4".equalsIgnoreCase(c.getReportType())){
						html.append("<td class='"+tdTextClass+"' width='8%'>"+Utils.isNull(rst.getString("region_name"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("salesrep_code"))+"-"+Utils.isNull(rst.getString("salesrep_name"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='20%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.stringValueChkNull(rst.getDate("checkin_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_visit"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_equals_trip"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+getTrip(Utils.isNull(rst.getString("trip")))+"</td>");
					}else if("report5".equalsIgnoreCase(c.getReportType())){
						
						html.append("<td class='"+tdTextClass+"' width='12%'>"+Utils.isNull(rst.getString("salesrep_code"))+"-"+Utils.isNull(rst.getString("salesrep_name"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='10%'>"+Utils.isNull(rst.getString("province"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='10%'>"+Utils.isNull(rst.getString("amphur"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='15%'>"+Utils.isNull(rst.getString("customer_code"))+"-"+Utils.isNull(rst.getString("customer_desc"))+"</td>");
						html.append("<td class='"+tdTextClass+"' width='20%'>"+Utils.isNull(rst.getString("address"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("order_number"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.stringValueChkNull(rst.getDate("checkin_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_sales"))+"</td>");
						html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("flag_visit"))+"</td>");
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
	 
	 public static StringBuilder genSqlSearchCustomerCheckInList(Connection conn,LocationBean c) throws Exception {
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
				}
				
				if( !Utils.isNull(c.getProvince()).equals("")){
					province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
				}
				if( !Utils.isNull(c.getDistrict()).equals("")){
					district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
				}
				sql.append("\n select A.* FROM ( ");
				sql.append("\n select ");
				sql.append("\n tc.order_number ,tc.checkin_date ");
				sql.append("\n ,( CASE WHEN tc.flag ='Y' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_sales");
				sql.append("\n ,( CASE WHEN tc.flag ='N' THEN 'Y' ");
				sql.append("\n         ELSE '' END) as flag_visit");
				sql.append("\n ,( CASE WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip1 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip2 THEN 'Y' ");
				sql.append("\n         WHEN to_number(to_char(tc.checkin_date,'dd')) = cs.trip3 THEN 'Y' ");
				sql.append("\n         ELSE 'N' END) as flag_equals_trip");
				sql.append("\n ,("
						    + " cs.trip1 || decode(cs.trip2, null,null ,','||cs.trip2) || decode(cs.trip3, null,null ,','||cs.trip3)"
						    + ")as trip");
				sql.append("\n , cs.trip1 ");
				if("report1".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,c.customer_code ,c.customer_desc ");
					sql.append("\n ,cs.address ");
					
					orderBy = " A.checkin_date,A.order_number asc";
				}else if("report2".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,c.customer_code ,c.customer_desc ");
					sql.append("\n ,cs.address  ");
					sql.append("\n ,(select cl.address from xxpens_om_trip_cust_loc cl ");
					sql.append("\n   where cl.cust_account_id = c.customer_id) as google_address ");
					orderBy = " A.checkin_date,A.order_number asc";
					
				}else if("report3".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
					sql.append("\n ,c.customer_code ,c.customer_desc ");
					sql.append("\n ,cs.address ");
					
					orderBy = " A.checkin_date,A.order_number asc";
				}else if("report4".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,s.region ,s.region_name ");
					sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
					sql.append("\n ,c.customer_code ,c.customer_desc ");
					sql.append("\n ,cs.address ");
					
					orderBy = " A.checkin_date,A.order_number asc";
				}else if("report5".equalsIgnoreCase(c.getReportType())){
					sql.append("\n ,s.user_name as salesrep_code ,s.salesrep_name ");
					sql.append("\n ,cs.province ,cs.amphur ");
					sql.append("\n ,c.customer_code ,c.customer_desc ");
					sql.append("\n ,cs.address ");
					
					orderBy = " A.checkin_date,A.order_number asc";
				}
				
			   //Real
			   if("real".equalsIgnoreCase(c.getTripType())){
				    sql.append("\n from xxpens_om_trip_checkin tc , ");
					sql.append("\n xxpens_ar_cust_sales_all cs ,  ");
					sql.append("\n xxpens_salesreps_v s , ");
					sql.append("\n xxpens_bi_mst_customer c  ");
					sql.append("\n where 1=1 ");
					sql.append("\n and tc.account_number = c.customer_code ");
					sql.append("\n and cs.code = s.code ");
					sql.append("\n and cs.primary_salesrep_id = s.salesrep_id ");
					sql.append("\n and cs.cust_account_id =  c.customer_id  ");
					
					if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
						  Date startDate = Utils.parse(c.getDay(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						  String startDateStr = Utils.stringValue(startDate, Utils.DD_MM_YYYY_WITH_SLASH);
						  
						  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
							  Date endDate = Utils.parse(c.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
							  String endDateStr = Utils.stringValue(endDate, Utils.DD_MM_YYYY_WITH_SLASH);
							  
							  sql.append("\n and trunc(tc.checkin_date) >= to_date('"+startDateStr+"','"+Utils.DD_MM_YYYY_WITH_SLASH+"')");
							  sql.append("\n and trunc(tc.checkin_date) <= to_date('"+endDateStr+"','"+Utils.DD_MM_YYYY_WITH_SLASH+"')");
						  }else{
							  sql.append("\n and trunc(tc.checkin_date) = to_date('"+startDateStr+"','"+Utils.DD_MM_YYYY_WITH_SLASH+"')");
						  }//if
						}//if
						
						if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
							condMonth = condMonth.substring(0,condMonth.length()-1);
							sql.append("\n and to_char(tc.checkin_date,'yyyymm') in("+condMonth+")");
						}
				}else{
					orderBy = " to_number(A.trip1) asc";
					
					//Trip
					sql.append("\n from  ");
					sql.append("\n xxpens_ar_cust_sales_all cs ,  ");
					sql.append("\n xxpens_salesreps_v s , ");
					sql.append("\n xxpens_bi_mst_customer c  ");
					sql.append("\n left outer join ");
					sql.append("\n ( ");
					sql.append("\n select tc.* from xxpens_om_trip_checkin tc where 1=1");
					if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
						  Date startDate = Utils.parse(c.getDay(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						  String startDateStr = Utils.stringValue(startDate, Utils.DD_MM_YYYY_WITH_SLASH);
						  
						  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
							  Date endDate = Utils.parse(c.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
							  String endDateStr = Utils.stringValue(endDate, Utils.DD_MM_YYYY_WITH_SLASH);
							  
							  sql.append("\n and trunc(tc.checkin_date) >= to_date('"+startDateStr+"','"+Utils.DD_MM_YYYY_WITH_SLASH+"')");
							  sql.append("\n and trunc(tc.checkin_date) <= to_date('"+endDateStr+"','"+Utils.DD_MM_YYYY_WITH_SLASH+"')");
						  }else{
							  sql.append("\n and trunc(tc.checkin_date) = to_date('"+startDateStr+"','"+Utils.DD_MM_YYYY_WITH_SLASH+"')");
						  }//if
						}//if
						
						if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
							condMonth = condMonth.substring(0,condMonth.length()-1);
							sql.append("\n and to_char(tc.checkin_date,'yyyymm') in("+condMonth+")");
						}
				    sql.append("\n )tc  on tc.account_number = c.customer_code ");
					sql.append("\n where 1=1 ");
					sql.append("\n and cs.code = s.code ");
					sql.append("\n and cs.primary_salesrep_id = s.salesrep_id ");
					sql.append("\n and cs.cust_account_id =  c.customer_id  ");
					
					if("DAY".equalsIgnoreCase(c.getTypeSearch()) && !Utils.isNull(c.getDay()).equals("") ){
						  Calendar cal = Calendar.getInstance();
						  Date startDate = Utils.parse(c.getDay(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						  cal.setTime(startDate);
						  int startTrip = cal.get(Calendar.DAY_OF_MONTH);
						  
						  if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
							  Date endDate = Utils.parse(c.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
							  cal.setTime(endDate);
							  int endTrip = cal.get(Calendar.DAY_OF_MONTH);
							  
							  sql.append("\n and (");
							  sql.append("\n       (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
							  sql.append("\n    or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
							  sql.append("\n    or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
							  sql.append("\n  )");
							  
						  }else{
							  sql.append("\n and (");
							  sql.append("\n   cs.trip1 = "+startTrip +" or cs.trip2 = "+startTrip +" or cs.trip3 ="+startTrip);
							  sql.append("\n )");
						  }//if
					}//if
						
					/** Case Month 1-98**/
					if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
						  String startTrip = "1";
						  String endTrip = "98";
						  sql.append("\n and (");
						  sql.append("\n       (cs.trip1 >= "+startTrip +" and cs.trip1 <= "+endTrip +")");
						  sql.append("\n    or (cs.trip2 >= "+startTrip +" and cs.trip2 <= "+endTrip +")");
						  sql.append("\n    or (cs.trip3 >= "+startTrip +" and cs.trip3 <= "+endTrip +")");
						  sql.append("\n  )");
					}
					
				}//if
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
				sql.append("\n )A ");
				sql.append("\n  ORDER BY "+orderBy);

			} catch (Exception e) {
				throw e;
			} finally { 
				
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
			if("report1".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >ร้านค้า </th> \n");
			  h.append(" <th nowrap >ที่อยู่ </th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			}else if("report2".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >ร้านค้า </th> \n");
			  h.append(" <th nowrap >ที่อยู่ </th> \n");
			  h.append(" <th nowrap >ที่อยู่ตาม Google </th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			}else if("report3".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >พนักงานขาย</th> \n");
			  h.append(" <th nowrap >ร้านค้า</th> \n");
			  h.append(" <th nowrap >ที่อยู่</th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			}else if("report4".equalsIgnoreCase(c.getReportType())){
			  h.append(" <th nowrap >ภาคการขาย</th> \n");
			  h.append(" <th nowrap >พนักงานขาย</th> \n");
			  h.append(" <th nowrap >ร้านค้า</th> \n");
			  h.append(" <th nowrap >ที่อยู่</th> \n");
			  h.append(" <th nowrap >เลขที่เอกสาร </th> \n");
			  h.append(" <th nowrap >วันที่ขาย </th> \n");
			  h.append(" <th nowrap >ได้ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
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
			  h.append(" <th nowrap >ได้ขาย </th> \n");
			  h.append(" <th nowrap >ได้เยี่ยม </th> \n");
			  h.append(" <th nowrap >ตรง Trip </th> \n");
			  h.append(" <th nowrap >Trip </th> \n");
			 }
			
			h.append("</tr> \n");
			return h;
		}
}
