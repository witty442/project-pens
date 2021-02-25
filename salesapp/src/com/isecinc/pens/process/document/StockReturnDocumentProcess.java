package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.pens.util.Utils;

/**
 * MoveOrderReqDocumentProcess Class
 * 
 * @author Witty
 * @version 1.0
 * 
 * 
 */
public class StockReturnDocumentProcess extends DocumentSequenceProcess {


	
	/**
	 * Get Next DocNo
	 * Salecode +  YY + MM + Running 2 หลัก  เช่น S001 5810 01  เป็นต้น
	 */
	public String getNextDocumentNo(Date requestDate,String salesCode, int activeUserID, Connection conn)
			throws Exception {
		
		int seq = getNextSeqStockReturn(requestDate,salesCode, STOCK_RETURN_NUMBER, activeUserID); //connection seprarate
		
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
			String sql = "SELECT COUNT(*) as TOT FROM t_stock_return ";
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

protected int getNextSeqStockReturn(Date requestDate,String salesCode, int docTypeId, int activeUserID) throws Exception {
	boolean newSeq = false;
	String curYear = "";
	int curMonth = 0;
	int nextValue = 0;
	Connection conn = null;
	try {
		conn = new DBCPConnectionProvider().getConnection(conn);
		
		String keyNextSeq = "";//salesCode+"-"+pdCode;//V001-P001
		
		
		String today = df.format(requestDate);
		String[] d1 = today.split("/");
		curYear = d1[0];
		curMonth = Integer.parseInt(d1[1]);
		
		User user = new MUser().find(String.valueOf(activeUserID));
		
		// get order type
		StringBuffer whereCause = new StringBuffer("");
		whereCause.append("\n  AND doctype_id = " + docTypeId);
		whereCause.append("\n  AND current_month = " + curMonth);
		whereCause.append("\n  AND current_year = " + curYear);
		whereCause.append("\n  AND sales_code = '" + keyNextSeq + "' ");
	
		DocSequence[] seq = new MDocSequence().search(whereCause.toString());
	
		if (seq == null){
			
			//get OrderType
			whereCause = new StringBuffer("");
			whereCause.append("\n  AND doctype_id = " + docTypeId);
		    whereCause.append("\n  AND sales_code = '" + keyNextSeq + "' ");

			DocSequence[] docSeqFindOrderType = new MDocSequence().search(whereCause.toString());
			String orderType = "MM";//Default MM(month) ->DD or MM or YY
			if(docSeqFindOrderType != null){
				docSeqFindOrderType[0].getOrderType();
			}

			DocSequence docSeq = new DocSequence();
			docSeq.setId(getNexSeqAndChkDuplicate(conn, "c_doctype_sequence", "doctype_sequence_id", 0));
			docSeq.setOrderType(orderType); //DEFALUT
			docSeq.setCurrentYear(curYear);
			docSeq.setCurrentMonth(String.valueOf(curMonth));
			docSeq.setStartNo(1);
			docSeq.setCurrentNext(getCurrentNextStockReturn(conn,salesCode,docSeq.getCurrentMonth(),docSeq.getCurrentYear()));
			docSeq.setDoctypeID(docTypeId);
			docSeq.setActive("Y");
			docSeq.setSalesCode(keyNextSeq);
			
			nextValue = docSeq.getCurrentNext();
			
			// insert here
			System.out.println("Insert Case No found in c_doctype_sequence");
			new MDocSequence().saveNew(docSeq, activeUserID, conn);
			
			docSequence = docSeq;
			
			return nextValue;
			
		}else{
		
			String orderType = seq[0].getOrderType();
			logger.debug("orderType["+orderType+"]Year["+d1[0]+":"+seq[0].getCurrentYear()+"]Month["+Integer.parseInt(d1[1])+":"+Integer.parseInt(seq[0].getCurrentMonth())+"]");

			// reset by DD
			if (orderType.equalsIgnoreCase("DD")) {
				if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())
						&& Integer.parseInt(d1[1]) == Integer.parseInt(seq[0].getCurrentMonth())) {
					newSeq = false;
				} else {
					newSeq = true;
				}
			}

			// reset by MM
			if (orderType.equalsIgnoreCase("MM")) {
				if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())
						&& Integer.parseInt(d1[1]) == Integer.parseInt(seq[0].getCurrentMonth())) {
					newSeq = false;
				} else {
					newSeq = true;
				}
			}

			// reset by YY
			if (orderType.equalsIgnoreCase("YY")) {
				if (d1[0].equalsIgnoreCase(seq[0].getCurrentYear())) {
					newSeq = false;
				} else {
					newSeq = true;
				}
			}

			if (newSeq) {
				// start new seq with current month/year
				
				seq[0].setId(getNexSeqAndChkDuplicate(conn, "c_doctype_sequence", "doctype_sequence_id", 0));
				seq[0].setOrderType(orderType);
				seq[0].setCurrentYear(curYear);
				seq[0].setCurrentMonth(String.valueOf(curMonth));
				seq[0].setCurrentNext(getCurrentNextStockReturn(conn,salesCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
				nextValue = seq[0].getCurrentNext();
				
				// insert here
				logger.debug("Exist Insert ");
				new MDocSequence().saveNew(seq[0], activeUserID, conn);
				
			}else{
				//update old sequence
				seq[0].setCurrentNext(getCurrentNextStockReturn(conn,salesCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
				nextValue = seq[0].getCurrentNext();
				
				// update here
				logger.debug("Exist Update:");
				new MDocSequence().update(seq[0], activeUserID, conn);
			}

			docSequence = seq[0];
		}
		return nextValue;
	} catch (Exception e) {
		e.printStackTrace();
		throw e;
	}finally{
		if(conn != null){
			conn.close();conn = null;
		}
	}
}


//Request_number =S00158100001
protected int getCurrentNextStockReturn(Connection conn ,String salesCode,String currentMonth,String currentYear) throws Exception {
	int nextValue = 0;
	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
		currentYear = currentYear.substring(2,4);//2555 -> 55
		currentMonth = currentMonth.length()==1?"0"+currentMonth:currentMonth;
				
		StringBuffer whereCause = new StringBuffer("");
		whereCause.append("\n select max(a.currentSeq) max_current_seq from( "); 
		whereCause.append("\n select request_number ,");
		whereCause.append("\n CONVERT(substring(request_number,9,4),UNSIGNED INTEGER)  as currentSeq ");
		whereCause.append("\n from t_stock_return ");
		whereCause.append("\n where 1=1 ");
		whereCause.append("\n and substring(request_number,5,2) = '"+currentYear+"'");
		whereCause.append("\n and substring(request_number,7,2) = '"+currentMonth+"'");
		whereCause.append("\n )a ");
		
		logger.debug("sql:"+whereCause.toString());
		
		ps = conn.prepareStatement(whereCause.toString());
		rs = ps.executeQuery();
		if(rs.next()){
			logger.debug("max_current_seq["+Utils.isNull(rs.getString("max_current_seq"))+"]");
			if( !Utils.isNull(rs.getString("max_current_seq")).equals("")){
			   nextValue = Integer.parseInt(rs.getString("max_current_seq")) +1;
			}else{
			   nextValue = 1;
			}
		}else{
			nextValue = 1;
		}
		
		logger.debug("result nextValue["+nextValue+"]");
		
		return nextValue;
	} catch (Exception e) {
		e.printStackTrace();
		throw e;
	}finally{
		if(ps != null){
			ps.close();ps = null;
		}
		if(rs != null){
			rs.close();rs = null;
		}
	}
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
			System.out.println(new StockReturnDocumentProcess().getNextDocumentNo(new Date(),"VAN001", 1, conn));
			
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
