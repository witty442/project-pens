package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

public class PopupDAO {

	private static Logger logger = Logger.getLogger("PENS");
	
   
	 public static List<PopupForm> searchCustomerMaster(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.code,m.name \n");

				sql.append("\n  from m_customer M");
				
				sql.append("\n  where 1=1 ");
			
				if("equals".equalsIgnoreCase(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and code ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and name = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and code LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and name LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				
				sql.append("\n  ORDER BY code asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("code")));
					item.setDesc(Utils.isNull(rst.getString("name")));
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
	 
	 public static List<PopupForm> searchBrand(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.brand_no,m.brand_desc from M_BRAND M");
				sql.append("\n  where 1=1 ");
			
				if("equals".equalsIgnoreCase(operation)){
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
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("brand_no")));
					item.setDesc(Utils.isNull(rst.getString("brand_desc")));
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
	 
	 public static List<PopupForm> searchInvoice(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("select distinct ar_invoice_no from t_order h ,t_order_line l ");
				sql.append(" where h.order_id = l.order_id ");
				sql.append(" and interfaces = 'Y' ");
				sql.append(" and ar_invoice_no is not null ");
				sql.append(" and ar_invoice_no <> '' ");
				//whereCause += "  and user_id = " + userId; Comment Out Because 
				sql.append(" and customer_id = " + c.getCustomerId());
				sql.append(" and l.product_id = "+c.getProductId());
				//No Recipt
				sql.append(" and h.order_id not in( ");
				sql.append("    select order_id from t_receipt ");
				sql.append(" ) ");
				
				sql.append(" and doc_status <> 'VO' ");
				sql.append(" order by Ar_invoice_no asc ");
				
				sql.append("\n  where 1=1 ");
			
				if("equals".equalsIgnoreCase(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and ar_invoice_no ='"+c.getCodeSearch()+"' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and ar_invoice_no = '"+c.getDescSearch()+"' \n");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append(" and ar_invoice_no LIKE '%"+c.getCodeSearch()+"%' \n");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append(" and ar_invoice_no LIKE '%"+c.getDescSearch()+"%' \n");
					}
				}
				
				sql.append("\n  ORDER BY ar_invoice_no asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("ar_invoice_no")));
					item.setDesc(Utils.isNull(rst.getString("ar_invoice_no")));
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
