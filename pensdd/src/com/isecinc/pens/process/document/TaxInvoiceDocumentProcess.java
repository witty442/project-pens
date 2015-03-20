package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MUser;

public class TaxInvoiceDocumentProcess extends DocumentSequenceProcess {

	/**
	 * Get Next DocNo
	 */
	
	public String getNextDocumentNo(Date shipingDate, int activeUserID , int docTypeId , Connection conn,String currentYear , String currentMonth)
			throws Exception {
	
		int seq = getNextSeq(docTypeId, activeUserID, conn,shipingDate);

		String docNo = "";
		if (docSequence.getOrderType().equalsIgnoreCase("DD"))
			docNo += String.format("%s%s", currentYear, currentMonth);

		if (docSequence.getOrderType().equalsIgnoreCase("MM"))
			docNo += String.format("%s%s", currentYear, currentMonth);

		if (docSequence.getOrderType().equalsIgnoreCase("YY"))
			docNo += String.format("%s", currentYear);

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
			//System.out.println(new TaxInvoiceDocumentProcess().getNextDocumentNo("DD01", "xxx", 1,800, conn,"55","06"));
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
