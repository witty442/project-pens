package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.BMEControlBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class BMECControlDAO {
	private static Logger logger = Logger.getLogger("PENS");
	public static String TYPE_ONHAND_DATE_LOTUS_AS_OF ="ONHAND_DATE_LOTUS_AS_OF";
	
	 public static BMEControlBean calcMonthEndOnhandDateLotusAsOf(Connection conn,String storeCode,String asOfdate) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			BMEControlBean bean = new BMEControlBean();
			try {
				Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n select distinct max(year_month) as year_month FROM PENSBME_ENDING_STOCK WHERE 1=1 ");
				sql.append("\n and store_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					String yearMonth = Utils.isNull(rst.getString("year_month"));
					
			        //calc diff month
					int diffMonth = calcDiffMonth(yearMonth,christAsOfDateStr);
					logger.debug("diffMonth:"+diffMonth);
					
					//compare as asOfDate
					if(diffMonth == 1){
						bean.setCaseType("3");
						bean.setYearMonth(yearMonth);
						
						//set Month+1
						String yyyy= yearMonth.substring(0,4);
						String mm = yearMonth.substring(4,6);
						if("12".equals(mm)){
							mm = "01";
							yyyy = (Integer.parseInt(yyyy)+1)+"";
						}else{
						   mm = (Integer.parseInt(mm)+1)+"";
						}
						mm = mm.length()==1?"0"+mm:mm;
						
						String nextYearMonth = "01/"+mm+"/"+yyyy;
						bean.setStartDate(nextYearMonth);
						bean.setEndDate(christAsOfDateStr);
						
					}else if(diffMonth > 1){
						bean.setCaseType("2");
						bean.setYearMonth(yearMonth);
						
						//set Month Last
						String yyyy= yearMonth.substring(0,4);
						String mm = yearMonth.substring(4,6);
						if("12".equals(mm)){
							mm = "01";
							yyyy = (Integer.parseInt(yyyy)+1)+"";
						}else{
						   mm = (Integer.parseInt(mm)+1)+"";
						}
						mm = mm.length()==1?"0"+mm:mm;
						String nextYearMonth = "01/"+mm+"/"+yyyy;
						bean.setStartDate(nextYearMonth);
						bean.setEndDate(christAsOfDateStr);
					}else{
						//asOfDate > max yearMonth
						logger.debug("Case 4");
					    //String mmAsOf  = asOfdate.substring(3,5);
				        //String yyyyAsOf  = ""+(Integer.parseInt(asOfdate.substring(6,10))-543);
				            
						bean.setCaseType("4");
						bean.setYearMonth("NULL");
						bean.setStartDate(asOfdate);	
					}
				}else{
					// 
					bean.setCaseType("1");
					bean.setYearMonth("");
					bean.setStartDate(christAsOfDateStr);
					
				}

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return bean;
		}
	 
	 private static int calcDiffMonth(String yearMonth,String asOfDate){
		 int diffMonth = -1;
		 if( !"".equals(yearMonth)){
			 int mmYearMonth  = Integer.parseInt(yearMonth.substring(4,6));
		     int yyyyYearMonth  = Integer.parseInt(yearMonth.substring(0,4));
		     
		     int mmAsOf  = Integer.parseInt(asOfDate.substring(3,5));
		     int yyyyAsOf  = Integer.parseInt(asOfDate.substring(6,10));
		     
		     int diffYear = yyyyAsOf - yyyyYearMonth;
		     if(diffYear >0){
		    	 mmAsOf = (diffYear*12)+mmAsOf;
		    	 diffMonth = mmAsOf - mmYearMonth;
		     }else{
		    	 diffMonth = mmAsOf - mmYearMonth;
		     }
		 }
	     return diffMonth;
	        
	 }
	 
	 public static String getOnhandDateLotusAsOf(Connection conn) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String date ="";
			try {
				sql.append("\n select con_value FROM ");
				sql.append("\n PENSBI.PENSBME_C_CONTROL WHERE 1=1 ");
				sql.append("\n and con_type ='"+TYPE_ONHAND_DATE_LOTUS_AS_OF+"'");
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					date = Utils.isNull(rst.getString("con_value"));
				}

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return date;
		}
	 
	 public static String getOnhandDateAsOf(Connection conn,String ss) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String date ="";
			try {
				sql.append("\n select con_value FROM ");
				sql.append("\n PENSBI.PENSBME_C_CONTROL WHERE 1=1 ");
				sql.append("\n and con_type ='"+TYPE_ONHAND_DATE_LOTUS_AS_OF+"'");
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					date = Utils.isNull(rst.getString("con_value"));
				}

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return date;
		}
}
