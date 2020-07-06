package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.CConstantsBean;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.dao.SalesrepDAO;
import com.pens.util.CConstants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.MonitorTime;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

import sun.misc.Cleaner;

public class LocationDataSummaryReport {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuffer searchReport(String path,LocationBean c,boolean excel) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			StringBuffer html = new StringBuffer("");
			String tdTextClass = "td_text";
			String tdTextCenterClass = "td_text_center";
			String tripDate = "";//format christ date
			String tripDateShow = "";
			int r = 0;
			int count_cust_bill_by_trip = 0;
			int count_order_bill_by_trip = 0;
			int count_visit_cust_by_real= 0;
			int count_sale_cust_by_real= 0;
			int count_cust_equals_trip= 0;
			int count_cust_not_equals_trip= 0;
			String cond_mmyyyy = "",condMonth ="" ,mmyyyyB ="";
			try {
				conn = DBConnection.getInstance().getConnectionApps();
				
				 //Get SalesrepCode
				if(!Utils.isNull(c.getSalesrepCode()).equals("")){
				   SalesrepBean salesRep = SalesrepDAO.getSalesrepBeanById(conn, Utils.isNull(c.getSalesrepCode()));
			       c.setSalesCode(salesRep.getCode());
				}
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					logger.debug("chkMonth:"+c.getChkMonth().length);
					/** Set Group Display  **/
					for(int i=0;i<c.getChkMonth().length;i++){
						logger.debug("name:["+i+"]value:["+c.getChkMonth()[i]+"]");
						condMonth +="'"+c.getChkMonth()[i]+"',";
					}
				}else{
					//Get Month and Year to ChristDate 2018
					String mm = c.getDay().substring(3,5);
					String yyyy = String.valueOf(Integer.parseInt(c.getDay().substring(6,10))-543);
					cond_mmyyyy = "/"+mm+"/"+yyyy;
					logger.debug("mmyyyy:"+cond_mmyyyy);
					
					String yyyyB = String.valueOf(Integer.parseInt(c.getDay().substring(6,10)));
					mmyyyyB = "/"+mm+"/"+yyyyB;
					logger.debug("mmyyyyB:"+mmyyyyB);
				}
				MonitorTime monitorTime = new MonitorTime("Monitor Spider");
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
				   sql = genSqlMonitorReportByMonth(conn, c,condMonth);
				}else{
				   sql = genSqlMonitorReportByDay(conn, c,cond_mmyyyy);
				}
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
					if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
						tripDateShow = Utils.isNull(rst.getString("year_month"));
					}else{
						tripDate = genTripDate(Utils.isNull(rst.getString("trip_day")),cond_mmyyyy);
						tripDateShow  =genTripDate(Utils.isNull(rst.getString("trip_day")),mmyyyyB);
					}
					html.append("<tr>");
					html.append("<td class='"+tdTextClass+"' width='5%'>"+Utils.isNull(rst.getString("zone_name"))+"</td>");
					html.append("<td class='"+tdTextClass+"' width='10%'>"+Utils.isNull(rst.getString("sales_code"))+"-"+Utils.isNull(rst.getString("salesrep_name"))+"</td>");
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+tripDateShow+"</td>");
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("count_cust_bill_by_trip")));
					count_cust_bill_by_trip +=rst.getInt("count_cust_bill_by_trip");
					
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("count_order_bill_by_trip")));
					 count_order_bill_by_trip +=rst.getInt("count_order_bill_by_trip");
					html.append("</td>");
					
					//Re arrange Column
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");
					html.append(Utils.isNull(rst.getString("count_visit_cust_by_real")) );
					  count_visit_cust_by_real  +=rst.getInt("count_visit_cust_by_real");
					html.append("</td>");
					
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("count_sale_cust_by_real")));
					  count_sale_cust_by_real += rst.getInt("count_sale_cust_by_real");
					html.append("</td>");
					
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");
					html.append(Utils.isNull(rst.getString("count_cust_equals_trip")));
				      count_cust_equals_trip += rst.getInt("count_cust_equals_trip");
					html.append("</td>");
					
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");
					html.append(Utils.isNull(rst.getString("count_cust_not_equals_trip")));
					count_cust_not_equals_trip += rst.getInt("count_cust_not_equals_trip");
					html.append("</td>");
					
				  html.append("</tr>");
				}//while
				
				if(r >= 1){
			       html.append("</table>");
				}
				//debug Time use Process
				monitorTime.debugUsedTime();
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
	
	private static String genTripDate(String tripDay,String mmyyyy){
		return tripDay.length()==1?("0"+tripDay+mmyyyy):tripDay+mmyyyy;
	}
	 
	 public static StringBuilder genSqlMonitorReportByDay(Connection conn,LocationBean c,String cond_mmyyyy) throws Exception {
			StringBuilder sql = new StringBuilder();
			String startDateStr = "";
			String endDateStr = "";
			int startTrip = 0,endTrip=0;
			Calendar cal = Calendar.getInstance();
			try {
				//Get TripDay
				Date startDate = DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				c.setStartDate(startDateStr);
				cal.setTime(startDate);
				startTrip = cal.get(Calendar.DAY_OF_MONTH);
				
				if( !Utils.isNull(c.getDayTo()).equals("")){
					Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					c.setEndDate(endDateStr);
				}
				
				sql.append("\n select A.* FROM ( ");
				sql.append("\n select cs.zone_name,cs.code as sales_code,cs.primary_salesrep_id");
				sql.append("\n ,cs.salesrep_name,cs.trip_day ,to_number(cs.trip_day) as trip_day_num  ");

				//count_cust_bill_by_trip
				sql.append(""+genCountCustBillByTrip(conn, c,""));
				
				//count_order_bill_by_trip
				sql.append(""+genCountOrderBillByTrip(conn, c,""));
				
				//count_visit_cust_by_real
				sql.append(""+genCountVisitCustByReal(conn, c,""));
				
				//count_sale_cust_by_real
				sql.append(""+genCountSaleCustByReal(conn, c,""));
				
				//count_cust_equals_trip
				sql.append(""+genCountCustEqualsTrip(conn, c,""));
				
				//count_cust_not_equals_trip
				sql.append(""+genCountCustNotEqualsTrip(conn, c,""));
				
				sql.append("\n from  ");
				sql.append("\n ( ");
				sql.append("\n  select M.* ");
				sql.append("\n  ,to_date(M.tripday_str||'"+cond_mmyyyy+"','dd/mm/yyyy') as trip_date ");
				sql.append("\n  from( ");
				//Trip1
				sql.append("\n  select distinct z.zone_name,cs.code ,s.salesrep_name,cs.primary_salesrep_id,cs.trip1 as trip_day ");
				sql.append("\n ,(case when length(cs.trip1)=1 then '0'||cs.trip1 else cs.trip1 end) as tripday_str ");
				sql.append("\n  from apps.xxpens_ar_cust_sales_vs cs , ");
				sql.append("\n  apps.xxpens_salesreps_v s, ");
				sql.append("\n  PENSBI.XXPENS_BI_MST_SALES_ZONE z");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n  and cs.code = s.code ");
				sql.append("\n  and z.salesrep_id = s.salesrep_id ");
				sql.append("\n  and REGEXP_LIKE(cs.trip1,'^[[:digit:]]+$')  ");
				//genWhereCondSql
				sql.append(     genWhereCondSql(conn, "", c));
				//genDay trip1
				if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					 Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					 cal.setTime(endDate);
					 endTrip = cal.get(Calendar.DAY_OF_MONTH);
					  
					 sql.append("\n and (nvl(cs.trip1,0) >= "+startTrip +" and nvl(cs.trip1,0) <= "+endTrip +")");
				}else{
					 sql.append("\n and nvl(cs.trip1,0) = "+startTrip+"");
				}//if
				
				sql.append("\n UNION ");
				//Trip2
				sql.append("\n  select distinct z.zone_name,cs.code,s.salesrep_name,cs.primary_salesrep_id ,cs.trip2 as trip_day  ");
				sql.append("\n ,(case when length(cs.trip2)=1 then '0'||cs.trip2 else cs.trip2 end) as tripday_str ");
				sql.append("\n  from apps.xxpens_ar_cust_sales_vs cs , ");
				sql.append("\n  apps.xxpens_salesreps_v s, ");
				sql.append("\n  PENSBI.XXPENS_BI_MST_SALES_ZONE z");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n  and cs.code = s.code ");
				sql.append("\n  and z.salesrep_id = s.salesrep_id ");
				sql.append("\n  and REGEXP_LIKE(cs.trip2,'^[[:digit:]]+$')  ");
				//genWhereCondSql
				sql.append(     genWhereCondSql(conn, "", c));
				//genDay trip1
				if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					 Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					 cal.setTime(endDate);
					 endTrip = cal.get(Calendar.DAY_OF_MONTH);
					  
					 sql.append("\n and (nvl(cs.trip2,0) >= "+startTrip +" and nvl(cs.trip2,0) <= "+endTrip +")");
				}else{
					 sql.append("\n and nvl(cs.trip2,0) = "+startTrip+"");
				}//if
				sql.append("\n UNION ");
				//trip3
				sql.append("\n  select distinct z.zone_name,cs.code,s.salesrep_name,cs.primary_salesrep_id ,cs.trip3 as trip_day  ");
				sql.append("\n ,(case when length(cs.trip3)=1 then '0'||cs.trip3 else cs.trip3 end) as tripday_str ");
				sql.append("\n  from apps.xxpens_ar_cust_sales_vs cs ,");
				sql.append("\n  apps.xxpens_salesreps_v s , ");
				sql.append("\n  PENSBI.XXPENS_BI_MST_SALES_ZONE z");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n  and cs.code = s.code ");
				sql.append("\n  and z.salesrep_id = s.salesrep_id ");
				sql.append("\n  and REGEXP_LIKE(cs.trip3,'^[[:digit:]]+$') ");
				//genWhereCondSql
				sql.append(     genWhereCondSql(conn, "", c));
				//genDay trip1
				if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					 Date endDate = DateUtil.parse(c.getDayTo(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
					 cal.setTime(endDate);
					 endTrip = cal.get(Calendar.DAY_OF_MONTH);
					  
					 sql.append("\n and (nvl(cs.trip3,0) >= "+startTrip +" and nvl(cs.trip3,0) <= "+endTrip +")");
				}else{
					 sql.append("\n and nvl(cs.trip3,0) = "+startTrip+"");
				}//if
				sql.append("\n UNION ");
				//Case No set Trip (??)What 
				sql.append("\n  select distinct z.zone_name,cs.code,s.salesrep_name,cs.primary_salesrep_id ,'"+startTrip+"' as trip_day  ");
				sql.append("\n ,(case when length('"+startTrip+"')=1 then '0'||'"+startTrip+"' else '"+startTrip+"' end) as tripday_str ");
				sql.append("\n  from apps.xxpens_ar_cust_sales_vs cs ,");
				sql.append("\n  apps.xxpens_salesreps_v s,");
				sql.append("\n  PENSBI.XXPENS_BI_MST_SALES_ZONE z");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n  and cs.code = s.code ");
				sql.append("\n  and z.salesrep_id = s.salesrep_id ");
				//genWhereCondSql
				sql.append(     genWhereCondSql(conn, "", c));
				sql.append("\n   )M ");
				sql.append("\n  )cs ");
				sql.append("\n )A ");
				sql.append("\n  ORDER BY A.sales_code ,A.trip_day_num ");

				//debug log sql
				if(logger.isDebugEnabled()){
					//logger.debug("/n"+sql.toString());
					FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString(),"TIS-620");
				}
			} catch (Exception e) {
				throw e;
			} finally { 
				
			}
			return sql;
	}
	 
	 public static StringBuilder genSqlMonitorReportByMonth(Connection conn,LocationBean c,String condMonth) throws Exception {
		StringBuilder sql = new StringBuilder();
		try {
			//Get TripDay
		    condMonth = condMonth.substring(0,condMonth.length()-1);//remove last comma ,
			
			sql.append("\n select A.* FROM ( ");
			sql.append("\n select cs.zone_name,cs.code as sales_code,cs.primary_salesrep_id");
			sql.append("\n ,cs.salesrep_name,cs.year_month ");

			//count_cust_bill_by_trip
			sql.append(""+genCountCustBillByTrip(conn, c,condMonth));
			
			//count_order_bill_by_trip
			sql.append(""+genCountOrderBillByTrip(conn, c,condMonth));
			
			//count_visit_cust_by_real
			sql.append(""+genCountVisitCustByReal(conn, c,condMonth));
			
			//count_sale_cust_by_real
			sql.append(""+genCountSaleCustByReal(conn, c,condMonth));
			
			//count_cust_equals_trip
			sql.append(""+genCountCustEqualsTrip(conn, c,condMonth));
			
			//count_cust_not_equals_trip
			sql.append(""+genCountCustNotEqualsTrip(conn, c,condMonth));
			
		
			sql.append("\n from  ");
			sql.append("\n ( ");
			sql.append("\n  select M.* from( ");
			sql.append("\n   select distinct z.zone_name,cs.code ,s.salesrep_name,cs.primary_salesrep_id ");
			sql.append("\n   ,to_char(tc.checkin_date,'yyyymm') as year_month");
			sql.append("\n   from");
			sql.append("\n   apps.xxpens_om_trip_checkin tc,");
			sql.append("\n   apps.xxpens_ar_cust_sales_vs cs ,");
			sql.append("\n   apps.xxpens_salesreps_v s,");
			sql.append("\n   PENSBI.XXPENS_BI_MST_SALES_ZONE z");
			sql.append("\n   where 1=1 ");
			sql.append("\n   and tc.salesrep_id = cs.primary_salesrep_id ");
			sql.append("\n   and cs.primary_salesrep_id = s.salesrep_id ");
			sql.append("\n   and cs.code = s.code ");
			sql.append("\n   and z.salesrep_id = s.salesrep_id ");
			/*sql.append("\n   and REGEXP_LIKE(cs.trip1,'^[[:digit:]]+$')  ");
			sql.append("\n   and REGEXP_LIKE(cs.trip2,'^[[:digit:]]+$')  ");
			sql.append("\n   and REGEXP_LIKE(cs.trip3,'^[[:digit:]]+$')  ");*/
			//genWhereCondSql
			sql.append(      genWhereCondSql(conn, "", c));
			sql.append("\n   and to_char(tc.checkin_date,'yyyymm') in("+condMonth+")");
			sql.append("\n   )M ");
			sql.append("\n  )cs ");
			sql.append("\n )A ");
			sql.append("\n ORDER BY A.zone_name,A.sales_code ");

			//debug log sql
			if(logger.isDebugEnabled()){
				//logger.debug("/n"+sql.toString());
				FileUtil.writeFile("d:/dev_temp/temp/sql.sql", sql.toString(),"TIS-620");
			}
		} catch (Exception e) {
			throw e;
		} finally { 
			
		}
		return sql;
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
			  h.append(" <th nowrap >ภาคตามสายดูแล</th> \n");
			  h.append(" <th nowrap >พนักงานขาย</th> \n");
			  if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
			      h.append(" <th nowrap >เดือน</th> \n");
			  }else{
				  h.append(" <th nowrap >วันที่</th> \n"); 
			  }
			  h.append(" <th >จำนวนร้านที่เปิดบิลขาย</th> \n");
			  h.append(" <th >จำนวนบิลขาย</th> \n");
			  h.append(" <th >จำนวนร้านที่ บันทึกเยี่ยมจาก Spider</th> \n");
			  h.append(" <th >จำนวนร้านที่ บันทึกขายจาก Spider</th> \n");
			  h.append(" <th >จำนวนร้านที่ ตรง Trip </th> \n");
			  h.append(" <th >จำนวนร้านค้าที่ ไม่ตรง Trip</th> \n");
			h.append("</tr> \n");
			return h;
		}
		
	
	 private static StringBuffer genCountCustBillByTrip(Connection conn,LocationBean c,String condMonth){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(distinct cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n apps.xxpens_ar_cust_sales_vs cs_sub ");
				sql.append("\n left outer join( ");
				sql.append("\n select tc.customer_id as cust_account_id,tc.salesrep_id,tc.sales_order_date");
				sql.append("\n from PENSBI.XXPENS_BI_SALES_ANALYSIS_V tc");
				sql.append("\n where tc.customer_category in('ORDER - VAN SALES','ORDER - CREDIT SALES')");
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					sql.append("\n  and to_char(tc.sales_order_date,'yyyymm') in("+condMonth+")");
				}else{
					if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
					   sql.append("\n and tc.sales_order_date >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
					   sql.append("\n and tc.sales_order_date <= TO_TIMESTAMP('"+c.getEndDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')");
					}else{
					   sql.append("\n and tc.sales_order_date between to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
					   sql.append("\n and TO_TIMESTAMP('"+c.getStartDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')"); 
					}
				}
			 sql.append("\n ) tc_sub on tc_sub.cust_account_id = cs_sub.cust_account_id");
			 sql.append("\n and tc_sub.salesrep_id =cs_sub.primary_salesrep_id "); 
			 sql.append("\n where 1=1 "); 
			 if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and to_char(tc_sub.sales_order_date,'yyyymm') = cs.year_month");
			     
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 }else{
				 //DAY
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and to_number(to_char(tc_sub.sales_order_date,'dd')) = cs.trip_day");
			     
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 }
			
			 //where cond sql
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_cust_bill_by_trip ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 
	 private static StringBuffer genCountOrderBillByTrip(Connection conn,LocationBean c,String condMonth){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(distinct tc_sub.sales_order_no) from ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n apps.xxpens_ar_cust_sales_vs cs_sub ");
				sql.append("\n left outer join( ");
				sql.append("\n select tc.sales_order_no,tc.customer_id as cust_account_id,tc.salesrep_id,tc.sales_order_date");
				sql.append("\n from PENSBI.XXPENS_BI_SALES_ANALYSIS_V tc");
				sql.append("\n where tc.customer_category in('ORDER - VAN SALES','ORDER - CREDIT SALES')");
				if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
					sql.append("\n  and to_char(tc.sales_order_date,'yyyymm') in("+condMonth+")");
				}else{
					if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
					   sql.append("\n and tc.sales_order_date >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
					   sql.append("\n and tc.sales_order_date <= TO_TIMESTAMP('"+c.getEndDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')");
					}else{
					   sql.append("\n and tc.sales_order_date between to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
					   sql.append("\n and TO_TIMESTAMP('"+c.getStartDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')"); 
					}
				}
			 sql.append("\n ) tc_sub on tc_sub.cust_account_id = cs_sub.cust_account_id");
			 sql.append("\n and tc_sub.salesrep_id =cs_sub.primary_salesrep_id "); 
			 sql.append("\n where 1=1 "); 
			 if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and to_char(tc_sub.sales_order_date,'yyyymm') = cs.year_month");
			     
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 }else{
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and to_number(to_char(tc_sub.sales_order_date,'dd')) = cs.trip_day");
			     
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 }
			 //where cond sql
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_order_bill_by_trip ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 
	 private static StringBuffer genCountVisitCustByReal(Connection conn,LocationBean c,String condMonth){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(cs_sub.cust_account_id) from ");
			 sql.append("\n apps.xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n apps.xxpens_ar_cust_sales_vs cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub ");
			 sql.append("\n where 1=1 "); 
			
			 
			 sql.append("\n and (tc_sub.flag ='N' or tc_sub.flag='Y') ");
			 if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and to_char(tc_sub.checkin_date,'yyyymm')  = cs.year_month");
				 
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
				 sql.append("\n and cs_sub.code = s_sub.code ");
			 }else{
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
				 
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd') ) = cs.trip_day ");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
				 sql.append("\n and cs_sub.code = s_sub.code ");
				 
				 if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
				   sql.append("\n and tc_sub.checkin_date >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
				   sql.append("\n and tc_sub.checkin_date <= TO_TIMESTAMP('"+c.getEndDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')");
				 }else{
				   sql.append("\n and tc_sub.checkin_date between to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
				   sql.append("\n and TO_TIMESTAMP('"+c.getStartDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')"); 
				 }
			 }
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 
			 sql.append("\n )as count_visit_cust_by_real ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 private static StringBuffer genCountSaleCustByReal(Connection conn,LocationBean c ,String condMonth){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n apps.xxpens_ar_cust_sales_vs cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub ");
			 sql.append("\n where 1=1 "); 
			 if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
				 sql.append("\n and to_char(tc_sub.checkin_date,'yyyymm') = cs.year_month ");
			     
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
				 sql.append("\n and cs_sub.code = s_sub.code ");
				 
				 sql.append("\n  and to_char(tc_sub.checkin_date,'yyyymm') in("+condMonth+")");
			 }else{
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
				 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd') ) = cs.trip_day ");
			     
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
				 sql.append("\n and cs_sub.code = s_sub.code ");
				 
				 if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
				   sql.append("\n and tc_sub.checkin_date >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
				   sql.append("\n and tc_sub.checkin_date <= TO_TIMESTAMP('"+c.getEndDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')");
				 }else{
				   sql.append("\n and tc_sub.checkin_date between to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
				   sql.append("\n and TO_TIMESTAMP('"+c.getStartDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')"); 
				 }
			 }
			 sql.append("\n and (tc_sub.flag='Y') ");
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_sale_cust_by_real ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 private static StringBuffer genCountCustEqualsTrip(Connection conn,LocationBean c,String condMonth){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(cs_sub.cust_account_id) from ");
			 sql.append("\n apps.xxpens_om_trip_checkin tc_sub,");
			 sql.append("\n apps.xxpens_ar_cust_sales_vs cs_sub , ");
			 sql.append("\n apps.xxpens_salesreps_v s_sub ");
			 sql.append("\n where 1=1 "); 
			 if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and to_char(tc_sub.checkin_date,'yyyymm') = cs.year_month"); 
				 
				 sql.append("\n and ( nvl(cs_sub.trip1,0) = to_number(to_char(tc_sub.checkin_date,'dd')) ");
				 sql.append("\n    or nvl(cs_sub.trip2,0) = to_number(to_char(tc_sub.checkin_date,'dd')) ");
				 sql.append("\n    or nvl(cs_sub.trip3,0) = to_number(to_char(tc_sub.checkin_date,'dd')) )");  
				 
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  "); 
				 sql.append("\n and cs_sub.code = s_sub.code ");
				 
				 sql.append("\n  and to_char(tc_sub.checkin_date,'yyyymm') in("+condMonth+")");
			 }else{
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and trunc(tc_sub.checkin_date) = cs.trip_date"); 
				 sql.append("\n and ( nvl(cs_sub.trip1,0) = cs.trip_day ");
				 sql.append("\n    or nvl(cs_sub.trip2,0) = cs.trip_day ");
				 sql.append("\n    or nvl(cs_sub.trip3,0) = cs.trip_day )");  
				 
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  "); 
				 sql.append("\n and cs_sub.code = s_sub.code ");
				 
				 if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
				   sql.append("\n and tc_sub.checkin_date >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
				   sql.append("\n and tc_sub.checkin_date <= TO_TIMESTAMP('"+c.getEndDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')");
				 }else{
				   sql.append("\n and tc_sub.checkin_date between to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
				   sql.append("\n and TO_TIMESTAMP('"+c.getStartDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')"); 
				 }
			 }
			 sql.append("\n and (tc_sub.flag ='N' or tc_sub.flag='Y') ");
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_cust_equals_trip ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 
	 private static StringBuffer genCountCustNotEqualsTrip(Connection conn,LocationBean c,String condMonth){
		 StringBuffer sql = new StringBuffer();
		 try {
			
			 sql.append("\n ,( ");
			 sql.append("\n select count(cs_sub.cust_account_id) from ");
			 sql.append("\n apps.xxpens_om_trip_checkin tc_sub,");
			 sql.append("\n apps.xxpens_ar_cust_sales_vs cs_sub , ");
			 sql.append("\n apps.xxpens_salesreps_v s_sub ");
			 sql.append("\n where 1=1 "); 
			 if("MONTH".equalsIgnoreCase(c.getTypeSearch())){
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and to_char(tc_sub.checkin_date,'yyyymm') = cs.year_month"); 
				 sql.append("\n and nvl(cs_sub.trip1,0) <> to_number(to_char(tc_sub.checkin_date,'dd')) ");
				 sql.append("\n and nvl(cs_sub.trip2,0) <> to_number(to_char(tc_sub.checkin_date,'dd')) ");
				 sql.append("\n and nvl(cs_sub.trip3,0) <> to_number(to_char(tc_sub.checkin_date,'dd')) ");  
				 
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  "); 
				 sql.append("\n and cs_sub.code = s_sub.code ");
				 
				 sql.append("\n  and to_char(tc_sub.checkin_date,'yyyymm') in("+condMonth+")");
			 }else{
				 //DAY
				 sql.append("\n /** join main sql **/");
				 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
				 sql.append("\n and cs_sub.code = cs.code ");
				 sql.append("\n and trunc(tc_sub.checkin_date) = cs.trip_date"); 
				 sql.append("\n and nvl(cs_sub.trip1,0) <> cs.trip_day ");
				 sql.append("\n and nvl(cs_sub.trip2,0) <> cs.trip_day ");
				 sql.append("\n and nvl(cs_sub.trip3,0) <> cs.trip_day ");  
				 
				 sql.append("\n /** join sub sql **/");
				 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
				 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
				 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  "); 
				 sql.append("\n and cs_sub.code = s_sub.code ");
				 
				 if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
				   sql.append("\n and tc_sub.checkin_date >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
				   sql.append("\n and tc_sub.checkin_date <= TO_TIMESTAMP('"+c.getEndDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')");
				 }else{
				   sql.append("\n and tc_sub.checkin_date between to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
				   sql.append("\n and TO_TIMESTAMP('"+c.getStartDate()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')"); 
				 } 
			 }
			 sql.append("\n and (tc_sub.flag ='N' or tc_sub.flag='Y') ");
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_cust_not_equals_trip ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 
	
	 private static StringBuffer genWhereCondSql(Connection conn,String schema_name,LocationBean c){
		 StringBuffer sql = new StringBuffer();
		 String province = "";
		 String district = "";
		 try {
			 //Default condition
			 sql.append("\n and s"+schema_name+".region in(0,1,2,3,4) ");
			 sql.append("\n and s"+schema_name+".sales_channel in('C','S') ");
			 sql.append("\n and s"+schema_name+".salesrep_full_name not like '%ยกเลิก%'");
			 sql.append("\n and s"+schema_name+".isactive ='Y' ");
			 
			if( !Utils.isNull(c.getProvince()).equals("")){
				province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
			}
			if( !Utils.isNull(c.getDistrict()).equals("")){
				district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
			}
			if( !Utils.isNull(c.getCustomerCode()).equals("")){
				sql.append("\n and cs"+schema_name+".account_number ='"+c.getCustomerCode()+"' ");
			}
			if( !Utils.isNull(c.getCustomerName()).equals("")){
				sql.append("\n and cs"+schema_name+".party_name LIKE '%"+c.getCustomerName()+"%' ");
			}
			if( !Utils.isNull(c.getCustCatNo()).equals("")){
				sql.append("\n and s"+schema_name+".sales_channel = '"+Utils.isNull(c.getCustCatNo())+"' ");
			}
			if( !Utils.isNull(c.getSalesChannelNo()).equals("")){
				sql.append("\n and s"+schema_name+".region = "+Utils.isNull(c.getSalesChannelNo())+" ");
			}
			if( !Utils.isNull(c.getSalesrepCode()).equals("")){
				sql.append("\n and cs"+schema_name+".primary_salesrep_id = "+Utils.isNull(c.getSalesrepCode())+"");
				sql.append("\n and s"+schema_name+".salesrep_id = "+Utils.isNull(c.getSalesrepCode())+"");
			}
			if( !Utils.isNull(c.getSalesCode()).equals("")){
				sql.append("\n and cs"+schema_name+".code = '"+Utils.isNull(c.getSalesCode())+"'");
			    sql.append("\n and s"+schema_name+".code = '"+Utils.isNull(c.getSalesCode())+"'");
			}
			if( !Utils.isNull(province).equals("")){
				sql.append("\n and cs"+schema_name+".province = '"+province+"' ");
			}
			if( !Utils.isNull(district).equals("")){
				sql.append("\n and cs"+schema_name+".amphur = '"+district+"' ");
			}
			if( !Utils.isNull(c.getSalesZone()).equals("")){
			  /*  sql.append("\n  and cs"+schema_name+".primary_salesrep_id in(");
			    sql.append("\n    select salesrep_id from pensbi.XXPENS_BI_MST_SALES_ZONE ");
			    sql.append("\n    where zone = "+Utils.isNull(c.getSalesZone()) );
			    sql.append("\n  )");*/
				
				 sql.append("\n  and s"+schema_name+".code in(");
				 sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
				 sql.append("\n    where zone = "+Utils.isNull(c.getSalesZone()) );
				 sql.append("\n )");
				
				//sql.append("\n  and z"+schema_name+".salesrep_id = s"+schema_name+".salesrep_id ");
				//sql.append("\n  and z"+schema_name+".zone = "+Utils.isNull(c.getSalesZone()) );
			}
			
			if( !Utils.isNull(c.getCustomerType()).equals("")){
				if( Utils.isNull(c.getCustomerType()).equals("P")){
			        //sql.append("\n and ( cs"+schema_name+".customer_class_code IN('"+c.getCustomerType()+"','') or cs"+schema_name+".customer_class_code is null) ");
			        sql.append("\n and ( cs"+schema_name+".customer_class_code ='"+c.getCustomerType()+"' ");
			        sql.append("\n     or cs"+schema_name+".customer_class_code ='' ");
			        sql.append("\n     or cs"+schema_name+".customer_class_code is null ) ");
				}else{
				    sql.append("\n and cs"+schema_name+".customer_class_code ='"+c.getCustomerType()+"' ");
				}
			}else{
				//CustType
				 sql.append("\n and (  cs"+schema_name+".customer_class_code in('P','C','B','G','E','F','H','D') ");
			     sql.append("\n     or cs"+schema_name+".customer_class_code ='' ");
			     sql.append("\n     or cs"+schema_name+".customer_class_code is null ) ");
				
			}
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	
	
    public static StringBuffer searchCustNotEqualMstLocDetail(LocationBean c,boolean excel) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer result = new StringBuffer();
		int no = 0;
		int tripDay = 0;
		String width="100%";
		String salesTypePrefix ="";//
		String salesTypePrefixTo ="";//
		try {
			//set salesTypePrefix V=3,S=2
			if(Utils.isNull(c.getCustCatNo()).equals("S")){
			    salesTypePrefix = "S";
			    salesTypePrefixTo ="2";
			}else{
			    salesTypePrefix = "V";
			    salesTypePrefixTo ="3";
			}
			if(excel){
				result.append(ExcelHeader.EXCEL_HEADER);
			}
			/** init Connection **/
			 conn = DBConnection.getInstance().getConnectionApps();
			/** Gen TripDay from Date **/
			 tripDay = DateUtil.getDayOfDate(DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH));
			 
			 //Get Config LimitDistince
			 String maxDistanceStr = CConstants.getConstants(CConstants.SPIDER_REF_CODE, CConstants.MAX_DISTANCE).getValue();
			 int maxDistance = Utils.convertToInt(maxDistanceStr);//meter m.
			 logger.debug("maxDistance:"+maxDistance+" m");
			 
			 sql.append("\n select c_sub.account_number ,c_sub.party_name ,  ");
			 sql.append("\n cl_sub.latitude as mst_lat, cl_sub.longitude as mst_lng ,");
			 sql.append("\n tc_sub.latitude , tc_sub.longitude , tc_sub.distance");
			 sql.append("\n from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n apps.xxpens_ar_cust_sales_vs cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n xxpens_ar_customer_all_v c_sub , ");
			 sql.append("\n xxpens_om_trip_cust_loc cl_sub ");
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = "+c.getSalesrepCode()+"");
			 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd') ) = "+tripDay);
			 //filter sales_code
			 sql.append("\n and  tc_sub.sales_code  = cs_sub.code");
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 sql.append("\n and tc_sub.salesrep_id = cl_sub.salesrep_id  ");
			 
			 sql.append("\n and cs_sub.cust_account_id =  cl_sub.cust_account_id  "); 
			 
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			
			 sql.append("\n and (tc_sub.flag='N' or tc_sub.flag ='Y') ");
			// check_in.distance > config max distance the not equals master location
			 sql.append("\n and tc_sub.distance > "+maxDistance);
			 sql.append("\n and trunc(tc_sub.checkin_date) = to_date('"+c.getDay()+"','dd/mm/yyyy')"); 
		
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			
			 logger.debug("sql:"+sql.toString());
			 
			 ps = conn.prepareStatement(sql.toString());
			 rs = ps.executeQuery();
			 while(rs.next()){
				 no++;
				 /** Gen Header Table **/
				 if(no==1){
					 result.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
					 result.append("<tr> \n");
					 result.append(" <th nowrap >ลำดับ</th> \n");
					 result.append(" <th nowrap >รหัสร้านค้า</th> \n");
					 result.append(" <th nowrap >ชื่อร้านค้า</th> \n");
					 result.append(" <th >พิกัดหลัก Latitude</th> \n");
					 result.append(" <th >พิกัดหลัก Longtitude</th> \n");
					 result.append(" <th >พิกัดที่บันทึก Latitude</th> \n");
					 result.append(" <th >พิกัดที่บันทึก  Longtitude</th> \n");
					 result.append(" <th >ระยะห่างระหว่าง 2 พิกัด (กม.)</th> \n");
					 result.append("</tr> \n");
				 }
				 result.append("\n <tr>");
				 result.append("\n <td nowrap class='td_text'>"+no+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("account_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("party_name"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("mst_lat"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("mst_lng"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("latitude"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("longitude"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.convMeterToKilometer(rs.getString("distance"),2)+"</td>");
				 result.append("\n </tr>");
			 }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally { 
			conn.close();
			ps.close();
			rs.close();
		}
		return result;
	}
    
    public static StringBuffer searchCustDetail(LocationBean c,boolean excel) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer result = new StringBuffer();
		int no = 0;
		int tripDay = 0;
		String width="100%";
		String salesTypePrefix ="";//
		String salesTypePrefixTo ="";//
		String orderNoChkIn = "";
		try {
			//set salesTypePrefix V=3,S=2
			if(Utils.isNull(c.getCustCatNo()).equals("S")){
			    salesTypePrefix = "S";
			    salesTypePrefixTo ="2";
			}else{
			    salesTypePrefix = "V";
			    salesTypePrefixTo ="3";
			}
			if(excel){
				result.append(ExcelHeader.EXCEL_HEADER);
			}
			/** init Connection **/
			 conn = DBConnection.getInstance().getConnectionApps();
			/** Gen TripDay from Date **/
			 tripDay = DateUtil.getDayOfDate(DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH));
			 
			 //Get Config LimitDistince
			 String maxDistanceStr = CConstants.getConstants(CConstants.SPIDER_REF_CODE, CConstants.MAX_DISTANCE).getValue();
			 int maxDistance = Utils.convertToInt(maxDistanceStr);//meter m.
			 logger.debug("maxDistance:"+maxDistance+" m");
			 
			 sql.append("\n select A.* , ");
			 sql.append("\n (select o.creation_date from apps.xxpens_om_order_headers_temp o ");
			 sql.append("\n  where o.order_number = A.order_number_temp ) create_date_order");
			 sql.append("\n from( ");
			 sql.append("\n   select c_sub.account_number ,c_sub.party_name ,tc_sub.order_number,tc_sub.checkin_date ,");
			 sql.append("\n   cl_sub.latitude as mst_lat, cl_sub.longitude as mst_lng ,");
			 sql.append("\n   tc_sub.latitude , tc_sub.longitude , tc_sub.distance , tc_sub.file_name ,");
			 sql.append("\n   (case when order_number like '3%' THEN 'V'|| substr(order_number,2,length(order_number))");
			 sql.append("\n        else  'S'|| substr(order_number,2,length(order_number) )  end) as order_number_temp ");
			
			 sql.append("\n   from ");
			 sql.append("\n   apps.xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n   apps.xxpens_ar_cust_sales_vs cs_sub , ");
			 sql.append("\n   apps.xxpens_salesreps_v s_sub, ");
			 sql.append("\n   apps.xxpens_ar_customer_all_v c_sub , ");
			 sql.append("\n   apps.xxpens_om_trip_cust_loc cl_sub ");
			 sql.append("\n   where 1=1 "); 
			 sql.append("\n   and cs_sub.primary_salesrep_id = "+c.getSalesrepCode()+"");
			 sql.append("\n   and to_number(to_char(tc_sub.checkin_date,'dd') ) = "+tripDay);
			 //filter sales_code
			 sql.append("\n   and tc_sub.sales_code  = cs_sub.code");
			 
			 sql.append("\n   and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n   and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 sql.append("\n   and tc_sub.salesrep_id = cl_sub.salesrep_id  ");
			 
			//Case Customer change to new sale
			 //sql.append("\n and cs_sub.primary_salesrep_id = cl_sub.salesrep_id  ");
			 sql.append("\n   and cs_sub.cust_account_id =  cl_sub.cust_account_id  "); 
			 
			 sql.append("\n   and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n   and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n   and cs_sub.code = s_sub.code ");
			 
			 sql.append("\n   and (tc_sub.flag='N' or tc_sub.flag ='Y') ");
			 sql.append("\n and tc_sub.checkin_date between to_date('"+c.getDay()+"','dd/mm/yyyy')"); 
			 sql.append("\n    and TO_TIMESTAMP('"+c.getDay()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')"); 
		
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n   order by tc_sub.checkin_date");
			 sql.append("\n )A");
			 
			// logger.debug("sql:"+sql.toString());
			 if(logger.isDebugEnabled()){
				 FileUtil.writeFile("D://dev_temp//temp/sql.sql", sql.toString(),"TIS-620");
			 }//if 
			 ps = conn.prepareStatement(sql.toString());
			 rs = ps.executeQuery();
			 while(rs.next()){
				 //Set for get Customer from salesAnalysis not in
				 if(!Utils.isNull(rs.getString("order_number")).equals("")){
				    orderNoChkIn += "'"+rs.getString("order_number")+"',";
				 }
				 no++;
				 /** Gen Header Table **/
				 if(no==1){
					 result.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
					 result.append("<tr> \n");
					 result.append(" <th nowrap >ลำดับ</th> \n");
					 result.append(" <th nowrap >รหัสร้านค้า</th> \n");
					 result.append(" <th nowrap >ชื่อร้านค้า</th> \n");
					 result.append(" <th >พิกัดหลัก Latitude</th> \n");
					 result.append(" <th >พิกัดหลัก Longtitude</th> \n");
					 result.append(" <th >เลขที่เอกสาร</th> \n");
					 result.append(" <th >วันที่มาร์คจุด</th> \n");
					 result.append(" <th >วันเวลาที่บันทึก Sales App</th> \n");//
					 result.append(" <th >พิกัดที่บันทึก Latitude</th> \n");
					 result.append(" <th >พิกัดที่บันทึก  Longtitude</th> \n");
					 result.append(" <th >ระยะห่างระหว่าง 2 พิกัด (กม.)</th> \n");
					 result.append(" <th >File Name</th> \n");
					 result.append("</tr> \n");
				 }
				 result.append("\n <tr>");
				 result.append("\n <td nowrap class='td_text'>"+no+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("account_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("party_name"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("mst_lat"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("mst_lng"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+Utils.isNull(rs.getString("order_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+DateUtil.stringValueChkNull(rs.getTimestamp("checkin_date"),DateUtil.DD_MM_YYYY_HH_MM_WITH_SLASH,DateUtil.local_th)+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+DateUtil.stringValueChkNull(rs.getTimestamp("create_date_order"),DateUtil.DD_MM_YYYY_HH_MM_WITH_SLASH,DateUtil.local_th)+"</td>");
					
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("latitude"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("longitude"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.convMeterToKilometer(rs.getString("distance"),2)+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("file_name"))+"</td>");
				 result.append("\n </tr>");
			 }
			
			 if(orderNoChkIn.length()>0){
				 orderNoChkIn = orderNoChkIn.substring(0,orderNoChkIn.length()-1);
			 }
			 //Get customer from SalesAnalysis not exist CheckIn
			 result = searchCustDetailFromSalesAnalysis(conn, c, result, orderNoChkIn, excel,no);
			 
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally { 
			conn.close();
			ps.close();
			rs.close();
		}
		return result;
	}
    public static StringBuffer searchCustDetailFromSalesAnalysis(Connection conn,LocationBean c,StringBuffer result,String orderNoChkIn,boolean excel,int no) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		int tripDay = 0;
		String width="100%";
		try {
			
			/** Gen TripDay from Date **/
			 tripDay = DateUtil.getDayOfDate(DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH));
			
			 sql.append("\n select distinct A.* ,sales_order_date as create_date_order");
			 sql.append("\n from( ");
			 sql.append("\n   select  c_sub.account_number ,c_sub.party_name ,tc_sub.sales_order_no as order_number");
			 sql.append("\n   , null as checkin_date ,tc_sub.sales_order_date,");
			 sql.append("\n   0 as mst_lat, 0 as mst_lng ,");
			 sql.append("\n   0 as latitude , 0 as longitude , 0 as distance , '' as file_name ,");
			 sql.append("\n   '' as order_number_temp ");
			 sql.append("\n   from ");
			 sql.append("\n   apps.XXPENS_BI_SALES_ANALYSIS_V tc_sub , ");
			 sql.append("\n   apps.xxpens_ar_cust_sales_vs cs_sub , ");
			 sql.append("\n   apps.xxpens_salesreps_v s_sub, ");
			 sql.append("\n   apps.xxpens_ar_customer_all_v c_sub  ");
			// sql.append("\n   ,apps.xxpens_om_trip_cust_loc cl_sub ");
			 sql.append("\n   where 1=1 "); 
			 sql.append("\n   and cs_sub.primary_salesrep_id = "+c.getSalesrepCode()+"");
			 sql.append("\n   and to_number(to_char(tc_sub.sales_order_date,'dd') ) = "+tripDay);
			 //filter sales_code
			 sql.append("\n   and tc_sub.salesrep_id  = cs_sub.primary_salesrep_id");
			 
			 sql.append("\n   and tc_sub.customer_id = c_sub.cust_account_id ");
			 sql.append("\n   and tc_sub.customer_id = cs_sub.cust_account_id ");
			 
			 //sql.append("\n   and tc_sub.salesrep_id = cl_sub.salesrep_id  ");
			 //sql.append("\n   and cs_sub.cust_account_id =  cl_sub.cust_account_id  "); 
			 
			 sql.append("\n   and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n   and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n   and cs_sub.code = s_sub.code ");
			 sql.append("\n   and tc_sub.sales_order_date between to_date('"+c.getDay()+"','dd/mm/yyyy')"); 
			 sql.append("\n      and TO_TIMESTAMP('"+c.getDay()+" 23:59:59','dd/mm/yyyy HH24:MI:SS')"); 
			 //CustomerCode not in check in
			 if(!Utils.isNull(orderNoChkIn).equals("")){
			    sql.append("\n  and tc_sub.sales_order_no not in("+orderNoChkIn+") ");
			 }
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n   order by tc_sub.sales_order_date");
			 sql.append("\n )A");
			 
			// logger.debug("sql:"+sql.toString());
			 if(logger.isDebugEnabled()){
				 FileUtil.writeFile("D://dev_temp//temp/sql2.sql", sql.toString(),"TIS-620");
			 }//if
			 ps = conn.prepareStatement(sql.toString());
			 rs = ps.executeQuery();
			 while(rs.next()){
				 no++;
				 /** Gen Header Table **/
				 if(no==1){
					 result.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
					 result.append("<tr> \n");
					 result.append(" <th nowrap >ลำดับ</th> \n");
					 result.append(" <th nowrap >รหัสร้านค้า</th> \n");
					 result.append(" <th nowrap >ชื่อร้านค้า</th> \n");
					 result.append(" <th >พิกัดหลัก Latitude</th> \n");
					 result.append(" <th >พิกัดหลัก Longtitude</th> \n");
					 result.append(" <th >เลขที่เอกสาร</th> \n");
					 result.append(" <th >วันที่มาร์คจุด</th> \n");
					 result.append(" <th >วันเวลาที่บันทึก Sales App</th> \n");//
					 result.append(" <th >พิกัดที่บันทึก Latitude</th> \n");
					 result.append(" <th >พิกัดที่บันทึก  Longtitude</th> \n");
					 result.append(" <th >ระยะห่างระหว่าง 2 พิกัด (กม.)</th> \n");
					 result.append(" <th >File Name</th> \n");
					 result.append("</tr> \n");
				 }
				 result.append("\n <tr>");
				 result.append("\n <td nowrap class='td_text'>"+no+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("account_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("party_name"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("mst_lat"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("mst_lng"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+Utils.isNull(rs.getString("order_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+DateUtil.stringValueChkNull(rs.getTimestamp("checkin_date"),DateUtil.DD_MM_YYYY_HH_MM_WITH_SLASH,DateUtil.local_th)+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+DateUtil.stringValueChkNull(rs.getTimestamp("create_date_order"),DateUtil.DD_MM_YYYY_HH_MM_WITH_SLASH,DateUtil.local_th)+"</td>");
					
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("latitude"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("longitude"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.convMeterToKilometer(rs.getString("distance"),2)+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("file_name"))+"</td>");
				 result.append("\n </tr>");
			 }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally { 
			conn.close();
			ps.close();
			rs.close();
		}
		return result;
	}
    public static StringBuffer searchCustNoEqualsTrip(LocationBean c,boolean excel) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer result = new StringBuffer();
		int no = 0;
		int tripDay = 0;
		String width="100%";
		String salesTypePrefix ="";//
		String salesTypePrefixTo ="";//
		try {
			//set salesTypePrefix V=3,S=2
			if(Utils.isNull(c.getCustCatNo()).equals("S")){
			    salesTypePrefix = "S";
			    salesTypePrefixTo ="2";
			}else{
			    salesTypePrefix = "V";
			    salesTypePrefixTo ="3";
			}
			
			if(excel){
				result.append(ExcelHeader.EXCEL_HEADER);
			}
			/** init Connection **/
			 conn = DBConnection.getInstance().getConnectionApps();
			/** Gen TripDay from Date **/
			 tripDay = DateUtil.getDayOfDate(DateUtil.parse(c.getDay(), DateUtil.DD_MM_YYYY_WITH_SLASH));
			 
			 //Get Config LimitDistince
			 String maxDistanceStr = CConstants.getConstants(CConstants.SPIDER_REF_CODE, CConstants.MAX_DISTANCE).getValue();
			 int maxDistance = Utils.convertToInt(maxDistanceStr);//meter m.
			 logger.debug("maxDistance:"+maxDistance+" m");
			 
			 sql.append("\n select A.* , ");
			 sql.append("\n (select o.creation_date from apps.xxpens_om_order_headers_temp o ");
			 sql.append("\n  where o.order_number = A.order_number_temp ) create_date_order");
			 sql.append("\n from( ");
			 sql.append("\n   select c_sub.account_number ,c_sub.party_name ,tc_sub.order_number,tc_sub.checkin_date ,");
			 sql.append("\n   tc_sub.latitude , tc_sub.longitude , tc_sub.distance");
			 sql.append("\n  ,( ");
			 sql.append("\n     cs_sub.trip1 || decode(cs_sub.trip2, null,null ,','||cs_sub.trip2) || decode(cs_sub.trip3, null,null ,','||cs_sub.trip3) ");
			 sql.append("\n   )as trip ");
			 sql.append("\n   ,(case when order_number like '3%' THEN 'V'|| substr(order_number,2,length(order_number))");
			 sql.append("\n        else  'S'|| substr(order_number,2,length(order_number) )  end) as order_number_temp ");
			
			 sql.append("\n   from ");
			 sql.append("\n   xxpens_om_trip_checkin tc_sub ,");
			 sql.append("\n   apps.xxpens_ar_cust_sales_vs cs_sub ,");
			 sql.append("\n   xxpens_salesreps_v s_sub,");
			 sql.append("\n   xxpens_ar_customer_all_v c_sub");
			 sql.append("\n   where 1=1 "); 
			 sql.append("\n   and cs_sub.primary_salesrep_id = "+c.getSalesrepCode()+"");
			//filter sales_code
			 sql.append("\n   and tc_sub.sales_code  = cs_sub.code");
			 
			 sql.append("\n   and nvl(cs_sub.trip1,0) <> "+tripDay);
			 sql.append("\n   and nvl(cs_sub.trip2,0) <> "+tripDay);
			 sql.append("\n   and nvl(cs_sub.trip3,0) <> "+tripDay);
			 
			 sql.append("\n   and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n   and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 
			 sql.append("\n   and cs_sub.code = s_sub.code ");
			 sql.append("\n   and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n   and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 
			 sql.append("\n   and (tc_sub.flag='N' or tc_sub.flag ='Y') ");
			 sql.append("\n   and trunc(tc_sub.checkin_date) = to_date('"+c.getDay()+"','dd/mm/yyyy')"); 
		
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )A");
			// logger.debug("sql:"+sql.toString());
			 
			 ps = conn.prepareStatement(sql.toString());
			 rs = ps.executeQuery();
			 while(rs.next()){
				 no++;
				 /** Gen Header Table **/
				 if(no==1){
					 result.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
					 result.append("<tr> \n");
					 result.append(" <th nowrap >ลำดับ</th> \n");
					 result.append(" <th nowrap >รหัสร้านค้า</th> \n");
					 result.append(" <th nowrap >ชื่อร้านค้า</th> \n");
					 result.append(" <th >เลขที่เอกสาร</th> \n");
					 result.append(" <th >วันที่มาร์คจุด</th> \n");
					 result.append(" <th >วันเวลาที่บันทึก Sales App</th> \n");
					 result.append(" <th >Trip</th> \n");
					 result.append("</tr> \n");
				 }
				 result.append("\n <tr>");
				 result.append("\n <td nowrap class='td_text'>"+no+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("account_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("party_name"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+Utils.isNull(rs.getString("order_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+DateUtil.stringValueChkNull(rs.getTimestamp("checkin_date"),DateUtil.DD_MM_YYYY_HH_MM_WITH_SLASH,DateUtil.local_th)+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+DateUtil.stringValueChkNull(rs.getTimestamp("create_date_order"),DateUtil.DD_MM_YYYY_HH_MM_WITH_SLASH,DateUtil.local_th)+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("trip"))+"</td>");
				 result.append("\n </tr>");
			 }
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally { 
			conn.close();
			ps.close();
			rs.close();
		}
		return result;
	}
}
