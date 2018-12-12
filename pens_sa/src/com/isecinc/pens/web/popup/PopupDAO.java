package com.isecinc.pens.web.popup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.DBConnection;
import util.Utils;

import com.isecinc.pens.web.location.LocationControlPage;

public class PopupDAO {
	private static Logger logger = Logger.getLogger("PENS");
	
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
			
			sql.append("\n select c.customer_code ,c.customer_desc ");
			sql.append("\n from xxpens_ar_cust_sales_v cs ,  ");
			sql.append("\n xxpens_salesreps_v s , ");
			sql.append("\n xxpens_bi_mst_customer c ");
			sql.append("\n where 1=1 ");
			sql.append("\n and cs.code = s.code ");
			sql.append("\n and cs.cust_account_id = c.customer_id ");
			
			if( !Utils.isNull(c.getCodeSearch()).equals("")){
				sql.append("\n and c.customer_code ='"+c.getCodeSearch()+"' ");
			}
			if( !Utils.isNull(c.getDescSearch()).equals("")){
				sql.append("\n and c.customer_desc LIKE '%"+c.getDescSearch()+"%' ");
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
	
	  
}
