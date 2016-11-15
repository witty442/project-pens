package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.SAEmpBean;
import com.isecinc.pens.bean.SATranBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class SATranDAO {

	 private static Logger logger = Logger.getLogger("PENS");
	 
	 public static List<SATranBean> initYearMonth(String empId,String payDate) throws Exception {
			Connection conn = null;
			List<SATranBean> itemsList = new ArrayList<SATranBean>();
			Map<String,SATranBean> YEARMMDB_MAP = new HashMap<String, SATranBean>();
			String startYearMonth = "";
			int diffMonth = 0;
			String yyyymmPayDate = "";
			try {
				 conn = DBConnection.getInstance().getConnection();
				 
				if( !Utils.isNull(payDate).equals("")){
				   String mm  = payDate.substring(3,5);
		           String yyyy  = payDate.substring(6,10);
		           yyyymmPayDate = yyyy+mm;
				}else{
					//Case View
					yyyymmPayDate = getYearMonthFromTran(conn, empId,"max");
					if( !Utils.isNull(yyyymmPayDate).equals("")){
						payDate = "01"+"/"+yyyymmPayDate.substring(4,6)+"/"+yyyymmPayDate.substring(0,2);
					}else{
						//default now date
						yyyymmPayDate = Utils.stringValue(new Date(), Utils.YYYYMM,Utils.local_th);
						payDate = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					}
				}
		        
                //GET DATA DB
                YEARMMDB_MAP = getDataInDB_To_MAP(conn, empId);
                
                //Get Master Bean
                SAEmpBean masterEmpBean = SAEmpDAO.getEmp(conn,empId);
                
                // get Start Year From SA_TRAN_REWARD
                startYearMonth = getYearMonthFromTran(conn, empId,"min");
               
                //get Start YearMonth From Employee
                SATranBean startYearMonthBean = getStartYearMonthFromEmployee(conn, empId);
                
                if("".equals(Utils.isNull(startYearMonth))){
                	startYearMonth = startYearMonthBean.getYearMonth();
                }
                
                if( !"".equals(Utils.isNull(startYearMonth))){
	                //case payDate < startYearMonth -> startYearMonth = yearMonthPayDate
	                if(Long.parseLong(yyyymmPayDate) < Long.parseLong(startYearMonth)){
	                	 logger.debug("startYearMonthNew:"+startYearMonth);
	                	 
	                	 diffMonth = calcDiffMonth(yyyymmPayDate, startYearMonth);
	                	 startYearMonth = yyyymmPayDate;
	                }else{
	                	 diffMonth = calcDiffMonth(startYearMonth, yyyymmPayDate);
	                }
	                
	                //case diffMonth ==0 calc from max-min (payDateMonth==min YearMonth)
	                if(diffMonth ==0){
	                	String maxYearMonth = getYearMonthFromTran(conn, empId,"max");
	                	String minYearMonthNew = startYearMonth;
	                	
	                	logger.debug("maxYearMonth:"+maxYearMonth);
	                	logger.debug("minYearMonthNew:"+minYearMonthNew);
	                	diffMonth = calcDiffMonth(minYearMonthNew, maxYearMonth);
	                }
	
	                logger.debug("startYearMonth:"+startYearMonth);
	                logger.debug("payDate:"+payDate);
	                logger.debug("diffMonth:"+diffMonth);
	                
                }else{
                	//case no date in sa_employee and no date in tran
                	logger.debug("Case No Date init");
                	startYearMonth = yyyymmPayDate;
                	return null;
                }
                
				String yearMonthCount = startYearMonth;
				int mm_Count  = Integer.parseInt(yearMonthCount.substring(4,6));
			    int yyyy_Count  = Integer.parseInt(yearMonthCount.substring(0,4));
			    SATranBean dataDB = null;
			    Date payDateScreen = null;
			    Date payDateDB = null;
			    
				for(int i=0;i<=diffMonth;i++){
		
					SATranBean item= new SATranBean();
					item.setYearMonth(yearMonthCount);
					
					///Check isExist DB get value from reward_tran
					if(YEARMMDB_MAP.get(item.getYearMonth()) != null){
						//logger.debug("Exist DB yearMonth["+item.getYearMonth()+"]");
						dataDB =  YEARMMDB_MAP.get(item.getYearMonth());
						
						item.setExistDB(true);
						
						//Check canChange Chkbox payDateDB >= payDateScreen 
						payDateScreen = Utils.parse(payDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						payDateDB = Utils.parse(dataDB.getPayDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						
						logger.debug("payDateScreen:"+payDateScreen);
						logger.debug("payDateDB    :"+payDateDB);
						logger.debug("before:"+payDateDB.before(payDateScreen));
						
						//Can Change
						if(payDateScreen.equals(payDateDB)){// payDateScreen < payDateDB
							item.setCanChange(true);
						}else{
							item.setCanChange(false);
						}
						item.setBmeAmt(dataDB.getBmeAmt());
						item.setWacoalAmt(dataDB.getWacoalAmt());
						//case isSave set payDate
						item.setPayDate(dataDB.getPayDate());
						
					}else{
						//Not found in SA_TRAN
						item.setExistDB(false);
						//logger.debug(yearMonthCount+":"+Utils.convertStrToInt(startYearMonthBean.getStartBmeYearMonth()));
						logger.debug(yearMonthCount+":"+Utils.convertStrToInt(startYearMonthBean.getStartWacoalYearMonth()));
						
						if( Utils.convertStrToInt(yearMonthCount) >= Utils.convertStrToInt(startYearMonthBean.getStartBmeYearMonth()) ){
						   item.setBmeAmt(masterEmpBean.getRewardBme());
						}else{
						   item.setBmeAmt("0");
						}
						if( Utils.convertStrToInt(yearMonthCount) >= Utils.convertStrToInt(startYearMonthBean.getStartWacoalYearMonth()) ){
							item.setWacoalAmt(masterEmpBean.getRewardWacoal());
						}else{
							item.setWacoalAmt("0");
						}
						
						item.setCanChange(true);
						item.setPayDate("");
					}
					itemsList.add(item);
					
					//Add month ++
					if(mm_Count ==12){
						mm_Count = 1;
						yyyy_Count++;
					}else{
						mm_Count++;
					}
					
					yearMonthCount = ""+yyyy_Count+( (String.valueOf(mm_Count)).length()==1?"0"+mm_Count:mm_Count);
				  
				}//for
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
                    if(conn != null){
					  conn.close();conn =null;
                    }
				} catch (Exception e) {}
			}
			return itemsList;
	}
	 
	 private static int calcDiffMonth(String yearMonth,String yearMonthPayDate){
		 int diffMonth = -1;
		 if( !"".equals(yearMonth)){
			 int mmYearMonth  = Integer.parseInt(yearMonth.substring(4,6));
		     int yyyyYearMonth  = Integer.parseInt(yearMonth.substring(0,4));
		     
		     int mmAsOf  = Integer.parseInt(yearMonthPayDate.substring(4,6));
		     int yyyyAsOf  = Integer.parseInt(yearMonthPayDate.substring(0,4));
		     
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
	 
	 public static String getYearMonthFromTran(Connection conn ,String empId,String method) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String yearMonth = "";
			try {
				sql.append("\n SELECT "+method+"(year_month) as year_month ");
				sql.append("\n FROM  SA_REWARD_TRAN");
				sql.append("\n WHERE emp_id = '"+empId+"' ");
				
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					yearMonth = Utils.isNull(rst.getString("year_month"));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
	
				} catch (Exception e) {}
			}
			return yearMonth;
		}
	
	 public static SATranBean getStartYearMonthFromEmployee(Connection conn ,String empId) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			SATranBean item= null;
			try {
				sql.append("\n SELECT start_reward_bme_date,start_reward_wacoal_date");
				sql.append("\n ,(CASE WHEN start_reward_bme_date < start_reward_wacoal_date " );
				sql.append("\n     THEN  TO_CHAR(TO_NUMBER(TO_CHAR(start_reward_bme_date,'YYYY') ))+543|| TO_CHAR(start_reward_bme_date,'MM')  ");
				sql.append("\n     ELSE TO_CHAR(TO_NUMBER(TO_CHAR(start_reward_wacoal_date,'YYYY') ))+543|| TO_CHAR(start_reward_wacoal_date,'MM')  END) year_month ");
				sql.append("\n ,null as paydate ,reward_bme as bme_amt,reward_wacoal as wacoal_amt ");
				sql.append("\n  FROM  SA_EMPLOYEE");
				sql.append("\n  WHERE emp_id = '"+empId+"' ");
				
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				if(rst.next()) {
					item = new SATranBean();
					item.setYearMonth(Utils.isNull(rst.getString("year_month")));
					item.setStartBmeYearMonth(Utils.stringValue(rst.getDate("start_reward_bme_date"), Utils.YYYYMM,Utils.local_th));
					item.setStartWacoalYearMonth(Utils.stringValue(rst.getDate("start_reward_wacoal_date"), Utils.YYYYMM,Utils.local_th));
					
					item.setBmeAmt(Utils.decimalFormat(rst.getDouble("bme_amt"), Utils.format_current_2_disgit));
					item.setWacoalAmt(Utils.decimalFormat(rst.getDouble("wacoal_amt"), Utils.format_current_2_disgit));
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
	
				} catch (Exception e) {}
			}
			return item;
	}
	 
	 public static Map<String,SATranBean> getDataInDB_To_MAP(Connection conn ,String empId) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Map<String,SATranBean> YEARMMDB_MAP = new HashMap<String, SATranBean>();
			SATranBean item = null;
			try {
				sql.append("\n SELECT year_month,bme_amt,wacoal_amt ,paydate FROM  SA_REWARD_TRAN");
				sql.append("\n  WHERE emp_id = '"+empId+"'");
				
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
					item = new SATranBean();
					item.setYearMonth(Utils.isNull(rst.getString("year_month")));
					item.setBmeAmt(Utils.decimalFormat(rst.getDouble("bme_amt"), Utils.format_current_2_disgit));
					item.setWacoalAmt(Utils.decimalFormat(rst.getDouble("wacoal_amt"), Utils.format_current_2_disgit));
					
					if(rst.getDate("paydate") != null){
						item.setPayDate(Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					}else{
						item.setPayDate("");
					}
					
					YEARMMDB_MAP.put(Utils.isNull(rst.getString("year_month")), item);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				
				} catch (Exception e) {}
			}
			return YEARMMDB_MAP;
	}
	 
	 public static SATranBean insertModel(Connection conn,SATranBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("insertModel");
			int c =1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("INSERT INTO SA_REWARD_TRAN \n");
				sql.append("(EMP_ID, year_month, paydate, Bme_amt, Wacoal_amt,CREATE_DATE, CREATE_USER) \n");
				sql.append("VALUES (?, ?, ?, ?, ?, ?, ?) \n");//7
				
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getEmpId()));//1
				ps.setString(c++, Utils.isNull(o.getYearMonth())); //2
			
				if( !Utils.isNull(o.getPayDate()).equals("")){//4
					 ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getPayDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					 ps.setTimestamp(c++,null);
				}
				ps.setDouble(c++, Utils.convertStrToDouble2Digit(o.getBmeAmt()));//12
				ps.setDouble(c++, Utils.convertStrToDouble2Digit(o.getWacoalAmt()));//12
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//14
				ps.setString(c++, o.getCreateUser());//15
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
			return o;
		}
	 
	 public static int updateModel(Connection conn,SATranBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("updateHeadModel");
			int c =1;
			try{
		
				StringBuffer sql = new StringBuffer("");
				sql.append("UPDATE SA_REWARD_TRAN \n");
				sql.append("SET paydate =?, Bme_amt =?, Wacoal_amt=?,UPDATE_DATE=?, UPDATE_USER=? \n");
				sql.append("WHERE EMP_ID=? AND year_month=? \n");//7
				
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				
				if( !Utils.isNull(o.getPayDate()).equals("")){//4
					 ps.setTimestamp(c++, new java.sql.Timestamp((Utils.parse(o.getPayDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)).getTime()));
				}else{
					 ps.setTimestamp(c++,null);
				}
				
				ps.setDouble(c++, Utils.convertStrToDouble2Digit(o.getBmeAmt()));//12
				ps.setDouble(c++, Utils.convertStrToDouble2Digit(o.getWacoalAmt()));//12
				
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//14
				ps.setString(c++, o.getCreateUser());//15
				
				ps.setString(c++, Utils.isNull(o.getEmpId()));//1
				ps.setString(c++, Utils.isNull(o.getYearMonth())); //2
			
				return  ps.executeUpdate();

			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static int deleteModel(Connection conn,SATranBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("deleteModel");
			int c =1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append("DELETE FROM SA_REWARD_TRAN \n");
				sql.append("WHERE EMP_ID=? AND year_month=? \n");//7
				
				logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
	
				ps.setString(c++, Utils.isNull(o.getEmpId()));//1
				ps.setString(c++, Utils.isNull(o.getYearMonth())); //2
			
				return  ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}

   public static SATranBean searchHead(Connection conn,SATranBean o) throws Exception {
	  return searchHeadModel(conn,o,"");
	}
   

   public static SATranBean searchHead(Connection conn,String mode,SATranBean o) throws Exception {
	  return searchHeadModel(conn,o,mode);
	}
   
   public static SATranBean searchHead(SATranBean o ,String mode) throws Exception {
	   Connection conn = null;
	   try{
		  conn = DBConnection.getInstance().getConnection();
		  return searchHeadModel(conn, o,mode);
	   }catch(Exception e){
		   throw e;
	   }finally{
		   conn.close();
	   }
	}
   
	public static SATranBean searchHeadModel(Connection conn,SATranBean o ,String mode) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			
			SATranBean h = null;
			List<SATranBean> items = new ArrayList<SATranBean>();
			int r = 1;
			try {
			   sql.append(" \n select E.* " );
			   sql.append(" \n,(SELECT M.pens_desc FROM PENSBME_MST_REFERENCE M where M.reference_code = 'Region' AND M.pens_value =E.region)as region_desc" );
			   sql.append("\n  from SA_EMPLOYEE E ");
			   sql.append("\n  WHERE 1=1");
			   
			    if( !Utils.isNull(o.getEmpId()).equals("") ){
					sql.append("\n and E.emp_id ='"+Utils.isNull(o.getEmpId())+"'");
			    }
			    if( !Utils.isNull(o.getOracleRefId()).equals("")){
					sql.append("\n and E.oracle_ref_id ='"+o.getOracleRefId()+"'");
				}
				if( !Utils.isNull(o.getType()).equals("")){
					sql.append("\n and E.type = '"+Utils.isNull(o.getType())+"'");
				}
				if( !Utils.isNull(o.getName()).equals("")){
					sql.append("\n and E.name LIKE '%"+Utils.isNull(o.getName())+"%'");
				}
				if( !Utils.isNull(o.getSurname()).equals("")){
					sql.append("\n and E.surname LIKE '%"+Utils.isNull(o.getSurname())+"%'");
				}
				if( !Utils.isNull(o.getGroupStore()).equals("")){
					sql.append("\n and E.GROUP_STORE = '"+Utils.isNull(o.getGroupStore())+"'");
				}
				if( !Utils.isNull(o.getBranch()).equals("")){
					sql.append("\n and E.branch LIKE '%"+Utils.isNull(o.getBranch())+"%'");
				}
				
				sql.append("\n order by E.emp_id asc ");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				
				while(rst.next()) {
				  h = new SATranBean();
				   h.setEmpId(Utils.isNull(rst.getString("emp_id")));
				  // h.setYearMonth(Utils.isNull(rst.getString("year_month")));
				   h.setType(Utils.isNull(rst.getString("type")));
				   
				   h.setOracleRefId(Utils.isNull(rst.getString("oracle_ref_id")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
				   h.setName(Utils.isNull(rst.getString("name")));
				   h.setSurname(Utils.isNull(rst.getString("surname")));
				   h.setFullName(h.getName()+" "+h.getSurname());
				   
				   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
				   h.setRegion(Utils.isNull(rst.getString("region")));
				   h.setRegionDesc(Utils.isNull(rst.getString("region_desc")));
					   
				   if("edit".equalsIgnoreCase(mode)){
					   h.setDisableTextClass("disableText");
				   }
				   items.add(h);
				   r++;
				   
				}//while
				
				//set Result 
				o.setItems(items);
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		return o;
	}
	 
	 public static SATranBean searchReport(SATranBean o ) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchReportModel(conn, o);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	public static SATranBean searchReportModel(Connection conn,SATranBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Map<String,String> empMap = new HashMap<String, String>();
		SATranBean h = null;
		List<SATranBean> items = new ArrayList<SATranBean>();
		int r = 1;
		try {
		   sql.append(" \n select E.emp_id,E.name,E.surname,E.group_store,E.branch " );
		   sql.append("\n  ,T.year_month,T.paydate,T.bme_amt,T.wacoal_amt");
		   sql.append("\n  from SA_EMPLOYEE E ,SA_REWARD_TRAN T");
		   sql.append("\n  WHERE 1=1 and E.emp_id = T.emp_id");
		   
		    if( !Utils.isNull(o.getEmpId()).equals("") ){
				sql.append("\n and E.emp_id ='"+Utils.isNull(o.getEmpId())+"'");
		    }
		    if( !Utils.isNull(o.getOracleRefId()).equals("")){
				sql.append("\n and E.oracle_ref_id ='"+o.getOracleRefId()+"'");
			}
			if( !Utils.isNull(o.getType()).equals("")){
				sql.append("\n and E.type = '"+Utils.isNull(o.getType())+"'");
			}
			if( !Utils.isNull(o.getName()).equals("")){
				sql.append("\n and E.name LIKE '%"+Utils.isNull(o.getName())+"%'");
			}
			if( !Utils.isNull(o.getSurname()).equals("")){
				sql.append("\n and E.surname LIKE '%"+Utils.isNull(o.getSurname())+"%'");
			}
			if( !Utils.isNull(o.getGroupStore()).equals("")){
				sql.append("\n and E.GROUP_STORE = '"+Utils.isNull(o.getGroupStore())+"'");
			}
			if( !Utils.isNull(o.getBranch()).equals("")){
				sql.append("\n and E.branch LIKE '%"+Utils.isNull(o.getBranch())+"%'");
			}
			
			sql.append("\n order by E.emp_id ,T.year_month asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			
			while(rst.next()) {
			   h = new SATranBean();
			   if(empMap.get(Utils.isNull(rst.getString("emp_id"))) == null){
				   h.setEmpId(Utils.isNull(rst.getString("emp_id")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
				   h.setName(Utils.isNull(rst.getString("name")));
				   h.setSurname(Utils.isNull(rst.getString("surname")));
				   h.setFullName(h.getName()+" "+h.getSurname());
				   h.setGroupStore(Utils.isNull(rst.getString("group_store")));
				   h.setBranch(Utils.isNull(rst.getString("branch")));
			   }else{
				   h.setEmpId("");
				   h.setBranch("");
				   h.setName("");
				   h.setSurname("");
				   h.setFullName("");
				   h.setGroupStore("");
				   h.setBranch(""); 
			   }
			   
			   h.setYearMonth(Utils.isNull(rst.getString("year_month")));
			   h.setBmeAmt(Utils.decimalFormat(rst.getDouble("bme_amt"), Utils.format_current_2_disgit));
			   h.setWacoalAmt(Utils.decimalFormat(rst.getDouble("wacoal_amt"), Utils.format_current_2_disgit));
				
			   if(rst.getDate("paydate") != null){
				 h.setPayDate(Utils.stringValue(rst.getDate("paydate"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  }else{
				 h.setPayDate("");
			   }
				   
			   items.add(h);
			   r++;
			   
			   empMap.put(Utils.isNull(rst.getString("emp_id")), Utils.isNull(rst.getString("emp_id")));
			   
			}//while
			
			//set Result 
			o.setItems(items);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
}
