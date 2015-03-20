package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderTransaction;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptConfirm;
import com.isecinc.pens.bean.ReceiptGenerateSummary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.confirm.ConfirmGenerateReceipt;
import com.isecinc.pens.process.confirm.ControlOrderTransaction;
import com.isecinc.pens.process.document.ReceiptDocumentProcess;

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

	private String[] columns = { COLUMN_ID, "RECEIPT_NO", "RECEIPT_DATE",
			"ORDER_TYPE", "CUSTOMER_ID", "CUSTOMER_NAME", "PAYMENT_METHOD",
			"BANK", "CHEQUE_NO", "CHEQUE_DATE", "RECEIPT_AMOUNT", "INTERFACES",
			"DOC_STATUS", "USER_ID", "CREATED_BY", "UPDATED_BY",
			"CREDIT_CARD_TYPE", "DESCRIPTION", "PREPAID", "APPLY_AMOUNT",
			"INTERNAL_BANK", "TAXINVOICE_ID","TRIP_NO" };

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
		List<Receipt> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause,
				Receipt.class);
		if (pos.size() == 0)
			return null;
		Receipt[] array = new Receipt[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param receipt
	 * @param activeUserID
	 * @param conn
	 * @return 0:duplicate , > 0 :receiptId
	 * @throws Exception
	 */
	public boolean save(Receipt receipt, int activeUserID, Connection conn)
			throws Exception {
		int id = 0;
		if (receipt.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			Date shippingDate = Utils.parse(receipt.getReceiptDate(), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th);
			if (ConvertNullUtil.convertToString(receipt.getReceiptNo()).trim().length() == 0)
				receipt.setReceiptNo("3"
						+ new ReceiptDocumentProcess().getNextDocumentNo(
								shippingDate,
								activeUserID,
								receipt.getReceiptDateYear(),
								receipt.getReceiptDateMonth(),
								conn));
		} else {
			id = receipt.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "RECEIPT_NO",
				receipt.getReceiptNo(), id, conn)) {
			throw new Exception("Receipt No Is Dupplicate.");
		}

		Object[] values = {
				id,
				ConvertNullUtil.convertToString(receipt.getReceiptNo()).trim(),
				DateToolsUtil.convertToTimeStamp(receipt.getReceiptDate()),
				ConvertNullUtil.convertToString(receipt.getOrderType()).trim(),
				receipt.getCustomerId(),
				ConvertNullUtil.convertToString(receipt.getCustomerName())
						.trim(),
				null,
				null,
				null,
				null,
				receipt.getReceiptAmount(),
				receipt.getInterfaces(),
				receipt.getDocStatus(),
				receipt.getSalesRepresent().getId(),
				activeUserID,
				activeUserID,
				null,
				ConvertNullUtil.convertToString(receipt.getDescription())
						.trim(),
				receipt.getPrepaid(),
				receipt.getApplyAmount(),
				ConvertNullUtil.convertToString(receipt.getInternalBank()),
				(receipt.getTaxinvoiceId() == 0 ? null : receipt.getTaxinvoiceId()), 
				receipt.getTripNo()
		        };
		if (super.save(TABLE_NAME, columns, values, receipt.getId(), conn)) {
			receipt.setId(id);
		}
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
	public boolean saveWOCheckDup(Receipt receipt, int activeUserID,
			Connection conn) throws Exception {
		int id = 0;
		if (receipt.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);

			//if (ConvertNullUtil.convertToString(receipt.getReceiptNo()).trim().length() == 0)
				//receipt.setReceiptNo("R"+ new ReceiptDocumentProcess().getNextDocumentNo(shippingDate,activeUserID, conn));
		} else {
			id = receipt.getId();
		}

		Object[] values = {
				id,
				ConvertNullUtil.convertToString(receipt.getReceiptNo()).trim(),
				DateToolsUtil.convertToTimeStamp(receipt.getReceiptDate()),
				ConvertNullUtil.convertToString(receipt.getOrderType()).trim(),
				receipt.getCustomerId(),
				ConvertNullUtil.convertToString(receipt.getCustomerName())
						.trim(),
				null,
				null,
				null,
				null,
				receipt.getReceiptAmount(),
				receipt.getInterfaces(),
				receipt.getDocStatus(),
				receipt.getSalesRepresent().getId(),
				activeUserID,
				activeUserID,
				null,
				ConvertNullUtil.convertToString(receipt.getDescription())
						.trim(),
				receipt.getPrepaid(),
				receipt.getApplyAmount(),
				ConvertNullUtil.convertToString(receipt.getInternalBank()),
				(receipt.getTaxinvoiceId() == 0 ? null : receipt.getTaxinvoiceId()), 
				receipt.getTripNo()
		       };
		if (super.save(TABLE_NAME, columns, values, receipt.getId(), conn)) {
			receipt.setId(id);
		}
		return true;
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
		whereCause += "  and receipt_id in (select receipt_id from t_receipt_line where order_id = "
				+ orderId + ") ";
		whereCause += " order by receipt_no desc ";
		Receipt[] receipts = search(whereCause);
		if (receipts != null)
			return receipts[0].getReceiptNo();
		return receiptNo;
	}

	/**
	 * cancelReceiptByID
	 * 
	 * @param conn
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public int cancelReceiptByID(Connection conn, int receiptId, String remark,
			int userId) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE "
					+ TABLE_NAME
					+ " SET  DOC_STATUS = ? ,REASON = ?,UPDATED_BY =? ,UPDATED = CURRENT_TIMESTAMP  WHERE receipt_id = ? ";
			logger.debug("receipt_id:" + receiptId);
			logger.debug("SQL:" + sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index, Order.DOC_VOID);
			ps.setString(++index, "-" + remark);
			ps.setInt(++index, userId);
			ps.setInt(++index, receiptId);

			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (ps != null) {
				ps.close();
				ps = null;
			}
		}
	}


	public ReceiptGenerateSummary createReceiptFromReceiptConfirmation(
			List<ReceiptConfirm> confirms, String confirmDate, User user)throws Exception {
		
		ReceiptGenerateSummary summary = new ReceiptGenerateSummary();
		Connection conn = null;
		MOrder orderService = new MOrder();
		Receipt receipt = null;
		boolean checkPrevTripConfirmReceipt = false;
		long startTime  = new Date().getTime();
		try{
			// Sort Confirmation
			//Collections.sort(confirms);
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			for (ReceiptConfirm confirm : confirms) {
				Order order = null;
				BigDecimal actQty = BigDecimal.valueOf(confirm.getActQty());
				BigDecimal qty = BigDecimal.valueOf(confirm.getQty());
				BigDecimal totalAmt = BigDecimal.valueOf(confirm.getTotalAmt()).setScale(2, BigDecimal.ROUND_HALF_UP); //Qty * Price
				BigDecimal lineAmt = BigDecimal.valueOf(confirm.getNeedBillAmt());
				BigDecimal confirmAmt = BigDecimal.valueOf(confirm.getConfirmAmt());
	
				logger.info("actQty["+actQty+"]");
				logger.info("qty["+qty+"]");
				logger.info("totalAmt["+totalAmt+"]");
				logger.info("lineAmt["+lineAmt+"]");
				logger.info("confirmAmt["+confirmAmt+"]");
	
				try {
					boolean isCredit = false;
					order = orderService.find("" + confirm.getOrderId());
	
					/***************** Validate **********************************/
					if ("CR".equals(confirm.getPaymentMethod())) {
						if (StringUtils.isEmpty(confirm.getCreditCardName())
								|| StringUtils.isEmpty(confirm.getCreditCardNo())
								|| StringUtils.isEmpty(confirm.getBank()))
							throw new Exception(
									"Credit Card Information Is Not Complete");
						isCredit = true;
					} else if ("CH".equals(confirm.getPaymentMethod())) {
						if (StringUtils.isEmpty(confirm.getChequeDate())
								|| StringUtils.isEmpty(confirm.getChequeNo())
								|| StringUtils.isEmpty(confirm.getBank()))
							throw new Exception(
									"Cheque Information Is Not Complete");
						isCredit = true;
					}
					
					// confirm =y
					if("Y".equals(confirm.getIsConfirm())){
						//For next step insert OrderTransaction
						OrderTransaction m = new OrderTransaction();
						m.setOrderId(confirm.getOrderId());
						m.setTripNo(confirm.getTripNo());
						m.setConfirmReceiptDate(confirmDate);
						m.setUserId(user.getId());
					    m.setCustomerId(order.getCustomerId());
					    m.setCustomerName(order.getCustomerName());
					    m.setOrderType(order.getOrderType());
					    
						/** Step 1 Insert or update Control orderTransaction **/
					    startTime = new Date().getTime();
						m = ControlOrderTransaction.processOrderTransactionCaseReceipt(conn, m,checkPrevTripConfirmReceipt);
						logger.debug("**** Debug Time processOrderTransactionCaseReceipt:"+(new Date().getTime() - startTime));
						
						/** Step 2 Generate Receipt and taxInvoice **/
						startTime = new Date().getTime();
						List<Receipt> receiptSuccessList = ConfirmGenerateReceipt.generateReceipt(conn, m,user,confirm);
						logger.debug("**** Debug Time OverAll generateReceipt:"+(new Date().getTime() - startTime));
						
					    /** Update Order Transaction **/
						startTime = new Date().getTime();
						m = ControlOrderTransaction.processOrderTransactionCaseReceipt(conn, m,false);
						logger.debug("**** Debug Time processOrderTransactionCaseReceipt:"+(new Date().getTime() - startTime));
						
						if (receiptSuccessList != null) {
							summary.getSuccessReceiptList().addAll(receiptSuccessList);
						}
						
						logger.debug("Conn commit");
                        conn.commit();
					}
	
				} catch (Exception ex) {
					logger.debug("Conn Rollback");
					conn.rollback();
					summary.addFailOrder(order, ex.getMessage());
					// receipt = null;
				}
				
			}//for
	
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(conn !=null){
		       conn.close();conn = null;
			}
		}
		return summary;
	}

	
}
