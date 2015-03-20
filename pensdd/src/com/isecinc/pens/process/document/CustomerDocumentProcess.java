package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.text.DecimalFormat;

import util.DBCPConnectionProvider;

import com.isecinc.pens.init.InitialReferences;

/**
 * CustomerDocumentProcess Class
 * 
 * @author Atiz.b
 * @version $Id: CustomerDocumentProcess.java,v 1.0 13/10/2010 15:52:00 atiz.b Exp $
 * 
 *          atiz.b : edit for no new running
 * 
 */
public class CustomerDocumentProcess extends DocumentSequenceProcess {

	/**
	 * Get Next Doc No
	 * 
	 * @param territory
	 * @param province
	 * @param district
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String getNextDocumentNo(String territory, String province, String district, int activeUserID,
			Connection conn) throws Exception {
		int seq = getNextSeq(territory, province, district, activeUserID, conn);
		String docNo = territory + province + district;
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
			//System.out.println(new CustomerDocumentProcess().getNextDocumentNo("VAN001", "xxx", 1, conn));
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
