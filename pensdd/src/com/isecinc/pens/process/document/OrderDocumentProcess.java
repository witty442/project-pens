package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MUser;

/**
 * OrderDocumentProcess Class
 * 
 * @author Atiz.b
 * @version $Id: OrderDocumentProcess.java,v 1.0 13/10/2010 15:52:00 atiz.b Exp $
 * 
 *          atiz.b : edit for dd sequence
 * 
 */
public class OrderDocumentProcess extends DocumentSequenceProcess {

	/**
	 * Get Next DocNo
	 */
	public String getNextDocumentNo( int activeUserID, Connection conn)
			throws Exception {
		int seq = getNextSeq(ORDER_NUMNER, activeUserID, conn,new Date());
		// String docNo = "O";
		String docNo = "";
		User user = new MUser().find(String.valueOf(activeUserID));
		
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
			//System.out.println(new OrderDocumentProcess().getNextDocumentNo("DD01", "xxx", 1, conn));
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
