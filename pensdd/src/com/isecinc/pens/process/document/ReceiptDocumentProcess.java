package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MUser;

import util.DBCPConnectionProvider;

/**
 * ReceiptDocumentProcess Class
 * 
 * @author Atiz.b
 * @version $Id: ReceiptDocumentProcess.java,v 1.0 13/10/2010 15:52:00 atiz.b Exp $
 * 
 *          atiz.b : edit for dd sequence
 * 
 */
public class ReceiptDocumentProcess extends DocumentSequenceProcess {

	/**
	 * Get Next DocNo
	 */
	public String getNextDocumentNo(Date shippingDate, int activeUserID,String currentYear , String currentMonth,Connection conn)
			throws Exception {
	
		int seq = getNextSeq(RECEIPT_NUMNER, activeUserID,conn,shippingDate);

		String docNo = "";

		// sale code to tt/van
		if (docSequence.getOrderType().equalsIgnoreCase("DD"))
			docNo += String.format("%s%s", currentYear, currentMonth);

		if (docSequence.getOrderType().equalsIgnoreCase("MM"))
			docNo += String.format("%s%s", currentYear, currentMonth);

		if (docSequence.getOrderType().equalsIgnoreCase("YY"))
			docNo += String.format("%s", currentYear);

		docNo += String.format("%s", new DecimalFormat("0000").format(seq));
		
		logger.debug("***docNo["+docNo+"]");

		return docNo;
	}

	/**
	 * Test
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			InitialReferences init = new InitialReferences();
			init.init(conn);
			//System.out.println(new ReceiptDocumentProcess().getNextDocumentNo("VAN001", "xxx", 1, conn));
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

	}

}
