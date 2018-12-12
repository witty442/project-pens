package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import antlr.StringUtils;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.ProductCategory;
import com.isecinc.pens.bean.SalesTargetNew;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.web.receiptAll.ReceiptAllForm;

/**
 * MOrderLine Class
 * 
 * @author atiz.b
 * @version $Id: MOrderLine.java,v 1.0 07/10/2010 00:00:00 atiz.b Exp $
 * 
 */

public class MOrderLine extends I_Model<OrderLine> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "t_order_line";
	public static String COLUMN_ID = "ORDER_LINE_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "LINE_NO", "ORDER_ID", "PRODUCT_ID", "UOM_ID", "PRICE", "QTY",
			"LINE_AMOUNT", "DISCOUNT", "TOTAL_AMOUNT", "REQUEST_DATE", "SHIPPING_DATE", "PROMOTION", "CREATED_BY",
			"UPDATED_BY", "PAYMENT", "VAT_AMOUNT", "TRIP_NO","NEED_BILL","PAYMENT_METHOD", 
			"PROMOTION_FROM", "ISCANCEL", "NEED_EXPORT", "EXPORTED",
			"INTERFACES","CALL_BEFORE_SEND","SHIPMENT","COMMENT","ACTUAL_QTY",
			"ACT_NEED_BILL","PROMOTION1","CF_SHIP_DATE",
			"CF_RECEIPT_DATE","PREPAY","SHIPMENT_COMMENT"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OrderLine find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, OrderLine.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public OrderLine[] search(String whereCause) throws Exception {
		List<OrderLine> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
		if (pos.size() == 0) return null;
		OrderLine[] array = new OrderLine[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param customer
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(OrderLine line, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (line.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = line.getId();
		}
		Object[] values = { id, line.getLineNo(), line.getOrderId(), line.getProduct().getId(), line.getUom().getId(),
				line.getPrice(), line.getQty(), line.getLineAmount(), line.getDiscount(), line.getTotalAmount(),
				line.getRequestDate() != null ? DateToolsUtil.convertToTimeStamp(line.getRequestDate()): null,
				line.getShippingDate() != null ? DateToolsUtil.convertToTimeStamp(line.getShippingDate()): null,
				line.getPromotion(), activeUserID,
				activeUserID, line.getPayment(), line.getVatAmount(), line.getTripNo(),line.getNeedBill(),
				line.getPaymentMethod(),
				ConvertNullUtil.convertToString(line.getPromotionFrom()),
				line.getIscancel() != null ? line.getIscancel() : "N",
				line.getNeedExport() != null ? line.getNeedExport() : "N",
				line.getExported() != null ? line.getExported() : "N",
				line.getInterfaces() != null ? line.getInterfaces() : "N" ,
				line.getCallBeforeSend() != null ? line.getCallBeforeSend() : "N",
				!org.apache.commons.lang.StringUtils.isEmpty(line.getShipment())  ? line.getShipment() : "N",
				line.getComment()	,	line.getActualQty() ,line.getActNeedBill(),
				line.getPromotion1() != null ? line.getPromotion1() : "N",
				line.getConfirmShipDate() != null ? DateToolsUtil.convertToTimeStamp(line.getConfirmShipDate()): null,
				line.getConfirmReceiptDate() != null ? DateToolsUtil.convertToTimeStamp(line.getConfirmReceiptDate()): null,
				line.getPrepay() != null ? line.getPrepay() : "N",
				line.getShipComment()
				};
		
		if (super.save(TABLE_NAME, columns, values, line.getId(), conn)) {
			line.setId(id);
		}
		return true;
	}

	/**
	 * Look Up
	 */
	public List<OrderLine> lookUp(int orderId) {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " AND ISCANCEL='N' ORDER BY TRIP_NO, LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	public List<OrderLine> lookUp(Connection conn,int orderId) {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " AND ISCANCEL='N' ORDER BY TRIP_NO, LINE_NO ";
			pos = super.search(conn,TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	/**
	 * Check duplicate in tripNo 
	 * @param orderId
	 * @param tripNo
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public boolean isProductDuplicateInTrip(Connection conn,int orderId,int tripNo,int productId,String promotion1)  throws Exception{
		boolean isDup = false;
		PreparedStatement ps = null;
		ResultSet rst =null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			StringBuffer sql = new StringBuffer("");
			sql.append(" select count(*) as c FROM t_order_line l WHERE 1=1  \n");
			sql.append(" AND l.ORDER_ID = " + orderId + " AND l.trip_no="+tripNo+" AND l.product_id ="+productId+" AND l.promotion1='"+promotion1+"' AND l.ISCANCEL='N' \n");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()){
				if(rst.getInt("c") > 0)
				   isDup = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ps != null){
				ps.close();ps = null;
			}
			if(rst != null){
				rst.close();rst = null;
			}
		}
		return isDup;
	}
	
	/**
	 * Look Up
	 */
	public List<OrderLine> lookUpNew(int orderId)  throws Exception{
		List<OrderLine> pos = new ArrayList<OrderLine>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst =null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			StringBuffer sql = new StringBuffer("");
			sql.append(" select l.*,  \n");
			sql.append(" p.CODE as PRODUCT_CODE,p.NAME as PRODUCT_NAME,p.DESCRIPTION as PRODUCT_DESC,p.ISACTIVE as PRODUCT_ISACTIVE ,p.PRODUCT_CATEGORY_ID, \n");
			sql.append(" u.CODE as UOM_CODE ,u.NAME as UOM_NAME ,u.ISACTIVE as UOM_ISACTIVE, \n");
			sql.append(" pc.product_category_id,pc.NAME as PC_NAME ,pc.ISACTIVE as PC_ISACTIVE \n");
			sql.append(" FROM t_order_line l ,m_product p, m_uom u ,m_product_category pc \n");
			sql.append(" WHERE l.product_id = p.product_id \n");
			sql.append(" AND p.product_category_id = pc.product_category_id \n");
			sql.append(" AND l.uom_id = u.uom_id \n");
			sql.append(" AND l.ORDER_ID = " + orderId + " AND l.ISCANCEL='N' ORDER BY l.TRIP_NO, l.LINE_NO \n");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
				OrderLine line = new OrderLine();
				line.setId(rst.getInt("ORDER_LINE_ID"));
				line.setLineNo(rst.getInt("LINE_NO"));
				line.setOrderId(rst.getInt("ORDER_ID"));
				
				//setProduct(new MProduct().find(rst.getString("PRODUCT_ID")));
				Product p = new Product();
				p.setId(rst.getInt("PRODUCT_ID"));
				p.setCode(rst.getString("PRODUCT_CODE").trim());
				p.setName(rst.getString("PRODUCT_NAME").trim());
				p.setDescription(ConvertNullUtil.convertToString(rst.getString("PRODUCT_DESC")).trim());
				p.setUom(new MUOM().find(rst.getString("UOM_ID")));
				p.setIsActive(rst.getString("PRODUCT_ISACTIVE").trim());
				//p.setProductCategory(new MProductCategory().find(String.valueOf(rst.getInt("PRODUCT_CATEGORY_ID"))));
				 ProductCategory pc = new ProductCategory();
				 pc.setId(rst.getInt("PRODUCT_CATEGORY_ID"));
				 pc.setName(rst.getString("PC_NAME"));
				 pc.setIsActive(rst.getString("PC_ISACTIVE"));
				p.setProductCategory(pc);
				
				line.setProduct(p);
				//setUom(new MUOM().find(rst.getString("UOM_ID")));
				UOM u = new UOM();
				u.setId(rst.getString("UOM_ID").trim());
				u.setCode(rst.getString("UOM_CODE").trim());
				u.setName(rst.getString("UOM_NAME").trim());
				u.setIsActive(rst.getString("UOM_ISACTIVE").trim());
				line.setUom(u);
				
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
				
				line.setNeedBill(rst.getDouble("NEED_BILL"));
				
				line.setPaymentType(rst.getInt("PAYMENT_TYPE"));
				line.setPaymentMethod(rst.getString("PAYMENT_METHOD"));
				
				line.setPromotion1(rst.getString("PROMOTION1"));
				
				line.setShipment(rst.getString("SHIPMENT"));
				line.setComment(rst.getString("COMMENT"));
				line.setActualQty(rst.getDouble("ACTUAL_QTY"));
				line.setActNeedBill(rst.getDouble("ACT_NEED_BILL"));
				
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
				
				
				if (rst.getTimestamp("CF_SHIP_DATE") != null)
					line.setConfirmShipDate(DateToolsUtil.convertToString(rst.getTimestamp("CF_SHIP_DATE")));
				
				if (rst.getTimestamp("CF_RECEIPT_DATE") != null)
					line.setConfirmReceiptDate(DateToolsUtil.convertToString(rst.getTimestamp("CF_RECEIPT_DATE")));
				
				line.setPrepay(rst.getString("PREPAY"));
				line.setShipComment(rst.getString("SHIPMENT_COMMENT"));
				
				pos.add(line);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(conn != null){
				conn.close();conn = null;
			}
			if(ps != null){
				ps.close();ps = null;
			}
			if(rst != null){
				rst.close();rst = null;
			}
		}
		return pos;
	}
	
	public List<OrderLine> searchOrderLineConfirmReceipt(ReceiptAllForm receiptAllForm)  throws Exception{
		List<OrderLine> pos = new ArrayList<OrderLine>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst =null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			String whereCause = "";
			if (receiptAllForm.getOrder().getOrderDateTo().trim().length() > 0) {

				if (receiptAllForm.getOrder().getOrderDateFrom().trim().length() > 0) {
					whereCause += "\n AND l.SHIPPING_DATE >= '"
							+ DateToolsUtil.convertToTimeStamp(receiptAllForm
									.getOrder().getOrderDateFrom().trim())
							+ "'";
				}
				if (receiptAllForm.getOrder().getOrderDateTo().trim().length() > 0) {
					whereCause += "\n AND l.SHIPPING_DATE <= '"
							+ DateToolsUtil.convertToTimeStamp(receiptAllForm.getOrder().getOrderDateTo().trim()) + "'";
				}
			} else {
				if (receiptAllForm.getOrder().getOrderDateFrom().trim().length() > 0) {
					whereCause += "\n AND l.SHIPPING_DATE = '"
							+ DateToolsUtil.convertToTimeStamp(receiptAllForm.getOrder().getOrderDateFrom().trim())
							+ "'";
				}
			}

			if (receiptAllForm.getOrder().getPaymentMethod().trim().length() > 0) {
				whereCause += "\n AND l.PAYMENT_METHOD = '"
						+ receiptAllForm.getOrder().getPaymentMethod().trim()
						+ "'";
			}
			
			if("CS".equals(receiptAllForm.getOrder().getPaymentMethod().trim())){
				whereCause+="\n AND l.ACT_NEED_BILL > 0";
			}else{
				whereCause+="\n AND l.NEED_BILL > 0";
			}

	
			StringBuffer sql = new StringBuffer("");
			sql.append(" select l.*,  \n");
			sql.append(" p.CODE as PRODUCT_CODE,p.NAME as PRODUCT_NAME,p.DESCRIPTION as PRODUCT_DESC,p.ISACTIVE as PRODUCT_ISACTIVE ,p.PRODUCT_CATEGORY_ID, \n");
			sql.append(" u.CODE as UOM_CODE ,u.NAME as UOM_NAME ,u.ISACTIVE as UOM_ISACTIVE, \n");
			sql.append(" pc.product_category_id,pc.NAME as PC_NAME ,pc.ISACTIVE as PC_ISACTIVE \n");
			sql.append(" FROM t_order_line l ,m_product p, m_uom u ,m_product_category pc \n");
			sql.append(" WHERE l.product_id = p.product_id \n");
			sql.append(" AND p.product_category_id = pc.product_category_id \n");
			sql.append(" AND l.uom_id = u.uom_id \n");
			sql.append(whereCause +"\n AND SHIPMENT = 'Y' AND (PAYMENT = 'N' or PAYMENT is null) AND qty > 0 ");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
				OrderLine line = new OrderLine();
				line.setId(rst.getInt("ORDER_LINE_ID"));
				line.setLineNo(rst.getInt("LINE_NO"));
				line.setOrderId(rst.getInt("ORDER_ID"));
				
				//setProduct(new MProduct().find(rst.getString("PRODUCT_ID")));
				Product p = new Product();
				p.setId(rst.getInt("PRODUCT_ID"));
				p.setCode(rst.getString("PRODUCT_CODE").trim());
				p.setName(rst.getString("PRODUCT_NAME").trim());
				p.setDescription(ConvertNullUtil.convertToString(rst.getString("PRODUCT_DESC")).trim());
				p.setUom(new MUOM().find(rst.getString("UOM_ID")));
				p.setIsActive(rst.getString("PRODUCT_ISACTIVE").trim());
				//p.setProductCategory(new MProductCategory().find(String.valueOf(rst.getInt("PRODUCT_CATEGORY_ID"))));
				 ProductCategory pc = new ProductCategory();
				 pc.setId(rst.getInt("PRODUCT_CATEGORY_ID"));
				 pc.setName(rst.getString("PC_NAME"));
				 pc.setIsActive(rst.getString("PC_ISACTIVE"));
				p.setProductCategory(pc);
				
				line.setProduct(p);
				//setUom(new MUOM().find(rst.getString("UOM_ID")));
				UOM u = new UOM();
				u.setId(rst.getString("UOM_ID").trim());
				u.setCode(rst.getString("UOM_CODE").trim());
				u.setName(rst.getString("UOM_NAME").trim());
				u.setIsActive(rst.getString("UOM_ISACTIVE").trim());
				line.setUom(u);
				
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
				
				line.setNeedBill(rst.getDouble("NEED_BILL"));
				
				line.setPaymentType(rst.getInt("PAYMENT_TYPE"));
				line.setPaymentMethod(rst.getString("PAYMENT_METHOD"));
				
				line.setPromotion1(rst.getString("PROMOTION1"));
				
				line.setShipment(rst.getString("SHIPMENT"));
				line.setComment(rst.getString("COMMENT"));
				line.setActualQty(rst.getDouble("ACTUAL_QTY"));
				line.setActNeedBill(rst.getDouble("ACT_NEED_BILL"));
				
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
				
				
				if (rst.getTimestamp("CF_SHIP_DATE") != null)
					line.setConfirmShipDate(DateToolsUtil.convertToString(rst.getTimestamp("CF_SHIP_DATE")));
				
				if (rst.getTimestamp("CF_RECEIPT_DATE") != null)
					line.setConfirmReceiptDate(DateToolsUtil.convertToString(rst.getTimestamp("CF_RECEIPT_DATE")));
				
				line.setPrepay(rst.getString("PREPAY"));
				line.setShipComment(rst.getString("SHIPMENT_COMMENT"));
				
				Order order = new MOrder().find(String.valueOf(line.getOrderId()));
				Member member = new MMember().find(String.valueOf(order.getCustomerId()));

				line.setCustomerName(order.getCustomerName());
				line.setCustomerCode(member.getCode());
				line.setDeliveryGroup(member.getDeliveryGroup());
				line.setProduct(new MProduct().find(String.valueOf(line.getProduct().getId())));
				
				// Add Default Credit Card Information
				if("CR".equals(line.getPaymentMethod())){
					line.setCreditCardName(member.getCardName());
					line.setCreditCardNo(member.getCreditCardNo());
					line.setExpiredDate(member.getExpiredDate());
					line.setBank(member.getCreditCardBank());
				}

				line.setOrderNo(order.getOrderNo());
				pos.add(line);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(conn != null){
				conn.close();conn = null;
			}
			if(ps != null){
				ps.close();ps = null;
			}
			if(rst != null){
				rst.close();rst = null;
			}
		}
		return pos;
	}

	/**
	 * Look Up By OrderNo for(report)
	 */
	public List<OrderLine> lookUpByCondition(String whereSQL) {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		try {
			String whereCause = " AND ORDER_ID IN (SELECT ORDER_ID FROM t_order where "+whereSQL+" )  ORDER BY ORDER_ID";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Delete Promotion
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean deletePromotion(int orderId, Connection conn) throws Exception {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		String deleteId = "";
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " AND PROMOTION = 'Y' ORDER BY LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
			for (OrderLine l : pos)
				deleteId += "," + l.getId();
			if (deleteId.length() > 0) {
				deleteId = deleteId.substring(1);
				return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Delete All Lines
	 * 
	 * @param orderId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean deleteAllLines(int orderId, Connection conn) throws Exception {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		String deleteId = "";
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " ORDER BY LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
			for (OrderLine l : pos)
				deleteId += "," + l.getId();
			if (deleteId.length() > 0) {
				deleteId = deleteId.substring(1);
				return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Delete
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String deleteId, Connection conn) throws Exception {
		return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
	}

	private static String COMPARE_FOR_AMOUNT = "AMT";
	private static String COMPARE_FOR_BASE_QTY = "QTY";
	private static String COMPARE_FOR_SUB_QTY = "SUBQTY";
	private static String COMPARE_FOR_BASE_PROMO = "PROMO";
	private static String COMPARE_FOR_SUB_PROMO = "SUBPROMO";

	/**
	 * Compare Sales Target
	 * 
	 * @param targets
	 * @throws Exception
	 */
	public void compareSalesTarget(SalesTargetNew[] targets,String dateFrom,String dateTo) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			String sql = "";
			double totalAmount = 0;
			double percentCompare = 0;
			int qty = 0;
			// List<ProductPrice> pps;
			// MProductPrice mProductPrice = new MProductPrice();
			// UOM baseUOM = null;
			// UOM uom = null;
			// int capacity;
			// int tempB = 0;
			// int tempS = 0;
			for (SalesTargetNew t : targets) {
				// logger.debug(t);
				totalAmount = 0;
				// compare amount
				sql = createCompareQuery(t, COMPARE_FOR_AMOUNT,dateFrom,dateTo);
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					totalAmount += rst.getDouble("amount");
				}
				t.setSoldAmount(totalAmount);
				// calculate %
				percentCompare = 100 / t.getTargetAmount() * t.getSoldAmount();
				t.setPercentCompare(percentCompare);

				// compare qty base
				sql = createCompareQuery(t, COMPARE_FOR_BASE_QTY,dateFrom,dateTo);
				qty = 0;
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					qty += rst.getInt("amount");
				}
				t.setBaseQty(qty);
				// sub
				sql = createCompareQuery(t, COMPARE_FOR_SUB_QTY,dateFrom,dateTo);
				qty = 0;
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					qty += rst.getInt("amount");
				}
				t.setSubQty(qty);

				// compare promo base
				sql = createCompareQuery(t, COMPARE_FOR_BASE_PROMO,dateFrom,dateTo);
				qty = 0;
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					qty += rst.getInt("amount");
				}
				t.setBasePromo(qty);
				// sub
				sql = createCompareQuery(t, COMPARE_FOR_SUB_PROMO,dateFrom,dateTo);
				qty = 0;
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					qty += rst.getInt("amount");
				}
				t.setSubPromo(qty);

				// uom conversion
				// pps = mProductPrice.lookUp(t.getProduct().getId(), t.getPriceList().getId());
				// for (ProductPrice pp : pps) {
				// if (pp.getUom().getId().equalsIgnoreCase(t.getProduct().getUom().getId())) {
				// baseUOM = pp.getUom();
				// } else {
				// uom = pp.getUom();
				// }
				// }
				// capacity = new Double(new MUOMConversion().getCapacity(baseUOM, uom, t.getProduct())).intValue();
				// // transfer from subQty>baseQty
				// tempB = t.getSubQty() / capacity;
				// tempS = t.getSubQty() % capacity;
				//
				// t.setBaseQty(t.getBaseQty() + tempB);
				// t.setSubQty(tempS);
				//
				// // transfer from subQty>baseQty
				// tempB = t.getSubPromo() / capacity;
				// tempS = t.getSubPromo() % capacity;
				//
				// t.setBasePromo(t.getBasePromo() + tempB);
				// t.setSubPromo(tempS);
				// logger.debug("xxx");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e) {}
			try {
				stmt.close();
			} catch (Exception e) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}

	private String createCompareQuery(SalesTargetNew t, String comparefor,String dateFrom,String dateTo) throws Exception {
		String sql = "";
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_AMOUNT)) {
			sql = "select sum(line_amount) amount from t_order_line \r\n";
		} else {
			sql = "select sum(qty) amount from t_order_line \r\n";
		}
		sql += "where product_id = " + t.getProduct().getId() + " \r\n";
		sql += "  and ISCANCEL = 'N' \r\n";
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_BASE_QTY)) {
			sql += "  and UOM_ID = '" + t.getProduct().getUom().getId() + "' ";
			sql += "  and PROMOTION = 'N' ";
		}
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_SUB_QTY)) {
			sql += "  and UOM_ID <> '" + t.getProduct().getUom().getId() + "' ";
			sql += "  and PROMOTION = 'N' ";
		}
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_BASE_PROMO)) {
			sql += "  and UOM_ID = '" + t.getProduct().getUom().getId() + "' ";
			sql += "  and PROMOTION = 'Y' ";
		}
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_SUB_PROMO)) {
			sql += "  and UOM_ID <> '" + t.getProduct().getUom().getId() + "' ";
			sql += "  and PROMOTION = 'Y' ";
		}
		sql += "  and ORDER_ID in ( \r\n";
		sql += "	select ORDER_ID from t_order where doc_status = 'SV' \r\n";
		sql += "	  and order_date >= '" + (DateToolsUtil.convertToTimeStamp(dateFrom==null?t.getTargetFrom():dateFrom)) + "' \r\n";
		sql += "	  and order_date <= '" + (DateToolsUtil.convertToTimeStamp(dateTo==null?t.getTargetTo():dateTo)) + "' \r\n";
		sql += "	  and user_id = " + t.getUserId() + " \r\n";
		sql += "  ) \r\n";

		return sql;
	}
	
	public int getMaxLineNo(int p_order_id) throws Exception{
		StringBuffer sql = new StringBuffer("SELECT COALESCE(MAX(line_no),0) as MAX_LINE_NO from t_order_line where order_id = ?");
		Connection conn = null;
		if(p_order_id == 0){
			throw new Exception("CANNOT FIND MAX LINE NO : ORDER_ID IS ZERO");
		}
		
		int ret = 0 ;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
			ppstmt.setInt(1,p_order_id );
			
			ResultSet rset = ppstmt.executeQuery();
			if(rset.next()){
				ret = rset.getInt("MAX_LINE_NO");
			}
		}
		catch(Exception ex){
			new Exception("CANNOT FIND MAX LINE NO "+ex.getMessage());
		}
		
		return ret;
	}
	
	public boolean isTripCanCancel(int orderId,int tripNo) throws Exception{
		StringBuffer sql = new StringBuffer("SELECT MAX(SHIPMENT) shipment , max(PAYMENT) payment FROM t_order_line where order_id= ? and trip_no = ?");
		Connection conn = null;
		if(tripNo == 0){
			throw new Exception("CANNOT FIND SHIPMENT : TRIP NO IS ZERO");
		}
		
		if(orderId == 0){
			throw new Exception("CANNOT FIND SHIPMENT : ORDER_ID IS ZERO");
		}
		
		boolean isShipment = false;
		boolean isPayment = false;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
			ppstmt.setInt(1,orderId );
			ppstmt.setInt(2,tripNo );
			
			ResultSet rset = ppstmt.executeQuery();
			if(rset.next()){
				isShipment = "Y".equals(rset.getString("shipment"));
				isPayment = "Y".equals(rset.getString("payment"));
			}
		}
		catch(Exception ex){
			new Exception("CANNOT FIND SHIPMENT "+ex.getMessage());
		}
		
		return isShipment||isPayment;
	}
	
	public boolean isTripPayment(int orderId,int tripNo) throws Exception{
		StringBuffer sql = new StringBuffer("SELECT MAX(PAYMENT) payment FROM t_order_line where order_id= ? and trip_no = ?");
		Connection conn = null;
		if(tripNo == 0){
			throw new Exception("CANNOT FIND PAYMENT : TRIP NO IS ZERO");
		}
		
		if(orderId == 0){
			throw new Exception("CANNOT FIND PAYMENT : ORDER_ID IS ZERO");
		}
		
		boolean ret = false ;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
			ppstmt.setInt(1,orderId );
			ppstmt.setInt(2,tripNo );
			
			ResultSet rset = ppstmt.executeQuery();
			if(rset.next()){
				ret = "Y".equals(rset.getString("payment"));
			}
		}
		catch(Exception ex){
			new Exception("CANNOT FIND PAYMENT "+ex.getMessage());
		}
		
		return ret;
	}
}
