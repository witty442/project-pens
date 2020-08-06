package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

import com.isecinc.pens.bean.CustomerSequence;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MCustomerSequence;
import com.pens.util.DBCPConnectionProvider;

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
	public String getNextDocumentNo(String salesCode, int activeUserID,Connection conn) throws Exception {
		int seq = getNextSeqCustomer(salesCode,CUSTOMER_NUMBER, activeUserID);
		String docNo = "3"+salesCode.substring(1,4);
		docNo += String.format("%s", new DecimalFormat("000000").format(seq));
		
		if(checkCustomerCodeDuplicate(docNo,conn)){
			return docNo;//getNextDocumentNo(salesCode,activeUserID,conn);
		}else{
		    return docNo;
		}
	}
	
	public boolean checkCustomerCodeDuplicate(String customerCode,Connection conn) throws Exception {
		
		Statement stmt = null;
		ResultSet rst = null;
		boolean dup = false;
		try {
			int tot = 0;
			stmt = conn.createStatement();
			String sql = "SELECT COUNT(*) as TOT FROM m_customer ";
			sql += " WHERE code='" + customerCode + "' ";
			
			logger.debug(sql);
			rst = stmt.executeQuery(sql);
			if (rst.next()) {
				tot = rst.getInt("TOT");
			}
			if (tot > 0) dup = true;
			
			logger.info("Check Duplicate CustomerCode["+customerCode+"] exist["+dup+"]");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e) {}
		}
		return dup;
	}

	
protected int getNextSeqCustomerCodeBK(String territory, String province, String district, int activeUserID)throws Exception {
	
	int currentNext = 1;
	Connection conn = null;
	try {
		conn = new DBCPConnectionProvider().getConnection(conn);
		// get order type
		String whereCause = "";
		whereCause += "  AND territory = '" + territory + "' ";
		whereCause += "  AND province = '" + province + "' ";
		whereCause += "  AND district = '" + district + "' ";
	
		CustomerSequence[] seq = new MCustomerSequence().search(whereCause);
		boolean bnew = false;
		if (seq == null) bnew = true;
	
		CustomerSequence customerSequence;
		if (bnew) {
			currentNext = 1;
			customerSequence = new CustomerSequence();
			customerSequence.setTerritory(territory);
			customerSequence.setProvince(province);
			customerSequence.setDistrict(district);
			customerSequence.setCurrentNext(currentNext + 1);
	
			new MCustomerSequence().save(customerSequence, activeUserID, conn);
		} else {
			customerSequence = seq[0];
			currentNext = customerSequence.getCurrentNext();
			customerSequence.setCurrentNext(currentNext + 1);
			// update here
			new MCustomerSequence().update(customerSequence, activeUserID, conn);
		}
	
		return currentNext;
	} catch (Exception e) {
		throw e;
	}finally{
		if(conn != null ){
			conn.close();conn = null;
		}
	}
}
	/**
	 * Get Next DocNo
	 */
	@Deprecated
	public String getNextDocumentNo(String salesCode, String prefix, int activeUserID, Connection conn)
			throws Exception {
		int seq = getNextSeq(salesCode, CUSTOMER_NUMBER, activeUserID, conn);
		// String docNo = salesCode;
		String docNo = String.valueOf(Integer.parseInt(prefix));
		// if (docSequence.getOrderType().equalsIgnoreCase("DD"))
		// docNo += String.format("%s%s", docSequence.getCurrentYear().substring(2), new DecimalFormat("00")
		// .format(Integer.parseInt(docSequence.getCurrentMonth())));
		//
		// if (docSequence.getOrderType().equalsIgnoreCase("MM"))
		// docNo += String.format("%s%s", docSequence.getCurrentYear().substring(2), new DecimalFormat("00")
		// .format(Integer.parseInt(docSequence.getCurrentMonth())));
		//
		// if (docSequence.getOrderType().equalsIgnoreCase("YY"))
		// docNo += String.format("%s", docSequence.getCurrentYear().substring(2));

		docNo += String.format("%s", new DecimalFormat("0000000000").format(seq));

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
			System.out.println(new CustomerDocumentProcess().getNextDocumentNo("VAN001", "xxx", 1, conn));
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
