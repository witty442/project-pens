package com.isecinc.pens.process.document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.CustomerSequence;
import com.isecinc.pens.bean.DocSequence;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MCustomerSequence;
import com.isecinc.pens.model.MDocSequence;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.process.SequenceProcess;

/**
 * DocumentSequenceProcess Class
 * 
 * @author Atiz.b
 * @version $Id: DocumentSequenceProcess.java,v 1.0 13/10/2010 15:52:00 atiz.b Exp $
 * 
 *          atiz.b : edit for dd sequence
 * 
 */
public abstract class DocumentSequenceProcess {

	public static Logger logger = Logger.getLogger("PENS");

	protected static final int CUSTOMER_NUMBER = 100;
	protected static final int MEMBER_NUMBER = 200;
	protected static final int ORDER_NUMNER = 300;
	protected static final int RECEIPT_NUMNER = 400;
	protected static final int VISIT_NUMNER = 500;
	
	protected static final int MOVE_ORDER_REQ_NUMBER = 600;
	protected static final int MOVE_ORDER_RETURN_NUMBER = 700;
	protected static final int STOCK_PD_NUMBER = 800;
	protected static final int REQUISITION_PRODUCT_NUMBER = 900;
	protected static final int REQ_PROMOTION_NUMBER = 111;
	protected static final int STOCK_NUMBER = 222;
	
	protected static final int MAX_SEQ_NO = 9999;
	
	protected SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", new Locale("th", "TH"));

	protected DocSequence docSequence;

	
	/**
	 * Get Next Sequence
	 * 
	 * @param salesCode
	 * @param docType
	 * @param activeUserId
	 * @return
	 * @throws Exception
	 * reset by day or month or year
	 */
	protected int getNextSeq(String salesCode, int docTypeId, int activeUserID, Connection conn) throws Exception {
		boolean newSeq = false;
		String curYear = "";
		int curMonth = 0;
		int nextValue = 0;
		try {
			String today = df.format(new Date());
			String[] d1 = today.split("/");
			curYear = d1[0];
			curMonth = Integer.parseInt(d1[1]);
			
			User user = new MUser().find(String.valueOf(activeUserID));
			
			// get order type
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n  AND doctype_id = " + docTypeId);
			whereCause.append("\n  AND current_month = " + curMonth);
			whereCause.append("\n  AND current_year = " + curYear);
			
			if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
				whereCause.append("\n AND sales_code = '" + salesCode + "' ");
			}
			if (user.getType().equalsIgnoreCase(User.DD)) {
				whereCause.append("\n AND sales_code = '' ");
			}

			System.out.println("sql:"+whereCause.toString());
			
			DocSequence[] seq = new MDocSequence().search(whereCause.toString());
			if (seq == null){
				//throw new Exception(
						//"\u0E01\u0E23\u0E38\u0E13\u0E32\u0E2A\u0E23\u0E49\u0E32\u0E07\u0E40\u0E25\u0E02\u0E17\u0E35\u0E48\u0E40\u0E2D\u0E01\u0E2A\u0E32\u0E23\u0E01\u0E48\u0E2D\u0E19\u0E1A\u0E31\u0E19\u0E17\u0E36\u0E01\u0E23\u0E32\u0E22\u0E01\u0E32\u0E23...");
			
				System.out.println("No set update : insert new Record ");
				
				//get OrderType
				whereCause = new StringBuffer("");
				whereCause.append("\n  AND doctype_id = " + docTypeId);
				if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
					whereCause.append("\n AND sales_code = '" + salesCode + "' ");
				}
				if (user.getType().equalsIgnoreCase(User.DD)) {
					whereCause.append("\n AND sales_code = '' ");
				}
				DocSequence[] docSeqFindOrderType = new MDocSequence().search(whereCause.toString());
				
				String orderType = "MM";//Default MM(month) ->DD or MM or YY
				if(docSeqFindOrderType != null){
					docSeqFindOrderType[0].getOrderType();
				}
				
				nextValue = 1;
				DocSequence docSeq = new DocSequence();
				docSeq.setId(0);
				docSeq.setOrderType(orderType); //DEFALUT
				docSeq.setCurrentYear(curYear);
				docSeq.setCurrentMonth(String.valueOf(curMonth));
				docSeq.setStartNo(1);
				docSeq.setCurrentNext(docSeq.getStartNo()+1);
				docSeq.setDoctypeID(docTypeId);
				docSeq.setActive("Y");
				docSeq.setSalesCode(salesCode);
				
				// insert here
				System.out.println("Insert ");
				new MDocSequence().saveNew(docSeq, activeUserID, conn);
				
				docSequence = docSeq;
				
				return nextValue;
				
			}else{
			
				String orderType = seq[0].getOrderType();
	
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
					nextValue = seq[0].getStartNo();
					seq[0].setId(0);
					seq[0].setOrderType(orderType);
					seq[0].setCurrentYear(curYear);
					seq[0].setCurrentMonth(String.valueOf(curMonth));
					seq[0].setCurrentNext(seq[0].getStartNo()+1);//start 1
					
					// insert here
					System.out.println("Insert ");
					new MDocSequence().saveNew(seq[0], activeUserID, conn);
					
				}else{
					//update old sequence
					nextValue = seq[0].getCurrentNext();
					seq[0].setCurrentNext(seq[0].getCurrentNext() + 1);
					// update here
					System.out.println("Update ");
					new MDocSequence().update(seq[0], activeUserID, conn);
				}
	
				docSequence = seq[0];
			}
			return nextValue;
		} catch (Exception e) {
			throw e;
		}
	}

	
	/**
	 * 
	 * @param salesCode
	 * @param docTypeId
	 * @param activeUserID
	 * @return
	 * @throws Exception
	 * desc : get Next Seq and update next Seq 
	 * MoveOrder Req
	 * MoveOrder Return
	 */
	protected int getNextSeqMoveOrder(Date requestDate,String moveOrderType,String salesCode,String pdCode, int docTypeId, int activeUserID) throws Exception {
		boolean newSeq = false;
		String curYear = "";
		int curMonth = 0;
		int nextValue = 0;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			String keyNextSeq = salesCode+"-"+pdCode;//V001-P001
			if(docTypeId==MOVE_ORDER_RETURN_NUMBER){
			   keyNextSeq = pdCode+"-"+salesCode ;//P001-V001
			}
			
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
			
			if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
				whereCause.append("\n AND sales_code = '" + keyNextSeq + "' ");
			}
		
			DocSequence[] seq = new MDocSequence().search(whereCause.toString());
		
			if (seq == null){
				
				//get OrderType
				whereCause = new StringBuffer("");
				whereCause.append("\n  AND doctype_id = " + docTypeId);
				if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
					whereCause.append("\n AND sales_code = '" + keyNextSeq + "' ");
				}
				
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
				docSeq.setCurrentNext(getCurrentNextMoveOrder(conn,moveOrderType, salesCode, pdCode,docSeq.getCurrentMonth(),docSeq.getCurrentYear()));
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
					seq[0].setCurrentNext(getCurrentNextMoveOrder(conn,moveOrderType, salesCode, pdCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
					nextValue = seq[0].getCurrentNext();
					
					// insert here
					logger.debug("Exist Insert ");
					new MDocSequence().saveNew(seq[0], activeUserID, conn);
					
				}else{
					//update old sequence
					seq[0].setCurrentNext(getCurrentNextMoveOrder(conn,moveOrderType, salesCode, pdCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
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
	
	protected int getNextSeqStock(Date requestDate,String salesCode, int docTypeId, int activeUserID) throws Exception {
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
			whereCause.append("\n AND sales_code = '" + keyNextSeq + "' ");
		
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
				docSeq.setCurrentNext(getCurrentNextStock(conn,salesCode,docSeq.getCurrentMonth(),docSeq.getCurrentYear()));
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
					seq[0].setCurrentNext(getCurrentNextStock(conn,salesCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
					nextValue = seq[0].getCurrentNext();
					
					// insert here
					logger.debug("Exist Insert ");
					new MDocSequence().saveNew(seq[0], activeUserID, conn);
					
				}else{
					//update old sequence
					seq[0].setCurrentNext(getCurrentNextStock(conn,salesCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
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
	
	protected int getNextSeqReqPromotion(Date requestDate,String salesCode, int docTypeId, int activeUserID) throws Exception {
		boolean newSeq = false;
		String curYear = "";
		int curMonth = 0;
		int nextValue = 0;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			String keyNextSeq = salesCode;
			
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
			
			if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
				whereCause.append("\n AND sales_code = '" + keyNextSeq + "' ");
			}
		
			DocSequence[] seq = new MDocSequence().search(whereCause.toString());
		
			if (seq == null){
				
				//get OrderType
				whereCause = new StringBuffer("");
				whereCause.append("\n  AND doctype_id = " + docTypeId);
				if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
					whereCause.append("\n AND sales_code = '" + keyNextSeq + "' ");
				}
				
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
				docSeq.setCurrentNext(getCurrentNextReqPromotion(conn,salesCode, docSeq.getCurrentMonth(),docSeq.getCurrentYear()));
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
					seq[0].setCurrentNext(getCurrentNextReqPromotion(conn,salesCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
					nextValue = seq[0].getCurrentNext();
					
					// insert here
					logger.debug("Exist Insert ");
					new MDocSequence().saveNew(seq[0], activeUserID, conn);
					
				}else{
					//update old sequence
					seq[0].setCurrentNext(getCurrentNextReqPromotion(conn, salesCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
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
	
	protected int getNextSeqRequisitionProduct(Date requestDate,String salesCode, int docTypeId, int activeUserID) throws Exception {
		boolean newSeq = false;
		String curYear = "";
		int curMonth = 0;
		int nextValue = 0;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
	
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
			whereCause.append("\n  AND sales_code = '" + salesCode + "' ");
			
		
			DocSequence[] seq = new MDocSequence().search(whereCause.toString());
		
			if (seq == null){
				
				//get OrderType
				whereCause = new StringBuffer("");
				whereCause.append("\n AND doctype_id = " + docTypeId);
				whereCause.append("\n AND sales_code = '" + salesCode + "' ");

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
				docSeq.setCurrentNext(getCurrentNextRequisitionProduct(conn,salesCode,docSeq.getCurrentMonth(),docSeq.getCurrentYear()));
				docSeq.setDoctypeID(docTypeId);
				docSeq.setActive("Y");
				docSeq.setSalesCode(salesCode);
				
				nextValue = docSeq.getCurrentNext();
				
				logger.debug("id:"+docSeq.getId());
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
					seq[0].setCurrentNext(getCurrentNextRequisitionProduct(conn,salesCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
					nextValue = seq[0].getCurrentNext();
					
					// insert here
					logger.debug("Exist Insert ");
					new MDocSequence().saveNew(seq[0], activeUserID, conn);
					
				}else{
					//update old sequence
					seq[0].setCurrentNext(getCurrentNextRequisitionProduct(conn, salesCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
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
	
	protected int getNextSeqStockPD(Date requestDate,String salesCode,String pdCode, int docTypeId, int activeUserID) throws Exception {
		boolean newSeq = false;
		String curYear = "";
		int curMonth = 0;
		int nextValue = 0;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			String keyNextSeq = salesCode+"-"+pdCode;//V001-P001
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
			
			if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
				whereCause.append("\n AND sales_code = '" + keyNextSeq + "' ");
			}
		
			DocSequence[] seq = new MDocSequence().search(whereCause.toString());
		
			if (seq == null){
				
				//get OrderType
				whereCause = new StringBuffer("");
				whereCause.append("\n  AND doctype_id = " + docTypeId);
				if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
					whereCause.append("\n AND sales_code = '" + keyNextSeq + "' ");
				}
				
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
				docSeq.setCurrentNext(getCurrentNextStockPD(conn,salesCode, pdCode,docSeq.getCurrentMonth(),docSeq.getCurrentYear()));
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
					seq[0].setCurrentNext(getCurrentNextStockPD(conn, salesCode, pdCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
					nextValue = seq[0].getCurrentNext();
					
					// insert here
					logger.debug("Exist Insert ");
					new MDocSequence().saveNew(seq[0], activeUserID, conn);
					
				}else{
					//update old sequence
					seq[0].setCurrentNext(getCurrentNextStockPD(conn,salesCode, pdCode,seq[0].getCurrentMonth(),seq[0].getCurrentYear()));// getCurrentNextSeq
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
	
	/**
	 * 
	 * @param salesCode
	 * @param docTypeId
	 * @param activeUserID
	 * @return
	 * @throws Exception
	 * desc : get Next Seq and update next Seq 
	 * MoveOrder Req
	 * MoveOrder Return
	 */
	protected int getCurrentNextMoveOrder(Connection conn ,String moveOrderType,String salesCode,String pdCode,String currentMonth,String currentYear) throws Exception {
		int nextValue = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			currentYear = currentYear.substring(2,4);//2555 -> 55
			currentMonth = currentMonth.length()==1?"0"+currentMonth:currentMonth;
					
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n select max(a.currentSeq) max_current_seq from( "); 
			whereCause.append("\n select request_number ,substring(request_number,15,3)  as currentSeq from t_move_order ");
			whereCause.append("\n where move_order_type  = '" + moveOrderType + "' ");
			whereCause.append("\n and sales_code = '" + salesCode + "' ");
			whereCause.append("\n and pd_code = '" + pdCode + "' ");
			whereCause.append("\n and substring(request_number,11,2) = '"+currentYear+"'");
			whereCause.append("\n and substring(request_number,13,2) = '"+currentMonth+"'");
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
	
	//Request_number =S001581001
	protected int getCurrentNextStock(Connection conn ,String salesCode,String currentMonth,String currentYear) throws Exception {
		int nextValue = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			currentYear = currentYear.substring(2,4);//2555 -> 55
			currentMonth = currentMonth.length()==1?"0"+currentMonth:currentMonth;
					
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n select max(a.currentSeq) max_current_seq from( "); 
			whereCause.append("\n select request_number ,substring(request_number,9,3)  as currentSeq from t_stock ");
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
	
	
	// Format พนักงานขาย + YYMM + Running 2 หลัก  เช่น S204570701 เป็นต้น
	protected int getCurrentNextReqPromotion(Connection conn ,String salesCode,String currentMonth,String currentYear) throws Exception {
		int nextValue = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			currentYear = currentYear.substring(2,4);//2555 -> 55
			currentMonth = currentMonth.length()==1?"0"+currentMonth:currentMonth;
					
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n select max(a.currentSeq) max_current_seq from( "); 
			whereCause.append("\n select request_no ,substring(request_no,9,2)  as currentSeq from t_req_promotion ");
			whereCause.append("\n where 1=1");
			whereCause.append("\n and substring(request_no,5,2) = '"+currentYear+"'");
			whereCause.append("\n and substring(request_no,7,2) = '"+currentMonth+"'");
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
	protected int getCurrentNextRequisitionProduct(Connection conn ,String salesCode,String currentMonth,String currentYear) throws Exception {
		int nextValue = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			currentYear = currentYear.substring(2,4);//2555 -> 55
			currentMonth = currentMonth.length()==1?"0"+currentMonth:currentMonth;
					
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n select max(a.currentSeq) max_current_seq from( "); 
			whereCause.append("\n select request_number ,substring(request_number,10,3)  as currentSeq from t_requisition_product ");
			whereCause.append("\n where 1=1 ");
			whereCause.append("\n and sales_code = '" + salesCode + "' ");
			whereCause.append("\n and substring(request_number,6,2) = '"+currentYear+"'");
			whereCause.append("\n and substring(request_number,8,2) = '"+currentMonth+"'");
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
	
	
	protected int getCurrentNextStockPD(Connection conn ,String salesCode,String pdCode,String currentMonth,String currentYear) throws Exception {
		int nextValue = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			currentYear = currentYear.substring(2,4);//2555 -> 55
			currentMonth = currentMonth.length()==1?"0"+currentMonth:currentMonth;
					
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n select max(a.currentSeq) max_current_seq from( "); 
			whereCause.append("\n select request_number ,substring(request_number,15,3)  as currentSeq from t_stock_pd ");
			whereCause.append("\n where 1=1 ");
			whereCause.append("\n and sales_code = '" + salesCode + "' ");
			whereCause.append("\n and pd_code = '" + pdCode + "' ");
			whereCause.append("\n and substring(request_number,11,2) = '"+currentYear+"'");
			whereCause.append("\n and substring(request_number,13,2) = '"+currentMonth+"'");
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
	 * getNextSeqCustomer
	 * @param moveOrderType
	 * @param salesCode
	 * @param pdCode
	 * @param docTypeId
	 * @param activeUserID
	 * @return
	 * @throws Exception
	 */
	protected int getNextSeqCustomer(String salesCode, int docTypeId,int activeUserID) throws Exception {
		boolean newSeq = false;
		String curYear = "0";
		String curMonth = "0";
		int nextValue = 0;
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			// get order type
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n  AND doctype_id = " + docTypeId);
			whereCause.append("\n  AND sales_code = '" + salesCode + "' ");
			
			DocSequence[] seq = new MDocSequence().search(whereCause.toString());
		
			if (seq == null){
				
				//get OrderType
				whereCause = new StringBuffer("");
				whereCause.append("\n AND doctype_id = " + docTypeId);
				whereCause.append("\n AND sales_code = '" + salesCode + "' ");
				
				DocSequence[] docSeqFindOrderType = new MDocSequence().search(whereCause.toString());
				String orderType = "";//Default BLANK
				if(docSeqFindOrderType != null){
				   docSeqFindOrderType[0].getOrderType();
				}
				
				DocSequence docSeq = new DocSequence();
				docSeq.setId(getNexSeqAndChkDuplicate(conn, "c_doctype_sequence", "doctype_sequence_id", 0));
				docSeq.setOrderType(orderType); //DEFALUT
				docSeq.setCurrentYear(curYear);
				docSeq.setCurrentMonth(curMonth);
				docSeq.setStartNo(1);
				docSeq.setCurrentNext(getCurrentNextCustomer(conn,salesCode, activeUserID));
				docSeq.setDoctypeID(docTypeId);
				docSeq.setActive("Y");
				docSeq.setSalesCode(salesCode);
				
				nextValue = docSeq.getCurrentNext();
				
				// insert here
				System.out.println("Insert Case No found in c_doctype_sequence");
				new MDocSequence().saveNew(docSeq, activeUserID, conn);
				
				docSequence = docSeq;
				
				return nextValue;
				
			}else{
			
				//update old sequence
				seq[0].setCurrentNext(getCurrentNextCustomer(conn,salesCode, activeUserID));// getCurrentNextSeq
				nextValue = seq[0].getCurrentNext();
				
				// update here
				logger.debug("Exist Update:");
				new MDocSequence().update(seq[0], activeUserID, conn);
				
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
	
	/**
	 * getCurrentNextCustomer
	 * @param conn
	 * @param salesCode
	 * @param activeUserID
	 * @return
	 * @throws Exception
	 */
	protected int getCurrentNextCustomer(Connection conn ,String salesCode,int activeUserID) throws Exception {
		int nextValue = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			salesCode = "3"+salesCode.substring(1,4);//V001 ->3001
	
			StringBuffer whereCause = new StringBuffer("");
			whereCause.append("\n select max(a.currentSeq) max_current_seq from( "); 
			whereCause.append("\n select code ,substring(code,5,6)  as currentSeq from m_customer ");
			whereCause.append("\n where  user_id = " + activeUserID + " and length(code) = 10");
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
 * 	
 * @param conn
 * @param tableName
 * @param columnSeqName
 * @param currSeq
 * @return
 * @throws Exception
 * if dup recurrsive method until no dup 
 */
public int getNexSeqAndChkDuplicate(Connection conn,String tableName,String columnSeqName ,int currSeq) throws Exception {
		
		Statement stmt = null;
		ResultSet rst = null;
		try {
			currSeq = SequenceProcess.getNextValue(tableName);//Add Next Seq
			stmt = conn.createStatement();
			String sql = "SELECT "+columnSeqName+" FROM "+tableName;
			sql += " WHERE "+columnSeqName+"=" + currSeq + " ";
			
			logger.debug(sql);
			rst = stmt.executeQuery(sql);
			if (rst.next()) {
				return getNexSeqAndChkDuplicate(conn,tableName,columnSeqName,currSeq);
			}else{
				return currSeq;
			}
			
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
	}
	
}
