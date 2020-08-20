package com.isecinc.pens.web.salestarget;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.buf.C2BConverter;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.UserUtils;
import com.pens.util.Utils;

public class SalesTargetTTUtils {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
		initPeriodAllYear(3, 4);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * setAccess
	 * @param o
	 * @return
	 */
	public static SalesTargetBean setAccess(SalesTargetBean o,User user,String pageName){
		//Set By Role
		//MKT
		if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.ADMIN,User.MKT}) && pageName.equalsIgnoreCase(SalesTargetConstants.PAGE_MKT_TT) ){
			//CanSet
			if(  Utils.isNull(o.getStatus()).equals("") 
			  || Utils.statusInCheck(o.getStatus(),new String[]{SalesTargetConstants.STATUS_OPEN,SalesTargetConstants.STATUS_REJECT})
			 ){
				o.setCanSet(true);
			}
			//CanPost
			if(  Utils.isNull(o.getStatus()).equals("") 
			   || Utils.statusInCheck(o.getStatus(),new String[]{SalesTargetConstants.STATUS_OPEN,SalesTargetConstants.STATUS_REJECT})
			 ){
				o.setCanPost(true);
			}
			//check line readonly
			//logger.debug("status["+o.getStatus()+"]in(Post,Accept]");
			if(Utils.statusInCheck(o.getStatus(),new String[]{SalesTargetConstants.STATUS_POST,SalesTargetConstants.STATUS_ACCEPT})){
				o.setLineReadonly("readonly");
				o.setLineStyle("disableText");
				o.setLineNumberStyle("disableNumber");
			}else{
				o.setLineReadonly("");
				o.setLineStyle("normalText");
				o.setLineNumberStyle("enableNumber");
			}
			//logger.debug("itemCode:"+o.getItemCode()+",status["+o.getStatus()+"],lineReadonly["+o.getLineReadonly()+"]");
		//TTSUPER
		}else if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.ADMIN,User.TTSUPER})  && pageName.equalsIgnoreCase(SalesTargetConstants.PAGE_TTSUPER)){
			if(   Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_POST )
	           || Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_UN_ACCEPT )	
			 ){
			  o.setCanSet(true);
			  o.setCanAccept(true);
			  o.setCanReject(true);
			  
			}
			 o.setCanExport(true);
			 o.setLineReadonly("readonly");
			 o.setLineStyle("disableText");
			 o.setLineNumberStyle("disableNumber");
			 o.setLineRejectReasonStyle("disableText");
			 if(o.isCanReject()){
				 o.setLineRejectReasonStyle("normalText");
			 }
			 //logger.debug("item:"+o.getItemCode()+",status["+o.getStatus()+"],lineReadonly["+o.getLineReadonly()+"]CanReject["+o.isCanReject()+"]CanPost["+o.isCanPost()+"]");
	    //TTMGR
		}else if ( UserUtils.userInRoleSalesTarget(user,new String[]{User.ADMIN,User.TTMGR})  && pageName.equalsIgnoreCase(SalesTargetConstants.PAGE_TTMGR)){
			
			if( Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_ACCEPT)){
			    o.setCanFinish(true);
			    o.setCanUnAccept(true);
			}
			
			o.setLineReadonly("readonly");
			o.setLineStyle("disableText");
			o.setLineNumberStyle("disableNumber");
			if( Utils.isNull(o.getStatus()).equals(SalesTargetConstants.STATUS_REJECT)){
				o.setLineRejectReasonStyle("errorDisableText");
				o.setLineStatusStyle("errorDisableTextCenter");
			}
		}
		//logger.debug("canPost:"+o.isCanSet());
		return o;
	}
	
	public static List<PopupBean> initPeriod(Connection conn){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		PopupBean item = new PopupBean();
		SalesTargetBean period = null;
		try{
			int day = cal.get(Calendar.DAY_OF_MONTH);
			//if(day >1){ //For TEST
			
			/*if(day>=27){ //prod
				//Next Month +2
				item = new PopupBean();
				cal.add(Calendar.MONTH, 2);//Current+1
				periodName =  Utils.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
				period = getPeriodList(conn,periodName).get(0);//get Period View
				item.setKeyName(periodName);
				item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
				monthYearList.add(item);
			}*/
			
			//Next Month
			item = new PopupBean();
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 3);//Current+1
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
			//Next Month
			item = new PopupBean();
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 2);//Current+1
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
			//Next Month
			item = new PopupBean();
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);//Current+1
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
			//Current Month
			item = new PopupBean();
			cal = Calendar.getInstance();
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
			//Prev Month
			item = new PopupBean();
			cal.add(Calendar.MONTH, -1);//Current-1
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
			
		/*	//Fortest 
			//Prev Month
			item = new PopupBean();
			cal.add(Calendar.MONTH, -5);//Current-5
			periodName =  Utils.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			*/
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	 return monthYearList;
	}
	public static List<PopupBean> initPeriodTT(Connection conn){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		PopupBean item = new PopupBean();
		SalesTargetBean period = null;
		try{
		
			//Next Month
			item = new PopupBean();
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);//Current+1
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
			//Current Month
			item = new PopupBean();
			cal = Calendar.getInstance();
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
			//Prev Month
			item = new PopupBean();
			cal.add(Calendar.MONTH, -1);//Current-1
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
			//Prev Month
			item = new PopupBean();
			cal.add(Calendar.MONTH, -2);//Current-2
			periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			
		/*	//Fortest 
			//Prev Month
			item = new PopupBean();
			cal.add(Calendar.MONTH, -5);//Current-5
			periodName =  Utils.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);
			*/
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	 return monthYearList;
	}
	public static List<PopupBean> initPeriodAllYear(int prevMonth,int nextMonth) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return initPeriodAllYear(conn,prevMonth,nextMonth);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			conn.close();
		}
		
	}
	public static List<PopupBean> initPeriodAllYear(Connection conn,int prevMonthC,int nextMonthC) throws Exception{
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		PopupBean item = new PopupBean();
		cal = Calendar.getInstance();
		String startDate  = "";
		String endDate;
		Date currentMonthDate =null;
		Date prevMonthDate =null;
		Date nextMonthDate = null;
		try{
			//get Current Month
			currentMonthDate =cal.getTime();
			
		   //startMonth = current Month -prevMonth
			cal.add(Calendar.MONTH, (-1*prevMonthC));
			prevMonthDate = cal.getTime();
			
			logger.debug("prevMonthDate:"+prevMonthDate);
			logger.debug("currentMonthDate:"+currentMonthDate);
			
			int diffMonth = DateUtil.calcDiffMonthYear(prevMonthDate, currentMonthDate);
			logger.debug("diffMonth:"+diffMonth);
			
			//loop prevMonth to Current
			for(int m=0;m<diffMonth;m++){		
				//Next Month
				item = new PopupBean();
				cal.add(Calendar.MONTH, 1);//
				periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
				logger.debug("periodName:"+periodName);
				
				startDate = "01-"+DateUtil.stringValue(cal.getTime(),"MMM-yyyy").toUpperCase();
				endDate = cal.getMaximum(Calendar.DAY_OF_MONTH)+"-"+DateUtil.stringValue(cal.getTime(),"MMM-yyyy").toUpperCase();
				
				item.setKeyName(periodName);
				item.setValue(periodName+"|"+startDate +"|"+endDate);
				monthYearList.add(item);
			
			}
			
			//startMonth = current Month +nextMonth
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, nextMonthC);
			nextMonthDate = cal.getTime();
			
			logger.debug("currentMonthDate:"+currentMonthDate);
			logger.debug("nextMonthDate:"+nextMonthDate);
			diffMonth = DateUtil.calcDiffMonthYear(currentMonthDate, nextMonthDate);
			logger.debug("diffMonth:"+diffMonth);
			
			//loop start Current +nextMonth
			cal = Calendar.getInstance();//reset to Current Date
			for(int m=0;m<diffMonth;m++){		
				//Next Month
				item = new PopupBean();
				cal.add(Calendar.MONTH, 1);//
				periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
				logger.debug("periodName:"+periodName);
				
				startDate = "01-"+DateUtil.stringValue(cal.getTime(),"MMM-yyyy").toUpperCase();
				endDate = cal.getMaximum(Calendar.DAY_OF_MONTH)+"-"+DateUtil.stringValue(cal.getTime(),"MMM-yyyy").toUpperCase();
				
				item.setKeyName(periodName);
				item.setValue(periodName+"|"+startDate +"|"+endDate);
				monthYearList.add(item);
			}
			
		}catch(Exception e){
			throw e;
		}
	 return monthYearList;
	}
	
	public static List<SalesTargetBean> getPeriodList()throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getPeriodList(conn,"");
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	
	public static List<SalesTargetBean> getPeriodList(Connection conn,String periodName){
		SalesTargetBean s = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<SalesTargetBean> pos = new ArrayList<SalesTargetBean>();
		try{
			sql.append("\n  SELECT PERIOD_NAME,start_date,end_date,period_year,period_num,quarter_num" );
			sql.append("\n  from XXPENS_BI_MST_PERIOD M  ");
			if( !Utils.isNull(periodName).equals(""))
			  sql.append("\n  where period_name ='"+periodName+"'");

			//logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  s = new SalesTargetBean();
			  s.setPeriod(Utils.isNull(rst.getString("PERIOD_NAME")));
			  s.setStartDate(DateUtil.stringValue(rst.getDate("start_date"),DateUtil.DD_MMM_YYYY));
			  s.setEndDate(DateUtil.stringValue(rst.getDate("end_date"),DateUtil.DD_MMM_YYYY));
			  s.setTargetQuarter(Utils.isNull(rst.getString("quarter_num")));
			  pos.add(s);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return pos;
	}
	
	/**
	 * getProduct
	 * @param salesChannelNo
	 * @return
	 * @throws Exception
	 */
	public static SalesTargetBean getProduct(String priceListId,String period,String custCatNo,String salesZone,String itemCode,String brand)throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getProduct(conn,priceListId,period,custCatNo,salesZone,itemCode,brand);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	
	public static SalesTargetBean getProduct(Connection conn,String priceListId,String period,String custCatNo,String salesZone,String itemCode,String brand){
		SalesTargetBean s = null;
		PreparedStatement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT M.INVENTORY_ITEM_ID ,M.INVENTORY_ITEM_CODE ,M.INVENTORY_ITEM_DESC" );
			/*sql.append("\n  ,(SELECT max(P.price) from xxpens_bi_mst_price_list P " +
					"where P.product_id =M.INVENTORY_ITEM_ID " +
					"and P.primary_uom_code ='Y' " +
					"and P.pricelist_id ="+priceListId+") as price");*/
			
			sql.append("\n  ,(SELECT max(P.unit_price) from apps.xxpens_om_price_list_v P " );
			sql.append("\n   where P.INVENTORY_ITEM_ID =M.INVENTORY_ITEM_ID " );
			//witty edit:21/10/2019 (get only active )
			sql.append("\n   and P.end_date_active is null ");
			sql.append("\n   and P.list_header_id ="+priceListId+") as price");
			
		//	sql.append("\n  ,(apps.xxpens_bi.Get_Sales_Avg('"+period+"','"+custCatNo+"',"+salesrepId+","+customerId+",INVENTORY_ITEM_ID,12)) as amt_12 ");
	    //	sql.append("\n  ,(apps.xxpens_bi.Get_Sales_Avg('"+period+"','"+custCatNo+"',"+salesrepId+","+customerId+",INVENTORY_ITEM_ID,3)) as amt_3 ");
			sql.append("\n  ,P.SUM3 as amt_3  ");
			sql.append("\n  ,P.SUM12 as amt_12  ");
			sql.append("\n  from XXPENS_BI_MST_ITEM M  ");
			sql.append("\n  LEFT OUTER JOIN ( ");
			sql.append("\n   SELECT INVENTORY_ITEM_ID ,SUM(V.sum3) as SUM3,sum(V.SUM12) as SUM12 ");
			sql.append("\n   FROM apps.XXPENS_BI_MST_SALES_AVG_V V ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n   WHERE V.PERIOD ='"+period+"'");
			sql.append("\n   AND V.CUSTOMER_CATEGORY ='"+custCatNo+"'");
			sql.append("\n   AND Z.ZONE ='"+salesZone+"'");
			sql.append("\n   AND V.salesrep_id = Z.salesrep_id");
			sql.append("\n   GROUP BY INVENTORY_ITEM_ID ");
			sql.append("\n  ) P ON M.INVENTORY_ITEM_ID = P.INVENTORY_ITEM_ID  ");
			sql.append("\n  where M.INVENTORY_ITEM_CODE ='"+itemCode+"' ");
			// 504 ,821 ,833 No check dup in page
			if( !brand.equalsIgnoreCase("504") 
			   && !brand.equalsIgnoreCase("821") 
			   && !brand.equalsIgnoreCase("833")
			   && !brand.equalsIgnoreCase("505")
			   ){
			   sql.append("\n  AND M.INVENTORY_ITEM_CODE LIKE '"+brand+"%' ");
			}
			
			logger.debug("sql:"+sql);
			stmt = conn.prepareStatement(sql.toString());
			rst = stmt.executeQuery();
			if (rst.next()) {
			  s= new SalesTargetBean();
			  s.setItemCode(Utils.isNull(rst.getString("INVENTORY_ITEM_CODE")));
			  s.setItemId(Utils.isNull(rst.getString("INVENTORY_ITEM_ID")));
			  s.setItemName(Utils.isNull(rst.getString("INVENTORY_ITEM_DESC")));
			  //getPrice
			  s.setPrice(Utils.decimalFormat(rst.getDouble("price"),Utils.format_current_2_disgit)); 
			  //getAvgAmount 
			  s.setOrderAmt12Month(Utils.decimalFormat(rst.getDouble("amt_12"),Utils.format_current_2_disgit));
			  s.setOrderAmt3Month(Utils.decimalFormat(rst.getDouble("amt_3"),Utils.format_current_2_disgit));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return s;
	}
	
	public static String getPrice(Connection conn,String priceListId,String itemId){
		String price = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT price from xxpens_bi_mst_price_list M  ");
			sql.append("\n  where product_id ='"+itemId+"' ");
			sql.append("\n  and primary_uom_code ='Y' ");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				price =Utils.decimalFormat(rst.getDouble("price"),Utils.format_current_2_disgit);
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return price;
	}
	
	/**
	 * apps.xxpens_bi.Get_Sales_Avg(p_period ,
                           p_cust_cat,
                           p_sale_id,
                           p_cust_id,
                           p_item_id,
                           p_month)
	 */
	
	public static String getAvgAmount(Connection conn,String period,String custCatNo,String customerId,String salesrepId,String itemId,int month){
		String amount = "";
		PreparedStatement stmt = null;
		ResultSet rst = null;
		try{
			String sql = " select (apps.xxpens_bi.Get_Sales_Avg(?,?,?,?,?,?)) as amt from dual";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, period);
			stmt.setString(2, custCatNo);
			stmt.setBigDecimal(3, new BigDecimal(salesrepId));
			stmt.setBigDecimal(4, new BigDecimal(customerId));
			stmt.setBigDecimal(5, new BigDecimal(itemId));
			stmt.setInt(6, month);
			
			rst = stmt.executeQuery();
			if(rst.next()){
				amount = Utils.decimalFormat(rst.getDouble("amt"), Utils.format_current_2_disgit);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return amount;
	}
	
	/**
	 * getSalesrepId
	 * @param saleserpCode
	 * @return
	 * @throws Exception
	 */
	public static String getSalesrepId(String saleserpCode) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getSalesrepId(conn,saleserpCode);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	public static String getSalesrepId(Connection conn,String saleserpCode){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT SALESREP_ID from XXPENS_BI_MST_SALESREP M  ");
			sql.append("\n  where  SALESREP_CODE ='"+saleserpCode+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("SALESREP_ID"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
	public static String getBrandGroup(Connection conn,String brandNo){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT brand_group_no from XXPENS_BI_MST_BRAND_GROUP M  ");
			sql.append("\n  where  brand_no ='"+brandNo+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("brand_group_no"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
	public static String getBrandName(Connection conn,String brandNo){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT brand_desc from XXPENS_BI_MST_BRAND M  ");
			sql.append("\n  where  brand_no ='"+brandNo+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("brand_desc"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
	/**
	 * getPriceListId
	 * @param saleserpCode
	 * @return
	 * @throws Exception
	 */
	public static String getPriceListId(String zone,String custCatNo,User user) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getPriceListId(conn,zone,custCatNo,user);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	
	public static String getPriceListId(Connection conn,String zone,String custCatNo,User user){
		String value = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT PRICELIST_ID from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT M  ");
			sql.append("\n  where ZONE ='"+zone+"'");
			sql.append("\n  and  CUST_CAT_NO ='"+custCatNo+"' \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				value =Utils.isNull(rst.getString("PRICELIST_ID"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return value;
	}
	/**
	 * getSalesChannelName
	 * @param salesChannelNo
	 * @return
	 * @throws Exception
	 */
	public static String getSalesChannelName(String salesChannelNo) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getSalesChannelName(conn,salesChannelNo);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	public static String getSalesChannelName(Connection conn,String salesChannelNo){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL M  ");
			sql.append("\n  where  sales_channel_no ='"+salesChannelNo+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("sales_channel_desc"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	//salesChannelNo  =salesZone
	public static String getDivision(Connection conn,String salesChannelNo){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT division from XXPENS_BI_MST_SALES_CHANNEL M  ");
			sql.append("\n  where  sales_channel_no ='"+salesChannelNo+"' \n");

			//logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("division"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
	public static String getCustomerGroup(Connection conn,String customerId){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT customer_group from xxpens_ar_cust_group_v M  ");
			sql.append("\n  where  cust_account_id ='"+customerId+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("customer_group"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
	public static String getCustName(String custCode) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return getCustName(conn,custCode);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	public static String getCustName(Connection conn,String custCode){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT short_name from XXPENS_BI_MST_CUST_SALES M  ");
			sql.append("\n  where  customer_code ='"+custCode+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("short_name"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
	public static List<PopupBean> searchCustCatNoMTList(String salesChannelNo) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return searchCustCatNoMTListModel(conn,salesChannelNo);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	  }
	 
	public static List<PopupBean> searchCustCatNoMTListModel(Connection conn,String salesChannelNo){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT distinct M.cust_cat_no,C.cust_cat_desc from XXPENS_BI_MST_CUST_CAT_MAP M ,XXPENS_BI_MST_CUST_CAT C  ");
			sql.append("\n  where 1=1 AND M.cust_cat_no = C.cust_cat_no ");
			if( !Utils.isNull(salesChannelNo).equals("")){
			  sql.append("\n  and m.sales_channel_no ='"+salesChannelNo+"'");
			}
			sql.append("\n  ORDER BY M.cust_cat_no asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setCustCatNo(Utils.isNull(rst.getString("cust_cat_no")));
				item.setCustCatDesc(Utils.isNull(rst.getString("cust_cat_desc")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public static List<PopupBean> searchCustCatNoTTList() throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return searchCustCatNoTTListModel(conn);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	  }
	 
	public static List<PopupBean> searchCustCatNoTTListModel(Connection conn){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT distinct C.cust_cat_no,C.cust_cat_desc from PENSBI.XXPENS_BI_MST_CUST_CAT C  ");
			sql.append("\n  where  C.cust_cat_no IN( 'ORDER - CREDIT SALES','ORDER - VAN SALES')");
			sql.append("\n  ORDER BY C.cust_cat_no asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setCustCatNo(Utils.isNull(rst.getString("cust_cat_no")));
				item.setCustCatDesc(Utils.isNull(rst.getString("cust_cat_desc")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public static List<PopupBean> searchSalesZoneTTList(User user,String salesChannelNo) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return searchSalesZoneTTListModel(conn,user,salesChannelNo);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	  }
	 
	public static List<PopupBean> searchSalesZoneTTListModel(Connection conn,User user,String salesZone){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct M.zone,M.zone_name from PENSBI.XXPENS_BI_MST_SALES_ZONE M   ");
			sql.append("\n  where 1=1  ");
			if( !Utils.isNull(salesZone).equals("")){
			  sql.append("\n  and m.zone ='"+salesZone+"'");
			}
			 sql.append("\n  and m.zone in(0,1,2,3,4,92)");
			//Filter By User Case Role TTSUPER,TTMGR : MKT(ALL)
			if ( !"admin".equalsIgnoreCase(user.getUserName()) && !UserUtils.userInRoleSalesTarget(user, new String[]{User.MKT})
					&& UserUtils.userInRoleSalesTarget(user,new String[]{User.TTSUPER,User.TTMGR}) ){
				 sql.append("\n  and m.zone in( ");
				 sql.append("\n  select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT ");
				 sql.append("\n  where  user_name='"+user.getUserName()+"'");
				 sql.append("\n  )");
			}
			sql.append("\n  ORDER BY M.zone asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesZone(Utils.isNull(rst.getString("zone")));
				item.setSalesZoneDesc(Utils.isNull(rst.getString("zone_name")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	public static List<PopupBean> searchSalesrepTTListAll(Connection conn,User user){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		try{
			sql.append("\n SELECT distinct S.code as salesrep_code,S.salesrep_id ");
			sql.append("\n from PENSBI.XXPENS_BI_MST_SALES_ZONE M ,apps.xxpens_salesreps_v S ");
			sql.append("\n where M.salesrep_id =S.salesrep_id ");
			sql.append("\n and M.zone in(0,1,2,3,4 )");
			//Filter By User Case Role TTSUPER,TTMGR : MKT(ALL)
			if ( !"admin".equalsIgnoreCase(user.getUserName())
				&& !UserUtils.userInRoleSalesTarget(user, new String[]{User.MKT})
				){
				 sql.append("\n  and M.zone in( ");
				 sql.append("\n  select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT ");
				 sql.append("\n  where  user_name='"+user.getUserName()+"'");
				 sql.append("\n  )");
			}
			sql.append("\n  ORDER BY S.code asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				 
				 //salesrepCode +=Utils.isNull(rst.getString("salesrep_code"))+",";
				 //salesrepId +=Utils.isNull(rst.getString("salesrep_id"))+",";
				 bean = new PopupBean();
				 bean.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				 bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				 
				 pos.add(bean);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	public static List<PopupBean> searchCustCatNoTTListModel(Connection conn,User user,String salesZone){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT distinct M.cust_cat_no,C.cust_cat_desc from XXPENS_BI_MST_CUST_CAT_MAP_TT M ,XXPENS_BI_MST_CUST_CAT C  ");
			sql.append("\n  where 1=1 AND M.cust_cat_no = C.cust_cat_no ");
			if( !Utils.isNull(salesZone).equals("")){
			  sql.append("\n  and m.sales_zone ='"+salesZone+"'");
			}
			if( !"admin".equalsIgnoreCase(user.getUserName()) 
				&& !UserUtils.userInRoleSalesTarget(user, new String[]{User.MKT})
			 ){
				sql.append("\n  and m.user_name ='"+user.getUserName()+"'");
			}
			sql.append("\n  ORDER BY M.cust_cat_no asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setCustCatNo(Utils.isNull(rst.getString("cust_cat_no")));
				item.setCustCatDesc(Utils.isNull(rst.getString("cust_cat_desc")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	
	
	
	public static List<PopupBean> searchSalesrepListAll() throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return searchSalesrepListAll(conn);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	  }
	public static List<PopupBean> searchSalesrepListAll(Connection conn){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		try{
			sql.append("\n  SELECT distinct M.salesrep_code,S.salesrep_id from XXPENS_BI_MST_CUST_SALES M ,XXPENS_BI_MST_SALESREP S ");
			sql.append("\n  where M.salesrep_code =S.salesrep_code ");
			sql.append("\n  ORDER BY M.salesrep_code asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				 
				 //salesrepCode +=Utils.isNull(rst.getString("salesrep_code"))+",";
				 //salesrepId +=Utils.isNull(rst.getString("salesrep_id"))+",";
				 bean = new PopupBean();
				 bean.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				 bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				 
				 pos.add(bean);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public static List<PopupBean> searchSalesrepListTT(User user,String salesChannelNo,String custCatNo,String salesZone) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchSalesrepListTT(conn,user,salesChannelNo,custCatNo,salesZone);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	public static List<PopupBean> searchSalesrepListTT(Connection conn,User user,String salesChannelNo,String custCatNo,String salesZone){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		try{	
			sql.append("\n  SELECT distinct S.code,S.salesrep_id ");
			sql.append("\n  from apps.xxpens_salesreps_v S ");
			sql.append("\n  ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n  where Z.salesrep_id =S.salesrep_id ");
			sql.append("\n  and S.region in('0','1','2','3','4')");
			sql.append("\n  and S.isactive ='Y'");
			sql.append("\n  and S.code not like 'SN%'");
			sql.append("\n  and S.salesrep_full_name not like '%ยกเลิก%' ");
			if( !Utils.isNull(custCatNo).equals("")){
				if("ORDER - CREDIT SALES".equalsIgnoreCase(custCatNo)){
			       sql.append("\n  and S.sales_channel_name ='Credit Sales'");
				}else if("ORDER - VAN SALES".equalsIgnoreCase(custCatNo)){
				   sql.append("\n  and S.sales_channel_name ='Van Sales'");
				}
			}
			if( !Utils.isNull(salesChannelNo).equals("")){
				sql.append("\n  and S.region ='"+salesChannelNo+"'");
			}
			if( !Utils.isNull(salesZone).equals("")){
				sql.append("\n  and Z.zone ='"+salesZone+"'");
			}
			//Filter By User Case Role TTSUPER,TTMGR : MKT(ALL)
			if ( !"admin".equalsIgnoreCase(user.getUserName()) && UserUtils.userInRoleSalesTarget(user,new String[]{User.TTSUPER,User.TTMGR}) ){
				 sql.append("\n  and Z.zone in( ");
				 sql.append("\n  select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT ");
				 sql.append("\n  where  user_name='"+user.getUserName()+"'");
				 sql.append("\n  )");
			}
			sql.append("\n  ORDER BY S.code asc \n");		
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				 bean = new PopupBean();
				 bean.setSalesrepCode(Utils.isNull(rst.getString("code")));
				 bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				 if(Utils.isNull(bean.getSalesrepCode()).length()>=4){
				   pos.add(bean);
				 }
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	 public static List<PopupBean> searchSalesChannelTTList(User user) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return searchSalesChannelTTListModel(conn,user);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	  }
	 
	public static List<PopupBean> searchSalesChannelTTListModel(Connection conn,User user){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.sales_channel_no ,S.sales_channel_desc ");
			sql.append("\n  from XXPENS_BI_MST_SALES_CHANNEL S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and S.sales_channel_no in(0,1,2,3,4) ");
			sql.append("\n  ORDER BY S.sales_channel_no asc \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesChannelNo(Utils.isNull(rst.getString("sales_channel_no")));
				item.setSalesChannelDesc(Utils.isNull(rst.getString("sales_channel_desc")));
				pos.add(item);
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public static List<PopupBean> searchSalesChannelListModelByUserName(Connection conn,String userName){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT distinct M.sales_channel_no ,S.sales_channel_desc from XXPENS_BI_MST_CUST_SALES M ,XXPENS_BI_MST_SALES_CHANNEL S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and M.sales_channel_no = S.sales_channel_no  ");
			if( !Utils.isNull(userName).equals("")){
				sql.append("\n  and M.user_name ='"+userName+"'");
			}
			sql.append("\n  ORDER BY M.sales_channel_no asc \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesChannelNo(Utils.isNull(rst.getString("sales_channel_no")));
				item.setSalesChannelDesc(Utils.isNull(rst.getString("sales_channel_desc")));
				pos.add(item);
				
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public static List<PopupBean> searchCustomerListByUserName(Connection conn,User user){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT distinct M.customer_code ,M.short_name from XXPENS_BI_MST_CUST_SALES M  ");
			sql.append("\n  where  M.user_name ='"+user.getUserName()+"'");
			sql.append("\n  ORDER BY M.customer_code asc \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
				item.setCustomerName(Utils.isNull(rst.getString("short_name")));
				pos.add(item);
				
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	 public static List<PopupBean> searchBrand(PopupBean c,String operation) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return searchBrandModel(conn,c, operation);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	  }
	 
	  public static List<PopupBean> searchBrandModel(Connection conn,PopupBean c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupBean> pos = new ArrayList<PopupBean>();
			StringBuilder sql = new StringBuilder();
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* from XXPENS_BI_MST_BRAND M");
				sql.append("\n  where 1=1  ");
				if("equals".equals(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and brand_no ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and brand_desc = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and brand_no LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and brand_desc LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				sql.append("\n  ORDER BY brand_no asc \n");
				
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupBean item = new PopupBean();
					item.setNo(no);
					item.setBrandId(Utils.isNull(rst.getString("brand_no")));
					item.setBrandName(Utils.isNull(rst.getString("brand_desc")));
					pos.add(item);
					
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
}
