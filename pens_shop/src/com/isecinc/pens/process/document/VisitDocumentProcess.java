package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.text.DecimalFormat;

import com.isecinc.pens.init.InitialReferences;

import util.DBCPConnectionProvider;

/**
 * ReceiptDocumentProcess Class
 * 
 * @author Atiz.b
 * @version $Id: ReceiptDocumentProcess.java,v 1.0 13/10/2010 15:52:00 atiz.b Exp $
 * 
 */
public class VisitDocumentProcess extends DocumentSequenceProcess {

	/**
	 * Get Next DocNo
	 */
	public String getNextDocumentNo(String salesCode, String prefix, int activeUserID, Connection conn)
			throws Exception {
		int seq = getNextSeq(salesCode, VISIT_NUMNER, activeUserID, conn);
		String docNo = salesCode;
		if (docSequence.getOrderType().equalsIgnoreCase("DD"))
			docNo += String.format("%s%s", docSequence.getCurrentYear().substring(2), new DecimalFormat("00")
					.format(Integer.parseInt(docSequence.getCurrentMonth())));

		if (docSequence.getOrderType().equalsIgnoreCase("MM"))
			docNo += String.format("%s%s", docSequence.getCurrentYear().substring(2), new DecimalFormat("00")
					.format(Integer.parseInt(docSequence.getCurrentMonth())));

		if (docSequence.getOrderType().equalsIgnoreCase("YY"))
			docNo += String.format("%s", docSequence.getCurrentYear().substring(2));

		docNo += String.format("%s", new DecimalFormat("0000").format(seq));

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
			System.out.println(new VisitDocumentProcess().getNextDocumentNo("VAN001", "xxx", 1, conn));
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
