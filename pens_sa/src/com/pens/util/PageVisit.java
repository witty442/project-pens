package com.pens.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.bean.PageVisitBean;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.salestarget.SalesTargetBean;

public class PageVisit {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void processPageVisit(HttpServletRequest request,String pageName){
		Connection conn = null;
		Calendar cal = Calendar.getInstance();
		try{
			User user = (User)request.getSession().getAttribute("user");
			conn = DBConnection.getInstance().getConnectionApps();
			
			//Get PageName Thai from SystemProperty
			String pageNameTH = SystemProperties.getCaption(pageName,Locale.getDefault());
			
			//get month ,year from currentDate
			String month = (""+(cal.get(Calendar.MONTH)+1));
			month = month.length()==1?"0"+(month):month;
			
			int year = cal.get(Calendar.YEAR);
			int update = updatePageVisit(conn, user.getUserName(), pageNameTH,month,year);
			if(update==0){
				insertPageVisit(conn, user.getUserName(), pageNameTH,month,year);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception ee){}
		}
	}
	
	private static int insertPageVisit(Connection conn ,String userName,String pageName,String month,int year) throws Exception {
		int result = 0;
		PreparedStatement ps = null;
		int index = 0;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO pensbi.page_visit( \n");
			sql.append(" user_name,page_name,page_count,update_date,month,year) \n"); //6 ) \n");//22
			sql.append(" VALUES (?,?,1,?,?,?) \n");//
			logger.debug("SQL:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, Utils.isNull(userName));//1
			ps.setString(++index, Utils.isNull(pageName));//1
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, Utils.isNull(month));
			ps.setInt(++index, year);
			
			result = ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	private static int updatePageVisit(Connection conn ,String userName,String pageName,String month,int year) throws Exception {
		int result = 0;
		PreparedStatement ps = null;
		int index = 0;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" update pensbi.page_visit \n");
			sql.append(" set page_count = (page_count+1) ,update_date =?\n");
			sql.append(" where user_name = ? \n"); 
			sql.append(" and page_name = ? \n");
			sql.append(" and month =?  and year =? \n");
			
			logger.debug("SQL:"+sql);
			ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, Utils.isNull(userName));
			ps.setString(++index, Utils.isNull(pageName));
			ps.setString(++index, Utils.isNull(month));
			ps.setInt(++index, year);
			result = ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return result;
	}
	
	public static StringBuffer search(Connection conn,HttpServletRequest request,PageVisitBean pageVisitBean, boolean excel) throws Exception{
		StringBuffer h = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean found = false;
		try{
			String sql  = "\n select * from pensbi.page_visit where 1=1";
		       if( !pageVisitBean.getPageName().equals("")){
		    	   sql += "\n and page_name ='"+pageVisitBean.getPageName()+"'";
		       }
		       if( !Utils.isNull(pageVisitBean.getTargetMonth()).equals("")){
					sql +="\n and month = '"+Utils.isNull(pageVisitBean.getTargetMonth())+"'";
				}
				if( !Utils.isNull(pageVisitBean.getTargetYear()).equals("")){
					sql +="\n and year = '"+Utils.isNull(pageVisitBean.getTargetYear())+"'";
				}
		       sql += " order by page_name";
		       
		       System.out.println("sql \n"+sql);
		       ps = conn.prepareStatement(sql);
		       rs = ps.executeQuery();
		       
		       //Gen Haeder 
		       h.append("\n <table align='center' border='1' cellpadding='1' cellspacing='0' width='100%'> ");
		       h.append("\n <tr> ");
		       h.append("\n <th align='center'>User Name</th> ");
		       h.append("\n <th align='center'>PageName</th> ");
		       h.append("\n <th align='center'>Year</th> ");
		       h.append("\n <th align='center'>Month</th> ");
		       h.append("\n <th align='center'>Visit Page Count</th> ");
		       h.append("\n <th align='center'>Lastest Visit</th> ");
		       h.append("\n </tr> ");
	    		
		       while(rs.next()){
		    	   found = true;
		    	   h.append("\n <tr> ");
    			   h.append("\n <td align='center'>"+rs.getString("user_name")+"</td>");
    			   h.append("\n <td align='center'>"+rs.getString("page_name") +"</td>");
    			   h.append("\n <td align='center'>"+rs.getString("year") +"</td>");
    			   h.append("\n <td align='center'>"+rs.getString("month") +"</td>");
    			   h.append("\n <td align='center'>"+rs.getString("page_count") +"</td>");
    			   h.append("\n <td align='center'>"+rs.getString("update_date") +"</td>");
    			   h.append("\n </tr>");
		       }
		    	
		       h.append("\n </table>");
		       
		       logger.debug("found:"+found);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			rs.close();
			ps.close();
		}
		return h;
	}
	
	public static List<String> initPageNameList(HttpServletRequest request) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> pageNameList = new ArrayList<String>();
		try{
			conn = DBConnection.getInstance().getConnection();
			String sql  = "select distinct page_name from pensbi.page_visit order by page_name";
		    ps = conn.prepareStatement(sql);
		    rs = ps.executeQuery();
		    while(rs.next()){
		    	pageNameList.add(rs.getString("page_name"));
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ps.close();
			rs.close();
		}
		return pageNameList;
	}
	
	public static List<PopupBean> initPeriod(Connection conn) throws Exception{
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		PopupBean item = new PopupBean();
		SalesTargetBean period = null;
		try{
			//Next Month
			/*item = new PopupBean();
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);//Current+1
			periodName =  Utils.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			period = getPeriodList(conn,periodName).get(0);//get Period View
			item.setKeyName(periodName);
			item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
			monthYearList.add(item);*/
			
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
		}finally{
			
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
	public static PageVisitBean convertCriteria(PageVisitBean o) throws Exception{
		logger.debug("startDate:"+o.getStartDate());
		Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MMM_YYYY);
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		//set Month
		String month =String.valueOf(c.get(Calendar.MONTH)+1);
		o.setTargetMonth(month.length()==1?"0"+month:month);
        //Set Year
		o.setTargetYear(c.get(Calendar.YEAR)+"");
		
		return o;
	}
}
