package com.isecinc.pens.web.popup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

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
	
	  
}
