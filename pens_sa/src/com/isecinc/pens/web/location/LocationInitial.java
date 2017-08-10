package com.isecinc.pens.web.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.SAGenCondition;
import com.isecinc.pens.report.salesanalyst.helper.SAUtils;
import com.isecinc.pens.report.salesanalyst.helper.SecurityHelper;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

public class LocationInitial {
   
	/**
	 * WITTY
	 */
	protected static  Logger logger = Logger.getLogger("PENS");
	
	public   static Map<String,String> MONTH_MAP = new HashMap<String,String>();
	
	public static final String TYPE_SEARCH_DAY = "DAY";
	public static final String TYPE_SEARCH_MONTH = "MONTH";
	public static final String TABLE_VIEW = "XXPENS_BI_SALES_ANALYSIS_V";
	
	//For Prefix ColumnName CALL NO DUP 
	public static final String NO_DUP_PREFIX = "ND_";
	
	public static List<String> MULTI_SELECTION_LIST = new ArrayList<String>();
	SAGenCondition saGen = new SAGenCondition();
	SAUtils saU = new SAUtils();
	private static LocationInitial salesAnalystProcess;
	
	public static LocationInitial getInstance(){
		
		if(salesAnalystProcess == null){
			salesAnalystProcess = new LocationInitial();
			return salesAnalystProcess;
		}
		return salesAnalystProcess;
	}


	
	public  void initSession(HttpServletRequest requestWeb) {
		References r = null;
		HttpSession session = requestWeb.getSession(true);
		Connection conn = null;
		int i = 0;
		try{
			logger.debug("Initail Session ");
			conn = DBConnection.getInstance().getConnection();
			
			/** init TypeSearch ***/
			List<References> typeSearchList = new ArrayList<References>();
			r = new References("DAY","วัน");
			typeSearchList.add(r);
			r = new References("MONTH","เดือน");
			typeSearchList.add(r);
			//r = new References("QUARTER","ไตรมาส");
			//typeSearchList.add(r);
			//r = new References("YEAR","ปี");
			//typeSearchList.add(r);
			session.setAttribute("typeSearchList", typeSearchList);
			
			/** init year **/
			
			List<References> yearL = initYearList(conn," DESC");
			session.setAttribute("yearList", yearL);
			
			List<References> yearLSAC = initYearList(conn," ASC");
			session.setAttribute("yearListASC", yearLSAC);
			
			for(References year:yearL){
				String yearKey = year.getKey();
				String yearShow = year.getName();
				
				MONTH_MAP.put(yearKey+"01", "ม.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"02", "ก.พ. "+yearShow);
				MONTH_MAP.put(yearKey+"03", "มี.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"04", "เม.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"05", "พ.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"06", "มิ.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"07", "ก.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"08", "ส.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"09", "ก.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"10", "ต.ค. "+yearShow);
				MONTH_MAP.put(yearKey+"11", "พ.ย. "+yearShow);
				MONTH_MAP.put(yearKey+"12", "ธ.ค. "+yearShow);
			}
		
			/** init day **/
			List<References> dayList = new ArrayList<References>();
			for(i=1;i<=31;i++){
			   r = new References(""+i,""+i);
			   dayList.add(r);
			}
			session.setAttribute("dayList", dayList);

			//Remove Session RESULT
			session.setAttribute("RESULT", null);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			 	DBConnection.getInstance().closeConn(conn, null, null);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
	}
	
	public  List<References> initYearList(Connection conn,String sort) throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs= null;
        List<References> yearList = new ArrayList<References>();
		try{
			sql = "SELECT DISTINCT ORDER_YEAR from XXPENS_BI_MST_ORDER_DATE ORDER BY ORDER_YEAR "+sort;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				yearList.add(new References(rs.getString("ORDER_YEAR"), (rs.getInt("ORDER_YEAR")+543)+""));
			}
			
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
		return yearList;
	}
}
