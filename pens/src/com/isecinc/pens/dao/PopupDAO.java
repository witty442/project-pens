package com.isecinc.pens.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.NumberToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.MoveOrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MStockReturn;
import com.isecinc.pens.model.MUOMConversion;
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
	 public static List<PopupForm> searchCustomerMasterAndAddress(PopupForm c,String operation,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
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
					PopupForm item = new PopupForm();
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
	 
	 public static List<PopupForm> searchInvoiceStockReturn(PopupForm c,String operation,User user) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String backDate = "";
			StockBeanUtils stockBean = null;
			try {
				conn = DBConnection.getInstance().getConnection();
				
				//Case requestNumber is not null get back from stockReturn
				if( !Utils.isNull(c.getRequestNumber()).equals("")){
					Date backDateObj = new MStockReturn().getBackDate(conn, Utils.isNull(c.getRequestNumber()));
					backDate = Utils.stringValue(backDateObj, Utils.DD_MM_YYYY_WITH_SLASH);
				}else{
					// get back_date(from c_reference) for get data
					List<References> backDateInvoiceStockReturnList = InitialReferences.getReferenceListByCode(conn,InitialReferences.BACKDATE_INVOICE_STOCKRETURN);
					if(backDateInvoiceStockReturnList != null ){
					   References refbackDate =  backDateInvoiceStockReturnList.get(0);
					   Calendar curdate = Calendar.getInstance();
					   curdate.add(Calendar.MONTH, -1*Integer.parseInt(refbackDate.getKey()));
					   
					   logger.debug("backDate:"+curdate.getTime());
					   backDate = Utils.stringValue(curdate.getTime(), Utils.DD_MM_YYYY_WITH_SLASH);
					   //set to 01/mm/yyyy
					   backDate = "01/"+backDate.substring(3,backDate.length());
					   logger.debug("backDate:"+backDate);
					}
				}
				
				sql.append("\n select h.ar_invoice_no,COALESCE(sum(l.qty),0) as qty" );
				sql.append("\n from t_order h ,t_order_line l ");
				sql.append("\n where h.order_id = l.order_id ");
				sql.append("\n and h.ar_invoice_no is not null ");
				sql.append("\n and h.ar_invoice_no <> '' ");
				sql.append("\n and l.promotion <> 'Y' ");
				sql.append("\n and l.iscancel <> 'Y' ");
				sql.append("\n and h.user_id = " +c.getUserId());
				sql.append("\n and h.customer_id =(select customer_id from m_customer where code='"+c.getCustomerCode()+"')");
				sql.append("\n and l.product_id =(select product_id from m_product where code='"+c.getProductCode()+"')");
				sql.append("\n and order_date >= STR_TO_DATE('"+backDate+"', '%d/%m/%Y') ");
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
				sql.append("\n GROUP BY h.ar_invoice_no ");
				sql.append("\n ORDER BY h.ar_invoice_no asc ");
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("ar_invoice_no")));
					item.setPrice(getOrderItemPriceByArInvoiceNo(conn, c, backDate, user, Utils.isNull(rst.getString("ar_invoice_no"))));
					
					//calc priQty
					stockBean = calcStockReturnPriQtyByArInvoice(conn,c.getRequestNumber(), Utils.isNull(rst.getString("ar_invoice_no")),backDate,c,user );
					if(stockBean != null){
					   item.setDesc(""+stockBean.getPriAllQty());
					   item.setPriAllQty(Utils.decimalFormat(stockBean.getPriAllQty(),Utils.format_current_5_digit));
					   item.setPriQty(Utils.decimalFormat(stockBean.getPriQty(),Utils.format_current_5_digit));
					   item.setSubQty(Utils.decimalFormat(stockBean.getSubQty(),Utils.format_current_5_digit));
					   
					   if(stockBean.getPriAllQty() > 0.00){
					      pos.add(item);
					   }
					}
					
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
	 public static String getOrderItemPriceByArInvoiceNo(Connection conn,PopupForm c,String backDate,User user,String arInvoiceNo) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String price ="";
			try {
				sql.append("\n select distinct l.price" );
				sql.append("\n from t_order h ,t_order_line l ");
				sql.append("\n where h.order_id = l.order_id ");
				sql.append("\n and h.ar_invoice_no is not null ");
				sql.append("\n and h.ar_invoice_no <> '' ");
				sql.append("\n and h.user_id = " +c.getUserId());
				sql.append("\n and h.customer_id =(select customer_id from m_customer where code='"+c.getCustomerCode()+"')");
				sql.append("\n and l.product_id =(select product_id from m_product where code='"+c.getProductCode()+"')");
				sql.append("\n and h.doc_status <> 'VO' ");
				sql.append("\n and l.promotion <> 'Y' ");
				sql.append("\n and h.order_date >= STR_TO_DATE('"+backDate+"', '%d/%m/%Y') ");
			    sql.append("\n and h.ar_invoice_no ='"+arInvoiceNo+"'");
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					price = Utils.decimalFormat(rst.getDouble("price"),Utils.format_current_2_disgit);
	
				}//if
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return price;
		}
	 
	 public static StockBeanUtils calcStockReturnPriQtyByArInvoice(Connection conn,String curRequestNumber
			 ,String arInvoiceNo,String dateStart,PopupForm c,User user) throws Exception {
		    StockBeanUtils bean = new StockBeanUtils();
		    Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			double priQtyInit = 0;
			double priQtyNotInCurrRequestNumber = 0;
			double priAllQty = 0;
			String productId = "";
			try {
				sql.append("\n select h.ar_invoice_no,l.product_id,l.uom_id,COALESCE(sum(l.qty),0) as remain_pri_qty" );
				sql.append("\n from t_order h ,t_order_line l ");
				sql.append("\n where h.order_id = l.order_id ");
				sql.append("\n and h.ar_invoice_no is not null ");
				sql.append("\n and h.ar_invoice_no <> '' ");
				sql.append("\n and doc_status <> 'VO' ");
				sql.append("\n and h.user_id = " +c.getUserId());
				sql.append("\n and h.customer_id =(select customer_id from m_customer where code='"+c.getCustomerCode()+"')");
				sql.append("\n and l.product_id =(select product_id from m_product where code='"+c.getProductCode()+"')");
				sql.append("\n and order_date >= STR_TO_DATE('"+dateStart+"', '%d/%m/%Y') ");
				sql.append("\n and h.ar_invoice_no ='"+arInvoiceNo+"'");
				sql.append("\n GROUP BY h.ar_invoice_no ,l.product_id,l.uom_id");
				sql.append("\n ORDER BY h.ar_invoice_no asc ");
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				while (rst.next()) {
					priAllQty += StockUtilsDAO.calcPriQty(conn,rst.getString("product_id"), rst.getString("uom_id"), rst.getInt("remain_pri_qty"));
                    productId = rst.getString("product_id");
				}//while
				logger.debug("priAllQty:"+priAllQty);
				
				/** del stockReturnItem <> curRequestnumber **/
				priQtyNotInCurrRequestNumber = getPriQtyExistNotInCurrentNumber(conn, arInvoiceNo, curRequestNumber, productId);
				logger.debug("priQtyNotInCurrRequestNumber:"+priQtyNotInCurrRequestNumber);
				
				/** PriQtyInit ***/
				priQtyInit = getPriQtyInt(conn,user, arInvoiceNo, c.getCustomerCode(), productId);
				logger.debug("priQtyInit:"+priQtyInit);		
						
				/**  = priQtyall +(neg priQtyInit) - priQty(exist <> curRequestNumber) **/
				logger.debug("Calc priAllQty["+priAllQty+"]-priQtyInit["+priQtyInit+"]-priQtyNotInCurrReqNum["+priQtyNotInCurrRequestNumber+"]");
				priAllQty =  priAllQty + priQtyInit - priQtyNotInCurrRequestNumber;
				priAllQty = NumberToolsUtil.round(priAllQty, 5, BigDecimal.ROUND_HALF_UP); 
				logger.debug("Result priAllQty:"+priAllQty);
				
				bean.setPriAllQty(priAllQty);
				
				/*** Calc remain priQty to pri_qty and sub_qty ***/
				String tempPriAllQty = priAllQty+"";
				double priQty = Utils.convertStrToDouble(tempPriAllQty.substring(0,tempPriAllQty.indexOf(".")));
				double subQtyTemp =Utils.convertStrToDouble(tempPriAllQty.substring(tempPriAllQty.indexOf("."),tempPriAllQty.length()));
				logger.debug("priQty["+priQty+"]");
				logger.debug("subQtyTemp["+subQtyTemp+"]");
				
				//get Uom2
				Product p = new MProduct().getStockReturnProduct(c.getProductCode(),user);
				double subQty = 0;
				if(p != null){
				   subQty = StockUtilsDAO.calcPriQtyToSubQty(conn, productId, p.getUom2(), subQtyTemp); 
				}
				logger.debug("after clac subQty["+subQty+"]");
				
				bean.setPriQty(priQty);
				bean.setSubQty(subQty);
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

	 public static double getPriQtyExistNotInCurrentNumber(Connection conn,String arInvoiceNo,String curRequestNumber,String productId) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			double existPriQty = 0;
			try {
				sql.append("\n select l.inventory_item_id as product_id,COALESCE(sum(l.pri_qty),0) as existPriQty" );
				sql.append("\n from t_stock_return_line l ");
				sql.append("\n where 1=1");
				if( !Utils.isNull(curRequestNumber).equals("")){
				  sql.append("\n and l.request_number <> '"+curRequestNumber+"'");
				}
				sql.append("\n and l.ar_invoice_no ='"+arInvoiceNo+"'");
				sql.append("\n and l.inventory_item_id ="+productId+"");
				sql.append("\n GROUP BY l.inventory_item_id ");
				
				//+ current store_return (requestNumber)
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					existPriQty  = rst.getDouble("existPriQty");
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return existPriQty;
		}

	 public static double getPriQtyInt(Connection conn,User user,String arInvoiceNo,String customerCode,String productId) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			double existPriQty = 0;
			try {
				sql.append("\n select COALESCE(sum(l.pri_qty),0) as init_pri_Qty" );
				sql.append("\n from pens.t_stock_return_init l ");
				sql.append("\n where 1=1");
				sql.append("\n and l.customer_code = '"+customerCode+"'");
				sql.append("\n and l.ar_invoice_no ='"+arInvoiceNo+"'");
				sql.append("\n and l.inventory_item_id ="+productId+"");
				sql.append("\n and l.user_id ="+user.getId()+"");
				sql.append("\n GROUP BY l.inventory_item_id ");
				
				//+ current store_return (requestNumber)
				
				logger.debug("sql:"+sql);
			
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				if (rst.next()) {
					existPriQty  = rst.getDouble("init_pri_Qty");
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return existPriQty;
		}

	 
}
