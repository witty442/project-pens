package com.isecinc.pens.web.salestarget;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.UserUtils;
import com.pens.util.Utils;

public class SalesTargetPDControlPage {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void prepareSearchSalesTargetPD(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		String zone= "";
		try{
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", initPeriodPD(conn));
			
			//Init Sale Zone List
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			List<PopupBean> salesZoneList_s = searchSalesZoneTTListModel(conn,user, zone);
			if(user.getUserName().equalsIgnoreCase("admin") || salesZoneList_s.size() >2){
				item = new PopupBean();
				item.setSalesZone("");
				item.setSalesZoneDesc("");
				salesZoneList.add(item);
				salesZoneList.addAll(salesZoneList_s);
			}else{
				salesZoneList.addAll(salesZoneList_s);
			}
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
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
			 sql.append("\n  and m.zone in(0,1,2,3,4 )");
			//Filter By User Case Role TTSUPER,TTMGR : MKT(ALL)
			if (isUserMapCustSalesTT(user)){
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
	//Return is set map :true
	public static boolean isUserMapCustSalesTT(User user){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		boolean isMap = false;
		try{
			sql.append("\n  SELECT count(*) as c from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT M  ");
			sql.append("\n  where user_name='"+user.getUserName()+"'");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnectionApps();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				if(rst.getInt("c")>0){
					isMap = true;
				}
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	 return isMap;
	}
	public static List<PopupBean> initPeriodPD(Connection conn){
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
}
