package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.DocSequence;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MDocSequence;
import com.isecinc.pens.model.MMoveOrder;
import com.isecinc.pens.model.MUser;

/**
 * MoveOrderReqDocumentProcess Class
 * 
 * @author Witty
 * @version 1.0
 * 
 * 
 */
public class RequisitionProductDocumentProcess extends DocumentSequenceProcess {


	
	/**
	 * 
	 * Get Next DocNo
	 * V3025508001
	 * Dest-Source-date
	 */
	public String getNextDocumentNo(Date requestDate,String salesCode, int activeUserID, Connection conn)
			throws Exception {
		
		int seq = getNextSeqRequisitionProduct(requestDate,salesCode ,REQUISITION_PRODUCT_NUMBER, activeUserID); //connection seprarate
		// String docNo = "O";
		String docNo = "";
		User user = new MUser().find(String.valueOf(activeUserID));
		// sale code to tt/van
		if (!user.getType().equalsIgnoreCase(User.DD)) docNo += salesCode+"-";

		System.out.println("docSequence.getOrderType():"+docSequence.getOrderType());
		
		if (docSequence.getOrderType().equalsIgnoreCase("DD"))
			docNo += String.format("%s%s", docSequence.getCurrentYear().substring(2), new DecimalFormat("00")
					.format(Integer.parseInt(docSequence.getCurrentMonth())));

		if (docSequence.getOrderType().equalsIgnoreCase("MM"))
			docNo += String.format("%s%s", docSequence.getCurrentYear().substring(2), new DecimalFormat("00")
					.format(Integer.parseInt(docSequence.getCurrentMonth())));

		if (docSequence.getOrderType().equalsIgnoreCase("YY"))
			docNo += String.format("%s", docSequence.getCurrentYear().substring(2));
		 
		docNo += String.format("%s", new DecimalFormat("000").format(seq));
		
		if(checkCodeDuplicate(docNo,conn)){
			return getNextDocumentNo(requestDate,salesCode,activeUserID,conn);
		}else{
		    return docNo;
		}

	}

	
public boolean checkCodeDuplicate(String code,Connection conn) throws Exception {
		
		Statement stmt = null;
		ResultSet rst = null;
		boolean dup = false;
		try {
			int tot = 0;
			stmt = conn.createStatement();
			String sql = "SELECT COUNT(*) as TOT FROM t_requisition_product ";
			sql += " WHERE request_number='" + code + "' ";
			
			logger.debug(sql);
			rst = stmt.executeQuery(sql);
			if (rst.next()) {
				tot = rst.getInt("TOT");
			}
			if (tot > 0) dup = true;
			
			logger.info("Check Duplicate requestNumber["+code+"] exist["+dup+"]");
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
			System.out.println(new RequisitionProductDocumentProcess().getNextDocumentNo(new Date(),"VAN001", 1, conn));
			
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
