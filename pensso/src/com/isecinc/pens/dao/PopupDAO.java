package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MAddress;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class PopupDAO {

	private static Logger logger = Logger.getLogger("PENS");
	
   
	 public static List<PopupBean> searchCustomerMaster(PopupBean c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupBean> pos = new ArrayList<PopupBean>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.code,m.name ");
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
					PopupBean item = new PopupBean();
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
	 public static List<PopupBean> searchCustomerMasterAndAddress(PopupBean c,String operation,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupBean> pos = new ArrayList<PopupBean>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			Address address= null;
			MAddress mAddress = new MAddress();
			
			try {
				sql.append("\n  SELECT M.customer_id,M.code,m.name ");
				sql.append("\n  from m_customer M");
				sql.append("\n  where isactive ='Y' and user_id ="+user.getId());
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
					PopupBean item = new PopupBean();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("code")));
					item.setDesc(Utils.isNull(rst.getString("name")));
					item.setCustomerId(Utils.isNull(rst.getString("customer_id")));
					address = mAddress.findAddressByCustomerId(conn, rst.getString("customer_id"),"B");
					if(address != null){
						item.setAddress(address.getLine1()+" "+address.getLine2()+" "+address.getLine3()+" "+address.getLine4()+" "+address.getProvince().getName()+" "+address.getPostalCode());
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
	 public static List<PopupBean> searchBrand(PopupBean c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupBean> pos = new ArrayList<PopupBean>();
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
					PopupBean item = new PopupBean();
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
	 
	 public static List<PopupBean> searchInvoice(PopupBean c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupBean> pos = new ArrayList<PopupBean>();
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
				   dateStart = DateUtil.stringValue(curdate.getTime(), DateUtil.DD_MM_YYYY_WITH_SLASH);
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
				
				sql.append("\n and order_date >= TO_DATE('"+dateStart+"', 'dd/mm/yyyy') ");
				
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
					PopupBean item = new PopupBean();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("ar_invoice_no")));
					item.setStringDate(DateUtil.stringValue(rst.getDate("order_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
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
	 public static List<PopupBean> searchSubbrandList()
				throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			PopupBean m = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			try {
				conn = DBConnectionApps.getInstance().getConnection();

				sql.append("\n  SELECT distinct m.subbrand_no,m.subbrand_desc");
				sql.append("\n  from PENSBI.XXPENS_BI_MST_SUBBRAND m");
				sql.append("\n  where m.subbrand_no like '38%' ");
				sql.append("\n  ORDER BY m.subbrand_no \n");

				logger.debug("sql:" + sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					m = new PopupBean();
					m.setSubBrand(Utils.isNull(rst.getString("subbrand_no")));
					m.setSubBrandDesc(Utils.isNull(rst.getString("subbrand_desc")));
					dataList.add(m);
				
				}// while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (Exception e) {
				}
			}
			return dataList;
		}
	 public static PopupBean searchSubbrandStock(PopupBean mCriteria, User user)
				throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			PopupBean m = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			Map<String, PopupBean> subBrandMap = new HashMap<String, PopupBean>();
			try {
				conn = DBConnectionApps.getInstance().getConnection();

				sql.append("\n  SELECT m.subbrand_no,m.subbrand_desc");
				sql.append("\n  ,p.code ,p.name");
				sql.append("\n  from PENSBI.XXPENS_BI_MST_SUBBRAND m ,PENSSO.M_PRODUCT p ");
				sql.append("\n  where m.inventory_item_id = p.product_id");
				sql.append("\n  and (m.subbrand_no like '381%' or m.subbrand_no like '382%' or m.subbrand_no like '383%')");
				if( !Utils.isNull(mCriteria.getSubBrand()).equals("")){
					sql.append("\n  and m.subbrand_no = '"+Utils.isNull(mCriteria.getSubBrand())+"'");
				}
				sql.append("\n  ORDER BY m.subbrand_no,p.code  \n");

				logger.debug("sql:" + sql);

				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					m = new PopupBean();
					m.setProductCode(Utils.isNull(rst.getString("code")));
					m.setProductName(Utils.isNull(rst.getString("name")));
					m.setSubBrand(Utils.isNull(rst.getString("subbrand_no")));
					m.setSubBrandDesc(Utils.isNull(rst.getString("subbrand_desc")));
					dataList.add(m);
					
					subBrandMap.put(m.getSubBrand(), m);
				}// while
				
				mCriteria.setDataList(dataList);
				mCriteria.setData2List(new ArrayList<PopupBean>(subBrandMap.values()));
				
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					if (conn != null) {
						conn.close();
						conn = null;
					}
				} catch (Exception e) {
				}
			}
			return mCriteria;
		}
	 
}
