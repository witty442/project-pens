package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;

import com.isecinc.pens.bean.DocSequence;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MDocSequence;
import com.isecinc.pens.model.MMoveOrder;
import com.isecinc.pens.model.MStock;
import com.isecinc.pens.model.MUser;
import com.pens.util.DBCPConnectionProvider;

/**
 * MoveOrderReqDocumentProcess Class
 * 
 * @author Witty
 * @version 1.0
 * 
 * 
 */
public class StockDocumentProcess extends DocumentSequenceProcess {


	
	/**
	 * Get Next DocNo
	 * Salecode +  YY + MM + Running 2 หลัก  เช่น S001 5810 01  เป็นต้น
	 */
	public String getNextDocumentNo(Date requestDate,String salesCode, int activeUserID, Connection conn)
			throws Exception {
		
		int seq = getNextSeqStock(requestDate,salesCode, STOCK_NUMBER, activeUserID); //connection seprarate
		
		// String docNo = "O";
		String docNo = salesCode;
		
		// sale code to tt/van
		System.out.println("docSequence.getOrderType():"+docSequence.getOrderType());
		
		if (docSequence.getOrderType().equalsIgnoreCase("DD"))
			docNo += String.format("%s%s", docSequence.getCurrentYear().substring(2), new DecimalFormat("00")
					.format(Integer.parseInt(docSequence.getCurrentMonth())));

		if (docSequence.getOrderType().equalsIgnoreCase("MM"))
			docNo += String.format("%s%s", docSequence.getCurrentYear().substring(2), new DecimalFormat("00")
					.format(Integer.parseInt(docSequence.getCurrentMonth())));

		if (docSequence.getOrderType().equalsIgnoreCase("YY"))
			docNo += String.format("%s", docSequence.getCurrentYear().substring(2));
		 
		docNo += String.format("%s", new DecimalFormat("0000").format(seq));
		
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
			String sql = "SELECT COUNT(*) as TOT FROM t_stock ";
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
			System.out.println(new StockDocumentProcess().getNextDocumentNo(new Date(),"VAN001", 1, conn));
			
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
