package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
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
			String dateStart = "";
			try {
				List<References> backdateinvoiceList = InitialReferences.getReferenes(InitialReferences.BACKDATE_INVOICE, "6");
				if(backdateinvoiceList != null ){
				   References refbackDate =  backdateinvoiceList.get(0);
				   Calendar curdate = Calendar.getInstance();
				   curdate.add(Calendar.MONTH, -1*Integer.parseInt(refbackDate.getKey()));
				   
				   logger.debug("DateStart:"+curdate.getTime());
				   dateStart = Utils.stringValue(curdate.getTime(), Utils.DD_MM_YYYY_WITH_SLASH);
				   logger.debug("DateStart:"+dateStart);
				}
				
				sql.delete(0, sql.length());
				sql.append("\n select h.ar_invoice_no, h.order_date ,COALESCE(sum(l.qty),0) as qty" );
				sql.append("\n from t_order h ,t_order_line l ");
				sql.append("\n where h.order_id = l.order_id ");
				sql.append("\n and h.ar_invoice_no is not null ");
				sql.append("\n and h.ar_invoice_no <> '' ");
				sql.append("\n and h.user_id = " +c.getUserId());
				sql.append("\n and h.customer_id =(select customer_id from m_customer where code='"+c.getCustomerCode()+"')");
				sql.append("\n and l.product_id =(select product_id from m_product where code='"+c.getProductCode()+"')");
				//no Recipt
				/*sql.append("\n and h.order_id not in( ");
				sql.append("\n    select order_id from t_receipt_line ");
				sql.append("\n ) ");
				sql.append("\n and l.order_line_id not in( ");
				sql.append("\n  select order_line_id from t_receipt_line ");
				sql.append("\n ) ");*/
				
				sql.append("\n and order_date >= STR_TO_DATE('"+dateStart+"', '%d/%m/%Y') ");
				
				/*sql.append("\n and h.ar_invoice_no not in( ");
				sql.append("\n select invoice_no from t_req_promotion_line ");
				sql.append("\n )");*/
				sql.append("\n and doc_status <> 'VO' ");
			
				if("equals".equalsIgnoreCase(operation)){
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and h.ar_invoice_no ='"+c.getCodeSearch()+"'");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append("\n and h.ar_invoice_no = '"+c.getDescSearch()+"'");
					}
				}else{
					if( !Utils.isNull(c.getCodeSearch()).equals("")){
						sql.append("\n and h.ar_invoice_no LIKE '%"+c.getCodeSearch()+"%' ");
					}
					if( !Utils.isNull(c.getDescSearch()).equals("")){
						sql.append("\n and h.ar_invoice_no LIKE '%"+c.getDescSearch()+"%' ");
					}
				}
				sql.append("\n GROUP BY h.ar_invoice_no ,h.order_date ");
				sql.append("\n ORDER BY h.ar_invoice_no asc ");
				
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
					item.setStringDate(Utils.stringValue(rst.getDate("order_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					item.setDesc(Utils.isNull(rst.getString("qty")));
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
