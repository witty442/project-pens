package com.isecinc.pens.model;

import static util.ConvertNullUtil.convertToString;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.interim.bean.IOrderToReceipt;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.ReceiptDocumentProcess;
import com.isecinc.pens.web.pd.PDReceiptForm;

/**
 * Receipt Model
 * 
 * @author atiz.b
 * @version $Id: MReceipt.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MReceipt extends I_Model<Receipt> {

	private static final long serialVersionUID = -8532039857520296789L;

	public static String TABLE_NAME = "t_receipt";
	public static String COLUMN_ID = "RECEIPT_ID";

	private String[] columns = { COLUMN_ID, "RECEIPT_NO", "RECEIPT_DATE", "ORDER_TYPE", "CUSTOMER_ID", "CUSTOMER_NAME",
			"PAYMENT_METHOD", "BANK", "CHEQUE_NO", "CHEQUE_DATE", "RECEIPT_AMOUNT", "INTERFACES", "DOC_STATUS",
			"USER_ID", "CREATED_BY", "UPDATED_BY", "CREDIT_CARD_TYPE", "DESCRIPTION", "PREPAID", "APPLY_AMOUNT",
			"INTERNAL_BANK" ,"ISPDPAID","PDPAID_DATE","PD_PAYMENTMETHOD","EXPORTED"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Receipt find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Receipt.class);
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
	public Receipt[] search(String whereCause) throws Exception {
		List<Receipt> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Receipt.class);
		if (pos.size() == 0) return null;
		Receipt[] array = new Receipt[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	public Receipt[] search(Connection conn,String whereCause) throws Exception {
		List<Receipt> pos = super.search(conn,TABLE_NAME, COLUMN_ID, whereCause, Receipt.class);
		if (pos.size() == 0) return null;
		Receipt[] array = new Receipt[pos.size()];
		array = pos.toArray(array);
		return array;
	}

public Receipt[] searchOptCasePDPAID_NO(PDReceiptForm pdForm ,User user) throws Exception {
	Connection conn = null;
	Statement stmt = null;
	ResultSet rst = null;
	List<Receipt> receiptList = new ArrayList<Receipt>();
	Receipt[] array = null;
	try {
		//Start Date From Config date
		References refConfigCreditDateFix = InitialReferences.getReferenesByOne(InitialReferences.VAN_DATE_FIX,InitialReferences.VAN_DATE_FIX);
		String  creditDateFix = refConfigCreditDateFix!=null?refConfigCreditDateFix.getKey():"";
		logger.debug("vanDateFix:"+creditDateFix);
		String dateCheck = "";
		if( !"".equalsIgnoreCase(creditDateFix)){
			java.util.Date d = Utils.parse(creditDateFix, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			dateCheck = "str_to_date('"+Utils.stringValue(d, Utils.DD_MM_YYYY_WITH_SLASH)+"','%d/%m/%Y')" ;
		}
		
		String orderNoFrom = pdForm.getReceipt().getOrderNoFrom();
		String orderNoTo = pdForm.getReceipt().getOrderNoTo();
		
		String receiptDateFrom = pdForm.getReceipt().getReceiptDateFrom();
		String receiptDateTo = pdForm.getReceipt().getReceiptDateTo();
		
		String sql = " select  order_id as receipt_id , \n";
		       sql+= " ORDER_NO AS RECEIPT_NO, ORDER_DATE AS RECEIPT_DATE, ORDER_TYPE, CUSTOMER_ID, \n";
		       sql+= " (select concat(code,concat('-',name)) from m_customer m where m.customer_id = o.customer_id) as CUSTOMER_NAME,"; 
			   sql+= " PAYMENT_METHOD, '' as BANK, '' as CHEQUE_NO, null as CHEQUE_DATE, NET_AMOUNT as RECEIPT_AMOUNT, '' as INTERFACES, DOC_STATUS,  \n";
			   sql+= " USER_ID, CREATED_BY, UPDATED_BY, '' as CREDIT_CARD_TYPE, '' as DESCRIPTION, '' as PREPAID, NET_AMOUNT as APPLY_AMOUNT,  \n";
			   sql+= " '' as INTERNAL_BANK ,'' as ISPDPAID, null as PDPAID_DATE,'' as PD_PAYMENTMETHOD  \n";
               sql+= " FROM t_order o \n";
               sql+= " WHERE 1=1 \n";
               sql+= " AND ISCASH ='N' \n";
               sql+= " AND DOC_STATUS ='SV' \n";
               sql+= " AND USER_ID ='"+user.getId()+"' \n";
               sql+= " AND order_id not in( select order_id from t_receipt_line)  \n";
               sql+= " AND order_no not in( select receipt_no from t_receipt)  \n";
               sql+= " AND order_no not in( select order_no from t_receipt_pdpaid_no)  \n";
               
               if(!StringUtils.isEmpty(dateCheck)){
        			sql+="\n  AND ORDER_DATE >= "+dateCheck+"";
        	   }
               if(!StringUtils.isEmpty(orderNoFrom)){
            	  sql+="\n  AND ORDER_NO >= '"+orderNoFrom+"'";
       		   }
       		   if(!StringUtils.isEmpty(orderNoTo)){
       			  sql+="\n  AND ORDER_NO <= '"+orderNoTo+"'";
       		   }
       		   if(!StringUtils.isEmpty(receiptDateFrom)){
       			  sql+="\n  AND ORDER_DATE >= '"+DateToolsUtil.convertToTimeStamp(receiptDateFrom)+"'";
       		   }
       		   if(!StringUtils.isEmpty(receiptDateTo)){
       			  sql+="\n  AND ORDER_DATE <= '"+DateToolsUtil.convertToTimeStamp(receiptDateTo)+"'";
       		   }
		     
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			
			while(rst.next()){
				Receipt m = new Receipt();
				m.setId(rst.getInt("receipt_id"));
				m.setReceiptNo(rst.getString("RECEIPT_NO").trim());
				m.setReceiptDate(DateToolsUtil.convertToString(rst.getTimestamp("RECEIPT_DATE")));
				m.setOrderType(rst.getString("ORDER_TYPE").trim());
				m.setCustomerId(rst.getInt("CUSTOMER_ID"));
				m.setCustomerName(rst.getString("CUSTOMER_NAME").trim());
				m.setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
				m.setBank(ConvertNullUtil.convertToString(rst.getString("BANK")).trim());
				m.setChequeNo(ConvertNullUtil.convertToString(rst.getString("CHEQUE_NO")).trim());
				m.setChequeDate("");
				if (rst.getTimestamp("CHEQUE_DATE") != null)
					m.setChequeDate(DateToolsUtil.convertToString(rst.getTimestamp("CHEQUE_DATE")));
				m.setReceiptAmount(rst.getDouble("RECEIPT_AMOUNT"));
				m.setInterfaces(rst.getString("INTERFACES").trim());
				m.setDocStatus(rst.getString("DOC_STATUS").trim());
				m.setSalesRepresent(new MUser().find(rst.getString("USER_ID")));
				m.setCreditCardType(ConvertNullUtil.convertToString(rst.getString("CREDIT_CARD_TYPE")).trim());
				m.setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
				m.setPrepaid(rst.getString("PREPAID").trim());
				m.setApplyAmount(rst.getDouble("APPLY_AMOUNT"));
				m.setInternalBank(ConvertNullUtil.convertToString(rst.getString("INTERNAL_BANK")));
				
				receiptList.add(m);
			}
			
			//convert to Obj
			if(receiptList != null && receiptList.size() >0){
				array = new Receipt[receiptList.size()];
				array = receiptList.toArray(array);
			}else{
				array = null;
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e2) {}
		}
	return array;
}
	/**
	 * Save
	 * 
	 * @param receipt
	 * @param activeUserID
	 * @param conn
	 * @return 0:duplicate ,  > 0 :receiptId
	 * @throws Exception
	 */
	public boolean save(Receipt receipt, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (receipt.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			String prefix = "";
			if (ConvertNullUtil.convertToString(receipt.getReceiptNo()).trim().length() == 0)
				receipt.setReceiptNo("R"
						+ new ReceiptDocumentProcess().getNextDocumentNo(receipt.getSalesRepresent().getCode(), prefix,
								activeUserID, conn));	
		} else {
			id = receipt.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "RECEIPT_NO", receipt.getReceiptNo(), id, conn))
			return false;

		Object[] values = { id, ConvertNullUtil.convertToString(receipt.getReceiptNo()).trim(),
				DateToolsUtil.convertToTimeStamp(receipt.getReceiptDate()),
				ConvertNullUtil.convertToString(receipt.getOrderType()).trim(), receipt.getCustomerId(),
				ConvertNullUtil.convertToString(receipt.getCustomerName()).trim(), null, null, null, null,
				receipt.getReceiptAmount(), receipt.getInterfaces(), receipt.getDocStatus(),
				receipt.getSalesRepresent().getId(), activeUserID, activeUserID, null,
				ConvertNullUtil.convertToString(receipt.getDescription()).trim(), receipt.getPrepaid(),
				receipt.getApplyAmount(), ConvertNullUtil.convertToString(receipt.getInternalBank()) , 
				receipt.getIsPDPaid(),DateToolsUtil.convertToTimeStamp(ConvertNullUtil.convertToString(receipt.getPdPaidDate())),
				receipt.getPdPaymentMethod(),Utils.isNull(receipt.getExported())};
		if (super.save(TABLE_NAME, columns, values, receipt.getId(), conn)) {
			receipt.setId(id);
		}
		logger.debug("Create New ReceiptId:"+receipt.getId());
		return true;
	}

	/**
	 * Save WO Check DUP
	 * 
	 * @param receipt
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean saveWOCheckDup(Receipt receipt, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (receipt.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			String prefix = "";
			if (ConvertNullUtil.convertToString(receipt.getReceiptNo()).trim().length() == 0)
				receipt.setReceiptNo("R"
						+ new ReceiptDocumentProcess().getNextDocumentNo(receipt.getSalesRepresent().getCode(), prefix,
								activeUserID, conn));
		} else {
			id = receipt.getId();
		}

		Object[] values = { id, ConvertNullUtil.convertToString(receipt.getReceiptNo()).trim(),
				DateToolsUtil.convertToTimeStamp(receipt.getReceiptDate()),
				ConvertNullUtil.convertToString(receipt.getOrderType()).trim(), receipt.getCustomerId(),
				ConvertNullUtil.convertToString(receipt.getCustomerName()).trim(), null, null, null, null,
				receipt.getReceiptAmount(), receipt.getInterfaces(), receipt.getDocStatus(),
				receipt.getSalesRepresent().getId(), activeUserID, activeUserID, null,
				ConvertNullUtil.convertToString(receipt.getDescription()).trim(), receipt.getPrepaid(),
				receipt.getApplyAmount(), ConvertNullUtil.convertToString(receipt.getInternalBank()) , 
				receipt.getIsPDPaid() , DateToolsUtil.convertToTimeStamp(ConvertNullUtil.convertToString(receipt.getPdPaidDate())) 
				, receipt.getPdPaymentMethod(),""};
		if (super.save(TABLE_NAME, columns, values, receipt.getId(), conn)) {
			receipt.setId(id);
		}
		return true;
	}

	public int saveReceiptCasePDPaidNo(Connection conn,Receipt r,User user) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "\n INSERT INTO t_receipt_pdpaid_no ";
	               sql +="\n ( ORDER_NO,ORDER_DATE,CUSTOMER_ID,RECEIPT_AMOUNT, PDPAID_DATE, ";
	               sql +="\n PD_PAYMENTMETHOD, CREATED, CREATED_BY) ";
	               sql +="\n VALUES(?,?,?,?,?,?,?,?)";
	               
			logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index, r.getReceiptNo());
			ps.setDate(++index, new java.sql.Date(Utils.parse(r.getReceiptDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			ps.setInt(++index, r.getCustomerId());
			ps.setDouble(++index, r.getReceiptAmount());
			if( !Utils.isNull(r.getPdPaidDate()).equals("")){
			  Timestamp d=DateToolsUtil.convertToTimeStamp(ConvertNullUtil.convertToString(r.getPdPaidDate())) ;
			   ps.setTimestamp(++index, d);
			}else{
			   ps.setTimestamp(++index, null);
			}
			ps.setString(++index, r.getPdPaymentMethod());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setInt(++index, user.getId());
			
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
	 * Get Lasted Receipt From Order
	 * 
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public String getLastestReceiptFromOrder(int orderId) throws Exception {
		String receiptNo = "";
		String whereCause = "  and doc_status = 'SV' ";
		whereCause += "  and receipt_id in (select receipt_id from t_receipt_line where order_id = " + orderId + ") ";
		whereCause += " order by receipt_no desc ";
		Receipt[] receipts = search(whereCause);
		if (receipts != null) return receipts[0].getReceiptNo();
		return receiptNo;
	}
	
	/**
	 * cancelReceiptByID
	 * @param conn
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public int cancelReceiptByID(Connection conn,int receiptId,String remark,int userId) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE "+TABLE_NAME+" SET  DOC_STATUS = ? ,REASON = ?,UPDATED_BY =? ,UPDATED = CURRENT_TIMESTAMP  WHERE receipt_id = ? ";
			logger.debug("receipt_id:"+receiptId);
			logger.debug("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index,Order.DOC_VOID);
			ps.setString(++index, "-"+remark);
			ps.setInt(++index, userId);
			ps.setInt(++index, receiptId);
			
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	/** Interim Program to Create Receipt Automatic From I_Order_To_Receipt
	 *  This program using to clear customer bill that was clear in oracle but In Sales Application Is Not Clear 
	 * */
	public String autoCreateReceipt(IOrderToReceipt tOrder, Connection conn) throws Exception{
		conn.setAutoCommit(false);
		
		if(tOrder.getOrderID()<=0 )
			throw new Exception("Cannot found Invoice");
		
		BigDecimal creditAmt = BigDecimal.valueOf(tOrder.getCreditAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal limitAmt = BigDecimal.valueOf(0.01d);
		
		// Check Credit Amount Should Be Over Than Limit Amount
		if(creditAmt.compareTo(limitAmt) == -1 ){
			throw new Exception("Invoice Amount Is ZERO "+tOrder.getCreditAmount());
		}
		
		Receipt receipt = new Receipt();
		receipt.setApplyAmount(tOrder.getCreditAmount());
		receipt.setInternalBank("002");
		receipt.setDescription("Cleansing Data");
		
		receipt.setReceiptDate(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
		
		User sales = new MUser().find(""+tOrder.getOrder().getSalesRepresent().getId());
		
		receipt.setSalesRepresent(sales);
		
		Customer customer = new MCustomer().find(String.valueOf(tOrder.getCustomerID()));
		receipt.setCustomerId(customer.getId());
		receipt.setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
		receipt.setPaymentMethod(customer.getPaymentMethod());
		
		receipt.setOrderType(sales.getOrderType().getKey());
		receipt.setInterfaces("Y");
		receipt.setDocStatus("SV");
		receipt.setExported("Y");
		receipt.setPrepaid("N");
		receipt.setReceiptAmount(tOrder.getCreditAmount());
		receipt.setApplyAmount(tOrder.getCreditAmount());
		
		if(!save(receipt,sales.getId(),conn)){
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Exception("CANNOT SAVE RECEIPT HEADER");
		}
		
		
		if(receipt.getId()<= 0){
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Exception("Cannot Save Receipt Header "+tOrder.getArInvoiceNo());
		}
		
		// create receipt line 
		ReceiptLine line = new ReceiptLine();
		line.setArInvoiceNo(tOrder.getArInvoiceNo());
		line.setCreditAmount(tOrder.getCreditAmount());
		line.setInvoiceAmount(tOrder.getCreditAmount());
		line.setRemainAmount(0d);
		line.setOrder(tOrder.getOrder());
		line.setSalesOrderNo(tOrder.getSalesOrderNo());
		line.setPaidAmount(tOrder.getCreditAmount());
		line.setReceiptId(receipt.getId());
		
		MReceiptLine mLine = new MReceiptLine();
		if(!mLine.save(line, sales.getId(), conn)){
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Exception("Cannot Save Receipt Line "+tOrder.getArInvoiceNo());
		}
		
		if(line.getId()<= 0){
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Exception("Cannot Save Receipt Line "+tOrder.getArInvoiceNo());
		}
		
		ReceiptBy by = new ReceiptBy();
		by.setPaidAmount(tOrder.getCreditAmount());
		by.setPaymentMethod("CS");
		by.setReceiptAmount(tOrder.getCreditAmount());
		by.setReceiptId(receipt.getId());
		by.setSeedId("1234");
		by.setWriteOff("N");
		by.setChequeDate("");
		
		MReceiptBy mBy = new MReceiptBy();
		if(!mBy.save(by, sales.getId(), conn)){
			conn.rollback();
			conn.setAutoCommit(true);
			throw new Exception("Cannot Save Receipt By "+tOrder.getArInvoiceNo());
		}
		
		if(by.getId()<= 0){
			conn.rollback();
			throw new Exception("Cannot Save Receipt By "+tOrder.getArInvoiceNo());
		}
		
		ReceiptMatch match = new ReceiptMatch();
		match.setReceiptId(receipt.getId());
		match.setReceiptLineId(line.getId());
		match.setReceiptById(by.getId());
		match.setPaidAmount(tOrder.getCreditAmount());
		
		MReceiptMatch mMatch = new MReceiptMatch();
		mMatch.save(match, sales.getId(), conn);
		if(match.getId()<= 0){
			conn.rollback();
			throw new Exception("Cannot Save Receipt Line "+tOrder.getArInvoiceNo());
		}
		
		conn.commit();
		return receipt.getReceiptNo();
	}
	
	public int getReceiptId(Connection conn,String receiptNo) throws Exception {
        int receiptId = 0;
		Statement stmt = null;
		ResultSet rst = null;
		try{
			String sql ="select receipt_id from t_receipt where receipt_no ='"+receiptNo+"' and doc_status ='SV'";
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				receiptId = rst.getInt("receipt_id");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		return receiptId;
	}
	
	
}
