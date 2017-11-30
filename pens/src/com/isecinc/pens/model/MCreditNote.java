package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.inf.helper.Utils;

/**
 * Credit Note Model
 * 
 * @author atiz.b
 * @version $Id: MCreditNote.java,v 1.0 15/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MCreditNote extends I_Model<CreditNote> {
	private static final long serialVersionUID = 3186716042569663705L;

	public static String TABLE_NAME = "t_credit_note";
	public static String COLUMN_ID = "CREDIT_NOTE_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CreditNote find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, CreditNote.class);
	}

	/**
	 * Look up with AR INVOICE
	 * 
	 * @param arInvoiceNo
	 * @return
	 */
	public List<CreditNote> lookUpForReceipt(int userId, String ids,String customerId) {
		List<CreditNote> pos = new ArrayList<CreditNote>();
		try {
			String whereCause = "\n  AND USER_ID = " + userId;
			whereCause += "\n  AND ACTIVE = 'Y' ";
			whereCause += "\n  AND DOC_STATUS = 'SV' ";
			whereCause += "\n  AND CREDIT_NOTE_ID NOT IN(SELECT CREDIT_NOTE_ID FROM t_receipt_cn rcn, t_receipt rc ";
			whereCause += "\n  WHERE rc.receipt_id = rcn.receipt_id AND rc.Doc_Status = 'SV') ";
			// Add This Clause for Credit Note Still Not Apply To Invoice Only <Pasuwat Wang-arrayagul>
			whereCause += "\n AND (AR_Invoice_No Is Null OR TRIM(AR_INVOICE_NO) = '' ) "; 
			
			/** Wit Edit 16/03/2011 :Filter Credit Note by User **/
			if( !Utils.isNull(customerId).equals("")){
			    whereCause += "\n  AND CUSTOMER_ID ="+customerId+" ";
		    }
			if (ids.length() > 0){
			    whereCause += "\n  AND CREDIT_NOTE_ID NOT IN(" + ids + ") ";
			}
			whereCause += "\n ORDER BY CREDIT_NOTE_NO ";
			
			logger.debug("whereCause:"+whereCause);
			
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, CreditNote.class);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return pos;
	}

	/**
	 * Look up with AR INVOICE
	 * 
	 * @param arInvoiceNo
	 * @return
	 */
	public List<CreditNote> lookUp(String arInvoiceNo) {
		List<CreditNote> pos = new ArrayList<CreditNote>();
		try {
			String whereCause = "  AND AR_INVOICE_NO = '" + arInvoiceNo + "' ";
			whereCause += "  AND ACTIVE='Y' ";
			whereCause += "  AND DOC_STATUS ='SV' ";
			whereCause += "ORDER BY CREDIT_NOTE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, CreditNote.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	public List<CreditNote> lookUpByReceiptId(Connection conn,int receiptId) {
		List<CreditNote> pos = new ArrayList<CreditNote>();
		try {
			String whereCause = "";
			whereCause += "\n AND ACTIVE='Y' ";
			whereCause += "\n AND DOC_STATUS ='SV' ";
			whereCause += "\n AND CREDIT_NOTE_ID IN(";
			whereCause += "\n   select credit_note_id from t_receipt_cn where receipt_id = "+receiptId;
			whereCause += "\n )";
			pos = super.search(conn,TABLE_NAME, COLUMN_ID, whereCause, CreditNote.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Look up with AR INVOICE
	 * 
	 * @param arInvoiceNo
	 * @return
	 */
	public List<CreditNote> lookUpForPaid(String ids,String customerId) {
		List<CreditNote> pos = new ArrayList<CreditNote>();
		try {
			String whereCause = "";
			whereCause += "  AND ACTIVE='Y' ";
			whereCause += "  AND DOC_STATUS ='SV' ";
			/** Wit Edit 16/03/2011 :Filter Credit Note by User **/
			if( !Utils.isNull(customerId).equals("")){
			    whereCause += "  AND CUSTOMER_ID ="+customerId+" ";
		    }
			if (ids.length() > 0) 
				whereCause += "  AND CREDIT_NOTE_ID IN(" + ids + ")";
			else 
				whereCause += "  AND 1=2 ";
			whereCause += " ORDER BY CREDIT_NOTE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, CreditNote.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	
	public double getTotalCreditNoteAmt (String invoiceNo){
		double totalCreditNoteAmt = 0;
		List<CreditNote> pos = new ArrayList<CreditNote>();
		String whereClause = "AND ACTIVE = 'Y'  AND DOC_STATUS ='SV' AND AR_Invoice_No = '"+invoiceNo+"'";
		
		try {
			pos = super.search(TABLE_NAME, COLUMN_ID, whereClause, CreditNote.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Sum Total Amount
		for(CreditNote crediteNote : pos){
			totalCreditNoteAmt = totalCreditNoteAmt + crediteNote.getTotalAmount();
		}
		
		return totalCreditNoteAmt;
	}
	
}
