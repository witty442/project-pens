package com.isecinc.pens.gendate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.Utils;

public class OrderDateUtils {

	protected static Logger logger = Logger.getLogger("PENS");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getOrderDate();
	}
	
	public static Date getOrderDate() {
		Date orderDate= new Date();
		boolean loopBreak = false;
		try{
			//Date testDate = Utils.parse("19/07/2556", Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//logger.debug("OrderDate["+orderDate+"]");
			
			Map<String,String> bussinessHolidayMap =  getBussinessHolidayMap();
			
			Calendar nextOrderDate = Calendar.getInstance();
			nextOrderDate.add(Calendar.DATE, 1);
			
			while(loopBreak == false){
				boolean isHolidayB = isHoliday(nextOrderDate.getTime());
				logger.debug("isHolidayB["+isHolidayB+"]");
				if(isHolidayB ==false){
				   boolean isBussinessHolidayB = isBussinessHoliday(nextOrderDate.getTime(),bussinessHolidayMap);
				   logger.debug("isBussinessHoliday["+isBussinessHolidayB+"]");
				   if(isBussinessHolidayB == false){
					   loopBreak = true;
					   orderDate = nextOrderDate.getTime();
				   }else{
					   nextOrderDate.add(Calendar.DATE, 1); 
				   }
				}else{
					nextOrderDate.add(Calendar.DATE, 1);
				}
			}//while
			
			logger.debug("orderDate["+orderDate+"]");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
		return orderDate;
	}
	
	/**
	 * 
	 * @param d
	 * @return
	 * @throws Exception
	 * Sun 1 ,Mon 2 ,Tue 3 ,Wen 4,Thurs 5,Fri 6,Sat 7
	 */
	private static boolean isHoliday(Date d) throws Exception{
		boolean b = false;
		try{
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int dd = c.get(Calendar.DAY_OF_WEEK);
			logger.debug("dd["+dd+"]");
			if(dd == 1 || dd == 7){
				b = true;
			}
			
			return b;
		}catch(Exception e){
			throw e;
		}
	}

	private static boolean isBussinessHoliday(Date d,Map<String, String> bussinessHolidayMap) throws Exception{
		boolean b = false;
        try{
        	String budishOrderDate = Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
        	if(bussinessHolidayMap.get(budishOrderDate) != null){
        		b = true;
        	}
        	
        	return b;
		}catch(Exception e){
			throw e;
		}
	}
	
	public static Map<String, String> getBussinessHolidayMap() throws Exception{
		Connection conn = null;
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String, String> bussinessHolidayMap= new HashMap<String, String>();
		try{
			conn = DBConnection.getInstance().getConnection();
			StringBuffer sql = new StringBuffer("");
			sql.append(" select interface_value from PENSBME_MST_REFERENCE where reference_code ='Holiday'  \n");
	
			ps = conn.prepareStatement(sql.toString());
			
			rs = ps.executeQuery();
			while(rs.next()){
				Date dd = Utils.parse(rs.getString("interface_value"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String budishDateStr = Utils.stringValue(dd, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				logger.debug("dd["+dd+"]budishDateStr["+budishDateStr+"]");
				
				bussinessHolidayMap.put(budishDateStr,budishDateStr);
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
			
		}
		return bussinessHolidayMap;
	} 
}
