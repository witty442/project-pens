package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CreditNote;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.pens.util.DateToolsUtil;

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
	
	public List<CreditNote> lookUpForReceipt(Connection conn,int userId, String ids,int customerId) throws Exception  {
		return lookUpForReceiptModel(conn,userId, ids, customerId);
	}
	
	public List<CreditNote> lookUpForReceipt(int userId, String ids,int customerId) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return lookUpForReceiptModel(conn,userId, ids, customerId);
		}catch(Exception e){
			throw e;
		}finally{
			try{
				conn.close();
			}catch(Exception ee){}
		}
	
	}

	public List<CreditNote> lookUpForReceiptModel(Connection conn,int userId, String ids,int customerId)  {
		List<CreditNote> pos = new ArrayList<CreditNote>();
		String whereCause = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			whereCause += "\n  SELECT  CREDIT_NOTE_ID,CREDIT_NOTE_NO ,DOCUMENT_DATE";
			whereCause += "\n  ,AR_INVOICE_NO , ACTIVE";
			whereCause += "\n  , (  COALESCE(c.TOTAL_AMOUNT,0) ";
			//New code cn = cn +adjust 
			whereCause += "\n     + COALESCE((SELECT sum(adjust_amount) from pensso.t_adjust ad ";
			whereCause += "\n                 where ad.ar_invoice_no = c.credit_note_no),0) ";
			whereCause += "\n    ) as TOTAL_AMOUNT ";
			whereCause += "\n  FROM pensso.T_CREDIT_NOTE c WHERE 1=1";
			whereCause += "\n  AND USER_ID = " + userId;
			whereCause += "\n  AND ACTIVE = 'Y' ";
			whereCause += "\n  AND DOC_STATUS = 'SV' ";
			whereCause += "\n  AND CREDIT_NOTE_ID NOT IN (";
			whereCause += "\n     SELECT CREDIT_NOTE_ID FROM pensso.t_receipt_cn rcn, pensso.t_receipt rc ";
			whereCause += "\n    WHERE rc.receipt_id = rcn.receipt_id AND rc.Doc_Status = 'SV' ) ";
			whereCause += "\n    AND (AR_Invoice_No Is Null OR TRIM(AR_INVOICE_NO) = '' ) "; 
			
			/** Wit Edit 16/03/2011 :Filter Credit Note by User **/
			if( customerId != 0){
			    whereCause += "\n  AND CUSTOMER_ID ="+customerId+" ";
		    }
			if (ids.length() > 0){
			    whereCause += "\n  AND CREDIT_NOTE_ID NOT IN(" + ids + ") ";
			}
			whereCause += "\n ORDER BY CREDIT_NOTE_NO ";
			
			logger.debug("whereCause:"+whereCause);
			
			ps = conn.prepareStatement(whereCause);
		    rs =ps.executeQuery();
		    
		    while(rs.next()){
		    	pos.add(new CreditNote(rs));
		    }

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			try{
				ps.close();
				rs.close();
			}catch(Exception ee){}
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
	
	
	public List<CreditNote> lookUpByReceiptId(Connection conn, long receiptId)  {
		List<CreditNote> pos = new ArrayList<CreditNote>();
		String whereCause = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			whereCause += "\n  SELECT  CREDIT_NOTE_ID,CREDIT_NOTE_NO ,DOCUMENT_DATE";
			whereCause += "\n  ,AR_INVOICE_NO , ACTIVE";
			whereCause += "\n  , (  COALESCE(c.TOTAL_AMOUNT,0) ";
			whereCause += "\n     + COALESCE((SELECT SUM(adjust_amount) from t_adjust ad ";
			whereCause += "\n                 where ad.ar_invoice_no = c.credit_note_no),0) ";
			whereCause += "\n    ) as TOTAL_AMOUNT ";
			whereCause += "\n  FROM T_CREDIT_NOTE c WHERE 1=1";
		
			whereCause += "\n AND ACTIVE='Y' ";
			whereCause += "\n AND DOC_STATUS ='SV' ";
			whereCause += "\n AND CREDIT_NOTE_ID IN(";
			whereCause += "\n   select credit_note_id from t_receipt_cn where receipt_id = "+receiptId;
			whereCause += "\n )";
			//logger.debug("whereCause:"+whereCause);
			
			ps = conn.prepareStatement(whereCause);
		    rs =ps.executeQuery();
		    
		    while(rs.next()){
		    	pos.add(new CreditNote(rs));
		    }

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			try{
				ps.close();
				rs.close();
			}catch(Exception ee){}
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
			logger.debug("whereCaluse:"+whereCause);
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, CreditNote.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	
	public double getTotalCreditNoteAmt(String arInvoiceNo) throws Exception{
		Connection conn = null;
		try {
			 conn = DBConnection.getInstance().getConnection();
			 return getTotalCreditNoteAmtModel(conn,arInvoiceNo);
		}catch(Exception e){
			throw e;
		}finally{
			try{
				conn.close();
			}catch(Exception ee){}
		}
		
	}
	
	public double getTotalCreditNoteAmt(Connection conn,String arInvoiceNo){
		return getTotalCreditNoteAmtModel(conn, arInvoiceNo);
	}
	
	//CN = cn+ajust_cn(t_credit_note.credit_note_no = t_adjust. ar_invoice_no)
	public double getTotalCreditNoteAmtModel(Connection conn,String arInvoiceNo){
		double totalCreditNoteAmt = 0;
		String whereCause = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {	
			whereCause += "\n select ar_invoice_no ";
			whereCause += "\n ,COALESCE(sum(cn_amount) +sum(adjust_amount),0) as TOTAL_AMOUNT ";
			whereCause += "\n from(";
			whereCause += "\n  SELECT  ar_invoice_no,credit_note_no ";
			whereCause += "\n  , COALESCE(sum(total_amount),0) as cn_amount ";
			whereCause += "\n  ,(COALESCE((SELECT SUM(adjust_amount) from PENSSO.t_adjust aj ";
			whereCause += "\n                 where aj.ar_invoice_no = c.credit_note_no),0) ";
			whereCause += "\n    ) as ADJUST_AMOUNT ";
			whereCause += "\n  FROM PENSSO.T_CREDIT_NOTE c WHERE 1=1";
			whereCause += "\n  AND ACTIVE='Y' ";
			whereCause += "\n  AND DOC_STATUS ='SV' ";
			whereCause += "\n  AND AR_INVOICE_NO ='"+arInvoiceNo+"'";
			whereCause += "\n  group by ar_invoice_no,credit_note_no ";
			whereCause += "\n )S ";
			whereCause += "\n group by ar_invoice_no ";
			
			//logger.debug("whereCause:"+whereCause);
			ps = conn.prepareStatement(whereCause);
		    rs =ps.executeQuery();
		    
		    if(rs.next()){
		    	totalCreditNoteAmt = rs.getDouble("TOTAL_AMOUNT");
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally{
			try{
				ps.close();
				rs.close();
			}catch(Exception ee){}
		}
		return totalCreditNoteAmt;
	}
	
}
