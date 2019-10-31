package com.isecinc.pens.web.popup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.web.location.LocationControlPage;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class PopupDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
	 public static List<PopupForm> searchPDStockVanList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				//Get Parameter
				String salesChannelNo = Utils.isNull(c.getCriteriaMap().get("salesChannelNo"));
				String pdType = Utils.isNull(c.getCriteriaMap().get("pdType"));
				
				sql.append("\n SELECT M.* FROM (");
				sql.append("\n SELECT distinct pd.subinventory_code,pd.name as subinv_name ");
				sql.append("\n from apps.xxpens_inv_onhand_r00_v pd");
				sql.append("\n ,apps.xxpens_inv_subinv_access subinv");
				sql.append("\n ,apps.xxpens_salesreps_v s");
				sql.append("\n  WHERE pd.subinventory_code = subinv.subinventory");
				sql.append("\n  AND subinv.salesrep_id = s.salesrep_id");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and pd.subinventory_code ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and pd.name LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(salesChannelNo).equals("")){
					sql.append("\n and s.region = '"+Utils.isNull(salesChannelNo)+"'");
				}
				if( !Utils.isNull(pdType).equals("")){
					sql.append("\n and subinv.subinventory Like '"+Utils.isNull(pdType)+"%'");
				}
				sql.append("\n UNION ");
				
				sql.append("\n SELECT distinct pd_int.to_subinventory as subinventory_code ,pd_int.name as subinv_name ");
				sql.append("\n from apps.xxpens_inv_intransit_r00_v pd_int");
				sql.append("\n ,apps.xxpens_inv_subinv_access subinv");
				sql.append("\n ,apps.xxpens_salesreps_v s");
				sql.append("\n  WHERE pd_int.to_subinventory = subinv.subinventory");
				sql.append("\n  AND subinv.salesrep_id = s.salesrep_id");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and pd_int.subinventory_code ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and pd_int.name LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(salesChannelNo).equals("")){
					sql.append("\n and s.region = '"+Utils.isNull(salesChannelNo)+"'");
				}
				if( !Utils.isNull(pdType).equals("")){
					sql.append("\n and subinv.subinventory Like '"+Utils.isNull(pdType)+"%'");
				}
				sql.append("\n )M  ORDER BY M.subinventory_code asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("subinventory_code"));
					item.setDesc(rst.getString("subinv_name"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchPDList(PopupForm c,String pdCodeLike) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n SELECT distinct secondary_inventory_name,description ");
				sql.append("\n from apps.mtl_secondary_inventories subinv where 1=1");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and subinv.secondary_inventory_name ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and subinv.description LIKE '%"+c.getDescSearch()+"%' ");
				}
			    sql.append("\n and subinv.secondary_inventory_name Like '"+pdCodeLike+"%'");
			    sql.append("\n order by  subinv.secondary_inventory_name ");
			    
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("secondary_inventory_name"));
					item.setDesc(rst.getString("description"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	
	 public static List<PopupForm> searchItemStockVanList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				String brand = Utils.isNull(c.getCriteriaMap().get("brand"));
				
				sql.append("\n SELECT distinct A.segment1,A.description FROM(");
				
				sql.append("\n SELECT distinct p.segment1,p.description ");
				sql.append("\n from apps.xxpens_inv_onhand_r00_v M");
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n where M.inventory_item_id = p.inventory_item_id  ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and p.segment1 ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and p.description LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(brand).equals("")){
					sql.append("\n and p.brand in("+SQLHelper.converToTextSqlIn(brand)+") ");
				}
				sql.append("\n  UNION ALL");
				
				sql.append("\n SELECT distinct p.segment1,p.description ");
				sql.append("\n from apps.xxpens_inv_intransit_r00_v M");
				sql.append("\n ,apps.xxpens_om_item_mst_v p");
				sql.append("\n where M.inventory_item_id = p.inventory_item_id  ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and p.segment1 ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and p.description LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(brand).equals("")){
					sql.append("\n and p.brand in("+SQLHelper.converToTextSqlIn(brand)+") ");
				}
				sql.append("\n )A");
				sql.append("\n  ORDER BY A.segment1 asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("segment1"));
					item.setDesc(rst.getString("description"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchBrandList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n SELECT distinct M.brand ,B.brand_desc from XXPENS_BI_SALES_TARGET_TEMP M, XXPENS_BI_MST_BRAND B ");
				sql.append("\n where 1=1  ");
				sql.append("\n and M.brand = B.brand_no ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.brand ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and B.brand_desc LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n  ORDER BY M.brand asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("brand"));
					item.setDesc(rst.getString("brand_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchBrandStockList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n SELECT distinct M.brand ,B.brand_desc from xxpens_om_check_order_v M, XXPENS_BI_MST_BRAND B ");
				sql.append("\n where 1=1  ");
				sql.append("\n and M.brand = B.brand_no ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.brand ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and B.brand_desc LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n  ORDER BY M.brand asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("brand"));
					item.setDesc(rst.getString("brand_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchBrandStockVanList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n SELECT distinct A.brand_no,A.brand_desc FROM( ");
				sql.append("\n SELECT distinct B.brand_no ,B.brand_desc ");
				sql.append("\n from apps.xxpens_inv_onhand_r00_v pd ");
				sql.append("\n ,apps.xxpens_om_item_mst_v P ");
				sql.append("\n ,pensbi.XXPENS_BI_MST_BRAND B ");
				sql.append("\n where pd.inventory_item_id = P.inventory_item_id ");
				sql.append("\n and P.brand = B.brand_no ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and B.brand_no ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and B.brand_desc LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n UNION ALL");
				
				sql.append("\n SELECT distinct B.brand_no ,B.brand_desc ");
				sql.append("\n from apps.xxpens_inv_intransit_r00_v pd_int ");
				sql.append("\n ,apps.xxpens_om_item_mst_v P ");
				sql.append("\n ,pensbi.XXPENS_BI_MST_BRAND B ");
				sql.append("\n where pd_int.inventory_item_id = P.inventory_item_id ");
				sql.append("\n and P.brand = B.brand_no ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and B.brand_no ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and B.brand_desc LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n )A ");
				sql.append("\n  ORDER BY A.brand_no asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("brand_no"));
					item.setDesc(rst.getString("brand_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchBrandProdShowList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n SELECT distinct M.brand ,B.brand_desc from APPS.XXPENS_OM_PRODSHOW_DT M, PENSBI.XXPENS_BI_MST_BRAND B");
				sql.append("\n where 1=1  ");
				sql.append("\n and M.brand = B.brand_no ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.brand ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and B.brand_desc LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n  ORDER BY M.brand asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("brand"));
					item.setDesc(rst.getString("brand_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	
	 
	 public static List<PopupForm> searchCustomerList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.append("\n SELECT distinct M.customer_code ,M.short_name as customer_desc " );
				sql.append("\n from XXPENS_BI_MST_CUST_SALES M ");
				sql.append("\n where 1=1  ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.customer_code ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and M.short_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n  ORDER BY M.customer_code asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("customer_code"));
					item.setDesc(rst.getString("customer_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static List<PopupForm> searchCustomerCreditSalesList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				//prepare parameter Fix CConstant 
				logger.debug("startDate:"+Utils.isNull(c.getCriteriaMap().get("startDate")));
				Date startDate = DateUtil.parse(Utils.isNull(c.getCriteriaMap().get("startDate")), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
				String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				logger.debug("startDateStr:"+startDateStr);
				
				String userName = Utils.isNull(c.getCriteriaMap().get("userName"));
				String salesrepCode = Utils.isNull(c.getCriteriaMap().get("salesrepCode"));
				
				sql.append("\n SELECT distinct C.account_number as customer_code ,C.party_name as customer_desc " );
				sql.append("\n from apps.xxpens_ar_cust_sales_all M,apps.xxpens_ar_customer_all_v C");
				sql.append("\n ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
				sql.append("\n where M.cust_account_id = C.cust_account_id ");
				sql.append("\n and M.primary_salesrep_id = Z.salesrep_id ");
				sql.append("\n and M.code like 'S%' ");//Credit Sales Only
				sql.append("\n and Z.zone in('0','1','2','3','4') ");
				if( !Utils.isNull(salesrepCode).equals("")){
					sql.append("\n and Z.salesrep_code in("+SQLHelper.converToTextSqlIn(salesrepCode)+")");
				}
				/** filter sales by user login **/
				if( !Utils.isNull(userName).equals("")){
					sql.append("\n and Z.zone in( ");
					sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT ");
					sql.append("\n   where user_name ='"+userName+"'");
					sql.append("\n ) ");
				}
				/** filter customer is check stock **/
			/*	sql.append("\n and C.account_number in( ");
				sql.append("\n   select customer_number from apps.xxpens_om_check_order_v ");
				sql.append("\n   where request_date >= to_date('"+startDateStr+"','dd/mm/yyyy') ");
				sql.append("\n )");*/
				
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and C.account_number ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and C.party_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n  ORDER BY C.account_number asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("customer_code"));
					item.setDesc(rst.getString("customer_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchSalesrepCreditSalesList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				String userName = Utils.isNull(c.getCriteriaMap().get("userName"));
				
				sql.append("\n SELECT distinct code ,salesrep_full_name " );
				sql.append("\n from apps.xxpens_salesreps_v M ,PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
				sql.append("\n where M.salesrep_id =Z.salesrep_id ");
				sql.append("\n and z.zone in('0','1','2','3','4') ");
				sql.append("\n and M.code like 'S%' ");//Credit Sales Only
				
				/** filter sales by user login **/
				if( !Utils.isNull(userName).equals("")){
					sql.append("\n and Z.zone in( ");
					sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT where user_name ='"+userName+"'");
					sql.append("\n ) ");
				}
				
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.code ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and M.salesrep_full_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				sql.append("\n  ORDER BY M.code asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("code"));
					item.setDesc(rst.getString("salesrep_full_name"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchCustomerStockList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				logger.debug("searchCustomerStockList");
				//criteria
				String salesChannelNo = Utils.isNull(c.getCriteriaMap().get("salesChannelNo"));
				String salesrepCode = Utils.isNull(c.getCriteriaMap().get("salesrepCode"));
				logger.debug("salesChannelNo:"+salesChannelNo);
				logger.debug("salesrepCode:"+salesrepCode);
				
				sql.append("\n SELECT distinct M.customer_number as customer_code ");
				sql.append("\n ,M.party_name as customer_desc " );
				sql.append("\n from xxpens_om_check_order_v M ");
				sql.append("\n where 1=1  ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.customer_number ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and M.party_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(salesChannelNo).equals("")){
					sql.append("\n and M.region = '"+salesChannelNo+"' ");
				}
				if( !Utils.isNull(salesrepCode).equals("")){
					sql.append("\n and M.sales_code = '"+salesrepCode+"' ");
				}
				sql.append("\n  ORDER BY M.customer_number asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("customer_code"));
					item.setDesc(rst.getString("customer_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static List<PopupForm> searchCustomerVanProdShowList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				logger.debug("searchCustomerStockList");
				//criteria
				String salesChannelNo = Utils.isNull(c.getCriteriaMap().get("salesChannelNo"));
				String salesrepCode = Utils.isNull(c.getCriteriaMap().get("salesrepCode"));
				logger.debug("salesChannelNo:"+salesChannelNo);
				logger.debug("salesrepCode:"+salesrepCode);
				
				sql.append("\n SELECT distinct c.account_number as customer_code ");
				sql.append("\n ,c.party_name as customer_desc " );
				sql.append("\n FROM XXPENS_OM_PRODSHOW_MST H");
				sql.append("\n ,XXPENS_OM_PRODSHOW_DT D"); 
				sql.append("\n ,xxpens_ar_customer_all_v C");
				sql.append("\n WHERE H.order_number = D.order_number");
				sql.append("\n AND C.account_number = H.customer_number");
				//Only customer van
				sql.append("\n and H.order_number LIKE 'V%' ");
				
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and C.account_number ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and C.party_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(salesChannelNo).equals("")){
					sql.append("\n and substr(H.order_number,2,1) = '"+salesChannelNo+"' ");
				}
				if( !Utils.isNull(salesrepCode).equals("")){
					sql.append("\n and substr(H.order_number,1,4) = '"+salesrepCode+"' ");
				}
				sql.append("\n  ORDER BY c.account_number asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("customer_code"));
					item.setDesc(rst.getString("customer_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchCustomerCreditPromotionList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				logger.debug("searchCustomerStockList");
				//criteria
				String salesChannelNo = Utils.isNull(c.getCriteriaMap().get("salesChannelNo"));
				String salesrepCode = Utils.isNull(c.getCriteriaMap().get("salesrepCode"));
				logger.debug("salesChannelNo:"+salesChannelNo);
				logger.debug("salesrepCode:"+salesrepCode);
				
				sql.append("\n SELECT distinct c.account_number as customer_code ");
				sql.append("\n ,c.party_name as customer_desc " );
				sql.append("\n FROM xxpens_om_req_promotion_mst H");
				sql.append("\n ,xxpens_ar_customer_all_v C ");
				sql.append("\n WHERE 1=1");
				sql.append("\n AND C.account_number = H.customer_number");
			
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and C.account_number ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and C.party_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(salesChannelNo).equals("")){
					sql.append("\n and substr(H.request_no,2,1) = '"+salesChannelNo+"' ");
				}
				if( !Utils.isNull(salesrepCode).equals("")){
					sql.append("\n and substr(H.request_no,1,4) = '"+salesrepCode+"' ");
				}
				
				sql.append("\n  ORDER BY c.account_number asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("customer_code"));
					item.setDesc(rst.getString("customer_desc"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 //Wait
	 public static List<PopupForm> searchCustomerLocationList(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<PopupForm> pos = new ArrayList<PopupForm>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			logger.debug("searchCustomerLocationList");

			//criteria
			String custCatNo = Utils.isNull(c.getCriteriaMap().get("custCatNo"));
			String salesChannelNo = Utils.isNull(c.getCriteriaMap().get("salesChannelNo"));
			String salesrepId = Utils.isNull(c.getCriteriaMap().get("salesrepId"));
			String province = Utils.isNull(c.getCriteriaMap().get("province"));
			String district = Utils.isNull(c.getCriteriaMap().get("district"));
			
			if( !Utils.isNull(province).equals("")){
				province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getCriteriaMap().get("province")));
			}
			if( !Utils.isNull(district).equals("")){
				district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getCriteriaMap().get("district")));
			}
			logger.debug("custCatNo:"+custCatNo);
			logger.debug("salesChannelNo:"+salesChannelNo);
			logger.debug("salesrepId:"+salesrepId);
			logger.debug("province:"+province);
			logger.debug("district:"+district);
			
			sql.append("\n select cs.account_number as customer_code ,cs.party_name as customer_desc ");
			sql.append("\n from xxpens_ar_cust_sales_vs cs ,  ");
			sql.append("\n xxpens_salesreps_v s  ");
			sql.append("\n where 1=1 ");
			sql.append("\n and cs.code = s.code ");
			sql.append("\n and s.region in(0,1,2,3,4) ");
			sql.append("\n and s.sales_channel in('C','S') ");
			 
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append("\n and cs.account_number ='"+c.getCodeSearch()+"' ");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append("\n and cs.party_name LIKE '%"+c.getDescSearch()+"%' ");
			}
			if( !Utils.isNull(custCatNo).equals("")){
				sql.append("\n and s.sales_channel = '"+custCatNo+"' ");
			}
			if( !Utils.isNull(salesChannelNo).equals("")){
				sql.append("\n and s.region = '"+salesChannelNo+"' ");
			}
			if( !Utils.isNull(salesrepId).equals("")){
				sql.append("\n and cs.primary_salesrep_id = '"+salesrepId+"' ");
			}
			if( !Utils.isNull(province).equals("")){
				sql.append("\n and cs.province = '"+province+"' ");
			}
			if( !Utils.isNull(district).equals("")){
				sql.append("\n and cs.amphur = '"+district+"' ");
			}
			sql.append("\n  ORDER BY cs.account_number asc ");
			
			logger.debug("sql:"+sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			while (rst.next()) {
				PopupForm item = new PopupForm();
				no++;
				item.setNo(no);
				item.setCode(rst.getString("customer_code"));
				item.setDesc(rst.getString("customer_desc"));
				pos.add(item);
				
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return pos;
	}
	 
	 public static List<PopupForm> searchCustomerLocNoTripList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				conn = DBConnection.getInstance().getConnectionApps();
				logger.debug("searchCustomerLocNoTripList");

				//criteria
				String custCatNo = Utils.isNull(c.getCriteriaMap().get("custCatNo"));
				String salesChannelNo = Utils.isNull(c.getCriteriaMap().get("salesChannelNo"));
				String salesrepId = Utils.isNull(c.getCriteriaMap().get("salesrepId"));
				String province = Utils.isNull(c.getCriteriaMap().get("province"));
				String district = Utils.isNull(c.getCriteriaMap().get("district"));
				
				if( !Utils.isNull(province).equals("")){
					province = LocationControlPage.getProvinceName(conn, Utils.isNull(c.getCriteriaMap().get("province")));
				}
				if( !Utils.isNull(district).equals("")){
					district = LocationControlPage.getDistrictName(conn, Utils.isNull(c.getCriteriaMap().get("district")));
				}
				logger.debug("custCatNo:"+custCatNo);
				logger.debug("salesChannelNo:"+salesChannelNo);
				logger.debug("salesrepId:"+salesrepId);
				logger.debug("province:"+province);
				logger.debug("district:"+district);
				
				sql.append("\n select cs.account_number as customer_code ,cs.party_name as customer_desc ");
				sql.append("\n from xxpens_ar_cust_sales_vl cs ");
				sql.append("\n where 1=1 ");
				
				sql.append("\n and cs.trip1 is null and cs.trip2 is null and cs.trip3 is null ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and cs.account_number LIKE '%"+c.getCodeSearch()+"%' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and cs.party_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(custCatNo).equals("")){
					sql.append("\n and cs.sales_channel = '"+custCatNo+"' ");
				}
				if( !Utils.isNull(salesChannelNo).equals("")){
					sql.append("\n and cs.region = '"+salesChannelNo+"' ");
				}
				if( !Utils.isNull(salesrepId).equals("")){
					sql.append("\n and cs.primary_salesrep_id = '"+salesrepId+"' ");
				}
				if( !Utils.isNull(province).equals("")){
					sql.append("\n and cs.province = '"+province+"' ");
				}
				if( !Utils.isNull(district).equals("")){
					sql.append("\n and cs.amphur = '"+district+"' ");
				}
				sql.append("\n  ORDER BY cs.account_number asc ");
				
				logger.debug("sql:"+sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("customer_code"));
					item.setDesc(rst.getString("customer_desc"));
					pos.add(item);
					
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchItemStockList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				logger.debug("searchItemStockList");
				//criteria
				String brand = Utils.isNull(c.getCriteriaMap().get("brand"));
				logger.debug("brand:"+brand);

				sql.append("\n SELECT distinct M.item_no , M.item_name" );
				sql.append("\n from xxpens_om_check_order_v M ");
				sql.append("\n where 1=1  ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and M.item_no ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and M.item_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(brand).equals("")){
					sql.append("\n and M.brand = '"+brand+"' ");
				}
				
				sql.append("\n  ORDER BY M.item_no asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("item_no"));
					item.setDesc(rst.getString("item_name"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static List<PopupForm> searchSalestargetPDList(PopupForm c,String typeSearch) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				logger.debug("searchSalestargetPDList tyep:"+typeSearch);
				//criteria
				String brand = Utils.isNull(c.getCriteriaMap().get("brand"));
				logger.debug("brand:"+brand);

				if("ItemSalesTargetPD".equalsIgnoreCase(typeSearch)){
					sql.append("\n SELECT distinct M.item_no , M.item_name" );
					sql.append("\n from apps.xxpens_inv_pd_target_sales_v M ");
					sql.append("\n where 1=1  ");
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and M.inventory_item_code ='"+c.getCodeSearch()+"' ");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append("\n and M.inventory_item_desc LIKE '%"+c.getDescSearch()+"%' ");
					}
					if( !Utils.isNull(brand).equals("")){
						sql.append("\n and M.inventory_item_code like '"+brand+"%' ");
					}
					sql.append("\n  ORDER BY M.inventory_item_code asc ");
					
				}else if("PDSalesTargetPD".equalsIgnoreCase(typeSearch)){
					sql.append("\n SELECT distinct M.SUBINVENTORY " );
					sql.append("\n from apps.xxpens_inv_pd_target_sales_v M ");
					sql.append("\n where 1=1  ");
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and M.SUBINVENTORY ='"+c.getCodeSearch()+"' ");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append("\n and M.SUBINVENTORY LIKE '%"+c.getDescSearch()+"%' ");
					}
					sql.append("\n  ORDER BY M.SUBINVENTORY asc ");
					
				}else if("BrandSalesTargetPD".equalsIgnoreCase(typeSearch)){
					
					sql.append("\n select I.brand ,B.brand_desc");
					sql.append("\n from( ");
					sql.append("\n  SELECT distinct SUBSTR(M.item_no,1,3) as brand " );
					sql.append("\n  from apps.xxpens_inv_pd_target_sales_v M ");
					sql.append("\n  )I ,PENSBI.XXPENS_BI_MST_BRAND B ");
					sql.append("\n where I.brand = B.brand ");
					
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and B.brand ='"+c.getCodeSearch()+"' ");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append("\n and B.brand_desc LIKE '%"+c.getDescSearch()+"%' ");
					}
					sql.append("\n  ORDER BY B.brand asc ");
				}
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					if("ItemSalesTargetPD".equalsIgnoreCase(typeSearch)){
						item.setNo(no);
						item.setCode(rst.getString("inventory_item_code"));
						item.setDesc(rst.getString("inventory_item_desc"));
					}else if("PDSalesTargetPD".equalsIgnoreCase(typeSearch)){
						item.setNo(no);
						item.setCode(rst.getString("SUBINVENTORY"));
						item.setDesc(rst.getString("SUBINVENTORY"));
					}else if("BrandSalesTargetPD".equalsIgnoreCase(typeSearch)){
						item.setNo(no);
						item.setCode(rst.getString("brand"));
						item.setDesc(rst.getString("brand_desc"));
					}
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	
	 public static List<PopupForm> searchItemCreditPromotionList(PopupForm c) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				logger.debug("searchItemCreditPromotionList");
				//criteria
				String brand = Utils.isNull(c.getCriteriaMap().get("brand"));
				logger.debug("brand:"+brand);

				sql.append("\n SELECT A.* FROM (");
				sql.append("\n  SELECT distinct M.product_code " );
				sql.append("\n  ,(select max(inventory_item_desc) from PENSBI.XXPENS_BI_MST_ITEM I ");
				sql.append("\n    where I.inventory_item_code = M.product_code ) as product_name " );
				sql.append("\n  from xxpens_om_req_promotion_dt1 M ");
				sql.append("\n )A ");
				sql.append("\n  where 1=1  ");
				if( !Utils.isNull(c.getCodeSearch()).equals("")){
					sql.append("\n and A.product_code ='"+c.getCodeSearch()+"' ");
				}
				if( !Utils.isNull(c.getDescSearch()).equals("")){
					sql.append("\n and A.item_name LIKE '%"+c.getDescSearch()+"%' ");
				}
				if( !Utils.isNull(brand).equals("")){
					sql.append("\n and substr(A.product_code,1,3) = '"+brand+"' ");
				}
				
				sql.append("\n  ORDER BY A.product_code asc ");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnectionApps();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				while (rst.next()) {
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("product_code"));
					item.setDesc(rst.getString("product_name"));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 public static List<PopupForm> searchCustomerStockMC(PopupForm c) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<PopupForm> pos = new ArrayList<PopupForm>();
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		String customerCode = "";
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			logger.debug("searchCustomerStockMC");

			//criteria
			if(c.getCriteriaMap() != null){
			   customerCode = Utils.isNull(c.getCriteriaMap().get("customerCode"));
			}else{
			   customerCode = Utils.isNull(c.getCodeSearch());
			}
			logger.debug("customerCode:"+customerCode);
			
			sql.append("\n select c.customer_code ,c.customer_name,c.cust_type ");
			sql.append("\n from pensbi.mc_cust c");
			sql.append("\n where 1=1 ");
			
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append("\n and c.customer_code ='"+c.getCodeSearch()+"' ");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append("\n and c.customer_name LIKE '%"+c.getDescSearch()+"%' ");
			}
			
			sql.append("\n  ORDER BY c.customer_code asc ");
			
			logger.debug("sql:"+sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no = 0;
			while (rst.next()) {
				PopupForm item = new PopupForm();
				no++;
				item.setNo(no);
				item.setCode(rst.getString("customer_code"));
				item.setDesc(rst.getString("customer_name"));
				pos.add(item);
				
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
		return pos;
	}
	 
	 public static List<PopupForm> searchSubInvList(PopupForm c){
		    List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuffer sql = new StringBuffer("");
			Statement stmt = null;
			ResultSet rst = null;
			String subInv = "",subInvName ="";
			Connection  conn = null;
			int no = 0;
			try{
				conn = DBConnection.getInstance().getConnectionApps();
				//criteria
				subInv = Utils.isNull(c.getCodeSearch());
				subInvName = Utils.isNull(c.getDescSearch());
				
				sql.append("select distinct a.secondary_inventory_name,a.description from \n");
				sql.append("apps.mtl_secondary_inventories a where 1=1 \n");
				if( !Utils.isNull(subInv).equals("")){
				   sql.append("where a.secondary_inventory_name ='"+subInv+"' \n");
				}
				if( !Utils.isNull(subInvName).equals("")){
					   sql.append("where a.description ='"+subInvName+"' \n");
					}
				sql.append("and a.organization_id = 166 \n");
				sql.append("order by a.secondary_inventory_name ");
				
				logger.debug("sql:"+sql.toString());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while(rst.next()){
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("secondary_inventory_name"));
					item.setDesc(rst.getString("description"));
					pos.add(item);
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
		 return pos;
	}
	 
	 public static List<PopupForm> searchItemList(PopupForm c){
		    List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuffer sql = new StringBuffer("");
			Statement stmt = null;
			ResultSet rst = null;
			String productCode = "",productName ="",brand ="";;
			Connection  conn = null;
			int no = 0;
			try{
				conn = DBConnection.getInstance().getConnectionApps();
				//criteria
				productCode = Utils.isNull(c.getCodeSearch());
				productName = Utils.isNull(c.getDescSearch());
				if(c.getCriteriaMap() != null){
					brand = Utils.isNull(c.getCriteriaMap().get("brand"));
				}
				
				sql.append("select a.segment1,a.description \n");
				sql.append("from apps.xxpens_om_item_mst_v a where 1=1 \n");
				if( !Utils.isNull(productCode).equals("")){
				   sql.append("and a.segment1 LIKE '%"+productCode+"%' \n");
				}
				if( !Utils.isNull(productName).equals("")){
				   sql.append("and a.description LIKE '%"+productName+"%' \n");
				}
				if( !Utils.isNull(brand).equals("")){
					if(brand.split("\\,").length >1){ //101,102
					   sql.append("and a.brand in("+SQLHelper.converToTextSqlIn(brand)+")\n");
					}else{
					   sql.append("and a.brand LIKE '"+brand+"%' \n");
					}
				}
				
				sql.append("and rownum <= 500 ");
				sql.append("order by a.segment1 ");
				
				logger.debug("sql:"+sql.toString());
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while(rst.next()){
					PopupForm item = new PopupForm();
					no++;
					item.setNo(no);
					item.setCode(rst.getString("segment1"));
					item.setDesc(rst.getString("description"));
					pos.add(item);
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
		 return pos;
	}
}
