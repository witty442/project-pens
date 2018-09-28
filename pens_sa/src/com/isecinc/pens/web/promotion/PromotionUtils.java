package com.isecinc.pens.web.promotion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.DBConnection;
import util.Utils;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;

public class PromotionUtils {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static List<PopupBean> initPeriod(Connection conn){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		PopupBean item = new PopupBean();
		String startDate = "";
		String endDate ="";
		StringBuffer sql = new StringBuffer("");
		Statement stmt = null;
		ResultSet rst = null;
		Date requestDate = null;
		try{
			sql.append("select distinct r ,r2 from( \n");
			sql.append("select to_char(request_date,'mm/yyyy') as r \n"
					+ ", to_number(to_char(request_date,'yyyymm') ) as r2 \n"
					+ " from xxpens_om_req_promotion_mst \n"
					+ " ) order by r2 desc \n");
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				item = new PopupBean();
				requestDate = Utils.parse("01/"+rst.getString("r"), Utils.DD_MM_YYYY_WITH_SLASH);
				cal.setTime(requestDate);
				logger.debug("Cal:"+cal.getTime());
				periodName =  Utils.stringValue(cal.getTime(),"MMM-yy",Utils.local_th).toUpperCase();
				logger.debug("period:"+periodName);
		        startDate  =  "01/"+Utils.stringValue(cal.getTime(),"MM/yyyy",Utils.local_th);
		        endDate    =   cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"/"+Utils.stringValue(cal.getTime(),"MM/yyyy",Utils.local_th);
				item.setKeyName(periodName);
				item.setValue(periodName+"|"+startDate +"|"+endDate);
				monthYearList.add(item);	
				
				cal = Calendar.getInstance();
			}
			if(monthYearList.size()==0){
				cal.setTime(new Date());
				periodName =  Utils.stringValue(cal.getTime(),"MMM-yy",Utils.local_th).toUpperCase();
				startDate  =  "01/"+Utils.stringValue(cal.getTime(),"MM/yyyy",Utils.local_th);
			    endDate    =   cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"/"+Utils.stringValue(cal.getTime(),"MM/yyyy",Utils.local_th);
			    
			    item.setKeyName(periodName);
				item.setValue(periodName+"|"+startDate +"|"+endDate);
				monthYearList.add(item);	
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return monthYearList;
	}

	public static PromotionBean getPeriodDesc(Calendar cal)throws Exception{
		PromotionBean s = new PromotionBean();
		String mm ="";
		String yyyy="";
		try{
			  mm = String.valueOf(cal.get(Calendar.MONTH)+1);
			  mm = mm.length()==1?"0"+mm:mm;
			  
			  yyyy = String.valueOf(cal.get(Calendar.YEAR)+543);
			  
			  s.setPeriod(Utils.isNull(Utils.stringValue(cal.getTime(), Utils.MMM_YY,Utils.local_th)));
			  s.setStartDate("01/"+mm+"/"+yyyy);
			  s.setEndDate(cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"/"+mm+"/"+yyyy);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
		}
	  return s;
	}
	
	
	/**
	 * getSalesrepId
	 * @param saleserpCode
	 * @return
	 * @throws Exception
	 */
	public static String getSalesrepCode(String saleserpId) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnectionApps();
			 return getSalesrepCode(conn,saleserpId);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	}
	public static String getSalesrepCode(Connection conn,String saleserpId){
		String CODE = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT CODE from xxpens_salesreps_v M  ");
			sql.append("\n  where  SALESREP_ID ="+saleserpId+"\n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				CODE =Utils.isNull(rst.getString("CODE"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return CODE;
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
	
	
	public static List<PopupBean> searchSalesrepListByUserName(Connection conn,User user){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		try{
			sql.delete(0, sql.length());
			sql.append("\n  SELECT distinct M.salesrep_code,S.salesrep_id from XXPENS_BI_MST_CUST_SALES M ,XXPENS_BI_MST_SALESREP S ");
			sql.append("\n  where M.salesrep_code =S.salesrep_code ");
			sql.append("\n and user_name ='"+user.getUserName()+"'");
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
	
	public static List<PopupBean> searchSalesrepListAll(String salesChannelNo,String custCatNo) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return searchSalesrepListAll(conn, salesChannelNo, custCatNo);
		}catch(Exception e){
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static List<PopupBean> searchSalesrepListAll(Connection conn,String salesChannelNo,String custCatNo){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		try{
			sql.append("\n  SELECT distinct S.code as salesrep_code,S.salesrep_id from xxpens_salesreps_v S ");
			sql.append("\n  where 1=1 ");
			if( !Utils.isNull(custCatNo).equals("")){
				if( Utils.isNull(custCatNo).equalsIgnoreCase("S")){//credit Sales
					sql.append("\n  and S.sales_channel = 'S' ");
					sql.append("\n  and S.code like 'S%' ");
					sql.append("\n  and S.code not like 'SN%' ");
				}else if( Utils.isNull(custCatNo).equalsIgnoreCase("V")){//Van Sale
					sql.append("\n  and S.sales_channel ='C' ");
				}else{
					sql.append("\n  and S.code not like 'SN%' ");
					sql.append("\n  and S.code not like 'C%' ");
				}
			}else{
				sql.append("\n  and S.code not like 'SN%' ");
				sql.append("\n  and S.code not like 'C%' ");
			}
			if( !Utils.isNull(salesChannelNo).equals("")){
				sql.append("\n  and s.region ='"+Utils.isNull(salesChannelNo)+"'");
			}
			sql.append("\n  and length(S.code) >= 3");
			sql.append("\n  and S.ISACTIVE ='Y'");
			//Not in()
			//sql.append("\n  and S.code not in('V081','V082') ");
			sql.append("\n  ORDER BY S.code asc ");
			
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

	 public static List<PopupBean> searchSalesChannelList(User user) throws Exception{
		 Connection conn = null;
		 try{
			 conn = DBConnection.getInstance().getConnection();
			 return searchSalesChannelListModel(conn,user);
		 }catch(Exception e){
			 throw e;
		 }finally{
			conn.close();
		 }
	  }
	 
	public static List<PopupBean> searchSalesChannelListModel(Connection conn,User user){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.sales_channel_no ,S.sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and sales_channel_no in('0','1','2','3','4') ");
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
