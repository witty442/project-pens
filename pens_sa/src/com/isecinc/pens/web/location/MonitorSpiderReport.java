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

import sun.misc.Cleaner;
import util.CConstants;
import util.DBConnection;
import util.DateToolsUtil;
import util.ExcelHeader;
import util.Utils;

public class MonitorSpiderReport {
	private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuffer searchMonitorReport(String path,LocationBean c,boolean excel,Map<String, CConstantsBean> constantsMap) throws Exception {
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
			String linkJavaScript = "";
			String bgcolor= "";
			int MinVisitByReal = 0;int MaxNotEqualsMasloc=0;
			String MinVisitByRealColor = "";String MaxNotEqualsMaslocColor="";
			int MaxNotEqualsTrip =0;int MinVisitByTrip=0;
			String MaxNotEqualsTripColor ="";String MinVisitByTripColor="";
			String prefix_="Credit";
			int count_cust_all_by_trip = 0;
			int count_visit_cust_by_trip = 0;
			int count_sale_cust_by_trip= 0;
			int count_visit_cust_by_real= 0;
			int count_sale_cust_by_real= 0;
			int count_cust_not_equals_trip= 0;
			int count_cust_not_equal_masloc= 0;
			try {
				conn = DBConnection.getInstance().getConnectionApps();

				if("C".equalsIgnoreCase(c.getCustCatNo())){
					prefix_="Van";
				}
				CConstantsBean MinVisitByRealObj = constantsMap.get(prefix_+CConstants.MIN_VISIT_BY_REAL);
				MinVisitByReal  = Utils.convertToInt(MinVisitByRealObj.getValue());
				MinVisitByRealColor = MinVisitByRealObj.getValue2();
				
				CConstantsBean MaxNotEqualsMaslocObj = constantsMap.get(prefix_+CConstants.MAX_NOT_EQUALS_MASLOC);
				MaxNotEqualsMasloc  = Utils.convertToInt(MaxNotEqualsMaslocObj.getValue());
				MaxNotEqualsMaslocColor = MaxNotEqualsMaslocObj.getValue2();
				
				CConstantsBean MaxNotEqualsTripObj = constantsMap.get(prefix_+CConstants.MAX_NOT_EQUALS_TRIP);
				MaxNotEqualsTrip  = Utils.convertToInt(MaxNotEqualsTripObj.getValue());
				MaxNotEqualsTripColor = MaxNotEqualsTripObj.getValue2();
				
				CConstantsBean MinVisitByTripObj = constantsMap.get(prefix_+CConstants.MIN_VISIT_BY_TRIP);
			    MinVisitByTrip  = Utils.convertToInt(MinVisitByTripObj.getValue());
				MinVisitByTripColor = MinVisitByTripObj.getValue2();
				
				//Get Month and Year to ChristDate 2018
				String mm = c.getDay().substring(3,5);
				String yyyy = String.valueOf(Integer.parseInt(c.getDay().substring(6,10))-543);
				String mmyyyy = "/"+mm+"/"+yyyy;
				logger.debug("mmyyyy:"+mmyyyy);
				
				String yyyyB = String.valueOf(Integer.parseInt(c.getDay().substring(6,10)));
				String mmyyyyB = "/"+mm+"/"+yyyyB;
				logger.debug("mmyyyyB:"+mmyyyyB);
				
				sql = genSqlMonitorReport(conn, c,mmyyyy);
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

					tripDate = genTripDate(Utils.isNull(rst.getString("trip_day")),mmyyyy);
					tripDateShow  =genTripDate(Utils.isNull(rst.getString("trip_day")),mmyyyyB);
					
					bgcolor ="";
					//check count style
					//1
				    if(rst.getInt("count_visit_cust_by_real") < MinVisitByReal){
				    	bgcolor=MinVisitByRealColor;
				    //2
				    }else if(rst.getInt("count_cust_not_equal_masloc") > MaxNotEqualsMasloc){
			        	bgcolor=MaxNotEqualsMaslocColor;
					//3
			        }else  if(rst.getInt("count_cust_not_equals_trip") > MaxNotEqualsTrip){
			        	bgcolor=MaxNotEqualsTripColor;
					//4
			        }else if(rst.getInt("count_visit_cust_by_trip") < MinVisitByTrip){
						bgcolor= MinVisitByTripColor;
			        }
				    
					html.append("<tr bgcolor='"+bgcolor+"'>");
					html.append("<td class='"+tdTextClass+"' width='5%'>"+Utils.isNull(rst.getString("sales_code"))+"</td>");
					html.append("<td class='"+tdTextClass+"' width='10%'>"+Utils.isNull(rst.getString("salesrep_name"))+"</td>");
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+tripDateShow+"</td>");
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("count_cust_all_by_trip"))+"</td>");
					count_cust_all_by_trip +=rst.getInt("count_cust_all_by_trip");
							
					html.append("<td class='"+tdTextCenterClass+"'  width='5%'>");
					html.append(  Utils.isNull(rst.getString("count_visit_cust_by_trip")) );
					   count_visit_cust_by_trip += rst.getInt("count_visit_cust_by_trip");
					html.append("</td>");
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("count_sale_cust_by_trip"))+"</td>");
					   count_sale_cust_by_trip += rst.getInt("count_sale_cust_by_trip");
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");
					html.append(Utils.isNull(rst.getString("count_visit_cust_by_real")) );
					   count_visit_cust_by_real  +=rst.getInt("count_visit_cust_by_real");
					html.append("</td>");
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>"+Utils.isNull(rst.getString("count_sale_cust_by_real"))+"</td>");
					  count_sale_cust_by_real += rst.getInt("count_sale_cust_by_real");
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");
					html.append(Utils.isNull(rst.getString("count_cust_not_equals_trip")));
					  count_cust_not_equals_trip += rst.getInt("count_cust_not_equals_trip");
					html.append("</td>");
					/***********************************************************/
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");//View Cust No equals Trip
					  linkJavaScript  = "viewDetail('CustNotEqualTrip'";
					  linkJavaScript += ",'"+Utils.isNull(rst.getString("primary_salesrep_id"))+"'";
					  linkJavaScript += ",'"+tripDate+"')";
					html.append("<a class='link_style' href=javascript:"+linkJavaScript+">View </a></td>");
					/*************************************************************/
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");
					html.append(Utils.isNull(rst.getString("count_cust_not_equal_masloc")));
					  count_cust_not_equal_masloc += rst.getInt("count_cust_not_equal_masloc");
					html.append("</td>");
					/***********************************************************/
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");//View Cust No equals Trip
					  linkJavaScript  = "viewDetail('CustNotEqualMasLocation'";
					  linkJavaScript += ",'"+Utils.isNull(rst.getString("primary_salesrep_id"))+"'";
					  linkJavaScript += ",'"+tripDate+"')";
					html.append("<a class='link_style' href=javascript:"+linkJavaScript+"> View </a></td>");
					/*************************************************************/
					html.append("<td class='"+tdTextCenterClass+"' width='5%'>");//View Detail
					  linkJavaScript  = "viewDetail('CustDetail'";
					  linkJavaScript += ",'"+Utils.isNull(rst.getString("primary_salesrep_id"))+"'";
					  linkJavaScript += ",'"+tripDate+"')";
					html.append("<a class='link_style' href=javascript:"+linkJavaScript+"> View </a></td>");
					/*************************************************************/
					
				  html.append("</tr>");
				}//while
				
				//Gen Row Summary
				html.append("<tr class='row_hilight'>");
				html.append("<td  width='20%' colspan='3'>���</td>");
				html.append("<td  width='5%'>");
				html.append(    Utils.decimalFormat(count_cust_all_by_trip,Utils.format_current_no_disgit));
				html.append("</td>");
				html.append("<td  width='5%'>");
				html.append(    Utils.decimalFormat(count_visit_cust_by_trip,Utils.format_current_no_disgit));
				html.append("</td>");
				html.append("<td  width='5%'>");
				html.append(    Utils.decimalFormat(count_sale_cust_by_trip,Utils.format_current_no_disgit) );
				html.append("</td>");
				html.append("<td  width='5%'>");
				html.append(    Utils.decimalFormat(count_visit_cust_by_real,Utils.format_current_no_disgit));
				html.append("</td>");
				html.append("<td  width='5%'>");
				html.append(    Utils.decimalFormat(count_sale_cust_by_real,Utils.format_current_no_disgit));
				html.append("</td>");
				html.append("<td  width='5%'>");
				html.append(    Utils.decimalFormat(count_cust_not_equals_trip,Utils.format_current_no_disgit));
				html.append("</td>");
				/***********************************************************/
				html.append("<td  width='5%'>");//View Cust No equals Trip
				html.append("</td>");
				/*************************************************************/
				html.append("<td  width='5%'>");
				html.append(  Utils.decimalFormat(count_cust_not_equal_masloc,Utils.format_current_no_disgit));
				html.append("</td>");
				/***********************************************************/
				html.append("<td  width='5%'>");//View Cust No equals Trip
				html.append("</td>");
				/*************************************************************/
				html.append("<td width='5%'>");//View Detail
				html.append("</td>");
				/*************************************************************/
				html.append("</tr>");
				
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
	
	private static String genTripDate(String tripDay,String mmyyyy){
		return tripDay.length()==1?("0"+tripDay+mmyyyy):tripDay+mmyyyy;
	}
	 
	 public static StringBuilder genSqlMonitorReport(Connection conn,LocationBean c,String mmyyyy) throws Exception {
			StringBuilder sql = new StringBuilder();
			String startDateStr = "";
			String endDateStr = "";
			String salesTypePrefix ="";//
			String salesTypePrefixTo ="";//
			try {
				//Get TripDay
				Calendar cal = Calendar.getInstance();
				Date startDate = Utils.parse(c.getDay(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				startDateStr = Utils.stringValue(startDate, Utils.DD_MM_YYYY_WITH_SLASH);
				c.setStartDate(startDateStr);
				cal.setTime(startDate);
				int startTrip = cal.get(Calendar.DAY_OF_MONTH);
				
				if( !Utils.isNull(c.getDayTo()).equals("")){
					Date endDate = Utils.parse(c.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					endDateStr = Utils.stringValue(endDate, Utils.DD_MM_YYYY_WITH_SLASH);
					c.setEndDate(endDateStr);
				}
				//set salesTypePrefix V=3,S=2
				if(Utils.isNull(c.getCustCatNo()).equals("S")){
				   salesTypePrefix = "S";
				   salesTypePrefixTo ="2";
				}else{
				  salesTypePrefix = "V";
			      salesTypePrefixTo ="3";
				}
				sql.append("\n select A.* FROM ( ");
				sql.append("\n select cs.code as sales_code,cs.primary_salesrep_id");
				sql.append("\n ,cs.salesrep_name,cs.trip_day ,to_number(cs.trip_day) as trip_day_num  ");
                
				//count_cust_all_by_trip
				sql.append(""+genCountCustAllByTrip(conn, c));
				
				//count_visit_cust_by_trip
				sql.append(""+genCountVisitCustByTrip(conn, c,salesTypePrefix,salesTypePrefixTo));
				
				//count_sale_cust_by_trip
				sql.append(""+genCountSaleCustByTrip(conn, c,salesTypePrefix,salesTypePrefixTo));
				
				//count_visit_cust_by_real
				sql.append(""+genCountVisitCustByReal(conn, c,salesTypePrefix,salesTypePrefixTo));
				
				//count_sale_cust_by_real
				sql.append(""+genCountSaleCustByReal(conn, c,salesTypePrefix,salesTypePrefixTo));
				
				//count_cust_not_equals_trip
				sql.append(""+genCountCustNotEqualsTrip(conn, c,mmyyyy,salesTypePrefix,salesTypePrefixTo));
				
				//count_cust_not_equal_masloc
				sql.append(""+genCountCustNotEqualsLocationMaster(conn, c,salesTypePrefix,salesTypePrefixTo));
				
				sql.append("\n from  ");
				sql.append("\n ( ");
				sql.append("\n  select M.* ");
				sql.append("\n  ,to_date(M.tripday_str||'"+mmyyyy+"','dd/mm/yyyy') as trip_date ");
				sql.append("\n  from( ");
				//Trip1
				sql.append("\n  select distinct cs.code ,s.salesrep_name,cs.primary_salesrep_id,cs.trip1 as trip_day ");
				sql.append("\n ,(case when length(cs.trip1)=1 then '0'||cs.trip1 else cs.trip1 end) as tripday_str ");
				
				sql.append("\n  from xxpens_ar_cust_sales_all cs , ");
				sql.append("\n  xxpens_salesreps_v s, ");
				sql.append("\n  xxpens_ar_customer_all_v c ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n  and cs.code = s.code ");
				sql.append("\n  and cs.cust_account_id =  c.cust_account_id ");
				//genWhereCondSql
				sql.append(     genWhereCondSql(conn, "", c));
				//genDay trip1
				if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					 Date endDate = Utils.parse(c.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					 cal.setTime(endDate);
					 int endTrip = cal.get(Calendar.DAY_OF_MONTH);
					  
					 sql.append("\n and (nvl(cs.trip1,0) >= "+startTrip +" and nvl(cs.trip1,0) <= "+endTrip +")");
				}else{
					 sql.append("\n and nvl(cs.trip1,0) = "+startTrip);
				}//if
				sql.append("\n UNION ");
				//Trip2
				sql.append("\n  select distinct cs.code,s.salesrep_name,cs.primary_salesrep_id ,cs.trip2 as trip_day  ");
				sql.append("\n ,(case when length(cs.trip2)=1 then '0'||cs.trip2 else cs.trip2 end) as tripday_str ");
				sql.append("\n  from xxpens_ar_cust_sales_all cs ,   ");
				sql.append("\n  xxpens_salesreps_v s ,");
				sql.append("\n  xxpens_ar_customer_all_v c ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n  and cs.code = s.code ");
				sql.append("\n  and cs.cust_account_id =  c.cust_account_id ");
				//genWhereCondSql
				sql.append(     genWhereCondSql(conn, "", c));
				//genDay trip1
				if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					 Date endDate = Utils.parse(c.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					 cal.setTime(endDate);
					 int endTrip = cal.get(Calendar.DAY_OF_MONTH);
					  
					 sql.append("\n and (nvl(cs.trip2,0) >= "+startTrip +" and nvl(cs.trip2,0) <= "+endTrip +")");
				}else{
					 sql.append("\n and nvl(cs.trip2,0) = "+startTrip);
				}//if
				sql.append("\n UNION ");
				//trip3
				sql.append("\n  select distinct cs.code,s.salesrep_name,cs.primary_salesrep_id ,cs.trip3 as trip_day  ");
				sql.append("\n ,(case when length(cs.trip3)=1 then '0'||cs.trip3 else cs.trip3 end) as tripday_str ");
				sql.append("\n  from xxpens_ar_cust_sales_all cs ,   ");
				sql.append("\n  xxpens_salesreps_v s, ");
				sql.append("\n  xxpens_ar_customer_all_v c ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n  and cs.code = s.code ");
				sql.append("\n  and cs.cust_account_id =  c.cust_account_id ");
				//genWhereCondSql
				sql.append(     genWhereCondSql(conn, "", c));
				//genDay trip1
				if(!Utils.isNull(c.getDay()).equals("")  && !Utils.isNull(c.getDayTo()).equals("")){
					 Date endDate = Utils.parse(c.getDayTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					 cal.setTime(endDate);
					 int endTrip = cal.get(Calendar.DAY_OF_MONTH);
					  
					 sql.append("\n and (nvl(cs.trip3,0) >= "+startTrip +" and nvl(cs.trip3,0) <= "+endTrip +")");
				}else{
					 sql.append("\n and nvl(cs.trip3,0) = "+startTrip);
				}//if
				sql.append("\n UNION ");
				//Case No set Trip
				sql.append("\n  select distinct cs.code,s.salesrep_name,cs.primary_salesrep_id ,'"+startTrip+"' as trip_day  ");
				sql.append("\n ,(case when length('"+startTrip+"')=1 then '0'||'"+startTrip+"' else '"+startTrip+"' end) as tripday_str ");
				sql.append("\n  from xxpens_ar_cust_sales_all cs ,   ");
				sql.append("\n  xxpens_salesreps_v s, ");
				sql.append("\n  xxpens_ar_customer_all_v c ");
				sql.append("\n  where 1=1 ");
				sql.append("\n  and cs.primary_salesrep_id = s.salesrep_id ");
				sql.append("\n  and cs.code = s.code ");
				sql.append("\n  and cs.cust_account_id =  c.cust_account_id ");
				//genWhereCondSql
				sql.append(     genWhereCondSql(conn, "", c));
				sql.append("\n   )M ");
				sql.append("\n  )cs ");
				sql.append("\n )A ");
				sql.append("\n  ORDER BY A.sales_code ,A.trip_day_num ");

			} catch (Exception e) {
				throw e;
			} finally { 
				
			}
			return sql;
	}
	 
	 private static StringBuffer genCountCustAllByTrip(Connection conn,LocationBean c){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(distinct cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n xxpens_ar_customer_all_v c_sub   ");
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
			 sql.append("\n and (cs_sub.trip1=cs.trip_day  ");
			 sql.append("\n      or (cs_sub.trip2=cs.trip_day and cs_sub.trip2 is not null )  ");
			 sql.append("\n      or (cs_sub.trip3=cs.trip_day and cs_sub.trip3 is not null ) ");  
			 sql.append("\n     ) ");
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n ) as count_cust_all_by_trip ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 private static StringBuffer genCountVisitCustByTrip(Connection conn,LocationBean c,String salesTypePrefix,String salesTypePrefixTo){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(distinct cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n xxpens_ar_customer_all_v c_sub,  ");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub  ");
			 sql.append("\n left outer join ");
				sql.append("\n ( ");
				sql.append("\n select tc.* from xxpens_om_trip_checkin tc where 1=1");
				sql.append("\n and (tc.flag ='N' or tc.flag='Y') ");
				if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
				   sql.append("\n and trunc(tc.checkin_date) >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
				   sql.append("\n and trunc(tc.checkin_date) <= to_date('"+c.getEndDate()+"','dd/mm/yyyy')");
				}else{
				   sql.append("\n and trunc(tc.checkin_date) = to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
				}
			 sql.append("\n ) tc_sub  on tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 //filter sales_code
			 sql.append("\n      and tc_sub.sales_code  = cs_sub.code");
			 
			/* sql.append("\n and (   (nvl(cs_sub.trip1,0)=to_number(to_char(tc_sub.checkin_date,'dd'))) ");
			 sql.append("\n     or (nvl(cs_sub.trip2,0)=to_number(to_char(tc_sub.checkin_date,'dd'))) "); 
			 sql.append("\n     or (nvl(cs_sub.trip3,0)=to_number(to_char(tc_sub.checkin_date,'dd'))) ");
			 sql.append("\n ) ");*/
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
			 sql.append("\n and cs_sub.code = cs.code ");
			 sql.append("\n and (nvl(cs_sub.trip1,0)=cs.trip_day  ");
			 sql.append("\n      or (nvl(cs_sub.trip2,0)=cs.trip_day )  ");
			 sql.append("\n      or (nvl(cs_sub.trip3,0)=cs.trip_day ) ");  
			 sql.append("\n     ) ");
			 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd'))  =cs.trip_day");
			 //sql.append("\n and trunc(tc_sub.checkin_date)  =cs.trip_date");
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 
			//Case change customer to new Sales no join by sale
			// sql.append("\n and tc_sub.salesrep_id = cs_sub.primary_salesrep_id ");
			// sql.append("\n and tc_sub.sales_code = cs_sub.code ");
			 
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 
			 //where cond sql
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 
			 sql.append("\n )as count_visit_cust_by_trip ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 
	 private static StringBuffer genCountSaleCustByTrip(Connection conn,LocationBean c,String salesTypePrefix,String salesTypePrefixTo){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(distinct cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_salesreps_v s_sub,");
			 sql.append("\n xxpens_ar_customer_all_v c_sub, ");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub ");
			 sql.append("\n left outer join ");
				sql.append("\n ( ");
				sql.append("\n select tc.* from xxpens_om_trip_checkin tc where 1=1");
				sql.append("\n and (tc.flag='Y') ");
				if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
				   sql.append("\n and trunc(tc.checkin_date) >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
				   sql.append("\n and trunc(tc.checkin_date) <= to_date('"+c.getEndDate()+"','dd/mm/yyyy')");
				}else{
				   sql.append("\n and trunc(tc.checkin_date) = to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
				}
			 sql.append("\n ) tc_sub  on tc_sub.cust_account_id = cs_sub.cust_account_id ");
			//filter sales_code
			 sql.append("\n      and tc_sub.sales_code  = cs_sub.code");
			 
			 //OLD code
			/* sql.append("\n and (  (nvl(cs_sub.trip1,0)=to_number(to_char(tc_sub.checkin_date,'dd'))) ");
			 sql.append("\n     or (nvl(cs_sub.trip2,0)=to_number(to_char(tc_sub.checkin_date,'dd'))) "); 
			 sql.append("\n     or (nvl(cs_sub.trip3,0)=to_number(to_char(tc_sub.checkin_date,'dd'))) ");
			 sql.append("\n ) ");*/
			 
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
			 sql.append("\n and cs_sub.code = cs.code ");
			 sql.append("\n and (   nvl(cs_sub.trip1,0)=cs.trip_day  ");
			 sql.append("\n      or (nvl(cs_sub.trip2,0)=cs.trip_day )  ");
			 sql.append("\n      or (nvl(cs_sub.trip3,0)=cs.trip_day ) ");  
			 sql.append("\n     ) ");
			 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd')) =cs.trip_day");
			 //sql.append("\n and trunc(tc_sub.checkin_date)  =cs.trip_date");
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 
			//Case change customer to new Sales no join by sale
			// sql.append("\n and tc_sub.salesrep_id = cs_sub.primary_salesrep_id ");
			// sql.append("\n and tc_sub.sales_code = cs_sub.code ");
			
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 
			 //where cond sql
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_sale_cust_by_trip ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 private static StringBuffer genCountVisitCustByReal(Connection conn,LocationBean c,String salesTypePrefix,String salesTypePrefixTo){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n xxpens_ar_customer_all_v c_sub   ");
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
			 sql.append("\n and cs_sub.code = cs.code ");
			 //filter sales_code
			 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
			 
			 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd') ) = cs.trip_day ");
			 //sql.append("\n and trunc(tc_sub.checkin_date) = cs.trip_date ");
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 
			//Case change customer to new Sales no join by sale
			 //sql.append("\n and tc_sub.salesrep_id = cs_sub.primary_salesrep_id ");
			 //sql.append("\n and tc_sub.sales_code = cs_sub.code ");
			 
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 
			 sql.append("\n and (tc_sub.flag ='N' or tc_sub.flag='Y') ");
			 if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
			    sql.append("\n and trunc(tc_sub.checkin_date) >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
			    sql.append("\n and trunc(tc_sub.checkin_date) <= to_date('"+c.getEndDate()+"','dd/mm/yyyy')");
			 }else{
				sql.append("\n and trunc(tc_sub.checkin_date) = to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
			 }
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_visit_cust_by_real ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 private static StringBuffer genCountSaleCustByReal(Connection conn,LocationBean c,String salesTypePrefix,String salesTypePrefixTo){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n select count(cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n xxpens_ar_customer_all_v c_sub   ");
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
			 sql.append("\n and cs_sub.code = cs.code ");
			 //filter sales_code
			 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
			 
			 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd') ) = cs.trip_day ");
			// sql.append("\n and trunc(tc_sub.checkin_date) = cs.trip_date ");
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			
			 //Case change customer to new Sales no join by sale
			 //sql.append("\n and tc_sub.salesrep_id = cs_sub.primary_salesrep_id ");
			// sql.append("\n and tc_sub.sales_code = cs_sub.code ");
			 
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 
			 sql.append("\n and (tc_sub.flag='Y') ");
			 if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
			    sql.append("\n and trunc(tc_sub.checkin_date) >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
			    sql.append("\n and trunc(tc_sub.checkin_date) <= to_date('"+c.getEndDate()+"','dd/mm/yyyy')");
			 }else{
				sql.append("\n and trunc(tc_sub.checkin_date) = to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
			 }
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_sale_cust_by_real ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 private static StringBuffer genCountCustNotEqualsTrip(Connection conn,LocationBean c,String mmyyyy,String salesTypePrefix,String salesTypePrefixTo){
		 StringBuffer sql = new StringBuffer();
		 try {
			 //thai format date
			 mmyyyy = mmyyyy.replaceAll("\\/", "");
			 //convert yyyy thai to end date
			 mmyyyy = mmyyyy.substring(0,2)+String.valueOf(Integer.parseInt(mmyyyy.substring(2,6))-543);
			 
			 sql.append("\n ,( ");
			 sql.append("\n select count(cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub,");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n xxpens_ar_customer_all_v c_sub   ");
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
			 sql.append("\n and cs_sub.code = cs.code ");
			 sql.append("\n and trunc(tc_sub.checkin_date) = cs.trip_date"); 
			 sql.append("\n and nvl(cs_sub.trip1,0) <> cs.trip_day ");
			 sql.append("\n and nvl(cs_sub.trip2,0) <> cs.trip_day ");
			 sql.append("\n and nvl(cs_sub.trip3,0) <> cs.trip_day ");  
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 
			//filter sales_code
			 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
			 
			//Case change customer to new Sales no join by sale
			 //sql.append("\n and tc_sub.salesrep_id = cs_sub.primary_salesrep_id ");
			 //sql.append("\n and tc_sub.sales_code = cs_sub.code ");
			 
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 
			 if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
			    sql.append("\n and trunc(tc_sub.checkin_date) >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
			    sql.append("\n and trunc(tc_sub.checkin_date) <= to_date('"+c.getEndDate()+"','dd/mm/yyyy')");
			 }else{
				sql.append("\n and trunc(tc_sub.checkin_date) = to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
			 }
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_cust_not_equals_trip ");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return sql;
	 }
	 //count_cust_not_equal_masloc
	 private static StringBuffer genCountCustNotEqualsLocationMaster(Connection conn,LocationBean c,String salesTypePrefix,String salesTypePrefixTo){
		 StringBuffer sql = new StringBuffer();
		 try {
			 //Get Config LimitDistince
			 String maxDistanceStr = CConstants.getConstants(CConstants.SPIDER_REF_CODE, CConstants.MAX_DISTANCE).getValue();
			 int maxDistance = Utils.convertToInt(maxDistanceStr);//meter m.
			 logger.debug("maxDistance:"+maxDistance+" m");
			 
			 sql.append("\n ,( ");
			 sql.append("\n select count(distinct cs_sub.cust_account_id) from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n xxpens_ar_customer_all_v c_sub   ");
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = cs.primary_salesrep_id ");
			 sql.append("\n and cs_sub.code = cs.code ");
			 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd') ) = cs.trip_day ");
			 //sql.append("\n and trunc(tc_sub.checkin_date) = cs.trip_date ");
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");

			//filter sales_code
			 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
			 
			//Case change customer to new Sales no join by sale
			 //sql.append("\n and tc_sub.salesrep_id = cs_sub.primary_salesrep_id ");
			 //sql.append("\n and tc_sub.sales_code = cs_sub.code ");
			 
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 
			 sql.append("\n and (tc_sub.flag='N' or tc_sub.flag ='Y') ");
			// check_in.distance > config max distance the not equals master location
			 sql.append("\n and tc_sub.distance > "+maxDistance);
			 if( !Utils.isNull(c.getDay()).equals("") && !Utils.isNull(c.getDayTo()).equals("") ){
			    sql.append("\n and trunc(tc_sub.checkin_date) >= to_date('"+c.getStartDate()+"','dd/mm/yyyy')");
			    sql.append("\n and trunc(tc_sub.checkin_date) <= to_date('"+c.getEndDate()+"','dd/mm/yyyy')");
			 }else{
				sql.append("\n and trunc(tc_sub.checkin_date) = to_date('"+c.getStartDate()+"','dd/mm/yyyy')"); 
			 }
			 sql.append(""+genWhereCondSql(conn, "_sub", c));
			 sql.append("\n )as count_cust_not_equal_masloc ");
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
			if( !Utils.isNull(c.getProvince()).equals("")){
				province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getProvince()));
			}
			if( !Utils.isNull(c.getDistrict()).equals("")){
				district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getDistrict()));
			}
			if( !Utils.isNull(c.getCustomerCode()).equals("")){
				sql.append("\n and c"+schema_name+".account_number ='"+c.getCustomerCode()+"' ");
			}
			if( !Utils.isNull(c.getCustomerName()).equals("")){
				sql.append("\n and c"+schema_name+".party_name LIKE '%"+c.getCustomerName()+"%' ");
			}
			if( !Utils.isNull(c.getCustCatNo()).equals("")){
				sql.append("\n and s"+schema_name+".sales_channel = '"+Utils.isNull(c.getCustCatNo())+"' ");
			}
			if( !Utils.isNull(c.getSalesChannelNo()).equals("")){
				sql.append("\n and s"+schema_name+".region = '"+Utils.isNull(c.getSalesChannelNo())+"' ");
			}
			if( !Utils.isNull(c.getSalesrepCode()).equals("")){
				sql.append("\n and cs"+schema_name+".primary_salesrep_id = "+Utils.isNull(c.getSalesrepCode())+"");
			}
			if( !Utils.isNull(province).equals("")){
				sql.append("\n and cs"+schema_name+".province = '"+province+"' ");
			}
			if( !Utils.isNull(district).equals("")){
				sql.append("\n and cs"+schema_name+".amphur = '"+district+"' ");
			}
			if( !Utils.isNull(c.getSalesZone()).equals("")){
			    sql.append("\n  and cs"+schema_name+".primary_salesrep_id in(");
			    sql.append("\n    select salesrep_id from pensbi.XXPENS_BI_MST_SALES_ZONE ");
			    sql.append("\n    where zone = "+Utils.isNull(c.getSalesZone()) );
			    sql.append("\n  )");
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
				 sql.append("\n and (  cs"+schema_name+".customer_class_code ='P' ");
				 sql.append("\n     or cs"+schema_name+".customer_class_code ='B' ");
			     sql.append("\n     or cs"+schema_name+".customer_class_code ='' ");
			     sql.append("\n     or cs"+schema_name+".customer_class_code is null ) ");
				
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
			  h.append(" <th nowrap >��ѡ�ҹ���</th> \n");
			  h.append(" <th nowrap >���;�ѡ�ҹ���</th> \n");
			  h.append(" <th nowrap >�ѹ���</th> \n");
			  h.append(" <th >�ҹ Call ��� Trip ������˹����</th> \n");
			  h.append(" <th >�ӹǹ��ҹ������ (��� Trip )</th> \n");
			  h.append(" <th >�ӹǹ��ҹ����� (��� Trip ) </th> \n");
			  h.append(" <th >�ӹǹ��ҹ������ (����ѹ�֡��ԧ )</th> \n");
			  h.append(" <th >�ӹǹ��ҹ����� (����ѹ�֡��ԧ )</th> \n");
			  h.append(" <th >�ӹǹ��ҹ��ҷ�����ç Trip</th> \n");
			  h.append(" <th >View ��ҹ������ç Trip </th> \n");
			  h.append(" <th >�ӹǹ��ҹ���ѹ�֡���������͢�� �Ũҡ�ԡѴ��ҹ���</th> \n");
			  h.append(" <th >View ��ҹ���ѹ�֡�Ũҡ�ԡѴ</th> \n");
			  h.append(" <th >View Detail </th> \n");
			 
			h.append("</tr> \n");
			return h;
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
			 tripDay = DateToolsUtil.getDayOfDate(Utils.parse(c.getDay(), Utils.DD_MM_YYYY_WITH_SLASH));
			 
			 //Get Config LimitDistince
			 String maxDistanceStr = CConstants.getConstants(CConstants.SPIDER_REF_CODE, CConstants.MAX_DISTANCE).getValue();
			 int maxDistance = Utils.convertToInt(maxDistanceStr);//meter m.
			 logger.debug("maxDistance:"+maxDistance+" m");
			 
			 sql.append("\n select c_sub.account_number ,c_sub.party_name ,  ");
			 sql.append("\n cl_sub.latitude as mst_lat, cl_sub.longitude as mst_lng ,");
			 sql.append("\n tc_sub.latitude , tc_sub.longitude , tc_sub.distance");
			 sql.append("\n from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub , ");
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
					 result.append(" <th nowrap >������ҹ���</th> \n");
					 result.append(" <th nowrap >������ҹ���</th> \n");
					 result.append(" <th >�ԡѴ��ѡ Latitude</th> \n");
					 result.append(" <th >�ԡѴ��ѡ Longtitude</th> \n");
					 result.append(" <th >�ԡѴ���ѹ�֡ Latitude</th> \n");
					 result.append(" <th >�ԡѴ���ѹ�֡  Longtitude</th> \n");
					 result.append(" <th >������ҧ�����ҧ 2 �ԡѴ (��.)</th> \n");
					 result.append("</tr> \n");
				 }
				 result.append("\n <tr>");
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
			 tripDay = DateToolsUtil.getDayOfDate(Utils.parse(c.getDay(), Utils.DD_MM_YYYY_WITH_SLASH));
			 
			 //Get Config LimitDistince
			 String maxDistanceStr = CConstants.getConstants(CConstants.SPIDER_REF_CODE, CConstants.MAX_DISTANCE).getValue();
			 int maxDistance = Utils.convertToInt(maxDistanceStr);//meter m.
			 logger.debug("maxDistance:"+maxDistance+" m");
			 
			 sql.append("\n select c_sub.account_number ,c_sub.party_name ,tc_sub.order_number,tc_sub.checkin_date ,");
			 sql.append("\n cl_sub.latitude as mst_lat, cl_sub.longitude as mst_lng ,");
			 sql.append("\n tc_sub.latitude , tc_sub.longitude , tc_sub.distance ,");
			 sql.append("\n tc_sub.file_name ");
			 sql.append("\n from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub , ");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub , ");
			 sql.append("\n xxpens_salesreps_v s_sub, ");
			 sql.append("\n xxpens_ar_customer_all_v c_sub , ");
			 sql.append("\n xxpens_om_trip_cust_loc cl_sub ");
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = "+c.getSalesrepCode()+"");
			 sql.append("\n and to_number(to_char(tc_sub.checkin_date,'dd') ) = "+tripDay);
			 //filter sales_code
			 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 sql.append("\n and tc_sub.salesrep_id = cl_sub.salesrep_id  ");
			 
			//Case Customer change to new sale
			 //sql.append("\n and cs_sub.primary_salesrep_id = cl_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  cl_sub.cust_account_id  "); 
			 
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 
			 sql.append("\n and (tc_sub.flag='N' or tc_sub.flag ='Y') ");
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
					 result.append(" <th nowrap >������ҹ���</th> \n");
					 result.append(" <th nowrap >������ҹ���</th> \n");
					 result.append(" <th >�ԡѴ��ѡ Latitude</th> \n");
					 result.append(" <th >�ԡѴ��ѡ Longtitude</th> \n");
					 result.append(" <th >�Ţ����͡���</th> \n");
					 result.append(" <th >�ѹ���ѹ�֡</th> \n");
					 result.append(" <th >�ԡѴ���ѹ�֡ Latitude</th> \n");
					 result.append(" <th >�ԡѴ���ѹ�֡  Longtitude</th> \n");
					 result.append(" <th >������ҧ�����ҧ 2 �ԡѴ (��.)</th> \n");
					 result.append(" <th >File Name</th> \n");
					 result.append("</tr> \n");
				 }
				 result.append("\n <tr>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("account_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("party_name"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("mst_lat"))+"</td>");
				 result.append("\n <td class='td_text_center'>"+Utils.isNull(rs.getString("mst_lng"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+Utils.isNull(rs.getString("order_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+Utils.stringValueChkNull(rs.getTimestamp("checkin_date"),Utils.DD_MM_YYYY_HH_MM_WITH_SLASH,Utils.local_th)+"</td>");
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
			 tripDay = DateToolsUtil.getDayOfDate(Utils.parse(c.getDay(), Utils.DD_MM_YYYY_WITH_SLASH));
			 
			 //Get Config LimitDistince
			 String maxDistanceStr = CConstants.getConstants(CConstants.SPIDER_REF_CODE, CConstants.MAX_DISTANCE).getValue();
			 int maxDistance = Utils.convertToInt(maxDistanceStr);//meter m.
			 logger.debug("maxDistance:"+maxDistance+" m");
			 
			 sql.append("\n select c_sub.account_number ,c_sub.party_name ,tc_sub.order_number,tc_sub.checkin_date ,");
			 sql.append("\n tc_sub.latitude , tc_sub.longitude , tc_sub.distance");
			 sql.append("\n,( ");
			 sql.append("\n   cs_sub.trip1 || decode(cs_sub.trip2, null,null ,','||cs_sub.trip2) || decode(cs_sub.trip3, null,null ,','||cs_sub.trip3) ");
			 sql.append("\n )as trip ");
			 sql.append("\n from ");
			 sql.append("\n xxpens_om_trip_checkin tc_sub ,");
			 sql.append("\n xxpens_ar_cust_sales_all cs_sub ,");
			 sql.append("\n xxpens_salesreps_v s_sub,");
			 sql.append("\n xxpens_ar_customer_all_v c_sub");
			 sql.append("\n where 1=1 "); 
			 sql.append("\n and cs_sub.primary_salesrep_id = "+c.getSalesrepCode()+"");
			//filter sales_code
			 sql.append("\n and tc_sub.sales_code  = cs_sub.code");
			 
			 sql.append("\n and nvl(cs_sub.trip1,0) <> "+tripDay);
			 sql.append("\n and nvl(cs_sub.trip2,0) <> "+tripDay);
			 sql.append("\n and nvl(cs_sub.trip3,0) <> "+tripDay);
			 
			 sql.append("\n and tc_sub.account_number = c_sub.account_number ");
			 sql.append("\n and tc_sub.cust_account_id = cs_sub.cust_account_id ");
			 
			 sql.append("\n and cs_sub.code = s_sub.code ");
			 sql.append("\n and cs_sub.primary_salesrep_id = s_sub.salesrep_id  ");
			 sql.append("\n and cs_sub.cust_account_id =  c_sub.cust_account_id  "); 
			 
			 sql.append("\n and (tc_sub.flag='N' or tc_sub.flag ='Y') ");
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
					 result.append(" <th nowrap >������ҹ���</th> \n");
					 result.append(" <th nowrap >������ҹ���</th> \n");
					 result.append(" <th >�Ţ����͡���</th> \n");
					 result.append(" <th >�ѹ���ѹ�֡</th> \n");
					 result.append(" <th >Trip</th> \n");
					 result.append("</tr> \n");
				 }
				 result.append("\n <tr>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("account_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text'>"+Utils.isNull(rs.getString("party_name"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+Utils.isNull(rs.getString("order_number"))+"</td>");
				 result.append("\n <td nowrap class='td_text_center'>"+Utils.stringValueChkNull(rs.getTimestamp("checkin_date"),Utils.DD_MM_YYYY_HH_MM_WITH_SLASH,Utils.local_th)+"</td>");
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
