package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MUser;

public class ShipmentDocumentProcess extends DocumentSequenceProcess {

	/**
	 * Get Next DocNo
	 */
	
	public String getNextDocumentNo(Date shippingDate, int activeUserID,String currentYear , String currentMonth,Connection conn)
			throws Exception {
		
		int seq = getNextSeq(SHIPMENT_NUMBER, activeUserID,conn,shippingDate);
		// String docNo = "O";
		String docNo = "";
		User user = new MUser().find(String.valueOf(activeUserID));
	
		if (docSequence.getOrderType().equalsIgnoreCase("DD"))
			docNo += String.format("%s%s", currentYear, currentMonth);;

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
			//System.out.println(new ShipmentDocumentProcess().getNextDocumentNo("DD01", "xxx", 1, conn));
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
