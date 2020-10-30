package com.isecinc.pens.web.report.analyst.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.report.analyst.bean.ConditionFilterBean;
import com.isecinc.pens.web.report.analyst.bean.DisplayBean;
import com.isecinc.pens.web.report.analyst.helper.AUtils;
import com.isecinc.pens.web.report.analyst.helper.SecurityHelper;
import com.pens.util.ControlCode;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

public class AGenrateCondPopup {
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public static List<DisplayBean> getConditionValueList(Connection conn,String reportName,HttpServletRequest request,String condType)throws Exception{
		return getConditionValueListModel(conn,reportName,request,condType, null,null);
	}
	
	public static List<DisplayBean> getConditionValueList(String reportName,HttpServletRequest request,String condType)throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getConditionValueListModel(conn,reportName,request,condType, null,null);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public static List<DisplayBean> getConditionValueList(Connection conn,String reportName,HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		return getConditionValueListModel(conn,reportName,request,condType, code,desc);
	}
	
	public static List<DisplayBean> getConditionValueList(String reportName ,HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getConditionValueListModel(conn,reportName,request,condType, code,desc);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	
	public  static List<DisplayBean> getConditionValueListModel(Connection conn,String reportName,HttpServletRequest request,String condType,String code ,String desc)throws Exception{
		String sql = "";
		int no = 0;
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<DisplayBean> returnList = new ArrayList<DisplayBean>();
		User user = (User) request.getSession().getAttribute("user");
		try{
			logger.debug("condType:"+condType);
			logger.debug("code:"+code);
			logger.debug("desc:"+desc);
			
			if("inventory_item_id".equalsIgnoreCase(condType)){
				sql = "SELECT INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and INVENTORY_ITEM_CODE IN ("+AUtils.converToText("INVENTORY_ITEM_CODE", code) +") \n"; 
					}
					else{
						sql += " and INVENTORY_ITEM_CODE like '"+code+"%' \n"; 
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and INVENTORY_ITEM_DESC LIKE '%"+desc+"%' \n";
				}
				
				sql += "order by INVENTORY_ITEM_CODE \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("INVENTORY_ITEM_ID"),rs.getString("INVENTORY_ITEM_CODE") ,rs.getString("INVENTORY_ITEM_DESC")));
				}
			}else if("Customer_Category".equalsIgnoreCase(condType)){
				sql = "select cust_cat_no,cust_cat_desc from XXPENS_BI_MST_CUST_CAT where cust_cat_no is not null \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and cust_cat_no IN ("+AUtils.converToText("Customer_Category", code) +") \n"; 
					}else{
					    sql += " and cust_cat_no like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and cust_cat_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Customer_Category","");
				
				sql += "order by cust_cat_no \n";
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("cust_cat_no"),rs.getString("cust_cat_no"),rs.getString("cust_cat_desc")));
				}
			}else if("Division".equalsIgnoreCase(condType)){
				sql = "select div_no,div_desc from XXPENS_BI_MST_DIVISION where div_no is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and div_no like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and div_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Division","");
				
				sql += "order by div_no \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("div_no"),rs.getString("div_no"),rs.getString("div_desc")));
				}	
				
			}else if("Salesrep_id".equalsIgnoreCase(condType)){
				sql  = "select salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
				sql += " and salesrep_code not like 'C%'  \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and Salesrep_code in ("+AUtils.converToText("Salesrep_id", code) +") \n"; 
					}else{
					    sql += " and Salesrep_code like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and salesrep_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Salesrep_id","");
				
				
				sql += "order by Salesrep_code \n";
				
				logger.debug("SQL:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("salesrep_id"),rs.getString("salesrep_code"),rs.getString("salesrep_desc")));
				}	
			}else if("Sales_Channel".equalsIgnoreCase(condType)){
				sql = "select sales_channel_no,sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL where sales_channel_no is not null \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and sales_channel_no IN ("+AUtils.converToText("Sales_Channel", code) +") \n"; 
					}else{
					    sql += " and sales_channel_no like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and sales_channel_desc LIKE '%"+desc+"%' \n";
				}
				
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Sales_Channel","");

				sql += "order by sales_channel_no \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("sales_channel_no"),rs.getString("sales_channel_no"),rs.getString("sales_channel_desc")));
				}	
			}else if("Customer_Group".equalsIgnoreCase(condType)){
				sql = "select cust_group_no,cust_group_desc from XXPENS_BI_MST_CUST_GROUP where cust_group_no is not null ";
				if(!Utils.isNull(code).equals("")){
					sql += " and cust_group_no like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and cust_group_desc LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Customer_Group");
				
				sql += "order by cust_group_no \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("cust_group_no"),rs.getString("cust_group_no"),rs.getString("cust_group_desc")));
				}
				
			// Record Over display 
			}else if("Customer_id".equalsIgnoreCase(condType)){
				sql = " select customer_id,customer_code,customer_desc \n";
				sql +=" from PENSBI.XXPENS_BI_MST_CUSTOMER where customer_code is not null \n";	 
				
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and customer_code in ("+AUtils.converToText("customer_code", code) +") \n";
					}else{
						sql += " and customer_code like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and customer_desc LIKE '%"+desc+"%' \n";
				}
			    /** ProjectCAnalyst filter in BME_TT_BRANCH **/
				if(Utils.isNull(reportName).equalsIgnoreCase("ProjectCAnalyst")){
					sql += " and customer_code IN(SELECT ORACLE_CUST_NO FROM PENSBI.PENS_TT_BRANCH)  \n";
				}
				
			    /** filter by user **/
			    sql += SecurityHelper.genWhereSqlFilterByUser(request, "Customer_id");
			    sql += "and rownum <="+AConstants.MAX_SHOW_CUSTOMER+" \n";
				sql += "order by customer_code \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("customer_id"),rs.getString("customer_code"),rs.getString("customer_code")+"-"+rs.getString("customer_desc")));
				}
				
			/** Customer ID for Project C  **/
			}else if("CUSTOMER_GROUP_TT".equalsIgnoreCase(condType)){
					
				sql = "select distinct cust_group_desc as customer_code, \n";
				sql += "cust_group_desc as customer_id,'' as customer_desc \n";
				sql += "from PENSBI.PENS_TT_BRANCH WHERE 1=1 \n";
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and cust_group_desc in ("+AUtils.converToText("customer_code", code) +") \n";
					}else{
						sql += " and cust_group_desc like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and cust_group_desc LIKE '%"+desc+"%' \n";
				}
				sql += "order by cust_group_desc \n";
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("customer_id"),rs.getString("customer_code"),rs.getString("customer_code")));
				}
			}else if("Brand".equalsIgnoreCase(condType)){
				sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
				
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and brand_no in ("+AUtils.converToText("Brand", code) +") \n";
					}
					else{
						sql += " and brand_no like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and brand_desc LIKE '%"+desc+"%' \n";
				}
				
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user,"Brand","");
				
				sql += "order by brand_no \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("brand_no"),rs.getString("brand_no"),rs.getString("brand_desc")));
				}
				
			}else if("Brand_Group".equalsIgnoreCase(condType)){
		
                sql = "select distinct brand_group_no,brand_group_desc  from XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n";
				
				if(!Utils.isNull(code).equals("")){
					if(code.indexOf(",") > -1){
						sql += " and brand_group_no in ("+AUtils.converToText("Brand", code) +") \n";
					}
					else{
						sql += " and brand_group_no like '"+code+"%' \n";
					}
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and brand_group_desc LIKE '%"+desc+"%' \n";
				}
				
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Brand","Brand_no");
				
				sql += "order by brand_group_no \n";
				
				logger.debug("SQL:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("brand_group_no"),rs.getString("brand_group_no"),rs.getString("brand_group_desc")));
				}
				
			 }else if("SUBBRAND".equalsIgnoreCase(condType)){
					
	                sql = "select distinct subbrand_no,subbrand_desc  from XXPENS_BI_MST_SUBBRAND where subbrand_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						if(code.indexOf(",") > -1){
							sql += " and subbrand_no in ("+AUtils.converToText("SubBrand", code) +") \n";
						}
						else{
							sql += " and subbrand_no like '"+code+"%' \n";
						}
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and subbrand_desc LIKE '%"+desc+"%' \n";
					}
					
					/** filter by user **/
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "subbrand","");
					
					sql += "order by subbrand_no \n";
					
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("subbrand_no"),rs.getString("subbrand_no"),rs.getString("subbrand_desc")));
					}
			//SALES ZONE
			 }else if("SALES_ZONE".equalsIgnoreCase(condType)){
				 sql  = "select distinct ZONE,ZONE_NAME  from \n ";
				 sql +=" PENSBI.XXPENS_BI_MST_SALES_ZONE where ZONE is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						if(code.indexOf(",") > -1){
							sql += " and ZONE in ("+AUtils.converToText("SALES_ZONE", code) +") \n";
						}
						else{
							sql += " and ZONE like '"+code+"%' \n";
						}
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and ZONE_NAME LIKE '%"+desc+"%' \n";
					}
					
					/** Case ProjectCAnalyst show 0,1,2,3,4 **/
					if(Utils.isNull(reportName).equalsIgnoreCase("ProjectCAnalyst")){
						sql += " and ZONE IN('0','1','2','3','4') \n";
					}
					
					/** filter by user **/
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "SALES_ZONE","");
					
					sql += "order by ZONE \n";
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("ZONE"),rs.getString("ZONE"),rs.getString("ZONE_NAME")));
					}
			}else if("Invoice_Date".equalsIgnoreCase(condType)){
				sql = "select invoice_date from XXPENS_BI_MST_INVOICE_DATE where invoice_date is not null ";
				if(!Utils.isNull(code).equals("")){
					Date date = DateUtil.parseToBudishDate(code, DateUtil.DD_MM_YYYY_WITH_SLASH);
					String chrisDateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql += " and invoice_date = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
				}
				if(!Utils.isNull(desc).equals("")){
					Date date = DateUtil.parseToBudishDate(code, DateUtil.DD_MM_YYYY_WITH_SLASH);
					String chrisDateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql += " and invoice_date = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
				}
				sql +="order by invoice_date \n";
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					String dateStr = DateUtil.stringValue(rs.getDate("invoice_date",Calendar.getInstance(Locale.US)), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th);
					
					returnList.add(new DisplayBean(no,dateStr,dateStr,dateStr));
				}
			}else if("SALES_ORDER_DATE".equalsIgnoreCase(condType)){
				sql = "select ORDER_DATE from XXPENS_BI_MST_ORDER_DATE where ORDER_DATE is not null ";
				if(!Utils.isNull(code).equals("")){
					Date date = DateUtil.parseToBudishDate(code, DateUtil.DD_MM_YYYY_WITH_SLASH);
					String chrisDateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql += " and ORDER_DATE = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
				}
				if(!Utils.isNull(desc).equals("")){
					Date date = DateUtil.parseToBudishDate(code, DateUtil.DD_MM_YYYY_WITH_SLASH);
					String chrisDateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql += " and ORDER_DATE = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
				}
				sql +="order by ORDER_DATE \n";
				
				//logger.debug("SQL:"+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					String dateStr = DateUtil.stringValue(rs.getDate("ORDER_DATE",Calendar.getInstance(Locale.US)), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th);
					returnList.add(new DisplayBean(no,dateStr,dateStr,dateStr));
				}
			}else if("Province".equalsIgnoreCase(condType)){
				sql = "select * from XXPENS_BI_MST_PROVINCE where province is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and province like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and province LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "Province");
				
				sql += "order by province \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("province"),rs.getString("province"),rs.getString("province")));
				}
			}else if("AMPHOR".equalsIgnoreCase(condType)){
				sql = "select * from XXPENS_BI_MST_AMPHOR where amphor is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and amphor like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and amphor LIKE '%"+desc+"%' \n";
				}
				/** filter by user **/
				sql += SecurityHelper.genWhereSqlFilterByUser(request, "AMPHOR");

				sql += "order by amphor \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("amphor"),rs.getString("amphor"),rs.getString("amphor")));
				}
			}else if("TAMBOL".equalsIgnoreCase(condType)){
				sql = "select * from XXPENS_BI_MST_TAMBOL WHERE tambol is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and tambol like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and tambol LIKE '%"+desc+"%' \n";
				}
				sql += "order by tambol \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("tambol"),rs.getString("tambol"),rs.getString("tambol")));
				}
			}
			else if("SALES_ORDER_NO".equalsIgnoreCase(condType)){
				sql = "select  DISTINCT  SALES_ORDER_NO from XXPENS_BI_SALES_ANALYSIS WHERE SALES_ORDER_NO is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and SALES_ORDER_NO like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and SALES_ORDER_NO LIKE '%"+desc+"%' \n";
				}
				sql += "order by SALES_ORDER_NO \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO")));
				}
			}
			
			else if("INVOICE_NO".equalsIgnoreCase(condType)){
				sql = "select  DISTINCT  INVOICE_NO from XXPENS_BI_SALES_ANALYSIS WHERE INVOICE_NO is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and INVOICE_NO like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and INVOICE_NO LIKE '%"+desc+"%' \n";
				}
				sql += "order by INVOICE_NO \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO")));
				}
			}

			// Over Display  display 100 record
			else if("SHIP_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "select  * from XXPENS_BI_MST_CUST_SHIP_ADDR WHERE CUSTOMER_SHIP_TO_ADDRESS is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and CUSTOMER_SHIP_TO_ADDRESS like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and CUSTOMER_SHIP_TO_ADDRESS LIKE '%"+desc+"%' \n";
				}
				sql +=" and rownum <= 100 \n";
				sql +=" order by CUSTOMER_SHIP_TO_ADDRESS \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("CUSTOMER_SHIP_TO_ADDRESS")));
				}
			}
			// Over Display  display 100 record
			else if("BILL_TO_SITE_USE_ID".equalsIgnoreCase(condType)){
				sql = "select * from XXPENS_BI_MST_CUST_BILL_ADDR WHERE CUSTOMER_BILL_TO_ADDRESS is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and CUSTOMER_BILL_TO_ADDRESS like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and CUSTOMER_BILL_TO_ADDRESS LIKE '%"+desc+"%' \n";
				}
				sql +=" and rownum <= 100 \n";
				sql +=" order by CUSTOMER_BILL_TO_ADDRESS \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("CUSTOMER_BILL_TO_ADDRESS")));
				}
			}
			//Organization 
			else if("Organization_id".equalsIgnoreCase(condType)){
				sql = "select  * from XXPENS_BI_MST_ORGANIZATION WHERE Organization_code is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and Organization_code like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and Organization_code LIKE '%"+desc+"%' \n";
				}
				sql += "order by Organization_code \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("Organization_id"),rs.getString("Organization_code"),rs.getString("Organization_code")));
				}
			}
			
			//Order Type
			else if("Order_type_id".equalsIgnoreCase(condType)){
				sql = "select  * from XXPENS_BI_MST_ORDER_TYPE WHERE Order_type_id is not null \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and order_type_id like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and order_type_name LIKE '%"+desc+"%' \n";
				}
				sql += "order by order_type_name \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("Order_type_id"),rs.getString("Order_type_id"),rs.getString("Order_type_name")+"-"+rs.getString("Order_type_cat")));
				}
			}
			//Customer_class_code (customerType)
			else if("CUSTOMER_CLASS_CODE".equalsIgnoreCase(condType)){
				sql = "select  * from apps.xxpens_ar_cust_class_v WHERE ENABLED_FLAG ='Y' \n";
				if(!Utils.isNull(code).equals("")){
					sql += " and code like '"+code+"%' \n";
				}
				if(!Utils.isNull(desc).equals("")){
					sql += " and description LIKE '%"+desc+"%' \n";
				}
				sql += "order by code \n";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					no++;
					returnList.add(new DisplayBean(no,rs.getString("code"),rs.getString("code"),rs.getString("description")));
				}
			}
			
			logger.debug("SQL:"+sql);
		}catch(Exception e){
		   throw e;
		}finally{
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return returnList;
	}
	
	public  static List<DisplayBean> getConditionValueListByParent(String reportName,User user,String currCondType,String code ,String desc,ConditionFilterBean filterBean)throws Exception{
		if(ControlCode.canExecuteMethod("", "getConditionValueListByParent_NEW")){
			return getConditionValueListByParent_NEW(reportName, user, currCondType, code, desc, filterBean);
		}
		return getConditionValueListByParent_OLD(reportName, user, currCondType, code, desc, filterBean);
	}
	
	//WAIT NEXT IDEA ???
	public static List<DisplayBean> getConditionValueListByParent_NEW(String reportName,User user,String currCondType,String code ,String desc,ConditionFilterBean filterBean)throws Exception{
		StringBuffer sql = new StringBuffer("");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<DisplayBean> returnList = new ArrayList<DisplayBean>();
		int no = 0;
		boolean debug = true;
		ConditionFilterBean parentCondBean = null;
		Map<String,ConditionFilterBean> parentRelateMap = new HashMap<String, ConditionFilterBean>();
		try{
			 logger.debug("reportName:"+reportName);
			
			 conn = DBConnection.getInstance().getConnectionApps();
			
			  //Get Parent Cond
			  for(int i=1;i<Integer.parseInt(filterBean.getCurrCondNo());i++){
				//check parent cond is relate currCondition
				parentCondBean = filterBean.getCondAllMap().get(String.valueOf(i));
				if( !Utils.isNull(parentCondBean.getCondType()).equals("") && parentCondBean.isRelate()){
					//Gen sql 
					parentRelateMap.put(parentCondBean.getCondType().toUpperCase(),parentCondBean);
				}
			  }
				
			   //แบรนด์ --> สินค้า (SKU) OK
			   //Brand_Group->Brand->Sku
			   if("inventory_item_id".equalsIgnoreCase(currCondType)){ 

					sql.append(" SELECT distinct u.INVENTORY_ITEM_ID,u.segment1 as INVENTORY_ITEM_CODE \n");
					sql.append(" ,u.description as INVENTORY_ITEM_DESC \n");
					sql.append(" from apps.xxpens_om_item_mst_v u \n");
					sql.append(" where 1=1 \n");
					
					if(parentRelateMap.get("BRAND_GROUP") != null){
						parentCondBean = parentRelateMap.get("BRAND_GROUP");
						
						sql.append(" and u.brand in (  \n");
						sql.append("   select brand_no from PENSBI.XXPENS_BI_MST_BRAND where brand_no is not null \n");
						sql.append("   and brand_no in ( \n" );
						sql.append("     select distinct brand_no  from PENSBI.XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n" );
						sql.append("     and brand_group_no in ("+AUtils.converToText("Brand_Group", parentCondBean.getCondCode())+") \n");
						sql.append("   ) \n ");
						sql.append(" ) \n ");
					}
					if(parentRelateMap.get("BRAND") != null){
						parentCondBean = parentRelateMap.get("BRAND");
						sql.append(" and brand IN ("+AUtils.converToText("Brand", parentCondBean.getCondCode()) + ") \n");
					
					}
					
					if(!Utils.isNull(code).equals("")){
						sql.append(" and segment1 like '"+code+"%' \n");
					}
					if(!Utils.isNull(desc).equals("")){
						sql.append(" and DESCRIPTION LIKE '%"+desc+"%' \n");
					}
					sql.append("order by segment1 \n");
					
					
					if(debug)logger.debug("sql:\n "+sql.toString());
					
					ps = conn.prepareStatement(sql.toString());
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("INVENTORY_ITEM_ID"),rs.getString("INVENTORY_ITEM_CODE") ,rs.getString("INVENTORY_ITEM_DESC")));
					}
					
				}
			   /*
				ดิวิชั่น   Division               ==>  พนักงานขาย Salesrep_id	
				ประเภทขาย Customer_Category    ==>  พนักงานขาย Salesrep_id
				ภาคตามพนักงานขาย Sales_ZONE     ==>  พนักงานขาย Salesrep_id
				*/
				
				/*
				ประเภทขาย Customer_Category   + ภาคตามพนักงานขายย Sales_Channel ==>  พนักงานขาย Salesrep_id
				ดิวิชั่น   Division              + ภาคตามพนักงานขาย Sales_Channel  ==>  พนักงานขาย Salesrep_id	
				ภาคตามพนักงานขาย Sales_Channel  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
				ภาคตามพนักงานขาย Sales_Channel  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_id
				
				ประเภทขาย Customer_Category   + ภาคตามการดูแล SALES_ZONE ==>  พนักงานขายย Salesrep_id
				ดิวิชั่น   Division              + ภาคตามการดูแล  SALES_ZONE  ==>  พนักงานขาย Salesrep_id	
				ภาคตามการดูแล SALES_ZONE  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
				ภาคตามการดูแล SALES_ZONE  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_id
				
				*/
			   else  if("Salesrep_id".equalsIgnoreCase(currCondType)){ 
				  
					   /*sql.append(" select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n");
					   sql.append(" from PENSBI.XXPENS_BI_MST_SALESREP u \n");
					   if("SalesAnalyst".equalsIgnoreCase(reportName)){
					       sql.append(" ,PENSBI.xxpens_bi_sales_analysis t1 \n");
					   }else if("ProjectCAnalyst".equalsIgnoreCase(reportName)){
						   sql.append(" ,PENSBI.xxpens_bi_sales_analysis t1 \n");
					   }
					   if(parentRelateMap.get("SALES_ZONE") != null){
					      sql.append(" ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z \n");
					   }
					   sql.append(" where 1=1 \n");
					   sql.append(" and u.salesrep_code is not null \n");
					   sql.append(" and t1.salesrep_id = u.salesrep_id \n"); 
					  
					   if(parentRelateMap.get("SALES_ZONE") != null){
					      sql.append(" and u.salesrep_id = z.salesrep_id \n");
					   }
					   if("ProjectCAnalyst".equalsIgnoreCase(reportName)){
						   sql.append(" and t1.customer_id in( \n");
						   sql.append("   select C.customer_id from PENSBI.PENS_TT_BRANCH B,PENSBI.XXPENS_BI_MST_CUSTOMER C \n");
						   sql.append("   WHERE C.customer_code = B.oracle_cust_no \n");
						   sql.append(" ) \n");
					   }
					   //filter by parentCondition
					   
					   // ประเภทขาย Customer_Category
					   if(parentRelateMap.get("CUSTOMER_CATEGORY") != null){
							parentCondBean = parentRelateMap.get("CUSTOMER_CATEGORY");
							sql.append(" and t1.CUSTOMER_CATEGORY IN ("+AUtils.converToText("CUSTOMER_CATEGORY", parentCondBean.getCondCode()) + ") \n");
							sql.append(" and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(parentCondBean.getCondCode())+"%'  \n");
					   }
					   //ภาคตามพนักงานขาย Sales_Channel
					   if(parentRelateMap.get("SALES_CHANNEL") != null){
							parentCondBean = parentRelateMap.get("SALES_CHANNEL");
							sql.append(" and t1.Sales_Channel IN ("+AUtils.converToText("SALES_CHANNEL", parentCondBean.getCondCode()) + ") \n");
							sql.append(" and substr(u.salesrep_code,2,1) IN ("+AUtils.converToText("SALES_CHANNEL", parentCondBean.getCondCode()) + ") \n");
					   }
					   //ภาคตามการดูแล SALES_ZONE
					   if(parentRelateMap.get("SALES_ZONE") != null){
							parentCondBean = parentRelateMap.get("SALES_ZONE");
							sql.append(" and z.zone IN ("+AUtils.converToText("SALES_ZONE", parentCondBean.getCondCode()) + ") \n");
					   }
					   //ดิวิชั่น   Division
					   if(parentRelateMap.get("DIVISION") != null){
							parentCondBean = parentRelateMap.get("DIVISION");
							sql.append(" and t1.division IN ("+AUtils.converToText("DIVISION", parentCondBean.getCondCode()) + ") \n");
					   }
					   
					   sql.append(" and u.salesrep_code not like 'C%'  \n");*/
				  
				   /** new code **/
				   sql.append("SELECT distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc  \n");
				   sql.append("FROM PENSBI.XXPENS_BI_MST_SALESREP_MAP SP ,PENSBI.XXPENS_BI_MST_SALESREP u \n");
				   if(parentRelateMap.get("SALES_ZONE") != null){
					  sql.append(" ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z \n");
				   }
				   sql.append(" where 1=1 \n");
				   sql.append(" and u.salesrep_code is not null \n");
				   sql.append(" and sp.salesrep_id = u.salesrep_id \n"); 
				  
				   if(parentRelateMap.get("SALES_ZONE") != null){
				      sql.append(" and u.salesrep_id = z.salesrep_id \n");
				   }
				   if(parentRelateMap.get("SALES_ZONE") != null){
					  sql.append(" and u.salesrep_id = z.salesrep_id \n");
				   }
				   
				   if("ProjectCAnalyst".equalsIgnoreCase(reportName)){
					   sql.append(" and u.salesrep_code like 'S%'  \n");//ProjectC Display CreditSales Only
				   }
				   //filter by parentCondition
				   
				   // ประเภทขาย Customer_Category
				   if(parentRelateMap.get("CUSTOMER_CATEGORY") != null){
						parentCondBean = parentRelateMap.get("CUSTOMER_CATEGORY");
						sql.append(" and sp.CUST_CAT_NO IN ("+AUtils.converToText("CUSTOMER_CATEGORY", parentCondBean.getCondCode()) + ") \n");
						sql.append(" and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(parentCondBean.getCondCode())+"%'  \n");
				   }
				   //ภาคตามพนักงานขาย Sales_Channel
				   if(parentRelateMap.get("SALES_CHANNEL") != null){
						parentCondBean = parentRelateMap.get("SALES_CHANNEL");
						sql.append(" and sp.SALES_CHANNEL_NO IN ("+AUtils.converToText("SALES_CHANNEL", parentCondBean.getCondCode()) + ") \n");
						sql.append(" and substr(u.salesrep_code,2,1) IN ("+AUtils.converToText("SALES_CHANNEL", parentCondBean.getCondCode()) + ") \n");
				   }
				   //ภาคตามการดูแล SALES_ZONE
				   if(parentRelateMap.get("SALES_ZONE") != null){
						parentCondBean = parentRelateMap.get("SALES_ZONE");
						sql.append(" and z.zone IN ("+AUtils.converToText("SALES_ZONE", parentCondBean.getCondCode()) + ") \n");
				   }
				   //ดิวิชั่น   Division
				   if(parentRelateMap.get("DIV_NO") != null){
						parentCondBean = parentRelateMap.get("DIVISION");
						sql.append(" and sp.DIV_NO IN ("+AUtils.converToText("DIVISION", parentCondBean.getCondCode()) + ") \n");
				   }
				   
				   sql.append(" and u.salesrep_code not like 'C%'  \n");
				  
				   //Filter By Role User
				   sql.append(SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u."));
				   
				   //criteria 
				   if(!Utils.isNull(code).equals("")){
					   sql.append("  and salesrep_code like '"+code+"%' \n");
				   }
				   if(!Utils.isNull(desc).equals("")){
						sql.append("  and salesrep_desc LIKE '%"+desc+"%' \n");
				   }
				   sql.append(" order by salesrep_code \n");
				   
				   if(debug){
					   logger.debug("sql:"+sql);
				   }
					
				   ps = conn.prepareStatement(sql.toString());
				   rs = ps.executeQuery();
				   while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("salesrep_id"),rs.getString("salesrep_code"),rs.getString("salesrep_desc")));
				  }	
			  }
			   
			 //ภาคตามการดูแล SALES_ZONE  ==> กลุ่มร้านค้า(TT) CUTOMER_GROUP_TT 
		       else if("CUSTOMER_GROUP_TT".equalsIgnoreCase(currCondType)){ 

					sql.append(" SELECT distinct u.cust_group_desc \n");
					sql.append(" from PENSBI.PENS_TT_BRANCH u \n");
					sql.append(" where 1=1 \n");
					
					//filter by parentCondition
					if(parentRelateMap.get("SALES_ZONE") != null){
						parentCondBean = parentRelateMap.get("SALES_ZONE");
						sql.append(" and ZONE IN ("+AUtils.converToText("SALES_ZONE", parentCondBean.getCondCode()) + ") \n");
					}
					
					if(!Utils.isNull(code).equals("")){
						sql.append(" and cust_group_desc like '"+code+"%' \n");
					}
					if(!Utils.isNull(desc).equals("")){
						sql.append(" and cust_group_desc LIKE '%"+desc+"%' \n");
					}
					sql.append("order by cust_group_desc \n");
					
					
					if(debug)logger.debug("sql:\n "+sql.toString());
					
					ps = conn.prepareStatement(sql.toString());
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("cust_group_desc"),rs.getString("cust_group_desc") ,rs.getString("cust_group_desc")));
					}
					
				}
			   
			   //กลุ่มร้านค้า(Customer_Group) --> ร้านค้า(Customer_id)
			   //กลุ่มร้านค้า(Customer_Group_TT) --> ร้านค้า(Customer_id)
		       else if("Customer_id".equalsIgnoreCase(currCondType)){ 
		    	   
		    	   //กลุ่มร้านค้า(Customer_Group) --> ร้านค้า(Customer_id)
                    if("SalesAnalyst".equalsIgnoreCase(reportName)){
						sql.append(" select distinct u.customer_id,u.customer_code,u.customer_desc \n");
						sql.append(" from PENSBI.XXPENS_BI_MST_CUSTOMER u, PENSBI.xxpens_bi_sales_analysis_v t1 \n");
						sql.append(" where u.customer_id is not null and t1.customer_id=u.customer_id \n");
						
						//filter by parentCondition
						if(parentRelateMap.get("CUSTOMER_GROUP") != null){
							parentCondBean = parentRelateMap.get("CUSTOMER_GROUP");
							sql.append(" and t1.customer_group IN ("+AUtils.converToText("CUSTOMER_GROUP", parentCondBean.getCondCode()) + ") \n");
						}
						//Filter By Role User
						sql.append(SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Customer_id","u."));

						if(!Utils.isNull(code).equals("")){
							sql.append(" and u.customer_code like '"+code+"%' \n");
						}
						if(!Utils.isNull(desc).equals("")){
							sql.append(" and u.customer_desc LIKE '%"+desc+"%' \n");
						}
						sql.append("order by u.customer_code \n");
						
					//กลุ่มร้านค้า(Customer_Group_TT) --> ร้านค้า(Customer_id)
                    }else if("ProjectCAnalyst".equalsIgnoreCase(reportName)){
                    	
                    	sql.append(" select distinct u.customer_id,u.customer_code,u.customer_desc \n");
						sql.append(" from PENSBI.XXPENS_BI_MST_CUSTOMER u \n");
						sql.append(" where u.customer_id is not null \n");
						
                    	//filter by parentCondition
                    	if(parentRelateMap.get("CUSTOMER_GROUP_TT") != null){
							parentCondBean = parentRelateMap.get("CUSTOMER_GROUP_TT");
							sql.append(" and u.customer_code IN ( \n");
							sql.append("   select oracle_cust_no from PENSBI.PENS_TT_BRANCH \n");
							sql.append("   WHERE cust_group_desc ="+AUtils.converToText("CUSTOMER_GROUP_TT", parentCondBean.getCondCode()) );
							sql.append(") \n");
						}

						if(!Utils.isNull(code).equals("")){
							sql.append(" and u.customer_code like '"+code+"%' \n");
						}
						if(!Utils.isNull(desc).equals("")){
							sql.append(" and u.customer_desc LIKE '%"+desc+"%' \n");
						}
						sql.append("order by u.customer_code \n");
                    }
                    
					logger.debug("sql:\n "+sql.toString());
					
					ps = conn.prepareStatement(sql.toString());
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
					    returnList.add(new DisplayBean(no,rs.getString("customer_id"),rs.getString("customer_code"),rs.getString("customer_code")+"-"+rs.getString("customer_desc")));
					}
			  }
			   
			 //จังหวัด(Province) --> อำเภอ(AMPHOR)
               else if("AMPHOR".equalsIgnoreCase(currCondType)){ 
		    	 
					sql.append(" select distinct u.* \n");
					sql.append(" from PENSBI.XXPENS_BI_MST_AMPHOR u, \n");
					sql.append(" PENSBI.xxpens_bi_sales_analysis t1 \n");
					sql.append(" where u.amphor is not null and t1.amphur = u.amphor \n");
					
					//filter by parentCondition
					if(parentRelateMap.get("PROVINCE") != null){
						parentCondBean = parentRelateMap.get("PROVINCE");
						sql.append(" and t1.province IN ("+AUtils.converToText("PROVINCE", parentCondBean.getCondCode()) + ") \n");
					}
					//Filter By Role User
					sql.append(SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "AMPHOR","u."));

					if(!Utils.isNull(code).equals("")){
						sql.append(" and u.amphor like '"+code+"%' \n");
					}
					if(!Utils.isNull(desc).equals("")){
						sql.append(" and u.amphor LIKE '%"+desc+"%' \n");
					}
					sql.append("order by u.amphor \n");
						
					
					logger.debug("sql:\n "+sql.toString());
					
					ps = conn.prepareStatement(sql.toString());
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("amphor"),rs.getString("amphor"),rs.getString("amphor")));
					}
				}
		}catch(Exception e){
		   throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
			if(ps != null){
			   ps.close();ps= null;
			}
			if(rs != null){
			   rs.close();rs=null;
			}
		}
		return returnList;
	}
	public static List<DisplayBean> getConditionValueListByParent_OLD(String reportName,User user,String currCondType,String code ,String desc,ConditionFilterBean filterBean)throws Exception{
		String sql = "";
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs= null;
		List<DisplayBean> returnList = new ArrayList<DisplayBean>();
		int no = 0;
		boolean debug = true;
		try{
			//logger.debug("panrentType1:"+panrentType1+",parentCode1:"+parentCode1);
			
				conn = DBConnection.getInstance().getConnectionApps();
				
				//แบรนด์ --> สินค้า (SKU) OK
				//Brand_Group->Brand->Sku
				if("inventory_item_id".equalsIgnoreCase(currCondType)){ 
                    
					if(filterBean.getCurrCondNo().equals("2")){
						if ("Brand".equals(filterBean.getCondType1())){
							sql = "SELECT distinct u.INVENTORY_ITEM_ID,u.INVENTORY_ITEM_CODE,u.INVENTORY_ITEM_DESC "+
							" from XXPENS_BI_MST_ITEM u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.INVENTORY_ITEM_CODE is not null and t1.inventory_item_id=u.inventory_item_id \n"+ 
							" and t1.brand IN ("+AUtils.converToText("Brand", filterBean.getCondCode1()) + ") \n";
						} else {
						    sql = "SELECT distinct INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
						}
					}else if(filterBean.getCurrCondNo().equals("3")){
						if (filterBean.getCondType1().equalsIgnoreCase("Brand_Group") && filterBean.getCondType2().equalsIgnoreCase("Brand")){
								sql = " \n SELECT distinct u.INVENTORY_ITEM_ID,u.INVENTORY_ITEM_CODE,u.INVENTORY_ITEM_DESC "+
								" from XXPENS_BI_MST_ITEM u, xxpens_bi_sales_analysis_v t1 \n"+
								" where u.INVENTORY_ITEM_CODE is not null and t1.inventory_item_id=u.inventory_item_id \n"+ 
								" and t1.brand in (  \n"+
							    "   select brand_no from XXPENS_BI_MST_BRAND where brand_no is not null \n"+
								"   and brand_no in ( \n" +
										" select distinct brand_no  from XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n" +
										"   and brand_group_no in ("+AUtils.converToText("Brand_Group", filterBean.getCondCode1())+") \n"+
										" ) \n "+
							   "   and brand_no IN ("+AUtils.converToText("Brand", filterBean.getCondCode2()) + ") \n"+
								"  ) \n";

						} else {
							 sql = "SELECT distinct INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
						}
					}else{
						sql = "SELECT distinct INVENTORY_ITEM_ID,INVENTORY_ITEM_CODE,INVENTORY_ITEM_DESC from XXPENS_BI_MST_ITEM where INVENTORY_ITEM_CODE is not null \n";
					}
					
					if(!Utils.isNull(code).equals("")){
						sql += " and INVENTORY_ITEM_CODE like '"+code+"%' \n"; 
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and INVENTORY_ITEM_DESC LIKE '%"+desc+"%' \n";
					}
					sql += "order by INVENTORY_ITEM_CODE \n";
					
					
					if(debug)logger.debug("sql:"+sql);
					
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("INVENTORY_ITEM_ID"),rs.getString("INVENTORY_ITEM_CODE") ,rs.getString("INVENTORY_ITEM_DESC")));
					}
					
					/*
					ดิวิชั่น   Division               ==>  พนักงานขาย Salesrep_id	
					ประเภทขาย Customer_Category    ==>  พนักงานขาย Salesrep_id
					ภาคตามพนักงานขาย Sales_ZONE     ==>  พนักงานขาย Salesrep_id
					*/
				}else if("Salesrep_id".equalsIgnoreCase(currCondType)){
					if(filterBean.getCurrCondNo().equals("2")){
						//ดิวิชั่น  ==>> พนักงานขาย OK
						 if (filterBean.getCondType1().equalsIgnoreCase("Division")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							      " from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							      " where u.salesrep_code is not null and t1.salesrep_id=u.salesrep_id \n"+ 
							      " and t1.division in ("+AUtils.converToText("Division", filterBean.getCondCode1()) + ") \n";
							sql += " and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
						 //ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
						 }else if (filterBean.getCondType1().equalsIgnoreCase("Customer_Category") ){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							      " from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							      " where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							      " and t1.Customer_Category in ("+AUtils.converToText("Customer_Category", filterBean.getCondCode1()) + ")  \n";
							sql += " and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(filterBean.getCondCode1())+"%'  \n";
							sql += " and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							 
						  //ภาคตามพนักงานขาย  ==>  พนักงานขาย Salesrep_id
						 }else if (filterBean.getCondType1().equalsIgnoreCase("Sales_Channel") ){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							      " from XXPENS_BI_MST_SALESREP u,apps.xxpens_salesreps_v S \n"+
							      " where u.salesrep_code is not null and S.salesrep_id = u.salesrep_id \n"+ 
							      " and S.region in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ")  \n";
							sql += " and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(filterBean.getCondCode1())+"%'  \n";
							sql += " and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
						  //ภาคตามการดูแล  ==>  พนักงานขาย Salesrep_id
						 }else if (filterBean.getCondType1().equalsIgnoreCase("SALES_ZONE") ){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							      " from XXPENS_BI_MST_SALESREP u,apps.xxpens_salesreps_v S \n"+
							      " where u.salesrep_code is not null and S.salesrep_id = u.salesrep_id \n"+ 
							      " and S.zone in ("+AUtils.converToText("SALES_ZONE", filterBean.getCondCode1()) + ")  \n";
							sql += " and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(filterBean.getCondCode1())+"%'  \n";
							sql += " and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
						 }else {
							sql = "\n select distinct salesrep_id,salesrep_code,salesrep_desc \n"
								+ "from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
							sql += " and salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","");
						 }	
					
					 /*
						ประเภทขาย Customer_Category   + ภาคตามพนักงานขายย Sales_Channel ==>  พนักงานขาย Salesrep_id
						ดิวิชั่น   Division              + ภาคตามพนักงานขาย Sales_Channel  ==>  พนักงานขาย Salesrep_id	
						ภาคตามพนักงานขาย Sales_Channel  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
						ภาคตามพนักงานขาย Sales_Channel  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_id
						xx
						ประเภทขาย Customer_Category   + ภาคตามการดูแล SALES_ZONE ==>  พนักงานขายย Salesrep_id
						ดิวิชั่น   Division              + ภาคตามการดูแล  SALES_ZONE  ==>  พนักงานขาย Salesrep_id	
						ภาคตามการดูแล SALES_ZONE  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id
						ภาคตามการดูแล SALES_ZONE  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_id
					 */
					}else if(filterBean.getCurrCondNo().equals("3")){
						
						//ประเภทขาย Customer_Category   + ภาคตามพนักงานขายย Sales_Channel ==>  พนักงานขาย Salesrep_id
						if (filterBean.getCondType1().equalsIgnoreCase("Customer_Category") && filterBean.getCondType2().equalsIgnoreCase("Sales_Channel")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.Customer_Category in ("+AUtils.converToText("Customer_Category", filterBean.getCondCode1()) + ")  \n"+
							" and t1.Sales_Channel in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ")  \n"+
							" and substr(u.salesrep_code,2,1) in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ")  \n"+
							" and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(filterBean.getCondCode1())+"%'  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
							//ประเภทขาย Customer_Category   + ภาคตามการดูแล SALES_ZONE ==>  พนักงานขาย Salesrep_id
						}else if (filterBean.getCondType1().equalsIgnoreCase("Customer_Category") && filterBean.getCondType2().equalsIgnoreCase("SALES_ZONE")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and u.salesrep_id = z.salesrep_id \n"+ 
							" and t1.Customer_Category in ("+AUtils.converToText("Customer_Category", filterBean.getCondCode1()) + ")  \n"+
							" and z.zone in ("+AUtils.converToText("SALES_ZONE", filterBean.getCondCode2()) + ")  \n"+
							" and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(filterBean.getCondCode1())+"%'  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
							//ดิวิชั่น   Division           + ภาคตามพนักงานขาย Sales_Channel  ==>  พนักงานขาย Salesrep_id	
						}else if (filterBean.getCondType1().equalsIgnoreCase("Division") && filterBean.getCondType2().equalsIgnoreCase("Sales_Channel")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.division in ("+AUtils.converToText("Division", filterBean.getCondCode1()) + ") \n"+
							" and t1.Sales_Channel in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ") \n"+
							" and substr(u.salesrep_code,2,1) in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode2()) + ")  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
							//ดิวิชั่น   Division           + ภาคตามการดูแล SALES_ZONE  ==>  พนักงานขาย Salesrep_id	
						}else if (filterBean.getCondType1().equalsIgnoreCase("Division") && filterBean.getCondType2().equalsIgnoreCase("SALES_ZONE")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and u.salesrep_id = z.salesrep_id \n"+ 
							" and t1.division in ("+AUtils.converToText("Division", filterBean.getCondCode1()) + ") \n"+
							" and z.zone in ("+AUtils.converToText("SALES_ZONE", filterBean.getCondCode2()) + ") \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
						//ภาคตามพนักงานขาย Sales_Channel  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id	
						}else if (filterBean.getCondType1().equalsIgnoreCase("Sales_Channel") && filterBean.getCondType2().equalsIgnoreCase("Customer_Category")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.Sales_Channel in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ") \n"+
							" and t1.Customer_Category in ("+AUtils.converToText("Customer_Category", filterBean.getCondCode2()) + ")  \n"+
							" and substr(u.salesrep_code,2,1) in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ")  \n"+
							" and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(filterBean.getCondCode2())+"%'  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
						
							//ภาคตามการดูแล SALES_ZONE  + ประเภทขาย Customer_Category   ==>  พนักงานขาย Salesrep_id	
						}else if (filterBean.getCondType1().equalsIgnoreCase("SALES_ZONE") && filterBean.getCondType2().equalsIgnoreCase("Customer_Category")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and u.salesrep_id = z.salesrep_id \n"+ 
							" and z.zone in ("+AUtils.converToText("SALES_ZONE", filterBean.getCondCode1()) + ") \n"+
							" and t1.Customer_Category in ("+AUtils.converToText("Customer_Category", filterBean.getCondCode2()) + ")  \n"+
							" and u.salesrep_code like '"+AUtils.getCustomerCatagoryFlag(filterBean.getCondCode2())+"%'  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
						//ภาคตามพนักงานขาย Sales_Channel  + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_i
						}else if (filterBean.getCondType1().equalsIgnoreCase("Sales_Channel") && filterBean.getCondType2().equalsIgnoreCase("Division")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 \n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and t1.Sales_Channel in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ") \n"+
							" and t1.division in ("+AUtils.converToText("Division", filterBean.getCondCode2()) + ")  \n"+
							" and substr(u.salesrep_code,2,1) in ("+AUtils.converToText("Sales_Channel", filterBean.getCondCode1()) + ")  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
							
							//ภาคตามการดูแล SALES_ZONE   + ดิวิชั่น Division               ==>  พนักงานขาย Salesrep_i
						}else if (filterBean.getCondType1().equalsIgnoreCase("SALES_ZONE") && filterBean.getCondType2().equalsIgnoreCase("Division")){
							sql = "\n select distinct u.salesrep_id,u.salesrep_code,u.salesrep_desc \n"+
							" from XXPENS_BI_MST_SALESREP u, xxpens_bi_sales_analysis_v t1 ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z\n"+
							" where u.salesrep_code is not null and t1.salesrep_id = u.salesrep_id \n"+ 
							" and u.salesrep_id = z.salesrep_id \n"+ 
							" and z.zone in ("+AUtils.converToText("SALES_ZONE", filterBean.getCondCode1()) + ") \n"+
							" and t1.division in ("+AUtils.converToText("Division", filterBean.getCondCode2()) + ")  \n"+
							" and u.salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
						}else {
							sql = "select distinct salesrep_id,salesrep_code,salesrep_desc \n from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
							sql +=" and salesrep_code not like 'C%'  \n";
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","");
						}
					
					}else {
						sql = " select distinct salesrep_id,salesrep_code,salesrep_desc from XXPENS_BI_MST_SALESREP where salesrep_code is not null \n";
						sql +=" and salesrep_code not like 'C%'  \n";
						//Filter By Role User
						sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Salesrep_id","u.");
					}
					
					if(!Utils.isNull(code).equals("")){
						sql += " and salesrep_code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and salesrep_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by salesrep_code \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("salesrep_id"),rs.getString("salesrep_code"),rs.getString("salesrep_desc")));
					}	
					
				//กลุ่มร้านค้า(Customer_Group) --> ร้านค้า(Customer_id)
				}else if("Customer_id".equalsIgnoreCase(currCondType)){
					if(filterBean.getCurrCondNo().equals("2")){
						//Cond 1
						 if (filterBean.getCondType1().equalsIgnoreCase("Customer_Group")){
							sql = "select distinct u.customer_id,u.customer_code,u.customer_desc "+
							      " from XXPENS_BI_MST_CUSTOMER u, xxpens_bi_sales_analysis_v t1 \n"+
							      " where u.customer_id is not null and t1.customer_id=u.customer_id \n"+ 
							      " and t1.customer_group in ("+AUtils.converToText("Customer_Group", filterBean.getCondCode1()) + ") \n";
					
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Customer_id","u.");
						 }else{
							sql = "select u.customer_id,u.customer_code,u.customer_desc from XXPENS_BI_MST_CUSTOMER u where u.customer_code is not null \n";	
						 }
					}else{
                        sql = "select u.customer_id,u.customer_code,u.customer_desc from XXPENS_BI_MST_CUSTOMER u where u.customer_code is not null \n";	
					}
					
					if(!Utils.isNull(code).equals("")){
						sql += " and u.customer_code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and u.customer_desc LIKE '%"+desc+"%' \n";
					}
					sql += "and rownum <="+AConstants.MAX_SHOW_CUSTOMER+" \n";
					
					sql += "order by u.customer_code \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("customer_id"),rs.getString("customer_code"),rs.getString("customer_code")+"-"+rs.getString("customer_desc")));
					}
				}else if("Sales_Channel".equalsIgnoreCase(currCondType)){
			
					sql = "select sales_channel_no,sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL where sales_channel_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						sql += " and sales_channel_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and sales_channel_desc LIKE '%"+desc+"%' \n";
					}
					
					//Filter By Role User
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Sales_Channel","");
					
					sql += "order by sales_channel_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("sales_channel_no"),rs.getString("sales_channel_no"),rs.getString("sales_channel_desc")));
					}	
					
				}else if("Customer_Category".equalsIgnoreCase(currCondType)){
					sql = "select cust_cat_no,cust_cat_desc from XXPENS_BI_MST_CUST_CAT where cust_cat_no is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and cust_cat_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and cust_cat_desc LIKE '%"+desc+"%' \n";
					}
					//Filter By Role User
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Customer_Category","");
					
					sql += "order by cust_cat_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("cust_cat_no"),rs.getString("cust_cat_no"),rs.getString("cust_cat_desc")));
					}
				}else if("Division".equalsIgnoreCase(currCondType)){
					sql = "select div_no,div_desc from XXPENS_BI_MST_DIVISION where div_no is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and div_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and div_desc LIKE '%"+desc+"%' \n";
					}
					//Filter By Role User
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Division","");
					
					sql += "order by div_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("div_no"),rs.getString("div_no"),rs.getString("div_desc")));
					}	

				//กลุ่มร้านค้า
				}else if("Customer_Group".equalsIgnoreCase(currCondType)){
					sql = "select cust_group_no,cust_group_desc from XXPENS_BI_MST_CUST_GROUP where cust_group_no is not null ";
					if(!Utils.isNull(code).equals("")){
						sql += " and cust_group_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and cust_group_desc LIKE '%"+desc+"%' \n";
					}
					sql += "order by cust_group_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("cust_group_no"),rs.getString("cust_group_no"),rs.getString("cust_group_desc")));
					}
				
				}else if("Brand".equalsIgnoreCase(currCondType)){
					if(filterBean.getCurrCondNo().equals("2")){
						//Cond 1
						 if (filterBean.getCondType1().equalsIgnoreCase("Brand_Group")){
							
							sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
							sql += " and brand_no in ( \n" +
									" select distinct brand_no  from XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n" +
									"   and brand_group_no in ("+AUtils.converToText("Brand_Group", filterBean.getCondCode1())+") \n"+
									" ) \n ";
						 }else{
							 sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
						 }
					}else{
						sql = "select brand_no,brand_desc from XXPENS_BI_MST_BRAND where brand_no is not null \n";
					}
					
					//Condition
					if(!Utils.isNull(code).equals("")){
						sql += " and brand_no like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and brand_desc LIKE '%"+desc+"%' \n";
					}
					//Filter By Role User
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Brand","");
					sql += "order by brand_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("brand_no"),rs.getString("brand_no"),rs.getString("brand_desc")));
					}
					
				}else if("Brand_Group".equalsIgnoreCase(currCondType)){
					
	                sql = "select distinct brand_group_no,brand_group_desc  from XXPENS_BI_MST_BRAND_GROUP where brand_group_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						if(code.indexOf(",") > -1){
							sql += " and brand_group_no in ("+AUtils.converToText("Brand", code) +") \n";
						}
						else{
							sql += " and brand_group_no like '"+code+"%' \n";
						}
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and brand_group_desc LIKE '%"+desc+"%' \n";
					}
					
					/** filter by user **/
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Brand","");
					
					sql += "order by brand_group_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("brand_group_no"),rs.getString("brand_group_no"),rs.getString("brand_group_desc")));
					}
	            }else if("SUBBRAND".equalsIgnoreCase(currCondType)){
					
	                sql = "select distinct subbrand_no,subbrand_desc  from XXPENS_BI_MST_SUBBRAND where subbrand_no is not null \n";
					
					if(!Utils.isNull(code).equals("")){
						if(code.indexOf(",") > -1){
							sql += " and subbrand_no in ("+AUtils.converToText("SubBrand", code) +") \n";
						}
						else{
							sql += " and subbrand_no like '"+code+"%' \n";
						}
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and subbrand_desc LIKE '%"+desc+"%' \n";
					}
					
					/** filter by user **/
					sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "Brand","");
					
					sql += "order by subbrand_no \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("subbrand_no"),rs.getString("subbrand_no"),rs.getString("subbrand_desc")));
					}
					//SALES ZONE
				 }else if("SALES_ZONE".equalsIgnoreCase(currCondType)){
					 sql = "select distinct ZONE,ZONE_NAME  from PENSBI.XXPENS_BI_MST_SALES_ZONE where ZONE is not null \n";
						
						if(!Utils.isNull(code).equals("")){
							if(code.indexOf(",") > -1){
								sql += " and ZONE in ("+AUtils.converToText("SALES_ZONE", code) +") \n";
							}
							else{
								sql += " and ZONE like '"+code+"%' \n";
							}
						}
						if(!Utils.isNull(desc).equals("")){
							sql += " and ZONE_NAME LIKE '%"+desc+"%' \n";
						}
						
						/** filter by user **/
						sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "SALES_ZONE","");
						
						sql += "order by ZONE \n";
						logger.debug("sql:"+sql);
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						while(rs.next()){
							no++;
							returnList.add(new DisplayBean(no,rs.getString("ZONE"),rs.getString("ZONE"),rs.getString("ZONE_NAME")));
						}
				}else if("Invoice_Date".equalsIgnoreCase(currCondType)){
					sql = "select invoice_date from XXPENS_BI_MST_INVOICE_DATE where invoice_date is not null ";
					if(!Utils.isNull(code).equals("")){
						Date date = DateUtil.parseToBudishDate(code, DateUtil.DD_MM_YYYY_WITH_SLASH);
						String chrisDateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql += " and invoice_date = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
					}
					if(!Utils.isNull(desc).equals("")){
						Date date = DateUtil.parseToBudishDate(code, DateUtil.DD_MM_YYYY_WITH_SLASH);
						String chrisDateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql += " and invoice_date = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
					}
					sql +="order by invoice_date \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						String dateStr = DateUtil.stringValue(rs.getDate("invoice_date",Calendar.getInstance(Locale.US)), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th);
						returnList.add(new DisplayBean(no,dateStr,dateStr,dateStr));
					}
				}else if("SALES_ORDER_DATE".equalsIgnoreCase(currCondType)){
					sql = "select ORDER_DATE from XXPENS_BI_MST_ORDER_DATE where ORDER_DATE is not null ";
					if(!Utils.isNull(code).equals("")){
						Date date = DateUtil.parseToBudishDate(code, DateUtil.DD_MM_YYYY_WITH_SLASH);
						String chrisDateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql += " and ORDER_DATE = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
					}
					if(!Utils.isNull(desc).equals("")){
						Date date = DateUtil.parseToBudishDate(code, DateUtil.DD_MM_YYYY_WITH_SLASH);
						String chrisDateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql += " and ORDER_DATE = to_date('"+chrisDateStr+"','dd/mm/yyyy') \n";
					}
					sql +="order by ORDER_DATE \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						String dateStr = DateUtil.stringValue(rs.getDate("ORDER_DATE",Calendar.getInstance(Locale.US)), DateUtil.DD_MM_YYYY_WITH_SLASH, DateUtil.local_th);
						returnList.add(new DisplayBean(no,dateStr,dateStr,dateStr));
					}
				}else if("Province".equalsIgnoreCase(currCondType)){
					sql = "select * from XXPENS_BI_MST_PROVINCE where province is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and province like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and province LIKE '%"+desc+"%' \n";
					}
					sql += "order by province \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("province"),rs.getString("province"),rs.getString("province")));
					}
				}else if("AMPHOR".equalsIgnoreCase(currCondType)){
					
					if(filterBean.getCurrCondNo().equals("2")){
						//Cond 1
						 if (filterBean.getCondType1().equalsIgnoreCase("Province")){
							sql = "select distinct u.* "+
							      " from XXPENS_BI_MST_AMPHOR u, xxpens_bi_sales_analysis_v t1 \n"+
							      " where u.amphor is not null and t1.amphor = u.amphor \n"+ 
							      " and t1.province in ("+AUtils.converToText("Province", filterBean.getCondCode1()) + ") \n";
					
							//Filter By Role User
							sql += SecurityHelper.genWhereSqlFilterByUserForSearchPopup(conn,user, "AMPHOR","u.");
						 }else{
							 sql = "select u.* from XXPENS_BI_MST_AMPHOR u where u.amphor is not null \n";
						 }
					}else{
						sql = "select u.* from XXPENS_BI_MST_AMPHOR u where u.amphor is not null \n";	
					}

					if(!Utils.isNull(code).equals("")){
						sql += " and u.amphor like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and u.amphor LIKE '%"+desc+"%' \n";
					}
					sql += "order by u.amphor \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("amphor"),rs.getString("amphor"),rs.getString("amphor")));
					}
				}else if("TAMBOL".equalsIgnoreCase(currCondType)){
					sql = "select * from XXPENS_BI_MST_TAMBOL WHERE tambol is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and tambol like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and tambol LIKE '%"+desc+"%' \n";
					}
					sql += "order by tambol \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("tambol"),rs.getString("tambol"),rs.getString("tambol")));
					}
				}
				
				else if("SALES_ORDER_NO".equalsIgnoreCase(currCondType)){
					sql = "select  DISTINCT  SALES_ORDER_NO from XXPENS_BI_SALES_ANALYSIS WHERE SALES_ORDER_NO is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and SALES_ORDER_NO like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and SALES_ORDER_NO LIKE '%"+desc+"%' \n";
					}
					sql += "order by SALES_ORDER_NO \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO"),rs.getString("SALES_ORDER_NO")));
					}
				}
				
				else if("INVOICE_NO".equalsIgnoreCase(currCondType)){
					sql = "select  DISTINCT  INVOICE_NO from XXPENS_BI_SALES_ANALYSIS WHERE INVOICE_NO is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and INVOICE_NO like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and INVOICE_NO LIKE '%"+desc+"%' \n";
					}
					sql += "order by INVOICE_NO \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO"),rs.getString("INVOICE_NO")));
					}
				}
				
				/** Over Display  show 100 receord **/
				else if("SHIP_TO_SITE_USE_ID".equalsIgnoreCase(currCondType)){
					sql = "select  * from XXPENS_BI_MST_CUST_SHIP_ADDR WHERE CUSTOMER_SHIP_TO_ADDRESS is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and CUSTOMER_SHIP_TO_ADDRESS like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and CUSTOMER_SHIP_TO_ADDRESS LIKE '%"+desc+"%' \n";
					}
					sql += " and rownum <= 100 \n";
					sql += "order by CUSTOMER_SHIP_TO_ADDRESS \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("SHIP_TO_SITE_USE_ID"),rs.getString("CUSTOMER_SHIP_TO_ADDRESS")));
					}
				}
				/** Over Display  show 100 receord **/
				else if("BILL_TO_SITE_USE_ID".equalsIgnoreCase(currCondType)){
					sql = "select  * from XXPENS_BI_MST_CUST_BILL_ADDR WHERE CUSTOMER_BILL_TO_ADDRESS is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and CUSTOMER_BILL_TO_ADDRESS like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and CUSTOMER_BILL_TO_ADDRESS LIKE '%"+desc+"%' \n";
					}
					sql += " and rownum <= 100 \n";
					sql += "order by CUSTOMER_BILL_TO_ADDRESS \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("BILL_TO_SITE_USE_ID"),rs.getString("CUSTOMER_BILL_TO_ADDRESS")));
					}
				}
				
				//Organization 
				else if("Organization_id".equalsIgnoreCase(currCondType)){
					sql = "select  * from XXPENS_BI_MST_ORGANIZATION WHERE Organization_code is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and Organization_code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and Organization_code LIKE '%"+desc+"%' \n";
					}
					sql += "order by Organization_code \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("Organization_id"),rs.getString("Organization_code"),rs.getString("Organization_code")));
					}
				}
				
				//Order Type
				else if("Order_type_id".equalsIgnoreCase(currCondType)){
					sql = "select  * from XXPENS_BI_MST_ORDER_TYPE WHERE Order_type_id is not null \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and order_type_id like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and order_type_name LIKE '%"+desc+"%' \n";
					}
					sql += "order by order_type_name \n";
					
					if(debug)logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("Order_type_id"),rs.getString("Order_type_id"),rs.getString("Order_type_name")+"-"+rs.getString("Order_type_cat")));
					}
				}
				
				//Customer_class_code (customerType)
				else if("CUSTOMER_CLASS_CODE".equalsIgnoreCase(currCondType)){
					sql = "select  * from apps.xxpens_ar_cust_class_v WHERE ENABLED_FLAG ='Y' \n";
					if(!Utils.isNull(code).equals("")){
						sql += " and code like '"+code+"%' \n";
					}
					if(!Utils.isNull(desc).equals("")){
						sql += " and description LIKE '%"+desc+"%' \n";
					}
					sql += "order by code \n";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						no++;
						returnList.add(new DisplayBean(no,rs.getString("code"),rs.getString("code"),rs.getString("description")));
					}
				}
	
		    //debug sql to temp_file
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp/sql.sql", sql.toString());
			}
			}catch(Exception e){
			   throw e;
			}finally{
				if(conn != null){
					conn.close();conn=null;
				}
				if(ps != null){
				   ps.close();ps= null;
				}
				if(rs != null){
				   rs.close();rs=null;
				}
			}
			return returnList;
	}

}
