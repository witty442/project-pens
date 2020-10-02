package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.document.OrderDocumentProcess;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateToolsUtil;
import com.pens.util.seq.SequenceProcessAll;

/**
 * MOrder Class
 * 
 * @author atiz.b
 * @version $Id: MOrder.java,v 1.0 07/10/2010 00:00:00 atiz.b Exp $
 * 
 */

public class MOrder extends I_Model<Order> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "pensso.t_order";
	public static String COLUMN_ID = "ORDER_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "ORDER_NO", "ORDER_DATE", "ORDER_TYPE", "CUSTOMER_ID", "CUSTOMER_NAME",
			"BILL_ADDRESS_ID", "SHIP_ADDRESS_ID", "PRICELIST_ID", "PAYMENT_TERM", "VAT_CODE", "VAT_RATE",
			"PAYMENT_METHOD", "SHIPPING_DAY", "SHIPPING_TIME", "TOTAL_AMOUNT", "VAT_AMOUNT", "NET_AMOUNT",
			"INTERFACES", "PAYMENT", "SALES_ORDER_NO", "AR_INVOICE_NO", "USER_ID", "DOC_STATUS", "CREATED_BY",
			"UPDATED_BY", "ISCASH", "ORDER_TIME", "REMARK", "CALL_BEFORE_SEND","ORA_BILL_ADDRESS_ID"
			,"ORA_SHIP_ADDRESS_ID","org","PO_NUMBER","TOTAL_AMOUNT_NON_VAT","VAN_PAYMENT_METHOD"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Order find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Order.class);
	}

	public Order find(Connection conn,String id) throws Exception {
		return super.find(conn,id, TABLE_NAME, COLUMN_ID, Order.class);
	}
	public Order findByWhereCond(String whereSql) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return findByWhereCond(conn, whereSql);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public Order findByWhereCond(Connection conn ,String whereSql) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Order p = null;
		try{
			String sql ="\n select * from t_order "+whereSql ;
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				p = new Order(rst);
			}
			return p;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
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
	public Order[] search(String whereCause) throws Exception {
		List<Order> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Order.class);
		if (pos.size() == 0) return null;
		Order[] array = new Order[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	public Order[] searchOpt(String whereCause) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Connection conn = null;
		List<Order> pos = new ArrayList<Order>();
		Order o = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			String sql ="\n select o.*"
					  +"\n ,(select count(*) from pensso.t_order_line l "
					  +"\n   where l.order_id = o.order_id and l.promotion ='S') as promotion_s_count"
					  +"\n ,c.code as customer_code"
					  +"\n ,a.line1,a.line2,a.line3,a.line4 "
					  +"\n ,(select d.name from pensso.m_district d where d.district_id = a.district_id) as district_name "
					  +"\n ,a.province_name,a.postal_code "
					  +"\n from pensso.t_order o ,pensso.m_customer c "
					  +"\n ,( "
	                  +"\n   select distinct customer_id,address_id,district_id,line1,line2,line3,line4 "
	                  +"\n   ,province_name,postal_code ,province_id "
	                  +"\n   from pensso.m_address where purpose ='S'"
	                  +"\n  )a "
			          +"\n where o.customer_id = a.customer_id "
			          +"\n and c.customer_id = a.customer_id "
			          +"\n and a.address_id = o.ship_address_id "
		              +"\n"+whereCause ;
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				o = new Order();
				o.setId(rst.getLong("ORDER_ID"));
				o.setOrderNo(rst.getString("ORDER_NO"));
				o.setOrderDate(DateToolsUtil.convertToString(rst.getTimestamp("ORDER_DATE")));
				o.setOrderTime(rst.getString("ORDER_TIME"));
				o.setOrderType(rst.getString("ORDER_TYPE").trim());
				o.setCustomerId(rst.getLong("CUSTOMER_ID"));
				o.setCustomerCode(rst.getString("CUSTOMER_CODE").trim());
				o.setCustomerName(rst.getString("CUSTOMER_NAME").trim());
				o.setBillAddressId(rst.getInt("BILL_ADDRESS_ID"));
				o.setShipAddressId(rst.getInt("SHIP_ADDRESS_ID"));
				o.setPriceListId(rst.getInt("PRICELIST_ID"));
				o.setPaymentTerm(rst.getString("PAYMENT_TERM").trim());
				o.setVatCode(rst.getString("VAT_CODE").trim());
				o.setVatRate(rst.getDouble("VAT_RATE"));
				o.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				o.setShippingDay(ConvertNullUtil.convertToString(rst.getString("SHIPPING_DAY")).trim());
				o.setShippingTime(ConvertNullUtil.convertToString(rst.getString("SHIPPING_TIME")).trim());
				o.setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
				o.setTotalAmountNonVat(rst.getDouble("TOTAL_AMOUNT_NON_VAT"));
				o.setVatAmount(rst.getDouble("VAT_AMOUNT"));
				o.setNetAmount(rst.getDouble("NET_AMOUNT"));
				o.setInterfaces(Utils.isNull(rst.getString("INTERFACES")));
				o.setPayment(rst.getString("PAYMENT").trim());
				o.setSalesOrderNo(ConvertNullUtil.convertToString(rst.getString("SALES_ORDER_NO")).trim());
				o.setArInvoiceNo(ConvertNullUtil.convertToString(rst.getString("AR_INVOICE_NO")).trim());
				o.setSalesRepresent(new MUser().find(rst.getString("USER_ID")));
				o.setDocStatus(ConvertNullUtil.convertToString(rst.getString("DOC_STATUS")).trim());
				o.setCreated(DateToolsUtil.convertFromTimestamp(rst.getTimestamp("CREATED")));
				o.setExported(rst.getString("EXPORTED"));
				o.setIsCash(rst.getString("ISCASH"));
				o.setRemark(ConvertNullUtil.convertToString(rst.getString("remark")).trim());
				o.setCallBeforeSend(rst.getString("CALL_BEFORE_SEND"));
				// set display
				o.setDisplayLabel();
				
				//wit 20110804
				o.setPaymentCashNow("CS".equals(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim())?true:false);
				
				// Add Oracle Reference Address ID
				o.setOraBillAddressID(rst.getInt("ORA_BILL_ADDRESS_ID"));
				o.setOraShipAddressID(rst.getInt("ORA_SHIP_ADDRESS_ID"));
				
				//Wit Edit 15/05/2012
				o.setOrg(rst.getString("org"));
				if( !"".equals(ConvertNullUtil.convertToString(o.getOrg()))){
					o.setPlaceOfBilled(new MOrgRule().getOrgRule(o.getOrg()).getName());
				}
				o.setPoNumber(Utils.isNull(rst.getString("po_number")));
				o.setVanPaymentMethod(Utils.isNull(rst.getString("van_payment_method")));
				
				//Case found special promotion
				o.setPromotionSP(rst.getInt("promotion_s_count") >0?true:false);
				
				String addressSummary  = Utils.isNull(rst.getString("line1"))+" "+Utils.isNull(rst.getString("line2"))+" "+Utils.isNull(rst.getString("line3"));
			           addressSummary += " "+Utils.isNull(rst.getString("line4"))+" "+Utils.isNull(rst.getString("province_name"))+" "+Utils.isNull(rst.getString("postal_code"));
			    o.setAddressSummary(addressSummary);
			    
			    o.setPickingNo(rst.getString("PICKING_NO"));
				pos.add(o);
			}
			Order[] array = new Order[pos.size()];
			array = pos.toArray(array);
			return array;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e2) {}
		}
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
	public boolean save(Order order, int activeUserID, Connection conn) throws Exception {
		long id = 0;
		if (order.getId() == 0) {
			id = SequenceProcessAll.getIns().getNextValue("t_order").longValue();
			String prefix = "";
			order.setOrderNo(new OrderDocumentProcess().getNextDocumentNo(order.getSalesRepresent().getCode(), prefix,
					activeUserID, conn));
		} else {
			id = order.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "ORDER_NO", order.getOrderNo(), id, conn)) return false;

		if (order.getVatCode() != null && !order.getVatCode().equals("")) {
			order.setVatRate(new Double(order.getVatCode()));
		}

		Object[] values = { id, ConvertNullUtil.convertToString(order.getOrderNo()).trim(),
				DateToolsUtil.convertToTimeStamp(order.getOrderDate()),
				ConvertNullUtil.convertToString(order.getOrderType()).trim(), order.getCustomerId(),
				ConvertNullUtil.convertToString(order.getCustomerName()).trim(), order.getBillAddressId(),
				order.getShipAddressId(), order.getPriceListId(),
				ConvertNullUtil.convertToString(order.getPaymentTerm()).trim(),
				ConvertNullUtil.convertToString(order.getVatCode()).trim(), order.getVatRate(),
				ConvertNullUtil.convertToString(order.getPaymentMethod()).trim(),
				ConvertNullUtil.convertToString(order.getShippingDay()).trim(),
				ConvertNullUtil.convertToString(order.getShippingTime()).trim(), order.getTotalAmount(),
				order.getVatAmount(), order.getNetAmount(), order.getInterfaces(), order.getPayment(),
				null,//ConvertNullUtil.convertToString(order.getSalesOrderNo()),
				null,//ConvertNullUtil.convertToString(order.getArInvoiceNo()),
				order.getSalesRepresent().getId(),
				order.getDocStatus(), activeUserID, activeUserID,
				ConvertNullUtil.convertToString(order.getIsCash()).length() > 0 ? order.getIsCash() : "N",
				ConvertNullUtil.convertToString(order.getOrderTime()),
				ConvertNullUtil.convertToString(order.getRemark()).trim(),
				ConvertNullUtil.convertToString(order.getCallBeforeSend()),
				order.getOraBillAddressID(),
				order.getOraShipAddressID(),
				order.getOrg(),
				order.getPoNumber(),
				order.getTotalAmountNonVat(),
				ConvertNullUtil.convertToString(order.getVanPaymentMethod())
				};
		if (super.save(TABLE_NAME, columns, values, order.getId(), conn)) {
			order.setId(id);
			order.setCreated(getCreated(order.getId(), conn));
		}
		return true;
	}

	/**
	 * Save Import Order
	 * 
	 * @param customer
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean updatePaymentOrder(Order order, int activeUserID, Connection conn) throws Exception {
		long id = order.getId();
		String[] columnsUpdate = { COLUMN_ID, "ORDER_NO", "PAYMENT"};

		Object[] values = { id,
				 ConvertNullUtil.convertToString(order.getOrderNo()).trim(),
				 order.getPayment()};
		if (super.save(TABLE_NAME, columnsUpdate, values, order.getId(), conn)) {
			order.setId(id);
			order.setCreated(getCreated(order.getId(), conn));
		}
		return true;
	}
	
	public Order reCalculateHeadAmount(Connection conn,String orderId) throws Exception{
		try{
		    Order orderUpdate = find(orderId);
		    List<OrderLine> orderLines = new MOrderLine().lookUp(conn,orderUpdate.getId());
		    //recalculate Head Amount
		    return reCalculateHeadAmount(orderUpdate,orderLines);
	    }catch(Exception e){
	    	throw e;
	    }
	}

	public Order reCalculateHeadAmount(Order order, List<OrderLine> lines) {
		BigDecimal totalAmountDec = new BigDecimal("0");
		BigDecimal totalAmountNonVatDec = new BigDecimal("0");
		BigDecimal totalAmountTemp = new BigDecimal("0");
		BigDecimal lineAmount = new BigDecimal("0");
		BigDecimal lineDiscount = new BigDecimal("0");
		
		BigDecimal VatCodeDec = new BigDecimal("7");
		BigDecimal VatDec = new BigDecimal(0);
		BigDecimal NetDec = new BigDecimal(0);
		DecimalFormat df = new DecimalFormat("###0.00");
		try{
			//get OrderLine 
			for (OrderLine l : lines) {
				//new BigDecimal("35.3456").setScale(4, RoundingMode.HALF_UP);
				lineAmount = new BigDecimal(l.getLineAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
				lineDiscount = new BigDecimal(l.getDiscount()).setScale(2,BigDecimal.ROUND_HALF_UP);
				totalAmountDec = totalAmountDec.add(lineAmount.subtract(lineDiscount) );
				//Non Vat
				
				if( !Utils.isNull(l.getTaxable()).equalsIgnoreCase("Y")){
					totalAmountNonVatDec = totalAmountNonVatDec.add(lineAmount.subtract(lineDiscount) );
				}
			}

			totalAmountDec = totalAmountDec.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			logger.debug("totalAmountDec:"+totalAmountDec);
			logger.debug("totalAmountNonVatDec:"+totalAmountNonVatDec);
			
			//Case some line is non vat (totalAmount-totaAmountNonVat) for calc vat only
			totalAmountTemp = totalAmountDec.subtract(totalAmountNonVatDec);
			totalAmountTemp = totalAmountTemp.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			logger.debug("Result totalAmountTemp:"+totalAmountTemp);
			
			VatDec = (VatCodeDec.multiply(totalAmountTemp)).divide(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP);
			NetDec = totalAmountDec.add(VatDec).setScale(2,BigDecimal.ROUND_HALF_UP);
			
			//recalc vatAmount
			VatDec = NetDec.subtract(totalAmountDec).setScale(2,BigDecimal.ROUND_HALF_UP);
			
			order.setTotalAmount(new Double(df.format(totalAmountDec.doubleValue())));
			order.setTotalAmountNonVat(new Double(df.format(totalAmountNonVatDec.doubleValue())));
			order.setVatAmount(new Double(df.format(VatDec.doubleValue())));
			order.setNetAmount(new Double(df.format(NetDec.doubleValue())));
			
		}catch(Exception e){
		   logger.debug(e.getMessage(),e);
		}finally{
			
		}
		return order;
	}
	
	public Order reCalculateHeadAmountCaseImport(Connection conn,String orderId) throws Exception{
		try{
		    Order orderUpdate = find(orderId);
		    List<OrderLine> orderLines = new MOrderLine().lookUp(conn,orderUpdate.getId());
		    //recalculate Head Amount
		    return reCalculateHeadAmountCaseImport(conn,orderUpdate,orderLines);
	    }catch(Exception e){
	    	throw e;
	    }
	}

	public Order reCalculateHeadAmountCaseImport(Connection conn,Order order, List<OrderLine> lines) {
		BigDecimal totalAmountDec = new BigDecimal("0");
		BigDecimal totalAmountNonVatDec = new BigDecimal("0");
		BigDecimal totalAmountTemp = new BigDecimal("0");
		BigDecimal lineAmount = new BigDecimal("0");
		BigDecimal lineDiscount = new BigDecimal("0");
		
		BigDecimal VatCodeDec = new BigDecimal("7");
		BigDecimal VatDec = new BigDecimal(0);
		BigDecimal NetDec = new BigDecimal(0);
		DecimalFormat df = new DecimalFormat("###0.00");
		try{
			//get OrderLine 
			for (OrderLine l : lines) {
				//new BigDecimal("35.3456").setScale(4, RoundingMode.HALF_UP);
				lineAmount = new BigDecimal(l.getLineAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
				lineDiscount = new BigDecimal(l.getDiscount()).setScale(2,BigDecimal.ROUND_HALF_UP);
				totalAmountDec = totalAmountDec.add(lineAmount.subtract(lineDiscount) );
				//Non Vat
				//get Product is taxable
				Product product = new MProduct().find(conn,String.valueOf(l.getProduct().getId()));
				if( !Utils.isNull(product.getTaxable()).equalsIgnoreCase("Y")){
					totalAmountNonVatDec = totalAmountNonVatDec.add(lineAmount.subtract(lineDiscount) );
				}
			}

			totalAmountDec = totalAmountDec.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			logger.debug("totalAmountDec:"+totalAmountDec);
			logger.debug("totalAmountNonVatDec:"+totalAmountNonVatDec);
			
			//Case some line is non vat (totalAmount-totaAmountNonVat) for calc vat only
			totalAmountTemp = totalAmountDec.subtract(totalAmountNonVatDec);
			totalAmountTemp = totalAmountTemp.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			logger.debug("Result totalAmountTemp:"+totalAmountTemp);
			
			VatDec = (VatCodeDec.multiply(totalAmountTemp)).divide(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP);
			NetDec = totalAmountDec.add(VatDec).setScale(2,BigDecimal.ROUND_HALF_UP);
			
			//recalc vatAmount
			VatDec = NetDec.subtract(totalAmountDec).setScale(2,BigDecimal.ROUND_HALF_UP);
			
			order.setTotalAmount(new Double(df.format(totalAmountDec.doubleValue())));
			order.setTotalAmountNonVat(new Double(df.format(totalAmountNonVatDec.doubleValue())));
			order.setVatAmount(new Double(df.format(VatDec.doubleValue())));
			order.setNetAmount(new Double(df.format(NetDec.doubleValue())));
			
		}catch(Exception e){
		   logger.debug(e.getMessage(),e);
		}finally{
			
		}
		return order;
	}
	
	//Recalc Net Amount From DB After save DB
		public Order reCalculateHeadAmountDB(Connection conn, Order order) {
			BigDecimal totalAmountDec = new BigDecimal("0");
			BigDecimal totalAmountNonVatDec = new BigDecimal("0");
			BigDecimal totalAmountTemp = new BigDecimal("0");
			BigDecimal lineAmount = new BigDecimal("0");
			BigDecimal lineDiscount = new BigDecimal("0");
			
			BigDecimal VatCodeDec = new BigDecimal("7");
			BigDecimal VatDec = new BigDecimal(0);
			BigDecimal NetDec = new BigDecimal(0);
			try{
				//get OrderLine 
				List<OrderLine> newlines = new MOrderLine().lookUp(conn,order.getId());
				DecimalFormat df = new DecimalFormat("###0.00");
				
				for (OrderLine l : newlines) {
					//new BigDecimal("35.3456").setScale(4, RoundingMode.HALF_UP);
					lineAmount = new BigDecimal(l.getLineAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
					lineDiscount = new BigDecimal(l.getDiscount()).setScale(2,BigDecimal.ROUND_HALF_UP);
					totalAmountDec = totalAmountDec.add(lineAmount.subtract(lineDiscount) );
					
					//Non Vat
					if( !Utils.isNull(l.getTaxable()).equalsIgnoreCase("Y")){
						totalAmountNonVatDec = totalAmountNonVatDec.add(lineAmount.subtract(lineDiscount) );
					}
				}

				totalAmountDec = totalAmountDec.setScale(2,BigDecimal.ROUND_HALF_UP);
				
				//Case some line is non vat (totalAmount-totaAmountNonVat) for calc vat only
				totalAmountTemp = totalAmountDec.subtract(totalAmountNonVatDec);
				totalAmountTemp = totalAmountTemp.setScale(2,BigDecimal.ROUND_HALF_UP);
				
				VatDec = (VatCodeDec.multiply(totalAmountTemp)).divide(new BigDecimal("100")).setScale(2,BigDecimal.ROUND_HALF_UP);
				NetDec = totalAmountDec.add(VatDec).setScale(2,BigDecimal.ROUND_HALF_UP);
				
				//recalc vatAmount
				VatDec = NetDec.subtract(totalAmountDec);
				
				order.setTotalAmount(new Double(df.format(totalAmountDec.doubleValue())));
				order.setVatAmount(new Double(df.format(VatDec.doubleValue())));
				order.setNetAmount(new Double(df.format(NetDec.doubleValue())));
				
			}catch(Exception e){
			   logger.debug(e.getMessage(),e);
			}finally{
				
			}
			return order;
		}
	
	public String validateProductIngroup(Connection conn,List<OrderLine> lines) throws Exception {
		String productCodeInvalid ="";
		String productGroupTemp = "";
		String productGroup = "";
		int i = 0;
		for (OrderLine l : lines) {
			if( !"Y".equalsIgnoreCase(l.getPromotion())){
				productGroup = getProductGroup(conn, l.getProduct().getCode());
				
				if(productGroup.equals("")){
					productGroup = "ALL"; 
				}
				
				if( !productGroupTemp.equals("") &&!productGroup.equals("") && !productGroupTemp.equals(productGroup)){
					productCodeInvalid += l.getProduct().getCode()+",";
					//break;
				}
				//First line to checkAll 
				if(i==0){
					productGroupTemp = productGroup;
				}
				i++;
			}
		}
		
		return productCodeInvalid;
	}
	public String getProductGroup(Connection conn,String productCode) throws Exception {
		String productGroup = "";
		ResultSet rs= null;
		PreparedStatement ps = null;
		try{
			ps =conn.prepareStatement("select group_code from m_product_special where code ='"+productCode+"'");
			rs = ps.executeQuery();
			if(rs.next()){
				productGroup = Utils.isNull(rs.getString("group_code"));
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}
		return productGroup;
	}
	
	public List<OrderLine> reCalculateLineAmountInLinesBeforeCalcPromotion(List<OrderLine> lines) throws Exception {
		List<OrderLine> newRecallines = new ArrayList<OrderLine>();
		for (OrderLine l : lines) {
			//recalc LineAmount
			l.setLineAmount(l.getPrice()*l.getQty());
			logger.info("recal :product["+l.getProduct().getCode()+"],qty["+l.getQty()+"]price["+l.getPrice()+"] after LineAmount["+l.getLineAmount()+"]");
			newRecallines.add(l);
		}
		return newRecallines;
	}
	
	public List<OrderLine> reCalculateLineAmountInLinesAfterCalcPromotion(List<OrderLine> lines) throws Exception {
		List<OrderLine> newRecallines = new ArrayList<OrderLine>();
		for (OrderLine l : lines) {
			//recalc LineAmount
			l.setLineAmount(l.getPrice()*l.getQty());
			l.setTotalAmount(l.getLineAmount()-l.getDiscount());
			logger.info("recal :product["+l.getProduct().getCode()+"]qty["+l.getQty()+"]price["+l.getPrice()+"]LineAmount["+l.getLineAmount()+"]Disc["+l.getDiscount()+"]total["+l.getTotalAmount()+"]");
			newRecallines.add(l);
		}
		return newRecallines;
	}
	

	public List<Order> lookUpByOrderAR(int userId, int customerId,int billToAddressId,
			String orderType, String operator, String selected) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			return lookUpByOrderAR(conn, userId, customerId,billToAddressId, orderType, operator, selected);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	/** Get invoice have Invoice to Show in ReceiptCR        **/
	/** t_invoice ,t.receipt.order_id = t_invoice.invoice_id **/
	public List<Order> lookUpByOrderAR(Connection conn
			,int userId, int customerId,int billToAddressId
			, String orderType, String operator, String selected)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		Order invoice = null;
		try{
			List<Order> pos = new ArrayList<Order>();
			if (operator.equalsIgnoreCase("in") && selected.equalsIgnoreCase("")) {
				return pos;
			}
			sql.append(" select  \n");
			sql.append("  inv.INVOICE_ID ,inv.invoice_no,INV.REF_ORDER ,INV.CT_REFERENCE \n");
			sql.append(" ,inv.BILL_TO_SITE_USE_ID ,inv.SHIP_TO_SITE_USE_ID \n");
			sql.append(" ,inv.total_amount as inv_total_amount \n");
			sql.append(" ,inv.vat_amount as inv_vat_amount \n");
			sql.append(" ,inv.net_amount as inv_net_amount \n");
			sql.append(" ,(inv.total_amount-inv.vat_amount) as inv_TOTAL_AMOUNT_NON_VAT \n");
			sql.append(" from pensso.t_invoice inv \n");
			sql.append(" where 1=1 \n");
			sql.append(" and inv.order_type = '" + orderType + "'  \n");
			sql.append(" and inv.bill_to_customer_id = " + customerId +" \n");
			
			//wait next process
			//sql.append(" and inv.BILL_TO_SITE_USE_ID = " + billToAddressId +" \n");
			
			if (selected.length() > 0){
				sql.append("  and inv.invoice_id " + operator + " (" + selected + ")  \n ");
			}
			sql.append("  order by invoice_no asc  \n");
			
			logger.debug("sql lookUpByOrderAR \n "+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				invoice = new Order();
				/** INVOICE **/
				invoice.setInvoiceId(rst.getLong("INVOICE_ID"));
				invoice.setArInvoiceNo(rst.getString("INVOICE_NO"));
				invoice.setOrderNo(rst.getString("REF_ORDER"));
				invoice.setTotalAmount(rst.getDouble("INV_TOTAL_AMOUNT"));
				invoice.setTotalAmountNonVat(rst.getDouble("INV_TOTAL_AMOUNT_NON_VAT"));
				invoice.setVatAmount(rst.getDouble("INV_VAT_AMOUNT"));
				invoice.setNetAmount(rst.getDouble("INV_NET_AMOUNT"));
				invoice.setSalesOrderNo(ConvertNullUtil.convertToString(rst.getString("CT_REFERENCE")).trim());
				// Add Oracle Reference Address ID
				invoice.setOraBillAddressID(rst.getInt("BILL_TO_SITE_USE_ID"));
				invoice.setOraShipAddressID(rst.getInt("SHIP_TO_SITE_USE_ID"));
				
				/** Order **/
				/*invoice.setId(rst.getLong("ORDER_ID"));
				invoice.setOrderDate(DateToolsUtil.convertToString(rst.getTimestamp("ORDER_DATE")));
				invoice.setOrderType(rst.getString("ORDER_TYPE").trim());
				invoice.setCustomerId(rst.getLong("CUSTOMER_ID"));
				invoice.setCustomerName(rst.getString("CUSTOMER_NAME").trim());
				invoice.setBillAddressId(rst.getInt("BILL_ADDRESS_ID"));
				invoice.setShipAddressId(rst.getInt("SHIP_ADDRESS_ID"));
				invoice.setPriceListId(rst.getInt("PRICELIST_ID"));
				invoice.setPaymentTerm(rst.getString("PAYMENT_TERM").trim());
				invoice.setVatCode(rst.getString("VAT_CODE").trim());
				invoice.setVatRate(rst.getDouble("VAT_RATE"));
				invoice.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				invoice.setPayment(rst.getString("PAYMENT").trim());
				invoice.setDocStatus(rst.getString("DOC_STATUS").trim());
				invoice.setIsCash(rst.getString("ISCASH"));
				invoice.setRemark(ConvertNullUtil.convertToString(rst.getString("remark")).trim());
			
				//wit 20110804
				invoice.setPaymentCashNow("CS".equals(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim())?true:false);*/
				
			    pos.add(invoice);
			}
			return pos;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
    }
	
	public Order findInvoice(long invoiceId)
			throws Exception {
		return findInvoiceModel(invoiceId, "");
	}
	public Order findInvoice(String invoiceNo)
			throws Exception {
		return findInvoiceModel(0, invoiceNo);
	}
	public Order findInvoiceModel(long invoiceId,String invoiceNo)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		Order invoice = null;
		Connection conn = null;
		try{
			if(invoiceId==0 && Utils.isNull(invoiceNo).equalsIgnoreCase("")){
				return null;
			}
			conn = DBConnectionApps.getInstance().getConnection();
			
			sql.append(" select \n");
			sql.append("  inv.INVOICE_ID ,inv.invoice_no,INV.REF_ORDER ,INV.CT_REFERENCE \n");
			sql.append(" ,inv.BILL_TO_SITE_USE_ID ,inv.SHIP_TO_SITE_USE_ID \n");
			sql.append(" ,inv.total_amount as inv_total_amount \n");
			sql.append(" ,inv.vat_amount as inv_vat_amount \n");
			sql.append(" ,inv.net_amount as inv_net_amount \n");
			sql.append(" ,(inv.total_amount-inv.vat_amount) as inv_TOTAL_AMOUNT_NON_VAT \n");
			sql.append(" from pensso.t_invoice inv  \n");
			sql.append(" where 1=1 \n");
			if(invoiceId !=0 ){
			    sql.append(" and inv.invoice_id  = " + invoiceId + "  \n");
			}
			if( !Utils.isNull(invoiceNo).equalsIgnoreCase("")){
				sql.append(" and inv.invoice_no  = " + invoiceNo + "  \n");
			}
			
			sql.append(" order by invoice_no asc  \n");
			
			logger.debug("sql findInvoice \n "+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				invoice = new Order();
				/** INVOICE **/
				invoice.setInvoiceId(rst.getLong("INVOICE_ID"));
				invoice.setArInvoiceNo(rst.getString("INVOICE_NO"));
				invoice.setOrderNo(rst.getString("REF_ORDER"));
				invoice.setTotalAmount(rst.getDouble("INV_TOTAL_AMOUNT"));
				invoice.setTotalAmountNonVat(rst.getDouble("INV_TOTAL_AMOUNT_NON_VAT"));
				invoice.setVatAmount(rst.getDouble("INV_VAT_AMOUNT"));
				invoice.setNetAmount(rst.getDouble("INV_NET_AMOUNT"));
				invoice.setSalesOrderNo(ConvertNullUtil.convertToString(rst.getString("CT_REFERENCE")).trim());
				// Add Oracle Reference Address ID
				invoice.setOraBillAddressID(rst.getInt("BILL_TO_SITE_USE_ID"));
				invoice.setOraShipAddressID(rst.getInt("SHIP_TO_SITE_USE_ID"));
				
		
			}
			return invoice;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e2) {}
		}
    }
	
	public Order findOrderByInvoice(long invoiceId)
			throws Exception {
		return findOrderByInvoiceIdModel(invoiceId, "");
	}
	public Order findOrderByInvoice(String invoiceNo)
			throws Exception {
		return findOrderByInvoiceIdModel(0, invoiceNo);
	}
	public Order findOrderByInvoiceIdModel(long invoiceId,String invoiceNo)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		Order invoice = null;
		Connection conn = null;
		try{
			if(invoiceId==0 && Utils.isNull(invoiceNo).equalsIgnoreCase("")){
				return null;
			}
			conn = DBConnectionApps.getInstance().getConnection();
			
			sql.append(" select o.*  \n");
			sql.append(" ,inv.INVOICE_ID ,inv.invoice_no,INV.REF_ORDER ,INV.CT_REFERENCE \n");
			sql.append(" ,inv.BILL_TO_SITE_USE_ID ,inv.SHIP_TO_SITE_USE_ID \n");
			sql.append(" ,inv.total_amount as inv_total_amount \n");
			sql.append(" ,inv.vat_amount as inv_vat_amount \n");
			sql.append(" ,inv.net_amount as inv_net_amount \n");
			sql.append(" ,(inv.total_amount-inv.vat_amount) as inv_TOTAL_AMOUNT_NON_VAT \n");
			sql.append(" from pensso.t_invoice inv ,pensso.t_order o  \n");
			sql.append(" where inv.ref_order = o.order_no \n");
			if(invoiceId !=0 ){
			    sql.append(" and inv.invoice_id  = " + invoiceId + "  \n");
			}
			if( !Utils.isNull(invoiceNo).equalsIgnoreCase("")){
				sql.append(" and inv.invoice_no  = " + invoiceNo + "  \n");
			}
			
			sql.append(" order by invoice_no asc  \n");
			
			logger.debug("sql findInvoice \n "+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				invoice = new Order();
				/** INVOICE **/
				invoice.setInvoiceId(rst.getLong("INVOICE_ID"));
				invoice.setArInvoiceNo(rst.getString("INVOICE_NO"));
				invoice.setOrderNo(rst.getString("REF_ORDER"));
				invoice.setTotalAmount(rst.getDouble("INV_TOTAL_AMOUNT"));
				invoice.setTotalAmountNonVat(rst.getDouble("INV_TOTAL_AMOUNT_NON_VAT"));
				invoice.setVatAmount(rst.getDouble("INV_VAT_AMOUNT"));
				invoice.setNetAmount(rst.getDouble("INV_NET_AMOUNT"));
				invoice.setSalesOrderNo(ConvertNullUtil.convertToString(rst.getString("CT_REFERENCE")).trim());
				// Add Oracle Reference Address ID
				invoice.setOraBillAddressID(rst.getInt("BILL_TO_SITE_USE_ID"));
				invoice.setOraShipAddressID(rst.getInt("SHIP_TO_SITE_USE_ID"));
				
				/** Order **/
				invoice.setId(rst.getLong("order_ID"));
				invoice.setOrderDate(DateToolsUtil.convertToString(rst.getTimestamp("ORDER_DATE")));
				invoice.setOrderType(rst.getString("ORDER_TYPE").trim());
				invoice.setCustomerId(rst.getLong("CUSTOMER_ID"));
				invoice.setCustomerName(rst.getString("CUSTOMER_NAME").trim());
				invoice.setBillAddressId(rst.getInt("BILL_ADDRESS_ID"));
				invoice.setShipAddressId(rst.getInt("SHIP_ADDRESS_ID"));
				invoice.setPriceListId(rst.getInt("PRICELIST_ID"));
				invoice.setPaymentTerm(rst.getString("PAYMENT_TERM").trim());
				invoice.setVatCode(rst.getString("VAT_CODE").trim());
				invoice.setVatRate(rst.getDouble("VAT_RATE"));
				invoice.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				invoice.setPayment(rst.getString("PAYMENT").trim());
				invoice.setDocStatus(rst.getString("DOC_STATUS").trim());
				invoice.setIsCash(rst.getString("ISCASH"));
				invoice.setRemark(ConvertNullUtil.convertToString(rst.getString("remark")).trim());
			
				//wit 20110804
				invoice.setPaymentCashNow("CS".equals(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim())?true:false);
				
			}
			return invoice;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e2) {}
		}
    }
	/**
	 * LookUp
	 * 
	 * @param userId
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public List<Order> lookUpPrepaid(int userId, int customerId, String orderType, String operator, String selected)
			throws Exception {
		List<Order> pos = new ArrayList<Order>();
		if (operator.equalsIgnoreCase("in") && selected.equalsIgnoreCase("")) return pos;
		String whereCause = " and order_type = '" + orderType + "' ";
		whereCause += "  and user_id = " + userId;
		whereCause += "  and customer_id = " + customerId;
		if (selected.length() > 0) whereCause += "  and order_id " + operator + " (" + selected + ") ";

		pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Order.class);
		return pos;
	}

	/**
	 * LookUp
	 * 
	 * @param userId
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public int lookUpByCustomer(long customerId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		int tot = 0;
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			String sql = "select count(*) as tot from pensso.t_order where customer_id = " + customerId;
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if (rst.next()) tot = rst.getInt("tot");
			return tot;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}

	/**
	 * get Created
	 * 
	 * @param orderId
	 * @param conn
	 * @return
	 */
	private String getCreated(long orderId, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String created = "";
		try {
			String sql = "select created from pensso.t_order where order_id = " + orderId;
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if (rst.next()) created = DateToolsUtil.convertFromTimestamp(rst.getTimestamp("created"));
			return created;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
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

	/**
	 * Look Up for Member
	 * 
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public List<Order> lookUpForMember(String memberId, String selected) throws Exception {
		List<Order> pos = new ArrayList<Order>();
		String whereCause = "";
		whereCause += "  and order_type = '" + Order.DIRECT_DELIVERY + "' ";
		whereCause += "  and customer_id = '" + memberId + "' ";
		Order[] orders = search(whereCause);

		List<OrderLine> lines;
		List<OrderLine> rmLines;
		List<Order> rmOrders = new ArrayList<Order>();
		String[] ss = selected.split(",");
		if(orders != null){
		for (Order r : orders) {
			lines = new MOrderLine().lookUp(r.getId());
			rmLines = new ArrayList<OrderLine>();
			for (OrderLine l : lines) {
				// remove paid
				if (l.getPayment().equalsIgnoreCase("Y")) rmLines.add(l);
				// remove selected
				for (String s : ss) {
					if (s.trim().length() > 0) {
						if (l.getId() ==0) {
							rmLines.add(l);
						}
					}
				}
			}
			for (OrderLine l : rmLines) {
				lines.remove(l);
			}
			if (lines.size() == 0) rmOrders.add(r);
		}
		for (Order r : orders) {
			pos.add(r);
		}
		for (Order r : rmOrders) {
			pos.remove(r);
		}
		}
		return pos;
	}
	
	
	/**
	 * cancelOrderByID
	 * @param conn
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public int cancelOrderByID(Connection conn,long orderId,String remark,int userId,String payment) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE "+TABLE_NAME+" SET  DOC_STATUS = ? ,UPDATED_BY =? ,UPDATED = CURRENT_TIMESTAMP WHERE order_id = ? ";
			logger.debug("order_id:"+orderId);
			logger.debug("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index,Order.STATUS_CANCEL);
			//ps.setString(++index, remark);
			ps.setInt(++index, userId);
			ps.setLong(++index, orderId);
			
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}

	
	public List<Order> getOrderVanNotReceipt(Connection conn,User user) throws Exception {

		Statement stmt = null;
		ResultSet rst = null;
		List<Order> orderList = new ArrayList<Order>();
		try{
			String sql =""+
			 "select * "+
			 "from t_order where 1=1 \n"+
			 "and doc_status ='SV' \n"+
			 "and payment ='N' \n"+
			 "and user_id = '"+user.getId()+"' \n"+
			 "and date(created) = date( sysdate()) \n"+
			 "and order_no like '"+user.getCode()+"%' \n"+
			 "and order_no not in( \n"+
			    "select receipt_no from t_receipt where 1=1 \n"+
			    "and user_id = '"+user.getId()+"' \n"+
			    "and doc_status ='SV' \n"+
			 ") \n";
			 
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				Order o = new Order(rst);
				
				orderList.add(o);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return orderList;
	}
	
	public int updatePaymentByOrderId(Connection conn,long orderId,String payment) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE "+TABLE_NAME+" SET  PAYMENT = ?  ,UPDATED_BY =? ,UPDATED = CURRENT_TIMESTAMP WHERE order_id = ? ";
			logger.debug("order_id:"+orderId);
			logger.debug("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index,payment);
			ps.setInt(++index, 9999);
			ps.setLong(++index, orderId);
			
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	/**
    
	 * @param conn
	 * @param order
	 */
	public void updatePrintPickStamp(Connection conn ,Order order) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updatePrintPickStamp ");
		try{
			String sql = "UPDATE "+TABLE_NAME+" SET print_DateTime_Pick = ? ,print_Count_Pick = ? WHERE order_id = ? ";
			int index = 0;
			ps = conn.prepareStatement(sql);
			
		    java.util.Date pickDate = Utils.isNull(order.getPrintDateTimePick()).equals("")?null:Utils.parse(order.getPrintDateTimePick(), Utils.DD_MM_YYYY_HH_mm_WITHOUT_SLASH, Utils.local_th);
			BigDecimal pickDateBig = new BigDecimal(0);
			if(pickDate !=null){
				pickDateBig = new BigDecimal(pickDate.getTime()); 
			}
			logger.debug("orderId:"+order.getId()+",pickDateBig:"+pickDateBig);
		  
		    ps.setBigDecimal(++index, pickDateBig.setScale(6));//updated
			ps.setInt(++index, order.getPrintCountPick());
			ps.setLong(++index, order.getId());
				
			ps.execute();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public void updatePrintRcpStamp(int orderId,String dateStr,int count)  throws Exception{
		PreparedStatement ps = null;
		Connection conn =null;
		logger.debug("updatePrintRcpStamp ");
		try{
			String sql = "UPDATE "+TABLE_NAME+" SET print_DateTime_Rcp = ? ,print_Count_rcp = ? WHERE order_id = ? ";
			int index = 0;
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			
			java.util.Date pickDate = null;
			pickDate = Utils.isNull(dateStr).equals("")?null:Utils.parse(dateStr, Utils.DD_MM_YYYY_HH_mm_WITHOUT_SLASH, Utils.local_th);
            BigDecimal pickDateBig = new BigDecimal(pickDate.getTime());
            
            logger.debug("pickDateBig:"+pickDateBig);
            
		    ps.setBigDecimal(++index, pickDateBig.setScale(6));//updated
			ps.setInt(++index, count);
			ps.setInt(++index, orderId);
				
			ps.execute();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	
	/**
	 * Check Order is have vat
	 * @param conn
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public boolean isOrderHaveVat(Connection conn,long orderId) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		boolean haveVat = true;
		try{
			String sql ="select count(*) as c from t_order_line where order_id="+orderId +" and taxable ='N'";
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				if(rst.getInt("c") >0){
					haveVat = false;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		return haveVat;
	}
	
	public void updatePrintTaxInvoiceStamp(Connection conn ,String orderNo,String reportName)  throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		logger.debug("updatePrintTaxInvoiceStamp ");
		String sql = "";
		int printCount = 1;
		try{
			//current time 
			String dateTime = Utils.stringValue(new java.util.Date(), Utils.DD_MM_YYYY__HH_mm_ss_SSSSSS_WITH_SLASH,Utils.local_th);
			
			//sql get last count by order,reportName
			sql  = "select max(print_count) as max_print_count from  t_stamp_print_order ";
			sql += "WHERE order_no = '"+orderNo+"' and report_name ='"+reportName+"' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				printCount = rs.getInt("max_print_count")+1;
			}
			
			//sql insert n
			sql  = "insert into t_stamp_print_order(order_no,report_name,date,print_count)";
			sql += "values('"+orderNo+"','"+reportName+"','"+dateTime+"',"+printCount+") ";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
}
