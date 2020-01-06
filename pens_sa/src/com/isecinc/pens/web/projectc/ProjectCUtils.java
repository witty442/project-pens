package com.isecinc.pens.web.projectc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class ProjectCUtils {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
		
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	public static List<PopupBean> searchSalesrepListAll(String salesChannelNo,String custCatNo,String salesZone) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return searchSalesrepListAll(conn, salesChannelNo, custCatNo,salesZone);
		}catch(Exception e){
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static List<PopupBean> searchSalesrepListAll(Connection conn,String salesChannelNo,String custCatNo,String salesZone){
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
			if( !Utils.isNull(salesZone).equals("")){
				sql.append("\n  and S.code in(");
				sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
				sql.append("\n    where zone = "+Utils.isNull(salesZone) );
				sql.append("\n  )");
			}
			
			sql.append("\n  and length(S.code) >= 3");
			sql.append("\n  and S.ISACTIVE ='Y'");
			//Not in()
			sql.append("\n  and S.code not in('V006','V008','V009','V010','V011','V013','V014','V020','V021')");
			sql.append("\n  and S.code not in('V081','V082') ");
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
	
	public static List<PopupBean> searchSalesZoneListModel(Connection conn){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.zone,S.zone_name from PENSBI.XXPENS_BI_MST_SALES_ZONE S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and zone in('0','1','2','3','4') ");
			sql.append("\n  ORDER BY S.zone asc \n");
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
