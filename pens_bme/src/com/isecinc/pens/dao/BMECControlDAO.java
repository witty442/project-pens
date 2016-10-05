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
			BMEControlBean bean = new BMEControlBean();
			String yearMonth = "";
			String yearMonthAsOfDate = "";
			String yearMonthMax = "";
			try {
				
				//Budish to ChristDate
				Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				
				//Get YearMonth By StoreCode and asOfdate
				yearMonthAsOfDate = getYearMonthByAsOfDate(conn,storeCode,asOfdate);
				if("".equals(yearMonthAsOfDate)){
					//NULL GET MAX YEAR MONTH
					yearMonthMax = getMaxYearMonthByStoreCode(conn,storeCode);
					yearMonth = yearMonthMax;
				}else{
					yearMonth = yearMonthAsOfDate;
				}
				
				logger.debug("YearMonth:"+yearMonth);
				
				if ( !"".equals(yearMonth)) {
					
			        //calc diff month
					int diffMonth = calcDiffMonth(yearMonth,christAsOfDateStr);
					logger.debug("diffMonth:"+diffMonth);
					
				 // asOfdate = YearMonth
				  if(diffMonth ==0){
						logger.debug("Case 1");
						bean.setCaseType("1");
				
						//set startDate and EndDate mm+1
						String yyyy= yearMonth.substring(0,4);
						String mm = yearMonth.substring(4,6);
						mm = mm.length()==1?"0"+mm:mm;
						
		                String nextYearMonth = "01/"+mm+"/"+yyyy;
						
						bean.setStartDate(nextYearMonth);
						bean.setEndDate(christAsOfDateStr);
						
						// set yearMonth mm-1
						 yyyy= yearMonth.substring(0,4);
						 mm = yearMonth.substring(4,6);
						if("01".equals(mm)){
							mm = "12";
							yyyy = (Integer.parseInt(yyyy)-1)+"";
						}else{
						    mm = (Integer.parseInt(mm)-1)+"";
						}
						mm = mm.length()==1?"0"+mm:mm;
						
		                yearMonth = yyyy+mm;
						bean.setYearMonth(yearMonth);
					
			      //asOfDate > yearMonth =1	
				  }else if(diffMonth >= 1 ){
						logger.debug("Case 2");
						bean.setCaseType("2");
						bean.setYearMonth(yearMonth);
						
						//set startDate mm+1
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
						logger.debug("Case 3");
					    //String mmAsOf  = asOfdate.substring(3,5);
				        //String yyyyAsOf  = ""+(Integer.parseInt(asOfdate.substring(6,10))-543);
				            
						bean.setCaseType("3");
						bean.setYearMonth("NULL");
						bean.setStartDate(asOfdate);	
					}
				}else{
					logger.debug("Case 4");
					bean.setCaseType("4");
					bean.setYearMonth("");
					bean.setStartDate(christAsOfDateStr);
					
				}

			} catch (Exception e) {
				throw e;
			} finally {
				try {
				
				} catch (Exception e) {}
			}
			return bean;
		}
	 
	 public static String getYearMonthByAsOfDate(Connection conn,String storeCode,String asOfdate) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String yearMonth = "";
			try {
			    String mmAsOf  = asOfdate.substring(3,5);
		        String yyyyAsOf  = ""+(Integer.parseInt(asOfdate.substring(6,10))-543);
				String yearMonthCheck = yyyyAsOf+mmAsOf;
				sql.append("\n select distinct year_month FROM PENSBME_ENDING_STOCK WHERE 1=1 and store_code ='"+storeCode+"' and year_month='"+yearMonthCheck+"'" );
			
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()){
					yearMonth = Utils.isNull(rst.getString("year_month"));
				}
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return yearMonth;
		}
	 
	 public static String getMaxYearMonthByStoreCode(Connection conn,String storeCode) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String yearMonth = "";
			try {
				sql.append("\n select distinct max(year_month) as year_month FROM PENSBME_ENDING_STOCK WHERE 1=1 and store_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if(rst.next()){
					yearMonth = Utils.isNull(rst.getString("year_month"));
				}
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return yearMonth;
		}
	 
	 public static BMEControlBean calcMonthEndOnhandDateLotusAsOf_BK(Connection conn,String storeCode,String asOfdate) throws Exception {
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
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					String yearMonth = Utils.isNull(rst.getString("year_month"));
					
			        //calc diff month
					int diffMonth = calcDiffMonth(yearMonth,christAsOfDateStr);
					logger.debug("diffMonth:"+diffMonth);
					
					//compare as asOfDate
					if(diffMonth == 1 ){
						logger.debug("Case 3");
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
						logger.debug("Case 2");
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
					logger.debug("Case 1");
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
	 
	 public static String[] canGenEndDateLotus(String storeCode,String asOfdate) throws Exception {
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return canGenEndDateLotus(conn, storeCode, asOfdate);
		 }catch(Exception e){
			 throw e;
		 }finally{
			 if(conn != null){
				 conn.close();
			 }
		 }
	 }
	 public static String[] canGenEndDateLotus(Connection conn,String storeCode,String asOfdate) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String[] results = new String[3];
			//int diffMonth = 0;
			try {
				Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				//String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n select distinct max(ending_date) as max_ending_date FROM PENSBME_ENDDATE_STOCK WHERE 1=1 ");
				sql.append("\n and store_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					if(rst.getDate("max_ending_date") != null){
						 Date endingDate = rst.getDate("max_ending_date");
					     if(asofDateTemp.after(endingDate)){
					    	 results[0] = "true";
					    	 results[1] = "";
					    	 results[2] ="";
					     }else{
					    	 results[0] = "false";
					    	 results[1] = "";
					    	 results[2] ="กรุณาตรวจสอบวันที่จะ End date ต้องมากกว่าการ End date ครั้งก่อนหน้านี้ ";
					     }
					}else{
						results[0] = "true";
				    	results[1] = "";
				    	results[2] ="";
					}
				}else{
					results[0] = "true";
			    	results[1] = "";
			    	results[2] ="";
				}
				
				return results;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			
		}
	 
	 public static String[] canGenMonthEndLotus(Connection conn,String storeCode,String asOfdate) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String[] results = new String[3];
			int diffMonth = 0;
			try {
				Date asofDateTemp = Utils.parse(asOfdate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String christAsOfDateStr = Utils.stringValue(asofDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n select distinct max(year_month) as max_year_month FROM PENSBME_ENDING_STOCK WHERE 1=1 ");
				sql.append("\n and store_code ='"+storeCode+"'");
			
				logger.debug("sql:"+sql);
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					String maxYearMonth = Utils.isNull(rst.getString("max_year_month"));
		
					if( !"".equals(maxYearMonth)){
						 int mmYearMonth  = Integer.parseInt(maxYearMonth.substring(4,6));
					     int yyyyYearMonth  = Integer.parseInt(maxYearMonth.substring(0,4));
					     
					     int mmAsOf  = Integer.parseInt(christAsOfDateStr.substring(3,5));
					     int yyyyAsOf  = Integer.parseInt(christAsOfDateStr.substring(6,10));
					     
					     int diffYear = yyyyAsOf - yyyyYearMonth;
					     if(diffYear > 0 ){
					    	 int mmAsOfTemp = (diffYear*12)+mmAsOf;
					    	 diffMonth = mmAsOfTemp - mmYearMonth;
					     }else{
					    	 diffMonth = mmAsOf - mmYearMonth;
					     }
					     
					     String mmAsOfStr = (mmAsOf+"").length()==1?"0"+mmAsOf:mmAsOf+"";
					     if(diffMonth >= 0 ){
					    	 results[0] = "true";
					    	 results[1] = ""+yyyyAsOf+""+mmAsOfStr;
					    	 results[2] ="";
					     }else{
					    	 results[0] = "false";
					    	 results[1] = ""+yyyyAsOf+""+mmAsOfStr;
					    	 results[2] ="เนื่องจาก ไม่สามรถ Generate ย้อนหลัง เดือนล่าสุด["+maxYearMonth+"] ได้ ";
					     }
					 }else{
						int mmAsOf  = Integer.parseInt(christAsOfDateStr.substring(3,5));
						int yyyyAsOf  = Integer.parseInt(christAsOfDateStr.substring(6,10));
						String mmAsOfStr = (mmAsOf+"").length()==1?"0"+mmAsOf:mmAsOf+"";    
						
						results[0] = "true";
				    	results[1] = ""+yyyyAsOf+""+mmAsOfStr;
				    	results[2] ="";
					 }
				}else{
					int mmAsOf  = Integer.parseInt(christAsOfDateStr.substring(3,5));
					int yyyyAsOf  = Integer.parseInt(christAsOfDateStr.substring(6,10));
					String mmAsOfStr = (mmAsOf+"").length()==1?"0"+mmAsOf:mmAsOf+"";    
					
					results[0] = "true";
			    	results[1] = ""+yyyyAsOf+""+mmAsOfStr;
			    	results[2] ="";
				}
				
				return results;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			
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
				sql.append("\n select con_value FROM PENSBME_C_CONTROL WHERE 1=1 ");
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
