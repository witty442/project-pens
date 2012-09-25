package com.isecinc.pens.report.invoicedetail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;
import util.NumberToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.process.order.OrderProcess;
import com.isecinc.pens.web.sales.OrderForm;

/**
 * InvoiceDetailReportProcess Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoiceDetailReportProcess extends I_ReportProcess<InvoiceDetailReport> {

	/**
	 * Search for performance report.
	 */
	public List<InvoiceDetailReport> doReport(InvoiceDetailReport t, User user, Connection conn) throws Exception {
		int i =0;
		List<InvoiceDetailReport> pos = new ArrayList<InvoiceDetailReport>();
		List<OrderLine> orderLines = new ArrayList<OrderLine>();

		try {
			
			String p_productCodeFrom = t.getProductCodeFrom();
			String p_productCodeTo = t.getProductCodeTo();
			
			if (!user.getRole().getKey().equals(User.DD)) {
				List<String> orderNoList = searchOrderNoList(t, user, conn);
				if(orderNoList != null && orderNoList.size() > 0){
					for(i=0;i<orderNoList.size();i++){
						String orderNo = (String)orderNoList.get(i);
						List<OrderLine> linesByOrderNo = searchOrderLine(orderNo,p_productCodeFrom,p_productCodeTo,conn);
						orderLines.addAll(new OrderProcess().fillLinesShow(linesByOrderNo));
					}
				}
			}
			
			if(orderLines != null && orderLines.size() > 0){
				String orderIdTem = "";
				int g = 0;
				int n = 0;
				i =0;
				while (i < orderLines.size()) {
					OrderLine orderLine = orderLines.get(i);
					
					InvoiceDetailReport item = new InvoiceDetailReport();
					item.setOrderNo(orderLine.getOrderNo());
					item.setCustomerName(orderLine.getCustomerName());
					
					item.setProductName(orderLine.getProduct().getCode()+" "+orderLine.getProduct().getName());
					//logger.debug("FullUom:"+orderLine.getFullUom());
					item.setFullUom(orderLine.getFullUom());
					
					if(orderLine.getPromotion().equals("Y")){
						if( (orderLine.getProduct().getUom().getId()==orderLine.getUom1().getId() && orderLine.getProduct().getUom().getId()==orderLine.getUom2().getId() )
							|| (orderLine.getProduct().getUom().getId() != orderLine.getUom1().getId() && orderLine.getProduct().getUom().getId() != orderLine.getUom2().getId()) ){
							
							item.setQtyString(new Double(orderLine.getQty1()).intValue()+ new Double(orderLine.getQty2()).intValue()+"");
						}else{
							item.setQtyString(new Double(orderLine.getQty1()).intValue()+"/"+new Double(orderLine.getQty2()).intValue());
						}
					}else{
						item.setQtyString(new Double(orderLine.getQty1()).intValue()+"/"+new Double(orderLine.getQty2()).intValue());
					}
					
					if(orderLine.getPromotion().equals("Y")){
					   item.setPriceString("0.000000");
					}else{
					   item.setPriceString(NumberToolsUtil.decimalFormat(orderLine.getPrice1(),NumberToolsUtil.format_current_5_digit)+"/"+NumberToolsUtil.decimalFormat(orderLine.getPrice2(),NumberToolsUtil.format_current_5_digit));
					}
					item.setLineAmount(NumberToolsUtil.decimalFormat(orderLine.getLineAmount(),NumberToolsUtil.format_current_5_digit));
					item.setDiscountAmount(NumberToolsUtil.decimalFormat(orderLine.getDiscount()));
					item.setTotalAmount(NumberToolsUtil.decimalFormat(orderLine.getLineAmount()-orderLine.getDiscount()));
					item.setShippingDate(orderLine.getShippingDate());
					item.setRequestDate(orderLine.getRequestDate());
		
					item.setCustomerName(orderLine.getCustomerName());
					item.setCustomerCode(orderLine.getCustomerCode());
					item.setOrderDate(orderLine.getOrderDate());

					//item.setLineAmount1(orderLine.getLineAmount());
					item.setTotalAmount1(orderLine.getLineAmount()-orderLine.getDiscount());
					item.setVatAmount(orderLine.getVatAmount());
					
					item.setStatus(orderLine.getStatus());
					
					if(orderLine.getPromotion().equals("Y")){
						item.setPromotion("Y");
					}else{
						item.setPromotion("");
					}
					
					Order order = new MOrder().find(orderLine.getOrderId()+"");
					item.setOrderAmt(order.getTotalAmount());
					item.setOrderVATAmt(order.getVatAmount());
					item.setOrderAmtIncludeVAT(order.getNetAmount());
					
					/** Check is Header by Order_id **/
					if( !String.valueOf(orderLine.getOrderId()).equals(orderIdTem)){
						item.setIsHeader("true");
						g++;//count group 
						n=0;
					}else{
						item.setIsHeader("false");
					}
					n++;// reset by group
					item.setGroupId(g);
					item.setId(g+"."+n);
					
					pos.add(item);
					i++;
					
					orderIdTem = String.valueOf(orderLine.getOrderId());
				}//while
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			
		}
		return pos;
	}

	
	public List<String> searchOrderNoList(InvoiceDetailReport t,User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<String> pos = new ArrayList<String>();
		StringBuilder sql = new StringBuilder();
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT distinct h.order_no from t_order h ,t_order_line l,m_customer m \n");
			sql.append("\n  where 1=1 \n");
			sql.append("\n  and h.order_id = l.order_id  \n");
			sql.append("\n  and h.customer_id = m.customer_id \n");
			if( !Utils.isNull(t.getOrderNoFrom()).equals("")
					&& !Utils.isNull(t.getOrderNoFrom()).equals("")){
				sql.append(" and h.order_no >='"+t.getOrderNoFrom()+"' \n");
				sql.append(" and h.order_no <='"+t.getOrderNoTo()+"' \n");
			}
			
			  if( !Utils.isNull(t.getOrderDateFrom()).equals("")
					&&	!Utils.isNull(t.getOrderDateTo()).equals("")	){
					
				sql.append(" and h.order_date >='"+DateToolsUtil.convertToTimeStamp(t.getOrderDateFrom())+"' \n");
				sql.append(" and h.order_date <='"+DateToolsUtil.convertToTimeStamp(t.getOrderDateTo())+"' \n");
			}
            sql.append("\n AND l.ISCANCEL='N' ");
			sql.append("\n ORDER BY h.order_id  ");
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				pos.add(rst.getString("order_no"));
			}
			
			return pos;
		}catch(Exception e){
			throw e;
		}
	}
	/**
	 * searchOrderLine
	 * @param t
	 * @param user
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<OrderLine> searchOrderLine(String orderNo,String productCodeFrom ,String productCodeTo,Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<OrderLine> pos = new ArrayList<OrderLine>();
		StringBuilder sql = new StringBuilder();
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT l.* ,h.doc_status as status,h.order_no,h.order_date,m.name as customer_name ,m.code as customer_code from t_order h,t_order_line l,m_customer m ,m_product pd ");
			sql.append("\n  where h.order_id = l.order_id  ");
			sql.append("\n  and h.customer_id = m.customer_id ");
			sql.append("\n  and pd.product_id = l.product_id ");
			sql.append("\n  AND h.order_no ='"+orderNo+"'");
			if(productCodeFrom!=null && productCodeFrom.length() > 0)
				sql.append("\n  AND pd.code >='"+productCodeFrom+"'");
			
			if(productCodeTo!=null && productCodeTo.length() > 0)
				sql.append("\n  AND pd.code <='"+productCodeTo+"'");
			
			sql.append("\n  AND l.ISCANCEL='N' ");
			sql.append("\n  ORDER BY l.TRIP_NO, l.LINE_NO  \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			while (rst.next()) {
				OrderLine line = new OrderLine();
				line.setId(rst.getInt("ORDER_LINE_ID"));
				line.setLineNo(rst.getInt("LINE_NO"));
				line.setOrderId(rst.getInt("ORDER_ID"));
				line.setProduct(new MProduct().find(rst.getString("PRODUCT_ID")));
				line.setUom(new MUOM().find(rst.getString("UOM_ID")));
				line.setPrice(rst.getDouble("PRICE"));
				line.setQty(rst.getDouble("QTY"));
				line.setLineAmount(rst.getDouble("LINE_AMOUNT"));
				line.setDiscount(rst.getDouble("DISCOUNT"));
				line.setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
				line.setShippingDate("");
				line.setRequestDate("");
				line.setPromotion(ConvertNullUtil.convertToString(rst.getString("PROMOTION")).trim());
				if (rst.getTimestamp("SHIPPING_DATE") != null)
					line.setShippingDate(DateToolsUtil.convertToString(rst.getTimestamp("SHIPPING_DATE")));
				if (rst.getTimestamp("REQUEST_DATE") != null)
					line.setRequestDate(DateToolsUtil.convertToString(rst.getTimestamp("REQUEST_DATE")));
				line.setPayment(rst.getString("PAYMENT"));
				line.setArInvoiceNo(ConvertNullUtil.convertToString(rst.getString("AR_INVOICE_NO")));
				line.setVatAmount(rst.getDouble("VAT_AMOUNT"));
				line.setIscancel(ConvertNullUtil.convertToString(rst.getString("ISCANCEL")));
				line.setTripNo(rst.getInt("TRIP_NO"));
				line.setPromotionFrom(ConvertNullUtil.convertToString(rst.getString("PROMOTION_FROM")));
				try {
					line.setNeedExport(rst.getString("NEED_EXPORT"));
				} catch (Exception e) {}
				try {
					line.setExported(rst.getString("EXPORTED"));
				} catch (Exception e) {}
				try {
					line.setInterfaces(rst.getString("INTERFACES"));
					line.setCallBeforeSend(rst.getString("CALL_BEFORE_SEND"));
				} catch (Exception e) {}
				/** Optional **/
				line.setOrderNo(rst.getString("order_no"));
				line.setCustomerName(rst.getString("customer_name"));
				line.setCustomerCode(rst.getString("customer_code"));
				line.setOrderDate(rst.getDate("order_date"));
				line.setStatus(rst.getString("status"));
				pos.add(line);
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
